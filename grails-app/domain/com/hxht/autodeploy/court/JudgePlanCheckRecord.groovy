package com.hxht.autodeploy.court

class JudgePlanCheckRecord {

    //法庭
    Long courtroomId
    //排期
    Long planId
    //状态
    Integer status
    //创建时间
    Date dateCreated
    //修改时间
    Date lastUpdated

    static constraints = {
    }

    static mapping = {
        planId comment: "排期主键"
        courtroomId comment: "法庭主键"
        dateCreated comment: "创建时间"
        status comment: "勾选状态"
        lastUpdated comment: "修改时间"
        comment "书记员选择排期记录"
    }
}
