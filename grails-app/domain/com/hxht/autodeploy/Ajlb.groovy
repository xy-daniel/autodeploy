package com.hxht.autodeploy
/**
 * 系统字典
 */
class Ajlb {
    /**
     * 案件类别编号
     */
    String ajlbbh
    /**
     *
     */
    String spcxbh
    /**
     * 案件类别名称
     */
    String ajlbmc
    /**
     * 名称
     */
    String spcxmc

    /**
     * 创建时间
     */
    Date dateCreated
    /**
     * 修改时间
     */
    Date lastUpdated

    static constraints = {
        ajlbbh nullable: true
        spcxbh nullable: true
        ajlbmc nullable: true
        spcxmc nullable: true
    }
    static mapping = {
        autoTimestamp(true)
        ajlbbh comment: "案件类别编号"
        spcxbh comment: ""
        ajlbmc comment: "案件类别名称"
        spcxmc comment: ""
        dateCreated comment: "创建时间"
        lastUpdated comment: "修改时间"
        comment "案由表。"
    }

}
