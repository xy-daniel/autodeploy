package com.hxht.autodeploy

class PushLog {

    //同步地址（同步目标法院IP）
    String addressIp

    //同步数据
    String data

    //是否同步成功
    boolean isSuccess

    //同步方法地址
    String url

    //创建时间
    Date dateCreated



    static constraints = {
        addressIp nullable: true
        data nullable: true
        isSuccess nullable: true
        url nullable: true
    }
    static mapping = {
        addressIp comment: "同步地址（同步目标法院IP）"
        data sqlType: 'text', comment: "同步数据"
        isSuccess comment: "是否同步成功"
        url comment: "同步方法地址"
        dateCreated comment: "创建时间"
        comment "cmp系统推送日志"
    }
}
