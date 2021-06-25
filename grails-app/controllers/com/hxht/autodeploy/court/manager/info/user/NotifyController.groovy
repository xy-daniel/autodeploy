package com.hxht.autodeploy.court.manager.info.user

import com.hxht.techcrt.Notify
import com.hxht.techcrt.NotifyStatus
import com.hxht.techcrt.Resp
import com.hxht.techcrt.enums.RespType
import grails.converters.JSON

/**
 * 系统通知功能
 */
class NotifyController {

    NotifyService notifyService

    /**
     * 未读通知列表
     */
    def listUnread() {
        render Resp.toJson(RespType.SUCCESS, notifyService.listUnread())
    }

    /**
     * 通知列表
     */
    def list() {
        if (request.method == "POST") {
            def draw = params.int("draw") ?: 1// 记录操作的次数 每次加1
            def start = params.int("start") ?: 0// 起始
            def length = params.int("length") ?: 20// 每页显示的size
            def search = params.get("search[value]") as String//搜索内容
            def model = notifyService.list(draw, start, length, search)
            render model as JSON
        }
    }

    /**
     * 通知详情，并将未读通知修改为已读通知
     */
    def show() {
        def notify = Notify.get(params.long("id"))
        if (notify.is_read != NotifyStatus.read) {
            notify.is_read = NotifyStatus.read
            notifyService.save(notify)
        }
        [notify: notify]
    }
}
