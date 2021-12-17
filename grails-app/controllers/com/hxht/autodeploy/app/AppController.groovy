package com.hxht.autodeploy.app


import com.hxht.autodeploy.enums.Resp
import com.hxht.autodeploy.enums.RespType
import com.hxht.autodeploy.utils.SshUtil
import com.jcraft.jsch.Session
import grails.converters.JSON

class AppController {

    AppService appService

    def test() {
        Session session = SshUtil.connect("192.168.0.203", 22, "root", "bjhxht")
        SshUtil.scpFrom(session, "/home/hxht/123456/123.txt", "D:" + File.separator + "home" + File.separator + "hxht" + File.separator + "123.txt")
        SshUtil.disconnect(session)
        render Resp.toJson(RespType.SUCCESS)
    }

    def list() {
        if (request.method == "POST") {
            def draw = params.int("draw") ?: 1// 记录操作的次数 每次加1
            def start = params.int("start") ?: 0// 起始
            def length = params.int("length") ?: 20// 每页显示的size
            def search = params.get("search[value]") as String//搜索内容
            render appService.list(draw, start, length, search) as JSON
        }
    }

    def del() {
        App app = App.get(params.id as long)
        if (app.versions.size() > 0) {
            render Resp.toJson(RespType.FAIL)
            return
        }
        app.delete(flush: true)
        render Resp.toJson(RespType.SUCCESS)
    }

    def add() {

    }

    def addSave() {
        def app = new App(params)
        app.save(flush: true)
        render Resp.toJson(RespType.SUCCESS)
    }

    def edit() {
        [app: App.get(params.id as long)]
    }

    def editSave() {
        def app = App.get(params.id as long)
        app.itemName = params.itemName as String
        app.packageName = params.packageName as String
        app.save(flush: true)
        render Resp.toJson(RespType.SUCCESS)
    }
}
