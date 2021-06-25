package com.hxht.autodeploy.court.manager

import com.hxht.techcrt.Dict
import grails.gorm.transactions.Transactional

/**
 * 书记员客户端更新用
 */
@Transactional
class ClerkClientService {
    
    def saveDict(Dict dict){
        dict.save(flush: true)
        if (dict.hasErrors()) {
            def msg = "[ClerkClientService saveDict]保存dict失败,errors [${dict.errors}]"
            log.error(msg)
            throw new RuntimeException(msg)
        }
    }
   
}
