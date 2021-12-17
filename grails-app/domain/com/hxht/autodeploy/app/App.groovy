package com.hxht.autodeploy.app
//应用
class App {

    //应用名称
    String itemName

    //安装包名称
    String packageName

    static hasMany = [versions: Version]

    static constraints = {
    }

    static mapping = {
        autoTimestamp(true)
        itemName comment: "应用名称"
        packageName comment: "安装包名称无需后缀"
        comment "应用表"
    }
}
