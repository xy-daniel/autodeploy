package com.hxht.autodeploy.enums

import grails.converters.JSON

class Resp {
    static String toJson(RespType re, Object data) {
        Map<String, Object> model = [:]
        model.put("code", re.getCode())
        model.put("msg", re.getMsg())
        model.put("data", data)
        return model as JSON
    }

    static pack(RespType re, Object data) {
        Map<String, Object> model = [:]
        model.put("code", re.getCode())
        model.put("msg", re.getMsg())
        model.put("data", data)
        return model
    }

    static pack(RespType re) {
        pack(re, "")
    }

    static String toJson(RespType re) {
        toJson(re, "")
    }
}
