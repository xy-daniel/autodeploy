package com.hxht.autodeploy.sync.huaxia.model

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlType

/**
 * 2021.03.17 >>> 华夏推送部门实体创建 daniel
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "bm")
@XmlType(propOrder = ["bmbs", "bmmc", "sjbmbs", "sjbmmc", "sfyx", "fydm", "yzjd"])
class DeptModel {

    private String bmbs     //部门标识
    private String bmmc     //部门名称
    private String sjbmbs   //上级部门标识
    private String sjbmmc   //上级部门名称
    private String sfyx     //是否有效
    private String fydm     //法院代码
    private Integer yzjd    //是否为叶子节点

    String getBmbs() {
        return bmbs
    }

    void setBmbs(String bmbs) {
        this.bmbs = bmbs
    }

    String getBmmc() {
        return bmmc
    }

    void setBmmc(String bmmc) {
        this.bmmc = bmmc
    }

    String getSjbmbs() {
        return sjbmbs
    }

    void setSjbmbs(String sjbmbs) {
        this.sjbmbs = sjbmbs
    }

    String getSjbmmc() {
        return sjbmmc
    }

    void setSjbmmc(String sjbmmc) {
        this.sjbmmc = sjbmmc
    }

    String getSfyx() {
        return sfyx
    }

    void setSfyx(String sfyx) {
        this.sfyx = sfyx
    }

    String getFydm() {
        return fydm
    }

    void setFydm(String fydm) {
        this.fydm = fydm
    }

    Integer getYzjd() {
        return yzjd
    }

    void setYzjd(Integer yzjd) {
        this.yzjd = yzjd
    }
}
