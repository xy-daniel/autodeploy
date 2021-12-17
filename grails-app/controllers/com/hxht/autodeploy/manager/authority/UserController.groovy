package com.hxht.autodeploy.manager.authority

import com.hxht.autodeploy.enums.Resp
import com.hxht.autodeploy.enums.RespType
import com.hxht.autodeploy.manager.UserService
import com.hxht.autodeploy.utils.UUIDGenerator
import grails.converters.JSON

/**
 * 用户管理控制器 by Arctic in 2019.10.22
 */
class UserController {

    UserService userService

    /**
     * 用户列表
     * @return view="user.list"
     */
    def list() {
        if (request.method == "POST") {
            def draw = params.int("draw") ?: 1// 记录操作的次数 每次加1
            def start = params.int("start") ?: 0// 起始
            def length = params.int("length") ?: 20// 每页显示的size
            def search = params.get("search[value]") as String//搜索内容
            def model = userService.list(draw, start, length, search)
            render model as JSON
        }
    }

    /**
     * 前往添加用户页面
     * @return view="user.save"
     */
    def add() {
        [roleList: Role.findAll()]
    }

    /**
     * 保存用户
     * @return -->/user/list
     */
    def addSave() {
        def realName = params.get("realName")
        def username = params.get("username")
        def pwd = params.get("pwd")
        def enabled = params.get("enabled")
        enabled = enabled == 'true'
        def accountLocked = params.get("accountLocked")
        accountLocked = accountLocked == 'true'
        def checkRoleList = params.get("checkRole")

        //添加User
        def user = new User(
                uid: UUIDGenerator.nextUUID(),
                username: username,
                password: pwd,
                realName: realName,
                photo: null,
                enabled: enabled,
                accountExpired: 0,
                accountLocked: accountLocked,
        )
        def userRoleList = new ArrayList<UserRole>()
        if (checkRoleList instanceof String) {
            def ur = new UserRole(
                    user: user,
                    role: Role.get(checkRoleList as long)
            )
            userRoleList.add(ur)
        } else {
            for (def roleId : checkRoleList) {
                //添加UserRole
                def ur = new UserRole(
                        user: user,
                        role: Role.get(roleId as long)
                )
                userRoleList.add(ur)
            }
        }
        userService.addSave(user, userRoleList)
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 根据用户名获取用户信息
     * @return RespType.data
     */
    def getUserByUsername() {
        render Resp.toJson(RespType.SUCCESS, User.countByUsername(params.get("username") as String))
    }
    /**
     * 删除部分用户
     * @return RespType.data
     */
    def del() {
        def userIdStr = params.get("userIds") as String
        if (!userIdStr) {
            render Resp.toJson(RespType.DATA_NOT_EXIST)
            return
        }
        def userIdArr = userIdStr.split(",")
        userService.del(userIdArr)
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 前往修改用户信息页面
     * @return -->view="user.edit"
     */
    def edit() {
        def user = User.get(params.long("id"))
        def roleList = Role.findAll()
        def roleIdList = UserRole.findAllByUser(user).roleId
        [user: user, roleList: roleList, roleIdList: roleIdList]
    }

    /**
     * 更新用户信息
     * @return -->/user/list
     */
    def editSave() {
        def userId = params.long("userId")
        def realName = params.get("realName") as String
        def username = params.get("username") as String
        def pwd = params.get("pwd") as String
        def enabled = params.get("enabled")
        enabled = enabled == 'true'
        def accountLocked = params.get("accountLocked")
        accountLocked = accountLocked == 'true'
        def checkRoleList = params.get("checkRole")
        if (!(userId && realName && username && pwd)) {
            render Resp.toJson(RespType.FAIL)
            return
        }
        def user = User.get(userId)
        def userRoleList = new ArrayList<UserRole>()
        if (checkRoleList instanceof String) {
            def ur = new UserRole(
                    user: user,
                    role: Role.get(checkRoleList as long)
            )
            userRoleList.add(ur)
        } else {
            for (def roleId : checkRoleList) {
                //添加UserRole
                def ur = new UserRole(
                        user: user,
                        role: Role.get(roleId as long)
                )
                userRoleList.add(ur)
            }
        }
        //执行修改
        userService.editSave(user, realName, username, pwd, enabled, accountLocked, userRoleList)
        render Resp.toJson(RespType.SUCCESS)
    }
}
