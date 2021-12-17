package com.hxht.autodeploy.manager.authority

class User implements Serializable{
    private static final long serialVersionUID = 1
    /**
     * 唯一编号
     */
    String uid
    /**
     * 账号
     */
    String username
    /**
     * 密码
     */
    String password
    /**
     * 姓名
     */
    String realName
    /**
     * 头像
     */
    String photo

    boolean enabled = true //账号启用
    boolean accountExpired //账号过期
    boolean accountLocked //账号锁定

    /**
     * 创建时间
     */
    Date dateCreated
    /**
     * 修改时间
     */
    Date lastUpdated

    Set<Role> getAuthorities() {
        (UserRole.findAllByUser(this) as List<UserRole>)*.role as Set<Role>
    }

    static constraints = {
        uid nullable: true, maxSize: 64
        password blank: false, password: true
        username blank: false, unique: true
        realName nullable: true, maxSize: 128
        photo nullable: true
    }
    static mapping = {
        autoTimestamp(true)
        uid comment: "唯一编号", index:true
        username column: '`username`', comment: "账号"
        password column: '`password`', comment: "密码"
        realName comment: "姓名"
        photo comment: "头像"
        enabled comment: "账号是否启用"
        accountExpired comment: "账号是否过期"
        accountLocked comment: "账号是否锁定"
        dateCreated comment: "创建时间"
        lastUpdated comment: "修改时间"
        comment "用户表"
    }
}
