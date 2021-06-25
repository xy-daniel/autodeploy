package com.hxht.autodeploy

class CourtroomOnlineStatus {
    /**
     * 离线状态
     */
    public static final Integer OFFLINE = 0

    /**
     * 在线状态
     */
    public static final Integer ONLINE = 1

    /**
     * 未设置设备地址
     */
    public static final Integer NOT_SET = 2

    /**
     * 状态尚未初始化
     */
    public static final Integer NOT_INIT = 3


    static String getString(int status){
        switch (status){
            case 0:
                return "离线状态"
            case 1:
                return "在线状态"
            case 2:
                return "未设置设备地址"
            case 3:
                return "尚未初始化状态"
            default:
                return ""
        }
    }

}
