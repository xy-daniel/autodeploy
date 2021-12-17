package com.hxht.autodeploy.device


import com.hxht.autodeploy.enums.Resp
import com.hxht.autodeploy.enums.RespType
import com.hxht.autodeploy.utils.FileUtils
import grails.converters.JSON

class DataFileController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    DataFileService dataFileService

    def list() {
        long tableId = params.id as long
        if (request.method == "POST") {
            def draw = params.int("draw") ?: 1// 记录操作的次数 每次加1
            def start = params.int("start") ?: 0// 起始
            def length = params.int("length") ?: 20// 每页显示的size
            def search = params.get("search[value]") as String//搜索内容
            render dataFileService.list(draw, start, length, search, tableId) as JSON
        } else {
            ["dataTable": DataTable.get(tableId)]
        }
    }

    def download() {
        FileUtils.download(response, params.get("filePath") as String)
    }

    def del() {
        def dataFile = DataFile.get(params.id as long)
        def file = new File(dataFile.address)
        if (file.exists()) {
            file.delete()
        }
        dataFile.delete(flush: true)
        render Resp.toJson(RespType.SUCCESS)
    }
}
