package com.hxht.autodeploy.service.sync.huigu.entity;

import javax.persistence.*;
import java.util.Date;

public class Users {
    private String uid;

    private String interfaceId;

    private String userid;

    private String roleId;

    private String deptId;

    private String postId;

    private String username;

    private String password;

    private Date regtime;

    private Integer flag;

    private String encryptpw;

    @Column(name = "is_input")
    private Byte isinput;

    public Users() {
    }

    public Users(Users user) {
        this.uid = user.getUid();
        this.userid = user.getUserid();
        this.roleId = user.getRoleId();
        this.deptId = user.getDeptId();
        this.postId = user.getPostId();
        this.username = user.getUsername();
        this.password = user.getPassword();
    }

    /**
     * @return uid
     */
    public String getUid() {
        return uid;
    }

    /**
     * @param uid
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * @return uid
     */
    public String getInterfaceId() {
        return interfaceId;
    }

    /**
     * @param interfaceId
     */
    public void setInterfaceId(String interfaceId) {
        this.interfaceId = interfaceId;
    }

    /**
     * @return userid
     */
    public String getUserid() {
        return userid;
    }

    /**
     * @param userid
     */
    public void setUserid(String userid) {
        this.userid = userid;
    }

    /**
     * @return role_id
     */
    public String getRoleId() {
        return roleId;
    }

    /**
     * @param roleId
     */
    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    /**
     * @return dept_id
     */
    public String getDeptId() {
        return deptId;
    }

    /**
     * @param deptId
     */
    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    /**
     * @return post_id
     */
    public String getPostId() {
        return postId;
    }

    /**
     * @param postId
     */
    public void setPostId(String postId) {
        this.postId = postId;
    }

    /**
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return regtime
     */
    public Date getRegtime() {
        return regtime;
    }

    /**
     * @param regtime
     */
    public void setRegtime(Date regtime) {
        this.regtime = regtime;
    }

    /**
     * @return flag
     */
    public Integer getFlag() {
        return flag;
    }

    /**
     * @param flag
     */
    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    /**
     * @return encryptpw
     */
    public String getEncryptpw() {
        return encryptpw;
    }

    /**
     * @param encryptpw
     */
    public void setEncryptpw(String encryptpw) {
        this.encryptpw = encryptpw;
    }

    /**
     * @return isInput
     */
    public Byte getIsinput() {
        return isinput;
    }

    /**
     * @param isinput
     */
    public void setIsinput(Byte isinput) {
        this.isinput = isinput;
    }

    @Override
    public String toString() {
        return "Users{" +
                "uid='" + uid + '\'' +
                ", interfaceId='" + interfaceId + '\'' +
                ", userid='" + userid + '\'' +
                ", roleId='" + roleId + '\'' +
                ", deptId='" + deptId + '\'' +
                ", postId='" + postId + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", regtime=" + regtime +
                ", flag=" + flag +
                ", encryptpw='" + encryptpw + '\'' +
                ", isinput=" + isinput +
                '}';
    }
}