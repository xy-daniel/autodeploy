package com.hxht.autodeploy.sync.showVideoPlatform

import cn.hutool.core.date.DateUtil
import com.hxht.techcrt.DataStatus
import com.hxht.techcrt.Dict
import com.hxht.techcrt.court.TrialInfo
import com.hxht.techcrt.court.VideoInfo
import com.hxht.techcrt.court.manager.SystemController
import com.hxht.techcrt.utils.http.HttpUtil
import grails.core.GrailsApplication
import grails.gorm.transactions.Transactional

@Transactional
class ShowVideoPlatformService {
    GrailsApplication grailsApplication

    //向深圳点播平台系统推送数据
    def pushData(long trialInfoId) {
        //只有深圳需要对接点播总平台系统
        def showVideoUrl = Dict.findByCode("ShowVideoPlatform")?.val
        if (grailsApplication.config.getProperty("tc.deployPlace") == "shenzhen" && showVideoUrl) {
            try {
                log.info("异步对接执行开庭对接点播总平台系统---开始执行！")
                def trialInfo = TrialInfo.get(trialInfoId)
                /*if (!(trialInfo.status && (trialInfo.status == PlanStatus.CLOSED || trialInfo.status == PlanStatus.ADJOURN))){
                    log.info("庭审不是休庭或者闭庭状态！并返回。")
                    return 
                }*/
                def planInfo = trialInfo.planInfo
                def caseInfo = planInfo.caseInfo
//                def videoInfoList = trialInfo.videoInfo
                
                def modelCaseInfo = [:]
                def modelPlanInfo = [:]
                def modelTrialInfo = [:]
//                def modelVideoInfo = [:]
                def arrayTrialInfo = []
                def arrayVideoInfo = []
                def arrayData = []
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
                modelPlanInfo.put("uid", planInfo.uid)//排期uid
                modelPlanInfo.put("courtroom", planInfo.courtroom?.name)//排期法庭
                modelPlanInfo.put("judge", planInfo.judge?.name)//排期法官
                modelPlanInfo.put("secretary", planInfo.secretary?.name)//排期书记员
                modelPlanInfo.put("undertake", planInfo.undertake?.name)//当事人
                modelPlanInfo.put("startDate", DateUtil.format(planInfo.startDate as Date, "yyyy-MM-dd HH:mm:ss"))//开始时间
                modelPlanInfo.put("endDate", DateUtil.format(planInfo.endDate as Date, "yyyy-MM-dd HH:mm:ss"))//结束时间
                modelPlanInfo.put("status", planInfo.status)//排期状态
                modelPlanInfo.put("active", DataStatus.HIDE)//排期状态为隐藏
                
                //庭审信息
                modelTrialInfo.put("uid", trialInfo.uid)//庭审uid
                modelTrialInfo.put("startDate", DateUtil.format(trialInfo.startDate as Date, "yyyy-MM-dd HH:mm:ss"))//庭审开始时间
                if (trialInfo.endDate){
                    modelTrialInfo.put("endDate", DateUtil.format(trialInfo.endDate as Date, "yyyy-MM-dd HH:mm:ss"))//庭审闭庭时间
                }else{
                    modelTrialInfo.put("endDate", DateUtil.format(planInfo.endDate as Date, "yyyy-MM-dd HH:mm:ss"))//庭审闭庭时间
                }
                
                modelTrialInfo.put("status", trialInfo.status)//庭审状态
                
                /*for (def video: videoInfoList){
                    //视频信息
                    modelVideoInfo.put("uid",video.uid) // 视频uid
                    modelVideoInfo.put("channelNum",video.channelNum) // 视频通道号
                    modelVideoInfo.put("channelName",video.channelName) // 视频画面名称
                    modelVideoInfo.put("mediaType",video.mediaType? video.mediaType: "mp4") // 视频类型为mp4
                    modelVideoInfo.put("resolution",video.resolution) // 视频分辨率
                    modelVideoInfo.put("mediaStreamSize",video.mediaStreamSize) // 视频码率
                    modelVideoInfo.put("startRecTime",DateUtil.dateToStr(video.startRecTime)) // 视频开始截取时间
                    modelVideoInfo.put("endRecTime",DateUtil.dateToStr(video.endRecTime)) // 视频结束截取时间
                    modelVideoInfo.put("fileName","http://"+ trialInfo.courtroom.storeIp +":8200/"+video.fileName) // 视频播放路径
                    modelVideoInfo.put("size",video.size) // 视频大小
                    modelVideoInfo.put("length",video.length) // 视频时长
                    arrayVideoInfo.add(modelVideoInfo)
                }*/
                modelTrialInfo.put("videoInfoList",arrayVideoInfo)
                arrayTrialInfo.add(modelTrialInfo)
                modelPlanInfo.put("trialInfoList", arrayTrialInfo)
                modelData.put("planInfo", modelPlanInfo)
                arrayData.add(modelData)
                //将数据发送
                def postUrl = showVideoUrl + "/api/savePlan"
                def result = HttpUtil.postToJson(postUrl, arrayData)
                if (result.code == 1){
                    log.info("异步对接执行开庭对接点播总平台系统---执行失败！ 错误信息：${result.data}")
                    return 
                }
                log.info("异步对接执行开庭对接点播总平台系统---执行结束！")
            }catch(e){
                e.printStackTrace()
                def msg = "[ShowVideoPlatformService.pushData] 异步对接执行开庭对接点播总平台系统---失败\n错误信息[${e.message}]"
                log.error("[ShowVideoPlatformService.pushData]${msg}")
            }
            
        }
    }

