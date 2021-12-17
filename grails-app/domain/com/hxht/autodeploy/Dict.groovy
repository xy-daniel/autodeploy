package com.hxht.autodeploy

/**
 * 系统字典
 */
class Dict {
    /**
     * 名称
     */
    String name
    /**
     * 代码
     */
    String code
    /**
     * 内容
     */
    String val
    /**
     * 状态
     */
    Integer state
    /**
     * 备用拓展
     */
    String ext1
    String ext2
    String ext3
    String ext4
    String ext5
    /**
     * 备注
     */
    String note
    Date dateCreated
    Date lastUpdated

    /**
     * 子节点
     */
    static hasMany = [dicts: Dict]
    /**
     * 父节点
     */
    static belongsTo = [parent: Dict]

    static constraints = {
        name nullable: true, maxSize: 200
        code nullable: true, maxSize: 200
        val nullable: true, maxSize: 500
        state nullable: true
        ext1 nullable: true, maxSize: 300
        ext2 nullable: true, maxSize: 300
        ext3 nullable: true, maxSize: 300
        ext4 nullable: true, maxSize: 300
        ext5 nullable: true, maxSize: 300
        dicts nullable: true
        parent nullable: true
        note nullable: true, maxSize: 512
    }
    static mapping = {
        autoTimestamp(true)
        name comment: "名称"
        code comment: "代码"
        val comment: "内容"
        state comment: "状态"
        ext1 comment: "拓展字段1"
        ext2 comment: "拓展字段2"
        ext3 comment: "拓展字段3"
        ext4 comment: "拓展字段4"
        ext5 comment: "拓展字段5"
        note comment: "备注"
        dicts comment: "子节点"
        parent comment: "父节点"
        dateCreated comment: "创建时间"
        lastUpdated comment: "修改时间"
        comment "字典表"
    }

}
