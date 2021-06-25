package com.hxht.autodeploy

/**
 * 2021.06.11 >>> 开庭模式类型 daniel
 */
class ModelType {

    /**
     * 本地开庭
     */
    public static final Integer LOCALE = 0

    /**
     * 互联网开庭
     */
    public static final Integer INTERNET = 1

    static String getString(int status) {
        switch (status) {
            case 0:
                return "本地开庭"
            case 1:
                return "互联网开庭"
            default:
                return ""
        }
    }
}
