package com.hxht.autodeploy.sync.huaxia.enumerate

/**
 * 2021.03.18 >>> 华夏案件类型标准创建 daniel
 */
enum CaseTypeEnum {
    XS(1, "刑"),
    MS(2, "民"),
    XZ(6, "行"),
    PC(7, "赔"),
    ZX(8, "执"),
    QT(255, "其它"),

    private int code
    private String desc

    CaseTypeEnum(int code, String desc) {
        this.code = code
        this.desc = desc
    }

    int getCode() {
        return code
    }

    void setCode(int code) {
        this.code = code
    }

    String getDesc() {
        return desc
    }

    void setDesc(String desc) {
        this.desc = desc
    }
}