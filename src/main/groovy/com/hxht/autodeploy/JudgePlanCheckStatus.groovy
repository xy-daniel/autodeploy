package com.hxht.autodeploy

class JudgePlanCheckStatus {
    /**
     * 已选择
     */
    public static final Integer CHOOSE = 0

    /**
     * 未选择
     */
    public static final Integer NOT_CHOOSE = 1

    static String getString(int status){
        switch (status){
            case 0:
                return "选择"
            case 1:
                return "未选择"
            default:
                return ""
        }
    }

    static Integer getCode(String status){
        switch (status){
            case "已选择":
                return 0
            case "未选择":
                return 1
            default:
                return null
        }
    }
}
