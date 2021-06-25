package com.hxht.autodeploy.sync.tongdahai

import cn.hutool.core.date.DateUtil
import com.hxht.techcrt.CourtroomStatus
import com.hxht.techcrt.DataStatus
import com.hxht.techcrt.Manufacturer
import com.hxht.techcrt.PlanStatus
import com.hxht.techcrt.PositionStatus
import com.hxht.techcrt.court.CaseInfo
import com.hxht.techcrt.court.CaseType
import com.hxht.techcrt.court.Courtroom
import com.hxht.techcrt.court.Department
import com.hxht.techcrt.court.Employee
import com.hxht.techcrt.court.PlanInfo
import com.hxht.techcrt.court.admin.ToolBoxService
import com.hxht.techcrt.court.manager.SystemController
import com.hxht.techcrt.sync.tongdahai.model.caset.CaseData
import com.hxht.techcrt.sync.tongdahai.model.caset.CaseModel
import com.hxht.techcrt.sync.tongdahai.model.caset.CaseRequestModel
import com.hxht.techcrt.sync.tongdahai.model.caset.RespCase
import com.hxht.techcrt.sync.tongdahai.model.courtroom.CourtRoomData
import com.hxht.techcrt.sync.tongdahai.model.courtroom.CourtroomModel
import com.hxht.techcrt.sync.tongdahai.model.courtroom.CourtroomRequestModel
import com.hxht.techcrt.sync.tongdahai.model.courtroom.RespCourtRoom
import com.hxht.techcrt.sync.tongdahai.model.dept.DeptData
import com.hxht.techcrt.sync.tongdahai.model.dept.DeptModel
import com.hxht.techcrt.sync.tongdahai.model.dept.DeptRequestModel
import com.hxht.techcrt.sync.tongdahai.model.dept.RespDept
import com.hxht.techcrt.sync.tongdahai.model.plan.PlanData
import com.hxht.techcrt.sync.tongdahai.model.plan.PlanModel
import com.hxht.techcrt.sync.tongdahai.model.plan.PlanRequestModel
import com.hxht.techcrt.sync.tongdahai.model.plan.RespPlan
import com.hxht.techcrt.sync.tongdahai.model.user.RespUser
import com.hxht.techcrt.sync.tongdahai.model.user.UserData
import com.hxht.techcrt.sync.tongdahai.model.user.UserModel
import com.hxht.techcrt.sync.tongdahai.model.user.UserRequestModel
import com.hxht.techcrt.sync.util.XMLUtils
import com.hxht.techcrt.utils.Base64Utils
import com.hxht.techcrt.utils.http.HttpUtil
import com.hxht.techcrt.utils.UUIDGenerator
import grails.gorm.transactions.Transactional
import grails.web.context.ServletContextHolder
import org.apache.commons.lang3.StringUtils
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean
import org.dom4j.Document
import org.dom4j.Element
import org.dom4j.io.SAXReader
import org.grails.web.json.JSONObject

import java.text.SimpleDateFormat

/**
 * 对接通达海接口
 * 2021.03.19 >>> tongdahai字符串修改为使用枚举,排期法官和书记员默认使用承办人,否则使用含有部门的第一个人员 daniel
 * 2021.03.22 >>> 以开庭时间为默认立案日期供华夏推送数据使用 daniel
 * 2021.05.24 >>> 同步排期方法添加案件明细 daniel
 * 2021.05.28 >>> 添加恢复5.24之前的案件当事人接口 daniel
 */
@Transactional
class TongdahaiService {
    //1.写死法院代码
    String FYDM
    String TOKEN
    SzftWebService szftWebService
    ToolBoxService toolBoxService

    /**
     * 初始化代理
     */
    void initService() {
        JaxWsProxyFactoryBean clientFactoryBean = new JaxWsProxyFactoryBean()
        clientFactoryBean.setServiceClass(SzftWebService.class)
        //2.解析yunnan.xml
        clientFactoryBean.setAddress(jxXml())
        szftWebService = (SzftWebService) clientFactoryBean.create()
    }

