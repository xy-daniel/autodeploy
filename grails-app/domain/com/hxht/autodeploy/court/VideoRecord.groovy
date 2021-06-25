package com.hxht.autodeploy.court
/**
 * 直播观看记录信息表 created by arctic in 2020.02.27
 */
class VideoRecord {
    /**
     * 用户主键
     */
    Long userId
    /**
     * 排期主键
     */
    Long planId
    /**
     * 用户登录ip
     */
    String ip
    /**
     * 观看状态
     */
    Integer playStatus
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
        userId comment: "用户主键"
        planId comment: "排期主键"
        playStatus comment: "观看状态"
        ip comment: "登录用户ip"
        dateCreated comment: "创建时间"
        lastUpdated comment: "修改时间"
        comment "直播观看记录表"
    }
}
