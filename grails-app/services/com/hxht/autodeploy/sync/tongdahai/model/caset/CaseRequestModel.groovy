package com.hxht.autodeploy.sync.tongdahai.model.caset


import javax.xml.bind.annotation.*

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Request")
@XmlType()
class CaseRequestModel {

    private String LARQ //立案日期
    private String ZT //案件状态(1已结案,0未结案,查询已结案案件时,必须输入结案日期条件)
    private String JARQ //结案日期
    private String DSR //当事人名称
    private String AH //案号
    private String AHDM //案件标识
    private String ZHGXSJ //案件最后更新时间(yyyy-MM-dd HH:mm:ss)
    private String PAGENUM //页数，如果未填写，则默认为第一页

    String getLARQ() {
        return LARQ
    }

    void setLARQ(String LARQ) {
        this.LARQ = LARQ
    }

    String getZT() {
        return ZT
    }

    void setZT(String ZT) {
        this.ZT = ZT
    }

    String getJARQ() {
        return JARQ
    }

    void setJARQ(String JARQ) {
        this.JARQ = JARQ
    }

    String getDSR() {
        return DSR
    }

    void setDSR(String DSR) {
        this.DSR = DSR
    }

    String getAH() {
        return AH
    }

    void setAH(String AH) {
        this.AH = AH
    }

    String getAHDM() {
        return AHDM
    }

    void setAHDM(String AHDM) {
        this.AHDM = AHDM
    }

    String getPAGENUM() {
        return PAGENUM
    }

    void setPAGENUM(String PAGENUM) {
        this.PAGENUM = PAGENUM
    }

    @Override
    String toString() {
        return "CaseRequestModel{" +
                "LARQ='" + LARQ + '\'' +
                ", ZT='" + ZT + '\'' +
                ", JARQ='" + JARQ + '\'' +
                ", DSR='" + DSR + '\'' +
                ", AH='" + AH + '\'' +
                ", AHDM='" + AHDM + '\'' +
                ", ZHGXSJ='" + ZHGXSJ + '\'' +
                ", PAGENUM='" + PAGENUM + '\'' +
                '}'
    }
}
