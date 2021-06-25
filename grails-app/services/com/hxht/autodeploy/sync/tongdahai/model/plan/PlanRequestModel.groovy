package com.hxht.autodeploy.sync.tongdahai.model.plan

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlType

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Request")
@XmlType()
class PlanRequestModel {

    private String AHDM //案件标识
    private String KTRQ //开庭日期
    private String OPTYPE //如果值=ydkt，则可下载异地开庭信息，其他则不下载
    private String PAGENUM //页码

    String getAHDM() {
        return AHDM
    }

    void setAHDM(String AHDM) {
        this.AHDM = AHDM
    }

    String getKTRQ() {
        return KTRQ
    }

    void setKTRQ(String KTRQ) {
        this.KTRQ = KTRQ
    }

    String getOPTYPE() {
        return OPTYPE
    }

    void setOPTYPE(String OPTYPE) {
        this.OPTYPE = OPTYPE
    }

    String getPAGENUM() {
        return PAGENUM
    }

    void setPAGENUM(String PAGENUM) {
        this.PAGENUM = PAGENUM
    }

    @Override
    String toString() {
        return "PlanRequestModel{" +
                "AHDM='" + AHDM + '\'' +
                ", KTRQ='" + KTRQ + '\'' +
                ", OPTYPE='" + OPTYPE + '\'' +
                ", PAGENUM='" + PAGENUM + '\'' +
                '}'
    }
}
