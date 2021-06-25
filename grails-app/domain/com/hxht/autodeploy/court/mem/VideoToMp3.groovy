package com.hxht.autodeploy.court.mem


/**
 * 系统字典
 */
class VideoToMp3 implements Serializable {
    private static final long serialVersionUID = 1
    //视频是否转移成功
    boolean isSuccess

    //创建时间
    Date dateCreated

    //记录视频的信息
    Long videoInfo

    //记录视频的信息
    Long trialInfo

    //尝试次数
    Integer linkNumber

    static constraints = {
    }
    static mapping = {
        datasource 'mem'
    }

}
