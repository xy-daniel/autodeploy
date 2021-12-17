package autodeploy

import com.hxht.autodeploy.enums.Resp
import com.hxht.autodeploy.enums.RespType


class AuthorityController {

    def index() {
        def accept = request.getHeader("accept")
        if (!(accept?.indexOf("json") > -1)) {
            render(view: '/authority')
        } else {
            response.setContentType("application/json; charset=utf-8")
            response.status = 300
            render Resp.toJson(RespType.NO_AUTHORIZED)
        }

    }
}
