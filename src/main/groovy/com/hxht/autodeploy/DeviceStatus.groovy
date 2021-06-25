package com.hxht.autodeploy

class DeviceStatus {
    /**
     * 正常
     */
    public static final Integer NORMAL = 0

    /**
     * 关闭
     */
    public static final Integer CLOSE = 1

    /**
     * 损坏
     */
    public static final Integer DAMAGED = 2

    /**
     * 维护
     */
    public static final Integer MAINTAIN = 3

    /**
     * 异常
     */
    public static final Integer ABNORMAL = 4


    static String getString(int status) {
        switch (status) {
            case 0:
                return "正常"
            case 1:
                return "关闭"
            case 2:
                return "损坏"
            case 3:
                return "维护"
            case 4:
                return "异常"
            default:
                return ""
        }
    }

    static Integer getCode(String status) {
        switch (status) {
            case "正常":
                return 0
            case "关闭":
                return 1
            case "损坏":
                return 2
            case "维护":
                return 3
            case "异常":
                return 4
            default:
                return null
        }
    }

    /**
     * 校验传入设备状态
     * @param status
     * @return 在状态范围内true，不在false
     */
    def static valid(Integer status) {
        NORMAL == status || CLOSE == status || DAMAGED == status || MAINTAIN == status || ABNORMAL == status
    }
}
