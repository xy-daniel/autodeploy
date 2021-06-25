package com.hxht.autodeploy
/**
 * 系统登陆记录表
 */
class LogLogin {
    /**
     * 系统用户id
     */
    Long userId
    /**
     * 明细
     */
    String message
    /**
     * 创建时间
     */
    Date dateCreated

    static constraints = {
        message nullable: true
    }
    static mapping = {
        userId comment: "系统用户id"
        message comment: "明细"
        dateCreated comment: "创建时间"
        comment "系统登陆记录表表。"
    }
}
