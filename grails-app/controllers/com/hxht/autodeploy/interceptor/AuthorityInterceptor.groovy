package com.hxht.autodeploy.interceptor

import grails.plugin.springsecurity.SpringSecurityService
import org.springframework.security.access.AccessDeniedException

class AuthorityInterceptor {
    SpringSecurityService springSecurityService

    AuthorityInterceptor() {
        matchAll()
                .excludes(controller: "login")              //登录
                .excludes(controller: "logout")             //登出
                .excludes(controller: "error")              //错误
    }

    boolean before() {
        //获取请求路径
        def url = request.getRequestURI().substring(request.getContextPath().length())
        //拦截除一下所有的controller
        if (url.indexOf("stomp") != -1) {
            return true
        }
        def path = url.substring(url.indexOf("/tc") + 1)
        //获取当前用户的所有角色权限
        def auths = springSecurityService.authentication.authorities

        //将路径后的id去除
        def lastPath = path.substring(path.lastIndexOf("/") + 1)
        if (lastPath && lastPath.isNumber()) {
            path = path.substring(0, path.lastIndexOf("/"))
        }
        for (def auth : auths) {
            if (auth.toString() == "ROLE_SUPER" || auth.toString() == "ROLE_ADMIN") {
                return true
            }
            //获取路径对比是否具有权限没有则返回false。跳转无权限页
            if ('ROLE_' + path == auth.toString()) {
                return true
            } else {
                if ((path.contains("/add") && auth.toString().contains("/add")) || (path.contains("/edit") && auth.toString().contains("/edit"))) {
                    return true
                }
            }
        }
        //访问首页判断是否具有首页访问权限 ，如果没有则跳转到下一个有权限的页面
        if ('ROLE_' + path == 'ROLE_/') {
            for (def auth : auths) {
                if (auth.toString() == 'ROLE_/') {
                    return true
                }
            }
            for (def auth : auths) {
                if (auth.toString() == 'ROLE_/plan/list') {
                    redirect(controller: "plan", action: "list")
                    return true
                } else if (auth.toString() == 'ROLE_/charts/index') {
                    redirect(controller: "charts", action: "index")
                    return true
                } else if (auth.toString() == 'ROLE_/courtroom/list') {
                    redirect(controller: "courtroom", action: "list")
                    return true
                } else if (grailsApplication.config.getProperty('pageVersion') == 'v2' && auth.toString() == 'ROLE_/distance/index') {
                    redirect(controller: "distance", action: "index")
                    return true
                } else if (grailsApplication.config.getProperty('pageVersion') == 'v2' && auth.toString() == 'ROLE_/mountDisk/list') {
                    redirect(controller: "mountDisk", action: "list")
                    return true
                } else if (grailsApplication.config.getProperty('pageVersion') == 'v2' && auth.toString() == 'ROLE_/systemTitle/edit') {
                    redirect(controller: "systemTitle", action: "edit")
                    return true
                } else if (grailsApplication.config.getProperty('pageVersion') == 'v2' && auth.toString() == 'ROLE_/osInfo/list') {
                    redirect(controller: "osInfo", action: "list")
                    return true
                }
            }
        }
        throw new AccessDeniedException(
                "您缺少`ROLE_${path}`权限，请联系管理员分配权限!\n" +
                        "如果您拥有权限管理相关权限,您可以这样操作(当然仍然建议直接寻找管理员使用管理员账户执行操作)：\n" +
                        "1、选择配置与管理-院信息管理-系统功能管理，此时您可以查找是否有您刚刚操作的${path}\n" +
                        "1.1、如果有,您可以直接跳过系统功能管理的操作.\n" +
                        "1.2、如果没有,您必须点击添加功新功能,执行以下操作:\n" +
                        "1.2.1、`名称,类型，上级类型`您暂时可以随便写,但是请通知管理员您进行了怎样的操作.\n" +
                        "1.2.2、`路径`请填写${path}.\n" +
                        "2、选择配置与管理-院信息管理-用户管理\n" +
                        "2.1、找到您的用户信息\n" +
                        "2.2、点击编辑查看您具有的用户权限\n" +
                        "2.2.1、如果已经勾选角色,请记住您现在拥有什么角色权限,请勿随意勾选权限.\n" +
                        "2.2.2、如果没有勾选,请直接联系管理员进行操作，当然在系统没有bug的情况下，不认为您能在没有角色的情况下可以访问此页面.\n" +
                        "3、选择配置与管理-院信息管理-角色管理\n" +
                        "3.1、找到您的用户所具有的权限，如果您拥有多个角色,请尽可能修改您认为拥有权限最大的角色.\n" +
                        "3.2、点击编辑,执行以下操作:" +
                        "3.2.1、在功能权限中勾选您需要的权限,但是请通知管理员您进行了怎样的操作.\n" +
                        "3.2.2、点击确认执行保存操作.\n" +
                        "4、请点击右上角`欢迎,###`,选择登出之后重新登录以执行您刚刚执行失败的操作."
        )
    }

    boolean after() {
        true
    }

    void afterView() {
        // no-op
    }
}
