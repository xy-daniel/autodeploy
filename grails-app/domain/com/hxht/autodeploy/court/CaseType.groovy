package com.hxht.autodeploy.court
/**
 * 案件类型
 */
class CaseType {
    /**
     * 案件类型名称
     */
    String name
    /**
     * 案件类型标识
     */
    String code
    /**
     * 案件类型代字
     */
    String shortName
    /**
     * 父id
     */
    CaseType parent

    @Override
    String toString() {
        return this.name
    }

    static constraints = {
        name maxSize: 30
        code maxSize: 4, minSize: 4
        shortName nullable: true, maxSize: 16
        parent nullable: true
    }
    static mapping = {
        name comment: "类型名称", index: true
        code comment: "案件类型标识", index:true
        shortName comment: "案件类型代字"
        parent comment: "父案件类型uid"
        comment "案件类型表。根据中华人民共和国最高人民法院发布的《案件类型代码技术规范》为准"
    }
}
