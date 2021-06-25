package com.hxht.autodeploy.sync.huaxia.enumerate

/**
 * 2021.03.18 >>> 华夏排期适用程序标准创建 daniel
 */
enum CaseProgress {
    SIMPLE(1, "简易程序"),
    NORMAL(2, "普通程序")


    private Integer code
    private String name

    CaseProgress(Integer code, String name) {
        this.code = code
        this.name = name
    }

    Integer getCode() {
        return code
    }

    void setCode(Integer code) {
        this.code = code
    }

    String getName() {
        return name
    }

    void setName(String name) {
        this.name = name
    }
}