    /**
     * 解析yunnan.xml
     * @return wsdl,token
     */
    String jxXml() {
        FYDM = SystemController.currentCourt.ext3
        Document doc
        InputStream inputStream = ServletContextHolder.getServletContext().getResourceAsStream("/court_list/yunnan.xml")
        SAXReader sr = new SAXReader()
        InputStreamReader reader = new InputStreamReader(inputStream)
        BufferedReader bufferedReader = new BufferedReader(reader)
        doc = sr.read(bufferedReader)
        Element root = doc.getRootElement()
        Element courtInfoE = (Element) root.selectSingleNode("//court[@courtid='" + FYDM + "']")
        //3.获取wsdl和token
        TOKEN = courtInfoE.attributeValue("token")
        String ip = courtInfoE.attributeValue("ip")
        //暂时先解决盘锦市大洼区法院需要access_token请求webservice接口
        if (FYDM == "6B3") {
            String tokenUrl = courtInfoE.attributeValue("access")
            Map<String, String> data = new HashMap<>()
            data.put("clientId", "szft22")
            data.put("clientSecret", "135246")
            JSONObject respData = HttpUtil.postToJson(tokenUrl, data)
            log.info("请求token返回值:${respData}")
            String code = respData.get("code") as String
            if (code != "0") {
                log.info("请求通达海access_token失败,返回信息为:${respData.get("msg")}")
                throw new RuntimeException("请求通达海access_token失败")
            }
            ip += "?access_token=" + respData.get("data")
        }
        return ip
    }

    /**
     * 获取法庭数据
     */
    RespCourtRoom getRespCourtroomByPage(String pageNum) {
        //4.拼装参数
        CourtroomRequestModel requestModel = new CourtroomRequestModel()
        requestModel.setPAGENUM(pageNum)

        //5.实体类转xml
        String modelXML = XMLUtils.convertToXml(requestModel)
        log.info("请求 法庭信息-请求参数:${modelXML}")

        //6.xml标签内编码
        String modelXMLEncode = XMLUtils.convertToXmlEncode(requestModel)
        if (modelXMLEncode == null) {
            throw new RuntimeException("请求JavaBean转XML失败")
        }
        log.info("请求 法庭信息-标签内容编码后:${modelXMLEncode}")

        //7.xml整体编码
        String request = Base64Utils.encode(modelXMLEncode)
        if (StringUtils.isBlank(request)) {
            throw new RuntimeException("请求XML整体编码失败")
        }
        log.info("请求 法庭信息-整体编码后:${request}")

        //8.发送请求获得响应字符串
        String response = szftWebService.getFt(FYDM, TOKEN, request)
        if (StringUtils.isBlank(response)) {
            throw new RuntimeException("响应数据为空")
        }
        log.info("响应 法庭信息-响应信息:${response}")

        //9.响应值整体解码
        String responseDecode = Base64Utils.decode(response)
        if (StringUtils.isBlank(responseDecode)) {
            throw new RuntimeException("响应数据整体解码失败")
        }
        log.info("响应 法庭信息-整体解码后:${responseDecode}")

        //10.响应数据转JavaBean
        RespCourtRoom respCourtRoom = XMLUtils.convertXmlStrToObjectDecode(RespCourtRoom.class, responseDecode) as RespCourtRoom

        //11.解密
        respCourtRoom = XMLUtils.convertToXmlDecode(respCourtRoom, CourtRoomData.class) as RespCourtRoom
        log.info("解密后实体类:\n${respCourtRoom.toString()}")

        //12、返回RespCourtroom
        return respCourtRoom
    }

