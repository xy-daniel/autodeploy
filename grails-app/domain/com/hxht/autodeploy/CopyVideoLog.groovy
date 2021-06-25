package com.hxht.autodeploy

import com.hxht.techcrt.court.VideoInfo

class CopyVideoLog {
    //视频是否拷贝成功
    boolean isSuccess

    //创建时间
    Date dateCreated

    //记录视频的信息
    VideoInfo videoInfo

    //尝试次数
    Integer linkNumber

    static constraints = {
    }
    static mapping = {
        version false
        isSuccess comment: "视频是否拷贝成功"
        dateCreated comment: "创建时间"
        videoInfo comment: "拷贝视频的信息"
        linkNumber comment: "连接次数"
        comment "定时向挂载磁盘拷贝视频的日志表"
    }
}
