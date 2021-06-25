package com.hxht.autodeploy.sync.huaxia.enumerate

/**
 * 2021.03.17 >>> 华夏法庭类型标准创建 daniel
 */
enum CourtRoomType {
    MT(1, "民庭"),
    XT(2, "刑庭"),
    XZ(3, "行政"),
    YNFT(4, "狱内法庭"),
    PCFT(5, "派出法庭"),
    YDFT(6, "移动法庭"),
    HLWFT(7, "互联网法庭")

    private int code
    private String name

    CourtRoomType(int code, String name) {
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