    /**
     * 同步法庭
     */
    int synCourtRoom() {
        RespCourtRoom respCourtRoom = getRespCourtroomByPage("1")
        int totalCourtroomPage = Integer.parseInt(respCourtRoom.getData().getTotalPageNum())
        for (int i = 1; i <= totalCourtroomPage; i++) {
            if (i != 1) {
                respCourtRoom = getRespCourtroomByPage("${i}")
            }
            //处理respCourtRoom（包含法庭数Count、当前页码CurPageNum、总页码TotalPageNum、法庭列表list）
            List<CourtroomModel> list = respCourtRoom.getData().getList()
            for (CourtroomModel courtroomModel : list) {
                String ftbh = courtroomModel.getFTBH()
                String ftmc = courtroomModel.getFTMC()
                Courtroom courtroom = Courtroom.findByName(ftmc)
                if (!courtroom) {
                    courtroom = new Courtroom()
                    courtroom.uid = UUIDGenerator.nextUUID()
                    courtroom.status = CourtroomStatus.NORMAL
                    courtroom.active = DataStatus.SHOW
                }
                courtroom.name = ftmc
                //使用罗湖法院syncId校验
                courtroom.sycLuoHuId = ftbh
                courtroom.save(flush: true)
                if (courtroom.hasErrors()) {
                    String msg = "保存法庭出错"
                    log.error("${msg},错误信息:${courtroom.errors}")
                    throw new RuntimeException(msg)
                }
            }
        }
        return 0
    }

    /**
     * 获取部门数据
     */
    RespDept getRespDeptByPage(String pageNum) {
        //4.拼装参数
        DeptRequestModel requestModel = new DeptRequestModel()
        requestModel.setPAGENUM(String.valueOf(pageNum))

        //5.实体类转xml
        String modelXML = XMLUtils.convertToXml(requestModel)
        log.info("请求 部门信息-请求参数:${modelXML}")

        //6.xml标签内编码
        String modelXMLEncode = XMLUtils.convertToXmlEncode(requestModel)
        if (modelXMLEncode == null) {
            throw new RuntimeException("请求JavaBean转XML失败")
        }
        log.info("请求 部门信息-标签内容编码后:${modelXMLEncode}")

        //7.xml整体编码
        String request = Base64Utils.encode(modelXMLEncode)
        if (StringUtils.isBlank(request)) {
            throw new RuntimeException("请求XML整体编码失败")
        }
        log.info("请求 部门信息-整体编码后:${request}")

        //8.发送请求获得响应字符串
        String response = szftWebService.getZzjg(FYDM, TOKEN, request)
        if (StringUtils.isBlank(response)) {
            throw new RuntimeException("响应数据为空")
        }
        log.info("响应 法庭信息-响应信息:${response}")

        //9.响应值整体解码
        String responseDecode = Base64Utils.decode(response)
        if (StringUtils.isBlank(responseDecode)) {
            throw new RuntimeException("响应数据整体解码失败")
        }
        log.info("响应 法庭信息-整体解码后:${responseDecode}")

        //10.响应数据转JavaBean
        RespDept respDept = XMLUtils.convertXmlStrToObjectDecode(RespDept.class, responseDecode) as RespDept

        //11.进行解密操作
        respDept = XMLUtils.convertToXmlDecode(respDept, DeptData.class) as RespDept
        log.info("解密后实体类:\n${respDept.toString()}")
        return respDept
    }

    /**
     * 同步部门
     */
    int syncDept() {
        RespDept respDept = getRespDeptByPage("1")
        int totalDeptPage = Integer.parseInt(respDept.getData().getTotalPageNum())
        for (int i = 1; i <= totalDeptPage; i++) {
            if (i != 1) {
                respDept = getRespDeptByPage("${i}")
            }
            //respDept（包含部门数Count、当前页码CurPageNum、总页码TotalPageNum、部门列表list）
            List<DeptModel> list = respDept.getData().getList()
            for (DeptModel deptModel : list) {
                String bmdm = deptModel.getBMDM()
                String mc = deptModel.getMC()
                Department department = Department.findByName(mc)
                if (!department) {
                    department = new Department()
                    department.uid = UUIDGenerator.nextUUID()
                }
                department.name = mc
                department.synchronizationId = bmdm
                department.manufacturer = Manufacturer.TONGDAHAI
                department.save(flush: true)
                if (department.hasErrors()) {
                    String msg = "保存部门出错"
                    log.error("${msg},错误信息:${department.errors}")
                    throw new RuntimeException(msg)
                }
            }
        }
        return 0
    }

