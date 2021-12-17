package com.hxht.autodeploy.device

import cn.hutool.core.date.DateUtil
import grails.gorm.transactions.Transactional

@Transactional
class DataFileService {

    def list(int draw, int start, int length, String search, long tableId) {
        def database = DataTable.get(tableId)
        def model = [:]
        model.put("draw", draw)
        def count = DataFile.createCriteria().count() {
            if (search) {
                or{
                    like("address", "%${search}%")
                }
            }
            eq("database", database)
        }
        def dataList = DataFile.createCriteria().list {
            and {
                setMaxResults(length)
                setFirstResult(start)
            }
            if (search) {
                or{
                    like("address", "%${search}%")
                }
            }
            eq("database", database)
        } as List<DataFile>
        def modelDataList = []
        for (def dataFile : dataList) {
            def data = [:]
            data.put("id", dataFile.id)
            data.put("tableName", dataFile.database.tableName)
            data.put("address", dataFile.address)
            data.put("size", dataFile.size)
            data.put("dateCreated", DateUtil.formatDateTime(dataFile.dateCreated))
            modelDataList.add(data)
        }
        model.put("recordsTotal", count)//数据总条数
        model.put("recordsFiltered", count)//显示的条数
        model.put("data", modelDataList)
        model
    }
}
