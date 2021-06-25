package com.hxht.autodeploy
/**
 * 系统字典
 */
class AnYou {
    /**
     * 案由编号
     */
    String aybh
    /**
     * 案由名称
     */
    String aymc
    /**
     * ayfdm
     */
    String ayfdm

    /**
     * 创建时间
     */
    Date dateCreated
    /**
     * 修改时间
     */
    Date lastUpdated

    static constraints = {
        aymc nullable: true
        ayfdm nullable: true
        aybh nullable: true
    }
    static mapping = {
        autoTimestamp(true)
        aymc comment: "案由名称"
        ayfdm comment: "案由fdm"
        aybh comment: "案由编号"
        dateCreated comment: "创建时间"
        lastUpdated comment: "修改时间"
        comment "案由表。"
    }

}
