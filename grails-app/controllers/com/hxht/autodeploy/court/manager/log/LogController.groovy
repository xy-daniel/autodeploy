package com.hxht.autodeploy.court.manager.log


import com.hxht.techcrt.LogSystem
import com.hxht.techcrt.LogSystemUtil
import com.hxht.techcrt.Resp
import com.hxht.techcrt.enums.RespType
import grails.converters.JSON

/**
 * 日志控制器
 * Created By Arctic 2019/10/17
 */
class LogController {

    LogService logService

    /**
     * 操作日志列表
     */
    def logList() {
        if (request.method == "POST") {
            def draw = params.int("draw") ?: 1// 记录操作的次数 每次加1
            def start = params.int("start") ?: 0// 起始
            def length = params.int("length") ?: 20// 每页显示的size
            def search = params.get("search[value]") as String//搜索内容
            def model = logService.logSystemList(draw, start, length, search)
            render model as JSON
        }
    }

    /**
     * 登录日志列表
     */
    def logLoginList() {
        if (request.method == "POST") {
            def draw = params.int("draw") ?: 1// 记录操作的次数 每次加1
            def start = params.int("start") ?: 0// 起始
            def length = params.int("length") ?: 20// 每页显示的size
            def search = params.get("search[value]") as String//搜索内容
            def model = logService.logLoginList(draw, start, length, search)
            render model as JSON
        }
    }

    /**
     * 删除部分日志
     */
    def delSystemLogs(){
        LogSystemUtil.log(LogSystemUtil.DEBUG,"删除部分日志")
        def id1 = params.get("ids") as String
        if (!id1) {
            render Resp.toJson(RespType.DATA_NOT_EXIST)
            return
        }
        def ids = id1.split(",")
        for (String id:ids){
            def logSystem = LogSystem.get(id)
            logSystem.delete(flush:true)
        }
        render Resp.toJson(RespType.SUCCESS, 1)
    }
}
