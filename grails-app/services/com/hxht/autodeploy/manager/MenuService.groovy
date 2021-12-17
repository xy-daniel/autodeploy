package com.hxht.autodeploy.manager

import com.hxht.autodeploy.manager.authority.Menu
import com.hxht.autodeploy.enums.MenuStatus
import com.hxht.autodeploy.manager.authority.UserRole
import grails.gorm.transactions.Transactional

/**
 * 菜单操作Service by Arctic in 2019.10.22
 */
@Transactional
class MenuService {
    /**
     * 菜单列表
     */
    def list() {
        def menuList = Menu.findAll()
        def modelDataList = []
        for (def menu : menuList) {
            def data = [:]
            data.put("id", menu.id)
            data.put("parentId", menu.parentId)
            data.put("name", menu.name)
            data.put("url", menu.url)
            data.put("type", MenuStatus.getString(menu.type))
            data.put("orderNum", menu.orderNum)
            modelDataList.add(data)
        }
        modelDataList
    }

    /**
     * 菜单添加
     * @param menu  菜单信息
     * @param ur  角色信息
     */
    def addSave(Menu menu,List<UserRole> ur){
        menu.save(flush:true)
        if (menu.hasErrors()) {
            def msg = "[MenuService addSave menu.save]菜单添加保存失败 errors [${menu.errors}]"
            log.error(msg)
            throw new RuntimeException(msg)
        }
        for (def userrole: ur){
            userrole.save(flush:true)
            if (userrole.hasErrors()) {
                def msg = "[MenuService addSave userrole.save]菜单添加保存失败 errors [${userrole.errors}]"
                log.error(msg)
                throw new RuntimeException(msg)
            }
        }
    }

    /**
     * 菜单删除
     * @param id
     */
    def del(long id){
        def menu= Menu.get(id)
        menu.delete(flush:true)
        if (menu.hasErrors()) {
            def msg = "[MenuService del]菜单删除失败 errors [${menu.errors}]"
            log.error(msg)
            throw new RuntimeException(msg)
        }
        def menuList = Menu.findAllByParentId(id)//查询所有的子项目并将其删除
        for (Menu mu : menuList){
            //删除菜单
            mu.delete(flush:true)
            if (mu.hasErrors()) {
                def msg = "[MenuService del]菜单删除失败 errors [${mu.errors}]"
                log.error(msg)
                throw new RuntimeException(msg)
            }
            
        }
    }

    /**
     * 菜单信息修改
     */
    def editSave(Menu menu){
        //更新菜单信息表
        menu.save(flush: true)
        if (menu.hasErrors()) {
            def msg = "[MenuService editSave]菜单修改保存失败 errors [${menu.errors}]"
            log.error(msg)
            throw new RuntimeException(msg)
        }
    }
}
