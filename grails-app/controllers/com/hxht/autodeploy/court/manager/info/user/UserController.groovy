package com.hxht.autodeploy.court.manager.info.user

import com.hxht.techcrt.LogSystemUtil
import com.hxht.techcrt.Resp
import com.hxht.techcrt.Role
import com.hxht.techcrt.User
import com.hxht.techcrt.UserRole
import com.hxht.techcrt.court.Department
import com.hxht.techcrt.court.Employee
import com.hxht.techcrt.enums.RespType
import com.hxht.techcrt.utils.UUIDGenerator
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
        def roleList = Role.findAll()
        def employeeid = params.long("id")
        def employee = null
        def depts = null
        if (employeeid){
            employee = Employee.get(employeeid)
            depts = Department.findAll()
        }
        [roleList: roleList, employeeid: employeeid, employee: employee, depts: depts]
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
        def employeeid = params.get("employeeid")
        def checkroleList = params.get("checkRole")

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
                dateCreated: new Date(),
                lastUpdated: new Date(),
                employee: employeeid
        )
        def userroleList = new ArrayList<UserRole>()
        if (checkroleList instanceof String){
            def ur = new UserRole(
                    user: user,
                    role: Role.get(checkroleList as long)
            )
            userroleList.add(ur)
        }else{
            for (def roleid : checkroleList){
                //添加UserRole
                def ur = new UserRole(
                        user: user,
                        role: Role.get(roleid)
                )
                userroleList.add(ur)
            }
        }
        userService.addSave(user, userroleList)
        render Resp.toJson(RespType.SUCCESS,employeeid)
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
        LogSystemUtil.log(LogSystemUtil.INFO, "删除部分用户")
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
        [user: user, roleList: roleList, roleIdList: roleIdList, employee: Employee.get(user.employee), depts: Department.findAll()]
    }

    /**
     * 更新用户信息
     * @return -->/user/list
     */
    def editSave() {
        def userId = params.long("userId")
        def realName = params.get("realName")
        def username = params.get("username")
        def pwd = params.get("pwd")
        def enabled = params.get("enabled")
        enabled = enabled == 'true'
        def accountLocked = params.get("accountLocked")
        accountLocked = accountLocked == 'true'
        def checkroleList = params.get("checkRole")
        if(!(userId && realName && username && pwd)){
            render Resp.toJson(RespType.FAIL)
            return
        }
        def user = User.get(userId)
        def userroleList = new ArrayList<UserRole>()
        if (checkroleList instanceof String){
            def ur = new UserRole(
                    user: user,
                    role: Role.get(checkroleList as long)
            )
            userroleList.add(ur)
        }else{
            for (def roleid : checkroleList){
                //添加UserRole
                def ur = new UserRole(
                        user: user,
                        role: Role.get(roleid)
                )
                userroleList.add(ur)
            }
        }
        //执行修改
        userService.editSave(user, realName, username, pwd,enabled, accountLocked, userroleList)
        render Resp.toJson(RespType.SUCCESS)
    }
}
