package com.hxht.autodeploy.triggers

import com.hxht.techcrt.Alarm
import com.hxht.techcrt.Dict
import com.hxht.techcrt.court.manager.info.AlarmService
import com.hxht.techcrt.util.os.OSUtils

/**
 *  每隔一小时执行一次，获取内存或者磁盘是否空间不足
 */
class AlarmJob {
    AlarmService alarmService

    static triggers = {
        cron cronExpression: "0 0 */1 * * ?"//每隔1小时执行一次
//        cron cronExpression: "15 * * * * ?"//一分钟执行一次
    }

    def execute() {
        def dict = Dict.findByCode("CURRENT_MEM_ALARM")
        //内存
        def memMap = OSUtils.memoryUsage()
        if (Double.parseDouble(memMap.get("memFree") as String) < Double.parseDouble(dict.val)) { // 目前设置的是内存小于2G存储到报警日志
            log.info("剩余内存为：${memMap.get("memFree") as String}")
            alarmService.addSave(new Alarm(alarmType: 1, alarmInfo: "内存空间小于" + dict.val + "G"))
        }
        //磁盘
        def url = "/usr/local/movies"
        def file = new File(url)
        //磁盘剩余空间
        def usableSpace = file.getUsableSpace()
        if (Double.parseDouble(Double.parseDouble(usableSpace.toString()) / (1024 * 1024 * 1024) as String) < Double.parseDouble(dict.ext1)) {
            log.info("磁盘剩余大小为：${Double.parseDouble(usableSpace.toString()) / (1024 * 1024 * 1024) as String}")
            alarmService.addSave(new Alarm(alarmType: 2, alarmInfo: "磁盘空间小于" + dict.ext1 + "G"))
        }

        /*//cpu
        def cpuUsage = OSUtils.cpuUsage()()
        if (cpuUsage > 95) { // 目前设置的是cpu使用率大于95%，则报警信息写到表
            log.info("cpu使用率为：${cpuUsage}")
            alarmService.addSave(new Alarm(alarmType: 3, alarmInfo: "cpu使用率大于95进行报警！"))
        }*/
    }
}
