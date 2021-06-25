package com.hxht.autodeploy

class CaseStatus {
    /**
    * 正在使用
    */
    public static final Integer IN_USE = 1

    /**
    * 不再使用
    */
    public static final Integer NOT_USE = 4

    static String getString(int status){
        switch (status){
            case 1:
                return "正在使用"
            case 4:
                return "不再使用"
            default:
                return "其他"
        }
    }

    static Integer getCode(String status){
        switch (status){
            case "正在使用":
                return 1
            case "不再使用":
                return 4
            default:
                return null
        }
    }
}
