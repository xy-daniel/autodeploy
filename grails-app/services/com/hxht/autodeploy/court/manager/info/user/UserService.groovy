package com.hxht.autodeploy.court.manager.info.user

import com.hxht.techcrt.User
import com.hxht.techcrt.UserRole
import grails.gorm.transactions.Transactional

/**
 * 用户操作Service by Arctic in 2019.10.22
 */
@Transactional
class UserService {
    /**
     * 用户列表
     * @param draw  标志
     * @param start  起始坐标
     * @param length  长度
     * @param search  搜索关键词
     * @return  List<User>
     */
    def list(int draw, int start, int length, String search) {
        def model = [:]
        model.put("draw", draw)
        def count = User.createCriteria().count() {
            if (search) {
                or {
                    like("realName", "%${search}%")
                    like("username", "%${search}%")
                }
            }
            and {
                notEqual("username", "super")
            }
        }
        def dataList = User.createCriteria().list {
            and {
                setMaxResults(length)
                setFirstResult(start)
            }
            or {
                like("realName", "%${search}%")
                like("username", "%${search}%")
            }
            and {
                notEqual("username", "super")
            }
            order("dateCreated","desc")
        } as List<User>
        def modelDataList = []
        for (def user : dataList) {
            //id--用户名--真实姓名--注册时间--最后登录时间
            def data = [:]
            data.put("id", user.id)
            data.put("username", user.username)
            data.put("realName", user.realName)
            data.put("enabled", user.enabled)
            data.put("accountLocked", user.accountLocked)
            modelDataList.add(data)
        }
        model.put("recordsTotal", count)//数据总条数
        model.put("recordsFiltered", count)//显示的条数
        model.put("data", modelDataList)
        model
    }

    /**
     * 用户添加
     * @param user  用户信息
     * @param ur  角色信息
     */
    def addSave(User user,List<UserRole> ur){
        user.save(flush:true)
        if (user.hasErrors()) {
            def msg = "[UserService addSave user.save]用户添加保存失败 errors [${user.errors}]"
            log.error(msg)
            throw new RuntimeException(msg)
        }
        for (def userrole: ur){
            userrole.save(flush:true)
            if (userrole.hasErrors()) {
                def msg = "[UserService addSave userrole.save]用户添加保存失败 errors [${userrole.errors}]"
                log.error(msg)
                throw new RuntimeException(msg)
            }
        }
    }

    /**
     * 用户删除
     * @param ids  多个id
     */
    def del(String[] ids){
        for (String id : ids){
            //获取用户
            def user = User.get(id)
            //删除UserRole
            UserRole.findAllByUser(user)*.delete(flush:true)
            //删除用户
            user.delete(flush:true)
            if (user.hasErrors()) {
                def msg = "[UserService del]用户删除失败 errors [${user.errors}]"
                log.error(msg)
                throw new RuntimeException(msg)
            }
        }
    }

    /**
     * 用户信息修改
     * @param user  原用户信息
     * @param realName  新真实名称
     * @param username  新用户名称
     * @param pwd  新密码
     */
    def editSave(User user, String realName, String username, String pwd, boolean enabled, boolean accountLocked,List<UserRole> ur){
        //删除旧的UserRole
        def userall = UserRole.findAllByUser(user)
        for (def allus: userall){
            allus.delete(flush:true)
            if (allus.hasErrors()) {
                def msg = "[UserService allus]用户删除失败 errors [${allus.errors}]"
                log.error(msg)
                throw new RuntimeException(msg)
            }
        }
        //增加新的UserRole信息
        for (def userrole: ur){
            userrole.save(flush:true)
            if (userrole.hasErrors()) {
                def msg = "[UserService editSave]用户保存失败 errors [${userrole.errors}]"
                log.error(msg)
                throw new RuntimeException(msg)
            }
        }
        //更新用户信息表
        user.realName = realName
        user.username = username
        user.password = pwd
        user.enabled = enabled
        user.accountLocked = accountLocked
        user.lastUpdated = new Date()
        user.save(flush: true)
        if (user.hasErrors()) {
            def msg = "[UserService editSave]用户修改保存失败 errors [${user.errors}]"
            log.error(msg)
            throw new RuntimeException(msg)
        }
    }
}
