package com.hxht.autodeploy.device

//主机
class Device implements Serializable {

    //主机名称
    String name

    //主机地址
    String ip

    //主机端口
    Integer port

    //主机用户名
    String point

    //主机密码
    String pwd

    //创建时间
    Date dateCreated

    //修改时间
    Date lastUpdated

    static constraints = {}

    static mapping = {
        autoTimestamp(true)
        name comment: "主机名称"
        ip comment: "主机地址"
        port comment: "主机端口"
        point comment: "主机用户名"
        pwd comment: "用户名密码"
        dateCreated comment: "创建时间"
        lastUpdated comment: "修改时间"
        comment "主机表"
    }
}
