package com.hxht.autodeploy.mem

import com.hxht.techcrt.CourtroomOnlineStatus
import com.hxht.techcrt.court.mem.CourtroomIsOnline
import grails.gorm.transactions.Transactional

@Transactional("mem")
class DeviceIsOnlineService {

    def check(long courtroomId, Integer status){
        CourtroomIsOnline courtroomIsOnline = CourtroomIsOnline.findByCourtroomId(courtroomId)
        if (courtroomIsOnline) {
            courtroomIsOnline.status = status
        } else {
            courtroomIsOnline = new CourtroomIsOnline(
                    courtroomId: courtroomId,
                    status: status
            )
            courtroomIsOnline.save(flush: true)
        }
    }

    def isConnect(Long courtroomId) {
        def courtroomIsOnline = CourtroomIsOnline.findByCourtroomId(courtroomId)
        def status = CourtroomOnlineStatus.NOT_INIT
        if (courtroomIsOnline){
            status = courtroomIsOnline.status
        }
        return CourtroomOnlineStatus.getString(status)
    }
}
