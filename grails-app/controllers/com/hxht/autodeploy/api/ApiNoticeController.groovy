package com.hxht.autodeploy.api

import com.hxht.techcrt.Resp
import com.hxht.techcrt.enums.RespType
import grails.core.GrailsApplication

/**
 * TNS项目接口
 */
class ApiNoticeController {
    GrailsApplication grailsApplication
    ApiNoticeService apiNoticeService

    /**
     * 获取全部法庭向TNS服务发送数据
     */
    def courtroom() {
        def courtroomList = apiNoticeService.courtroomAll()
        render Resp.toJson(RespType.SUCCESS, courtroomList)
    }

    /**
     * 获取一周排期数据
     */
    def plan() {
        def planList = apiNoticeService.getPlan()
        render Resp.toJson(RespType.SUCCESS, planList)
    }
}
