package com.hxht.autodeploy.utils.comm

import com.hxht.techcrt.utils.http.HttpUtil
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.grails.web.json.JSONObject

/**
 * 模块通信工具包
 */
class StoreCommUtil {
    private static final Log log = LogFactory.getLog(StoreCommUtil.class)

    static JSONObject stop(Long trialId, String storeIp) {
        def url = "http://${storeIp}:2420/"
        def data = [
                trial  : "${trialId}",
                type   : "stop",
        ]
        return HttpUtil.postToJson(url, data)
    }

    static JSONObject start(Long planId, Long trialId, String storeIp, List chnList) {
        def url = "http://${storeIp}:2420/"
        def data = [
                plan   : "${planId}",
                trial  : "${trialId}",
                type   : "start",
                channel: chnList,
        ]
        return HttpUtil.postToJson(url, data)
    }
}
