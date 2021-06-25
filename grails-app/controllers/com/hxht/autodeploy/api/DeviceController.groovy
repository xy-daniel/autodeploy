package com.hxht.autodeploy.api

import cn.hutool.core.date.DateUtil
import com.hxht.techcrt.Dict
import com.hxht.techcrt.PlanStatus
import com.hxht.techcrt.Resp
import com.hxht.techcrt.court.Courtroom
import com.hxht.techcrt.court.PlanInfo
import com.hxht.techcrt.court.detention.DetentionDevice
import com.hxht.techcrt.court.manager.SystemController
import com.hxht.techcrt.enums.RespType
import com.hxht.techcrt.utils.http.HttpUtil

/**
 * 2021.03.25 >>> 语音传唤页面只显示被告人 daniel
 */
class DeviceController {

    /**
     * 为羁押室平板提供所有法庭
     */
    def allCourtroom() {
        def courtroomList = Courtroom.findAll()
        def allCourtroom = []
        courtroomList.each {
            allCourtroom.add([
                    id: it.id,
                    name: it.name
            ])
        }
        render Resp.toJson(RespType.SUCCESS, allCourtroom)
    }

    /**
     * 提供所有设备列表
     */
    def allDevice () {
        def deviceData = []
        def deviceList = DetentionDevice.findAll()
        deviceList.each {
            deviceData.add([
                    "deviceUid": it.deviceUid,
                    "deviceName": it.deviceName,
                    "courtroomId": it.courtroom?.id
            ])
        }
        render Resp.toJson(RespType.SUCCESS, deviceData)
    }

    /**
     * 获取所有羁押室和当事人接口
     */
    def getLitigant () {
        def courtroom = Courtroom.get(params.get("id"))
        if (!courtroom) {
            render Resp.toJson(RespType.DATA_NOT_ALLOWED)
            return
        }
        def litigantData = []
        def planInfo = PlanInfo.findByCourtroomAndStatus(courtroom, PlanStatus.SESSION)
        def caseInfo = planInfo?.caseInfo
        if (!caseInfo) {
            render Resp.toJson(RespType.DATA_NOT_EXIST)
            return
        }
//        def accuser = caseInfo.accuser.split(",")
        def accused = caseInfo.accused.split(",")
//        if (accuser.size() != 0) {
//            accuser.each {
//                litigantData.add(it)
//            }
//        }
        if (accused.size() != 0) {
            accused.each {
                litigantData.add(it)
            }
        }
        render Resp.toJson(RespType.SUCCESS, ["id": planInfo.id, "empData": litigantData])
    }

    /**
     * 进入语音传唤页面
     */
    def voice(){
        [contentPath: request.contextPath]
    }

    /**
     * 语音播报软件上线初始化
     */
    def init () {
        def courtName = SystemController.currentCourt.ext1
        def deviceName = DetentionDevice.findByIp(request.remoteAddr).deviceName
        def time = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss")
        render Resp.toJson(RespType.SUCCESS, [courtName: courtName, deviceName: deviceName, time: time])
    }

    /**
     * 传唤接口
     */
    def call () {
        try {
            //法庭
            def courtroom = Courtroom.get(params.get("id"))
            //设备
            def device = DetentionDevice.findByDeviceUid(params.get("deviceUid"))
            //当事人名称
            def name = params.get("name")
            if (!(device && name)) {
                render Resp.toJson(RespType.DATA_NOT_ALLOWED)
                return
            }
            //拼装传唤语音
            def voice = Dict.findByCode("CALL_MODEL").val
            voice = voice.replace("{user}", name)
            if (courtroom) {
                voice = voice.replace("{courtroom}", courtroom.name)
            }
            String respResult = HttpUtil.simplePost("http://${device.ip}:${device.port}/textSpeech", ["text": voice])
            if (respResult == "success") {
                render Resp.toJson(RespType.SUCCESS)
            } else {
                render Resp.toJson(RespType.FAIL)
            }
        } catch (e) {
            e.printStackTrace()
            render Resp.toJson(RespType.DEVICE_EXCEPTION)
        }
    }

    /**
     * 语音播报软件心跳
     */
    def heart() {
        //获取设备
        def device = DetentionDevice.findByIp(request.remoteAddr)
        //设备不存在
        if (!device) {
            render Resp.toJson(RespType.DATA_NOT_EXIST)
            return
        }
        device.beatDate = new Date()
        println device.beatDate.getTime().toString()
        device.save(flush: true)
        if (device.hasErrors()) {
            log.error("[DeviceController.hear] 语音播报软件设置最新心跳时间失败，错误信息：${device.errors}")
            render Resp.toJson(RespType.FAIL)
            return
        }
        render Resp.toJson(RespType.SUCCESS)
    }
}
