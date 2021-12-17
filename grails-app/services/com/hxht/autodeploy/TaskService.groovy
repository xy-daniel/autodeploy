package com.hxht.autodeploy

import com.hxht.autodeploy.device.Device
import com.hxht.autodeploy.device.DeviceTask
import grails.gorm.transactions.Transactional

@Transactional
class TaskService {

    def list(int draw, int start, int length, String search, Device deviceVo) {
        def vs = Task.findAllByDelIsNull()
        vs.each {
            it.del = "no"
            it.save(flush: true)
        }
        def model = [:]
        model.put("draw", draw)
        def count = Task.createCriteria().count() {
            if (search) {
                or{
                    like("name", "%${search}%")
                }
            }
            if (!deviceVo){
                eq("del", "no")
            }
        }
        if (deviceVo) {
            count = DeviceTask.findAllByDevice(deviceVo).size()
        }
        def dataList = Task.createCriteria().list {
            and {
                setMaxResults(length)
                setFirstResult(start)
            }
            if (search) {
                or{
                    like("name", "%${search}%")
                }
            }
            if (!deviceVo){
                eq("del", "no")
            }
        } as List<Task>
        if (deviceVo) {
            dataList = new ArrayList<Task>()
            def deviceTaskList = DeviceTask.findAllByDevice(deviceVo)
            int fromIndex = start //0
            int toIndex = start+length //10
            if (toIndex > deviceTaskList.size()) {
                toIndex = deviceTaskList.size()
            }
            deviceTaskList = deviceTaskList.subList(fromIndex, toIndex)
            deviceTaskList.each {
                dataList.add(it.task)
            }
        }
        def modelDataList = []
        for (def task : dataList) {
            def data = [:]
            data.put("id", task.id)
            data.put("name", task.name)
            data.put("content", task.content)
            List<String> devices = new ArrayList<>()
            List<String> executed = new ArrayList<>()
            def deviceTaskList = DeviceTask.findAllByTask(task)
            deviceTaskList.each {
                devices.add(it.device.name)
                if (it.exec == "yes") {
                    executed.add(it.device.name)
                }
            }
            data.put("devices", devices)
            data.put("executed", executed)
            data.put("radio", "${executed.size()}/${devices.size()}")
            if(deviceVo) {
                def dt = DeviceTask.findByDeviceAndTask(deviceVo, task)
                data.put("exec", (dt.exec && dt.exec == "yes") ? "是" : "否")
            }
            data.put("del", (task.del && task.del == "yes") ? "是" : "否")
            modelDataList.add(data)
        }
        model.put("recordsTotal", count)//数据总条数
        model.put("recordsFiltered", count)//显示的条数
        model.put("data", modelDataList)
        model
    }
}
