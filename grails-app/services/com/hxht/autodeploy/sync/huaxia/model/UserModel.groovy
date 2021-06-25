package com.hxht.autodeploy.sync.huaxia.model

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlType

/**
 * 2021.03.17 >>> 华夏推送用户实体创建 daniel
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ft")
@XmlType(propOrder = ["yhbs", "yhmc", "bmbs", "bmmc", "sjhm", "dzyx", "imzh", "xb", "sfyx", "fydm"])
class UserModel {

    private String yhbs //人员标识
    private String yhmc //人员名称
    private String bmbs //部门标识
    private String bmmc //部门名称
    private String sjhm //手机号码
    private String dzyx //电子邮箱
    private String imzh //即时通讯账号
    private String xb   //性别
    private String sfyx //是否有效
    private String fydm //法院代码

    String getYhbs() {
        return yhbs
    }

    void setYhbs(String yhbs) {
        this.yhbs = yhbs
    }

    String getYhmc() {
        return yhmc
    }

    void setYhmc(String yhmc) {
        this.yhmc = yhmc
    }

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

    String getSjhm() {
        return sjhm
    }

    void setSjhm(String sjhm) {
        this.sjhm = sjhm
    }

    String getDzyx() {
        return dzyx
    }

    void setDzyx(String dzyx) {
        this.dzyx = dzyx
    }

    String getImzh() {
        return imzh
    }

    void setImzh(String imzh) {
        this.imzh = imzh
    }

    String getXb() {
        return xb
    }

    void setXb(String xb) {
        this.xb = xb
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
}

