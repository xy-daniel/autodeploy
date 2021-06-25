package com.hxht.autodeploy.api

import cn.hutool.core.date.DateUtil
import com.hxht.techcrt.DataStatus
import com.hxht.techcrt.Participants
import com.hxht.techcrt.PlanStatus
import com.hxht.techcrt.PositionStatus
import com.hxht.techcrt.court.*
import com.hxht.techcrt.utils.UUIDGenerator
import grails.gorm.transactions.Transactional
import org.grails.web.json.JSONObject

@Transactional
class ZhonghengService{

//    ApiService apiService
//    WebServiceNormalService webServiceNormalService

    /**
     * @return 中恒排期信息
     */
    def planScheduleInfo(Integer status,String uid,Courtroom courtroom,Date startDate,Date endDate) {
        def planList = PlanInfo.createCriteria().list {
            and {
                eq("active", DataStatus.SHOW)
                if (uid){
                    eq("uid", uid)
                }
                if (courtroom) {
                    eq("courtroom", courtroom)//法庭
                }
                if (status || status == 0) {
                    eq("status", status)//状态
                }
                if (startDate) {
                    ge("startDate", startDate)
                }
                if (endDate) {
                    le("endDate", endDate)
                }
            }
        } as List<PlanInfo>
        planList
    }

//    /**
//     * 保存案件——排期
//     * @param caseInfo  案件信息CaseInfo
//     * @param planInfo  排期信息PlanInfo
//     */
//    def savePlan(CaseInfo caseInfo, PlanInfo planInfo){
//        caseInfo.save(flush: true)
//        if (caseInfo.hasErrors()){
//            log.info("[ZhonghengService.savePlan] 保存案件时出错，错误信息：\n${caseInfo.errors}")
//            throw new RuntimeException("保存案件时错误")
//        }
//        planInfo.caseInfo = caseInfo
//        planInfo.save(flush: true)
//        if (planInfo.hasErrors()){
//            log.info("[ZhonghengService.savePlan] 保存排期时出错，错误信息：\n${planInfo.errors}")
//            throw new RuntimeException("保存排期时错误")
//        }
//    }

//    /**
//     * 根据同步主键删除排期
//     * @param syncId  同步主键
//     * @return 删除失败返回同步主键值，成功返回字符串true
//     */
//    def delPlanBySyncId(String syncId){
//        def planInfo = PlanInfo.findByManufacturerAndSynchronizationId("zhongheng",syncId)
//        if (!planInfo){
//            log.info("[ZhonghengService.delPlanBySyncId] 根据syncId:${syncId},没有查找到对应的排期记录，删除失败")
//            return syncId
//        }
//        planInfo.active = DataStatus.DEL
//        planInfo.save(flush: true)
//        if (planInfo.hasErrors()){
//            log.info("[ZhonghengService.delPlanBySyncId] 删除syncId:${syncId}时出现错误，删除失败，错误信息:\n${planInfo.errors}")
//            return syncId
//        }
//        return "true"
//    }

//    /**
//     * 中恒信开庭
//     * @param caseInfo
//     * @param courtroom
//     * @return
//     */
//    def openTrial(CaseInfo caseInfo, Courtroom courtroom){
//        caseInfo.save(flush: true)
//        if (caseInfo.hasErrors()) {
//            //这儿需要修改
//            log.error("[ZhonghengService.openTrial] 中恒信离线开庭时保存案件失败.错误信息：\n${caseInfo.errors}")
//            throw new RuntimeException()
//        }
//        def planInfo = new PlanInfo()
//        def uid = UUIDGenerator.nextUUID()
//        planInfo.uid = uid
//        planInfo.caseInfo = caseInfo
//        planInfo.status = PlanStatus.PLAN
//        planInfo.active = DataStatus.SHOW
//        planInfo.startDate = new Date()
//        planInfo.endDate = new Date()
//        planInfo.manufacturer = "zhongheng"
//        planInfo.synchronizationId = uid
//        planInfo.save(flush: true)
//        if (planInfo.hasErrors()) {
//            log.error("[ZhonghengService.openTrial] 中恒信离线开庭时保存排期失败.错误信息：\n${planInfo.errors}")
//            throw new RuntimeException()
//        }
//        def trialInfo = apiService.planOpen(planInfo, courtroom, planInfo.judge, planInfo.secretary, null)
//        //向CMP平台推送数据
//        List<CaseInfo> caseInfos = new ArrayList<>()
//        caseInfos.add(caseInfo)
//        webServiceNormalService.pushCase(caseInfos)
//        List<PlanInfo> planInfos = new ArrayList<>()
//        planInfos.add(planInfo)
//        webServiceNormalService.pushPlan(planInfos)
//        trialInfo
//    }

