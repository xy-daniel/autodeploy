package com.hxht.autodeploy

import com.alibaba.fastjson.JSONObject
import com.hxht.techcrt.enums.RespType
import grails.converters.JSON

class Resp {
    static String toJson (RespType re, Object data){
        Map<String, Object> model = [:]
        model.put("code", re.getCode())
        model.put("msg", re.getMsg())
        model.put("data", data)
        return model as JSON
    }

    static pack(RespType re, Object data){
        Map<String, Object> model = [:]
        model.put("code", re.getCode())
        model.put("msg", re.getMsg())
        model.put("data", data)
        return model
    }

    static pack(RespType re){
        pack(re,"")
    }

    static String toJson (RespType re){
        toJson(re,"")
    }

    /**
     * 对接中恒用的庭审公告和助手的返回类型
     * @param code
     * @param msg
     * @return
     */
    static String return_error(int code, String msg) {
        JSONObject obj=new JSONObject()
        obj.put("code", code)
        obj.put("Message", msg)
        obj.put("Data", null)
        return com.alibaba.fastjson.JSON.toJSONString(obj)
    }
    static  String return_success(int code, Object data) {
        JSONObject obj=new JSONObject()
        obj.put("code",code)
        obj.put("data", data)
        return com.alibaba.fastjson.JSON.toJSONString(obj)
    }
}