    /**
     * 获取用户数据
     */
    RespUser getRespUserByPage(String pageNum) {
        //4.获取请求地址
        String serviceAddress = jxXml()

        //5.拼装请求参数
        UserRequestModel userRequestModel = new UserRequestModel()
        userRequestModel.setIsDownloadSignature(String.valueOf(2))
        userRequestModel.setPageNum(pageNum)

        //6.实体类转xml
        String userModelXML = XMLUtils.convertToXml(userRequestModel)
        log.info("请求 人员信息-请求参数:${userModelXML}")

        //7.xml标签内编码
        String userModelXMLEncode = XMLUtils.convertToXmlEncode(userRequestModel)
        if (userModelXMLEncode == null) {
            throw new RuntimeException("请求JavaBean转XML失败")
        }
        log.info("请求 人员信息-标签内容编码后:${userModelXMLEncode}")

        //8.xml整体编码
        String request = Base64Utils.encode(userModelXMLEncode)
        if (StringUtils.isBlank(request)) {
            throw new RuntimeException("请求XML整体编码失败")
        }
        log.info("请求 人员信息-整体编码后:${request}")

        //9.拼装报文
        StringBuilder soap = new StringBuilder()
        soap.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:szft=\"http://szft.tdh/\">")
        soap.append("<soapenv:Header/>")
        soap.append("<soapenv:Body>")
        soap.append("<szft:GetRy>")
        soap.append("<fydm>" + FYDM + "</fydm>")
        soap.append("<token>" + TOKEN + "</token>")
        soap.append("<xml>" + request + "</xml>")
        soap.append("</szft:GetRy>")
        soap.append("</soapenv:Body>")
        soap.append("</soapenv:Envelope>")
        log.info("请求 人员信息-soap报文:${soap}")

