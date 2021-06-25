package com.hxht.autodeploy.court.manager

import com.hxht.techcrt.Dict
import com.hxht.techcrt.Resp
import com.hxht.techcrt.enums.RespType
import grails.converters.JSON
import java.util.regex.Pattern

/**
 * 服务器磁盘详情
 */
class OsInfoController {

    OsInfoService osInfoService

    def list() {
        if (request.method == "POST") {
            def draw = params.int("draw") ?: 1
            def model = osInfoService.list(draw)
            render model as JSON
        }
    }

    def edit() {
        [memAlarm: Dict.findByCode("CURRENT_MEM_ALARM")]
    }

    def editSave() {
        if (params.get("diskAlarm")) {
            def pattern = Pattern.compile("^-?[0-9]+")
            if (pattern.matcher(params.get("diskAlarm") as String).matches()) {
                osInfoService.editSave(params.get("diskAlarm"))
                render Resp.toJson(RespType.SUCCESS)
            } else {
                render Resp.toJson(RespType.BUSINESS_VALID_FAIL)
            }
        } else {
            render Resp.toJson(RespType.DATA_NOT_ALLOWED)
        }
    }
}