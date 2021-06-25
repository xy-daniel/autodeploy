package com.hxht.autodeploy.court.admin

import cn.hutool.core.date.DateUtil
import com.hxht.techcrt.DataStatus
import com.hxht.techcrt.Dict
import com.hxht.techcrt.PlanStatus
import com.hxht.techcrt.PositionStatus
import com.hxht.techcrt.Role
import com.hxht.techcrt.User
import com.hxht.techcrt.UserRole
import com.hxht.techcrt.court.*
import com.hxht.techcrt.court.manager.SystemController
import com.hxht.techcrt.utils.CaseTypeNewUtil
import com.hxht.techcrt.utils.CourtJurisdictionsAndCodesUtil
import com.hxht.techcrt.utils.PinYinUtils
import com.hxht.techcrt.utils.UUIDGenerator
import com.hxht.techcrt.utils.http.HttpUtil
import grails.converters.JSON
import grails.core.GrailsApplication
import grails.gorm.transactions.Transactional
import grails.plugin.springwebsocket.WebSocket
import groovy.sql.Sql
import org.hibernate.SessionFactory
import ws.schild.jave.MultimediaObject

import java.text.SimpleDateFormat

class ToolBoxService implements WebSocket {
    SessionFactory sessionFactory
    GrailsApplication grailsApplication
    def dataImportCaseInfo(Sql sql) {
        convertAndSend "/topic/data_import", "CaseInfo data import."
        def s = "select count(*) as count from `case`"
        def count = sql.firstRow(s).count as int
        def length = 100
        def recordsTotal = (int) Math.ceil(count / length)
        for (def i = 0; i < recordsTotal; i++) {
            dataImportCaseInfo(i,length,sql,count)
        }
        convertAndSend "/topic/data_import", "Insert CaseInfo finished."
    }
    def dataImportPlanInfo(Sql sql){
        convertAndSend "/topic/data_import", "PlanInfo data import."
        def s = "select count(*) as count from plan"
        def count = sql.firstRow(s).count as int
        def length = 100
        def recordsTotal = (int) Math.ceil(count / length)
        for (def i = 0; i < recordsTotal; i++) {
            dataImportPlanInfo(i,length,sql,count)
        }
        convertAndSend "/topic/data_import", "Insert PlanInfo finished."
    }
    def dataImportTrialInfo(Sql sql){
        convertAndSend "/topic/data_import", "TrialInfo data import."
        def s = "select count(*) as count from trial"
        def count = sql.firstRow(s).count as int
        def length = 100
        def recordsTotal = (int) Math.ceil(count / length)
        for (def i = 0; i < recordsTotal; i++) {
            dataImportTrialInfo(i,length,sql,count)
        }
        convertAndSend "/topic/data_import", "Insert TrialInfo finished."
    }
    def dataImportVideoInfo(Sql sql){
        convertAndSend "/topic/data_import", "VideoInfo data import."
        def s = "select count(*) as count from recordinfo"
        def count = sql.firstRow(s).count as int
        def length = 100
        def recordsTotal = (int) Math.ceil(count / length)
        for (def i = 0; i < recordsTotal; i++) {
            dataImportVideoInfo(i,length,sql,count)
        }
        convertAndSend "/topic/data_import", "Insert VideoInfo finished."
    }

    /**
     * 统计视频时长
     */
    @Transactional
    def dataVideoDuration(String videoPath){
        convertAndSend "/topic/data_video_duration", "Data video duration begins."
        try {
            def videoInfoList = VideoInfo.findAllByLengthIsNullOrLength(0)
            for(def videoInfo : videoInfoList){
                def source = new File("${videoPath}${videoInfo.fileName}")//原视频文件
                try {//尝试分析视频文件，通过视频文件获取视频时长
                    def multObj = new MultimediaObject(source)
                    def duration = multObj.getInfo().getDuration()
                    videoInfo.length = (duration / 1000).intValue()
                    videoInfo.save()
                    convertAndSend "/topic/data_video_duration", "Get video duration succes. Video id:[${videoInfo.id}] duration:[${videoInfo.length}]".toString()
                } catch (e) {
                    e.printStackTrace()
                    convertAndSend "/topic/data_video_duration", "ERROR! Get duration error. Video id:[${videoInfo.id}]".toString()
                }
            }

        }catch(e){
            e.printStackTrace()
            convertAndSend "/topic/data_video_duration", "Data video duration begins."
        } finally {
            convertAndSend "/topic/data_video_duration", "Finished."
        }
    }

