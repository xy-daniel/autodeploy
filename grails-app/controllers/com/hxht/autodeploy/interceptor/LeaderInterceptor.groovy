package com.hxht.autodeploy.interceptor

import com.hxht.techcrt.utils.JwtUtil
import io.jsonwebtoken.Claims

import javax.servlet.http.HttpServletResponse


class LeaderInterceptor {
    LeaderInterceptor(){
        match(controller: "leaderApi").excludes(action: "token")
    }
    boolean before() {
        if ("OPTIONS" == request.getMethod()) {
            return true
        }
        def url = request.getRequestURI().substring(request.getContextPath().length())

        def tokenPatterns = grailsApplication.config.getProperty('jwt.info.tokenExclude')
        if (url == tokenPatterns) {
            return true
        }
        String auth = request.getHeader("Authorization")
        if (auth != null) {
            if (auth.startsWith("Bearer ")) {
                auth = auth.replace("Bearer ", "")
            }
            def secret = grailsApplication.config.getProperty('jwt.info.secret')
            Claims claims = JwtUtil.parse(auth, secret)
            if (claims != null) {
                long nowMillis = System.currentTimeMillis()
                if (nowMillis < claims.getExpiration().getTime()) {//过期判断
                    return true
                }
            }
        }
        //验证不通过
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        return false }

    boolean after() { true }

    void afterView() {
        // no-op
    }
}
