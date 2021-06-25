package com.hxht.autodeploy.court.manager.info.courtroom

import com.hxht.techcrt.CourtroomOnlineStatus
import com.hxht.techcrt.DeviceStatus
import com.hxht.techcrt.court.detention.DetentionDevice
import com.hxht.techcrt.mem.DeviceIsOnlineService
import grails.gorm.transactions.Transactional

@Transactional
class DetentionDeviceService {

    DeviceIsOnlineService deviceIsOnlineService

    def list(int draw, int start, int length, String search) {
        def model = [:]
        model.put("draw", draw)
        def count = DetentionDevice.createCriteria().count() {
            if (search) {
                or{
                    like("deviceUid", "%${search}%")
                    like("deviceName", "%${search}%")
                    like("ip", "%${search}%")
                    like("port", "%${search}%")
                }
            }
        }
        def dataList = DetentionDevice.createCriteria().list {
            and {
                setMaxResults(length)
                setFirstResult(start)
            }
            if (search) {
                or{
                    like("deviceUid", "%${search}%")
                    like("deviceName", "%${search}%")
                    like("ip", "%${search}%")
                    like("port", "%${search}%")
                }
            }
        } as List<DetentionDevice>
        def modelDataList = []
        for (def device : dataList) {
            def data = [:]
            data.put("id", device.id)
            data.put("deviceUid", device.deviceUid)
            data.put("deviceName", device.deviceName)
            data.put("ip", device.ip)
            data.put("port", device.port)
            data.put("courtroom", device.courtroom?.name)
            //五分钟没有发送心跳默认为离线状态
            def status
            if (!device.ip) {
                status = CourtroomOnlineStatus.NOT_SET
            } else {
                if (!device.beatDate) {
                    status = CourtroomOnlineStatus.NOT_INIT
                } else {
                    if (new Date().getTime() - device.beatDate.getTime() < 1000 * 60) {
                        status = CourtroomOnlineStatus.ONLINE
                    }else{
                        status = CourtroomOnlineStatus.OFFLINE
                    }
                }
            }
            data.put("isConnect", CourtroomOnlineStatus.getString(status))
            data.put("ver", device.ver)
            data.put("status", DeviceStatus.getString(device.status))
            modelDataList.add(data)
        }
        model.put("recordsTotal", count)//数据总条数
        model.put("recordsFiltered", count)//显示的条数
        model.put("data", modelDataList)
        model
    }

    def addSave (DetentionDevice device) {
        device.save(flush: true)
        if(device.hasErrors()){
            log.info("[DetentionDeviceService.save] 保存羁押室设备出错,错误信息：[${device.errors}]")
            throw new RuntimeException("保存羁押室设备出错")
        }
    }
}