    /**
     * 将所有开完庭的旧数据导入到点播平台系统
     */
    @Transactional
    def showVideoPlatform(){
        convertAndSend "/topic/showVideoPlatform", "推送点播平台系统--开始！."
        try {
            def showVideoPlatUrl = Dict.findByCode("ShowVideoPlatform")?.val //对接点播平台的地址
            if (!showVideoPlatUrl){
                convertAndSend "/topic/showVideoPlatform", "dict表中未查到对接点播平台地址并返回"
                return 
            }
            List list = new ArrayList()
            list.add(PlanStatus.ADJOURN)
            list.add(PlanStatus.CLOSED)
            list.add(PlanStatus.ARCHIVED)
            def countPlanInfo = PlanInfo.countByActiveAndStatusInList(DataStatus.SHOW, list)
            log.info("查询出休庭、闭庭、归档的排期共---${countPlanInfo}---个")
            convertAndSend "/topic/showVideoPlatform", "查询出休庭、闭庭、归档的排期共---${countPlanInfo}---个".toString()
            //一百条排期传输
            def begin = (int) Math.ceil(countPlanInfo / 100)
            for (def n=0; n< begin; n++) {
                def planInfoList = PlanInfo.findAllByActiveAndStatusInList(DataStatus.SHOW, list, [max:100, offset:n*100])
                def arrayData = []
                for (def planInfo : planInfoList){
                    def trialInfoList = planInfo.trialInfo
                    def caseInfo = planInfo.caseInfo
                    def modelCaseInfo = [:]
                    def modelData = [:]
                    //案件信息
                    modelCaseInfo.put("uid", caseInfo.uid)//案件uid
                    modelCaseInfo.put("court", SystemController.currentCourt.ext2)//案件所属法院编号  如 粤03
                    modelCaseInfo.put("archives", caseInfo.archives)//案号
                    modelCaseInfo.put("name", caseInfo.name)//案件名称
                    modelCaseInfo.put("caseTypeName", caseInfo.type?.name)//案件类型名称
                    modelCaseInfo.put("summary", caseInfo.summary)//案件概要
                    modelCaseInfo.put("detail", caseInfo.detail)//案件详情
                    modelCaseInfo.put("accuser", caseInfo.accuser)//原告
                    modelCaseInfo.put("prosecutionCounsel", caseInfo.prosecutionCounsel)//原告律师
                    modelCaseInfo.put("accused", caseInfo.accused)//被告
                    modelCaseInfo.put("counselDefence", caseInfo.counselDefence)//被告律师
                    modelCaseInfo.put("department", caseInfo.department)//案件所属部门
                    modelCaseInfo.put("manufacturer", "hexing")//案件所属厂商
                    modelCaseInfo.put("filingDate", DateUtil.format(caseInfo.filingDate as Date, "yyyy-MM-dd HH:mm:ss"))//立案日期
                    modelData.put("caseInfo", modelCaseInfo)

                    //排期信息
                    def modelPlanInfo = [:]
                    modelPlanInfo.put("uid", planInfo.uid)//排期uid
                    modelPlanInfo.put("courtroom", planInfo.courtroom?.name)//排期法庭
                    modelPlanInfo.put("judge", planInfo.judge?.name)//排期法官
                    modelPlanInfo.put("secretary", planInfo.secretary?.name)//排期书记员
                    modelPlanInfo.put("undertake", planInfo.undertake?.name)//当事人
                    modelPlanInfo.put("startDate", DateUtil.format(planInfo.startDate as Date, "yyyy-MM-dd HH:mm:ss"))//开始时间
                    modelPlanInfo.put("endDate", DateUtil.format(planInfo.endDate as Date, "yyyy-MM-dd HH:mm:ss"))//结束时间
                    modelPlanInfo.put("status", planInfo.status)//排期状态
                    modelPlanInfo.put("active", planInfo.active)//排期状态为隐藏


                    //庭审信息
                    def arrayTrialInfo = []
                    for (def trialInfo: trialInfoList){
                       
                        def modelTrialInfo = [:]
                        modelTrialInfo.put("uid", trialInfo.uid)//庭审uid
                        modelTrialInfo.put("startDate", DateUtil.format(trialInfo.startDate as Date, "yyyy-MM-dd HH:mm:ss"))//庭审开始时间
                        modelTrialInfo.put("endDate", DateUtil.format(trialInfo.endDate as Date, "yyyy-MM-dd HH:mm:ss"))//庭审闭庭时间
                        modelTrialInfo.put("status", trialInfo.status)//庭审状态

                        //视频信息
                        def arrayVideoInfo = []
                        def videoInfoList = trialInfo.videoInfo
                        for (def video: videoInfoList){
                            def modelVideoInfo = [:]
                            modelVideoInfo.put("uid",video.uid) // 视频uid
                            modelVideoInfo.put("channelNum",video.channelNum) // 视频通道号
                            modelVideoInfo.put("channelName",video.channelName) // 视频画面名称
                            modelVideoInfo.put("mediaType",video.mediaType? video.mediaType: "mp4") // 视频类型为mp4
                            modelVideoInfo.put("resolution",video.resolution) // 视频分辨率
                            modelVideoInfo.put("mediaStreamSize",video.mediaStreamSize) // 视频码率
                            modelVideoInfo.put("startRecTime",DateUtil.format(video.startRecTime as Date, "yyyy-MM-dd HH:mm:ss")) // 视频开始截取时间
                            modelVideoInfo.put("endRecTime",DateUtil.format(video.endRecTime as Date, "yyyy-MM-dd HH:mm:ss")) // 视频结束截取时间
                            modelVideoInfo.put("fileName","http://"+ trialInfo.courtroom.storeIp +":8200/"+video.fileName) // 视频播放路径
                            modelVideoInfo.put("size",video.size) // 视频大小
                            modelVideoInfo.put("length",video.length) // 视频时长
                            arrayVideoInfo.add(modelVideoInfo)
                        }
                        modelTrialInfo.put("videoInfoList",arrayVideoInfo)
                        arrayTrialInfo.add(modelTrialInfo)
                    }
                    modelPlanInfo.put("trialInfoList", arrayTrialInfo)
                    modelData.put("planInfo", modelPlanInfo)
                    arrayData.add(modelData)
                }
                //将数据发送
                def postUrl = showVideoPlatUrl + "/api/savePlan"
                def result = HttpUtil.postToJson(postUrl, arrayData)
                if (result.code == 1){
                    log.info("对接点播总平台系统---执行失败！ 错误信息：${result.data}")
                    convertAndSend "/topic/showVideoPlatform", "对接点播总平台系统---执行失败！第---${n*100}---个开始 共---100---个 错误信息：${result.data} ".toString()
                }
                convertAndSend "/topic/showVideoPlatform", "推送点播平台成功！第---${n*100}---个开始 共---100---个".toString()
            }

        }catch(e){
            e.printStackTrace()
            convertAndSend "/topic/showVideoPlatform", "推送点播平台失败！"
        } finally {
            convertAndSend "/topic/showVideoPlatform", "Finished."
        }
    }


