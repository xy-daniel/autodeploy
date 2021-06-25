package com.hxht.autodeploy.webSocket

import java.security.Principal

class StompPrincipal implements Principal {
    String username
    String password

    StompPrincipal(String username, String password) {
        this.username = username
        this.password = password
    }

    String getUsername() {
        return username
    }

    void setUsername(String username) {
        this.username = username
    }

    String getPassword() {
        return password
    }

    void setPassword(String password) {
        this.password = password
    }

    @Override
    String getName() {
        return null
    }
}