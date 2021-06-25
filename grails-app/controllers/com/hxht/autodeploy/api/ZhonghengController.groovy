package com.hxht.autodeploy.api

import cn.hutool.core.date.DateUtil
import com.hxht.techcrt.*
import com.hxht.techcrt.court.*
import com.hxht.techcrt.court.plan.ChatRecordService
import com.hxht.techcrt.enums.RespType
import com.hxht.techcrt.utils.*
import grails.converters.JSON
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo

/***
 * 中恒信接口
 */
class ZhonghengController {

    ZhonghengService zhonghengService
    ChatRecordService chatRecordService

    /**
     * 中恒获取法庭信息
     */
    def getCourtRoomList() {
        if (request.method == "POST") {
            log.info("进入获取法庭信息接口-------------")
            def courtrooms
            try {
                def params = request.JSON as JSONObject
                def requestParam = params.requestParam
                if (requestParam) {
                    def uid = requestParam.uid
                    if (uid) {
                        courtrooms = Courtroom.findAllByStatusAndUid(CourtroomStatus.NORMAL, uid)
                    } else {
                        // 只查询当前正常使用的法庭
                        courtrooms = Courtroom.findAllByStatus(CourtroomStatus.NORMAL)
                    }
                } else {
                    // 只查询当前正常使用的法庭
                    courtrooms = Courtroom.findAllByStatus(CourtroomStatus.NORMAL)
                }
                def CourtRooms = new JSONArray()
                for (Courtroom courtroom : courtrooms) {
                    def courtroom_json = new com.alibaba.fastjson.JSONObject()
                    courtroom_json.put("uid", courtroom.uid)
                    courtroom_json.put("courtroomname", courtroom.name)
                    CourtRooms.add(courtroom_json)
                }
                def returnParam = Resp.return_success(RespType.SUCCESS.getCode(), CourtRooms)
                log.info("返回的参数为:" + returnParam)
                render returnParam
            } catch (Exception e) {
                def msg = "中恒获取法庭信息出错\nERROR[${e.message}]-----[${ExceptionUtil.getStackTrace(e)}"
                log.error("[ZhonghengController.getCourtRoomList]${msg}")
                render Resp.return_error(RespType.GROGRAMME_ERROE.getCode(), e.getMessage())
            }
        }
    }


    /**
     * 中恒根据多条件查询排期
     *
     */
    def getCourtScheduledInfo() {
        if (request.method == "POST") {
            try {
                List<PlanInfo> plans
                String StartTime = null
                String EndTime = null
                Integer status = null
                String uid = null
                def courtroom = null

                def params = request.JSON as JSONObject
//                log.info("请求参数:param=" + params.toString())
                def requestParam = params.requestParam
                if (requestParam) {

                    // 开庭时间
                    StartTime = requestParam.StartTime
                    // 闭庭时间
                    EndTime = requestParam.EndTime
                    status = requestParam.status//必填
                    uid = requestParam.planUid//排期uid
                    courtroom = Courtroom.findByUid(requestParam.courtroomId)
                }
                Date startDate
                Date endDate
                if (status == null || status == 0) {
                    status = 0//默认查询排期
                    if (StartTime && EndTime){
                        if ((DateUtils.str2Date(EndTime).getTime() - DateUtils.str2Date(StartTime).getTime())/(1000 * 3600 * 24) > 7){//大于七天则截取开始时间到结束时间为七天
                            startDate = DateUtils.str2Date(StartTime)
                            endDate = DateUtil.offsetDay(startDate, 7)
                        }else{
                            //小于七天则直接赋值传过来的开始时间和结束时间
                            startDate = DateUtils.str2Date(StartTime)
                            endDate = DateUtils.str2Date(EndTime)
                        }
                    }else{
                        //只让查一个星期的排期
                        startDate = DateUtil.beginOfDay(new Date())//获取今天的0点
                        endDate = DateUtil.offsetDay(startDate, 7)
                    }
                } else {
                    startDate = DateUtils.str2Date(StartTime)
                    endDate = DateUtils.str2Date(EndTime)
                }
                plans = zhonghengService.planScheduleInfo(status, uid, courtroom, startDate, endDate)

                def CourtSchedules = new JSONArray()
//                def CourtSchedules = []
                for (PlanInfo p : plans) {
//                    def plan_json = [:]
                    com.alibaba.fastjson.JSONObject plan_json = new com.alibaba.fastjson.JSONObject()
                    plan_json.put("caseId", p.caseInfo?.uid)//案件id
                    plan_json.put("planUid", p.uid)//排期id

                    plan_json.put("caseno", p.caseInfo?.archives)// 案号
                    plan_json.put("courtroomId", p.courtroom?.uid)//法庭编号
                    def trialInfoList = TrialInfo.findAllByPlanInfo(p,[sort: "startDate", order: "asc"])
                    plan_json.put("startTime", DateUtil.format(trialInfoList? trialInfoList[0].startDate as Date : p.startDate as Date,"yyyy-MM-dd HH:mm:ss"))//开始时间
                    plan_json.put("endTime", DateUtil.format(trialInfoList? trialInfoList[trialInfoList.size() - 1].startDate as Date : p.startDate as Date,"yyyy-MM-dd HH:mm:ss"))//结束时间
                    plan_json.put("status", p.status)//排期状态
                    plan_json.put("accuse", p.caseInfo?.accuser)//原告
                    plan_json.put("accused", p.caseInfo?.accused)//被告
                    plan_json.put("judge", p.judge?.name)//法官
                    plan_json.put("secretary", p.secretary?.name)//书记员
                    plan_json.put("summary", p.caseInfo?.summary)//案件描述

                    CourtSchedules.add(plan_json)
                }

//                log.info("返回的参数为:"+returnParam) TODO 打印过多内容 logback 抛出 OutOfMemoryError
                render(contentType: "application/json", text: Resp.return_success(RespType.SUCCESS.getCode(), CourtSchedules))
            } catch (Exception e) {
                def msg = "中恒获取法庭排期出错:\nERROR[${e.message}]-----[${ExceptionUtil.getStackTrace(e)}"
                log.error("[ZhonghengController.getCourtScheduledInfo]${msg}")
                render Resp.return_error(RespType.GROGRAMME_ERROE.getCode(), e.getMessage())
            }
        }
    }

