package com.hxht.autodeploy.sync.shandong

import cn.hutool.core.date.DateUtil
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.hxht.techcrt.CollegialType
import com.hxht.techcrt.CourtroomStatus
import com.hxht.techcrt.DataStatus
import com.hxht.techcrt.PlanStatus
import com.hxht.techcrt.court.admin.ToolBoxService
import com.hxht.techcrt.court.CaseInfo
import com.hxht.techcrt.court.CaseType
import com.hxht.techcrt.court.Collegial
import com.hxht.techcrt.court.Courtroom
import com.hxht.techcrt.court.Department
import com.hxht.techcrt.court.Employee
import com.hxht.techcrt.court.PlanInfo
import com.hxht.techcrt.utils.MD5Utils
import com.hxht.techcrt.utils.Spcx
import com.hxht.techcrt.utils.UUIDGenerator
import grails.gorm.transactions.Transactional

@Transactional
class DataImportService {
    ToolBoxService toolBoxService
    def planSyncId

    /**
     * 预定案件排期信息
     * <p>
     * 注意此处，每一条数据都是一条事物，要保证排期，案件，人员编号，法庭事物一致性
     */
    def getCaseAndPlan(JSONObject obj, Courtroom room) {
        try {
            // 处理部门
            def dept = dept(obj.getJSONObject("LABM"))
            // 处理书记员
            def secretary = secretary(obj.getJSONObject("sjy"),dept)
            //处理法官
            def zsfg = obj.getJSONObject("zsfg")
            if (zsfg.size() == 0) {//如果主审法官不存在就去合议庭去第一个元素
                zsfg = obj.getJSONArray("hyt_List").getJSONObject(0)
            }
            //法官目前待定
            def judge = judge(zsfg,dept)

            JSONArray ahs = obj.getJSONArray("ah_Info")
            if (ahs.size() == 0) {//不存在对应的案件信息
                log.info("处理山东法院对接接口排期++++" + obj.getString("pqid") + "号,没有对应的案件信息,不在处理")
            }
            if (ahs.size() > 1) {//一条排期有多个案件的情况
                log.info("处理山东法院对接接口--排期:(+++"+ obj.toJSONString() +"+++)信息存在多个案件,返回的本排期对应的案件数量:" + ahs.size())
            }
            //循环遍历所有案件 每个案件对应一个排期
            def ah_Info = new JSONObject()
            for (int k = 0; k < ahs.size(); k++) {
                ah_Info = ahs.getJSONObject(k)
                sub(ah_Info, secretary, judge, dept, room, obj)
            }

        } catch (Exception e) {
            log.error("处理山东法院对接接口---处理单个排期:" + obj.toJSONString() + "出错," + e.getMessage())
        }
    }


    private void sub(JSONObject ah_Info, Employee secretary, Employee judege, Department dept, Courtroom courtroom, JSONObject obj) throws Exception {

        def caseinformation = null
        //案号
        def archives = ah_Info.getString("ah")
        def casein = CaseInfo.findByArchives(archives)
        if (casein == null) {//不存在
            def caseInfo = new CaseInfo()
            caseinformation = saveCase(ah_Info,caseInfo,dept, obj)
        } else {
            // 处理排期编号
            def pqid = MD5Utils.getMD5(archives, false, 32)//生成32位小写md5
            def caseinfo = CaseInfo.findBySynchronizationId(pqid)
            //存在相同案号,判断是本地立案还是东软的案件
            if (caseinfo) {
                //是东软导入的案件,进行更新
                caseinformation = saveCase(ah_Info,caseinfo,dept, obj)
            } else {
                //说明不用更新
                caseinformation = casein
            }
        }

        def sycPlan = MD5Utils.getMD5(archives, false, 32) + "-" + Spcx.getSpcx(ah_Info.getString("spcx")) + "" //排期的案号 + 庭审的庭次
        def plan = PlanInfo.findBySynchronizationId(sycPlan)
        planSyncId = sycPlan
        if (plan){
            if (plan.status == PlanStatus.PLAN){//如果是排期状态则更新排期
                savePlan(secretary, judege, courtroom, obj,plan, sycPlan ,caseinformation)
            }
        }else{
            def planInfo = new PlanInfo()
            savePlan(secretary, judege, courtroom, obj,planInfo, sycPlan ,caseinformation)
        }

    }

