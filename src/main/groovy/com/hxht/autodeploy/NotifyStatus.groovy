package com.hxht.autodeploy

class NotifyStatus {
    /**
     * 未读
     */
    public static final Integer unread = 0

    /**
     * 已读
     */
    public static final Integer read = 1



    static String getString(int status){
        switch (status){
            case 0:
                return "未读"
            case 1:
                return "已读"
            default:
                return ""
        }
    }

    static Integer getCode(String status){
        switch (status){
            case "未读":
                return 0
            case "已读":
                return 1
            default:
                return null
        }
    }
}
