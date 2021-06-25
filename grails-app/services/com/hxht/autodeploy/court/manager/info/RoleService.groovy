package com.hxht.autodeploy.court.manager.info

import com.hxht.techcrt.Menu
import com.hxht.techcrt.Role
import com.hxht.techcrt.RoleMenu
import com.hxht.techcrt.UserRole
import grails.gorm.transactions.Transactional

/**
 * 角色操作Service by Arctic in 2019.10.22
 */
@Transactional
class RoleService {
    /**
     * 角色列表
     * @param draw  标志
     * @param start  起始坐标
     * @param length  长度
     * @param search  搜索关键词
     * @return  List<Role>
     */
    def list(int draw, int start, int length, String search) {
        def model = [:]
        model.put("draw", draw)
        def count = Role.createCriteria().count() {
            if (search) {
                or {
                    like("authority", "%${search}%")
                    like("remark", "%${search}%")
                }
            }
        }
        def dataList = Role.createCriteria().list {
            and {
                setMaxResults(length)
                setFirstResult(start)
            }
            or {
                like("authority", "%${search}%")
                like("remark", "%${search}%")
            }
//            order("dateCreated","desc")
        } as List<Role>
        def modelDataList = []
        for (def role : dataList) {
            //id--角色名--真实姓名--注册时间--最后登录时间
            def data = [:]
            data.put("id", role.id)
            data.put("sequence", role.sequence)
            data.put("authority", role.authority)
            data.put("remark", role.remark)
            modelDataList.add(data)
        }
        model.put("recordsTotal", count)//数据总条数
        model.put("recordsFiltered", count)//显示的条数
        model.put("data", modelDataList)
        model
    }

    /**
     * 角色添加
     * @param role  角色信息
     * @param ur  角色信息
     */
    def addSave(Role role, String ids){
        role.save(flush:true)
        if (role.hasErrors()) {
            def msg = "[RoleService addSave]新增角色失败 errors [${role.errors}]"
            log.error(msg)
            throw new RuntimeException(msg)
        }
        if (ids){
            def idList = ids.split(',')
            for (def id : idList){
                if (id){
                    def menu = Menu.get(id as long)
                    def roleMenu = new RoleMenu()
                    roleMenu.role = role
                    roleMenu.menu = menu
                    roleMenu.save(flush:true)
                    if (roleMenu.hasErrors()) {
                        def msg = "[RoleService addSave]新增角色失败 errors [${roleMenu.errors}]"
                        log.error(msg)
                        throw new RuntimeException(msg)
                    }
                }
            }
        }

    }

    /**
     * 角色添加
     * @param role  角色信息
     * @param ur  角色信息
     */
    def editSave(Role role, String ids){
        role.save(flush:true)
        if (role.hasErrors()) {
            def msg = "[RoleService addSave]新增角色失败 errors [${role.errors}]"
            log.error(msg)
            throw new RuntimeException(msg)
        }
        if (ids){
            def roleMenuList = RoleMenu.findAllByRole(role)
            for (def roleMenu : roleMenuList){
                roleMenu.delete(flush: true)
            }
            def idList = ids.split(',')
            for (def id : idList){
                if (id){
                    def menu = Menu.get(id as long)
                    def roleMenu = new RoleMenu()
                    roleMenu.role = role
                    roleMenu.menu = menu
                    roleMenu.save(flush:true)
                    if (roleMenu.hasErrors()) {
                        def msg = "[RoleService addSave]新增角色失败 errors [${roleMenu.errors}]"
                        log.error(msg)
                        throw new RuntimeException(msg)
                    }
                }
            }
        }

    }

    def roleMenuSave(String id){

    }

    /**
     * 角色删除
     * @param ids  多个id
     */
    def del(String[] ids){
        for (String id : ids){
            //获取角色
            def role = Role.get(id)
            //删除UserRole
            UserRole.findAllByRole(role)*.delete(flush:true)
            //删除RoleMenu
            RoleMenu.findAllByRole(role)*.delete(flush:true)
            //删除角色
            role.delete(flush:true)
            if (role.hasErrors()) {
                def msg = "[RoleService del]删除角色失败 errors [${role.errors}]"
                log.error(msg)
                throw new RuntimeException(msg)
            }
        }
    }


    /**
     * 设置菜单信息
     * @param role  角色信息
     */
    def editMenu(Role role){
        role.save(flush: true)
        if (role.hasErrors()) {
            def msg = "[RoleService editMenu]修改角色信息保存失败 errors [${role.errors}]"
            log.error(msg)
            throw new RuntimeException(msg)
        }
    }
}
