package com.hxht.autodeploy.court.manager.info.courtroom

import com.hxht.techcrt.Resp
import com.hxht.techcrt.court.Courtroom
import com.hxht.techcrt.court.detention.DetentionDevice
import com.hxht.techcrt.enums.RespType
import com.hxht.techcrt.utils.UUIDGenerator
import grails.converters.JSON

class DetentionDeviceController {

    DetentionDeviceService detentionDeviceService

    /**
     * 设备列表
     */
    def list() {
        if (request.method == "POST") {
            def draw = params.int("draw") ?: 1// 记录操作的次数 每次加1
            def start = params.int("start") ?: 0// 起始
            def length = params.int("length") ?: 20// 每页显示的size
            def search = params.get("search[value]") as String//搜索内容
            def model = detentionDeviceService.list(draw, start, length, search)
            render model as JSON
        }
    }

    /**
     * 设备添加页面
     */
    def add () {
        [courtroomList: Courtroom.findAll()]
    }

    /**
     * 设备添加保存
     */
    def addSave () {
        def device = new DetentionDevice(params)
        device.deviceUid = UUIDGenerator.nextUUID()
        if (params.get("courtroom")) {
            device.courtroom = Courtroom.get(params.get("courtroom") as long)
        }
        detentionDeviceService.addSave(device)
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 设备编辑页面
     */
    def edit () {
        [device: DetentionDevice.get(params.get("id") as long), courtroomList: Courtroom.findAll()]
    }

    /**
     * 设备编辑保存
     */
    def editSave () {
        def device = DetentionDevice.get(params.get("id") as long)
        def ip = params.get("ip")
        def port = params.get("port")
        if (!(device && ip && port)) {
            render Resp.toJson(RespType.DATA_NOT_ALLOWED)
            return
        }
        device.deviceName = params.get("deviceName")
        device.ip = ip
        device.port = port
        device.courtroom = null
        if (params.get("courtroom")) {
            device.courtroom = Courtroom.get(params.get("courtroom") as long)
        }
        device.ver = params.get("ver")
        device.status = Integer.parseInt(params.get("status") as String)
        device.save(flush: true)
        if (device.hasErrors()) {
            log.info("[DetentionDeviceController.editSave] 编辑羁押室设备出错,错误信息：[${device.errors}]")
            throw new RuntimeException("编辑羁押室设备出错")
        }
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 设备删除
     */
    def del () {
        def deviceIds = params.get("deviceIds") as String
        if (!deviceIds) {
            render Resp.toJson(RespType.DATA_NOT_EXIST)
            return
        }
        def idsArr = deviceIds.split(",")
        for (String id : idsArr) {
            def detentionDevice = DetentionDevice.get(id as Long)
            detentionDevice.delete(flush:true)
        }
        render Resp.toJson(RespType.SUCCESS)
    }
}
