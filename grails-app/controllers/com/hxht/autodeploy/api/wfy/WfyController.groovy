package com.hxht.autodeploy.api.wfy

import com.hxht.techcrt.Resp
import com.hxht.techcrt.court.PlanInfo
import com.hxht.techcrt.court.TrialInfo
import com.hxht.techcrt.enums.RespType
import com.hxht.techcrt.wfy.WfyService

/**
 * 为书记员提供对接中国移动微法院互联网开庭平台的API
 */
class WfyController {
    WfyService wfyService
    /**
     * 入会
     * 在选择完排期后调用，拉取庭审网关入会
     * id ： planId
     */
    def putIntoRoom() {
        def flag = grailsApplication.config.getProperty('weifayuan.flag') as Integer
        if (flag == 1) {
            def planId = params.long("id")
            log.info("[WfyController.putIntoRoom]书记员微法院入会，数据[${planId}]")
            def planInfo = PlanInfo.get(planId)
            if (!planInfo) {
                log.error("Error [WfyController.putIntoRoom]书记员微法院入会失败，排期数据不存在。数据[${planId}]")
                render Resp.toJson(RespType.DATA_NOT_EXIST, "排期数据不存在。")
                return
            }
            def result = wfyService.putIntoRoom(planInfo)
            if (result) {
                log.info("[WfyController.putIntoRoom]书记员微法院入会成功，数据[${planId}]")
                render Resp.toJson(RespType.SUCCESS)
            } else {
                log.error("Error [WfyController.putIntoRoom]书记员微法院入会失败，通知中国移动微法院互联网开庭平台入会失败。数据[${planId}]")
                render Resp.toJson(RespType.FAIL, "通知中国移动微法院互联网开庭平台入会失败。")
            }
        } else {
            render Resp.toJson(RespType.FAIL, "接口功能未开启。")
        }
    }

    /**
     * 发送笔录，等待参与人签名
     * 发送闭庭信息之前发送笔录，调用前要保证笔录已经上传到服务器。
     * id ： trialId
     */
    def sendClerkRecord(){
        def flag = grailsApplication.config.getProperty('weifayuan.flag') as Integer
        if (flag == 1) {
            def trialId = params.long("id")
            log.info("[WfyController.sendClerkRecord]书记员微法院发送笔录数据，数据[${trialId}]")
            def trialInfo = TrialInfo.get(trialId)
            if (!trialInfo) {
                log.error("Error [WfyController.sendClerkRecord]书记员微法院发送笔录数据失败，庭审数据不存在。数据[${trialId}]")
                render Resp.toJson(RespType.DATA_NOT_EXIST, "庭审数据不存在。")
                return
            }
            def result = wfyService.sendClerkRecord(trialInfo)
            if (result) {
                log.info("[WfyController.sendClerkRecord]书记员微法院发送笔录数据成功，数据[${trialId}]")
                render Resp.toJson(RespType.SUCCESS)
            } else {
                log.error("Error [WfyController.sendClerkRecord]书记员微法院发送笔录数据失败，通知中国移动微法院互联网开庭平台推送笔录数据失败。数据[${trialId}]")
                render Resp.toJson(RespType.FAIL, "通知中国移动微法院互联网开庭平台推送笔录数据失败。")
            }
        }else {
            render Resp.toJson(RespType.FAIL, "接口功能未开启。")
        }
    }


}
