package com.hxht.autodeploy

class CourtroomStatus {
    /**
     * 停止使用
     */
    public static final Integer STOP = 0

    /**
     * 正常使用
     */
    public static final Integer NORMAL = 1

    /**
     * 非正常状态
     */
    public static final Integer ERROR = 3

    /**
     * 远程庭审占用状态
     */
    public static final Integer OCCUPIED = 4

    /**
     * 根据code获取具体描述信息
     * @param status  状态码
     * @return  具体描述信息
     */
    static String getString(int status){
        switch (status){
            case 0:
                return "停止使用"
            case 1:
                return "正常使用"
            case 3:
                return "非正常状态"
            case 4:
                return "远程庭审占用"
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
