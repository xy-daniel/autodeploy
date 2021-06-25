package com.hxht.autodeploy.court.detention

import com.hxht.techcrt.court.Courtroom

/**
 * 羁押室设备
 */
class DetentionDevice {

    //设备编号
    String deviceUid

    //设备名称
    String deviceName = "未命名设备"

    //设备ip地址
    String ip

    //设备端口
    String port

    //所属法庭
    Courtroom courtroom

    //设备软件版本
    String ver

    //上次连接时间
    Date beatDate

    //设备可用状态
    Integer status

    //创建时间
    Date dateCreated

    //修改时间
    Date lastUpdated

    static constraints = {
        deviceUid maxSize: 64, unique: true  //设备编号唯一
        deviceName nullable: true  //设备名称可空
        ver nullable: true  //设备版本可空
        beatDate nullable: true  //设备最后连接时间可空
        courtroom nullable: true  //所属法庭能够为空
    }

    static mapping = {
        deviceUid comment: "设备编号", index: true
        deviceName comment: "设备名称"
        ip comment: "设备ip"
        ver comment: "设备版本"
        beatDate comment: "最后连接时间"
        courtroom comment: "默认所属法庭"
        dateCreated comment: "创建时间"
        lastUpdated comment: "更新时间"
        comment "羁押室设备表"
    }
}
