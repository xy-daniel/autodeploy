package autodeploy

import com.hxht.autodeploy.enums.Resp
import com.hxht.autodeploy.enums.RespType
import org.grails.web.errors.GrailsWrappedRuntimeException

class ErrorController {

    def index() {
        def accept = request.getHeader("accept")
        if (!(accept?.indexOf("json") > -1)) {
            render(view: '/error')
        } else {
            response.setContentType("application/json; charset=utf-8")
            response.status = 200
            def exception = request["exception"] as GrailsWrappedRuntimeException
            def msg = ""
            if (exception) {
                msg = exception.message
            }

            if (msg && msg.indexOf("证书无效") > -1) {
                render Resp.toJson(RespType.ACCOUNT_EXPIRED, msg)
            } else {
                render Resp.toJson(RespType.FAIL, msg)
            }

        }

    }
}
