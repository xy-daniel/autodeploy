package com.hxht.autodeploy.init

import com.hxht.techcrt.Dict
import com.hxht.techcrt.Role
import com.hxht.techcrt.User
import com.hxht.techcrt.UserRole
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
            if (!superRole.hasErrors()) {
                log.info("系统配置初始化：ROLE_SUPER 角色初始化完毕。")
            } else {
                log.error("系统配置初始化：ROLE_SUPER 角色初始化失败。[${superRole.errors}]")
            }
        }
        def adminRole = Role.findByAuthority("ROLE_ADMIN")
        if (!adminRole) {
            adminRole = new Role(authority: "ROLE_ADMIN", remark: "管理员", sequence: 0)
            adminRole.save(flush: true)
            if (!adminRole.hasErrors()) {
                log.info("系统配置初始化：ROLE_ADMIN 角色初始化完毕。")
            } else {
                log.error("系统配置初始化：ROLE_ADMIN 角色初始化失败。[${adminRole.errors}]")
            }
        }
        /*def todayTrialRole = Role.findByAuthority("ROLE_TODAYTRIAL")
        if (!todayTrialRole) {
            todayTrialRole = new Role(authority: "ROLE_TODAYTRIAL", remark: "今日庭审", sequence: 1)
            todayTrialRole.save(flush: true)
            if (!todayTrialRole.hasErrors()) {
                log.info("系统配置初始化：ROLE_TODAYTRIAL 角色初始化完毕。")
            } else {
                log.error("系统配置初始化：ROLE_TODAYTRIAL 角色初始化失败。[${todayTrialRole.errors}]")
            }
        }
        def trialListRole = Role.findByAuthority("ROLE_TRIALLIST")
        if (!trialListRole) {
            trialListRole = new Role(authority: "ROLE_TRIALLIST", remark: "庭审列表", sequence: 2)
            trialListRole.save(flush: true)
            if (!trialListRole.hasErrors()) {
                log.info("系统配置初始化：ROLE_TRIALLIST 角色初始化完毕。")
            } else {
                log.error("系统配置初始化：ROLE_TRIALLIST 角色初始化失败。[${trialListRole.errors}]")
            }
        }
        def analyzeRole = Role.findByAuthority("ROLE_ANALYZE")
        if (!analyzeRole) {
            analyzeRole = new Role(authority: "ROLE_ANALYZE", remark: "统计分析", sequence: 3)
            analyzeRole.save(flush: true)
            if (!analyzeRole.hasErrors()) {
                log.info("系统配置初始化：ROLE_ANALYZE 角色初始化完毕。")
            } else {
                log.error("系统配置初始化：ROLE_ANALYZE 角色初始化失败。[${analyzeRole.errors}]")
            }
        }
        def configRole = Role.findByAuthority("ROLE_CONFIG")
        if (!configRole) {
            configRole = new Role(authority: "ROLE_CONFIG", remark: "配置与管理", sequence: 4)
            configRole.save(flush: true)
            if (!configRole.hasErrors()) {
                log.info("系统配置初始化：ROLE_CONFIG 角色初始化完毕。")
            } else {
                log.error("系统配置初始化：ROLE_CONFIG 角色初始化失败。[${configRole.errors}]")
            }
        }
        def toolRole = Role.findByAuthority("ROLE_TOOL")
        if (!toolRole) {
            toolRole = new Role(authority: "ROLE_TOOL", remark: "管理员工具", sequence: 5)
            toolRole.save(flush: true)
            if (!toolRole.hasErrors()) {
                log.info("系统配置初始化：ROLE_TOOL 角色初始化完毕。")
            } else {
                log.error("系统配置初始化：ROLE_TOOL 角色初始化失败。[${toolRole.errors}]")
            }
        }*/
    }
    /**
     * 系统配置初始化用户，并赋予权限。
     */
    def user() {
        def superUser = User.findByUsername("super")
        if (!superUser) {
            superUser = new User(username: "super", password: "1", realName: "超级管理员", enabled: true)
            superUser.save(flush: true)
            if (!superUser.hasErrors()) {
                log.info("系统配置初始化：super 账号初始化完毕，super。")
            } else {
                log.error("系统配置初始化：super 账号初始化失败。[${superUser.errors}]")
            }
        }
        //分配超级管理员角色
        def superRole = Role.findByAuthority("ROLE_SUPER")
        if (!superUser.hasErrors() && superRole) {
            def superUserRole = UserRole.findByUserAndRole(superUser, superRole)
            if (!superUserRole) {
                superUserRole = new UserRole(user: superUser, role: superRole)
                superUserRole.save(flush: true)
                if (!superUserRole.hasErrors()) {
                    log.info("系统配置初始化：super 角色分配完毕。")
                } else {
                    log.error("系统配置初始化：super 角色分配失败。[${superUserRole.errors}]")
                }
            }
        }
    }

    def dict() {
        def confDict = Dict.findByCode("SYSTEM_CONFIG")
        if (!confDict) {
            confDict = new Dict(name: "系统配置", code: "SYSTEM_CONFIG", type: "String", state: 3, notes: "系统配置")
            confDict.save(flush: true)
            if (!confDict.hasErrors()) {
                log.info("系统配置初始化：字典 SYSTEM_CONFIG 初始化完毕。")
            } else {
                log.error("系统配置初始化：字典 SYSTEM_CONFIG 初始化失败。[${confDict.errors}]")
            }
        }
        def confCourtDict = Dict.findByCode("CURRENT_COURT")
        if (!confCourtDict) {
            confCourtDict = new Dict(name: "所在法院", code: "CURRENT_COURT", type: "String", val: "", ext5: "庭审管理系统", state: 1, notes: "项目所在法院", parent: confDict)
            confCourtDict.save(flush: true)
            if (!confCourtDict.hasErrors()) {
                log.info("系统配置初始化：字典 CENTRAL_COURT 初始化完毕。")
            } else {
                log.error("系统配置初始化：字典 CENTRAL_COURT 初始化失败。[${confCourtDict.errors}]")
            }
        } else {
            if (!confCourtDict.ext5) {
                confCourtDict.ext5 = "科技法庭管理系统"
                confCourtDict.save(flush: true)
                if (!confCourtDict.hasErrors()) {
                    log.info("系统配置初始化：字典 CENTRAL_COURT 初始化完毕。")
                } else {
                    log.error("系统配置初始化：字典 CENTRAL_COURT 初始化失败。[${confCourtDict.errors}]")
                }
            }
        }

        //项目所在服务器ip地址
        def confServiceIpDict = Dict.findByCode("CURRENT_SERVICE_IP")
        if (!confServiceIpDict) {
            confServiceIpDict = new Dict(name: "项目所在服务器IP", code: "CURRENT_SERVICE_IP", type: "String", val: "127.0.0.1", state: 1, notes: "项目所在服务器IP，部署后必改。", parent: confDict)
            confServiceIpDict.save(flush: true)
            if (!confServiceIpDict.hasErrors()) {
                log.info("系统配置初始化：字典 CURRENT_SERVICE_IP 初始化完毕。")
            } else {
                log.error("系统配置初始化：字典 CURRENT_SERVICE_IP 初始化失败。[${confServiceIpDict.errors}]")
            }
        }
        //项目所在服务器端口地址
        def confServicePortDict = Dict.findByCode("CURRENT_SERVICE_PORT")
        if (!confServicePortDict) {
            confServicePortDict = new Dict(name: "项目所在服务器端口", code: "CURRENT_SERVICE_PORT", type: "String", val: "80", state: 1, notes: "项目所在服务器端口，部署后必改。", parent: confDict)
            confServicePortDict.save(flush: true)
            if (!confServicePortDict.hasErrors()) {
                log.info("系统配置初始化：字典 CURRENT_SERVICE_PORT 初始化完毕。")
            } else {
                log.error("系统配置初始化：字典 CURRENT_SERVICE_PORT 初始化失败。[${confServicePortDict.errors}]")
            }
        }
        //项目所在服务器服务地址
        def confServicePathDict = Dict.findByCode("CURRENT_SERVICE_PATH")
        if (!confServicePathDict) {
            confServicePathDict = new Dict(name: "项目所在服务器服务地址", code: "CURRENT_SERVICE_PATH", type: "String", val: "http://127.0.0.1:80/", state: 1, notes: "项目所在服务器服务地址，部署后必改。", parent: confDict)
            confServicePathDict.save(flush: true)
            if (!confServicePathDict.hasErrors()) {
                log.info("系统配置初始化：字典 CURRENT_SERVICE_PATH 初始化完毕。")
            } else {
                log.error("系统配置初始化：字典 CURRENT_SERVICE_PATH 初始化失败。[${confServicePathDict.errors}]")
            }
        }


        //服务器内存/磁盘报警大小
        def memAlarmDict = Dict.findByCode("CURRENT_MEM_ALARM")
        if (!memAlarmDict) {
            memAlarmDict = new Dict(name: "服务器内存/磁盘报警大小", code: "CURRENT_MEM_ALARM", type: "String", val: "2", ext1: "20", state: 1, notes: "服务器内存/磁盘报警大小,单位（G）", parent: confDict)
            memAlarmDict.save(flush: true)
            if (!memAlarmDict.hasErrors()) {
                log.info("系统配置初始化：字典 CURRENT_MEM_ALARM 初始化完毕。")
            } else {
                log.error("系统配置初始化：字典 CURRENT_MEM_ALARM 初始化失败。[${memAlarmDict.errors}]")
            }
        }

        //对接深圳点播平台系统服务器配置
        def showVideoPlatform = Dict.findByCode("ShowVideoPlatform")
        if (!showVideoPlatform) {
            showVideoPlatform = new Dict(name: "对接深圳点播平台系统", code: "ShowVideoPlatform", type: "String", val: "", state: 1, notes: "对接点播平台系统的地址，部署后用到对接的需要修改！", parent: confDict)
            showVideoPlatform.save(flush: true)
            if (!showVideoPlatform.hasErrors()) {
                log.info("系统配置初始化：字典 ShowVideoPlatform 初始化完毕。")
            } else {
                log.error("系统配置初始化：字典 ShowVideoPlatform 初始化失败。[${showVideoPlatform.errors}]")
            }
        }
    }
}
