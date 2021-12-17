package com.hxht.autodeploy.init

import com.hxht.autodeploy.Dict
import com.hxht.autodeploy.manager.authority.Role
import com.hxht.autodeploy.manager.authority.User
import com.hxht.autodeploy.manager.authority.UserRole
import grails.gorm.transactions.Transactional

/**
 * 初始化系统字典、系统角色、超级管理员用户
 */
@Transactional
class InitService {
    /**
     * 系统配置初始化角色
     */
    def role() {
        def superRole = Role.findByAuthority("ROLE_SUPER")
        if (!superRole) {
            superRole = new Role(authority: "ROLE_SUPER", remark: "超级管理员", sequence: 0)
            superRole.save(flush: true)
            if (superRole.hasErrors()) {
                throw new RuntimeException("系统配置初始化:ROLE_SUPER 角色初始化失败.")
            }
            log.info("系统配置初始化:ROLE_SUPER 角色初始化完毕.")
        }
        def adminRole = Role.findByAuthority("ROLE_ADMIN")
        if (!adminRole) {
            adminRole = new Role(authority: "ROLE_ADMIN", remark: "管理员", sequence: 1)
            adminRole.save(flush: true)
            if (adminRole.hasErrors()) {
                throw new RuntimeException("系统配置初始化:ROLE_ADMIN 角色初始化失败.")
            }
            log.info("系统配置初始化:ROLE_ADMIN 角色初始化完毕.")
        }
    }
    /**
     * 系统配置初始化用户，并赋予权限.
     */
    def user() {
        def superUser = User.findByUsername("super")
        if (!superUser) {
            superUser = new User(username: "super", password: "1", realName: "超级管理员", enabled: true)
            superUser.save(flush: true)
            if (superUser.hasErrors()) {
                throw new RuntimeException("系统配置初始化:super 账号初始化失败.")
            }
            log.info("系统配置初始化:super 账号初始化完毕.")
        }
        def adminUser = User.findByUsername("admin")
        if (!adminUser) {
            adminUser = new User(username: "admin", password: "1", realName: "管理员", enabled: true)
            adminUser.save(flush: true)
            if (adminUser.hasErrors()) {
                throw new RuntimeException("系统配置初始化:admin 账号初始化失败.")
            }
            log.info("系统配置初始化:admin 账号初始化完毕.")
        }
        def superRole = Role.findByAuthority("ROLE_SUPER")
        if (!superUser.hasErrors() && superRole) {
            def superUserRole = UserRole.findByUserAndRole(superUser, superRole)
            if (!superUserRole) {
                superUserRole = new UserRole(user: superUser, role: superRole)
                superUserRole.save(flush: true)
                if (superUserRole.hasErrors()) {
                    throw new RuntimeException("系统配置初始化:super 角色分配失败.")
                }
                log.info("系统配置初始化:super 角色分配完毕.")
            }
        }
        def adminRole = Role.findByAuthority("ROLE_ADMIN")
        if (!adminUser.hasErrors() && adminRole) {
            def adminUserRole = UserRole.findByUserAndRole(adminUser, adminRole)
            if (!adminUserRole) {
                adminUserRole = new UserRole(user: adminUser, role: adminRole)
                adminUserRole.save(flush: true)
                if (adminUserRole.hasErrors()) {
                    throw new RuntimeException("系统配置初始化:super 角色分配失败.")
                }
                log.info("系统配置初始化:super 角色分配完毕.")
            }
        }
    }

    def dict() {
        def confDict = Dict.findByCode("SYSTEM_CONFIG")
        if (!confDict) {
            confDict = new Dict(name: "系统配置", code: "SYSTEM_CONFIG", state: 3, notes: "系统配置")
            confDict.save(flush: true)
            if (!confDict.hasErrors()) {
                log.info("系统配置初始化:字典 SYSTEM_CONFIG 初始化完毕.")
            } else {
                log.error("系统配置初始化:字典 SYSTEM_CONFIG 初始化失败.[${confDict.errors}]")
            }
        }
        def confCourtDict = Dict.findByCode("CURRENT_COURT")
        if (!confCourtDict) {
            confCourtDict = new Dict(name: "所在法院", code: "CURRENT_COURT", type: "String", val: "", ext5: "庭审管理系统", state: 1, notes: "项目所在法院", parent: confDict)
            confCourtDict.save(flush: true)
            if (!confCourtDict.hasErrors()) {
                log.info("系统配置初始化:字典 CENTRAL_COURT 初始化完毕.")
            } else {
                log.error("系统配置初始化:字典 CENTRAL_COURT 初始化失败.[${confCourtDict.errors}]")
            }
        } else {
            if (!confCourtDict.ext5) {
                confCourtDict.ext5 = "自动部署平台"
                confCourtDict.save(flush: true)
                if (!confCourtDict.hasErrors()) {
                    log.info("系统配置初始化:字典 CENTRAL_COURT 初始化完毕.")
                } else {
                    log.error("系统配置初始化:字典 CENTRAL_COURT 初始化失败.[${confCourtDict.errors}]")
                }
            }
        }
    }

    def initBasePath(String basePath) {
        def file = new File(basePath)
        if (!file.exists()) {
            file.mkdir()
            file.canRead()
            file.canWrite()
            file.canExecute()
        }
    }
}