    //2.1部门信息
    def dept(String name) {
        try {
            if (!name) {
                return
            }
            // 先判断部门是不是存在，存在更新，不存在插入
            def dept = Department.findByName(name)
            if (!dept){
                dept = new Department()
                dept.name = name
                dept.uid = UUIDGenerator.nextUUID()
                dept.save(flush: true)
                if (dept.hasErrors()) {
                    log.error("DataImportService.flush: true 处理山东法院对接接口++保存不存在的部门出错------[${dept.errors}]")
                    throw new RuntimeException()
                }
            }else{
                return dept
            }
        } catch (Exception e) {
            log.error("处理山东法院对接接口---处理部门出错:" + dept1.toJSONString() + "," + e.getMessage())
        }
    }

    //2.3人员(书记员)
    def secretary(String name, Department dept) {
        try {
            if (!name) {
                return
            }
            // 处理数据
            def returnName = Employee.findByName(name)
            if (returnName){
                return returnName
            }else{
                def employee = new Employee()
                employee.name = name
                employee.position = 6
                employee.uid = UUIDGenerator.nextUUID()
                employee.dept = dept
                employee.save(flush: true)
                if (employee.hasErrors()) {
                    log.error("DataImportService.secretary 处理山东法院对接接口--保存不存在的人员出错------[${employee.errors}]")
                    throw new RuntimeException()
                }
                toolBoxService.importUser(employee)
                return employee
            }
        } catch (Exception e) {
            log.error("处理山东法院对接接口--处理书记员," + secretary.toJSONString() + ",出错:" + e.getMessage())
        }
    }

    //2.3人员
    def judge(String name, Department dept) {
        def employee
        try {
            if (!name) {
                return
            }
            employee = Employee.findByName(name)
            if (employee){
                return employee
            }else{
                employee = new Employee()
                employee.name = name
                employee.position = 2
                employee.uid = UUIDGenerator.nextUUID()
                employee.dept = dept
                employee.save(flush: true)
                if (employee.hasErrors()) {
                    log.error("DataImportService.judge 处理山东法院对接接口+++保存不存在的人员出错------[${employee.errors}]")
                    throw new RuntimeException()
                }
                toolBoxService.importUser(employee)
            }
        } catch (Exception e) {
            log.error("处理山东法院对接接口---处理法官出错," + judge.toJSONString() + ",错误信息:" + e.getMessage())
        }
        employee
    }

    //查找案件类型
    def caseType(String anhao) {
        if (anhao){
            anhao = anhao.replaceAll("粤","")
            anhao = anhao.replaceAll("号","")
            anhao = anhao.replaceAll("（","")
            anhao = anhao.replaceAll("）","")
            anhao = anhao.replaceAll("\\d+","")
            anhao = anhao.trim()
            CaseType.findByShortName(anhao)
        }
    }

