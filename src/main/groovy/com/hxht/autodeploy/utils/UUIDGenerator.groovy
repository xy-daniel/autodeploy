package com.hxht.autodeploy.utils

/**
 * daniel
 */
class UUIDGenerator {
    /**
     * 获得一个UUID
     * @return String UUID
     */
    static String nextUUID() {
        UUID.randomUUID().toString().replaceAll("-", "")
    }
}
