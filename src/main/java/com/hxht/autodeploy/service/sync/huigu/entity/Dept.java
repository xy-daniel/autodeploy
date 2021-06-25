package com.hxht.autodeploy.service.sync.huigu.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Dept {

    private String uid;

    private String deptname;

    private Integer flag;

    private String pid;
    /**
     * 第三方数据主键
     */
    private String interfaceId;
    /**
     * 第三方数据上级部门 interface_id
     */
    private String interfacePid;

    public String getInterfacePid() {
        return interfacePid;
    }

    public void setInterfacePid(String interfacePid) {
        this.interfacePid = interfacePid;
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
     * @return deptname
     */
    public String getDeptname() {
        return deptname;
    }

    /**
     * @param deptname
     */
    public void setDeptname(String deptname) {
        this.deptname = deptname;
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
     * @return pid
     */
    public String getPid() {
        return pid;
    }

    /**
     * @param pid
     */
    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getInterfaceId() {
        return interfaceId;
    }

    public void setInterfaceId(String interfaceId) {
        this.interfaceId = interfaceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Dept dept = (Dept) o;

        return new EqualsBuilder()
                .append(uid, dept.uid)
                .append(deptname, dept.deptname)
                .append(flag, dept.flag)
                .append(pid, dept.pid)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(uid)
                .append(deptname)
                .append(flag)
                .append(pid)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "Dept{" +
                "uid='" + uid + '\'' +
                ", deptname='" + deptname + '\'' +
                ", flag=" + flag +
                ", pid='" + pid + '\'' +
                ", interfaceId='" + interfaceId + '\'' +
                ", interfacePid='" + interfacePid + '\'' +
                '}';
    }
}