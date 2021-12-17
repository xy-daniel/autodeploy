package com.hxht.autodeploy.device

import grails.gorm.transactions.Transactional

@Transactional
class DeviceService {

    def list(int draw, int start, int length, String search) {
        def model = [:]
        model.put("draw", draw)
        def count = Device.createCriteria().count() {
            if (search) {
                or{
                    like("name", "%${search}%")
                    like("ip", "%${search}%")
                    like("port", "%${search}%")
                    like("point", "%${search}%")
                    like("pwd", "%${search}%")
                }
            }
        }
        def dataList = Device.createCriteria().list {
            and {
                setMaxResults(length)
                setFirstResult(start)
            }
            if (search) {
                or{
                    like("name", "%${search}%")
                    like("ip", "%${search}%")
                    like("port", "%${search}%")
                    like("point", "%${search}%")
                    like("pwd", "%${search}%")
                }
            }
        } as List<Device>
        def modelDataList = []
        for (def host : dataList) {
            def data = [:]
            data.put("id", host.id)
            data.put("name", host.name)
            data.put("ip", host.ip)
            data.put("port", host.port)
            data.put("point", host.point)
            data.put("pwd", host.pwd)
            modelDataList.add(data)
        }
        model.put("recordsTotal", count)//数据总条数
        model.put("recordsFiltered", count)//显示的条数
        model.put("data", modelDataList)
        model
    }
}
