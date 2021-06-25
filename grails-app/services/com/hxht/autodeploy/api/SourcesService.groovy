package com.hxht.autodeploy.api

import com.hxht.techcrt.DataStatus
import com.hxht.techcrt.court.*
import com.hxht.techcrt.enums.RespType
import com.hxht.techcrt.utils.CaseTypeNewUtil
import com.hxht.techcrt.utils.UUIDGenerator
import grails.converters.JSON
import grails.gorm.transactions.Transactional
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject

import java.text.SimpleDateFormat

/**
 * 临时数据接口service
 */
@Transactional
class SourcesService {

    /**
     * 保存部门信息
     * @param department 旧数据
     * @return 0
     */
    def saveDepartment(JSONArray department) {
        for (def i = department.size() - 1; i >= 0; i--) {
            def deptMap = department[i] as JSONObject
            def uid = deptMap["uid"]
            def deptName = deptMap["deptname"]
            def pid = deptMap["pid"]
            def dept = Department.findByUid(uid as String)
            if (dept) {
                dept.name = deptName
                if (!pid) {
                    dept.parent = null
                    dept.save(flush: true)
                    department.remove(i)
                } else {
                    def deptParentVo = Department.findByUid(pid as String)
                    if (deptParentVo) {
                        dept.parent = deptParentVo
                        dept.save(flush: true)
                        department.remove(i)
                    }
                }
            } else {
                if (deptMap["pid"] == null) {
                    new Department(
                            version: 0,
                            name: deptMap["deptname"],
                            uid: deptMap["uid"],
                            syncId: null,
                            parent: null
                    ).save(flush: true)
                    department.remove(i)
                } else {
                    def deptParent = Department.findByUid(pid as String)
                    if (deptParent) {
                        new Department(
                                version: 0,
                                name: deptMap["deptname"],
                                uid: deptMap["uid"],
                                syncId: null,
                                parent: deptParent
                        ).save(flush: true)
                        department.remove(i)
                    }
                }
            }
        }
        if (department.size() == 0) {
            department.size()
        } else {
            saveDepartment(department)
        }
    }

