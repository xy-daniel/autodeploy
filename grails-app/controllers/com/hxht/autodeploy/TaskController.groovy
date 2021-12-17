package com.hxht.autodeploy

import com.hxht.autodeploy.app.App
import com.hxht.autodeploy.device.Device
import com.hxht.autodeploy.device.DeviceTask
import com.hxht.autodeploy.enums.Resp
import com.hxht.autodeploy.enums.RespType
import com.hxht.autodeploy.utils.SshUtil
import com.jcraft.jsch.Session
import grails.converters.JSON

class TaskController {

    TaskService taskService

    def list() {
        if (request.method == "POST") {
            def draw = params.int("draw") ?: 1// 记录操作的次数 每次加1
            def start = params.int("start") ?: 0// 起始
            def length = params.int("length") ?: 20// 每页显示的size
            def search = params.get("search[value]") as String//搜索内容
            render taskService.list(draw, start, length, search, null) as JSON
        }
    }

    def add() {
        [appList: App.all, devices: Device.all]
    }

    def addSave() {
        String name = params.name as String
        List<String> contentList = params.content as List<String>
        for (int i = contentList.size() - 1; i >= 0; i--) {
            if (!contentList[i]) {
                contentList.remove(i)
            }
        }
        def task = new Task(
                name: name,
                content: contentList,
        )
        task.save(flush: true)
        def devices = params.devices as List<String>
        devices.each {
            new DeviceTask(
                    device: Device.get(it as long),
                    task: task,
                    exec: "no"
            ).save(flush: true)
        }
        render Resp.toJson(RespType.SUCCESS)
    }

    def exec() {
        //任务
        Task task = Task.get(params.long("id"))
        List<String> taskList = task.content
        //任务主机
        List<DeviceTask> deviceTaskList = DeviceTask.findAllByTask(task)
        def deviceId = params.long("deviceId")
        if (deviceId) {
            deviceTaskList = DeviceTask.findAllByTaskAndDevice(task, Device.get(deviceId))
        }
        Map<String, String> totalResult = new HashMap<>()
        deviceTaskList.each {
            try {
                Device device = it.device
                Session session = SshUtil.connect(device.ip, device.port, device.point, device.pwd)
                taskList.each {
                    String singleResult = ""
                    if (it.startsWith("scp")) {
                        long result = SshUtil.scpTo(it.split(" from ")[1].split(" to ")[0].trim(), session, it.split(" to ")[1].trim())
                        singleResult = "${result}"
                    } else {
                        singleResult = SshUtil.exec(session, it)
                    }
                    totalResult.put("[${session.userName}@${session.host}]# ${it}" as String, singleResult)
                }
                SshUtil.disconnect(session)
                it.exec = "yes"
                it.save(flush: true)
            } catch (ignored) {
                totalResult.put("[${it.device.point}@${it.device.ip}]# ssh连接" as String, "连接失败")
            }
        }
        render Resp.toJson(RespType.SUCCESS, totalResult)
    }

    def have() {
        def device = Device.get(params.id as long)
        if (request.method == "POST") {
            def draw = params.int("draw") ?: 1// 记录操作的次数 每次加1
            def start = params.int("start") ?: 0// 起始
            def length = params.int("length") ?: 20// 每页显示的size
            def search = params.get("search[value]") as String//搜索内容
            render taskService.list(draw, start, length, search, device) as JSON
        } else {
            [device: device]
        }
    }

    def edit() {
        def task = Task.get(params.id as long)
        def deviceTaskList = DeviceTask.findAllByTask(task)
        List<Device> hasDevice = new ArrayList()
        deviceTaskList.each {
            hasDevice.add(it.device)
        }
        [task: task, hasDevice: hasDevice, devices: Device.all, appList: App.all]
    }

    def editSave() {
        def task = Task.get(params.id as long)
        task.name = params.name as String
        List<String> contentList = params.content as List<String>
        for (int i = contentList.size() - 1; i >= 0; i--) {
            if (!contentList[i]) {
                contentList.remove(i)
            }
        }
        task.content = contentList
        task.save(flush: true)
        def dts = DeviceTask.findAllByTask(task)
        dts.each {
            it.delete(flush: true)
        }
        def devices = params.devices as List<String>
        devices.each {
            new DeviceTask(
                    device: Device.get(it as long),
                    task: task,
                    exec: "no"
            ).save(flush: true)
        }
        render Resp.toJson(RespType.SUCCESS)
    }

    def del() {
        Task task = Task.get(params.id as long)
        task.del = "yes"
        task.save(flush: true)
        render Resp.toJson(RespType.SUCCESS)
    }
}
