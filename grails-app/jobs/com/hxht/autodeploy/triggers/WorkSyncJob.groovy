package com.hxht.autodeploy.triggers

import com.hxht.techcrt.Manufacturer
import com.hxht.techcrt.jobs.WorkSyncService
import grails.core.GrailsApplication

/**
 * 深圳对接东软排期定时器
 */
class WorkSyncJob {

    GrailsApplication grailsApplication
    WorkSyncService workSyncService

    static triggers = {
        String work = grailsApplication.config.getProperty('syncData.project_work')
        if (work.contains(Manufacturer.DONGRUAN)) {
            cron cronExpression: "0 0 2,9,13 * * ? *"//2点 9点 13点分别执行
        }
        if (work.contains(Manufacturer.HUIGU)) {
            cron cronExpression: "0 0 0,5,10,15,20 * * ? "
        }
        if (work.contains(Manufacturer.LUOHU)) {
            cron cronExpression: "0 0 2,13 * * ? *"
        }
        if (work.contains(Manufacturer.RONGJI)) {
            cron cronExpression: "0 45 18 * * ? *"
        }
        if (work.contains(Manufacturer.SHANDONG)) {
            cron cronExpression: "0 00 02 * * ? *"
        }
        if (work.contains(Manufacturer.TONGDAHAI)) {
            cron cronExpression: "0 0 0,6,12,18 * * ?"
        }
        if (work.contains(Manufacturer.BEIMING)) {
            cron cronExpression: "0 0 2,9,13 * * ? *"//2点 9点 13点分别执行
        }
    }

    def execute() {
        workSyncService.sync()
    }
}
