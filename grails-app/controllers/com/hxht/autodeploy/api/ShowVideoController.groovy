package com.hxht.autodeploy.api

import com.hxht.techcrt.Resp
import com.hxht.techcrt.court.TrialInfo
import com.hxht.techcrt.enums.RespType

/**
 * 2021.05.10 >>> 为东软提供直播和点播接口 daniel
 */
class ShowVideoController {

    ShowVideoService showVideoService

    /**
     * 直播页面数据，返回直播页面
     */
    def zhibo() {
        def result = showVideoService.zhibo(params)
        if (result == "排期不存在") {
            render Resp.toJson(RespType.DATA_NOT_EXIST, "案号对应排期不存在")
        }
        if (result == "庭次不存在") {
            render Resp.toJson(RespType.DATA_NOT_EXIST, "庭次不存在")
        }
        result
    }

    /**
     * 点播页面数据
     * @return 返回点播页面
     */
    def dianbo() {
        def result = showVideoService.dianbo(params)
        if (result == "排期不存在") {
            render Resp.toJson(RespType.DATA_NOT_EXIST, "案号对应排期不存在")
        }
        if (result == "庭次不存在") {
            render Resp.toJson(RespType.DATA_NOT_EXIST, "庭次不存在")
        }
        result
    }

    /**
     * 视频详情
     */
    def showVideo() {
        def trialInfo = TrialInfo.get(params.long("id"))//有视频的话一定会有trial
        if (!trialInfo) {
            render Resp.toJson(RespType.FAIL)
            return
        }
        render Resp.toJson(RespType.SUCCESS, [status: trialInfo.status, video: showVideoService.showVideo(trialInfo)])
    }
}
