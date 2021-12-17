package com.hxht.autodeploy.webSocket

import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptorAdapter

class PresenceChannelInterceptor extends ChannelInterceptorAdapter {

    @Override
    void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(message)
//        def user = (User)sha.getSessionAttributes().get(Constants.WEBSOCKET_USER_KEY)
        if (sha.getCommand() == null) {
            return
        }
        def name = sha.user?.name
        switch (sha.getCommand()) {
            case "CONNECT":
                break
            case "DISCONNECT":
                break
            case "SUBSCRIBE":
                break
            case "SEND":
                break
            case "UNSUBSCRIBE":
                break
            default:
                break
        }
    }
}
