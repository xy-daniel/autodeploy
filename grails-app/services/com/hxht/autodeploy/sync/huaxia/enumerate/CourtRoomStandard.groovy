package com.hxht.autodeploy.sync.huaxia.enumerate

/**
 * 2021.03.17 >>> 华夏法庭规格标准创建 daniel
 */
enum CourtRoomStandard {

    STANDARD(1, "标准型"),
    SIMPLE(2, "简易型"),
    EXTENDED(3, "扩展型")

    private int code
    private String name

    CourtRoomStandard(int code, String name) {
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