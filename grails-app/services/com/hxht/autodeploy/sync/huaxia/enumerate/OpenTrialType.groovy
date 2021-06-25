package com.hxht.autodeploy.sync.huaxia.enumerate

/**
 * 2021.03.18 >>> 华夏开庭类型标准创建 daniel
 */
enum OpenTrialType {

    LOCAL(1, "本地开庭"),
    DISTANCE(2, "远程开庭")

    private Integer code
    private String desc

    OpenTrialType(Integer code, String desc) {
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