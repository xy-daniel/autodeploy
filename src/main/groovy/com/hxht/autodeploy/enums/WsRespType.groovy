package com.hxht.autodeploy.enums

enum WsRespType {
    SUCCESS(0, "调用成功"),

    FAIL(1, "请求失败,请稍后再试"),

    MESSAGE(1000, "反馈信息"),
    PROGRESSBAR(1001, "当前进度：")

    WsRespType(int code, String msg) {
        this.setCode(code)
        this.setMsg(msg)
    }

    /**枚举值*/
    private int code
    /**描述*/
    private String msg

    int getCode() {
        return code
    }

    void setCode(int code) {
        this.code = code
    }

    String getMsg() {
        return msg
    }

    void setMsg(String msg) {
        this.msg = msg
    }
}