    /**
     * 通知点播平台休、闭庭
     */
    def pushSvClose(long trialInfoId){
        //只有深圳需要对接点播总平台系统
        def showVideoUrl = Dict.findByCode("ShowVideoPlatform")?.val
        if (grailsApplication.config.getProperty("tc.deployPlace") == "shenzhen" && showVideoUrl) {
            try {
                log.info("异步对接执行休闭庭对接点播总平台系统---开始执行！")
                def trialInfo = TrialInfo.get(trialInfoId)                
                def modelTrialInfo = [:]
                //庭审信息
                modelTrialInfo.put("uid", trialInfo.uid)//庭审uid
                modelTrialInfo.put("startDate", DateUtil.format(trialInfo.startDate as Date, "yyyy-MM-dd HH:mm:ss"))//庭审开始时间
                modelTrialInfo.put("endDate", DateUtil.format(trialInfo.endDate as Date, "yyyy-MM-dd HH:mm:ss"))//庭审闭庭时间
                modelTrialInfo.put("status", trialInfo.status)//庭审状态
                modelTrialInfo.put("active", trialInfo.active)//庭审数据状态
                //将数据发送
                def postUrl = showVideoUrl + "/api/saveTrial"
                def result = HttpUtil.postToJson(postUrl, modelTrialInfo)
                if (result.code == 1){
                    log.info("异步对接执行休闭庭对接点播总平台系统---执行失败！ 错误信息：${result.data}")
                    return
                }
                log.info("异步对接执行休闭庭对接点播总平台系统---执行结束！")
            }catch(e){
                e.printStackTrace()
                def msg = "[ShowVideoPlatformService.pushSvClose] 异步对接执行休闭庭对接点播总平台系统---失败\n错误信息[${e.message}]"
                log.error("[ShowVideoPlatformService.pushSvClose]${msg}")
            }

        } 
    }

    /**
     * 通知点播平台视频数据
     */
    def pushSvVideo(long trialInfoId, long videoInfoId){
        //只有深圳需要对接点播总平台系统
        def showVideoUrl = Dict.findByCode("ShowVideoPlatform")?.val
        if (grailsApplication.config.getProperty("tc.deployPlace") == "shenzhen" && showVideoUrl) {
            try {
                log.info("异步通知点播平台视频数据---开始执行！")
                def trialInfo = TrialInfo.get(trialInfoId)
                def modelVideoInfo = [:]
                def arrayVideoInfo = []
                def video = VideoInfo.get(videoInfoId)
                //视频信息
                modelVideoInfo.put("uid",video.uid) // 视频uid
                modelVideoInfo.put("trialInfoUid", trialInfo.uid) // 庭审uid
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
                //将数据发送
                def postUrl = showVideoUrl + "/api/saveVideoList"
                def result = HttpUtil.postToJson(postUrl, arrayVideoInfo)
                if (result.code == 1){
                    log.info("异步通知点播平台视频数据---执行失败！ 错误信息：${result.data}")
                    return
                }
                log.info("异步通知点播平台视频数据---执行结束！")
            }catch(e){
                e.printStackTrace()
                def msg = "[ShowVideoPlatformService.pushSvVideo] 异步通知点播平台视频数据---失败\n错误信息[${e.message}]"
                log.error("[ShowVideoPlatformService.pushSvVideo]${msg}")
            }

        }
    }

    /**
     * 转移视频数据时通知点播平台视频数据修改对应路径
     */
    def pushSvVideoUrl(long videoInfoId){
        //只有深圳需要对接点播总平台系统
        def showVideoUrl = Dict.findByCode("ShowVideoPlatform")?.val
        if (grailsApplication.config.getProperty("tc.deployPlace") == "shenzhen" && showVideoUrl) {
            try {
                log.info("异步通知点播平台视频，转移视频后修改对应路径---开始执行！")
                def modelVideoInfo = [:]
                def arrayVideoInfo = []
                def video = VideoInfo.get(videoInfoId)
                //视频信息
                modelVideoInfo.put("uid",video.uid) // 视频uid
                modelVideoInfo.put("fileName","http://"+ video.trialInfo.courtroom.storeIp +":8200/"+video.fileName) // 视频播放路径
                arrayVideoInfo.add(modelVideoInfo)
                //将数据发送
                def postUrl = showVideoUrl + "/api/saveVideoUrlList"
                def result = HttpUtil.postToJson(postUrl, arrayVideoInfo)
                if (result.code == 1){
                    log.info("异步通知点播平台视频，转移视频后修改对应路径---执行失败！ 错误信息：${result.data}")
                    return
                }
                log.info("异步通知点播平台视频，转移视频后修改对应路径---执行结束！")
            }catch(e){
                e.printStackTrace()
                def msg = "[ShowVideoPlatformService.pushSvVideoUrl] 异步通知点播平台视频，转移视频后修改对应路径---失败\n错误信息[${e.message}]"
                log.error("[ShowVideoPlatformService.pushSvVideoUrl]${msg}")
            }

        }
    }
}
