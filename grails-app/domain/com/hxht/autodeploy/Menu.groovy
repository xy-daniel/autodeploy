package com.hxht.autodeploy

class Menu implements Serializable{
    private static final long serialVersionUID = 1

    /**
     * 关联的父id
     */
    long parentId
    /**
     * 关联的菜单名
     */
    String name
    /**
     * 菜单url
     */
    String url
    /**
     * 类型   0：目录   1：菜单   2：按钮
     */
    Integer type
    /**
     * 排序
     */
    Integer orderNum
    /**
     * 创建时间
     */
    Date dateCreated
    /**
     * 修改时间
     */
    Date lastUpdated


    static constraints = {

        parentId nullable: false
        name nullable: true, maxSize: 50
        url nullable: true, maxSize: 200
        type nullable: true, maxSize: 10
        orderNum nullable: true, maxSize: 20
    }
    static mapping = {
        autoTimestamp(true)
        parentId comment: "父id", index:true
        url comment: "菜单url"
        type comment: "类型  0：目录   1：菜单   2：按钮"
        orderNum comment: "排序"
        dateCreated comment: "创建时间"
        lastUpdated comment: "修改时间"
        comment "菜单表，保存了所有按钮和菜单的url。"
    }
}
