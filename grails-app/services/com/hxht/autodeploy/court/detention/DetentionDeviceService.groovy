package com.hxht.autodeploy.court.detention

import grails.gorm.services.Service

@Service(DetentionDevice)
interface DetentionDeviceService {

    DetentionDevice get(Serializable id)

    List<DetentionDevice> list(Map args)

    Long count()

    void delete(Serializable id)

    DetentionDevice save(DetentionDevice detentionDevice)

}