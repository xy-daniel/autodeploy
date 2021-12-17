package com.hxht.autodeploy.manager

import com.hxht.autodeploy.manager.authority.User
import grails.gorm.transactions.Transactional

@Transactional
class SelfService {

    /**
     * 用户信息修改
     * @param user 原用户信息
     * @param realName 新真实名称
     * @param username 新用户名称
     * @param pwd 新密码
     */
    def editSave(User user, String realName, String username, String pwd) {
        //更新用户信息表
        user.realName = realName
        user.username = username
        user.password = pwd
        user.save(flush: true)
    }
}
