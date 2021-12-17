package com.hxht.autodeploy.device

import grails.gorm.transactions.Transactional

@Transactional
class DataTableService {

    def list(int draw, int start, int length, String search, long deviceId) {
        def device = Device.get(deviceId)
        def model = [:]
        model.put("draw", draw)
        def count = DataTable.createCriteria().count() {
            if (search) {
                or{
                    like("username", "%${search}%")
                    like("password", "%${search}%")
                    like("tableName", "%${search}%")
                }
            }
            if (device) {
                eq("device", device)
            }
        }
        def dataList = DataTable.createCriteria().list {
            and {
                setMaxResults(length)
                setFirstResult(start)
            }
            if (search) {
                or{
                    like("username", "%${search}%")
                    like("password", "%${search}%")
                    like("tableName", "%${search}%")
                }
            }
            if (device) {
                eq("device", device)
            }
        } as List<DataTable>
        def modelDataList = []
        for (def dataTable : dataList) {
            def data = [:]
            data.put("id", dataTable.id)
            data.put("device", dataTable.device.name)
            data.put("username", dataTable.username)
            data.put("password", dataTable.password)
            data.put("tableName", dataTable.tableName)
            modelDataList.add(data)
        }
        model.put("recordsTotal", count)//数据总条数
        model.put("recordsFiltered", count)//显示的条数
        model.put("data", modelDataList)
        model
    }

}
