package com.hxht.autodeploy

/**
 * 登陆日志工具类 in 2019.10.21
 * @auther Arctic
 */
class LogLoginUtil {

    /**
     * 含有用户id的登录日志
     * @param userId
     * @param message
     * @return
     */
    static log(long userId, String message) {
        new LogLogin(
                userId: userId,
                message: message,
        ).save(flush: true)
    }

    /**
     * 不含有id的登录日志
     * @param message
     * @return
     */
    static logNotId(String message){
        new LogLogin(
                message: message,
        ).save(flush: true)
    }

}
