package com.hxht.autodeploy.court.manager.info.user

import com.hxht.techcrt.Notify
import com.hxht.techcrt.NotifyStatus
import com.hxht.techcrt.court.Employee
import com.hxht.techcrt.utils.RelativeDateFormatUtil
import grails.gorm.transactions.Transactional

@Transactional
class NotifyService {

    /**
     * 分页获取所有消息
     * @param draw
     * @param start
     * @param length
     * @param search
     * @return
     */
    def list(int draw, int start, int length, String search) {
        def model = [:]
        model.put("draw", draw)
        def count = Notify.createCriteria().count() {
            if (search) {
                or {
                    like("remark", "%${search}%")
                }
            }
        }
        def dataList = Notify.createCriteria().list {
            and {
                setMaxResults(length)
                setFirstResult(start)
            }
            if (search) {
                or {
                    like("remark", "%${search}%")
                }
            }
            order("dateCreated", "desc")
        } as List<Notify>
        def modelDataList = []
        for (def NotifyInfo : dataList) {
            def data = [:]
            data.put("id", NotifyInfo.id)
            data.put("operator", Employee.get(NotifyInfo.operatorId).name)
            data.put("remark", NotifyInfo.remark)
            data.put("isread", NotifyStatus.getString(NotifyInfo.is_read))
            data.put("createTime", NotifyInfo.dateCreated?.format('yyyy/MM/dd HH:mm'))
            modelDataList.add(data)
        }
        model.put("recordsTotal", count)
        model.put("data", modelDataList)
        model
    }

    /**
     * 获取所有未读消息
     * @return
     */
    def listUnread() {
        def model = [:]
        def count = Notify.createCriteria().count() {
            and {
                eq("is_read", NotifyStatus.unread)
            }
        }
        def dataList = Notify.createCriteria().list {
            and {
                eq("is_read", NotifyStatus.unread)
            }
            order("dateCreated", "desc")
        } as List<Notify>
        def modelDataList = []
        for (def NotifyInfo : dataList) {
            def data = [:]
            data.put("id", NotifyInfo.id)
            data.put("remark", NotifyInfo.remark)
            data.put("createTime", RelativeDateFormatUtil.format(NotifyInfo.dateCreated?.format('yyyy/MM/dd HH:mm:ss')))
            modelDataList.add(data)
        }
        model.put("recordsTotal", count)
        model.put("data", modelDataList)
        model
    }

    def save(Notify notify){
        notify.save(flush:true)
    }

}