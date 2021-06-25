package com.hxht.autodeploy.court.manager

import com.hxht.techcrt.Resp
import com.hxht.techcrt.court.VideoRecord
import com.hxht.techcrt.enums.PlayStatus
import com.hxht.techcrt.enums.RespType
import grails.converters.JSON

/**
 * 直播授权控制器 created by arctic in 2020.02.27
 */
class VideoRecordController {

    VideoRecordService videoRecordService

    /**
     *  直播授权列表显示正在观看直播的记录信息
     */
    def list() {
        if (request.method == "POST") {
            def draw = params.int("draw") ?: 1// 记录操作的次数 每次加1
            def start = params.int("start") ?: 0// 起始
            def length = params.int("length") ?: 20// 每页显示的size
            def search = params.get("search[value]") as String//搜索内容
            def model = videoRecordService.list(draw, start, length, search)
            render model as JSON
        }
    }

    /**
     * 恢复普通用户的观看权限
     */
    def restart(){
        def vr = VideoRecord.get(params.long("vrId"))
        vr.playStatus = PlayStatus.DISCONNECT
        vr.save(flush: true)
        render Resp.toJson(RespType.SUCCESS)
    }
}
