package com.hxht.autodeploy.stomp

import com.hxht.techcrt.court.manager.VideoRecordService
import com.hxht.techcrt.enums.PlayStatus
import grails.converters.JSON
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo

/**
 * Stomp方式由管理员断开用户观看直播的连接
 */
class VideoController {

    VideoRecordService videoRecordService

    /**
     * 断开用户观看连接
     */
    @MessageMapping("/stopC")
    @SendTo("/topic/stopC")
    protected stopC(String msg) {
        def vr = videoRecordService.getVr(msg as Long)
        def sendData
        if (vr.playStatus == PlayStatus.ADMIN_DISCONNECT){
            sendData = [
                    planId: vr.planId,
                    userId: vr.userId,
                    flag: 1
            ]
            return (sendData as JSON) as String
        }else{
            sendData = [
                    planId: vr.planId,
                    userId: vr.userId,
                    flag: 0
            ]
        }
        return (sendData as JSON) as String
    }

    /**
     * 用户进入排期详情页面
     */
    @MessageMapping("/connect")
    @SendTo("/topic/connect")
    protected connect(String msg) {
        return msg
    }
}
