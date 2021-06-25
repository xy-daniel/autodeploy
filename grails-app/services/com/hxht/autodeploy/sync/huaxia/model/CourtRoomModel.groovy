package com.hxht.autodeploy.sync.huaxia.model

import com.hxht.techcrt.sync.huaxia.adapter.DayDateAdapter

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlType
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter

/**
 * 2021.03.17 >>> 华夏推送法庭实体创建 daniel
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ft")
@XmlType(propOrder = ["ftbs", "ftmc", "fydm", "ftlx", "ftms", "ftgg", "fbl", "szftcs", "jsrq"])
class CourtRoomModel {

    private String ftbs                         //法庭标识
    private String ftmc                         //法庭名称
    private String ftlx                         //法庭类型
    private String ftms                         //法庭模式
    private String ftgg                         //法庭规格
    private String fbl                          //分辨率
    @XmlElement(required = true)
    private String szftcs                       //数字法庭厂商
    @XmlJavaTypeAdapter(DayDateAdapter.class)
    //日期格式化注解
    private Date jsrq                           //建设日期
    private String fydm                         //法院代码

    String getFtbs() {
        return ftbs
    }

    void setFtbs(String ftbs) {
        this.ftbs = ftbs
    }

    String getFtmc() {
        return ftmc
    }

    void setFtmc(String ftmc) {
        this.ftmc = ftmc
    }

    String getFtlx() {
        return ftlx
    }

    void setFtlx(String ftlx) {
        this.ftlx = ftlx
    }

    String getFtms() {
        return ftms
    }

    void setFtms(String ftms) {
        this.ftms = ftms
    }

    String getFtgg() {
        return ftgg
    }

    void setFtgg(String ftgg) {
        this.ftgg = ftgg
    }

    String getFbl() {
        return fbl
    }

    void setFbl(String fbl) {
        this.fbl = fbl
    }

    String getSzftcs() {
        return szftcs
    }

    void setSzftcs(String szftcs) {
        this.szftcs = szftcs
    }

    Date getJsrq() {
        return jsrq
    }

    void setJsrq(Date jsrq) {
        this.jsrq = jsrq
    }

    String getFydm() {
        return fydm
    }

    void setFydm(String fydm) {
        this.fydm = fydm
    }
}
