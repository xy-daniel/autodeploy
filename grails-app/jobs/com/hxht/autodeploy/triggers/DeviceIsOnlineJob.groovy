package com.hxht.autodeploy.triggers

import com.hxht.techcrt.CourtroomOnlineStatus
import com.hxht.techcrt.court.Courtroom
import com.hxht.techcrt.mem.DeviceIsOnlineService
import com.hxht.techcrt.utils.IpUtil

class DeviceIsOnlineJob {

    DeviceIsOnlineService deviceIsOnlineService

    static triggers = {
                cron cronExpression: "0 0/5 * * * ? *"//每天1：10执行一次
    }

    def execute() {
        def courtrooms = Courtroom.findAll()
        courtrooms.each {
            it = it as Courtroom
            Integer status = CourtroomOnlineStatus.OFFLINE
            if (!it.deviceIp){
                status = CourtroomOnlineStatus.NOT_SET
            }else{
                if (IpUtil.ping(it.deviceIp)){
                    status = CourtroomOnlineStatus.ONLINE
                }
            }
            deviceIsOnlineService.check(it.id, status)
        }
    }
}
