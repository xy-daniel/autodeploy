package com.hxht.autodeploy.sync.huaxia.model

import com.hxht.techcrt.sync.huaxia.adapter.SecondDateAdapter

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlType
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter

/**
 * 2021.03.24 >>> 录像文件信息模板创建 daniel
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "lxwjxx")
@XmlType(propOrder = ["wjm", "xzwjm", "wjsc", "lzkssj", "lzjssj"])
class RecordFileInfoModel {

    String wjm      //文件名
    String xzwjm    //下载文件名
    String wjsc     //文件时长
    @XmlJavaTypeAdapter(SecondDateAdapter.class)
    Date lzkssj     //录制开始时间
    @XmlJavaTypeAdapter(SecondDateAdapter.class)
    Date lzjssj     //录制结束时间

    String getWjm() {
        return wjm
    }

    void setWjm(String wjm) {
        this.wjm = wjm
    }

    String getXzwjm() {
        return xzwjm
    }

    void setXzwjm(String xzwjm) {
        this.xzwjm = xzwjm
    }

    String getWjsc() {
        return wjsc
    }

    void setWjsc(String wjsc) {
        this.wjsc = wjsc
    }

    Date getLzkssj() {
        return lzkssj
    }

    void setLzkssj(Date lzkssj) {
        this.lzkssj = lzkssj
    }

    Date getLzjssj() {
        return lzjssj
    }

    void setLzjssj(Date lzjssj) {
        this.lzjssj = lzjssj
    }
}
