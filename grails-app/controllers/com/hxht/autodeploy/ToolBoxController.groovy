package com.hxht.autodeploy

import grails.plugin.springsecurity.annotation.Secured

/**
 * 管理员工具
 * 2021.04.20 >>> 修改书记员更新包上传路径 daniel
 */
@Secured(['ROLE_SUPER'])
class ToolBoxController {

    def about() {}

}
