package com.hxht.autodeploy


import com.hxht.techcrt.enums.WsRespType
import grails.converters.JSON

class WsResp {
    static String toJson(WsRespType wsRespType, Object data) {
        Map<String, Object> model = [:]
        model.put("code", wsRespType.getCode())
        model.put("msg", wsRespType.getMsg())
        model.put("data", data)
        return model as JSON
    }

}

