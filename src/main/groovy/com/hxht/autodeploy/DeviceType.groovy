package com.hxht.autodeploy

class DeviceType {

    /**
     * 旧版本庭审主机FTP类型
     */
    public static final Integer FTP = 1

    /**
     * 新版本庭审主机HTTP类型
     */
    public static final Integer HTTP = 3




    static String getString(int status) {
        switch (status) {
            case 1:
                return "旧版本庭审主机FTP类型"
            case 3:
                return "新版本庭审主机HTTP类型"
            default:
                return ""
        }
    }

    static Integer getCode(String status) {
        switch (status) {
            case "旧版本庭审主机FTP类型":
                return 1
            case "新版本庭审主机HTTP类型":
                return 3
            default:
                return null
        }
    }
}
