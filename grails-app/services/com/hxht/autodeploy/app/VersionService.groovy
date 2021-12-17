package com.hxht.autodeploy.app

import cn.hutool.core.date.DateUtil
import grails.gorm.transactions.Transactional

@Transactional
class VersionService {

    def list(int draw, int start, int length, String search, App app) {
        def vs = Version.findAllByDelIsNull()
        vs.each {
            it.del = "no"
            it.save(flush: true)
        }
        def model = [:]
        model.put("draw", draw)
        def count = Version.createCriteria().count() {
            if (search) {
                or {
                    like("number", "%${search}%")
                    like("path", "%${search}%")
                    like("size", "%${search}%")
                }
            }
            eq("app", app)
            eq("del", "no")
        }
        def dataList = Version.createCriteria().list {
            and {
                setMaxResults(length)
                setFirstResult(start)
            }
            if (search) {
                or {
                    like("number", "%${search}%")
                    like("path", "%${search}%")
                    like("size", "%${search}%")
                }
            }
            eq("app", app)
            eq("del", "no")
        } as List<Version>
        def modelDataList = []
        for (def version : dataList) {
            def data = [:]
            data.put("id", version.id)
            data.put("number", version.number)
            data.put("path", version.path)
            data.put("size", version.size)
            data.put("lastUpdated", DateUtil.formatDateTime(version.lastUpdated))
            modelDataList.add(data)
        }
        model.put("recordsTotal", count)//数据总条数
        model.put("recordsFiltered", count)//显示的条数
        model.put("data", modelDataList)
        println model as String
        model
    }
}