    /**
     * 遍历所有的上传排期执行添加操作
     * @param data  上传排期列表
     * @return 全部处理成功返回true,否则抛出异常
     */
    def handlePlanData(List<JSONObject> data){
        for (JSONObject plan:data) {
            def singlePlanResult = handleSinglePlan(plan)
            if (!singlePlanResult) {
                log.error("[ZhonghengService.handlePlanData] 单条排期处理失败，全部回滚")
                throw new RuntimeException("单条排期处理失败")
            }
        }
        return true
    }
    /**
     * 处理单个历史排期
     * @param data 单个历史排期数据
     * @return boolean类型的是否返回成功
     */
    def handleSinglePlan(JSONObject data){
        def CASE   = data.CASE //案件信息
        //案件信息不存在返回false
        if (CASE == null || CASE == ""){
            return false
        }
        //处理案件
        CASE = CASE as JSONObject
        def caseInfo = getCaseInfo(CASE)
        if (!caseInfo){
            log.error("[ZhonghengService.uploadHistoryPlan] 解析案件信息失败，此排期结束")
            return false
        }
        //处理参与人
        if (CASE.CYR){
            def cyrResult = handleCYR(CASE.CYR as List<JSONObject>, caseInfo)
            if (!cyrResult){
                log.error("[ZhonghengService.uploadHistoryPlan] 参与人信息处理失败，此排期结束")
                return false
            }
        }
        def planInfo = getPlanInfoNotHaveCase(data)
        if (!planInfo) {
            log.error("[ZhonghengService.uploadHistoryPlan] 解析排期信息失败，此排期结束")
            return false
        }
        planInfo.caseInfo = caseInfo
        planInfo.save(flush: true)
        if (planInfo.hasErrors()) {
            log.error("[ZhonghengService.uploadHistoryPlan] 添加或更新排期时出现错误，错误信息为：\n${planInfo.errors}")
            return false
        }
        //到此为止排期添加完毕，开始处理TrialInfo
        def trialResult = handleTrial(data.TRIAL, planInfo)
        if (!trialResult) {
            //无论是庭次信息处理失败还是视频信息处理失败都会返回false，抛出异常全部回滚
            log.error("[ZhonghengService.uploadHistoryPlan] 添加或更新庭次信息失败，错误信息为：\n${planInfo.errors}")
            return false
        }
        //处理结束返回true
        return true
    }

    /**
     * * 处理参与人信息
     * @param CYR 参与人信息
     * @param caseInfo 对应案件信息
     * @return boolean类型的是否添加成功
     */
    def handleCYR(List<JSONObject> CYR, CaseInfo caseInfo){
        for (JSONObject cyr: CYR){
            def XM   = cyr.XM
            def LX = cyr.LX
            def SJH  = cyr.SJH
            def SFZH = cyr.SFZH
            if (XM == null || XM == "" || SFZH == null || SFZH == "" || LX == null || LX == ""){
                log.info("[ZhonghengService.handleCYR] 参与人信息为${cyr}的参与人信息不完整舍弃该排期")
                return false
            }
            //通过身份证号查询此参与人
            def participant = Participants.findByIdCardNumber(SFZH as String)
            if (!participant){
                participant = new Participants(
                        idCardNumber: SFZH as String
                )
            }
            participant.name = XM as String
            participant.iphoneNumber = SJH as String
            participant.type = LX as Integer
            participant.about = caseInfo
            participant.save(flush: true)
            if (participant.hasErrors()){
                log.error("[ZhonghengService.handleCYR] 添加或修改参与人信息时出现异常")
                return false
            }
        }
        return true
    }