        //10.请求数据写入xml文件
        URL url = new URL(serviceAddress)
        HttpURLConnection conn = (HttpURLConnection) url.openConnection()
        conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8")
        conn.setRequestMethod("POST")
        conn.setUseCaches(false)
        conn.setDoInput(true)
        conn.setDoOutput(true)
        conn.setConnectTimeout(10000)
        conn.setReadTimeout(10000)
        DataOutputStream dos = new DataOutputStream(conn.getOutputStream())
        dos.write(soap.toString().getBytes("utf-8"))
        dos.flush()
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))
        Map<String, String> map = new HashMap<>()
        File dataDir = new File(File.separator + "usr" + File.separator + "local" + File.separator + "movies" + File.separator + "webservice")
        if (!dataDir.exists()) {
            //删除原有文件
            dataDir.mkdirs()
        }
        File dataFile = new File(File.separator + "usr" + File.separator + "local" + File.separator + "movies" + File.separator + "webservice" + File.separator + "GetRy.xml")
        if (dataFile.exists()) {
            dataFile.delete()
        }
        //创建新文件
        dataFile.createNewFile()
        PrintStream ps = new PrintStream(new FileOutputStream(dataFile))
        String line = null
        while ((line = reader.readLine()) != null) {
            //将数据写入文件
            log.info("[TongdahaiService.getRespUserByPage] 同步用户写入文件内容:${line}")
            ps.append(line)
        }
        ps.close()
        dos.close()
        reader.close()

        //11.解析文件获取响应值
        SAXReader saxReader = new SAXReader()
        Document document = saxReader.read(dataFile)
        Element root = document.getRootElement()
        String responseDecode = ""
        if (root.elements().get(0).elements().get(0).elements().get(0).getText().length() > 60000) {
            //取前6万个字符
            String data1 = root.elements().get(0).elements().get(0).elements().get(0).getText().substring(0, 60000)
            String data2 = root.elements().get(0).elements().get(0).elements().get(0).getText().substring(60000)
            responseDecode = Base64Utils.decode(data1) + Base64Utils.decode(data2)
        } else {
            responseDecode = Base64Utils.decode(root.elements().get(0).elements().get(0).elements().get(0).getText())
        }

        //12.响应数据转JavaBean
        RespUser respUser = XMLUtils.convertXmlStrToObjectDecode(RespUser.class, responseDecode) as RespUser
        if (respUser == null) {
            throw new RuntimeException("响应数据转JavaBean失败")
        }

        //13.进行解密操作
        respUser = XMLUtils.convertToXmlDecode(respUser, UserData.class) as RespUser
        log.info("解密后实体类:\n${respUser.toString()}")
        return respUser
    }

    /**
     * 同步用户
     */
    int syncUser() {
        RespUser respUser = getRespUserByPage("1")
        int totalUserPage = Integer.parseInt(respUser.getData().getTotalPageNum())
        for (int i = 1; i <= totalUserPage; i++) {
            if (i != 1) {
                respUser = getRespUserByPage("${i}")
            }
            //respDept（包含部门数Count、当前页码CurPageNum、总页码TotalPageNum、部门列表list）
            List<UserModel> list = respUser.getData().getList()
            for (UserModel userModel : list) {
                //获取用户代码
                String yhdm = userModel.getYHDM()
                def employee = Employee.findByManufacturerAndSynchronizationId(Manufacturer.TONGDAHAI, yhdm)
                if (!employee) {
                    employee = new Employee()
                    employee.uid = UUIDGenerator.nextUUID()
                    employee.manufacturer = Manufacturer.TONGDAHAI
                    employee.synchronizationId = yhdm
                }
                //获取用户名称
                String xm = userModel.getXM()
                employee.name = xm
                //获取用户部门
                String bmmc = userModel.getBMMC()
                def dept = Department.findByName(bmmc)
                employee.dept = dept
                //获取用户职位
                String sfpsy = userModel.getSFPSY()
                String sfspz = userModel.getSFSPZ()
                if (sfpsy == "1" || sfspz == "1") {
                    employee.position = PositionStatus.JUDGE
                } else {
                    employee.position = PositionStatus.OTHER
                }
                employee.save(flush: true)
                if (employee.hasErrors()) {
                    String msg = "保存人员出错"
                    log.error("${msg},错误信息:${employee.errors}")
                    throw new RuntimeException(msg)
                }
                toolBoxService.importUser(employee)
            }
        }
        return 0
    }

    /**
     * 根据页码及时间段获取排期
     */
    RespPlan getRespPlanByPageNumAndTime(String pageNum, String timeRange) {
        //4.拼装参数
        PlanRequestModel planRequestModel = new PlanRequestModel()
        planRequestModel.setKTRQ(timeRange)
        planRequestModel.setPAGENUM(pageNum)

        //5.实体类转xml
        String modelXML = XMLUtils.convertToXml(planRequestModel)
        log.info("请求 排期信息-请求参数:${modelXML}")

        //6.xml标签内编码
        String modelXMLEncode = XMLUtils.convertToXmlEncode(planRequestModel)
        if (modelXMLEncode == null) {
            throw new RuntimeException("请求JavaBean转XML失败")
        }
        log.info("请求 排期信息-标签内容编码后:${modelXMLEncode}")

        //7.xml整体编码
        String request = Base64Utils.encode(modelXMLEncode)
        if (StringUtils.isBlank(request)) {
            throw new RuntimeException("请求XML整体编码失败")
        }
        log.info("请求 排期信息-整体编码后:{}", request)

        //8.获取响应值
        String response = szftWebService.getPlKt(FYDM, TOKEN, request)
        if (StringUtils.isBlank(response)) {
            throw new RuntimeException("响应数据为空")
        }
        log.info("响应 排期信息-响应信息:${response}")

        //9.响应值整体解码
        String responseDecode = Base64Utils.decode(response)
        if (StringUtils.isBlank(responseDecode)) {
            throw new RuntimeException("响应数据整体解码失败")
        }
        log.info("响应 排期信息-整体解码后:${responseDecode}")

        //10.响应数据转JavaBean
        RespPlan respPlan = XMLUtils.convertXmlStrToObjectDecode(RespPlan.class, responseDecode)

        //11.进行解密操作
        respPlan = XMLUtils.convertToXmlDecode(respPlan, PlanData.class)
        log.info("解密后实体类:\n${respPlan.toString()}")
        return respPlan
    }

    /**
     * 同步排期
     * @return 同步结果
     */
    int syncPlan() {
        //拼接请求时间
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd")
        Date startDate = new Date()
        String startStr = format.format(startDate) //开始时间（今天）
        Calendar calendar = Calendar.getInstance()
        calendar.setTime(startDate)
        calendar.add(Calendar.DAY_OF_YEAR, 7) //（七天后）
        Date endDate = calendar.getTime()
        String endStr = format.format(endDate)//结束时间
        String timeRange = startStr + "-" + endStr
        RespPlan respPlan = getRespPlanByPageNumAndTime("1", timeRange)
        if (respPlan.getResult().getCode() != "0") {
            //{result=RespResult{code='1', msg='上班期间（9:00-11:30,13:30-17:00）不允许访问！'}, data=null}
            log.info("请求排期信息,响应错误详情:${respPlan.getResult().getMsg()}")
            return 0
        }
        int totalPlanPage = Integer.parseInt(respPlan.getData().getTotalPageNum())
        for (int i = 1; i <= totalPlanPage; i++) {
            if (i != 1) {
                respPlan = getRespPlanByPageNumAndTime("${i}", timeRange)
            }
            //respDept（包含部门数Count、当前页码CurPageNum、总页码TotalPageNum、部门列表list）
            List<PlanModel> list = respPlan.getData().getList()
            for (PlanModel planModel : list) {
                int flag = 0 //初始状态
                String archives = planModel.getAH()
                String ahdm = planModel.getAHDM()
                String rowuuid = planModel.getROWUUID()
                def planInfo = new PlanInfo()
                def caseInfo = CaseInfo.findByArchives(archives)

                if (caseInfo) {
                    planInfo = PlanInfo.findByCaseInfoAndManufacturerAndSynchronizationId(caseInfo, Manufacturer.TONGDAHAI, rowuuid)
                    if (!planInfo) {
                        flag = 1 //案件存在排期不存在
                    } else {
                        if (planInfo && planInfo.status != PlanStatus.PLAN) {
                            flag = 2 //案件存在排期存在但是不是排期状态
                        } else {
                            flag = 3 //案件存在排期存在但是是排期状态可以修改
                        }
                    }
                }
                //案件和排期都存在但是不是排期状态
                if (flag == 2) {
                    continue
                }
                if (flag == 0) {
                    //案件不存在新建案件--从通达海获取最新的案件信息(只新增不更新,如果存在更新的情况再作修改)
                    CaseModel caseModel = this.getRespCaseByAh(archives).data.list.first()
                    caseInfo = new CaseInfo(
                            uid: UUIDGenerator.nextUUID(),
                            synchronizationId: caseModel.getAHDM(),  //同步主键--案号代码
                            manufacturer: Manufacturer.TONGDAHAI,
                            active: DataStatus.SHOW,
                            archives: archives,
                            name: caseModel.getAJMC(),
                            department: Department.findBySynchronizationId(caseModel.getCBBM1()),
                            filingDate: caseModel.LARQ
                    )
                    String caseTypeCode = caseModel.AJLXBS //案件类型标识
                    String caseTypeName = caseModel.AJLXMC //案件类型名称
                    def caseType = CaseType.findByCodeOrName(caseTypeCode, caseTypeName)
                    if (caseType) {
                        if (caseType.name != caseTypeName) {
                            caseType.name = caseTypeName
                            caseType.save(flush: true)
                        }
                    } else {
                        def caseTypeNew = new CaseType(
                                code: caseTypeCode,
                                name: caseTypeName
                        )
                        caseTypeNew.save(flush: true)
                    }
                    caseInfo.type = CaseType.findByName(caseTypeCode)
                    //原告:史朝军;被告:张尧华----兼容官渡数据,官渡有不少这样的数据"DSR":"被告人:段桂福"
                    String[] dsrArr = caseModel.getDSR().split(";")
                    if (dsrArr.length == 2) {
                        caseInfo.accuser = dsrArr[0].split(":")[1]
                        caseInfo.accused = dsrArr[1].split(":")[1]
                    } else if (dsrArr.length == 1) {
                        String[] arr = dsrArr[0].split(":")
                        if (arr[0].contains("原")) {
                            caseInfo.accuser = arr[1]
                        } else if (arr[0].contains("被")) {
                            //拼装被告
                            caseInfo.accused = arr[1]
                        }
                    }
                    //合议庭成员不再进行处理(处理也应该在排期内进行)
                }
                //排期不存在
                if (flag == 0 || flag == 1) {
                    planInfo = new PlanInfo()
                    planInfo.uid = UUIDGenerator.nextUUID()  //排期标识
                    planInfo.status = PlanStatus.PLAN  //排期状态
                    planInfo.active = DataStatus.SHOW  //是否显示
                    planInfo.manufacturer = Manufacturer.TONGDAHAI  //所属厂商
                    planInfo.synchronizationId = rowuuid  //同步主键
                }
                Courtroom courtroom = Courtroom.findBySycLuoHuId(planModel.getFTBH())  //根据法庭编号查找法庭
                if (!courtroom) {
                    courtroom = Courtroom.findAll().get(0) //查找法庭
                }
                planInfo.courtroom = courtroom  //排期所属法庭

                Employee cbr = Employee.findByManufacturerAndSynchronizationId(Manufacturer.TONGDAHAI, planModel.getCBR())

                Employee judge = Employee.findByManufacturerAndSynchronizationId(Manufacturer.TONGDAHAI, planModel.getSPZ())
                //审判长即法官
                judge?(planInfo.judge = judge):(planInfo.judge = cbr)

                Employee secretary = Employee.findByManufacturerAndSynchronizationId(Manufacturer.TONGDAHAI, planModel.getSJY())
                //书记员
                secretary?(planInfo.secretary = secretary):(planInfo.secretary = cbr)

                log.info("案号为${archives}的排期合议组成员列表为${planModel.getHYCY()}")

                String ktrq = planModel.getKTRQ()  //20201223  //开庭日期
                String ktsj = planModel.getKTSJ()  //09:00  //开庭时间
                String jssj = planModel.getJSSJ()  //10:00  //闭庭时间
                planInfo.startDate = DateUtil.parse(ktrq + " " + ktsj, "yyyyMMdd HH:mm")
                planInfo.endDate = DateUtil.parse(ktrq + " " + jssj, "yyyyMMdd HH:mm")
                caseInfo.save(flush: true)
                if (caseInfo.hasErrors()) {
                    String msg = "保存案件出错"
                    log.error("${msg},错误信息:${caseInfo.errors}")
                    throw new RuntimeException(msg)
                }
                planInfo.caseInfo = caseInfo
                planInfo.save(flush: true)
                if (planInfo.hasErrors()) {
                    String msg = "保存排期出错"
                    log.error("${msg},错误信息:${planInfo.errors}")
                    throw new RuntimeException(msg)
                }
            }
        }
        return 0
    }

    /**
     * 根据案号查询案件信息
     */
    RespCase getRespCaseByAh(String ah) {
        //4.拼装参数
        CaseRequestModel caseRequestModel = new CaseRequestModel()
        caseRequestModel.setAH(ah)

        //5.实体类转xml
        String modelXML = XMLUtils.convertToXml(caseRequestModel)
        log.info("[TongdahaiService.getRespCaseByAh] 请求 案件信息-请求参数:${modelXML}")

        //6.xml标签内编码
        String modelXMLEncode = XMLUtils.convertToXmlEncode(caseRequestModel)
        if (modelXMLEncode == null)
            throw new RuntimeException("[TongdahaiService.getRespCaseByAh] 请求JavaBean转XML失败.")
        log.info("[TongdahaiService.getRespCaseByAh] 请求 案件信息-标签内容编码后:${modelXMLEncode}")

        //7.xml整体编码
        String request = Base64Utils.encode(modelXMLEncode)
        if (StringUtils.isBlank(request))
            throw new RuntimeException("[TongdahaiService.getRespCaseByAh] 请求XML整体编码失败.")
        log.info("[TongdahaiService.getRespCaseByAh] 请求 案件信息-整体编码后:${request}")

        //8.获取响应值
        String response = szftWebService.getPlAj(FYDM, TOKEN, request)
        if (StringUtils.isBlank(response))
            throw new RuntimeException("响应数据为空.")
        log.info("[TongdahaiService.getRespCaseByAh] 响应 案件信息-响应信息:${response}")

        //9.响应值整体解码
        String responseDecode = Base64Utils.decode(response)
        if (StringUtils.isBlank(responseDecode))
            throw new RuntimeException("[TongdahaiService.getRespCaseByAh] 响应数据整体解码失败.")
        log.info("[TongdahaiService.getRespCaseByAh] 响应 案件信息-整体解码后:${responseDecode}")

        //10.响应数据转JavaBean
        RespCase respCase = XMLUtils.convertXmlStrToObjectDecode(RespCase.class, responseDecode) as RespCase

        //11.进行解密操作
        respCase = XMLUtils.convertToXmlDecode(respCase, CaseData.class) as RespCase
        log.info("响应 解密后案件实体类:\n${respCase.toString()}")
        return respCase
    }

    /**
     * 解决案件没有当事人的问题
     */
    void handleCaseInfo () {
        //获取所有通达海排期
        def caseInfoList = CaseInfo.findAllByManufacturer("tongdahai")
        log.info("[TongdahaiService.handleCaseInfo] 获取到通达海案件数量:${caseInfoList.size()}")
        for (CaseInfo caseInfo:caseInfoList) {
            try {
                log.info("[TongdahaiService.handleCaseInfo] 待处理案件案号:${caseInfo.archives}")
                //根据案号获取案件信息
                CaseModel caseModel = this.getRespCaseByAh(caseInfo.archives).data.list.first()
                String caseTypeCode = caseModel.AJLXBS //案件类型标识
                String caseTypeName = caseModel.AJLXMC //案件类型名称
                def caseType = CaseType.findByCodeOrName(caseTypeCode, caseTypeName)
                if (caseType) {
                    if (caseType.name != caseTypeName) {
                        caseType.name = caseTypeName
                        caseType.save(flush: true)
                    }
                } else {
                    def caseTypeNew = new CaseType(
                            code: caseTypeCode,
                            name: caseTypeName
                    )
                    caseTypeNew.save(flush: true)
                }
                caseInfo.type = CaseType.findByName(caseTypeCode)
                //原告:史朝军;被告:张尧华----兼容官渡数据,官渡有不少这样的数据"DSR":"被告人:段桂福"
                String[] dsrArr = caseModel.getDSR().split(";")
                if (dsrArr.length == 2) {
                    caseInfo.accuser = dsrArr[0].split(":")[1]
                    caseInfo.accused = dsrArr[1].split(":")[1]
                } else if (dsrArr.length == 1) {
                    String[] arr = dsrArr[0].split(":")
                    if (arr[0].contains("原")) {
                        caseInfo.accuser = arr[1]
                    } else if (arr[0].contains("被")) {
                        //拼装被告
                        caseInfo.accused = arr[1]
                    }
                }
                log.info("[TongdahaiService.handleCaseInfo] 案件当事人:原告(${caseInfo.accuser}),被告(${caseInfo.accused})")
                caseInfo.save(flush: true)
                sleep(200)
            } catch (ignored) {
                log.error("[TongdahaiService.handleCaseInfo] 案号为${caseInfo.archives}的案件被处理失败.")
            }
        }
    }

    void sync() {
        syncDept()
        synCourtRoom()
        syncUser()
        syncPlan()
    }
}
