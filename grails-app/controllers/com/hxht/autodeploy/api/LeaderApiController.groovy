package com.hxht.autodeploy.api

import com.hxht.techcrt.Resp
import com.hxht.techcrt.enums.RespType
import com.hxht.techcrt.utils.JwtUtil
import org.grails.plugin.cache.GrailsCacheManager


class LeaderApiController {
    LeaderApiService LeaderApiService
    GrailsCacheManager grailsCacheManager


    private static String secret
    private static Long expires

    LeaderApiController() {
        secret = grailsApplication.config.getProperty('jwt.info.secret')
        expires = grailsApplication.config.getProperty('jwt.info.expires') as long
    }
    def token() {
        /**
         * POST
         * 获取TOKEN接口
         * .../token/
         * body:
         *  username: {username} 用户名
         *  password: {password} 密码
         */
        if (request.method == "POST") {
            def username = params.username as String
            def password = params.password as String
            def user = LeaderApiService.getUserByUsernameAndPassword(username, password)
            if (user) {
                def accessToken = JwtUtil.create(user, secret, expires)
                render Resp.toJson(RespType.SUCCESS, accessToken)
                return
            }
            render Resp.toJson(RespType.USERNAME_PASSWORD_ERROR)
        }
    }
}
