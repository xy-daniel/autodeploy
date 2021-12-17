package com.hxht.autodeploy.device


import com.hxht.autodeploy.enums.Resp
import com.hxht.autodeploy.enums.RespType
import grails.converters.JSON

class DeviceController {

    DeviceService deviceService

    def list() {
        if (request.method == "POST") {
            def draw = params.int("draw") ?: 1// 记录操作的次数 每次加1
            def start = params.int("start") ?: 0// 起始
            def length = params.int("length") ?: 20// 每页显示的size
            def search = params.get("search[value]") as String//搜索内容
            render deviceService.list(draw, start, length, search) as JSON
        }
    }

    def add() {}

    def addSave() {
        def device = new Device(params)
        device.save(flush: true)
        render Resp.toJson(RespType.SUCCESS)
    }

    def edit() {
        [device: Device.get(params.id as long)]
    }

    def editSave() {
        def device = Device.get(params.id as long)
        device.name = params.name as String
        device.ip = params.ip as String
        device.port = params.port as Integer
        device.point = params.point as String
        device.pwd = params.pwd as String
        device.save(flush: true)
        render Resp.toJson(RespType.SUCCESS)
    }

    def del() {
        Device device = Device.get(params.id as long)
        DeviceTask dt = DeviceTask.findByDeviceAndExec(device, "yes")
        if (dt) {
            render Resp.toJson(RespType.FAIL, "存在已执行的任务,禁止删除.")
            return
        }
        List<DeviceTask> dts = DeviceTask.findAllByDevice(device)
        dts.each {
            it.delete(flush: true)
        }
        device.delete(flush: true)
        render Resp.toJson(RespType.SUCCESS)
    }
}