    //保存案件信息
    def saveCase(JSONObject ah_Info,CaseInfo caseInfo,Department dept,JSONObject obj){
        //案由
        def laay = ah_Info.getString("laay")
        caseInfo.summary = laay
        // 案件名称--也设置为案由--先这样处理，
        caseInfo.name = laay
        //案号
        def archives = ah_Info.getString("ah")
        caseInfo.archives = archives
        // 处理排期编号
        String  pqid = MD5Utils.getMD5(archives, false, 32)//生成32位小写md5
        //审判次数
        def spcx = Spcx.getSpcx(ah_Info.getString("spcx")) + ""
        //处理案件类别
        CaseType type = caseType(archives)
        caseInfo.type = type
        //原告,被告
        String accuse = "";
        String accused = "";
        JSONArray dsr_array = ah_Info.getJSONArray("dsr")
        if (dsr_array.size() > 0) {
            int yuangao = 0
            int beigao = 0
            for (int j = 0; j < dsr_array.size(); j++) {
                JSONObject dsr = dsr_array.getJSONObject(j)
                String ssdw = dsr.getString("ssdw")
                if (ssdw != null && !"".equals(ssdw)) {
                    String name = dsr.getString("name")
                    if (ssdw.equals("原告") || ssdw.equals("申请人") || ssdw.equals("上诉人")) {
                        yuangao++;
                        //原告
                        //如果是第一个要特殊处理
                        if (j == dsr_array.size() - 1) {
                            if (yuangao > 1) {
                                accuse = accuse + "," + name;
                            } else {
                                accuse = accuse + name;
                            }

                        } else {
                            if ("".equals(accuse)) {
                                accuse = accuse + name;
                            } else {
                                accuse = accuse + "," + name;
                            }

                        }
                    }
                    if (ssdw.equals("被告") || ssdw.equals("被告人") || ssdw.equals("被申请人") || ssdw.equals("被上诉人")) {
                        beigao++;
                        //被告
                        if (j == dsr_array.size() - 1) {
                            if (beigao > 1) {
                                accused = accused + "," + name;
                            } else {
                                accused = accused + name;
                            }
                        } else {
                            if ("".equals(accused)) {
                                accused = accused + name;
                            }
                            accused = accused + "," + name;
                        }
                    }
                }
            }
        }

        caseInfo.accuser = accuse
        caseInfo.accused = accused
        caseInfo.department = dept
        //立案日期
//        caseInfo.filingDate = DateFormat.getDateInstance().parse(ah_Info.getString("larq"))
        // 预定日期
        def ktrq = obj.getString("ktrq")
        caseInfo.filingDate = DateUtil.parse(ktrq + " " + obj.getString("kssj"), "yyyy-MM-dd HH:mm:ss")
        caseInfo.active = DataStatus.SHOW //使用
        caseInfo.synchronizationId = pqid
        caseInfo.uid = UUIDGenerator.nextUUID()
        caseInfo.save(flush: true)
        if (caseInfo.hasErrors()) {
            log.error("DataImportService.saveCase 处理山东法院对接接口---保存不存在的案件信息出错------[${caseInfo.errors}]")
            throw new RuntimeException()
        }
        caseInfo
    }

    //保存审理庭列表
    def saveCollegial(JSONObject hyt){
        def collegial = new Collegial()
        collegial.name = hyt.getString("userName")
        def type = hyt.getString("roleName")
        if (CollegialType.getCode(type) == CollegialType.PERSIDING_JUDGE) {
            collegial.type = CollegialType.PERSIDING_JUDGE
        } else if (CollegialType.getCode(type) == CollegialType.JUDGE) {
            collegial.type = CollegialType.JUDGE
        } else if (CollegialType.getCode(type) == CollegialType.PEOPLE_ASSESSOR) {
            collegial.type = CollegialType.PEOPLE_ASSESSOR
        }
        if (!collegial.type) {
            collegial.type = CollegialType.OTHER
        }
        collegial.save(flush: true)
        if (collegial.hasErrors()) {
            log.error("处理山东法院对接接口+++保存合议庭时出错DataImportService.saveCollegial collegial [${collegial.errors}]")
            throw new RuntimeException()
        }
        collegial
    }

