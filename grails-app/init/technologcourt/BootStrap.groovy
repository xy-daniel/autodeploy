package technologcourt

import com.hxht.techcrt.Dict
import com.hxht.techcrt.court.manager.SystemController
import com.hxht.techcrt.init.InitService
import com.hxht.techcrt.sync.util.PushUtil
import com.hxht.techcrt.utils.ExceptionUtil
import com.hxht.techcrt.utils.RegisterUtil
import grails.core.GrailsApplication
import grails.events.EventPublisher

class BootStrap implements EventPublisher{
    InitService initService
    GrailsApplication grailsApplication
    def init = { servletContext ->
        try {
            initService.role()
            initService.user()
            initService.dict()
            SystemController.currentCourt = Dict.findByCode("CURRENT_COURT")
            PushUtil.initCentralUrlList(true)
            log.info("++++++++ 开始安装证书 ++++++++")
            def subject = grailsApplication.config.getProperty('tc.license.subject')
            def publicAlias = grailsApplication.config.getProperty('tc.license.publicAlias')
            def storePass = grailsApplication.config.getProperty('tc.license.storePass')
            def licensePath = grailsApplication.config.getProperty('tc.license.licensePath')
            def publicKeysStorePath = grailsApplication.config.getProperty('tc.license.publicKeysStorePath')
            RegisterUtil.registerInstall(subject, publicAlias, storePass, licensePath, publicKeysStorePath)
            log.info("++++++++ 证书安装结束 ++++++++")
            this.notify("planSyc")
        }catch (e) {
            e.printStackTrace()
            def msg = "[BootStrap.init] 初始化数据\nERROR[${e.message}]-----[${ExceptionUtil.getStackTrace(e)}]"
            log.error("[BootStrap.init]${msg}")
        }
    }
    def destroy = {
    }
}
