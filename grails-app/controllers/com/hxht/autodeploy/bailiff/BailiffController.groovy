package com.hxht.autodeploy.bailiff

import cn.hutool.core.date.DateUtil
import com.hxht.techcrt.DataStatus
import com.hxht.techcrt.Dict
import com.hxht.techcrt.PlanStatus
import com.hxht.techcrt.Resp
import com.hxht.techcrt.court.Courtroom
import com.hxht.techcrt.court.PlanInfo
import com.hxht.techcrt.enums.RespType
import grails.converters.JSON
import grails.plugin.springwebsocket.WebSocket

/**
 * 2021.05.13 >>> 法庭报警功能修复排期显示不正确的问题 daniel
 * 2021.05.14 >>> 修改法庭报警排期检索时间.限制在当天 daniel
 * 2021.06.03 >>> 添加法警确认接口
 */
class BailiffController implements WebSocket {

    /**
     * 法警室主页
     * 2021.03.26 >>> 将只显示正在开庭的法庭修改为显示所有法庭 daniel
     */
    def index() {
        def courtroomList = Courtroom.findAll([sort: "sequence", order: "esc"])
        def courtroomData = []
        courtroomList.each {

            def planInfo = PlanInfo.findByCourtroomAndStartDateGreaterThanEqualsAndStartDateLessThanEqualsAndStatusAndActive(it, DateUtil.beginOfDay(new Date()), DateUtil.beginOfDay(new Date()), PlanStatus.SESSION, DataStatus.SHOW)
            if (!planInfo) {
                planInfo = PlanInfo.findByCourtroomAndStartDateGreaterThanEqualsAndStartDateLessThanEqualsAndStatusAndActive(it, DateUtil.beginOfDay(new Date()), DateUtil.beginOfDay(new Date()), PlanStatus.PLAN, DataStatus.SHOW)
            }
            if (planInfo) {
                courtroomData.add([
                        "id"      : it.id,
                        "name"    : it.name,
                        "archives": planInfo.caseInfo.archives,
                        "litigant": planInfo.caseInfo.accuser + "," + planInfo.caseInfo.accused,
                        "clerk"   : planInfo.secretary?.name,
                        "judge"   : planInfo.judge?.name
                ])
            } else {
                courtroomData.add([
                        "id"      : it.id,
                        "name"    : it.name,
                        "archives": "暂无排期",
                        "litigant": "暂无排期",
                        "clerk"   : "暂无排期",
                        "judge"   : "暂无排期"
                ])
            }
        }
        ["courtroomList": courtroomData, "audio": Dict.findByCode("AUDIO_FILE").val]
    }

    /**
     * 报警
     */
    def warn() {
        def courtroom = Courtroom.get(params.long("id"))
        if (!courtroom) {
            render Resp.toJson(RespType.DATA_NOT_EXIST)
            return
        }
        log.info("[BailiffController.warn] ${courtroom.name}出现警情.")
        //开始警告
        def data = [
                "id"  : courtroom.id,
                "name": courtroom.name,
                "url" : "http://${courtroom.liveIp}:8791/${courtroom.deviceIp}/1.flv"
        ]
        convertAndSend("/topic/warn", (data as JSON) as String)
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 警报解除
     */
    def quit() {
        def courtroom = Courtroom.get(params.long("id"))
        if (!courtroom) {
            render Resp.toJson(RespType.DATA_NOT_EXIST)
            return
        }
        log.info("[BailiffController.quit] ${courtroom.name}警报解除.")
        convertAndSend("/topic/quit", courtroom.id as String)
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 法警确认
     */
    def confirm() {
        def id = params.id
        log.info("[BailiffController.confirm] 法警已确认${3}法庭报警信息.")
        convertAndSend("/topic/confirm", "${id}")
        render Resp.toJson(RespType.SUCCESS, id)
    }

    def flush() {
        convertAndSend("/topic/flush", "open")
        render Resp.toJson(RespType.SUCCESS)
    }
}
