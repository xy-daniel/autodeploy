package com.hxht.autodeploy.event

import com.hxht.techcrt.Manufacturer
import com.hxht.techcrt.api.ApiOfflinePlanService
import com.hxht.techcrt.async.ApiVerificationService
import com.hxht.techcrt.async.Mp4ToMp3Service
import com.hxht.techcrt.async.ScreenShotService
import com.hxht.techcrt.jobs.WorkSyncService
import com.hxht.techcrt.sync.huaxia.HuaXiaService
import com.hxht.techcrt.sync.showVideoPlatform.ShowVideoPlatformService
import com.hxht.techcrt.sync.util.WebServiceNormalService
import com.hxht.techcrt.wfy.WfyService
import grails.core.GrailsApplication
import grails.events.annotation.Subscriber

/**
 * 2021.03.19 >>> 项目启动给华夏推送法庭、部门、人员数据 daniel
 * 2021.04.14 >>> 项目启动初始化华夏接口代理 daniel
 */
class CourtEventService {
    WfyService wfyService
    ScreenShotService screenShotService
    Mp4ToMp3Service mp4ToMp3Service
    ApiVerificationService apiVerificationService
    WebServiceNormalService webServiceNormalService
    ApiOfflinePlanService apiOfflinePlanService
    WorkSyncService workSyncService
    HuaXiaService huaXiaService
    ShowVideoPlatformService showVideoPlatformService
    GrailsApplication grailsApplication

    /**
     * 当对案件进行排期时触发此事件
     */
    @Subscriber
    def createPlan(Long planInfoId) {

    }

    /**
     * 将排期进行开庭操作时触发此事件
     */
    @Subscriber
    def startTrial(Long trialInfoId) {
        wfyService.holdCourt(trialInfoId)//通知微法院开庭
        apiVerificationService.openToVc(trialInfoId)//通知核查系统进行核查
        //深圳点播平台推送开庭数据 提供系统视频点播
        showVideoPlatformService.pushData(trialInfoId)
    }
    /**
     * 将庭审进行闭庭操作时触发此事件
     */
    @Subscriber
    def stopTrial(Long trialInfoId) {
        println("stopTrial--------------")
        //通知微法院闭庭
        wfyService.closeCourt(trialInfoId)
        //通知评查系统停止核查
        apiVerificationService.closeToVc(trialInfoId)
        //深圳点播平台推送闭庭数据 提供系统视频点播
        showVideoPlatformService.pushSvClose(trialInfoId)
    }

    /**
     * 将庭审进行休庭操作时触发此事件
     */
    @Subscriber
    def adjournTrial(Long trialInfoId) {
        println("adjournTrial--------------")
        //通知微法院休庭,法官离场.
        wfyService.leaveRoom(trialInfoId)
        //通知评查系统停止核查
        apiVerificationService.closeToVc(trialInfoId)
        //深圳点播平台推送闭庭数据 提供系统视频点播
        showVideoPlatformService.pushSvClose(trialInfoId)
    }

    /**
     * 将庭审进行复庭操作时触发此事件
     */
    @Subscriber
    def resumeTrial(Long trialInfoId) {
        wfyService.holdCourt(trialInfoId)//通知微法院开庭
        apiVerificationService.openToVc(trialInfoId)//通知核查系统进行核查
    }

    /**
     * 刷新笔录时,触发此事件
     */
    @Subscriber
    def refreshNote(Long trialInfoId) {

    }

    /**
     * 执行视频截图。触发此事件
     */
    @Subscriber
    def screenShot(Long videoId) {
        screenShotService.screenShot(videoId)//通知视频截图
    }

    /**
     * 执行视频转mp3。触发此事件
     */
    @Subscriber
    def audioToVideo(Long videoId) {
        mp4ToMp3Service.audioToVideo(videoId)//通知视频转mp3
    }

    /**
     * 异步向核查系统对接执行视频地址存储,触发此事件
     */
    @Subscriber
    def videoInfoToVc(Long videoId, Long trialId) {
        apiVerificationService.videoInfoToVc(videoId, trialId)
    }
    /**
     * 异步向核查系统对接设备地址。触发此事件
     */
    @Subscriber
    def deviceIpToVc(String courtRoomName, String deviceIp) {
        apiVerificationService.deviceIpToVc(courtRoomName, deviceIp)
    }

    /**
     * 异步向CMP平台推送案件、排期 、庭审信息。触发此事件
     */
    @Subscriber
    def pushCmpCaseAndPlanAndTrial(Long caseInfoId, Long planInfoId, Long trialInfoId) {
        webServiceNormalService.pushCase(caseInfoId)//向CMP平台推送案件数据
        webServiceNormalService.pushPlan(planInfoId)//向CMP平台推送排期数据
        webServiceNormalService.pushTrial(trialInfoId)//向CMP平台推送庭审数据
    }
    /**
     * 异步下载视频文件。触发此事件
     */
    @Subscriber
    def downloadVideo(Long trialId, Integer version) {
        apiOfflinePlanService.downloadVideo(trialId, version)
    }

    /**
     * 异步执行排期接口
     */
    @Subscriber
    def planSyc() {
        workSyncService.sync()
        String work = grailsApplication.config.getProperty('syncData.project_work')
        if (work.contains(Manufacturer.HUAXIA)) {
            huaXiaService.initService()
            huaXiaService.pushCourtroom()
            huaXiaService.pushDept()
            huaXiaService.pushUser()
            log.info("[CourtEventService.planSyc] 项目启动给华夏推送数据完成.")
        }
    }

    /**
     * 异步执行深圳点播平台推送系统视频点播数据
     */
    @Subscriber
    def pushSvVideoData(Long trialInfoId, Long videoInfoId) {
        //深圳点播平台推送闭庭数据 提供系统视频点播
        showVideoPlatformService.pushSvVideo(trialInfoId, videoInfoId)
    }
    
    /**
     * 异步执行视频转移后修改路径 向深圳点播平台推送数据
     */
    @Subscriber
    def pushSvVideoUrlData(Long videoInfoId) {
        //深圳点播平台推送闭庭数据 提供系统视频点播
        showVideoPlatformService.pushSvVideoUrl(videoInfoId)
    }

}