    /**
     * 导入字典数据
     * @param sql
     */
    @Transactional
    def dataImportDict(Sql sql) {
        convertAndSend "/topic/data_import", "Dictionary data import."
        //获取当前法院信息
        def s = "select * from court"
        def theCourt = sql.firstRow(s)
        def courtCode = theCourt.courtid
//        def uid = theCourt.uid
        def ip = theCourt.centerip
        def confCourtDict = Dict.findByCode("CURRENT_COURT")
        for (String str : CourtJurisdictionsAndCodesUtil.list) {
            def strArr = str.split(",")
            if (strArr[2] == "${theCourt.courtid}") {//找到当前法院
                confCourtDict.val = strArr[0]//写入代码
                confCourtDict.ext1 = strArr[3]//标准化名称 法院名称
                confCourtDict.ext2 = strArr[4] //法院名称 简称
            }
        }
        confCourtDict.ext3 = courtCode //分级码
        confCourtDict.ext4 = ip //ip
        confCourtDict.save(flush: true)
        convertAndSend "/topic/data_import", "current court [${confCourtDict.ext1}${confCourtDict.val}]. Insert dictionary finished.".toString()
        sessionFactory.currentSession.clear()
    }
    /**
     * 导入案件类型
     * @return
     */
    @Transactional
    def dataImportCaseType() {
        convertAndSend "/topic/data_import", "CaseType data import."
        def 爸爸 = null
        for (def str : CaseTypeNewUtil.types) {
            def typeStrArr = str.split(",")
            def caseTypec = new CaseType()
            caseTypec.code = typeStrArr[0]
            caseTypec.name = typeStrArr[1]
            def code = caseTypec.code
            if (code.substring(2, 4) != "00") {//人中之龙 不是爸爸是爸爸的爸爸
                caseTypec.parent = 爸爸
            }
            if (typeStrArr.size() == 2) {//反正等于2不是爸爸就是爸爸的爸爸
                爸爸 = caseTypec
            } else {//孙子
                caseTypec.shortName = typeStrArr[2]
            }
            caseTypec.save(flush: true)
            if (!caseTypec.hasErrors()) {
                convertAndSend "/topic/data_import", caseTypec.name
            } else {
                convertAndSend "/topic/data_import", "ERROR! Insert CaseType [${caseTypec.name}] ERROR [${caseTypec.errors}].".toString()
                throw new RuntimeException()
            }
        }
        convertAndSend "/topic/data_import", "Insert CaseType finished."
        sessionFactory.currentSession.clear()
    }
    /**
     * 导入法庭
     */
    @Transactional
    def dataImportCourtroom(Sql sql) {
        convertAndSend "/topic/data_import", "Courtroom data import."
        def s = "select * from courtroom"
        def targetCourtRoomList = sql.rows(s)
        Courtroom courtRoom
        for (int i = 0; i < targetCourtRoomList.size; i++) {
            def targetCourtRoom = targetCourtRoomList.get(i)
            def uid = targetCourtRoom.uid as String
            courtRoom = Courtroom.findByUid(uid)
            if(courtRoom){
                continue
            }
            courtRoom = Courtroom.findByName(targetCourtRoom.courtroomname as String)
            if (courtRoom){
                continue
            }
            courtRoom = new Courtroom()
            courtRoom.uid = uid
            courtRoom.name = targetCourtRoom.courtroomname
            courtRoom.deviceIp = targetCourtRoom.devip
//            courtRoom.deviceStatus = DeviceStatus.NORMAL
            courtRoom.liveIp = targetCourtRoom.storeip
            //福建高级人民法院用下面这一行代码
            if (Dict.findByCode("CURRENT_COURT").ext3.startsWith("D")){
                courtRoom.livePort = 554
            }else{
                if(targetCourtRoom.port != null && targetCourtRoom.port != ""){
                    courtRoom.livePort = targetCourtRoom.port
                }else{
                    courtRoom.livePort = 554
                }
            }
            courtRoom.storeIp = targetCourtRoom.storeip
//            courtRoom.ftpUser = targetCourtRoom.ftpuser
//            courtRoom.ftpPsw = targetCourtRoom.ftppassword
//            courtRoom.ftpPort = targetCourtRoom.ftpport
//            courtRoom.deviceType = targetCourtRoom.dvstype
//            courtRoom.mode = targetCourtRoom.mode
//            courtRoom.remote = targetCourtRoom.remotestatus == false ? 0 : 1
//            courtRoom.storeSwitch = targetCourtRoom.storeswitch == false ? 0 : 1
//            courtRoom.storeFashion = targetCourtRoom.storefashion == false ? 0 : 1
            if (targetCourtRoom.flag == 1) {//导入初始数据的时候状态设置成一样的就可以了
                courtRoom.status = DataStatus.SHOW //使用
                courtRoom.active = DataStatus.SHOW //使用
            } else if (targetCourtRoom.flag == 0) {
                courtRoom.status = DataStatus.HIDE //未使用
                courtRoom.active = DataStatus.HIDE //未使用
            } else {
                courtRoom.status = DataStatus.DEL //删除
                courtRoom.active = DataStatus.DEL //删除
            }
            //解析cfg
            try {
                def cfg = new XmlParser().parseText("${targetCourtRoom.cfg}")
                def cfgModel = [
                        encode     : [],//法庭编码器
                        decode     : [],//法庭解码器
                        videoMatrix: [],//VIDEO矩阵
                        vgaMatrix  : [],//VGA矩阵
                        outMatrix  : [],//输出控制
                        soundMatrix: [],//音量控制
                        total      : [],//综合控制
                        power      : [],//强电控制
                        irctrl     : [],//红外控制
                        camera     : [//摄像头控制
                                      buttons : [],//摄像头按钮组
                                      presets : [],//摄像头预置位
                                      position: [] //摄像头位置
                        ]
                ]
                //法庭编码器
                for (def item : cfg.EncodeManage.encode) {
                    def isrecord = "1"
                    if ("false" == item.attribute("isrecord") || "0" == item.attribute("isrecord")){
                        isrecord = "0"
                    }
                    cfgModel.encode.add([
                            uuid  : UUIDGenerator.nextUUID(),//UUID
                            name  : item.attribute("chnname"),//名称
                            encodeip : item.attribute("encodeip"),//IP地址
                            number: item.attribute("encodeno"),//通道编号
                            record: isrecord//是否可以录制
                    ])
                }
                //法庭解码器
                for (def item : cfg.DecodeManage.decode) {
                    cfgModel.decode.add([
                            uuid  : UUIDGenerator.nextUUID(),//UUID
                            name  : item.attribute("chnname"),//名称
                            decodeip    : item.attribute("decodeip"),//IP地址
                            number: item.attribute("decodeno"),//解码器编号
                    ])
                }
                //VIDEO矩阵
                for (def item : cfg.vedioMtrx.radio) {
                    def visible = "1"
                    if ("false" == item.attribute("visible")){
                        visible = "0"
                    }
                    cfgModel.videoMatrix.add([
                            uuid    : UUIDGenerator.nextUUID(),//UUID
                            name    : item.attribute("name"),//名称
                            codeDown: item.attribute("code_down"),//控制指令
                            visible : visible//可见状态
                    ])
                }
                //VGA矩阵
                for (def item : cfg.vgaMtrx.radio) {
                    def visible = "1"
                    if ("false" == item.attribute("visible")){
                        visible = "0"
                    }
                    cfgModel.vgaMatrix.add([
                            uuid    : UUIDGenerator.nextUUID(),//UUID
                            name    : item.attribute("name"),//名称
                            codeDown: item.attribute("code_down"),//控制指令
                            visible : visible//可见状态
                    ])
                }
                //输出控制
                for (def item : cfg.outctrl.tab) {
                    def visible = "1"
                    if ("false" == item.attribute("visible")){
                        visible = "0"
                    }
                    cfgModel.outMatrix.add([
                            uuid    : UUIDGenerator.nextUUID(),//UUID
                            name    : item.attribute("name"),//名称
                            codeDown: item.attribute("code_down"),//控制指令
                            visible : visible//可见状态
                    ])
                }
                //音量控制
                for (def item : cfg.volumn.button) {
                    def visible = "1"
                    if ("false" == item.attribute("visible")){
                        visible = "0"
                    }
                    cfgModel.soundMatrix.add([
                            uuid    : UUIDGenerator.nextUUID(),//UUID
                            name    : item.attribute("name"),//名称
                            codeDown: item.attribute("code_down"),//控制指令
                            group   : item.attribute("group"),//分组
                            visible : visible//可见状态
                    ])
                }
                //综合控制
                for (def item : cfg.colligate.cmd) {
                    def visible = "1"
                    if ("false" == item.attribute("visible")){
                        visible = "0"
                    }
                    cfgModel.total.add([
                            uuid        : UUIDGenerator.nextUUID(),//UUID
                            name        : item.attribute("name"),//名称
                            codeDown    : item.attribute("code_down"),//控制指令
                            sendStatus  : item.attribute("sendStatus"),//发送时间
                            sendPriority: item.attribute("sendPriority"),//发送优先级
                            visible     : visible//可见状态
                    ])
                }
                //强电控制
                for (def item : cfg.devicectrl.tab) {
                    def visible = "1"
                    if ("false" == item.attribute("visible")){
                        visible = "0"
                    }
                    def tab = [
                            uuid   : UUIDGenerator.nextUUID(),//UUID
                            name   : item.attribute("name"),//名称
                            visible: visible,//可见状态
                            buttons: []//按钮
                    ] as HashMap

                    for (def btn : item.button) {
                        tab.buttons.add([
                                uuid    : UUIDGenerator.nextUUID(),//UUID
                                name    : btn.attribute("name"),//名称
                                codeDown: btn.attribute("code_down"),//控制指令
                        ])
                    }
                    cfgModel.power.add(tab)
                }

                //红外控制
                for (def item : cfg.irctrl.tab) {
                    def visible = "1"
                    if ("false" == item.attribute("visible")){
                        visible = "0"
                    }
                    def tab = [
                            uuid   : UUIDGenerator.nextUUID(),//UUID
                            name   : item.attribute("name"),//名称
                            visible: visible,//可见状态
                            buttons: []//按钮
                    ] as HashMap

                    for (def btn : item.button) {
                        tab.buttons.add([
                                uuid    : UUIDGenerator.nextUUID(),//UUID
                                name    : btn.attribute("name"),//名称
                                codeDown: btn.attribute("code_down"),//控制指令
                        ])
                    }
                    cfgModel.irctrl.add(tab)
                }
                //摄像机控制
                for (def item : cfg.camera.buttons.button) {//摄像头按钮组
                    def visible = "1"
                    if ("false" == item.attribute("visible")){
                        visible = "0"
                    }
                    cfgModel.camera.buttons.add([
                            uuid    : UUIDGenerator.nextUUID(),//UUID
                            name    : item.attribute("name"),//名称
                            codeDown: item.attribute("code_down"),//控制指令 按钮按下
                            codeUp  : item.attribute("code_up"),//控制指令 按钮抬起
                            visible : visible//可见状态
                    ])
                }
                for (def item : cfg.camera.presets.preset) {//摄像头预置位
                    cfgModel.camera.presets.add([
                            uuid: UUIDGenerator.nextUUID(),//UUID
                            name: item.attribute("name"),//名称
                            save: item.attribute("save"),//保存的预置位
                            call: item.attribute("call")//请求保存预置位
                    ])
                }
                for (def item : cfg.camera.tabs.tab) {//摄像头位置
                    def visible = "1"
                    if ("false" == item.attribute("visible")){
                        visible = "0"
                    }
                    cfgModel.camera.position.add([
                            uuid    : UUIDGenerator.nextUUID(),//UUID
                            name    : item.attribute("name"),//名称
                            codeDown: item.attribute("code_down"),//控制指令
                            visible : visible//可见状态

                    ])
                }
                courtRoom.cfg = cfgModel as JSON
            } catch (e) {
                brokerMessagingTemplate.convertAndSend "/topic/data/sysn", e.message
            }

            courtRoom.save(flush: true)
            if (!courtRoom.hasErrors()) {
                convertAndSend "/topic/data_import", courtRoom.name
            } else {
                convertAndSend "/topic/data_import", "ERROR! Insert CourtRoom [${courtRoom.name}] ERROR [${courtRoom.errors}].".toString()
                continue
            }

            sessionFactory.currentSession.clear()
        }
        convertAndSend "/topic/data_import", "Insert CourtRoom finished."
    }

