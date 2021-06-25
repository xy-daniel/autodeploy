package com.hxht.autodeploy.api

import com.hxht.techcrt.DataStatus
import com.hxht.techcrt.PlanStatus
import com.hxht.techcrt.Resp
import com.hxht.techcrt.court.TrialInfo
import com.hxht.techcrt.court.VideoInfo
import com.hxht.techcrt.enums.RespType
import com.hxht.techcrt.utils.http.HttpUtil
import grails.core.GrailsApplication

/**
 * 核查系统接口
 */
class ApiVerificationController {
    GrailsApplication grailsApplication

    /**
     * 异步对接执行老数据已有闭庭视频分析模块
     */
    def planDataToVc() {
        if (request.method == "GET") {
            //分十条数据进行返回
            try {
                def begin = 0
                while (true) {
                    def trial = this.planlist(begin)
                    if (trial) {
                        begin = begin + 10
                    } else {
                        break
                    }
                }
                render Resp.toJson(RespType.SUCCESS)
            } catch (e) {
                e.printStackTrace()
                def msg = "[ApiVerificationController.planDataToVc] 数据请求出错\n错误信息[${e.message}]"
                log.error("[ApiVerificationController.planDataToVc]${msg}")
            }
        }
    }

    /**
     * 获取闭庭排期数据接口
     */
    def planlist(Integer begin) {
        def trialInfoList = TrialInfo.findAllByStatusAndActive(PlanStatus.CLOSED, DataStatus.SHOW, [max: 10, offset: begin])
        //分页获取闭庭排期数据 否则数据量过大容易卡死
        if (!trialInfoList) {
            return false
        }
        def modelList = []
        for (def trialInfo : trialInfoList) {
            def model = [:]
            model.put("uid", trialInfo.uid)//排期uid
            model.put("courtroomName", trialInfo.courtroom?.name)//法庭名称
            model.put("judge", trialInfo.judge?.name)//法官
            model.put("secretary", trialInfo.secretary?.name)//书记员
            model.put("archives", trialInfo.planInfo.caseInfo?.archives)//案件编号
            model.put("archivesName", trialInfo.planInfo.caseInfo?.name)//案件名称
            model.put("startDate", trialInfo.planInfo.startDate?.format('yyyy/MM/dd HH:mm:ss'))//排期结束时间
            model.put("endDate", trialInfo.planInfo.endDate?.format('yyyy/MM/dd HH:mm:ss'))//排期开始时间
            model.put("status", trialInfo.status)//排期状态
            model.put("plaintiff", trialInfo.planInfo.caseInfo?.accuser)//原告
            model.put("defendant", trialInfo.planInfo.caseInfo?.accused)//被告
            model.put("recordDate", trialInfo.planInfo.caseInfo?.filingDate?.format('yyyy/MM/dd HH:mm:ss'))//立案日期

            def videoInfoList = VideoInfo.findAllByTrialInfo(trialInfo, [sort: "startRecTime", order: "asc"])
            def trialVideoList = []
            for (def i = 0; i < videoInfoList.size(); i++) {
                def videoInfo = videoInfoList[i]
                if (videoInfo.channelName && (videoInfo.channelName.contains("合成") || videoInfo.channelName.contains("法官"))) {
                    trialVideoList.add([
                            videoAddress: "http://${trialInfo.courtroom.storeIp}:8200/${videoInfo.fileName}"
                    ])
                }
            }
            model.put("videoInfo", trialVideoList)//视频存储地址
            model.put("deviceIp", trialInfo.courtroom?.deviceIp)//庭审设备地址
            modelList.add(model)
        }
        def postUrl = "http://" + grailsApplication.config.getProperty('verification.vc_ip_port') + "/vc/api/closedCourtOrFilingInfo"
        HttpUtil.postToJson(postUrl, modelList)
        true
    }
}
