package com.hxht.autodeploy
/**
 * 书记员客户端生成的uid向庭审主机发送指令 文件夹名称为此uid，后台保存此对应关系。
 * 书记员每次开庭都会生成一个uid。休庭再次开庭书记员都会新生成一个uid发送，所以应一对多的关系
 */
class TrialTask {
    /**
     * 庭审的id
     */
    long trialId
    /**
     * 生成的庭审主机中文件夹名称为uid
     */
    String taskId
    /**
     * 创建时间
     */
    Date dateCreated
    /**
     * 修改时间
     */
    Date lastUpdated

    static constraints = {
        trialId nullable: true
        taskId nullable: true
    }
    static mapping = {
        autoTimestamp(true)
        trialId comment: "庭审的id"
        taskId comment: "生成的庭审主机中文件夹名称为uid"
        dateCreated comment: "创建时间"
        lastUpdated comment: "修改时间"
        comment "庭审和庭审主机文件夹名称对应关系表。"
    }

}