    /**
     * 从JSON中解析出CaseInfo
     * @param caseData  JSON格式的案件数据
     * @return CaseInfo/null
     */
    def getCaseInfo(JSONObject caseData){
        def UID = caseData.UID //uid必填项
        def AH  = caseData.AH  //案号必填项
        if (!(UID && AH)){
            log.info("[ZhonghengService.getCaseInfo] 案件参数校验未通过,跳过本条数据")
            return null
        }
        UID = UID as String //uid必填项
        AH  = AH  as String //案号必填项
        def NAME = caseData.NAME //案件名称
        def TYPE = caseData.TYPE //案件类型标识
        def LARQ = caseData.LARQ //立案日期  xxxx-xx-xx xx:xx:xx
        def DEPT = caseData.DEPT //部门标识
        def AY   = caseData.AY   //案件概要或案由
        def AJMX = caseData.AJMX //案件明细
        def department = null
        if (DEPT != null && DEPT != ""){
            department = Department.findByManufacturerAndSynchronizationId("zhongheng", DEPT as String)
        }
        //根据同步主键查询案件
        def caseInfo = CaseInfo.findByManufacturerAndSynchronizationId("zhongheng", UID)
        //案件存在
        if (caseInfo && caseInfo.archives != AH){
            //案件存在并且案号不相同，下面根据案号查询案件
            def caseInfoByArchives = CaseInfo.findByArchives(AH)
            //查询到案件舍弃这个数据
            if (caseInfoByArchives){
                log.info("[ZhonghengService.getCaseInfo] 解析用于案件更新的数据时发现案号变更且已经存在,跳过本条数据")
                return null
            }
        }else{
            def caseInfoByArchives = CaseInfo.findByArchives(AH)
            if (caseInfoByArchives) {
                return caseInfoByArchives
            }
            caseInfo = new CaseInfo(
                    uid                : UUIDGenerator.nextUUID(),              //UUID
                    active             : 1,                                //案件状态必填项
                    manufacturer       : "zhongheng",                           //对接厂商
                    synchronizationId  : UID                                    //uid必填项
            )
        }
        caseInfo.archives           = AH                              //案号必填项
        caseInfo.name               = NAME.toString()                                  //案件名称
        caseInfo.type               = CaseType.findByCode(TYPE.toString())         //案件类型名称
        caseInfo.filingDate         = DateUtil.parse(LARQ.toString(), "yyyy-MM-dd HH:mm:ss")   //立案日期  xxxx-xx-xx xx:xx:xx
        caseInfo.department         = department                                  //部门
        caseInfo.summary            = AY.toString()                             //案件概要或案由
        caseInfo.detail             = AJMX.toString()                                //案件明细
        return caseInfo
    }

    /**
     * 处理人员信息(暂时定为添加时出现异常舍弃此人员)
     * @param data 人员数据
     * @param position 人员职位
     * @return 添加成功返回人员信息，否则为null
     */
    def handleEmployee(def data, Integer position){
        if (data == null || data == ""){
            return null
        }
        def employeeUid = data.UID
        def employeeName = data.NAME
        if (employeeUid == null || employeeUid == "" || employeeName == null || employeeName == ""){
            return null
        }
        def employee = Employee.findByManufacturerAndSynchronizationId("zhongheng", employeeUid as String)
        if (!employee){
            employee = new Employee(
                    uid      : UUIDGenerator.nextUUID(),
                    name     : employeeName as String,
                    position : position,
                    manufacturer: "zhongheng",
                    synchronizationId: employeeUid as String
            )
        }else{
            employee.name = employeeName as String
            employee.position = position
        }
        employee.save(flush: true)
        if (employee.hasErrors()){
            log.error("[ZhonghengService.handleEmployee] 添加或更新中恒信人员数据时出错")
            return null
        }
        return employee
    }

