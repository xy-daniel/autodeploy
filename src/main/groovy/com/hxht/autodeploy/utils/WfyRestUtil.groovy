package com.hxht.autodeploy.utils

import cn.hutool.http.Header
import cn.hutool.http.HttpRequest
import grails.converters.JSON
import grails.util.Holders
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.grails.web.json.JSONObject

/**
 * 中国移动微法院互联网开庭管理平台接口调用工具
 */
class WfyRestUtil {
    private static final Log log = LogFactory.getLog(WfyRestUtil.class)
    private static String HTTP_PATH
    private static String APP_NAME
    private static String C_TYPE

    static {
        def grailsApplication = Holders.grailsApplication
        HTTP_PATH = grailsApplication.config.getProperty('weifayuan.interface.httpPath')
        APP_NAME = grailsApplication.config.getProperty('weifayuan.interface.appName')
        C_TYPE = grailsApplication.config.getProperty('weifayuan.interface.cType')
    }

    static JSONObject post(String url, Map model) {
        try {
            return new JSONObject(HttpRequest.post(url)
                    .header(Header.ACCEPT, "application/json")
                    .header(Header.CONTENT_TYPE, "application/json")
                    .header("appName", APP_NAME)
                    .header("cType", C_TYPE)
                    .body(model as JSON)//表单内容
                    .timeout(20000)//超时，毫秒
                    .execute().body())
        } catch (Exception e) {
            ByteArrayOutputStream buf = new ByteArrayOutputStream()
            e.printStackTrace(new PrintWriter(buf, true))
            String expMessage = buf.toString()
            buf.close()
            log.error("[WfyRestUtil]发送http请求异常，请求参数[${model as JSON}]，异常堆栈：\n ${expMessage}")
        }
        return null
    }
}
