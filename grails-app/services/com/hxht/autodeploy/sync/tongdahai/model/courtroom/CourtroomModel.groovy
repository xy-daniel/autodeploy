package com.hxht.autodeploy.sync.tongdahai.model.courtroom

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlType

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "FTXX")
@XmlType
class CourtroomModel {

    private String FYDM    //法院代码
    private String FTBH    //法庭编号
    private String FTMC    //法庭名称
    private String FTGM    //
    private String MMJJ    //
    private String ZWS     //
    private String SFJY    //
    private String FJM     //分级码

    String getFYDM() {
        return FYDM
    }

    void setFYDM(String FYDM) {
        this.FYDM = FYDM
    }

    String getFTBH() {
        return FTBH
    }

    void setFTBH(String FTBH) {
        this.FTBH = FTBH
    }

    String getFTMC() {
        return FTMC
    }

    void setFTMC(String FTMC) {
        this.FTMC = FTMC
    }

    String getFTGM() {
        return FTGM
    }

    void setFTGM(String FTGM) {
        this.FTGM = FTGM
    }

    String getMMJJ() {
        return MMJJ
    }

    void setMMJJ(String MMJJ) {
        this.MMJJ = MMJJ
    }

    String getZWS() {
        return ZWS
    }

    void setZWS(String ZWS) {
        this.ZWS = ZWS
    }

    String getSFJY() {
        return SFJY
    }

    void setSFJY(String SFJY) {
        this.SFJY = SFJY
    }

    String getFJM() {
        return FJM
    }

    void setFJM(String FJM) {
        this.FJM = FJM
    }

    @Override
    public String toString() {
        return "CourtroomModel{" +
                "FYDM='" + FYDM + '\'' +
                ", FTBH='" + FTBH + '\'' +
                ", FTMC='" + FTMC + '\'' +
                ", FTGM='" + FTGM + '\'' +
                ", MMJJ='" + MMJJ + '\'' +
                ", ZWS='" + ZWS + '\'' +
                ", SFJY='" + SFJY + '\'' +
                ", FJM='" + FJM + '\'' +
                '}';
    }
}