    /**
     * 从JSON中解析出PlanInfo
     * @param planData  JSON格式的排期数据
     * @retuen PlanInfo/null
     */
    def getPlanInfoNotHaveCase(JSONObject data){
        def UID    = data.UID                    //排期唯一标识
        def FTMC   = data.FTMC                   //法庭名称
        def FG   = data.FG                   //法官标识
        def SJY  = data.SJY                  //书记员标识
        def CBR  = data.CBR                  //承办人标识
        def STATUS = data.STATUS                 //庭审状态
        def KTSJ   = data.KTSJ                   //计划开庭时间
        def BTSJ   = data.BTSJ                   //计划闭庭时间
        if (!(UID && KTSJ && BTSJ && STATUS != null)) {
            log.info("[ZhonghengService.getCaseInfo] 排期参数校验未通过")
            return null
        }
        def judge = handleEmployee(FG, PositionStatus.JUDGE)
        def secretary = handleEmployee(SJY, PositionStatus.SECRETARY)
        def undertake = handleEmployee(CBR, PositionStatus.OTHER)
        def planInfo = PlanInfo.findByManufacturerAndSynchronizationId("zhongheng", UID as String)
        //找到了排期并
        if (planInfo){
            //不是排期状态
            if (planInfo.status != PlanStatus.PLAN) {
                return planInfo
            }
        }else{
            planInfo = new PlanInfo(
                    uid               : UUIDGenerator.nextUUID(),
                    status            : STATUS as Integer,
                    active            : 1,
                    manufacturer      : "zhongheng",
                    synchronizationId : UID as String
            )
        }
        planInfo.judge             = judge
        planInfo.secretary         = secretary
        planInfo.undertake         = undertake
        planInfo.courtroom         = Courtroom.findByNameLike(FTMC as String)
        planInfo.startDate         = DateUtil.parse(KTSJ as String, "yyyy-MM-dd HH:mm:ss")
        planInfo.endDate           = DateUtil.parse(BTSJ as String, "yyyy-MM-dd HH:mm:ss")
        return planInfo
    }

    /**
     *
     * @param trialData 处理庭次信息
     * @param planInfo 对应排期
     * @return boolean类型是否处理成功
     */
    def handleTrial(def trialData, PlanInfo planInfo){
        if (!trialData){
            log.info("[ZhonghengService.handleTrial] 庭次信息不存在,此排期信息处理结束")
            return true
        }
        //庭次信息存在，继续处理
        trialData = trialData as List<JSONObject>
        for (JSONObject trial : trialData) {
            def UID     = trial.UID    //庭次同步主键-必须有
            def FG      = trial.FG    //法官信息
            def SJY     = trial.SJY    //书记员信息
            def FTMC    = trial.FTMC    //法庭名称-必填
            def KTSJ    = trial.KTSJ    //开庭时间-必填
            def BTSJ    = trial.BTSJ    //闭庭时间-必填
            def STATUS  = trial.STATUS    //庭审状态-必填
            def NOTEURL = trial.NOTEURL    //庭审笔录地址
            if (!(UID && FTMC && Courtroom.findByNameLike(FTMC as String) && KTSJ && BTSJ && STATUS != null && STATUS != "")){
                log.info("[ZhonghengService.handleTrial] 校验庭次信息失败,此排期结束")
                return false
            }
            //处理人员信息
            def judge = handleEmployee(FG, PositionStatus.JUDGE)
            def secretary = handleEmployee(SJY, PositionStatus.SECRETARY)
            //获取庭次信息
            def trialInfo = TrialInfo.findByManufacturerAndSynchronizationId("zhongheng", UID as String)
            if (!trialInfo){
                trialInfo = new TrialInfo(
                        uid: UUIDGenerator.nextUUID(),
                        active: 1,
                        manufacturer: "zhongheng",
                        synchronizationId: UID as String
                )
            }
            trialInfo.planInfo = planInfo
            trialInfo.judge = judge
            trialInfo.secretary = secretary
            trialInfo.courtroom = Courtroom.findByNameLike(FTMC as String)
            trialInfo.startDate = DateUtil.parse(KTSJ as String, "yyyy-MM-dd HH:mm:ss")
            trialInfo.endDate = DateUtil.parse(BTSJ as String, "yyyy-MM-dd HH:mm:ss")
            trialInfo.status = STATUS as Integer
            trialInfo.note = NOTEURL as String
            trialInfo.save(flush: true)
            if (trialInfo.hasErrors()){
                log.error("[ZhonghengService.handleTrial] 添加或更新庭次信息时出错，错误信息为：\n${trialInfo.errors}")
                return false
            }
            def videoResult = handleVideo(trial.VIDEO, trialInfo)
            if (!videoResult) {
                return false
            }
        }
        return true
    }

