package com.hxht.autodeploy.stomp

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.simp.annotation.SendToUser

/**
 * Stomp方式对接客户端
 */
class CourtController {
    CourtService courtService

    /**
     * 书记员电脑心跳连接
     */
    @MessageMapping("/beat")
    @SendToUser("/queue/beat")
    protected beat(SimpMessageHeaderAccessor sha, String msg) {
        println msg
        courtService.test(sha)
        return "返回消息1"
    }


}
