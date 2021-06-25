package com.hxht.autodeploy.sync.util

import com.hxht.techcrt.util.http.HttpClientJsonRequest
import com.hxht.techcrt.util.http.HttpClientResponse
import com.hxht.techcrt.util.http.HttpClientUtil
import com.hxht.techcrt.utils.MD5Utils
import org.grails.web.json.JSONObject
import org.springframework.web.bind.annotation.RequestMethod

class SyncUtils {
    /**
     * @param url    地址
     * @param method 请求方法 get,post
     * @param object 请求参数
     * @return success 用于判断本次请求是否成功,用于数据重试提交.
     */
    static HttpClientResponse sync(String url, RequestMethod method, Object object) {
        JSONObject jsonObject = new JSONObject()
        HttpClientJsonRequest request = new HttpClientJsonRequest(url, method)
        Long time = System.currentTimeMillis()
        String au = MD5Utils.code("A2089E7697BB46B042BA7BD3E2EB50F1" + time + "AB5AA954F8B22B8B5D5940BAEF71B880")
        request.addHeader("Authorization", "Basic " + au)
        request.addHeader("Accept", "application/json")
        jsonObject.put("appId", "A2089E7697BB46B042BA7BD3E2EB50F1")
        jsonObject.put("timestamp", time)
        jsonObject.put("data", object)
        request.setJsonObject(jsonObject)
        return HttpClientUtil.doRequest(request)
    }

}
