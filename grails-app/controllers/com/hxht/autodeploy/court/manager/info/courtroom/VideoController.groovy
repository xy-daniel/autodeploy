package com.hxht.autodeploy.court.manager.info.courtroom

import com.hxht.techcrt.DataStatus
import com.hxht.techcrt.PlanStatus
import com.hxht.techcrt.Resp
import com.hxht.techcrt.court.Courtroom
import com.hxht.techcrt.court.PlanInfo
import com.hxht.techcrt.court.TrialInfo
import com.hxht.techcrt.enums.RespType
import grails.converters.JSON

/**
 * 视频监控  by arctic in 2019.11.07
 */
class VideoController {

    CtrlService ctrlService

    /**
     * 前往视频监控页面
     */
    def show(){
        //根据法庭主键获取法庭信息
        def courtroom = Courtroom.get(params.long("id"))
        if (!courtroom){
            redirect(controller:"courtroom", action: "list")
            return
        }
        if (!courtroom.cfg){
            courtroom.cfg = ctrlService.getCfg(courtroom) as JSON
        }
        def cfg = JSON.parse(courtroom.cfg)
        def chnList = []
        for (def encode : cfg.encode) {
            chnList.add([
                    number : encode.number,
                    name: encode.name
            ])
        }
        //根据法庭获取相关正在开庭的排期---->分为正在开庭还是没有正在开庭
        def planInfo = PlanInfo.findByCourtroomAndStatus(courtroom, PlanStatus.SESSION)
        //如果有正在开庭的planInfo，那我们就去获取与其相关的trailInfo
        if (planInfo){
            def trialInfo = null
            //排期处于开庭状态
            if (planInfo.status == PlanStatus.SESSION) {
                //正在开庭的为显示trial
                trialInfo = TrialInfo.findByPlanInfoAndStatusAndActive(planInfo, PlanStatus.SESSION, DataStatus.SHOW)
            } else if (planInfo.status == PlanStatus.ADJOURN || planInfo.status == PlanStatus.CLOSED) {//排期休庭或者闭庭了
                def trialInfoList = TrialInfo.findAllByPlanInfoAndActive(planInfo, DataStatus.SHOW, [sort: "status", order: "asc"])
                trialInfo = trialInfoList.get(0)
            }
            def data = [
                    planId      : planInfo.id,
                    trialId     : trialInfo?.id ?: "",
                    caseArchives: planInfo.caseInfo.archives,
                    caseName    : planInfo.caseInfo.name,
                    caseType    : planInfo.caseInfo.type,
                    courtroom   : trialInfo?.courtroom ?: planInfo.courtroom,
                    accuser     : planInfo.caseInfo.accuser ?: "无数据",
                    accused     : planInfo.caseInfo.accused ?: "无数据",
                    filingDate  : planInfo.caseInfo.filingDate?.format('yyyy/MM/dd HH:mm'),
                    startDate   : trialInfo?.startDate?.format('yyyy/MM/dd HH:mm') ?: planInfo.startDate?.format('yyyy/MM/dd HH:mm'),
                    endDate     : trialInfo?.endDate?.format('yyyy/MM/dd HH:mm') ?: planInfo.endDate?.format('yyyy/MM/dd HH:mm'),
                    collegial   : planInfo.collegial,
                    secretary   : trialInfo?.secretary ?: planInfo.secretary,
                    summary     : planInfo.caseInfo.summary ?: "无数据",
                    detail      : planInfo.caseInfo.detail ?: "无数据",
                    status      : trialInfo?.status ?: planInfo.status
            ]
            [data: data, chnList: chnList, courtroom:courtroom]
        }else{
            //没有查找到正在开庭的排期
            def data = [
                    planId      : "",
            ]
            [data: data, chnList: chnList, courtroom:courtroom]
        }
    }

    def showVideo() {
        //房间主键一定存在
        def courtroom = Courtroom.get(params.long("id"))
        def cfg = JSON.parse(courtroom.cfg)
        def videoList = []
        //法庭没有开庭的案件
        for (def encode : cfg.encode) {
//            def url = "http://${courtroom.liveIp}:8300/hls/${encode.encodeip}/${encode.number}/s.m3u8"
            def url = "http://${courtroom.liveIp}:8791/${encode.encodeip}/${encode.number}.flv"
            videoList.add([
                    number: encode.number,
                    name  : encode.name,
                    url : url
            ])
        }
        render Resp.toJson(RespType.SUCCESS, [video: videoList])
    }
}
