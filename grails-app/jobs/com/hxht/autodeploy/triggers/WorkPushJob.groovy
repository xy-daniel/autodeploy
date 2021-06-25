package com.hxht.autodeploy.triggers

import com.hxht.techcrt.Manufacturer
import com.hxht.techcrt.sync.huaxia.HuaXiaService
import grails.core.GrailsApplication

/**
 * 2021.03.17 >>> 华夏推送排期定时器创建 daniel
 * 2021.03.25 >>> 添加推送点播视频接口 daniel
 * 2021.04.14 >>> 华夏笔录推送15秒太过频繁，统一3分钟一次 daniel
 */
class WorkPushJob {

    GrailsApplication grailsApplication
    HuaXiaService huaXiaService

    static triggers = {
        String work = grailsApplication.config.getProperty('syncData.project_work')
        if (work.contains(Manufacturer.HUAXIA)) {
            cron cronExpression: "0 0/3 * * * ? *"  //每三分钟执行一次
        }
    }

    def execute() {
        huaXiaService.pushPlan()
        huaXiaService.pushTrialStart()
        huaXiaService.pushTrialPause()
        huaXiaService.pushTrialStop()
        huaXiaService.pushTrialRecord()
        huaXiaService.pushTrialNote()
    }
}
