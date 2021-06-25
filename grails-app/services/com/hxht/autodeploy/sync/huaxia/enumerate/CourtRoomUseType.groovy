package com.hxht.autodeploy.sync.huaxia.enumerate

/**
 * 2021.03.18 >>> 华夏法庭用途标准创建 daniel
 */
enum CourtRoomUseType {

    TRIAL(1, "开庭审理"),
    CONVERSATION(2, "庭询、谈话"),
    SENTENCING(3, "宣判"),
    ARRAIGNED(4, "提讯"),
    EVIDENCE_EXCHANGE(5, "证据交换"),
    HEARING(6, "听证")

    private Integer code
    private String desc

    CourtRoomUseType(Integer code, String desc) {
        this.code = code
        this.desc = desc
    }

    Integer getCode() {
        return code
    }

    void setCode(Integer code) {
        this.code = code
    }

    String getDesc() {
        return desc
    }

    void setDesc(String desc) {
        this.desc = desc
    }
}