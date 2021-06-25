package com.hxht.autodeploy.court

class ChatRecord {
    /**
     * 排期主键
     */
    Long planId

    /**
     * 聊天记录
     */
    String chatRecord

    /**
     * 创建时间
     */
    Date dateCreated
    /**
     * 修改时间
     */
    Date lastUpdated


    static constraints = {
    }

    static mapping = {
        planId comment: "排期主键"
        chatRecord sqlType: "text", comment: "聊天记录"
        dateCreated comment: "创建时间"
        lastUpdated comment: "修改时间"
        comment "聊天记录表"
    }

}
