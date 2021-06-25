package com.hxht.autodeploy.sync.huaxia.model

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlType

/**
 * 2021.03.24 >>> 华夏推送点播视频地址模板创建 daniel
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "params")
@XmlType(propOrder = ["ajbs", "tc", "fydm", "lxwjxzdz", "ajlxxx"])
class TrialRecordListModel {

    String ajbs                         //案件标识
    String tc                           //庭次
    String fydm                         //法院代码
    RecordFileDownloadModel lxwjxzdz    //录像文件下载地址
    @XmlElement(name = "ajlxxx")
    List<CaseRecordInfoModel> ajlxxx    //案件录像信息(多个)

    String getAjbs() {
        return ajbs
    }

    void setAjbs(String ajbs) {
        this.ajbs = ajbs
    }

    String getTc() {
        return tc
    }

    void setTc(String tc) {
        this.tc = tc
    }

    String getFydm() {
        return fydm
    }

    void setFydm(String fydm) {
        this.fydm = fydm
    }

    RecordFileDownloadModel getLxwjxzdz() {
        return lxwjxzdz
    }

    void setLxwjxzdz(RecordFileDownloadModel lxwjxzdz) {
        this.lxwjxzdz = lxwjxzdz
    }

    List<CaseRecordInfoModel> getAjlxxx() {
        return ajlxxx
    }

    void setAjlxxx(List<CaseRecordInfoModel> ajlxxx) {
        this.ajlxxx = ajlxxx
    }
}
