package com.hxht.autodeploy.court

import grails.databinding.BindingFormat

class VideoInfo {
    /**
     * 视频标识
     */
    String uid
    /**
     * 通道号
     */
    String channelNum
    /**
     * 通道名称
     */
    String channelName
    /**
     * 媒体类型
     */
    String mediaType
    /**
     * 分辨率
     */
    String resolution
    /**
     * 码率
     */
    String mediaStreamSize
    /**
     * 开始录像时间
     */
    @BindingFormat('yyyy/MM/dd HH:mm:ss')
    Date startRecTime
    /**
     * 结束录像时间
     */
    @BindingFormat('yyyy/MM/dd HH:mm:ss')
    Date endRecTime
    /**
     * 视频文件名称
     */
    String fileName
    /**
     * 文件大小
     */
    String size
    /**
     * 录像时长 秒
     */
    Integer length
    /**
     * 数据状态
     */
    Integer active

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

    static belongsTo = [trialInfo: TrialInfo]

    static constraints = {
        uid unique: true, maxSize: 70
        channelNum nullable: true
        channelName nullable: true
        mediaType nullable: true
        resolution nullable: true
        mediaStreamSize nullable: true
        startRecTime nullable: true
        endRecTime nullable: true
        fileName nullable: true
        size nullable: true
        length nullable: true
        manufacturer nullable: true
        synchronizationId nullable: true
    }
    static mapping = {
        uid comment: "视频唯一标识", index: true
        channelNum comment: "通道号"
        channelName comment: "通道名称"
        mediaType comment: "媒体类型"
        resolution comment: "分辨率"
        mediaStreamSize comment: "码率"
        startRecTime comment: "开始录像时间"
        endRecTime comment: "结束录像时间"
        fileName comment: "文件名称"
        size comment: "文件大小"
        length comment: "录像时长 秒"
        active comment: "数据状态"
        dateCreated comment: "创建时间"
        lastUpdated comment: "修改时间"
        trialInfo comment: "审判"
        comment "视频表，保存视频基本信息。"
    }


    @Override
    String toString() {
        return "VideoInfo{" +
                "id=" + id +
                ", version=" + version +
                ", trialInfo=" + trialInfo +
                ", uid='" + uid + '\'' +
                ", channelNum='" + channelNum + '\'' +
                ", channelName='" + channelName + '\'' +
                ", mediaType='" + mediaType + '\'' +
                ", resolution='" + resolution + '\'' +
                ", mediaStreamSize='" + mediaStreamSize + '\'' +
                ", startRecTime=" + startRecTime +
                ", endRecTime=" + endRecTime +
                ", fileName='" + fileName + '\'' +
                ", size='" + size + '\'' +
                ", length=" + length +
                ", active=" + active +
                '}'
    }
}
