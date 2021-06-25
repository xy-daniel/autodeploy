package com.hxht.autodeploy.court

class CourtroomDevice {
    /**
     * 唯一编号
     */
    String uid
    /**
     * 设备编号
     */
    String deviceId
    /**
     * 设备名称
     */
    String name
    /**
     * 设备状态
     */
    Integer status

    static belongsTo = [courtroom: Courtroom]

    @Override
    String toString() {
        return "${this.name}-${this.deviceId}"
    }

    static constraints = {
        uid maxSize: 64
        deviceId maxSize: 250
        name maxSize: 250
    }

    static mapping = {
        uid comment: "唯一编号"
        deviceId comment: "设备编号"
        name comment: "设备名称"
        status comment: "设备状态 0 正常 1 关闭 2 损坏 3 维修 4 异常"
        courtroom comment: "设备所属法庭"
    }
}
