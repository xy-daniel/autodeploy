package com.hxht.autodeploy

class CollegialType {
    /**
     * 审判长 presiding judge
     */
    public static final Integer PERSIDING_JUDGE = 1

    /**
     * 审判员 judge
     */
    public static final Integer JUDGE = 2

    /**
     * 人民陪审员 People's assessor
     */
    public static final Integer PEOPLE_ASSESSOR = 4

    /**
     * 其他 other
     */
    public static final Integer OTHER = 255

    static String getString(int status){
        switch (status){
            case 1:
                return "审判长"
            case 2:
                return "审判员"
            case 4:
                return "人民陪审员"
            default:
                return "其他"
        }
    }

    static Integer getCode(String status){
        switch (status){
            case "审判长":
                return 1
            case "审判员":
                return 2
            case "人民陪审员":
                return 4
            default:
                return 255
        }
    }
}
