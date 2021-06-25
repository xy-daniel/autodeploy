package com.hxht.autodeploy.court

import grails.databinding.BindingFormat

/**
 * 2021.06.11 >>> 增加开庭模式字段 daniel
 */
class PlanInfo {
    /**
     * 排期标识
     */
    String uid
    /**
     * 法庭id
     */
    Courtroom courtroom
    /**
     * 法官id
     */
    Employee judge
    /**
     * 书记员id
     */
    Employee secretary
    /**
     * 承办人
     */
    Employee undertake
    /**
     * 合议庭成员列表
     */
    List<Collegial> collegial
    /**
     * 计划开庭时间
     */
    @BindingFormat('yyyy/MM/dd HH:mm')
    Date startDate
    /**
     * 计划闭庭时间
     */
    @BindingFormat('yyyy/MM/dd HH:mm')
    Date endDate
    /**
     * 庭审状态
     */
    Integer status
    /**
     * 是否是远程提讯
     */
    Integer distanceArraigned
    /**
     * 远程提讯签名人员，逗号分割
     */
    String distanceSignature
    /**
     * 数据状态
     */
    Integer active
    /**
     * 同步数据原始id
     */
    String syncId
    /**
     * 是否允许直播
     */
    Byte allowPlay
    /**
     * 微法院排期
     */
    Integer wfyPlan
    /**
     * 开庭模式 0:正常  1:互联网
     */
    Integer model

    //厂商
    String manufacturer

    //同步主键
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
     * 需要并案处理字段
     */
    String combinedPlan

    static belongsTo = [caseInfo: CaseInfo]

    static hasMany = [trialInfo: TrialInfo, collegial: Collegial]

    static constraints = {
        uid unique: true, maxSize: 70
        courtroom nullable: true
        judge nullable: true
        secretary nullable: true
        undertake nullable: true
        wfyPlan nullable: true
        distanceArraigned nullable: true
        distanceSignature nullable: true, maxSize: 512
        syncId nullable: true, maxSize: 120
        model nullable: true, maxSize: 10
        manufacturer nullable: true
        synchronizationId nullable: true, maxSize: 120
        combinedPlan nullable: true, maxSize: 70
        allowPlay nullable: true
    }
    static mapping = {
        author lazy: false
        uid comment: "排期唯一标识", index: true
        courtroom comment: "法庭", index:true
        judge comment: "法官id", index:true
        secretary comment: "书记员", index:true
        undertake comment: "承办人"
        collegial comment: "合议庭成员", index:true
        startDate comment: "计划开庭时间", index:true
        endDate comment: "计划闭庭时间", index:true
        status comment: "庭审状态", index: true
        distanceArraigned comment: "是否是远程提讯"
        distanceSignature comment: "远程提讯签名人员，逗号分割"
        active comment: "数据状态"
        wfyPlan comment: "1微法院排期，0普通排期"
        syncId comment: "同步数据原始id"
        model comment: "开庭模式 0:正常  1:互联网", defaultValue: "0"
        manufacturer comment: "厂商"
        synchronizationId comment: "数据同步主键", index:true
        allowPlay comment: "是否允许直播"
        dateCreated comment: "创建时间"
        lastUpdated comment: "修改时间"
        caseInfo comment: "案件", index:true
        trialInfo comment: "庭审"
        combinedPlan comment: "是否需要并案", index: true
        comment "排期表，保存排期基本信息。"
    }
}
