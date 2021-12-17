package com.hxht.autodeploy.enums

class MenuStatus {
    /**
     * 0：目录   1：菜单   2：按钮
     */
    public static final Integer catalogue = 0

    /**
     * 0：目录   1：菜单   2：按钮
     */
    public static final Integer menu = 1

    /**
     * 0：目录   1：菜单   2：按钮
     */
    public static final Integer button = 2


    static String getString(int status) {
        switch (status) {
            case 0:
                return "目录"
            case 1:
                return "菜单"
            case 2:
                return "按钮"
            default:
                return ""
        }
    }

    static Integer getCode(String status) {
        switch (status) {
            case "目录":
                return 0
            case "菜单":
                return 1
            case "按钮":
                return 2
            default:
                return null
        }
    }

}
