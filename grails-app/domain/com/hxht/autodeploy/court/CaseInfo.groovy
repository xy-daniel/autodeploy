package com.hxht.autodeploy.court

import grails.databinding.BindingFormat

class CaseInfo {
    /**
     * 案件标识
     */
    String uid
    /**
     * 案号
     */
    String archives
    /**
     * 案件名称
     */
    String name
    /**
     * 案件类型
     */
    CaseType type
    /**
     * 案件概要
     */
    String summary
    /**
     * 案件明细
     */
    String detail
    /**
     * 原告
     */
    String accuser
    /**
     * 原告律师
     */
    String prosecutionCounsel
    /**
     * 被告
     */
    String accused
    /**
     * 被告律师
     */
    String counselDefence
    /**
     * 案件所属部门
     */
    Department department
    /**
     * 立案日期
     */
    @BindingFormat('yyyy/MM/dd HH:mm')
    Date filingDate
    /**
     * 数据状态
     */
    Integer active
    /**
     * 同步数据原始id
     */
    String syncId

    //厂商
    String manufacturer

    //同住主键
    String synchronizationId
    /**
     * 创建时间
     */
    Date dateCreated
    /**
     * 修改时间
     */
    Date lastUpdated

    /**
     * 数据同步保存数据用（案由）
     */
    String caseCause

    static hasMany = [planInfo: PlanInfo]

    static constraints = {
        uid unique: true, maxSize: 70
        archives unique: true, maxSize: 200
        name nullable: true, maxSize: 500
        department nullable: true
        type nullable: true
        summary nullable: true, maxSize: 2000
        detail nullable: true
        accuser nullable: true, maxSize: 2000
        prosecutionCounsel nullable: true, maxSize: 200
        accused nullable: true, maxSize: 2000
        counselDefence nullable: true, maxSize: 200
        filingDate nullable: true
        syncId nullable: true, maxSize: 120
        manufacturer nullable: true
        synchronizationId nullable: true, maxSize: 120
        caseCause nullable: true, maxSize: 200
    }

    static mapping = {
        uid comment: "案件唯一标识", index: true
        archives comment: "案号", index: true
        name comment: "案件名称", index: true
        type comment: "案件类型CODE", index: true
        summary comment: "案件概要"
        detail comment: "案件明细"
        accuser comment: "原告"
        prosecutionCounsel comment: "原告律师"
        accused comment: "被告"
        counselDefence comment: "被告律师"
        department comment: "案件所属部门"
        filingDate comment: "立案日期"
        active comment: "数据状态"
        syncId comment: "同步数据原始id"
        manufacturer comment: "厂商"
        synchronizationId comment: "数据同步主键", index:true
        dateCreated comment: "创建时间"
        lastUpdated comment: "修改时间"
        caseCause comment: "案由（数据同步保存用）"
        planInfo comment: "排期"
        comment "案件表，保存案件基本信息。"
    }
}
