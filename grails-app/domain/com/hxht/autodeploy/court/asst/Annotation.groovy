package com.hxht.autodeploy.court.asst

import com.hxht.techcrt.court.TrialInfo

/**
 * 法官助手批注
 */
class Annotation {
    /**
     * 关联庭审
     */
    TrialInfo trialInfo
    /**
     * 批注人
     */
    Integer person
    /**
     * 批注图片地址
     */
    String path
    /**
     * 文件大小
     */
    String size
    /**
     * 文件类型
     */
    String type
    /**
     * 创建时间
     */
    Date dateCreated

    static constraints = {
        path maxSize: 250
        size maxSize: 120
        type maxSize: 120
    }
    static mapping = {
        trialInfo comment: "关联庭审", index:true
        person comment: "批注人"
        path comment: "批注图片地址"
        size comment: "文件大小"
        type comment: "文件类型"
        dateCreated comment: "创建时间"
        comment "法官助手批注表"
    }
}
