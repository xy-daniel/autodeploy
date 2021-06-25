package autodeploy

import com.hxht.techcrt.utils.FileUtil
import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.SpringSecurityUtils
import org.springframework.security.web.WebAttributes

class LoginController {
    SpringSecurityService springSecurityService

    def authfail() {
        String msg = ''
        def exception = session[WebAttributes.AUTHENTICATION_EXCEPTION]
        if (exception) {
            msg = exception.message
        }

        if (springSecurityService.isAjax(request)) {
            render([error: msg] as JSON)
        } else {
            flash.message = msg
            redirect action: 'auth', params: params
        }
    }

    /**
     * 登录页面文件下载
     */
    def download () {
        String path = File.separator + "usr" + File.separator + "local" + File.separator + "movies" + File.separator + "sysFile" + File.separator + params.get("file") as String
        FileUtil.download(response, path)
    }

    def auth() {

        def conf = getConf()

        if (springSecurityService.isLoggedIn()) {
            redirect uri: conf.successHandler.defaultTargetUrl
            return
        }

        String postUrl = request.contextPath + conf.apf.filterProcessesUrl
        render view: 'auth', model: [postUrl: postUrl,
                                     rememberMeParameter: conf.rememberMe.parameter,
                                     usernameParameter: conf.apf.usernameParameter,
                                     passwordParameter: conf.apf.passwordParameter,
                                     gspLayout: conf.gsp.layoutAuth,
                                     contentPath: request.contextPath]
    }

    protected ConfigObject getConf() {
        SpringSecurityUtils.securityConfig
    }
}
