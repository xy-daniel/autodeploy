package com.hxht.autodeploy.app

import grails.gorm.transactions.Transactional

@Transactional
class AppService {

    def list(int draw, int start, int length, String search) {
        def model = [:]
        model.put("draw", draw)
        def count = App.createCriteria().count() {
            if (search) {
                or {
                    like("itemName", "%${search}%")
                    like("packageName", "%${search}%")
                }
            }
        }
        def dataList = App.createCriteria().list {
            and {
                setMaxResults(length)
                setFirstResult(start)
            }
            if (search) {
                or {
                    like("itemName", "%${search}%")
                    like("packageName", "%${search}%")
                }
            }
        } as List<App>
        def modelDataList = []
        for (def app : dataList) {
            def data = [:]
            data.put("id", app.id)
            data.put("itemName", app.itemName)
            data.put("packageName", app.packageName)
            modelDataList.add(data)
        }
        model.put("recordsTotal", count)//数据总条数
        model.put("recordsFiltered", count)//显示的条数
        model.put("data", modelDataList)
        model
    }
}