    /**
     * 为中恒信提供接口调用token
     * @return JSON格式放置于data中的token
     */
    def token(){
        def secret = grailsApplication.config.getProperty('jwt.info.secret')
        def expires = grailsApplication.config.getProperty('jwt.info.expires') as long
        def authenticationCode = params.get("authenticationCode") as String
        render Resp.toJson(RespType.SUCCESS, JwtUtil.token(secret, expires, authenticationCode))
    }

//    /**
//     * 添加或修改排期
//     * @return  添加或修改是否成功的JSON数据
//     */
//    def savePlan(){
//        if (request.method == "POST") {
//            try {
//                def planData = request.JSON as JSONObject
//                if (!planData){
//                    render Resp.toJson(RespType.FAIL, "Plan does not exist.")
//                    return
//                }
//                def caseData = planData.case as JSONObject
//                if (!caseData) {
//                    render Resp.toJson(RespType.FAIL, "Case does not exist.")
//                    return
//                }
//                def caseInfo = zhonghengService.getCaseInfo(caseData)
//                if (!caseInfo){
//                    render Resp.toJson(RespType.FAIL, "Case parsing failed.The possible cause is uid/archives/active/date_created does not exist.")
//                    return
//                }
//                def planInfo = zhonghengService.getPlanInfoNotHaveCase(planData)
//                if (!planInfo){
//                    render Resp.toJson(RespType.FAIL, "Plan parsing failed.The possible cause is uid/date_ created/active/end_ date/start_ Date/status does not exist.")
//                    return
//                }
//                zhonghengService.savePlan(caseInfo, planInfo)
//                render Resp.toJson(RespType.SUCCESS)
//            }catch(e){
//                e.printStackTrace()
//                render Resp.toJson(RespType.FAIL, "An exception occurred while adding or updating the plan_case.")
//            }
//        }
//    }
//
//    /**
//     * 删除排期（逻辑删除）
//     * @return  JSON格式的成功标识，data为删除失败的同步主键标识
//     */
//    def delPlan(){
//        if (request.method == "POST") {
//            try {
//                def result = []
//                def uidData = request.JSON as List<JSONObject>
//                for (JSONObject data: uidData){
//                    def uid = data.uid
//                    if (uid == null || uid == ""){
//                        continue
//                    }
//                    def syncId = zhonghengService.delPlanBySyncId(uid as String)
//                    if (syncId != "true"){
//                        result.add([
//                                uid : syncId
//                        ])
//                    }
//                }
//                render Resp.toJson(RespType.SUCCESS, result)
//            }catch(e){
//                e.printStackTrace()
//                render Resp.toJson(RespType.FAIL, "An exception occurred while deleting the plan.")
//            }
//        }
//    }
//
//    /**
//     * 中恒信开庭操作(正常开庭，休庭后开庭，离线开庭)
//     * @return JSON格式个是否成功，及开庭成功后的排期标识和庭审标识
//     */
//    def openTrial(){
//        if (request.method == "POST") {
//            try {
//                log.info("[ZhonghengController.openTrial] 中恒信请求开庭")
//                def data = request.JSON as JSONObject
//                def planUid       = data.planUid                 //排期同步主键(正常开庭必填,临时开庭置空此项)
//                def trialUid      = data.trialUid                //若为休庭后开庭，请传休庭后返回值，否则请置空
//                def courtroomName = data.courtroom               //法庭名称，必填
//                def judgeName     = data.judge                   //法官姓名(临时开庭必填,正常开庭默认从排期获取可空)
//                def secretaryName = data.secretary               //书记员姓名(临时开庭必填,正常开庭默认从排期获取可空)
//                def resultData = []
//                //法官
//                def judge     = zhonghengService.getEmployee(judgeName as String, PositionStatus.JUDGE)
//                //书记员
//                def secretary = zhonghengService.getEmployee(secretaryName as String, PositionStatus.SECRETARY)
//                if (planUid != null && planUid != ""){
//                    log.info("[ZhonghengController.openTrial] 中恒信请求正常开庭,排期同步主键：${planUid as String}")
//                    //判断courtroom是否存在
//                    if (courtroomName == null || courtroomName == ""){
//                        log.error("[ZhonghengController.openTrial] 中恒信请求正常开庭，但是没有传法庭名称")
//                        render Resp.toJson(RespType.FAIL, "courtroom does not exist.")
//                        return
//                    }
//                    def courtroom = Courtroom.findByName(courtroomName as String)
//                    if (!courtroom){
//                        log.error("[ZhonghengController.openTrial] 中恒信请求正常开庭，但是没有找到法庭名称(${courtroomName})对应的法庭")
//                        render Resp.toJson(RespType.FAIL, "courtroom(${courtroomName}) was not found.")
//                        return
//                    }
//                    //当前审理庭的所有正在开庭的庭审 并将所有开庭的庭审关掉
//                    def trialList = TrialInfo.findAllByCourtroomAndStatus(courtroom, PlanStatus.SESSION)
//                    for (def trial : trialList) {
//                        def trialStop = trialService.planClosed(trial)
//                        trialService.stopStorevideo(trialStop)
//                    }
//                    //排期
//                    def planInfo = PlanInfo.findByManufacturerAndSynchronizationId("zhongheng", planUid as String)
//                    if (!planInfo) {
//                        log.error("[ZhonghengController.openTrial] 中恒信请求正常开庭失败,未获取到排期")
//                        render Resp.toJson(RespType.FAIL, "plan(${planUid}) was not found.")
//                        return
//                    }
//                    if (!judge) {
//                        judge = planInfo.judge
//                    }
//                    if (!secretary) {
//                        secretary = planInfo.secretary
//                    }
//                    def trial = null
//                    if (trialUid != null && trialUid != ""){
//                        //trialUid为开庭成功后返回给中恒信的uid
//                        trial = TrialInfo.findByUid(trialUid as String)
//                    }
//                    def trialId = null
//                    if (trial){
//                        if (trial.status == PlanStatus.CLOSED){
//                            log.error("[ZhonghengController.openTrial] 中恒信请求正常开庭失败,此庭审已经闭庭")
//                            render Resp.toJson(RespType.FAIL, "the trial(${trialUid as String}) is closed.")
//                            return
//                        }
//                        trialId = trial.id
//                    }
//                    def trialInfo = apiService.planOpen(planInfo, courtroom, judge, secretary, trialId as String)
//                    resultData.add([
//                            planUid : planInfo.synchronizationId,
//                            trialUid : trialInfo.uid
//                    ])
//                }else {
//                    log.info("[ZhonghengController.openTrial] 中恒信请求离线开庭")
//                    //案件信息(正常开庭为控制，临时开庭必填)
//                    def caseData = data.case as JSONObject
//                    //案号判空
//                    if (caseData.archives == null || caseData.archives == "") {
//                        log.info("[ZhonghengController.openTrial] 中恒信进行离线开庭时没有传案件编号的值，开庭失败。")
//                        render Resp.toJson(RespType.FAIL, "archives does not exist.")
//                        return
//                    }
//                    def caseInfo = zhonghengService.getCaseInfo(caseData)
//                    def planid
//                    //可能由于各种原因没有解析出案件信息
//                    if (!caseInfo) {
//                        //使用相同案件编号的案件进行开庭
//                        log.info("[ZhonghengController.openTrial] 中恒信离线开庭时case没有成功解析出案件信息,尝试使用案号获取案件信息")
//                        caseInfo = CaseInfo.findByArchives(params.archives as String)
//                    }
//                    //解析出或者查找到案件编号相同的案件信息
//                    if (!caseInfo) {
//                        log.info("[ZhonghengController.openTrial] 中恒信进行离线开庭时没有传案件编号的值，开庭失败。")
//                        render Resp.toJson(RespType.FAIL, "archives does not exist.")
//                        return
//                    }
//                    def trialInfo = zhonghengService.openTrial(caseInfo, Courtroom.findByName(courtroomName as String))
//                    resultData.add([
//                            planUid : trialInfo.planInfo.synchronizationId,
//                            trialUid : trialInfo.uid
//                    ])
//                }
//                render Resp.toJson(RespType.SUCCESS, resultData)
//            }catch(e){
//                e.printStackTrace()
//                render Resp.toJson(RespType.FAIL, "An exception occurred while opening the plan.")
//            }
//        }
//    }
//
//    /**
//     * 中恒信请求休庭或闭庭接口
//     */
//    def stopTrial(){
//        if (request.method == "POST") {
//            try {
//                def data = request.JSON as JSONObject
//                def type = data.type
//                def uid  = data.uid
//                if (type == null || type == "" || uid == null || uid == "" || (type != 0 && type != 1)){
//                    log.info("[ZhonghengController.stopTrial] 中恒信请求休庭或闭庭时数据校验未通过,数据为：type=${type},uid=${uid}")
//                    render Resp.toJson(RespType.FAIL, "type/uid does not exist.")
//                    return
//                }
//                type = type as Integer
//                uid  = uid as String
//                def trialInfo = TrialInfo.findByUid(uid)
//                if (!trialInfo){
//                    log.info("[ZhonghengController.stopTrial] 中恒信请求休庭或闭庭时没有找到对应的庭审,数据为：type=${type},uid=${uid}")
//                    render Resp.toJson(RespType.FAIL, "trial not found.")
//                    return
//                }
//                def trial
//                if (type == 0){
//                    log.info("[ZhonghengController.stopTrial] 中恒信请求闭庭")
//                    trial = apiService.planClosed(trialInfo)//闭庭操作
//                }else{
//                    log.info("[ZhonghengController.stopTrial] 中恒信请求休庭")
//                    trial = apiService.planAdjourn(trialInfo)//休庭操作
//                }
//                //通知评查系统停止核查
//                apiVerificationService.closeToVc(trial)
//                def fydm = Dict.findByCode("CURRENT_COURT").ext3
//                if (fydm == "J30") {//深圳中院用的对接太极接口
//                    apiTaiChiService.taichiDossier(trial)
//                }
//                render Resp.toJson(RespType.SUCCESS)
//            }catch(e){
//                e.printStackTrace()
//                render Resp.toJson(RespType.FAIL, "An exception occurred while stopping the plan.")
//            }
//        }
//    }
//
    /**
     * 处理历史排期接口
     */
    def uploadHistoryPlan() {
        if (request.method == "POST") {
            try {
                //获取到排期数据转化为List<JSONObject>
                zhonghengService.handlePlanData(request.JSON as List<JSONObject>)
                render Resp.toJson(RespType.SUCCESS)
            } catch (e) {
                e.printStackTrace()
                render Resp.toJson(RespType.FAIL, "处理上传排期的过程中产生异常")
            }
        }
    }