    /**
     * 导入法庭配置信息
     * @param sql
     */
    @Transactional
    def dataImportCFG(Sql sql) {
        convertAndSend "/topic/data_import", "CFG data import."
        convertAndSend "/topic/data_import", "Insert CFG finished."
    }

    /**
     * 导入部门
     * @param sql
     * @return
     */
    @Transactional
    def dataImportDept(Sql sql) {
        convertAndSend "/topic/data_import", "Department data import."
        def s = "select * from dept"
        def targetDeptList = sql.rows(s)
        Department department
        for (int i = 0; i < targetDeptList.size; i++) {
            def targetDepartment = targetDeptList.get(i)
            def uid = targetDepartment.uid as String
            department = Department.findByUid(uid)
            if (!department) {
                department = Department.findByName(targetDepartment.deptname)
                if (!department){
                    department = new Department()
                    department.uid = uid
                }
            }
            def pid = targetDepartment.pid as String
            def departmentParent = Department.findByUid(pid)
            if (departmentParent) {
                department.parent = departmentParent
            }
            department.name = targetDepartment.deptname
//            department.syncId = targetDepartment.interface_id
            //判断dcs数据库中
            def interface_id = "select * from information_schema.columns t where table_schema = 'dcs' and table_name = 'dept' and column_name = 'interface_id'"
            def dataid = sql.rows(interface_id)
            if (dataid){
                department.synchronizationId = targetDepartment.interface_id
            }
            def interface_pid = "select * from information_schema.columns t where table_schema = 'dcs' and table_name = 'dept' and column_name = 'interface_pid'"
            def datapid = sql.rows(interface_pid)
            if (datapid){
                department.synchronizationId = targetDepartment.interface_pid
            }

            department.save(flush: true)
            if (!department.hasErrors()) {
                convertAndSend "/topic/data_import", department.name
            } else {
                convertAndSend "/topic/data_import", "ERROR! Insert Department [${department.name}] ERROR [${department.errors}].".toString()
            }
            sessionFactory.currentSession.clear()
        }
        convertAndSend "/topic/data_import", "Insert Department finished."
    }
    /**
     * 导入用户
     * @param sql
     */
    @Transactional
    def dataImportEmployee(Sql sql) {
        convertAndSend "/topic/data_import", "Employee data import."
        def s = "select count(*) as count from users"
        def count = sql.firstRow(s).count as int
        def length = 100
        def recordsTotal = (int) Math.ceil(count / length)
        for (def i = 0; i < recordsTotal; i++) {
            s = "select users.*,role.rolename from users left join role on users.role_id = role.uid limit ${i * length}, ${length}"
            def dataList = sql.rows(s)
            def size = dataList.size()
            Employee employee
            for (int j = 0; j < size; j++) {
                def targetUser = dataList.get(j)
                def uid = targetUser.uid as String
                employee = Employee.findByUid(uid)
                if (!employee) {
                    employee = Employee.findBySynchronizationId(uid)
                    if (!employee){
                        employee = new Employee()
                        employee.uid = uid
                    }
                }
                def deptUid = targetUser.dept_id as String
                def dept = Department.findByUid(deptUid)
                if (dept) {
                    employee.dept = dept
                }
                employee.name = targetUser.username
                employee.position = PositionStatus.getCode(targetUser.rolename as String)
                //判断dcs数据库中
                def interface_id = "select * from information_schema.columns t where table_schema = 'dcs' and table_name = 'users' and column_name = 'interface_id'"
                def data = sql.rows(interface_id)
                if (data){
                    employee.synchronizationId = targetUser.interface_id
                }
                employee.save(flush: true)
                if (!employee.hasErrors()) {
                    convertAndSend "/topic/data_import", "${employee.name} [${i * length + j + 1}/${count}]".toString()
                    importUserForDcs(employee,targetUser.userid,targetUser.password)//保存用户
                } else {
                    convertAndSend "/topic/data_import", "ERROR! Insert Employee [${employee.name}] ERROR [${employee.errors}][${i * length + j + 1}/${count}].".toString()
                }
            }
            sessionFactory.currentSession.clear()
        }
        convertAndSend "/topic/data_import", "Insert Employee finished."
    }

