package technologcourt

import com.hxht.autodeploy.OSAnalysisJob
import com.hxht.autodeploy.init.InitService

class BootStrap {
    InitService initService
    def init = { servletContext ->
        try {
            initService.role()
            initService.user()
            initService.dict()
            OSAnalysisJob.exec()
        } catch (e) {
            e.printStackTrace()
        }
    }
    def destroy = {
    }
}
