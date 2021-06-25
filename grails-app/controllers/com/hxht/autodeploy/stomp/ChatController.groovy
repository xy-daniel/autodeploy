package com.hxht.autodeploy.stomp

import com.hxht.techcrt.api.ApiMoveVideoService
import com.hxht.techcrt.api.ApiOfflinePlanService
import com.hxht.techcrt.court.plan.ChatRecordService
import com.hxht.techcrt.court.plan.PlanService
import com.hxht.techcrt.utils.Base64Utils
import com.hxht.techcrt.utils.UUIDGenerator
import grails.converters.JSON
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo

/**
 * 即时通讯接口
 * 2021.06.16 >>> 发送信息时初始化聊天记录 daniel
 */
class ChatController {

    ChatRecordService chatRecordService
    ApiOfflinePlanService apiOfflinePlanService
    PlanService planService
    ApiMoveVideoService apiMoveVideoService

    /**
     * 接收聊天数据
     * @param msg 信息
     * @return 信息转发
     */
    @MessageMapping("/chat")
    @SendTo("/topic/chat")
    protected chat(String msg) {
        def json = JSON.parse(URLDecoder.decode(Base64Utils.decode(msg), "UTF-8"))
        //初始化聊天记录
        chatRecordService.getChatRecord(json.planId as long)
        def uuid = UUIDGenerator.nextUUID()
        def addData = [
                uuid       : uuid,
                time       : json.time,
                userName   : json.userName,
                chatContext: json.chatContext
        ]
        def sendData = [
                uuid       : uuid,
                time       : json.time,
                planId     : json.planId,
                userName   : json.userName,
                chatContext: json.chatContext
        ]
        //数据存储
        chatRecordService.addMessage(json.planId as Long, addData)
        return (sendData as JSON) as String
    }

    @MessageMapping("/revideo")
    @SendTo("/topic/revideo")
    protected reloadVideo(String data) {
        def json = JSON.parse data
        def id = json.id
        def uuid = json.uuid
        def version = json.version
        def path = json.path
        return apiOfflinePlanService.reVideo(id, uuid, version, path)
    }

    @MessageMapping("/indexVideo")
    @SendTo("/topic/indexVideo")
    protected indexVideo(String data) {
        return apiMoveVideoService.moveVideo()
    }

    @MessageMapping("/deleteVideo")
    @SendTo("/topic/deleteVideo")
    protected deleteVideo(String data) {
        def json = JSON.parse data
        def archives = json.archives
        return planService.deleteVideo(archives)
    }

    /**
     * 书记员电脑转发用接口
     */
    @MessageMapping("/cmd")
    @SendTo("/topic/cmd")
    protected cmd(String msg) {
        return msg
    }

}
