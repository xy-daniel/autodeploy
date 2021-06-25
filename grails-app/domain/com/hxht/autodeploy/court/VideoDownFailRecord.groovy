package com.hxht.autodeploy.court

/**
 * 视频下载失败记录
 */
class VideoDownFailRecord {
    //所属庭审
    TrialInfo trialInfo

    //视频信息,包括
    String videoMessage

    String taskId

    //开始录制时间
    Date startRecTime

    boolean handlerSuccess

    //创建时间
    Date dateCreated

    //修改时间
    Date lastUpdated

    Integer ver

    static constraints = {
        videoMessage nullable: true
        taskId nullable: true
        startRecTime nullable: true
    }

    static mapping = {
        trialInfo comment: "视频下载所属庭审"
        videoMessage sqlType: "text", comment: "视频信息"
        taskId comment: "视频查询失败时记录的taskId"
        startRecTime comment: "视频开始录制时间"
        dateCreated comment: "记录生成时间"
        lastUpdated comment: "记录更新时间"
        handlerSuccess comment: "是否处理成功"
        ver comment: "庭审主机版本"
        comment: "视频下载失败记录表"
    }

}
