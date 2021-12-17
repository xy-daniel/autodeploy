package com.hxht.autodeploy.device

import com.hxht.autodeploy.Task
import com.hxht.autodeploy.device.Device

class DeviceTask {

    //主机
    Device device

    //任务
    Task task

    //是否执行过
    String exec

    static constraints = {
        exec nullable: true
    }

    static mapping = {
        autoTimestamp(true)
        device comment: "主机"
        task comment: "任务"
        exec comment: "是否执行过"
        comment "主机任务表"
    }
}
