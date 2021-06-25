package com.hxht.autodeploy.api

import cn.hutool.core.date.DateUtil
import com.hxht.techcrt.Resp
import com.hxht.techcrt.enums.RespType

class TestController {

//    TongdahaiService tongdahaiService

    def index() {
////        tongdahaiService.handleCaseInfo()
//        String result = DateUtil.format(null as Date, "yyyy")
//        println result
        Date date = null
        def result = DateUtil.format(date, "yyyy")
        println result
        render Resp.toJson(RespType.SUCCESS, result)
    }
}