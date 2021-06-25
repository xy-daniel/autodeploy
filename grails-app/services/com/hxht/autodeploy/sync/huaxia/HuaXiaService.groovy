package com.hxht.autodeploy.sync.huaxia

import cn.hutool.core.codec.Base64
import cn.hutool.core.date.DateUtil
import cn.hutool.http.webservice.SoapClient
import com.hxht.techcrt.DataStatus
import com.hxht.techcrt.PlanStatus
import com.hxht.techcrt.court.CaseInfo
import com.hxht.techcrt.court.CaseType
import com.hxht.techcrt.court.Courtroom
import com.hxht.techcrt.court.Department
import com.hxht.techcrt.court.Employee
import com.hxht.techcrt.court.PlanInfo
import com.hxht.techcrt.court.TrialInfo
import com.hxht.techcrt.court.VideoInfo
import com.hxht.techcrt.court.admin.ToolBoxService
import com.hxht.techcrt.court.manager.SystemController
import com.hxht.techcrt.sync.huaxia.enumerate.*
import com.hxht.techcrt.sync.huaxia.model.*
import com.hxht.techcrt.sync.util.XMLUtils
import com.hxht.techcrt.utils.CfgUtil
import com.hxht.techcrt.utils.IpUtil
import grails.core.GrailsApplication
import grails.gorm.transactions.Transactional
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * 2021.03.17 >>> 华夏推送业务创建 daniel
 * 2021.03.22 >>> 华夏推送使用法院代码将分级码修改为法院代码 daniel
 * 2021.03.22 >>> 法庭修改为只上传拥有和兴庭审设备的法庭 daniel
 * 2021.03.23 >>> 修改推送人员信息代码 daniel
 * 2021.03.25 >>> 添加推送点播视频接口 daniel
 * 2021.03.31 >>> 推送排期时将公开开庭(gkkt)参数从未指定(255)修改为是(1) daniel
 * 2021.04.14 >>> 增加推送直播、笔录接口 daniel
 */
@Transactional
class HuaXiaService {

    RcsDataInputPortType rcsDataInputPortType
    GrailsApplication grailsApplication
    ToolBoxService toolBoxService

    /**
     * 华夏推送法庭信息
     */
    void pushCourtroom() {
        //拼装参数(查询所有device_ip不是NULL并且不是空的法庭)
        List<Courtroom> dataCourtroomList = Courtroom.findAllByDeviceIpIsNotNull()
        List<Courtroom> allCourtRoom = new ArrayList<>()
        dataCourtroomList.each {
            if (it.deviceIp) {
                allCourtRoom.add(it)
            }
        }
        log.info("[HuaXiaService.pushCourtroom] 获取到法庭数量:${allCourtRoom.size()}")
        CourtRoomListModel courtRoomListModel = new CourtRoomListModel()
        List<CourtRoomModel> courtRoomModelList = new ArrayList<>()
        allCourtRoom.each {
            CourtRoomModel courtRoomModel = new CourtRoomModel()
            courtRoomModel.ftbs = it.uid  //法庭标识
            courtRoomModel.ftmc = it.name  //法庭名称
            courtRoomModel.ftlx = CourtRoomType.XT.code as String  //法庭类型
            courtRoomModel.ftms = CourtRoomMode.HIGH_DEFINITION.code as String  //法庭模式
            courtRoomModel.ftgg = CourtRoomStandard.STANDARD.code as String  //法庭规格
            courtRoomModel.fbl = "1920x1080"  //分辨率
            courtRoomModel.szftcs = "110"  //数字法庭厂商(和兴宏图为110)
            courtRoomModel.jsrq = DateUtil.parse("2016-01-01", "yyyy-MM-dd")  //建设日期(没有这个参数，默认2016-01-01)
            courtRoomModel.fydm = SystemController.currentCourt.val  //法院代码
            courtRoomModelList.add(courtRoomModel)
        }
        courtRoomListModel.list = courtRoomModelList
        String convertToXml = XMLUtils.convertToXml(courtRoomListModel)
        //发送消息
        log.info("[HuaXiaService.pushCourtroom] 推送法庭信息:${convertToXml}")
        //处理响应
        handleResponse(rcsDataInputPortType.courtRoom(convertToXml), "pushCourtroom")
    }

