package com.hxht.autodeploy.court.manager.info

import cn.hutool.core.date.DateUtil
import com.hxht.techcrt.Alarm
import grails.gorm.transactions.Transactional

@Transactional
class AlarmService {

    def list(int draw, int start, int length, String search) {
        def model = [:]
        model.put("draw", draw)
        def count = Alarm.createCriteria().count() {
            if (search) {
                or {
                    like("alarmInfo", "%${search}%")
                }
            }
        }
        def dataList = Alarm.createCriteria().list {
            and {
                setMaxResults(length)
                setFirstResult(start)
            }
            or {
                like("alarmInfo", "%${search}%")
            }
            order("dateCreated", "desc")
        } as List<Alarm>
        def modelDataList = []
        for (def alarm : dataList) {
            def data = [:]
            data.put("id", alarm.id)
            data.put("alarmType", alarm.alarmType)
            data.put("alarmInfo", alarm.alarmInfo)
            data.put("dateCreated", DateUtil.format(alarm.dateCreated as Date, "yyyy-MM-dd HH:mm:ss"))
            modelDataList.add(data)
        }
        model.put("recordsTotal", count)//数据总条数
        model.put("recordsFiltered", count)//显示的条数
        model.put("data", modelDataList)
        model
    }


    def addSave(Alarm alarm) {
        alarm.save(flush: true)
        if (alarm.hasErrors()) {
            def msg = "[AlarmService addSave alarm.save]添加报警信息失败 errors [${alarm.errors}]"
            log.error(msg)
            throw new RuntimeException(msg)
        }
    }
}