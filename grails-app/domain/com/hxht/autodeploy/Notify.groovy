package com.hxht.autodeploy

import com.hxht.techcrt.court.Employee

class Notify {

    /**
     * 操作者
     */
    Employee operator

    /**
     * 是否已读
     * 0未读
     * 1已读
     */
    Integer is_read

    /**
     * 创建时间
     */
    Date dateCreated

    /**
     * 详细信息
     */
    String remark

    /**
     * 数据状态
     */
    Integer active

    static constraints = {
        operator nullable: true
        is_read nullable: true
        remark nullable: true, maxSize: 512
    }
    static mapping = {
        version false
        operator comment: "操作者的id"
        is_read comment: "是否已读  0未读,1已读"
        dateCreated comment: "创建时间"
        remark comment: "备注 记录详细信息"
        active comment: "数据状态"
        comment "消息提醒表，保存需要提醒用户的信息。"
    }
}
