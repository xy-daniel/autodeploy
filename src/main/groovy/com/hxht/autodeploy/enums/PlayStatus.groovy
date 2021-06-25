package com.hxht.autodeploy.enums

class PlayStatus {
    /**
     *  正在连接
     */
    public static final Integer CONNECTING = 0
    /**
     * 正常断开连接
     */
    public static final Integer DISCONNECT = 1
    /**
     * 管理员断开连接
     */
    public static final Integer ADMIN_DISCONNECT = 2


    static String getString(int status){
        switch (status){
            case 0:
                return "正在观看"
            case 1:
                return "结束观看"
            case 2:
                return "禁止观看"
            default:
                return "其他"
        }
    }

    static Integer getCode(String status){
        switch (status){
            case "正在连接":
                return 0
            case "正常断开连接":
                return 1
            case "管理员断开连接":
                return 2
            default:
                return 0
        }
    }
}
