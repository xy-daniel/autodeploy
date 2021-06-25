package com.hxht.autodeploy.court.manager.info

import com.hxht.techcrt.LogSystemUtil
import com.hxht.techcrt.Resp
import com.hxht.techcrt.Role
import com.hxht.techcrt.RoleMenu
import com.hxht.techcrt.enums.RespType
import grails.converters.JSON

/**
 * 角色管理控制器 by Arctic in 2019.10.22
 */
class RoleController {

    RoleService roleService

    /**
     * 角色列表
     * @return view="role.list"
     */
    def list() {
        if (request.method == "POST") {
            def draw = params.int("draw") ?: 1// 记录操作的次数 每次加1
            def start = params.int("start") ?: 0// 起始
            def length = params.int("length") ?: 20// 每页显示的size
            def search = params.get("search[value]") as String//搜索内容
            def model = roleService.list(draw, start, length, search)
            render model as JSON
        }
    }

    /**
     * 前往添加角色页面
     * @return view="role.save"
     */
    def add() {
        def roleList = Role.findAll()
        def employeeid = params.long("id")
        [roleList: roleList,employeeid: employeeid]
    }

    /**
     * 保存角色
     * @return -->/role/list
     */
    def addSave() {
        def role = new Role(params)
        def ids = params.ids
        roleService.addSave(role,ids)
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 根据角色名获取角色信息
     * @return RespType.data
     */
    def getRoleByAuthority() {
        def count = Role.countByAuthority(params.get("authority") as String)
        render Resp.toJson(RespType.SUCCESS, count)
    }

    /**
     * 根据remark取remark信息
     * @return RespType.data
     */
    def getRoleByRemark() {
        def count = Role.countByRemark(params.get("remark") as String)
        render Resp.toJson(RespType.SUCCESS, count)
    }
    /**
     * 删除部分角色
     * @return RespType.data
     */
    def del() {
        LogSystemUtil.log(LogSystemUtil.INFO,"删除部分角色")
        def roleIdStr = params.get("roleIds") as String
        if (!roleIdStr) {
            render Resp.toJson(RespType.DATA_NOT_EXIST)
            return
        }
        def roleIdArr = roleIdStr.split(",")
        roleService.del(roleIdArr)
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 前往修改角色信息页面
     * @return -->view="role.edit"
     */
    def edit() {
        //获取角色
        def role = Role.get(params.long("id"))
        def roleMenuList = RoleMenu.findAllByRole(role)
        [role: role]
    }

    /**
     * 更新角色信息
     */
    def editSave() {
        def role = Role.get(params.long("roleId"))//获取角色信息
        role.properties = params
        def ids = params.ids
        roleService.editSave(role,ids)
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 设置菜单权限
     * @return -->/role/list
     */
    def editMenu() {
        //获取这个角色信息
        def role = Role.get(params.long("roleId"))//获取角色信息
        role.properties = params
        roleService.editMenu(role)
        render Resp.toJson(RespType.SUCCESS)
    }
}
