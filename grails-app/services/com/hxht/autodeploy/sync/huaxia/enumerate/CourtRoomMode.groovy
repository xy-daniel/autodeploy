package com.hxht.autodeploy.sync.huaxia.enumerate

/**
 * 2021.03.17 >>> 华夏法庭模式标准创建 daniel
 */
enum CourtRoomMode {

    STANDARD_DEFINITION(1, "标清"),
    HIGH_DEFINITION(2, "高清"),
    UHD(3, "超高清")

    private int code
    private String name

    CourtRoomMode(int code, String name) {
        this.code = code
        this.name = name
    }

    int getCode() {
        return code
    }

    void setCode(int code) {
        this.code = code
    }

    String getName() {
        return name
    }

    void setName(String name) {
        this.name = name
    }
}