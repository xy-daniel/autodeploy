package com.hxht.autodeploy.court.plan

import com.hxht.techcrt.court.ChatRecord
import grails.converters.JSON
import grails.gorm.transactions.Transactional
import groovy.json.JsonSlurper

@Transactional
class ChatRecordService {

    /**
     * 添加聊天记录
     * @param planId
     * @param addDate
     * @return
     */
    def addMessage(Long planId, def addDate) {
        def chatRecord = ChatRecord.findByPlanId(planId)

        def chatRecordMap = new JsonSlurper().parseText(chatRecord.chatRecord) as HashMap
        chatRecordMap.chatRecord.add(addDate)

        chatRecord.chatRecord = (chatRecordMap as JSON).toString()
        chatRecord.save(flush: true)
        if (chatRecord.hasErrors()) {
            log.info("保存聊天记录信息出错 chatRecord PlanService.addMessage")
            throw new RuntimeException()
        }
    }

    /**
     * 获取聊天记录
     * @param planInfo 排期信息
     * @return 聊天记录Map对象
     */
    def getChatRecord(Long planId) {
        //根据排期主键查询聊天记录
        def chatRecord = ChatRecord.findByPlanId(planId)
        //如果不存在
        def chatRecordMap
        if (!chatRecord){
            chatRecordMap = [
                    chatRecord: []//聊天记录
            ]
            def record = new ChatRecord(
                    planId: planId,
                    chatRecord: (chatRecordMap as JSON).toString()
            )
            record.save(flush: true)
            if (record.hasErrors()) {
                log.info("保存聊天记录信息出错 record PlanService.getChatRecord")
                throw new RuntimeException()
            }
        }else{
            chatRecordMap = new JsonSlurper().parseText(chatRecord.chatRecord) as HashMap
        }
        chatRecordMap
    }

}
