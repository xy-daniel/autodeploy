package com.hxht.autodeploy.device

import com.hxht.autodeploy.device.DataTable

class DataFile {

    //所属数据库表
    DataTable database

    //存储地址
    String address

    //文件大小
    Long size

    //创建时间
    Date dateCreated

    static constraints = {
    }

    static mapping = {
        autoTimestamp(true)
        database comment: "所属数据库表"
        address comment: "存储地址"
        size comment: "文件大小"
        dateCreated comment: "创建时间"
        comment "数据文件表"
    }
}
