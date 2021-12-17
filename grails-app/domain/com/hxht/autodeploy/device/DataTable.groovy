package com.hxht.autodeploy.device

import com.hxht.autodeploy.device.Device

class DataTable implements Serializable {

    //所属设备
    Device device

    //用户名
    String username

    //密码
    String password

    //表名
    String tableName

    static constraints = {
    }

    static mapping = {
        autoTimestamp(true)
        device comment: "所属主机"
        username comment: "用户名"
        password comment: "用户密码"
        tableName comment: "表名"
        comment "数据库表"
    }
}
