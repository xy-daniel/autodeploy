package com.hxht.autodeploy.court

import grails.databinding.BindingFormat


class TrialInfo {
    /**
     * 审判标识
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
     * 开庭时间
     */
    @BindingFormat('yyyy/MM/dd HH:mm:ss')
    Date startDate
    /**
     * 结束时间
     */
    @BindingFormat('yyyy/MM/dd HH:mm:ss')
    Date endDate
    /**
     * 庭审状态
     */
    Integer status
    /**
     * 庭审笔录的pdf
     */
    String note
    /**
     * 庭审上传的word
     */
    String noteWord
    /**
     * 批注图片地址
     */
    String comment
    /**
     * 数据状态
     */
    Integer active
    /**
     * 创建时间
     */
    Date dateCreated
    /**
     * 修改时间
     */
    Date lastUpdated

    /**
     * 是否使用语音识别
     */
    Integer isCourtSpeech

    /**
     * 庭次对接厂商
     */
    String manufacturer

    /**
     * 庭次同步主键
     */
    String synchronizationId

    static belongsTo = [planInfo: PlanInfo]

    static hasMany = [videoInfo: VideoInfo]

    static constraints = {
        uid unique: true, maxSize: 70
        judge nullable: true
        secretary nullable: true
        endDate nullable: true
        courtroom nullable: true
        note nullable: true
        noteWord nullable: true
        comment nullable: true
        isCourtSpeech nullable: true
        manufacturer nullable: true
        synchronizationId nullable: true
    }
    static mapping = {
        uid comment: "审判唯一标识", index: true
        courtroom comment: "法庭", index: true
        judge comment: "法官", index: true
        secretary comment: "书记员", index: true
        startDate comment: "开庭时间"
        endDate comment: "结束时间"
        status comment: "庭审状态", index: true
        note sqlType: "text", comment: "庭审笔录签名pdf"
        noteWord sqlType: "text", comment: "庭审笔录word"
        active comment: "数据状态"
        dateCreated comment: "创建时间"
        lastUpdated comment: "修改时间"
        planInfo comment: "排期"
        videoInfo comment: "视频"
        isCourtSpeech comment: "法庭是否使用语音识别标识"
        comment sqlType: "text",comment: "批注图片地址"
        manufacturer comment: "庭次对接厂商"
        synchronizationId comment: "庭次同步主键", index: true
        comment "审判表，保存审判基本信息。"
    }
}
