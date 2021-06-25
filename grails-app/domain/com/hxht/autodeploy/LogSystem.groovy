package com.hxht.autodeploy

class LogSystem {
    /**
     * 日志等级
     */
    String level
    /**
     * 日志明细
     */
    String message
    /**
     * 创建时间
     */
    Date dateCreated

    static constraints = {
        level nullable: true, maxSize: 128
        message nullable: true
    }
    static mapping = {
        version false
        level comment: "日志等级"
        message sqlType: "text", comment: "日志信息"
        dateCreated comment: "创建时间"
        comment "日志表，保存需要的系统操作日志信息。"
    }
}
