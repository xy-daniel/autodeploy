package com.hxht.autodeploy.court.manager.info

import com.hxht.techcrt.Menu
import com.hxht.techcrt.MenuStatus
import com.hxht.techcrt.Resp
import com.hxht.techcrt.Role
import com.hxht.techcrt.RoleMenu
import com.hxht.techcrt.enums.RespType
import grails.converters.JSON
/**
 * 菜单管理控制器 by Arctic in 2019.10.22
 */
class MenuController {

    MenuService menuService

    /**
     * 菜单列表
     * @return view="menu.list"
     */
    def list() {
        if (request.method == "POST") {
            def model = menuService.list()
            render model as JSON
        }
    }

    /**
     * 前往添加菜单页面
     * @return view="menu.save"
     */
    def add() {
        //获取所有的菜单
        def menuList = Menu.findAll()
        [ menuList: menuList]
    }

    /**
     * 保存菜单
     * @return -->/menu/list
     */
    def addSave() {
        //获取这个菜单信息
        def menu = new Menu()
        menu.properties = params
        //执行修改
        menuService.editSave(menu)
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 根据菜单名获取菜单信息
     * @return RespType.data
     */
    def getUserByUsername() {
        def count = Menu.countByname(params.get("username") as String)
        render Resp.toJson(RespType.SUCCESS, count)
    }

    /**
     * 删除部分菜单
     * @return RespType.data
     */
    def del() {
        def id = params.long("id")
        menuService.del(id)
        redirect(controller: "menu", action: "list")
    }

    /**
     * 前往修改菜单信息页面
     * @return -->view="menu.edit"
     */
    def edit() {
        //获取菜单
        def menu = Menu.get(params.long("id"))
        //获取所有的菜单
        def menuList = Menu.findAll()
        [menu: menu, menuList: menuList]
    }

    /**
     * 更新菜单信息
     * @return -->/menu/list
     */
    def editSave() {
        //获取这个菜单信息
        def menu = Menu.get(params.long("menuId"))
        menu.properties = params
        //执行修改
        menuService.editSave(menu)
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 获取所有的角色菜单
     * @return -->/menu/info
     */
    def info() {
        //获取这个菜单信息
        def role = Role.get(params.long("id"))
        def roleMenuList = RoleMenu.findAllByRole(role)
        def modelDataList = []
        for (def roleMenu : roleMenuList) {
            def menu = roleMenu.menu
            def data = [:]
            data.put("id", menu.id)
            data.put("parentId", menu.parentId)
            data.put("name", menu.name)
            data.put("url", menu.url)
            data.put("type", MenuStatus.getString(menu.type))
            data.put("orderNum", menu.orderNum)
            modelDataList.add(data)
        }
        render modelDataList as JSON
    }

}
