package com.hxht.autodeploy.api

import com.hxht.techcrt.Resp
import com.hxht.techcrt.court.*
import com.hxht.techcrt.enums.RespType
import com.hxht.techcrt.utils.DateUtils
import com.hxht.techcrt.utils.ExceptionUtil
import grails.converters.JSON
import grails.core.GrailsApplication
import grails.events.EventPublisher
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject

/**
 * 离线开庭接口
 */
class ApiOfflinePlanController implements EventPublisher{
    GrailsApplication grailsApplication
    ApiOfflinePlanService apiOfflinePlanService

    /**
     * 离线对接书记员客户端接口，并保存离线数据
     */
    def offlineSave() {
        if (request.method == "POST") {
            try {
                def plan = request.JSON as JSONObject
                log.info("[ApiOfflinePlanController.offlineSave]---->接收离线开庭数据：${plan.toString()}")
                def version = plan.version
                def caseInfo = plan.caseInfo
                def planInfo = plan.planInfo
                def trialInfoList = plan.trialInfo as JSONArray

                //案件信息
                def caseSummary = caseInfo.summary //"案件概要（可为空）"
                def caseUid = caseInfo.uid //"c2f7a7dac67c4a50978a69b0d85ecb3c"
                def casePC = caseInfo.prosecutionCounsel //"原告律师（可为空）"
                def caseCD = caseInfo.counselDefence //"被告律师（可为空）"
                def caseAccuser = caseInfo.accuser //"原告"
                def caseAccused = caseInfo.accused //"被告"
                def caseArchives = caseInfo.archives //"案号"
                def caseName = caseInfo.name //"案件名称"
                def caseType = caseInfo.typeId //"案件类型"
                def fillingDate = caseInfo.fillingDate //立案日期

                //排期信息
                def planUid = planInfo.uid  //c2f7a7dac67c4a50978a69b0d85ec222
                def planCourt = planInfo.courtroomId // 法庭Id
                def planStartDate = planInfo.startDate //2020-05-22 14 14:36:04
                def planEndDate = planInfo.endDate //2020-05-22 14:36:04
                def planJudge = planInfo.judgeId //审判长
                def planSecretary = planInfo.secretaryId //书记员
                def planStatus = planInfo.status //庭审状态
                //法庭为空的话直接返回错误，因为离线下载视频需要根据法庭查找
                if (!planCourt){
                    render Resp.toJson(RespType.FAIL,"离线开庭输入的法庭不能为空！")
                    return
                }
                def pl = PlanInfo.findByUid(planInfo.uid)//验证排期是否存在
                def trialList
                def caseInfoExit
                if (pl) {//存在的情况
                    caseInfoExit = pl.caseInfo
                    /*//排期状态为开庭或者排期状态时则进行覆盖操作否则只修改状态*/
                    //将传值过的案件信息进行判断 存在则进行覆盖
                } else {
                    //通过案号查找 相同的则进行覆盖操作
                    caseInfoExit = CaseInfo.findByArchives(caseInfo.archives)
                    if (!caseInfoExit) {//存在相同案号则进行覆盖操作
                        caseInfoExit = new CaseInfo()
                    }
                    pl = new PlanInfo()
                }
                if (caseSummary) {
                    caseInfoExit.summary = caseSummary
                }
                if (casePC) {
                    caseInfoExit.prosecutionCounsel = casePC
                }
                if (caseCD) {
                    caseInfoExit.counselDefence = caseCD
                }
                if (caseAccuser) {
                    caseInfoExit.accuser = caseAccuser
                }
                if (caseAccused) {
                    caseInfoExit.accused = caseAccused
                }
                if (caseArchives) {
                    caseInfoExit.archives = caseArchives
                }
                if (caseName) {
                    caseInfoExit.name = caseName
                }
                if (caseType) {
                    caseInfoExit.type = CaseType.get(caseType as long)
                }
                if (fillingDate) {
                    caseInfoExit.filingDate = DateUtils.str2Date(fillingDate)
                }
                if (caseUid){
                    caseInfoExit.uid = caseUid
                }
                //排期信息判断存在则进行覆盖
                if (planUid) {
                    pl.uid = planUid
                }
                pl.courtroom = Courtroom.get(planCourt as long)
                if (planStartDate) {
                    pl.startDate = DateUtils.str2Date(planStartDate)
                }
                if (planEndDate) {
                    pl.endDate = DateUtils.str2Date(planEndDate)
                }
                if (planJudge) {
                    pl.judge = Employee.get(planJudge as long)//获取法官
                }
                if (planSecretary) {
                    pl.secretary = Employee.get(planSecretary as long)//获取书记员
                }
                if (planStatus) {
                    pl.status = planStatus as Integer
                }
                trialList = apiOfflinePlanService.saveCasePlanTrial(caseInfoExit, pl, trialInfoList)
                //异步下载视频
                log.info("[ApiOfflinePlanController.offlineSave]---->离线开庭数据添加成功，版本号：${version}")
                for (def trial : trialList) {
                    log.info("[ApiOfflinePlanController.offlineSave]---->离线开庭庭审uid:${trial.uid}")
                    this.notify("downloadVideo", trial.id, version)
                }
                render Resp.toJson(RespType.SUCCESS, trialList)
            } catch (e) {
                e.printStackTrace()
                log.error("[ApiOfflinePlanController.offlineSave] 离线开庭接收数据,接收到的数据：[${params as JSON}]\nERROR[${e.message}]-----[${ExceptionUtil.getStackTrace(e)}")
                render Resp.toJson(RespType.FAIL)
            }
        }
    }
}
