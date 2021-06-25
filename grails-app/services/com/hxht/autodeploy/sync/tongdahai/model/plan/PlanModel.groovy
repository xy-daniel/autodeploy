package com.hxht.autodeploy.sync.tongdahai.model.plan

import com.hxht.techcrt.sync.tongdahai.adapter.SecondEncodeDateAdapter

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlType
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "EAJ_FTSY")
@XmlType()
class PlanModel {

    private String AHDM     //案件标识
    private String XH       //序号
    private String FYDM     //经办法院
    private String FYDMMS   //经办法院名称
    private String TC       //庭次
    private String KTRQ     //开庭日期
    private String KTSJ     //开庭时间 hh:mm
    private String JSSJ     //结束时间 hh:mm
    private String DD       //开庭地点
    private String KTFT     //开庭法庭，汉字
    private String FTBH     //法庭编号
    private String FTMC     //法庭名称
    private String KTURL    //访问的URL
    private String AH       //案号
    private String SPZ      //审判长代码
    private String SPZMS    //审判长姓名
    private String CBR      //承办人代码
    private String CBRMS    //承办人姓名
    private String SJY      //书记员代码
    private String SJYMS    //书记员姓名
    private String HYCY     //合议庭成员姓名
    private String GKKT     //公开开庭
    private String ROWUUID  //实体码
    private String YDKTFYDM //异地开庭法院代码
    private String YDKTFTMC //异地开庭法庭名称
    private String YDKTFTBH //异地开庭法庭编号
    private String FTYT     //法庭用途
    @XmlJavaTypeAdapter(SecondEncodeDateAdapter.class)
    private Date LASTUPDATE //最后更新时间
    String getAHDM() {
        return AHDM
    }

    void setAHDM(String AHDM) {
        this.AHDM = AHDM
    }

    String getXH() {
        return XH
    }

    void setXH(String XH) {
        this.XH = XH
    }

    String getFYDM() {
        return FYDM
    }

    void setFYDM(String FYDM) {
        this.FYDM = FYDM
    }

    String getFYDMMS() {
        return FYDMMS
    }

    void setFYDMMS(String FYDMMS) {
        this.FYDMMS = FYDMMS
    }

    String getTC() {
        return TC
    }

    void setTC(String TC) {
        this.TC = TC
    }

    String getKTRQ() {
        return KTRQ
    }

    void setKTRQ(String KTRQ) {
        this.KTRQ = KTRQ
    }

    String getKTSJ() {
        return KTSJ
    }

    void setKTSJ(String KTSJ) {
        this.KTSJ = KTSJ
    }

    String getJSSJ() {
        return JSSJ
    }

    void setJSSJ(String JSSJ) {
        this.JSSJ = JSSJ
    }

    String getDD() {
        return DD
    }

    void setDD(String DD) {
        this.DD = DD
    }

    String getKTFT() {
        return KTFT
    }

    void setKTFT(String KTFT) {
        this.KTFT = KTFT
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

    String getKTURL() {
        return KTURL
    }

    void setKTURL(String KTURL) {
        this.KTURL = KTURL
    }

    String getAH() {
        return AH
    }

    void setAH(String AH) {
        this.AH = AH
    }

    String getSPZ() {
        return SPZ
    }

    void setSPZ(String SPZ) {
        this.SPZ = SPZ
    }

    String getSPZMS() {
        return SPZMS
    }

    void setSPZMS(String SPZMS) {
        this.SPZMS = SPZMS
    }

    String getCBR() {
        return CBR
    }

    void setCBR(String CBR) {
        this.CBR = CBR
    }

    String getCBRMS() {
        return CBRMS
    }

    void setCBRMS(String CBRMS) {
        this.CBRMS = CBRMS
    }

    String getSJY() {
        return SJY
    }

    void setSJY(String SJY) {
        this.SJY = SJY
    }

    String getSJYMS() {
        return SJYMS
    }

    void setSJYMS(String SJYMS) {
        this.SJYMS = SJYMS
    }

    String getHYCY() {
        return HYCY
    }

    void setHYCY(String HYCY) {
        this.HYCY = HYCY
    }

    String getGKKT() {
        return GKKT
    }

    void setGKKT(String GKKT) {
        this.GKKT = GKKT
    }

    String getROWUUID() {
        return ROWUUID
    }

    void setROWUUID(String ROWUUID) {
        this.ROWUUID = ROWUUID
    }

    String getYDKTFYDM() {
        return YDKTFYDM
    }

    void setYDKTFYDM(String YDKTFYDM) {
        this.YDKTFYDM = YDKTFYDM
    }

    String getYDKTFTMC() {
        return YDKTFTMC
    }

    void setYDKTFTMC(String YDKTFTMC) {
        this.YDKTFTMC = YDKTFTMC
    }

    String getYDKTFTBH() {
        return YDKTFTBH
    }

    void setYDKTFTBH(String YDKTFTBH) {
        this.YDKTFTBH = YDKTFTBH
    }

    String getFTYT() {
        return FTYT
    }

    void setFTYT(String FTYT) {
        this.FTYT = FTYT
    }

    Date getLASTUPDATE() {
        return LASTUPDATE
    }

    void setLASTUPDATE(Date LASTUPDATE) {
        this.LASTUPDATE = LASTUPDATE
    }

    @Override
    String toString() {
        return "PlanModel{" +
                "AHDM='" + AHDM + '\'' +
                ", XH='" + XH + '\'' +
                ", FYDM='" + FYDM + '\'' +
                ", FYDMMS='" + FYDMMS + '\'' +
                ", TC='" + TC + '\'' +
                ", KTRQ='" + KTRQ + '\'' +
                ", KTSJ='" + KTSJ + '\'' +
                ", JSSJ='" + JSSJ + '\'' +
                ", DD='" + DD + '\'' +
                ", KTFT='" + KTFT + '\'' +
                ", FTBH='" + FTBH + '\'' +
                ", FTMC='" + FTMC + '\'' +
                ", KTURL='" + KTURL + '\'' +
                ", AH='" + AH + '\'' +
                ", SPZ='" + SPZ + '\'' +
                ", SPZMS='" + SPZMS + '\'' +
                ", CBR='" + CBR + '\'' +
                ", CBRMS='" + CBRMS + '\'' +
                ", SJY='" + SJY + '\'' +
                ", SJYMS='" + SJYMS + '\'' +
                ", HYCY='" + HYCY + '\'' +
                ", GKKT='" + GKKT + '\'' +
                ", ROWUUID='" + ROWUUID + '\'' +
                ", YDKTFYDM='" + YDKTFYDM + '\'' +
                ", YDKTFTMC='" + YDKTFTMC + '\'' +
                ", YDKTFTBH='" + YDKTFTBH + '\'' +
                ", FTYT='" + FTYT + '\'' +
                ", LASTUPDATE=" + LASTUPDATE +
                '}'
    }
}
