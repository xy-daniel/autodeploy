package com.hxht.autodeploy.triggers

import com.hxht.techcrt.court.VideoRecord
import com.hxht.techcrt.enums.PlayStatus
import grails.plugin.springwebsocket.WebSocket

/**
 * 排期详情页面观看记录会记录到VideoRecord表中
 * 用户进入立即添加一个观看记录
 * 用户正常退出则此观看记录修改为结束观看状态
 * 但是有关闭浏览器、关闭电脑或断电等非正常操作会导致此观看记录一直处于观看状态，其实已经结束观看
 * 此定时器的作用就是没一个小时查看有没有已经结束观看但是状态仍未正在观看状态的
 * 将此观看记录的状态修改为结束观看
 */
class VideoHeatJob implements WebSocket {

    static triggers = {
        cron name: 'cronTrigger', startDelay: 10000, cronExpression: '0 0 0/1 * * ? *' //每1分钟执行一次
    }

    def execute() {
        def allVideoRecord = VideoRecord.findAllByPlayStatus(PlayStatus.CONNECTING)
        for (VideoRecord vr : allVideoRecord) {
            vr.playStatus = PlayStatus.DISCONNECT
            vr.save(flush: true)
        }
        convertAndSend("/queue/editVideoStatus", "0")
    }
}