    /**
     * 华夏推送人员信息
     */
    void pushUser() {
        //拼装参数
        List<Employee> allUser = Employee.getAll()
        log.info("[HuaXiaService.pushUser] 获取到人员数量:${allUser.size()}")
        UserListModel userListModel = new UserListModel()
        List<UserModel> userModelList = new ArrayList<>()
        allUser.each {
            UserModel userModel = new UserModel()
            userModel.yhbs = it.uid  //人员标识为uid
            userModel.yhmc = it.name  //人员名称为真名
            userModel.bmbs = it.dept?.uid  //部门标识为uid
            userModel.bmmc = it.dept?.name  //部门名称
            userModel.sjhm = null  //手机号码
            userModel.dzyx = null  //电子邮箱
            userModel.imzh = null  //即时通信账号
            userModel.xb = null  //性别
            userModel.sfyx = "1"  //是否有效: 1、是 2、否 255、未指定
            userModel.fydm = SystemController.currentCourt.val  //法院代码
            userModelList.add(userModel)
        }
        userListModel.list = userModelList
        String convertToXml = XMLUtils.convertToXml(userListModel)
        Pattern p = Pattern.compile("\\s*|\t|\r|\n")
        Matcher m = p.matcher("<params>" + convertToXml.split("<params>")[1])
        convertToXml = "<![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + m.replaceAll("") + "]]>"
        log.info("[HuaXiaService.pushUser] 推送用户信息:${convertToXml}")
        //发送消息
        String response = SoapClient.create(grailsApplication.config.getProperty("syncData.huaxia.url"))
                .setMethod("rcs:user", "http://chnsys.com.cn/rcs_ws/")
                .setParam("protocolXml", convertToXml).send(true)
        //处理响应
        log.info("[HuaXiaService.pushUser] 推送用户后响应信息:${response}")
        int code = response.indexOf("code=\"1\"")
        if (code == -1) {
            log.error("[HuaXiaService.pushUser] 推送用户失败.")
        }
    }

    /**
     * 华夏推送部门信息
     */
    void pushDept() {
        //拼装参数
        List<Department> allDept = Department.getAll()
        log.info("[HuaXiaService.pushDept] 获取到部门数量:${allDept.size()}")
        DeptListModel deptListModel = new DeptListModel()
        List<DeptModel> deptModelList = new ArrayList<>()
        allDept.each {
            DeptModel deptModel = new DeptModel()
            deptModel.fydm = SystemController.currentCourt.val  //法院代码
            deptModel.bmbs = it.uid  //部门标识
            deptModel.bmmc = it.name  //部门名称
            deptModel.sfyx = "1"  //是否有效: 1、是 2、否 255、未指定
            Department parent = it.parent
            if (parent != null) {
                deptModel.sjbmbs = parent.uid  //上级部门标识
                deptModel.sjbmmc = parent.name  //上级部门名称
                deptModel.yzjd = "1"  //是否为叶子节点: 1、是叶子节点 2、不是叶子节点
            } else {
                deptModel.yzjd = "2"  //是否为叶子节点: 1、是叶子节点 2、不是叶子节点
            }
            deptModelList.add(deptModel)
        }
        deptListModel.list = deptModelList
        String convertToXml = XMLUtils.convertToXml(deptListModel)
        //发送消息
        log.info("[HuaXiaService.pushDept] 推送部门信息:${convertToXml}")
        //处理响应
        handleResponse(rcsDataInputPortType.department(convertToXml), "pushDept")
    }

    /**
     * 开庭公告接口
     * 开庭公告信息必须先于“正在开庭案件”接口上报
     */
    void pushPlan() {
        //获取今日所有未开庭的排期
        List<PlanInfo> todayPlan = PlanInfo.findAllByStartDateBetweenAndStatusAndActive(DateUtil.beginOfDay(new Date()), DateUtil.endOfDay(new Date()), PlanStatus.PLAN, DataStatus.SHOW)
        log.info("[HuaXiaService.pushPlan] 获取到未开庭的排期数量:${todayPlan.size()},开始推送.")
        todayPlan.each {
            //排期数据补充
            this.planDataSupply(it)
            //拼装参数
            String convertToXml = planParams(it)
            //发送消息
            log.info("[HuaXiaService.pushPlan] 推送开庭公告信息:${it.id}:${convertToXml}")
            //处理响应
            handleResponse(rcsDataInputPortType.trialPublish(convertToXml), "pushPlan")
        }
        log.info("[HuaXiaService.pushPlan] 获取到未开庭的排期数量:${todayPlan.size()},推送结束.")
    }

    /**
     * 上报正在开庭的案件的同时上报直播接口和笔录接口
     */
    void pushTrialStart() {
        //获取今日所有正在开庭的排期
        List<PlanInfo> startPlan = PlanInfo.findAllByStartDateBetweenAndStatusAndActive(DateUtil.beginOfDay(new Date()), DateUtil.endOfDay(new Date()), PlanStatus.SESSION, DataStatus.SHOW)
        log.info("[HuaXiaService.pushTrialStart] 获取到正在开庭的排期数量:${startPlan.size()},开始推送.")
        startPlan.each {
            //推送此正在开庭的排期
            this.pushSingleTrialStart(it)
            //推送此正在开庭的排期直播
            this.pushSingleTrialLive(it)
        }
        log.info("[HuaXiaService.pushTrialStart] 获取到正在开庭的排期数量:${startPlan.size()},推送结束.")
    }

    /**
     * 庭审笔录接口(15秒一次太频繁了,也是三分钟一次)
     */
    void pushTrialNote() {
        //获取今日所有正在开庭的排期
        List<PlanInfo> planInfoList = PlanInfo.findAllByStartDateBetweenAndStatusNotEqualAndActive(DateUtil.beginOfDay(new Date()), DateUtil.endOfDay(new Date()), PlanStatus.PLAN, DataStatus.SHOW)
        log.info("[HuaXiaService.pushTrialNote] 推送非排期状态的笔录信息:${planInfoList.size()},开始推送.")
        planInfoList.each {
            //拼装参数
            StringBuffer convertToXml = new StringBuffer()
            convertToXml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><params><tsbl>")
            convertToXml.append("<ajbs>${it.caseInfo.uid}</ajbs>")    //案件标识
            convertToXml.append("<tc>${PlanInfo.findAllByStartDateLessThanAndCaseInfoAndActive(it.startDate, it.caseInfo, DataStatus.SHOW).size() + 1}</tc>")
            //庭次
            convertToXml.append("<blgs>1</blgs>")    //笔录格式
            convertToXml.append("<blnr>${Base64.encode(new File("/usr/local/movies/" + TrialInfo.findAllByPlanInfo(it).last().noteWord))}</blnr>")
            //笔录内容,二进制
            convertToXml.append("<fydm>${SystemController.currentCourt.val}</fydm></tsbl></params>")    //法院代码
            //发送消息
            log.info("[HuaXiaService.pushTrialNote] 推送非排期状态的笔录信息:${it.id}:${convertToXml}")
            handleResponse(rcsDataInputPortType.trialNote(convertToXml as String), "pushTrialNote")
        }
        log.info("[HuaXiaService.pushTrialNote] 推送非排期状态的笔录信息:${planInfoList.size()},推送结束.")
    }

    /**
     * 正在开庭案件接口
     * 上报“正在开庭案件”之前，必须先上报“开庭公告信息”
     * 数字法庭系统开庭时，除了需要上报“正在开庭案件接口”，还必须上报“案件直播视频接口”和“庭审笔录”，平台才认为这是一次完整的开庭操作
     * @param it 需要上报的排期
     */
    void pushSingleTrialStart(PlanInfo it) {
        //排期数据补充
        this.planDataSupply(it)
        //拼装参数
        String convertToXml = planParams(it)
        //发送消息
        log.info("[HuaXiaService.pushSingleTrialStart] 推送正在开庭的排期信息:${it.id}:${convertToXml}")
        //处理响应(推送正在开庭的排期)
        handleResponse(rcsDataInputPortType.startedTrial(convertToXml), "pushSingleTrialStart")
    }

    /**
     * 案件直播视频接口
     * 上报“案件直播视频”之前，必须先上报“正在开庭案件信息”
     */
    void pushSingleTrialLive(PlanInfo it) {
        //拼装参数
        StringBuffer convertToXml = new StringBuffer()
        convertToXml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><params>")
        convertToXml.append("<ajbs>${it.caseInfo.uid}</ajbs>")    //案件标识
        convertToXml.append("<tc>${PlanInfo.findAllByStartDateLessThanAndCaseInfoAndActive(it.startDate, it.caseInfo, DataStatus.SHOW).size() + 1}</tc>")
        //庭次
        convertToXml.append("<fydm>${SystemController.currentCourt.val}</fydm>")    //法院代码
        convertToXml.append("<llfs>1</llfs>")    //支持的拉流方式:1-tcp,
        def videoList = [] //最后返回结果集
        def chnList = CfgUtil.getEncodeToStore(it.courtroom)
        for (def encode : chnList) {
            convertToXml.append("<ajspxx>" +
                    "<xhmc>${encode.name}</xhmc>" +
                    "<xh>${encode.number}</xh>" +
                    "<sfzl>1</sfzl>" +    //是否直连
                    "<url>${encode.url}</url>" +
                    "</ajspxx>")
        }
        convertToXml.append("</params>")
        //发送消息
        log.info("[HuaXiaService.pushSingleTrialLive] 推送正在开庭的排期直播信息:${it.id}:${convertToXml}")
        handleResponse(rcsDataInputPortType.trialLive(convertToXml as String), "pushSingleTrialLive")
    }

    /**
     * 华夏推送休庭信息
     */
    void pushTrialPause() {
        //获取今日所有正在休庭的排期
        List<PlanInfo> pausePlan = PlanInfo.findAllByStartDateBetweenAndStatusAndActive(DateUtil.beginOfDay(new Date()), DateUtil.endOfDay(new Date()), PlanStatus.ADJOURN, DataStatus.SHOW)
        log.info("[HuaXiaService.pushTrialPause] 获取到正在休庭的排期数量:${pausePlan.size()},开始推送.")
        pausePlan.each {
            //排期数据补充
            this.planDataSupply(it)
            //拼装参数
            String convertToXml = planParams(it)
            //发送消息
            log.info("[HuaXiaService.pushTrialPause] 推送正在休庭的排期信息:${it.id}:${convertToXml}")
            //处理响应
            handleResponse(rcsDataInputPortType.pauseTrial(convertToXml), "pushTrialPause")
        }
        log.info("[HuaXiaService.pushTrialPause] 获取到正在休庭的排期数量:${pausePlan.size()},推送结束.")
    }

    /**
     * 华夏推送闭庭信息
     */
    void pushTrialStop() {
        //获取今日所有正在闭庭的排期
        List<PlanInfo> stopPlan = PlanInfo.findAllByStartDateBetweenAndStatusAndActive(DateUtil.beginOfDay(new Date()), DateUtil.endOfDay(new Date()), PlanStatus.CLOSED, DataStatus.SHOW)
        log.info("[HuaXiaService.pushTrialStop] 获取到正在闭庭的排期数量:${stopPlan.size()},开始推送.")
        stopPlan.each {
            //排期数据补充
            this.planDataSupply(it)
            //拼装参数
            String convertToXml = planParams(it)
            //发送消息
            log.info("[HuaXiaService.pushTrialStop] 推送正在闭庭的排期信息:${it.id}:${convertToXml}")
            //处理响应
            handleResponse(rcsDataInputPortType.stoppedTrial(convertToXml), "pushTrialStop")
        }
        log.info("[HuaXiaService.pushTrialStop] 获取到正在闭庭的排期数量:${stopPlan.size()},推送结束.")
    }

    /**
     * 华夏推送闭庭录像信息
     */
    void pushTrialRecord() {
        String ip = IpUtil.getServiceIp()
        List<PlanInfo> planInfoList = PlanInfo.findAllByStartDateBetweenAndStatusAndActive(DateUtil.beginOfDay(new Date()), DateUtil.endOfDay(new Date()), PlanStatus.CLOSED, DataStatus.SHOW)
        log.info("[HuaXiaService.pushTrialRecord] 今日推送闭庭录像信息数量:${planInfoList.size()},开始推送.")
        for (int i = 0; i < planInfoList.size(); i++) {
            //拼装参数
            PlanInfo planInfo = planInfoList.get(i)
            //庭审录像列表
            TrialRecordListModel trialRecordListModel = new TrialRecordListModel()
            trialRecordListModel.ajbs = planInfo.caseInfo.uid  //案件标识
            trialRecordListModel.tc = (PlanInfo.findAllByStartDateLessThanAndCaseInfoAndActive(planInfo.startDate, planInfo.caseInfo, DataStatus.SHOW).size() + 1).toString()
            //庭次
            trialRecordListModel.fydm = SystemController.currentCourt.val  //法院代码
            //录像文件下载地址
            RecordFileDownloadModel recordFileDownloadModel = new RecordFileDownloadModel()
            recordFileDownloadModel.xzfs = "1"  //下载方式1-FTP
            recordFileDownloadModel.ftpurl = "ftp://${ip}:21/"  //FTP地址
            recordFileDownloadModel.ftpzh = "HXHT"  //FTP账号
            recordFileDownloadModel.ftpmm = "FTP2011"  //FTP密码
            //设置录像文件下载地址
            trialRecordListModel.lxwjxzdz = recordFileDownloadModel
            List<CaseRecordInfoModel> caseRecordInfoModelList = new ArrayList<>()
            //庭次列表
            List<TrialInfo> trialInfoList = TrialInfo.findAllByPlanInfo(planInfo)
            log.info("[HuaXiaService.pushTrialRecord] ${planInfo.caseInfo.name}的此次排期的庭次数量:${trialInfoList.size()}")
            //查询所有的videoInfo
            List<VideoInfo> videoInfoList = new ArrayList<>()
            for (int j = 0; j < trialInfoList.size(); j++) {
                List<VideoInfo> videoInfos = VideoInfo.findAllByTrialInfo(trialInfoList.get(j))
                videoInfoList.addAll(videoInfos)
            }
            if (videoInfoList.size() == 0) {
                log.info("[HuaXiaService.pushTrialRecord] ${planInfo.caseInfo.name}的此次排期没有录像.")
                continue
            }
            log.info("[HuaXiaService.pushTrialRecord] ${planInfo.caseInfo.name}的此次排期视频数量:${videoInfoList.size()}")
            //通道号_通道名称
            List<String> num_name_model = new ArrayList<>()
            for (int j = 0; j < videoInfoList.size(); j++) {
                VideoInfo videoInfo = videoInfoList.get(j)
                num_name_model.add(videoInfo.channelNum + "_" + videoInfo.channelName)
            }
            Set set = new HashSet()
            List<String> num_name = new ArrayList<>()
            set.addAll(num_name_model)
            num_name.addAll(set)
            log.info("[HuaXiaService.pushTrialRecord] ${planInfo.caseInfo.name}的此次排期的通道集合:${num_name}")
            //根据通道号和通道名称查询视频(每一个通道遍历一次)
            for (int j = 0; j < num_name.size(); j++) {
                String num_name_str = num_name.get(j)
                String[] num_name_arr = num_name_str.split("_")
                log.info("${num_name_arr[0]}_${num_name_arr[1]}")
                CaseRecordInfoModel caseRecordInfoModel = new CaseRecordInfoModel()
                caseRecordInfoModel.xhmc = num_name_arr[1]  //信号名称
                caseRecordInfoModel.xh = num_name_arr[0]  //信号
                caseRecordInfoModel.sfzl = "1"  //是否直连
                caseRecordInfoModel.jlz = "http://${ip}:8200"  //基路径 + "/" + wjm
                caseRecordInfoModel.dycs = null  //调用参数(直连可以为空)
                List<RecordFileInfoModel> recordFileInfoModelList = new ArrayList<>()
                for (int k = videoInfoList.size() - 1; k >= 0; k--) {
                    VideoInfo videoInfo = videoInfoList.get(k)
                    log.info("[HuaXiaService.pushTrialRecord] k=${k},解析第${k + 1}个视频:videoInfo.channelNum=${videoInfo.channelNum},videoInfo.channelName=${videoInfo.channelName}")
                    if ((videoInfo.channelNum + "_" + videoInfo.channelName) != num_name_str) {
                        log.info("[HuaXiaService.pushTrialRecord] 第${k + 1}个视频没有匹配通道，跳过.")
                        continue
                    }
                    RecordFileInfoModel recordFileInfoModel = new RecordFileInfoModel()
                    recordFileInfoModel.wjm = videoInfo.fileName
                    recordFileInfoModel.xzwjm = videoInfo.fileName.substring(videoInfo.fileName.lastIndexOf("/") + 1)
                    recordFileInfoModel.wjsc = videoInfo.length
                    recordFileInfoModel.lzkssj = videoInfo.startRecTime
                    recordFileInfoModel.lzjssj = videoInfo.endRecTime
                    recordFileInfoModelList.add(recordFileInfoModel)
                    videoInfoList.remove(k)
                }
                caseRecordInfoModel.lxwjxx = recordFileInfoModelList
                caseRecordInfoModelList.add(caseRecordInfoModel)
            }
            //设置案件录像信息
            trialRecordListModel.ajlxxx = caseRecordInfoModelList
            String convertToXml = XMLUtils.convertToXml(trialRecordListModel)
            //发送消息
            log.info("[HuaXiaService.pushTrialRecord] 第${i + 1}个闭庭录像信息信息:${convertToXml}")
            //处理响应
            handleResponse(rcsDataInputPortType.trialRecord(convertToXml), "pushRecord")
        }
        log.info("[HuaXiaService.pushTrialRecord] 今日推送闭庭录像信息数量:${planInfoList.size()},推送结束.")
    }

    /**
     * 初始化华夏接口代理
     */
    void initService() {
        JaxWsProxyFactoryBean clientFactoryBean = new JaxWsProxyFactoryBean()
        clientFactoryBean.setServiceClass(RcsDataInputPortType.class)
        clientFactoryBean.setAddress(grailsApplication.config.getProperty("syncData.huaxia.url"))
        rcsDataInputPortType = (RcsDataInputPortType) clientFactoryBean.create()
    }

    /**
     * 补充排期参数
     * @param it 需要补充的排期
     */
    void planDataSupply(PlanInfo it) {
        boolean flag = false
        Employee judge = it.judge
        Employee cbr = it.undertake
        Employee secretary = it.secretary
        if (!judge) {
            flag = true
            log.error("[HuaXiaService.pushPlan:${it.id}] 对应法官不存在.")
            //存在承办人
            if (cbr && cbr.dept) {
                it.judge = cbr
            } else {
                it.judge = Employee.findAllByDeptIsNotNull().get(0) // 默认第一个部门不为空的人员作为法官
            }
        }
        if (!secretary) {
            flag = true
            log.error("[HuaXiaService.pushPlan:${it.id}] 对应书记员不存在.")
            //存在承办人
            if (cbr && cbr.dept) {
                it.secretary = cbr
            } else {
                it.secretary = Employee.findAllByDeptIsNotNull().get(0) // 默认第一个部门不为空的人员作为法官
            }
        }
        if (flag) {
            it.save(flush: true)
            if (it.hasErrors()) {
                log.error("[HuaXiaService.pushPlan:${it.id}] 修改法官或书记员后排期保存失败,失败信息:\n${it.errors}")
                throw new RuntimeException("id=${it.id}的排期修改法官或书记员后保存失败")
            }
        }
    }

    /**
     * 拼装排期参数
     * @param it 需要推送的排期
     */
    String planParams(PlanInfo it) {
        TrialPublishListModel trialPublishListModel = new TrialPublishListModel()
        List<TrialPublishModel> trialPublishModelList = new ArrayList<>()
        TrialPublishModel trialPublishModel = new TrialPublishModel()
        def beforePlanSize = PlanInfo.findAllByStartDateLessThanAndCaseInfoAndActive(it.startDate, it.caseInfo, DataStatus.SHOW).size()
        trialPublishModel.tc = (beforePlanSize + 1) as String  //庭次
        CaseInfo caseInfo = it.caseInfo  //案件
        trialPublishModel.ajbs = caseInfo.uid  //案件标识
        trialPublishModel.spzhmc = ""  //审判字号名称
        CaseType caseType = caseInfo.type
        if (!caseType) {
            trialPublishModel.ajlb = CaseTypeEnum.QT.code as String  //案件类型
        } else {
            if (caseType.name.contains(CaseTypeEnum.XS.desc)) {
                trialPublishModel.ajlb = CaseTypeEnum.XS.code as String  //案件类型
            } else if (caseType.name.contains(CaseTypeEnum.MS.desc)) {
                trialPublishModel.ajlb = CaseTypeEnum.MS.code as String  //案件类型
            } else {
                trialPublishModel.ajlb = CaseTypeEnum.QT.code as String  //案件类型
            }
        }
        trialPublishModel.ah = caseInfo.archives  //案号
        trialPublishModel.ay = caseInfo.name  //案由----设置为案件名称
        trialPublishModel.sycx = CaseProgress.NORMAL.code as String  //适用程序
        trialPublishModel.dsr = "被告:${caseInfo.accuser != null ? caseInfo.accuser : ""};原告:${caseInfo.accused != null ? caseInfo.accused : ""}"
        //当事人

        Employee judge = it.judge
        trialPublishModel.cbrbs = judge.uid  //承办人标识
        trialPublishModel.cbr = judge.name  //承办人姓名

        Department dept = judge.dept
        trialPublishModel.cbbmbs = dept.uid  //承办部门标识
        trialPublishModel.cbbm = dept.name  //承办部门名称

        trialPublishModel.jyaq = caseInfo.summary  //简要案情
        trialPublishModel.larq = caseInfo.filingDate != null ? caseInfo.filingDate : new Date()  //立案日期
        trialPublishModel.kssj = it.startDate != null ? it.startDate : new Date()  //开始时间
        trialPublishModel.jssj = it.endDate != null ? it.endDate : new Date()  //结束时间

        Courtroom courtroom = it.courtroom
        if (!courtroom) {
            log.error("[HuaXiaService.pushPlan:${it.id}] 对应法庭不存在.")
            return "courtroom"
        }
        trialPublishModel.ftbs = courtroom.uid  //法庭标识
        trialPublishModel.ktdd = courtroom.name //开庭地点----开庭所在法庭

        trialPublishModel.spzbs = judge.uid  //审判长标识
        trialPublishModel.spz = judge.name  //审判长姓名

        Employee secretary = it.secretary
        trialPublishModel.sjybs = secretary.uid  //书记员标识
        trialPublishModel.sjy = secretary.name  //书记员

        trialPublishModel.fydm = SystemController.currentCourt.val  //法院代码
        trialPublishModel.szftcs = "110"  //厂商
        trialPublishModel.ktlx = OpenTrialType.LOCAL.code as String  //开庭类型,写死为本地开庭
        trialPublishModel.ftyt = CourtRoomUseType.TRIAL.code as String  //法庭用途,写死为开庭审理
        trialPublishModel.sfzb = "1"  //是否直播(允许直播)
        trialPublishModel.gkkt = "1"  //是否公开开庭(1:是 2:否 255:未指定)
        trialPublishModel.sfsskl = "255"  //是否实时刻录(未指定)
        trialPublishModel.ycktfs = "255"  //远程开庭方式(未指定)
        trialPublishModelList.add(trialPublishModel)
        trialPublishListModel.list = trialPublishModelList
        return XMLUtils.convertToXml(trialPublishListModel)
    }

    /**
     * 华夏响应值处理
     * @param response 响应值
     * @param methodName 方法名
     */
    void handleResponse(String response, String methodName) {
        log.info("[HuaXiaService.${methodName}] 推送信息结果:${response}")
        String xmlCode = XMLUtils.getXMLCode(response)
        String xmlDesc = XMLUtils.getXMLDesc(response)
        if ("1" != xmlCode) {
            log.error("[HuaXiaService.${methodName}] 响应值不正确推送失败:[xmlCode=${xmlCode},xmlDesc=${xmlDesc}]")
        }
        log.info("[HuaXiaService.${methodName}] 推送成功.")
    }
}