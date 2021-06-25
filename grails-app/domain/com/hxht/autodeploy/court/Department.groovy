package com.hxht.autodeploy.court

class Department {
    /**
     * 部门标识
     */
    String uid
    /**
     * 组织机构部门名字
     */
    String name
    /**
     * 同步数据原始id
     */
    String syncId

    //厂商
    String manufacturer

    //同步主键
    String synchronizationId

    /**
     * 所有的子节点
     */
    static hasMany = [depts: Department]
    /**
     * 父节点
     */
    static belongsTo = [parent: Department]

    static constraints = {
        uid unique: true, maxSize: 70
        syncId nullable: true, maxSize: 120
        manufacturer nullable: true
        synchronizationId nullable: true, maxSize: 120
        name maxSize: 100
        parent nullable: true
    }

    static mapping = {
        uid comment: "部门唯一标识"
        name comment: "名字"
        syncId comment: "同步数据原始id"
        manufacturer comment: "厂商"
        synchronizationId comment: "同步数据主键"
        depts comment: "部门所有的子节点"
        parent comment: "部门父节点"
        comment "内部组织机构部门表。"
    }
}
