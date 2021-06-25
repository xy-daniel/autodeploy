package com.hxht.autodeploy.court
/**
 * 法院员工
 */
class Employee {
    /**
     * 员工标识
     */
    String uid
    /**
     * 姓名
     */
    String name
    /**
     * 职位
     * 2 法官
     * 6 书记员
     * 7 司法警察
     * 255 其他
     */
    Integer position
    /**
     * 员工所属部门
     */
    Department dept

    //对接厂商
    String manufacturer
    /**
     * 同步数据原始id
     */
    String syncId

    String synchronizationId

    @Override
    String toString() {
        return this.name
    }
    static constraints = {
        uid unique: true, maxSize: 70
        name maxSize: 100
        dept nullable: true
        position nullable: true
        manufacturer nullable: true
        syncId nullable: true, maxSize: 120
        synchronizationId nullable: true, maxSize: 120
    }

    static mapping = {
        uid comment: "员工唯一标识", index: true
        name comment: "姓名", index: true
        position comment: "职位\n" +
                "2 法官\n" +
                "6 书记员\n" +
                "7 司法警察\n" +
                "255 其他"
        dept comment: "员工所属部门"
        manufacturer comment: "对接厂商"
        syncId comment: "同步数据原始id"
        synchronizationId comment: "数据同步主键"
        comment "法院员工表，保存了法院员工基本信息。"
    }
}