    def uploadEmployee(){
        if (request.method == "POST") {
            try {
                //获取到排期数据转化为List<JSONObject>
                zhonghengService.handleEmployeeData(request.JSON as List<JSONObject>)
                render Resp.toJson(RespType.SUCCESS)
            } catch (e) {
                e.printStackTrace()
                render Resp.toJson(RespType.FAIL, "上传人员数据的过程中产生异常")
            }
        }
    }

    def uploadDepartment(){
        if (request.method == "POST") {
            try {
                //获取到排期数据转化为List<JSONObject>
                zhonghengService.handleDepartmentData(request.JSON as List<JSONObject>)
                render Resp.toJson(RespType.SUCCESS)
            } catch (e) {
                e.printStackTrace()
                render Resp.toJson(RespType.FAIL, "上传部门数据的过程中产生异常")
            }
        }
    }

    /**
     * 中恒信聊天接口
     */
    @MessageMapping("/zhongheng/chat")
    @SendTo("/topic/chat")
    protected chat(String msg) {
        def json = JSON.parse(URLDecoder.decode(Base64Utils.decode(msg), "UTF-8"))
        if (!json.planId){
            render Resp.toJson(RespType.FAIL, "传值planId为空")
            return
        }
        def uuid = UUIDGenerator.nextUUID()
        def addData = [
                uuid       : uuid,
                time       : json.time,
                userName   : json.userName,
                chatContext: json.chatContext
        ]
        def sendData = [
                uuid       : uuid,
                time       : json.time,
                planId     : json.planId,
                userName   : json.userName,
                chatContext: json.chatContext
        ]
        //数据存储
        chatRecordService.addMessage(json.planId as Long, addData)
        return (sendData as JSON) as String
    }
}
