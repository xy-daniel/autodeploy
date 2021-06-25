package com.hxht.autodeploy.interceptor

import com.hxht.techcrt.utils.IpUtil
import com.hxht.techcrt.utils.JwtUtil

import javax.servlet.http.HttpServletResponse

class ApiInterceptor {
    ApiInterceptor() {
        match(controller: "api")
                .excludes(action: "token")
                .excludes(action: "clientSignatureDownload")
                .excludes(action: "clientSignaturePersonList")
                .excludes(action: "signaturePlanList")
                .excludes(action: "signaturePersonList")
                .excludes(action: "signaturePersonSubmit")
    }

    boolean before() {
        def ip = IpUtil.getIpAddress(request)
        log.info("api接口被调用 ip[${ip}] Method[${request.getMethod()}] params[${params}] JSON[${request.JSON}]")
        return true
        if ("OPTIONS" == request.getMethod()) {
            return true
        }
        def url = request.getRequestURI().substring(request.getContextPath().length())

        def tokenPatterns = grailsApplication.config.getProperty('jwt.info.tokenExclude')
        if (url == tokenPatterns) {
            return true
        }
        def auth = request.getHeader("Authorization")
        if (auth != null) {
            if (auth.startsWith("Bearer ")) {
                auth = auth.replace("Bearer ", "")
            }
            def secret = grailsApplication.config.getProperty('jwt.info.secret')
            def claims = JwtUtil.parse(auth, secret)
            if (claims != null) {
                long nowMillis = System.currentTimeMillis()
                if (nowMillis < claims.getExpiration().getTime()) {//过期判断
                    return true
                }
            }
        }
        //验证不通过
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        return false
    }

    boolean after() {
        true
    }

    void afterView() {
        // no-op
    }
}
