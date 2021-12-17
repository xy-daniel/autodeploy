package com.hxht.autodeploy.device

import com.hxht.autodeploy.enums.Resp
import com.hxht.autodeploy.enums.RespType
import com.hxht.autodeploy.utils.SshUtil
import grails.converters.JSON

class DataTableController {

    DataTableService dataTableService

    def list() {
        def deviceId = params.id
        if (request.method == "POST") {
            def draw = params.int("draw") ?: 1// 记录操作的次数 每次加1
            def start = params.int("start") ?: 0// 起始
            def length = params.int("length") ?: 20// 每页显示的size
            def search = params.get("search[value]") as String//搜索内容
            render dataTableService.list(draw, start, length, search, deviceId ? deviceId as long : 0L) as JSON
        } else {
            ["deviceId": deviceId]
        }
    }

    def add() {
        ["deviceId": params.id]
    }

    def addSave() {
        def dataTable = new DataTable(params)
        dataTable.device = Device.get(params.deviceId as long)
        dataTable.save(flush: true)
        render Resp.toJson(RespType.SUCCESS)
    }

    def edit() {
        ["dataTable": DataTable.get(params.id as long)]
    }

    def editSave() {
        def dataTable = DataTable.get(params.id as long)
        dataTable.username = params.username
        dataTable.password = params.password
        dataTable.tableName = params.tableName
        dataTable.save(flush: true)
        render Resp.toJson(RespType.SUCCESS)
    }

    def del() {
        def dataTable = DataTable.get(params.id as long)
        def dataFile = DataFile.findByDatabase(dataTable)
        if (dataFile) {
            render Resp.toJson(RespType.FAIL)
            return
        }
        dataTable.delete(flush: true)
        render Resp.toJson(RespType.SUCCESS)
    }

    def bak() {
        def dataTable = DataTable.get(params.id as long)
        String filePath = SshUtil.mysqldump(dataTable)
        new DataFile(
                database: dataTable,
                address: filePath,
                size: Math.floor(new File(filePath).size() / 1024 / 1024) as long
        ).save(flush: true)
        render Resp.toJson(RespType.SUCCESS)
    }
}
