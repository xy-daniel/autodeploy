package com.hxht.autodeploy

class CourtRemoteStatus {

    /**
     * 非远程庭
     */
    public static final Integer NOTREMOTE = 0

    /**
     * 远程庭
     */
    public static final Integer REMOTE = 1

    /**
     * 广法庭远程状态码
     * @param status  咋混个泰马
     * @return  状态介绍
     */
    static String getString(int status){
        switch (status){
            case 0:
                return "非远程庭"
            case 1:
                return "远程庭"
            default:
                return ""

        }
    }
}
