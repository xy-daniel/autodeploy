package com.hxht.autodeploy.api

import com.hxht.techcrt.CourtroomStatus
import com.hxht.techcrt.DataStatus
import com.hxht.techcrt.Resp
import com.hxht.techcrt.court.Courtroom
import com.hxht.techcrt.enums.RespType
import com.hxht.techcrt.utils.ManagerHostApiUtil

/**
 * 庭审主机静音接口
 */
class ApiVolumeControlsController {

    /**
     * 修改页面
     */
    def edit() {}

    /**
     * 返回正在使用的法庭列表
     * @return 法庭列表
     */
    def editConfig() {
        List list = new ArrayList()
        list.add(CourtroomStatus.NORMAL)
        list.add(CourtroomStatus.OCCUPIED)
        def courtroomList = Courtroom.findAllByStatusInListAndActive(list, DataStatus.SHOW, [sort: "sequence", order: "asc"])
        [courtroomList: courtroomList]
    }

    /**
     * 发送静音指令
     */
    def send() {
        try {
            def chnArr = params.get("chn[]")
            def ip = params.get("ip") as String
            def isMute = params.int("isMute") as Boolean
            final String postUrl = "http://${ip}" + "/web/page/activeProtocol.action"
            def result
            if (chnArr instanceof String) {
                result = ManagerHostApiUtil.setAudioProcessorInputIsMuteProfile(chnArr, isMute, postUrl)
                if (result.result.params.retCode != 0) {
                    render Resp.toJson(RespType.FAIL)
                    return
                }
            } else {
                for (def chn : chnArr) {
                    result = ManagerHostApiUtil.setAudioProcessorInputIsMuteProfile(chn, isMute, postUrl)
                    if (result.result.params.retCode != 0) {
                        render Resp.toJson(RespType.FAIL)
                        return
                    }
                }
            }
            render Resp.toJson(RespType.SUCCESS)
        } catch (e) {
            log.error("[ApiVolumeControlsController send] 通庭审主机失败，错误信息：${e.message}")
            render Resp.toJson(RespType.FAIL)
        }
    }
}
