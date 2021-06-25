package com.hxht.autodeploy.util.http;

import com.hxht.autodeploy.exception.MethodNotSupportException;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * http request util
 * param @see{@link com.hxht.autodeploy.util.http.HttpClientRequest}
 * <p>support request method "GET","POST", "HEAD", "OPTIONS" see{@link RequestMethod}</p>
 *
 * @author alice
 * @version 1.0
 * @since 1.0
 */
public class HttpClientUtil {

    private static PoolingHttpClientConnectionManager connMgr;
    private static RequestConfig requestConfig;
    private static final int MAX_CONNECT_TIMEOUT = 20000;
    private static final int MAX_SOCKET_TIMEOUT = 30000;
    private static final int MAX_CONNECT_REQUEST_TIMEOUT = 50000;
    private static final int MAX_TOTAL = 100;

    static {
        // connecting pools
        connMgr = new PoolingHttpClientConnectionManager();
        //pool size
        connMgr.setMaxTotal(MAX_TOTAL);
        connMgr.setDefaultMaxPerRoute(connMgr.getMaxTotal());
        RequestConfig.Builder configBuilder = RequestConfig.custom();

        // timeout unit : milliseconds
        configBuilder.setConnectTimeout(MAX_CONNECT_TIMEOUT);
        configBuilder.setSocketTimeout(MAX_SOCKET_TIMEOUT);
        configBuilder.setConnectionRequestTimeout(MAX_CONNECT_REQUEST_TIMEOUT);

        // check availability
        //configBuilder.setStaleConnectionCheckEnabled(true);
        requestConfig = configBuilder.build();
    }

    /**
     * http request--get
     *
     * @param httpClientRequest @see {@link com.hxht.autodeploy.util.http.HttpClientRequest}
     * @return HttpClientResponse @see {@link com.hxht.autodeploy.util.http.HttpClientResponse}
     * @throws MethodNotSupportException please make sure request.methods is RequestMethod.GET
     *                                   RequestMethod @see {@link RequestMethod}
     */
    public static com.hxht.autodeploy.util.http.HttpClientResponse get(com.hxht.autodeploy.util.http.HttpClientRequest httpClientRequest) throws MethodNotSupportException {
        if (Objects.equals(httpClientRequest.getMethod(), RequestMethod.GET)) {
            return doRequest(httpClientRequest);
        } else {
            throw new MethodNotSupportException("HttpClientUtils.get");
        }
    }

    /**
     * http request--post
     *
     * @param httpClientRequest @see {@link com.hxht.autodeploy.util.http.HttpClientFormRequest} or @see {@link com.hxht.autodeploy.util.http.HttpClientJsonRequest}
     * @return HttpClientResponse @see {@link com.hxht.autodeploy.util.http.HttpClientResponse}
     * @throws MethodNotSupportException please make sure request.methods is RequestMethod.POST
     *                                   RequestMethod @see {@link RequestMethod}
     */
    public static com.hxht.autodeploy.util.http.HttpClientResponse post(com.hxht.autodeploy.util.http.HttpClientRequest httpClientRequest) throws MethodNotSupportException {
        if (Objects.equals(httpClientRequest.getMethod(), RequestMethod.POST)) {
            return doRequest(httpClientRequest);
        } else {
            throw new MethodNotSupportException("HttpClientUtils.post");
        }
    }

    /**
     * http request
     * <p>
     * <p>support request method "GET","POST", "HEAD", "OPTIONS" @see{@link RequestMethod}</p>
     *
     * @param httpClientRequest get:HttpClientRequest @see {@link com.hxht.autodeploy.util.http.HttpClientRequest}
     *                          post:HttpClientFormRequest @see {@link com.hxht.autodeploy.util.http.HttpClientFormRequest}
     *                          HttpClientJsonRequest @see {@link com.hxht.autodeploy.util.http.HttpClientJsonRequest}
     * @return HttpClientResponse @see {@link com.hxht.autodeploy.util.http.HttpClientResponse}
     * @throws MethodNotSupportException please make sure request.methods in {@link RequestMethod}
     */
    public static com.hxht.autodeploy.util.http.HttpClientResponse doRequest(com.hxht.autodeploy.util.http.HttpClientRequest httpClientRequest) {
        try {
            HttpRequestBase baseRequest = httpClientRequest.getHttpRequest();
            Header[] headers = httpClientRequest.getAllHeaders();
            if (null != headers && headers.length > 0) {
                baseRequest.setHeaders(headers);
            }
            baseRequest.setConfig(requestConfig);
            if (httpClientRequest instanceof com.hxht.autodeploy.util.http.BaseEntityRequest) { //to post entity
                ((HttpEntityEnclosingRequestBase) baseRequest).setEntity(((com.hxht.autodeploy.util.http.BaseEntityRequest) httpClientRequest).getEntity());
            }
            return toXHttpResponse(baseRequest, httpClientRequest.getResponseDefaultCharset(), httpClientRequest.isUseSSL());
        } catch (IOException e) {
            e.printStackTrace();
            return getErrorXResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (MethodNotSupportException e) {
            return getErrorXResponse(HttpServletResponse.SC_METHOD_NOT_ALLOWED, e.getMessage());
        }
    }

    private static com.hxht.autodeploy.util.http.HttpClientResponse getErrorXResponse(int code, String errorMsg) {
        return new com.hxht.autodeploy.util.http.HttpClientResponse(code, errorMsg, null, null, null);
    }

    private static com.hxht.autodeploy.util.http.HttpClientResponse toXHttpResponse(HttpUriRequest request, String defaultCharset
            , boolean useSSL) throws IOException {

        try (CloseableHttpClient httpClient = useSSL ? getHttpsClient() : HttpClients.createDefault()) {
            HttpResponse response = httpClient.execute(request);
            com.hxht.autodeploy.util.http.HttpClientResponse result = new com.hxht.autodeploy.util.http.HttpClientResponse(response.getStatusLine().getStatusCode(), response.getAllHeaders());
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                //content encoding
                String charset = (null == entity.getContentEncoding()) ? defaultCharset : entity.getContentEncoding().getValue();
                result.setContentEncoding(charset);
                //content type
                String contentType = (null == entity.getContentType()) ? "" : entity.getContentType().getValue();
                result.setContentType(contentType);

                try {
                    result.setResponseText(EntityUtils.toString(entity, defaultCharset));
                } finally {
                    try {
                        EntityUtils.consume(response.getEntity());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return result;
        }
    }

    private static CloseableHttpClient getHttpsClient() {
        return HttpClients.custom()
                .setSSLSocketFactory(createSSLConnSocketFactory())
                .setConnectionManager(connMgr)
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    //create SSLConnectionSocketFactory to trust all
    private static SSLConnectionSocketFactory createSSLConnSocketFactory() {
//        try {
//            SSLContext sslcontext = SSLContexts.custom()
//                    .loadTrustMaterial(null, (TrustStrategy) (chain, authType) -> true)
//                    .build();
//            return new SSLConnectionSocketFactory(sslcontext,
//                    SSLConnectionSocketFactory.getDefaultHostnameVerifier());
//        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
//            e.printStackTrace();
//        }
        return null;
    }


}

