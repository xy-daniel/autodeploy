package com.hxht.autodeploy

class Alarm {
    /**
     * 报警信息
     */
    String alarmInfo

    /**
     * 报警类型
     */
    Integer alarmType

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
        autoTimestamp(true)
        alarmInfo comment: "报警信息"
        alarmType comment: "报警类型（1：内存；2：磁盘； 3：cpu；4：高网络占用）"
        dateCreated comment: "创建时间"
        lastUpdated comment: "修改时间"
        comment "内存/磁盘空间报警表"
    }
}
