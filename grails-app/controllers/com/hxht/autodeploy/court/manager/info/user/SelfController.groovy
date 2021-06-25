package com.hxht.autodeploy.court.manager.info.user

import com.hxht.techcrt.Resp
import com.hxht.techcrt.User
import com.hxht.techcrt.enums.RespType
import grails.plugin.springsecurity.SpringSecurityService

/**
 * 个人信息功能
 */
class SelfController {

    SelfService selfService
    SpringSecurityService springSecurityService

    /**
     * 前往修改用户信息页面
     * @return -->view="user.edit"
     */
    def edit() {
        [user: springSecurityService.currentUser as User]
    }

    /**
     * 更新用户信息
     * @return -->/user/list
     */
    def editSave() {
        def realName = params.get("realName") as String
        def username = params.get("username") as String
        def pwd = params.get("pwd") as String
        if(!(realName && username && pwd)){
            render Resp.toJson(RespType.FAIL)
            return
        }
        if (username == "super") {
            render Resp.toJson(RespType.BUSINESS_VALID_FAIL)
            return
        }
        User user = springSecurityService.currentUser as User
        selfService.editSave(user, realName, username, pwd)
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 根据用户名获取用户信息
     * @return RespType.data
     */
    def getUserByUsername() {
        def count = User.countByUsername(params.get("username") as String)
        render Resp.toJson(RespType.SUCCESS, count)
    }
}