    /**
     * 处理录像信息
     * @param videoData 录像信息
     * @param trialInfo 对应庭次
     * @return boolean类型是否处理成功
     */
    def handleVideo(def videoData, TrialInfo trialInfo){
        if (!videoData) {
            log.info("[ZhonghengService.handleVideo] 此排期没有视频数据，排期结束")
            return true
        }
        videoData = videoData as List<JSONObject>
        for (JSONObject video : videoData){
            def UID     = video.UID     //VIDEO同步主键-必填
            def FILEURL = video.FILEURL //视频地址-必填
            def FBL     = video.FBL     //分辨率
            def SIZE    = video.SIZE    //文件大小-必填
            def TDH     = video.TDH     //通道号-必填
            def TDMC    = video.TDMC    //通道名称-必填
            def MTLX    = video.MTLX    //媒体类型
            def LXSC    = video.LXSC    //录像时长（秒）-必填
            def KSLXSJ  = video.KSLXSJ  //开始录像时间-必填
            def JSLXSJ  = video.JSLXSJ  //结束录像时间-必填
            def ML      = video.ML      //码率
            if (!(UID && FILEURL && SIZE && TDH && TDMC && LXSC && KSLXSJ && JSLXSJ)){
                log.info("[ZhonghengService.handleVideo] 校验视频信息失败,此排期结束")
                return false
            }
            def videoInfo = VideoInfo.findByManufacturerAndSynchronizationId("zhongheng", UID as String)
            if (!videoInfo){
                videoInfo = new VideoInfo(
                        uid: UUIDGenerator.nextUUID(),
                        active: 1,
                        manufacturer: "zhongheng",
                        synchronizationId: UID as String
                )
            }
            videoInfo.channelNum = TDH as String
            videoInfo.channelName = TDMC as String
            videoInfo.mediaType = MTLX as String
            videoInfo.resolution = FBL as String
            videoInfo.mediaStreamSize = ML as String
            videoInfo.startRecTime = DateUtil.parse(KSLXSJ as String, "yyyy-MM-dd HH:mm:ss")
            videoInfo.endRecTime = DateUtil.parse(JSLXSJ as String, "yyyy-MM-dd HH:mm:ss")
            videoInfo.fileName = FILEURL as String
            videoInfo.size = SIZE as String
            videoInfo.length = LXSC as Integer
            videoInfo.trialInfo = trialInfo
            videoInfo.save(flush: true)
            if (videoInfo.hasErrors()){
                log.info("[ZhonghengService.handleVideo] 添加或更新录像信息时失败，失败信息为：\n${videoInfo.errors}")
                return false
            }
        }
        return true
    }

    def handleEmployeeData(List<JSONObject> employeeData){
        println employeeData
    }

    def handleDepartmentData(List<JSONObject> deptData){
        println deptData
    }
}
