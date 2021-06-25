package com.hxht.autodeploy.sync.huaxia.model

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlType

/**
 * 2021.03.24 >>> 案件路线信息模板创建 daniel
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ajlxxx")
@XmlType(propOrder = ["xhmc", "xh", "sfzl", "jlz", "dycs", "lxwjxx"])
class CaseRecordInfoModel {

    String xhmc                         //信号名称
    String xh                           //信号
    String sfzl                         //是否直连
    String jlz                          //基路径
    String dycs                         //调用参数(直连可以为空)
    @XmlElement(name = "lxwjxx")
    List<RecordFileInfoModel> lxwjxx    //录像文件信息

    String getXhmc() {
        return xhmc
    }

    void setXhmc(String xhmc) {
        this.xhmc = xhmc
    }

    String getXh() {
        return xh
    }

    void setXh(String xh) {
        this.xh = xh
    }

    String getSfzl() {
        return sfzl
    }

    void setSfzl(String sfzl) {
        this.sfzl = sfzl
    }

    String getJlz() {
        return jlz
    }

    void setJlz(String jlz) {
        this.jlz = jlz
    }

    String getDycs() {
        return dycs
    }

    void setDycs(String dycs) {
        this.dycs = dycs
    }
}
