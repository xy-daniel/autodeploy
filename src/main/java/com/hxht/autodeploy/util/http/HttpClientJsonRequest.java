package com.hxht.autodeploy.util.http;

import com.hxht.autodeploy.exception.MethodNotSupportException;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Request for Json
 */
public class HttpClientJsonRequest extends com.hxht.autodeploy.util.http.BaseEntityRequest {

    private Object jsonObject;

    private static final String JSON_CONTENT_TYPE = "application/json";

    public HttpClientJsonRequest(String url, RequestMethod method) throws MethodNotSupportException {
        super(url, method);
    }

    public HttpClientJsonRequest(String url) throws MethodNotSupportException {
        super(url);
    }

    public Object getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(Object jsonObject) {
        this.jsonObject = jsonObject;
    }

    @Override
    public HttpEntity getEntity() {
        StringEntity stringEntity = new StringEntity(jsonObject.toString(), getRequestCharset());
        stringEntity.setContentEncoding(getResponseDefaultCharset());
        stringEntity.setContentType(JSON_CONTENT_TYPE);
        return stringEntity;
    }
}
