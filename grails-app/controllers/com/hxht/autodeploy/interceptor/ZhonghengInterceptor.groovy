package com.hxht.autodeploy.interceptor

import com.hxht.techcrt.Resp
import com.hxht.techcrt.enums.RespType
import com.hxht.techcrt.utils.IpUtil
import com.hxht.techcrt.utils.JwtUtil
import com.hxht.techcrt.utils.MD5Utils

import javax.servlet.http.HttpServletResponse

class ZhonghengInterceptor {
    ZhonghengInterceptor() {
        match(controller: "zhongheng")
                .excludes(action: "getCourtRoomList")
                .excludes(action: "getCourtScheduledInfo")
    }

    boolean before() {
        def requestIp = IpUtil.getIpAddress(request)
        def requestPath = request.getRequestURI().substring(request.getContextPath().length())
        def requestMethod = request.getMethod()
        log.info("中恒信接口被调用 requestIp:[${requestIp}],requestMethod:[${requestMethod}],requestPath:[${requestPath}],params:${params},JSON:${request.JSON}")
        if (requestPath == "/zhongheng/token"){
            return true
        }
        //获取授权码+时间戳生成的加密字符串生成的token
        def auth = request.getHeader("token")
        //获取用于生成加密字符串的时间戳
        def timestamp = request.getHeader("timestamp")
        if (auth == null || auth == ""){
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            render Resp.toJson(RespType.FAIL, "token does not exist")
            return false
        }
        if (timestamp == null || timestamp == ""){
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            render Resp.toJson(RespType.FAIL, "timestamp does not exist.")
            return false
        }
        //认证码
        def authenticationCode = grailsApplication.config.getProperty("syncData.zhongheng.authorizationCode") + timestamp
        //根据认证码生成MD5加密字符串
        def secret = grailsApplication.config.getProperty('jwt.info.secret')
        def claims = JwtUtil.parse(auth, secret)
        if (claims == null || claims.authenticationCode != MD5Utils.code(authenticationCode)) {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            render Resp.toJson(RespType.FAIL, "incorrect authentication code.")
            return false
        }

        long nowMillis = System.currentTimeMillis()
        if (nowMillis >= claims.getExpiration().getTime()) {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            render Resp.toJson(RespType.FAIL, "token timed out.")
            return false
        }
        return true
    }

    boolean after() {
        return true
    }

    void afterView() {
        // no-op
    }
}
