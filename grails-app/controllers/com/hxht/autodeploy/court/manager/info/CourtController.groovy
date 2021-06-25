package com.hxht.autodeploy.court.manager.info

import com.hxht.techcrt.Dict
import com.hxht.techcrt.Resp
import com.hxht.techcrt.enums.RespType

/**
 * 法院配置
 */
class CourtController {

    /**
     * 配置页面
     */
    def index() {
        [court: Dict.findByCode("CURRENT_COURT")]
    }

    /**
     * 保存法院信息
     */
    def courtSave() {
        def court = Dict.get(params.long("id"))
        court.properties = params
        court.save(flush: true)
        if(court.hasErrors()){
            log.error("[CourtController courtSave] 保存法院信息失败\n错误信息：${court.errors}")
            render Resp.toJson(RespType.FAIL)
            return
        }
        render Resp.toJson(RespType.SUCCESS)
    }
}
