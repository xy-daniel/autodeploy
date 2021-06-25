package com.hxht.autodeploy

class PlanStatus {
    /**
     * 排期
     */
    public static final Integer PLAN = 0

    /**
     * 开庭
     */
    public static final Integer SESSION = 1

    /**
     * 休庭
     */
    public static final Integer ADJOURN = 2

    /**
     * 闭庭
     */
    public static final Integer CLOSED = 3

    /**
     * 归档
     */
    public static final Integer ARCHIVED = 4


    static String getString(int status){
        switch (status){
            case 0:
                return "排期"
            case 1:
                return "开庭"
            case 2:
                return "休庭"
            case 3:
                return "闭庭"
            case 4:
                return "归档"
            default:
                return ""
        }
    }

    static Integer getCode(String status){
        switch (status){
            case "排期":
                return 0
            case "开庭":
                return 1
            case "休庭":
                return 2
            case "闭庭":
                return 3
            case "归档":
                return 4
            default:
                return null
        }
    }

}
