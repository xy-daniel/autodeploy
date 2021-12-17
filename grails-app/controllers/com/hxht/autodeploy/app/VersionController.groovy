package com.hxht.autodeploy.app

import com.hxht.autodeploy.enums.Resp
import com.hxht.autodeploy.enums.RespType
import com.hxht.autodeploy.utils.UUIDGenerator
import grails.converters.JSON
import org.springframework.web.multipart.MultipartHttpServletRequest

class VersionController {

    VersionService versionService

    def list() {
        App app = App.get(params.id as long)
        if (request.method == "POST") {
            def draw = params.int("draw") ?: 1// 记录操作的次数 每次加1
            def start = params.int("start") ?: 0// 起始
            def length = params.int("length") ?: 20// 每页显示的size
            def search = params.get("search[value]") as String//搜索内容
            render versionService.list(draw, start, length, search, app) as JSON
        } else {
            [app: app]
        }
    }

    def add() {
        [appId: params.id]
    }

    def addSave() {
        if (request instanceof MultipartHttpServletRequest) {
            def dir = UUIDGenerator.nextUUID()
            def number = params.number as String
            def software = request.getFile("software")
            if (!software) {
                render Resp.toJson(RespType.FAIL)
                return
            }
            String filePath = ""
            def osName = System.getProperty("os.name")
            if (osName.startsWith("Windows")) {
                filePath = "D:" + File.separator
            } else {
                filePath = File.separator
            }
            filePath += "home" + File.separator + "hxht" + File.separator + "apps" + File.separator + dir
            def file = new File(filePath)
            if (!file.exists()) {
                file.mkdir()
                file.canWrite()
                file.canWrite()
                file.canExecute()
            }
            filePath += File.separator + software.originalFilename
            def softAddress = new File(filePath)
            software.transferTo(softAddress)
            def version = new Version(
                    number: number,
                    path: filePath,
                    size: software.size,
                    app: App.get(params.id as long),
            )
            version.save(flush: true)
        }
        render Resp.toJson(RespType.SUCCESS)
    }

    def del() {
        Version version = Version.get(params.id as long)
        version.del = "yes"
        version.save(flush: true)
        render Resp.toJson(RespType.SUCCESS)
    }
}