    @Transactional
    def dataImportCaseInfo(def i, def length,def sql,def count) {
        def s = "select * from `case` limit ${i * length}, ${length}"
        def dataList = sql.rows(s)
        def size = dataList.size()
        def caseInfo
        for (int j = 0; j < size; j++) {
            def targetCase = dataList.get(j)
            def uid = targetCase.uid as String
            def interfaceId = targetCase.interface_id as String
            caseInfo = CaseInfo.findByUid(uid)
            if (caseInfo){
                continue
            }
            if (interfaceId){
                caseInfo = CaseInfo.findBySynchronizationId(interfaceId)
                if (caseInfo){
                    continue
                }
            }
            def caseno = CaseInfo.findByArchives(targetCase.caseno)
            if (caseno){
                continue
            }
            caseInfo = new CaseInfo()
            caseInfo.uid = uid
            caseInfo.archives = targetCase.caseno
            caseInfo.name = targetCase.casename
            //通过案号来判断是什么类型的案件

            def caseType = CaseType.findByCode(CaseTypeNewUtil.getCodeByArchives((targetCase.caseno as String) ?: "0100"))
            caseInfo.type = caseType
            def deptUid = targetCase.dept_id as String
            def dept = Department.findByUid(deptUid)
            if (dept) {
                caseInfo.department = dept
            }
//                caseInfo.department = targetCase.summary
            caseInfo.summary = targetCase.summary
            caseInfo.detail = targetCase.casedesc
            caseInfo.accuser = targetCase.accuse
            caseInfo.prosecutionCounsel = targetCase.accuse_lawer
            caseInfo.accused = targetCase.accused
            caseInfo.counselDefence = targetCase.accused_lawer
            caseInfo.filingDate = targetCase.casedate
            if (targetCase.flag == 1) {
                caseInfo.active = DataStatus.SHOW //使用
            } else {
                caseInfo.active = DataStatus.DEL //删除
            }
            caseInfo.syncId = targetCase.interface_id
            caseInfo.synchronizationId = targetCase.interface_id //判断对接导入的
            caseInfo.save()
            if (!caseInfo.hasErrors()) {
                convertAndSend "/topic/data_import", "${caseInfo.archives} [${i * length + j + 1}/${count}]".toString()
            } else {
                convertAndSend "/topic/data_import", "ERROR! Insert CaseInfo [${caseInfo.archives}] ERROR [${caseInfo.errors}][${i * length + j + 1}/${count}].".toString()
            }
        }
        sessionFactory.currentSession.clear()
    }

