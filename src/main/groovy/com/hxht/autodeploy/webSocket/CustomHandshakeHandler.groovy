package com.hxht.autodeploy.webSocket

import grails.gorm.transactions.Transactional
import org.springframework.http.server.ServerHttpRequest
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.support.DefaultHandshakeHandler

import java.security.Principal

@Transactional
class CustomHandshakeHandler extends DefaultHandshakeHandler {
    // Custom class for storing principal
    @Override
    protected Principal determineUser(ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {
        // Generate principal with UUID as name
        def username = request.getHeaders().get("username")?.get(0)
        def password = request.getHeaders().get("password")?.get(0)
        if (username && password) {
            return new StompPrincipal(username, password)
        } else {
            return null
        }
    }
}