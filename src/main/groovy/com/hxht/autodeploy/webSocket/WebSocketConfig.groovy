package com.hxht.autodeploy.webSocket

import grails.plugin.springwebsocket.GrailsSimpAnnotationMethodMessageHandler
import grails.plugin.springwebsocket.GrailsWebSocketAnnotationMethodMessageHandler
import groovy.transform.CompileStatic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.SubscribableChannel
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.server.RequestUpgradeStrategy
import org.springframework.web.socket.server.standard.TomcatRequestUpgradeStrategy

@CompileStatic
@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

    @Override
    void configureMessageBroker(MessageBrokerRegistry messageBrokerRegistry) {
        messageBrokerRegistry.enableSimpleBroker "/queue", "/topic"
        messageBrokerRegistry.setApplicationDestinationPrefixes "/app"
    }

    @Override
    void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
        RequestUpgradeStrategy upgradeStrategy = new TomcatRequestUpgradeStrategy()

        stompEndpointRegistry
                .addEndpoint("/stomp")
                .setAllowedOrigins("*")
                .setHandshakeHandler(new CustomHandshakeHandler())
                .addInterceptors(new SessionAuthHandshakeInterceptor())
                .withSockJS()
//
//        stompEndpointRegistry.addEndpoint("/stomp")
//                .setHandshakeHandler(new DefaultHandshakeHandler(upgradeStrategy))
//                .setAllowedOrigins("*")
//
//        stompEndpointRegistry
//                .addEndpoint("/stomp") // Set websocket endpoint to connect to
//                .setHandshakeHandler(new CustomHandshakeHandler()) // Set custom handshake handler
//                .setAllowedOrigins("*")
//                .withSockJS() // Add Sock JS support

    }
    /**
     * 配置入站通道拦截器
     */
    @Override
    void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(presenceChannelInterceptor())
    }
    /**
     * 配置输出通道参数设置
     */
    @Override
    void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.interceptors(presenceChannelInterceptor())
    }

    @Bean
    GrailsSimpAnnotationMethodMessageHandler grailsSimpAnnotationMethodMessageHandler(
            SubscribableChannel clientInboundChannel,
            MessageChannel clientOutboundChannel,
            SimpMessageSendingOperations brokerMessagingTemplate
    ) {
        def handler = new GrailsSimpAnnotationMethodMessageHandler(clientInboundChannel, clientOutboundChannel, brokerMessagingTemplate)
        handler.destinationPrefixes = ["/app"]
        return handler
    }

    @Bean
    GrailsWebSocketAnnotationMethodMessageHandler grailsWebSocketAnnotationMethodMessageHandler(
            SubscribableChannel clientInboundChannel,
            MessageChannel clientOutboundChannel,
            SimpMessageSendingOperations brokerMessagingTemplate
    ) {
        def handler = new GrailsWebSocketAnnotationMethodMessageHandler(clientInboundChannel, clientOutboundChannel, brokerMessagingTemplate)
        handler.destinationPrefixes = ["/app"]
        return handler
    }


    @Bean
    PresenceChannelInterceptor presenceChannelInterceptor() {
        return new PresenceChannelInterceptor()
    }

}
