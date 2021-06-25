package spring

import com.hxht.techcrt.security.TcAuthenticationProvider
import com.hxht.techcrt.webSocket.WebSocketConfig
import grails.core.GrailsApplication

// Place your Spring DSL code here
beans = {
    tcAuthenticationProvider(TcAuthenticationProvider)
    webSocketConfig(WebSocketConfig)
    grailsApplication(GrailsApplication)
    xmlns aop:"http://www.springframework.org/schema/aop"
}
