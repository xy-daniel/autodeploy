package com.hxht.autodeploy.triggers

import com.hxht.techcrt.PlanStatus
import com.hxht.techcrt.court.TrialInfo
import com.hxht.techcrt.court.plan.PlanService

/**
 * 每天凌晨两点半将未闭庭的排期强制闭庭
 * 2021.05.26 >>> 使用plan方法闭庭 daniel
 */
class TrialJob {

    PlanService planService

    static triggers = {
        cron cronExpression: "0 30 2 * * ? *"//每天2：30执行一次
    }

    def execute() {
        def begin = 0
        // execute job
        while (true) {
            def trial = this.tiralClosed(begin)
            if (!trial) {
                break
            }
        }

    }

    //闭庭接口
    def tiralClosed(Integer begin) {
        def trialInfoList = TrialInfo.findAllByStatus(PlanStatus.SESSION, [max: 10, offset: begin])
        if (!trialInfoList) {
            return false
        }
        for (def trialInfo : trialInfoList) {
            try {
                def trial = planService.planClose(trialInfo, PlanStatus.CLOSED, null)
                //排期闭庭后抛出排期事件
                this.notify("stopTrial", trialInfo.id)
                //向CMP系统推送开庭trial和plan信息
                this.notify("pushCmpCaseAndPlanAndTrial", null, trialInfo.planInfo.id, trialInfo.id)
            } catch (e) {
                e.printStackTrace()
                def msg = "[TrialJob.tiralClosed] 定时器闭庭失败\n错误信息[${e.message}]"
                log.error("[TrialJob.tiralClosed]${msg}")
            }
        }
        true
    }

}
