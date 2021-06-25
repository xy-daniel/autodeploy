package com.hxht.autodeploy.utils.http

import cn.hutool.core.util.CharsetUtil
import cn.hutool.http.Header
import cn.hutool.http.HttpRequest
import com.hxht.techcrt.court.manager.SystemController
import grails.converters.JSON
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.grails.web.json.JSONObject

/**
 * 通用Http工具类 create by arctic
 */
class HttpUtil {

    private static final Log log = LogFactory.getLog(HttpUtil.class)
    /**
     * 最简单的get请求
     * @param url 请求地址
     * @return 请求结果
     */
    static String simplestGet (String url) {
        cn.hutool.http.HttpUtil.get(url, CharsetUtil.CHARSET_UTF_8)
    }

    /**
     * 最简单的post请求
     * @param url  请求地址
     * @param data  请求参数
     * @return  响应字符串
     */
    static String simplePost (String url, def data) {
        HttpRequest.post(url).body((data as JSON) as String).execute().body()
    }

    /**
     * 将响应值转换为JSONObject的post请求
     * @param url 请求地址
     * @param data 请求数据
     * @return  A JSONObject
     */
    static JSONObject postToJson (String url, def data) {
        String resultString = simplePost(url, data)
        //判断响应字符串是否是JSON格式的字符串
        if (!resultString.startsWith("{")) {
            return null
        }
        return new JSONObject(resultString)
    }

    /**
     * 北明接口用的get请求
     * @param url 请求地址
     * @param paramMap 请求的参数
     * @return 请求结果
     */
    static JSONObject getForBeiMing (String url,Map paramMap) {
        try {
            return new JSONObject(HttpRequest.get(url)
                    .header( Header.CONTENT_TYPE, "application/json;charset=utf-8")
                    .header("identificationInfo","{\"companyId\":7550,\"courtInfos\":[{\"fydm\":\"${SystemController.currentCourt.val}\"}]}")
                    .timeout(20000)
                    .form(paramMap)
                    .execute().body())
        }catch (Exception e) {
            ByteArrayOutputStream buf = new ByteArrayOutputStream()
            e.printStackTrace(new PrintWriter(buf, true))
            String expMessage = buf.toString()
            buf.close()
            log.error("[HttpUtil.getForBeiMing]发送http请求异常，请求参数[${paramMap as JSON}]，异常堆栈：\n ${expMessage}")
        }
        return null
    }


    /**
     * 北明接口用get请求 获取部门、用户、审判组织信息等。
     * @param url 请求地址
     * @param paramMap 请求的参数
     * @return 请求结果
     */
    /*static JSONObject getForBeiMing2 (String url,Map paramMap) {
        try {
            return new JSONObject(HttpRequest.get(url)
                    .header( Header.CONTENT_TYPE, "application/json;charset=utf-8")
                    .timeout(20000)
                    .form(paramMap)
                    .execute().body())
        }catch (Exception e) {
            ByteArrayOutputStream buf = new ByteArrayOutputStream()
            e.printStackTrace(new PrintWriter(buf, true))
            String expMessage = buf.toString()
            buf.close()
            log.error("[HttpUtil]发送http请求异常，请求参数[${paramMap as JSON}]，异常堆栈：\n ${expMessage}")
        }
        return null
    }*/

}
