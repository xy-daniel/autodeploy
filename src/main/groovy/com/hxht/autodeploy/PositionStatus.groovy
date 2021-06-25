package com.hxht.autodeploy

class PositionStatus {
    /**
     * 法官 judge
     */
    public static final Integer JUDGE = 2
    /**
     * 书记员 secretary
     */
    public static final Integer SECRETARY = 6
    /**
     * 司法警察 police
     */
    public static final Integer POLICE = 7
    /**
     * 其他 other
     */
    public static final Integer OTHER = 255


    static String getString(int status){
        switch (status){
            case 2:
                return "法官"
            case 6:
                return "书记员"
            case 7:
                return "司法警察"
            default:
                return "其他"
        }
    }

    static Integer getCode(String status){
        switch (status){
            case "法官":
                return 2
            case "书记员":
                return 6
            case "司法警察":
                return 7
            default:
                return 255
        }
    }
}
