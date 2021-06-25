package com.hxht.autodeploy.stomp

import grails.events.Event
import grails.gorm.transactions.Transactional
import grails.plugin.springwebsocket.WebSocket
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import reactor.spring.context.annotation.Consumer
import reactor.spring.context.annotation.Selector

@Transactional
@Consumer
class CourtService implements WebSocket {

    def test(SimpMessageHeaderAccessor sha) {
        convertAndSendToUser(sha.user.name, "/queue/test", "返回消息2")
    }

    @Selector("myEvent")
    void hello(Event<String> event) {
        println "myEvent: ${event.data}"
    }
}