    //保存排期信息
    def savePlan(Employee secretary, Employee judege, Courtroom courtroom, JSONObject obj, PlanInfo planInfo, String sycPlan, CaseInfo caseInfo){
        planInfo.judge = judege
        planInfo.secretary = secretary
        planInfo.courtroom = courtroom
        planInfo.synchronizationId = sycPlan //排期的案号 + 庭审的庭次
        planInfo.uid = UUIDGenerator.nextUUID()
        //合议庭成员
        def hytList = obj.getJSONArray("hyt_List")

        if (hytList){
            def collegialList = Collegial.findAllBySynchronizationId(planSyncId)
            if (collegialList){
                planInfo.collegial = collegialList
            }else{
                for (int k = 0; k < hytList.size(); k++) {
                    def hyt = hytList.getJSONObject(k)
                    def colleage = saveCollegial(hyt)
                    collegialList.add(colleage)
                }
                planInfo.collegial = collegialList
            }

        }
        // 预定日期
        def ktrq = obj.getString("ktrq")
        //预定开始日期+时间
        planInfo.startDate = DateUtil.parse(ktrq + " " + obj.getString("kssj"), "yyyy-MM-dd HH:mm:ss")
        //预定结束时间
        planInfo.endDate = DateUtil.parse(ktrq + " " + obj.getString("jssj"), "yyyy-MM-dd HH:mm:ss")
        planInfo.status = PlanStatus.PLAN
        planInfo.active = DataStatus.SHOW
        planInfo.allowPlay = 0
        planInfo.caseInfo = caseInfo
        planInfo.save(flush: true)
        if (planInfo.hasErrors()) {
            def msg = "[DataImportService.sub]处理山东法院对接接口---保存plan 失败 errors [${planInfo.errors}]"
            log.error(msg)
            throw new RuntimeException(msg)
        }
    }
    /**
     * 获取法庭数据
     */
    def addCourtRoom(String name) {
        try {
            // 根据name判断是否存在
            def courtroom = Courtroom.findByName(name)
            if (courtroom){ //存在
                courtroom.active = DataStatus.SHOW
                courtroom.status = CourtroomStatus.NORMAL
                courtroom.save(flush: true)
                if (courtroom.hasErrors()) {
                    log.info("DataImportService.addCourtRoom 处理山东法院对接接口保存存在的法庭出错------[${courtroom.errors}]")
                    throw new RuntimeException()
                }
                return courtroom
            }else{
                def room = new Courtroom()
                room.uid = UUIDGenerator.nextUUID()
                room.active = DataStatus.SHOW
                room.status = CourtroomStatus.NORMAL
                room.name = name
                room.save(flush: true)
                if (room.hasErrors()) {
                    log.info("DataImportService.addCourtRoom 处理山东法院对接接口保存不存在的法庭出错------[${room.errors}]")
                    throw new RuntimeException()
                }
                return room
            }
        } catch (Exception e) {
            log.error("处理山东法院对接接口法庭出错:" + e.getMessage())
        }
    }

    //查找案件类型
    def getCaseType(String ajxz) {
        if (!ajxz){
            CaseType.findAll().get(0)
        }
        changeCodeToName(ajxz)
    }

    def changeCodeToName(String code){
        def name
        def caseType
        if("1".equals(code)){
            name="刑事"
            caseType = CaseType.findByName("刑事一审案件")
        }else if("2".equals(code)){
            name="民事"
            caseType = CaseType.findByName("民事一审案件")
        }else if("6".equals(code)){
            name="行政"
            caseType = CaseType.findByName("行政一审案件")
        }else if("7".equals(code)){
            name="执行"
            caseType = CaseType.findByName("首次执行案件")
        }else{
            caseType = CaseType.findByName("管辖案件")
        }
        caseType
    }

    /* 上诉人:孙贵银;;被上诉人:马训;苏艳丽;;原审第三人:菏泽豪联房地产开发有限公司
       上诉人:山东巨野农村商业银行股份有限公司;;原审被告:李长安;奚道坤;奚修占;奚道玉;奚道岭;;被上诉人:奚长存;;原审被告:李长进;王建秋;奚道红;奚道田
       上诉人:郭瑞良;被上诉人:李冬梅,王怀卿
       原告:中节能太阳能科技（镇江）有限公司;被告:西藏东旭电力工程有限公司,四川东旭电力工程有限公司
       上诉人:全芝勤;;被上诉人:时圣彬*/
    def getDsr(String dsr){
        def json = new JSONObject()
        def yuangao = ""
        def beigao = ""
        //把";;"替换为单分号
        dsr = dsr.replace(";;",";")
        String[] s1=dsr.split(";")
        if(s1.length>0){
            for(int i=0;i<s1.length;i++){
                //获取前置
                String[] key_value=s1[i].split(":")
                String key= key_value[0]
                if("原告".equals(key)||"上诉人".equals(key)){
                    yuangao=key_value[1]
                    break
                }

            }
            for(int i=0;i<s1.length;i++){
                //获取前置
                String[] key_value=s1[i].split(":")
                String key= key_value[0]
                if("被告".equals(key)||"被上诉人".equals(key)){
                    beigao=key_value[1]
                    break
                }

            }
        }
        json.put("accuse",yuangao)
        json.put("accused",beigao)
        return json
    }
}
