package com.hxht.autodeploy.app

import com.hxht.autodeploy.app.App

class Version {

    //所属项目
    App app

    //版本号
    String number

    //存储路径
    String path

    //占用空间
    Long size

    String del

    //创建时间
    Date dateCreated

    //修改时间
    Date lastUpdated

    static belongsTo = [app: App]

    static constraints = {
        del nullable: true
    }

    static mapping = {
        autoTimestamp(true)
        app comment: "所属应用"
        number comment: "版本号"
        path comment: "安装包路径"
        size comment: "安装包大小"
        del comment: "是否删除"
        dateCreated comment: "创建时间"
        lastUpdated comment: "更新时间"
        comment "版本表"
    }
}
