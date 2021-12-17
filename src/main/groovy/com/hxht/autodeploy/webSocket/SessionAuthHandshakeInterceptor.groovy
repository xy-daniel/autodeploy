package com.hxht.autodeploy.webSocket

import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.HandshakeInterceptor

import javax.servlet.http.HttpSession

class SessionAuthHandshakeInterceptor implements HandshakeInterceptor {
    @Override
    boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        HttpSession session = getSession(request)
//        if(session == null || session.getAttribute(Constants.SESSION_USER) == null){
//            println("websocket权限拒绝")
//            return false
        // throw new CmiException("websocket权限拒绝")
//        }
//        println 11
//        attributes.put(Constants.WEBSOCKET_USER_KEY, session.getAttribute(Constants.SESSION_USER))
        return true
    }

    @Override
    void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }

    private static HttpSession getSession(ServerHttpRequest request) {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest serverRequest = (ServletServerHttpRequest) request
            return serverRequest.getServletRequest().getSession(false)
        }
        return null
    }
}