    @Transactional
    def dataImportPlanInfo(def i, def length,def sql,def count) {
        def s = "select * from plan limit ${i * length}, ${length}"
        def dataList = sql.rows(s)
        def size = dataList.size()
        def planInfo
        for (int j = 0; j < size; j++) {
            def targetPlan = dataList.get(j)
            def uid = targetPlan.uid as String
            def interfaceId = targetPlan.interfaceplan_id as String
            planInfo = PlanInfo.findByUid(uid)
            if (planInfo && planInfo.status != PlanStatus.PLAN){
               continue
            }
            if (!planInfo) {
                if (interfaceId){
                    planInfo = PlanInfo.findBySynchronizationId(interfaceId)
                    if (planInfo && planInfo.status != PlanStatus.PLAN){
                        continue
                    }
                }
                planInfo = new PlanInfo()
                planInfo.uid = uid
            }
            planInfo.courtroom = Courtroom.findByUid(targetPlan.courtroom_id as String)
            def judge = Employee.findByUid(targetPlan.judge_id as String)
            if (!judge) {//判空
                judge = Employee.findByNameOrName("admin", "管理员")
            }
            planInfo.judge = judge
            planInfo.undertake = judge //初始导入的时候承办人就是法官
            //将主审法官添加到合议庭成员中
            def collegial = new Collegial()
            collegial.name = judge.name
            collegial.type = 1
            collegial.save(flush: true)
            planInfo.collegial = [collegial]
            def secretary = Employee.findByUid(targetPlan.secretary_id as String)
            if (!secretary) {//判空
                secretary = Employee.findByNameOrName("admin", "管理员")
            }
            planInfo.secretary = secretary
            planInfo.startDate = targetPlan.start_date == null?new Date():targetPlan.start_date
            planInfo.endDate = targetPlan.end_date == null?new Date():targetPlan.end_date
            planInfo.status = targetPlan.status
            planInfo.allowPlay = targetPlan.allowplay
            planInfo.active = DataStatus.SHOW //因为数据问题，处拾导入时候全部显示
            planInfo.syncId = targetPlan.interfaceplan_id
            planInfo.synchronizationId = targetPlan.interfaceplan_id
            def caseId = targetPlan.case_id
            if (caseId){
                //通过uid查询能查到caseInfo否则通过案号进行查找
                def caseInfo = CaseInfo.findByUid(caseId as String)
                if (!caseInfo){
                    caseInfo = CaseInfo.findBySynchronizationId(caseId as String)
                    if (!caseInfo){
                        def archive = "select caseno from `case` where uid = '${caseId}'"
                        def data = sql.rows(archive)
                        if (data){
                            caseInfo = CaseInfo.findByArchives(data.caseno as String)
                        }
                    }
                }
                planInfo.caseInfo = caseInfo
            }else{
                convertAndSend "/topic/data_import", "ERROR! 排期未找到对应案件！[${i * length + j + 1}/${count}].".toString()
                continue
            }
            planInfo.save()
            if (!planInfo.hasErrors()) {
                convertAndSend "/topic/data_import", "${planInfo.uid} [${i * length + j + 1}/${count}]".toString()
            } else {
                convertAndSend "/topic/data_import", "ERROR! Insert PlanInfo [${planInfo.uid}] ERROR [${planInfo.errors}][${i * length + j + 1}/${count}].".toString()
            }
        }
        sessionFactory.currentSession.clear()
    }