    def getCode(String status) {
        switch (status) {
            case "法官":
                return 2
            case "书记员":
                return 6
            case "司法警察":
                return 7
            default:
                return 255
        }
    }
    /**
     * 保存员工信息
     */
    def saveEmployee(JSONArray employee) {
        for (def i = employee.size() - 1; i >= 0; i--) {
            def employeeObj = employee[i] as JSONObject
            def uid = employeeObj["uid"]
            def name = employeeObj["name"]
            def p = employeeObj["position"]
            def position = getCode(p as String)
            def d = employeeObj["deptuid"]
            def dept = Department.findByUid(d as String)
            def employeePo = Employee.findByUid(uid as String)
            if (employeePo) {
                employeePo.name = name
                employeePo.position = position
                employeePo.dept = dept
                employeePo.save(flush: true)
                employee.remove(i)
            } else {
                new Employee(
                        version: 0,
                        uid: uid,
                        position: position,
                        dept: dept,
                        name: name
                ).save(flush: true)
                employee.remove(i)
            }
        }
        employee.size()
    }
    /**
     * 保存法庭信息
     */
    def saveCourtroom(JSONArray courtroom) {

        for (def i = courtroom.size() - 1; i >= 0; i--) {
            def courtroomObj = courtroom[i] as JSONObject
            def uid = courtroomObj["uid"]
            def courtroomName = courtroomObj["courtroomname"]
            def devIp = courtroomObj["devip"]
            def storeIp = courtroomObj["storeip"]
            def cfgVo = courtroomObj["cfg"]
            def flag = courtroomObj["flag"]
            def port = courtroomObj["port"]
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
            if (cfgVo) {
                def cfg = new XmlParser().parseText(cfgVo as String)
                //法庭编码器
                for (def item : cfg.EncodeManage.encode) {
                    cfgModel.encode.add([
                            uuid  : UUIDGenerator.nextUUID(),//UUID
                            name  : item.attribute("chnname"),//名称
                            ip    : item.attribute("encodeip"),//IP地址
                            number: item.attribute("encodeno"),//通道编号
                            record: item.attribute("isrecord")//是否可以录制
                    ])
                }
                //法庭解码器
                for (def item : cfg.DecodeManage.decode) {
                    cfgModel.decode.add([
                            uuid  : UUIDGenerator.nextUUID(),//UUID
                            name  : item.attribute("chnname"),//名称
                            ip    : item.attribute("decodeip"),//IP地址
                            number: item.attribute("decodeno"),//解码器编号
                    ])
                }
                //VIDEO矩阵
                for (def item : cfg.vedioMtrx.radio) {
                    cfgModel.videoMatrix.add([
                            uuid    : UUIDGenerator.nextUUID(),//UUID
                            name    : item.attribute("name"),//名称
                            codeDown: item.attribute("code_down"),//控制指令
                            visible : item.attribute("visible")//可见状态
                    ])
                }
                //VGA矩阵
                for (def item : cfg.vgaMtrx.radio) {
                    cfgModel.vgaMatrix.add([
                            uuid    : UUIDGenerator.nextUUID(),//UUID
                            name    : item.attribute("name"),//名称
                            codeDown: item.attribute("code_down"),//控制指令
                            visible : item.attribute("visible")//可见状态
                    ])
                }
                //输出控制
                for (def item : cfg.outctrl.tab) {
                    cfgModel.outMatrix.add([
                            uuid    : UUIDGenerator.nextUUID(),//UUID
                            name    : item.attribute("name"),//名称
                            codeDown: item.attribute("code_down"),//控制指令
                            visible : item.attribute("visible")//可见状态
                    ])
                }
                //音量控制
                for (def item : cfg.volumn.button) {
                    cfgModel.soundMatrix.add([
                            uuid    : UUIDGenerator.nextUUID(),//UUID
                            name    : item.attribute("name"),//名称
                            codeDown: item.attribute("code_down"),//控制指令
                            group   : item.attribute("group"),//分组
                            visible : item.attribute("visible")//可见状态
                    ])
                }
                //综合控制
                for (def item : cfg.colligate.cmd) {
                    cfgModel.total.add([
                            uuid        : UUIDGenerator.nextUUID(),//UUID
                            name        : item.attribute("name"),//名称
                            codeDown    : item.attribute("code_down"),//控制指令
                            sendStatus  : item.attribute("sendStatus"),//发送时间
                            sendPriority: item.attribute("sendPriority"),//发送优先级
                            visible     : item.attribute("visible")//可见状态
                    ])
                }
                //强电控制
                for (def item : cfg.devicectrl.tab) {
                    def tab = [
                            uuid   : UUIDGenerator.nextUUID(),//UUID
                            name   : item.attribute("name"),//名称
                            visible: item.attribute("visible"),//可见状态
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
                    def tab = [
                            uuid   : UUIDGenerator.nextUUID(),//UUID
                            name   : item.attribute("name"),//名称
                            visible: item.attribute("visible"),//可见状态
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
                    cfgModel.camera.buttons.add([
                            uuid    : UUIDGenerator.nextUUID(),//UUID
                            name    : item.attribute("name"),//名称
                            codeDown: item.attribute("code_down"),//控制指令 按钮按下
                            codeUp  : item.attribute("code_up"),//控制指令 按钮抬起
                            visible : item.attribute("visible")//可见状态
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
                    cfgModel.camera.position.add([
                            uuid    : UUIDGenerator.nextUUID(),//UUID
                            name    : item.attribute("name"),//名称
                            codeDown: item.attribute("code_down"),//控制指令
                            visible : item.attribute("visible")//可见状态
                    ])
                }
            }
            //状态配置
            def status
            def active
            if (flag == 1) {
                status = 1 //使用
                active = 1 //使用
            } else if (flag == 0) {
                status = 6 //未使用
                active = 6 //未使用
            } else {
                status = 4 //删除
                active = 4 //删除
            }
            def courtroomPo = Courtroom.findByUid(uid as String)
            if (courtroomPo) {
                courtroomPo.name = courtroomName
                courtroomPo.deviceIp = devIp
                courtroomPo.devicePort = port
                courtroomPo.storeIp = storeIp
                courtroomPo.cfg = cfgModel as JSON
                courtroomPo.status = status
                courtroomPo.active = active
                courtroomPo.lastUpdated = new Date()
                courtroomPo.save(flush: true)
            } else {
                def courtroomAdd = new Courtroom()
                courtroomAdd.lastUpdated = new Date()
                courtroomAdd.uid = uid
                courtroomAdd.name = courtroomName
                courtroomAdd.deviceIp = devIp
                courtroomAdd.devicePort = port
                courtroomAdd.storeIp = storeIp
                courtroomAdd.cfg = cfgModel as JSON
                courtroomAdd.status = status
                courtroomAdd.active = active
                courtroomAdd.dateCreated = new Date()
                courtroomAdd.save(flush: true)
            }
        }
        RespType.SUCCESS
    }

    /**
     * 案件更新或添加
     * @param caseInfo 案件信息
     * @return 成功与否
     */
    def saveCase(def caseData) {
        def uid = caseData.uid as String
        def caseInfo = CaseInfo.findByUid(uid)
        if (!caseInfo) {
            caseInfo = new CaseInfo()
            caseInfo.uid = uid
        }
        caseInfo.archives = caseData.caseno
        caseInfo.name = caseData.casename
        caseInfo.summary = caseData.summary
        caseInfo.detail = caseData.casedesc
        caseInfo.accuser = caseData.accuse
        caseInfo.prosecutionCounsel = caseData.accuselawer
        caseInfo.accused = caseData.accused
        caseInfo.counselDefence = caseData.accusedlawer
        def date = caseData.casedate
        def caseDate
        if (date){
            caseDate = new Date(date as Long)
        }else{
            caseDate = null
        }
        caseInfo.filingDate = caseDate
        if (caseData.flag == 1) {
            caseInfo.active = DataStatus.SHOW //使用
        } else {
            caseInfo.active = DataStatus.DEL //删除
        }
        caseInfo.syncId = caseData.interfaceid
        def caseType = CaseType.findByCode(CaseTypeNewUtil.getCodeByArchives((caseData.casetype as String) ?: "0100"))
        caseInfo.type = caseType
        caseInfo.save(flush: true)
        if (caseInfo.hasErrors()) {
            def msg = "[SourcesService saveCase]保存案件失败 errors:[${caseInfo.errors}]]"
            log.error(msg)
            throw new RuntimeException(msg)
        }
        caseInfo
    }

    /**
     * 处理排期
     * @param json 包含排期信息的JOSN
     * @return 返回值
     */
    def savePlan(def planData, CaseInfo caseInfo) {
        def uid = planData.uid as String
        def planInfo = PlanInfo.findByUid(uid)
        if (!planInfo) {
            planInfo = new PlanInfo()
            planInfo.uid = uid
        }
        planInfo.caseInfo = caseInfo
        planInfo.courtroom = Courtroom.findByUid(planData.courtroomid as String)

        def dateStart = planData.startdate
        def startDate
        if (dateStart){
            startDate = new Date(dateStart as Long)
        }else{
            startDate = null
        }
        planInfo.startDate = startDate

        def dateEnd = planData.enddate
        def endDate
        if (dateEnd){
            endDate = new Date(dateEnd as Long)
        }else{
            endDate = null
        }
        planInfo.endDate = endDate
        def judge = Employee.findByUid(planData.judge as String)
        if (!judge) {//判空
            judge = Employee.findByNameOrName("admin", "管理员")
        }
        planInfo.judge = judge
        def secretary = Employee.findByUid(planData.secretary as String)
        if (!secretary) {//判空
            secretary = Employee.findByNameOrName("admin", "管理员")
        }
        planInfo.secretary = secretary
        planInfo.allowPlay = planData.allowplay
        planInfo.status = planData.status
        planInfo.undertake = judge
        planInfo.syncId = planData.interfaceplanid
        planInfo.active = DataStatus.SHOW
        planInfo.save(flush: true)
        if (planInfo.hasErrors()) {
            def msg = "[SourcesService savePlan]保存排期失败 errors:[${planInfo.errors}]]"
            log.error(msg)
            throw new RuntimeException(msg)
        }
        planInfo
    }

    /**
     * 处理庭审  方法到达这儿一定存在数据
     * @param trialList 庭审信息列表
     */
    def saveTrial(def trialData, PlanInfo planInfo) {
        def uid = trialData.uid as String
        def trialInfo = TrialInfo.findByUid(uid)
        if (!trialInfo) {
            trialInfo = new TrialInfo()
            trialInfo.uid = uid
            trialInfo.dateCreated = new Date()
        }
        trialInfo.planInfo = planInfo
        trialInfo.courtroom = Courtroom.findByUid(trialData.courtroomid as String)

        def dateStart = trialData.startdate
        def startDate
        if (dateStart){
            startDate = new Date(dateStart as Long)
        }else{
            startDate = new Date()
        }
        trialInfo.startDate = startDate

        def dateEnd = trialData.enddate
        def endDate
        if (dateEnd){
            endDate = new Date(dateEnd as Long)
        }else{
            endDate = null
        }
        trialInfo.endDate = endDate

        def judge = Employee.findByUid(trialData.judge as String)
        if (!judge) {//判空
            judge = Employee.findByNameOrName("admin", "管理员")
        }
        trialInfo.judge = judge
        def secretary = Employee.findByUid(trialData.secretary as String)
        if (!secretary) {//判空
            secretary = Employee.findByNameOrName("admin", "管理员")
        }
        trialInfo.secretary = secretary
        trialInfo.status = DataStatus.SHOW
        trialInfo.note = trialData.courtrec
        trialInfo.active = DataStatus.SHOW
        trialInfo.lastUpdated = new Date()
        trialInfo.save(flush: true)
        if (trialInfo.hasErrors()) {
            def msg = "[SourcesService saveTrial]保存庭审失败 errors:[${trialInfo.errors}]]"
            log.error(msg)
            throw new RuntimeException(msg)
        }
        trialInfo
    }

    def saveVideo(def videoData, TrialInfo trialInfo) {
        def uid = videoData.uid as String
        def videoInfo = VideoInfo.findByUid(uid)
        if (!videoInfo) {
            videoInfo = new VideoInfo()
            videoInfo.uid = uid
            videoInfo.dateCreated = new Date()
        }
        videoInfo.lastUpdated = new Date()
        videoInfo.trialInfo = trialInfo
        videoInfo.size = videoData.filesize
        videoInfo.fileName = videoData.filename
        videoInfo.resolution = videoData.resolution
        videoInfo.channelNum = videoData.chn
        videoInfo.channelName = videoData.channelsname
        videoInfo.mediaType = videoData.mediatype
        try {
            def nameArr = "${videoInfo.filename}".split("_")
            def sdf = new SimpleDateFormat("yyyyMMddHHmmss")
            videoInfo.startRecTime = sdf.parse(nameArr[nameArr.size() - 3] + nameArr[nameArr.size() - 2])
            if (videoData.mediastreamsize) {//如果存在视频时长数据
                videoInfo.length = Integer.parseInt("${videoData.mediastreamsize}".trim())
                //存得是字符而且有空格，这里处理一下转换成int类型
                videoInfo.endRecTime = new Date(videoInfo.startRecTime.getTime() + (videoInfo.length * 1000))
            }
        } catch (ignored) {
        }
        videoInfo.active = DataStatus.SHOW
        videoInfo.save(flush: true)
        if (videoInfo.hasErrors()){
            def msg = "[SourcesService saveTrial]保存视频失败 errors:[${videoInfo.errors}]]"
            log.error(msg)
            throw new RuntimeException(msg)
        }
        videoInfo
    }

    def handlePlan(def json) {
        def caseInfo = saveCase(json.case)
        def planInfo = savePlan(json, caseInfo)
        def trialList = json.triallist
        for (def trialData : trialList) {
            def trialInfo = saveTrial(trialData, planInfo)
            def videoList = json.recordinfolist
            for (def videoData : videoList) {
                saveVideo(videoData, trialInfo)
            }
        }
    }

}