package com.hxht.autodeploy.async

import com.hxht.techcrt.court.TrialInfo
import com.hxht.techcrt.court.VideoInfo
import com.hxht.techcrt.court.manager.SystemController
import com.hxht.techcrt.utils.http.HttpUtil
import grails.converters.JSON
import grails.core.GrailsApplication
import grails.gorm.transactions.Transactional

/**
 * 视频核查
 */
@Transactional
class ApiVerificationService {
    GrailsApplication grailsApplication
    /**
     * 异步对接执行开庭视频分析模块
     */
    def openToVc(Long trialInfoId) {
        //深圳罗湖区法院用的对接视频分析模块 目前只有深圳罗湖区法院在用
        if (SystemController.currentCourt.ext3 == "J31") {
            try {
                log.info("异步对接执行开庭视频分析模块开始执行！")
                def trialInfo = TrialInfo.get(trialInfoId)
                //将trial中的对象取出 否则需要在表中禁止lazy加载,为了不影响整体性能所以在此取出
                def courtroomName = trialInfo?.courtroom?.name
                def judge = trialInfo?.judge?.name
                def secretary = trialInfo?.secretary?.name
                def caseInfo = trialInfo?.planInfo?.caseInfo
                def model = [:]
                model.put("uid", trialInfo.uid)//排期uid
                model.put("courtroomName", courtroomName)//法庭名称
                model.put("judge", judge)//法官
                model.put("secretary", secretary)//书记员
                model.put("archives", caseInfo?.archives)//案件编号
                model.put("archivesName", caseInfo?.name)//案件名称
                model.put("startDate", trialInfo.startDate?.format('yyyy/MM/dd HH:mm:ss')?.toString())//排期结束时间
                model.put("endDate", trialInfo.endDate?.format('yyyy/MM/dd HH:mm:ss')?.toString())//排期开始时间
                model.put("status", trialInfo.status)//排期状态
                model.put("plaintiff", caseInfo?.accuser)//原告
                model.put("defendant", caseInfo?.accused)//被告
                model.put("recordDate", caseInfo?.filingDate?.format('yyyy/MM/dd HH:mm:ss')?.toString())//立案日期

                def videoInfoList = []
                def courtroom = trialInfo.courtroom
                def cfg = JSON.parse(courtroom.cfg)
                for (def encode : cfg.encode) {
                    if (encode.name && (encode.name.contains("合成") || encode.name.contains("法官"))) {
                        def url = "http://${courtroom.liveIp}:8791/${encode.encodeip}/${encode.number}.flv"
                        videoInfoList.add([
                                videoAddress: url
                        ])
                    }
                }
                model.put("videoInfo", videoInfoList)//视频流地址
                //将数据发送
                def postUrl = "http://" + grailsApplication.config.getProperty('verification.vc_ip_port') + "/vc/api/startCourtInfo"
                def result = HttpUtil.postToJson(postUrl, model)
                log.info("异步对接执行开庭视频分析模块执行结束！")
            }catch(e){
                e.printStackTrace()
                def msg = "[ApiVerificationService.openToVc] 异步执行视频分析开庭通知出错\n错误信息[${e.message}]"
                log.error("[ApiVerificationService.openToVc]${msg}")
            }
        }
    }

    /**
     * 异步对接执行休闭庭视频分析模块
     */
    def closeToVc(Long trialInfoId) {
        //深圳罗湖区法院用的对接视频分析模块 目前只有深圳罗湖区法院在用
        if (SystemController.currentCourt.ext3 == "J31") {
            try {
                log.info("异步对接执行休闭庭视频分析模块开始执行！")
                def trialInfo = TrialInfo.get(trialInfoId)
                def model = [:]
                model.put("uid", trialInfo.uid)//排期状态
                model.put("status", trialInfo.status)//排期状态
                //将数据发送
                def postUrl = "http://" + grailsApplication.config.getProperty('verification.vc_ip_port') + "/vc/api/closeCourtInfo"
                def result = HttpUtil.postToJson(postUrl, model)
                log.info("异步对接执行休闭庭视频分析模块执行结束！")
            }catch(e){
                e.printStackTrace()
                def msg = "[ApiVerificationService.closeToVc] 异步对接执行休闭庭视频分析模块失败\n错误信息[${e.message}]"
                log.error("[ApiVerificationService.closeToVc]${msg}")
            }
        }
    }
    /**
     * 异步对接执行视频地址存储
     */
    def videoInfoToVc(Long videoInfoId, Long trialInfoId) {
        //深圳罗湖区法院用的对接视频分析模块 目前只有深圳罗湖区法院在用
        if (SystemController.currentCourt.ext3 == "J31") {
            try {
                log.info("异步对接执行视频地址存储开始执行！")
                def videoInfo = VideoInfo.get(videoInfoId)
                def trialInfo = TrialInfo.get(trialInfoId)
                if (videoInfo.channelName && (videoInfo.channelName.contains("合成") || videoInfo.channelName.contains("法官"))) {
                    def model = [:]
                    model.put("uid", trialInfo.uid)//庭审uid
                    model.put("videoAddress", "http://${trialInfo.courtroom.storeIp}:8200/${videoInfo.fileName}")//视频地址
                    //将数据发送
                    def postUrl = "http://" + grailsApplication.config.getProperty('verification.vc_ip_port') + "/vc/api/videoAddress"
                    def result = HttpUtil.postToJson(postUrl, model)
                }
                log.info("异步对接执行视频地址存储执行结束！")
            }catch(e){
                e.printStackTrace()
                def msg = "[ApiVerificationService.videoInfoToVc] 异步对接执行视频地址存储失败\n错误信息[${e.message}]"
                log.error("[ApiVerificationService.videoInfoToVc]${msg}")
            }
        }
    }

    /**
     * 异步对接设备地址
     */
    def deviceIpToVc(String courtRoomName, String deviceIp) {
        //深圳罗湖区法院用的对接视频分析模块 目前只有深圳罗湖区法院在用
        if (SystemController.currentCourt.ext3 == "J31") {
            try {
                log.info("异步对接设备地址开始执行！")
                if (courtRoomName && deviceIp) {
                    def model = [:]
                    model.put("courtRoomName", courtRoomName)//法庭名称
                    model.put("deviceIp", deviceIp)//设备地址
                    //将数据发送
                    def postUrl = "http://" + grailsApplication.config.getProperty('verification.vc_ip_port') + "/vc/api/deviceIp"
                    def result = HttpUtil.postToJson(postUrl, model)
                }
                log.info("异步对接设备地址执行结束！")
            }catch(e){
                e.printStackTrace()
                def msg = "[ApiVerificationService.deviceIpToVc] 异步对接设备地址出错\n错误信息[${e.message}]"
                log.error("[ApiVerificationService.deviceIpToVc]${msg}")
            }
        }
    }
}