    @Transactional
    def dataImportTrialInfo(def i, def length,def sql,def count) {
        def s = "select * from trial limit ${i * length}, ${length}"
        def dataList = sql.rows(s)
        def size = dataList.size()
        def trialInfo
        for (int j = 0; j < size; j++) {
            def targetTrial = dataList.get(j)
            def uid = targetTrial.uid as String
            trialInfo = TrialInfo.findByUid(uid)
            if (trialInfo && trialInfo.status != PlanStatus.PLAN){
               continue
            }
            if (!trialInfo) {
                trialInfo = new TrialInfo()
                trialInfo.uid = uid
            }
            trialInfo.courtroom = Courtroom.findByUid(targetTrial.courtroom_id as String)
            def judge = Employee.findByUid(targetTrial.judge_id as String)
            if (!judge) {//判空
                judge = Employee.findByNameOrName("admin", "管理员")
            }
            trialInfo.judge = judge
//                trialInfo.undertake = judge //初始导入的时候承办人就是法官
            //将主审法官添加到合议庭成员中
//                def collegial = new Collegial()
//                collegial.name = judge.name
//                collegial.type = 1
//                collegial.save(flush: true)
//                trialInfo.collegial = [collegial]
            def secretary = Employee.findByUid(targetTrial.secretary_id as String)
            if (!secretary) {//判空
                secretary = Employee.findByNameOrName("admin", "管理员")
            }
            trialInfo.secretary = secretary
            trialInfo.startDate = targetTrial.start_date == null?new Date():targetTrial.start_date
            trialInfo.endDate = targetTrial.end_date == null?new Date():targetTrial.end_date
            trialInfo.status = targetTrial.judge_process
            trialInfo.note = targetTrial.courtrec
            trialInfo.active = DataStatus.SHOW
            trialInfo.planInfo = PlanInfo.findByUid(targetTrial.plan_Id as String)
            trialInfo.save()
            if (!trialInfo.hasErrors()) {
                convertAndSend "/topic/data_import", "${trialInfo.uid} [${i * length + j + 1}/${count}]".toString()
            } else {
                convertAndSend "/topic/data_import", "ERROR! Insert TrialInfo [${trialInfo.uid}] ERROR [${trialInfo.errors}][${i * length + j + 1}/${count}].".toString()
            }
        }
        sessionFactory.currentSession.clear()
    }

    @Transactional
    def dataImportVideoInfo(def i, def length,def sql,def count) {
        def s = "select * from recordinfo limit ${i * length}, ${length}"
        def dataList = sql.rows(s)
        def size = dataList.size()
        def videoInfo
        for (int j = 0; j < size; j++) {
            def targetVideo = dataList.get(j)
            def uid = targetVideo.uid as String
            videoInfo = VideoInfo.findByUid(uid)
            if (!videoInfo) {
                videoInfo = new VideoInfo()
                videoInfo.uid = uid
            }
            videoInfo.channelNum = targetVideo.chn
            videoInfo.channelName = targetVideo.channelsname
            videoInfo.mediaType = targetVideo.mediatype
            videoInfo.resolution = targetVideo.resolution
//                videoInfo.mediaStreamSize =  //原码率字段并未存放码率 而是存的时长，这里暂时先不导入这个数据。
            //历史数据中不存在视频开时间，通过文件名计算
            try {
                def nameArr = "${targetVideo.filename}".split("_")
                def sdf = new SimpleDateFormat("yyyyMMddHHmmss")
                videoInfo.startRecTime = sdf.parse(nameArr[nameArr.size() - 3] + nameArr[nameArr.size() - 2])
                if (targetVideo.mediastreamsize) {//如果存在视频时长数据
                    videoInfo.length = Integer.parseInt("${targetVideo.mediastreamsize}".trim())
//存得是字符而且有空格，这里处理一下转换成int类型
                    videoInfo.endRecTime = new Date(videoInfo.startRecTime.getTime() + (videoInfo.length * 1000))
                }
            } catch (ignored) {
            }
            videoInfo.size = targetVideo.filesize
            videoInfo.fileName = targetVideo.filename
            videoInfo.active = DataStatus.SHOW
            videoInfo.trialInfo = TrialInfo.findByUid(targetVideo.plan_id as String)
            videoInfo.save(flush:true)
            if (!videoInfo.hasErrors()) {
                convertAndSend "/topic/data_import", "${videoInfo.uid} [${i * length + j + 1}/${count}]".toString()
            } else {
                convertAndSend "/topic/data_import", "ERROR! Insert VideoInfo [${videoInfo.uid}] ERROR [${videoInfo.errors}][${i * length + j + 1}/${count}].".toString()
            }
        }
        sessionFactory.currentSession.clear()
    }
    @Transactional
    def importUserForDcs(Employee employee,String userid,String password) {
        if (User.findByUsername(userid)) {
            return
        }
        def user = new User()
        user.uid = UUIDGenerator.nextUUID()
        user.enabled = true //账号启用
        user.accountExpired = false //账号过期
        user.accountLocked = false//账号锁定
        user.username = userid
        user.password = password//密码
        user.realName = employee.name//姓名
        user.employee = employee.id
        user.save()
        if (!user.hasErrors()) {
            convertAndSend "/topic/data_import", "${user.uid}".toString()
        } else {
            convertAndSend "/topic/data_import", "ERROR! Insert VideoInfo [${user.uid}] ERROR [${user.errors}].".toString()
        }
    }

