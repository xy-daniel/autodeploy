package com.hxht.autodeploy.triggers

import com.hxht.techcrt.DeviceType
import com.hxht.techcrt.api.ApiOfflinePlanService
import com.hxht.techcrt.court.VideoDownFailRecord

/**
 * 离线开庭后的视频在书记员第一次请求获取视频时出错，记录在数据表格中，之后每天12点半下载尚未下载的视频
 */
class ReloadDownVideoJob {

    ApiOfflinePlanService apiOfflinePlanService

    static triggers = {
        cron cronExpression: "0 30 12 * * ? *"//每天03：00执行一次，仍然失败放到第二天执行
    }

    def execute() {
        def vdfrs = VideoDownFailRecord.findAllByHandlerSuccess(false)
        for (VideoDownFailRecord vdfr : vdfrs) {
            if (vdfr.ver == DeviceType.HTTP) {
                log.info("[ReloadDownVideoJob.execute]存在HTTP版本庭审主机拉去视频失败，再次获取")
                if (vdfr.taskId) {
                    apiOfflinePlanService.handleMoreFailVideo(vdfr)
                } else {
                    apiOfflinePlanService.handleSingleFailVideo(vdfr)
                }
            } else {
                log.info("[ReloadDownVideoJob.execute]存在FTP版本庭审主机拉去视频失败，再次获取")
                if (vdfr.taskId) {
                    apiOfflinePlanService.handleMoreFailFTPVideo(vdfr)
                } else {
                    apiOfflinePlanService.handleSingleFailFTPVideo(vdfr)
                }
            }

        }
    }
}