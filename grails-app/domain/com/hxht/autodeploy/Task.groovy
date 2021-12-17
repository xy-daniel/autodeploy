package com.hxht.autodeploy

class Task {

    //任务名称
    String name

    //任务内容
    List<String> content

    String del

    //创建时间
    Date dateCreated

    //修改时间
    Date lastUpdated

    static constraints = {
        del nullable: true
    }

    static mapping = {
        autoTimestamp(true)
        name comment: "任务名称"
        content comment: "任务内容"
        del comment: "是否删除"
        dateCreated comment: "创建时间"
        lastUpdated comment: "修改时间"
        comment "任务表"
    }
}