    @Transactional
    def importUser(Employee employee){
        def username = PinYinUtils.getHanziPinYin(employee.name)
        if (username?.contains("lu:")){
            username = username.replace("lu:","lv")
        }
        if (User.findByUsername(username)){
            return
        }
        def user = new User()
        user.uid = UUIDGenerator.nextUUID()
        user.enabled = true //账号启用
        user.accountExpired = false //账号过期
        user.accountLocked = false//账号锁定
        user.username = username
        user.password = '123456'//密码
        user.realName = employee.name//姓名
        user.employee = employee.id
        user.save()
        if (!user.hasErrors()) {
            convertAndSend "/topic/data_import", "${user.uid}".toString()
        } else {
            convertAndSend "/topic/data_import", "ERROR! Insert VideoInfo [${user.uid}] ERROR [${user.errors}].".toString()
        }
        /*def ROLE_TODAYTRIAL = Role.findByAuthority('ROLE_TODAYTRIAL')
        def ROLE_TRIALLIST = Role.findByAuthority('ROLE_TRIALLIST')
        def ROLE_ANALYZE = Role.findByAuthority('ROLE_ANALYZE')
        def ROLE_CONFIG = Role.findByAuthority('ROLE_CONFIG')
        def userRoletl = new UserRole()
        userRoletl.user = user
        userRoletl.role = ROLE_TODAYTRIAL
        userRoletl.save()
        if (!user.hasErrors()) {
            convertAndSend "/topic/data_import", "${userRoletl.user}".toString()
        } else {
            convertAndSend "/topic/data_import", "ERROR! Insert VideoInfo [${userRoletl.user}] ERROR [${userRoletl.errors}].".toString()
        }
        def userRolett = new UserRole()
        userRolett.user = user
        userRolett.role = ROLE_TRIALLIST
        userRolett.save()
        if (!user.hasErrors()) {
            convertAndSend "/topic/data_import", "${userRolett.user}".toString()
        } else {
            convertAndSend "/topic/data_import", "ERROR! Insert VideoInfo [${userRolett.user}] ERROR [${userRolett.errors}].".toString()
        }
        def userRoleae = new UserRole()
        userRoleae.user = user
        userRoleae.role = ROLE_ANALYZE
        userRoleae.save()
        if (!user.hasErrors()) {
            convertAndSend "/topic/data_import", "${userRoleae.user}".toString()
        } else {
            convertAndSend "/topic/data_import", "ERROR! Insert VideoInfo [${userRoleae.user}] ERROR [${userRoleae.errors}].".toString()
        }
        def userRolerg = new UserRole()
        userRolerg.user = user
        userRolerg.role = ROLE_CONFIG
        userRolerg.save()
        if (!user.hasErrors()) {
            convertAndSend "/topic/data_import", "${userRolerg.user}".toString()
        } else {
            convertAndSend "/topic/data_import", "ERROR! Insert VideoInfo [${userRolerg.user}] ERROR [${user.errors}].".toString()
        }*/
    }

    @Transactional
    def userRole() {
        def userList = User.findAll()
        for (def user:userList){
            def userRole = UserRole.findByUser(user)
            //给未被分配权限的人员分配权限
            if (!userRole){
                if (user.employee){
                    def employee = Employee.get(user.employee)
                    if (employee.position == 2){
                        userRole = new UserRole()
                        userRole.user = user
                        userRole.role = Role.findByAuthority('ROLE_SHUJIYUAN')
                        userRole.save(flush:true)
                        if (userRole.hasErrors()){
                            log.error("给未被分配权限的人员 分配权限保存用户权限表时报错！")
                        }
                    }else if (employee.position == 6){
                        userRole = new UserRole()
                        userRole.user = user
                        userRole.role = Role.findByAuthority('ROLE_FAGUAN')
                        userRole.save(flush:true)
                        if (userRole.hasErrors()){
                            log.error("给未被分配权限的人员 分配权限保存用户权限表时报错！")
                        }
                    }else {
                        userRole = new UserRole()
                        userRole.user = user
                        userRole.role = Role.findByAuthority('ROLE_PUTONGYONGHU')
                        userRole.save(flush:true)
                        if (userRole.hasErrors()){
                            log.error("给未被分配权限的人员 分配权限保存用户权限表时报错！")
                        }
                    }
                }
            }
        }
    }

    def deleteTestCase() {
        def caseList = CaseInfo.findAllByArchivesLike('%测试%')
        for (def caseInfo : caseList){
            def planList = PlanInfo.findAllByCaseInfo(caseInfo)
            for (def plan: planList){
                def trialList  = TrialInfo.findAllByPlanInfo(plan)
                for (def trial : trialList){
                    def videoList = VideoInfo.findAllByTrialInfo(trial)
                    for (def video :videoList){
                        video.delete(flush: true)
                    }
                    trial.delete(flush: true)
                }
                plan.delete(flush: true)
            }
            caseInfo.delete(flush: true)
        }
    }

    def usershezhi(){
        def  userList = User.findAllByIdBetween(4,691)
        for (def user: userList){
            def userRole = new UserRole()
            userRole.user = user
            userRole.role = Role.get(3)
            userRole.save(flush:true)
        }
    }
}
