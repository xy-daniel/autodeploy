package com.hxht.autodeploy

class DataStatus {
    /**
     * 显示
     */
    public static final Integer SHOW = 1

    /**
     * 删除
     */
    public static final Integer DEL = 4

    /**
     * 隐藏
     */
    public static final Integer HIDE = 6



    static String getString(int status){
        switch (status){
            case 1:
                return "显示"
            case 4:
                return "删除"
            case 6:
                return "隐藏"
            default:
                return ""
        }
    }

    static Integer getCode(String status){
        switch (status){
            case "显示":
                return 1
            case "删除":
                return 4
            case "隐藏":
                return 6
            default:
                return null
        }
    }
}
