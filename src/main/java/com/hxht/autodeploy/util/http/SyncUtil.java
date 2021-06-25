package com.hxht.autodeploy.util.http;

import org.grails.web.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 请求cmp数据地址工具
 */
public class SyncUtil {

    public static HttpClientResponse sync(String url, RequestMethod method, Object object){
        JSONObject jsonObject = new JSONObject();
        HttpClientJsonRequest request = new HttpClientJsonRequest(url, method);
        long time = System.currentTimeMillis();
        String au = MD5Util.code("A2089E7697BB46B042BA7BD3E2EB50F1" + time + "AB5AA954F8B22B8B5D5940BAEF71B880");
        request.addHeader("Authorization", "Basic " + au);
        request.addHeader("Accept", "application/json");
        jsonObject.put("appId", "A2089E7697BB46B042BA7BD3E2EB50F1");
        jsonObject.put("timestamp", time);
        jsonObject.put("data", object);
        request.setJsonObject(jsonObject);
        return HttpClientUtil.doRequest(request);
    }

}
