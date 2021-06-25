package com.hxht.autodeploy.sync.tongdahai.model.caset

import com.hxht.techcrt.sync.tongdahai.adapter.DayEncodeDateAdapter

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlType
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "EAJ")
@XmlType()
class CaseModel {

    private String AHDM         //1案件标识
    private String AH           //2案号
    private String FYDM         //3经办法院代码
//    private String FJM          //4分级码
    private String FYDMMS       //5经办法院名称
    private String AJMC         //6案件名称
    private String AJLB         //7案件类别名称
    private String DZ           //8案件代字
    private String AJLXBS       //9案件类型标识
    private String AJLXMC       //10案件类型名称
    @XmlJavaTypeAdapter(DayEncodeDateAdapter.class)
    private Date LARQ         //1立案日期
    @XmlJavaTypeAdapter(DayEncodeDateAdapter.class)
    private Date JARQ         //12结案日期
    private String AYMS         //13案由名称
    private String JAFS         //14结案方式代码
    private String JAFSMS       //15结案方式名称
    private String CBBM1         //16承办部门代码
    private String CBBMMS       //17承办部门名称
    private String CBR          //18承办人代码
    private String CBRMS        //19承办人姓名
    private String ZT           //20案件状态代码：1、已结案 0、未结案
    private String AJZTMS       //21案件状态描述
    private String SPZ          //22审判长代码
    private String SPZMS        //23审判长名称
    private String SJY          //24书记员代码
    private String SJYMS        //25书记员姓名
    private String FGZL         //26法官助理
    private String FGZLMS       //27法官助理名称
    private String HYCY         //28合议庭成员名称
//    private String BDJE         //29立案标的金额
    private String SYCX         //30立案程序
    private String XLA          //31系列案
    private String AJLY         //32案件来源
    private String SPCX         //33审判程序
    private String LASTUPDATE   //34最后更新时间
//    private String SSQQ         //35诉讼请求
//    private String FDSX         //36法定审限
    private String DSR          //37当事人
//    private String SPZBS        //38审判长人员标识
//    private String CBRBS        //39承办人人员标识
//    private String SJYBS        //40书记员人员标识
//    private String FGZLBS       //41法官助理人员标识

    String getAHDM() {
        return AHDM
    }

    void setAHDM(String AHDM) {
        this.AHDM = AHDM
    }

    String getAH() {
        return AH
    }

    void setAH(String AH) {
        this.AH = AH
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

    String getAJMC() {
        return AJMC
    }

    void setAJMC(String AJMC) {
        this.AJMC = AJMC
    }

    String getAJLB() {
        return AJLB
    }

    void setAJLB(String AJLB) {
        this.AJLB = AJLB
    }

    String getDZ() {
        return DZ
    }

    void setDZ(String DZ) {
        this.DZ = DZ
    }

    String getAJLXBS() {
        return AJLXBS
    }

    void setAJLXBS(String AJLXBS) {
        this.AJLXBS = AJLXBS
    }

    String getAJLXMC() {
        return AJLXMC
    }

    void setAJLXMC(String AJLXMC) {
        this.AJLXMC = AJLXMC
    }

    Date getLARQ() {
        return LARQ
    }

    void setLARQ(Date LARQ) {
        this.LARQ = LARQ
    }

    Date getJARQ() {
        return JARQ
    }

    void setJARQ(Date JARQ) {
        this.JARQ = JARQ
    }

    String getAYMS() {
        return AYMS
    }

    void setAYMS(String AYMS) {
        this.AYMS = AYMS
    }

    String getJAFS() {
        return JAFS
    }

    void setJAFS(String JAFS) {
        this.JAFS = JAFS
    }

    String getJAFSMS() {
        return JAFSMS
    }

    void setJAFSMS(String JAFSMS) {
        this.JAFSMS = JAFSMS
    }

    String getCBBM1() {
        return CBBM1
    }

    void setCBBM1(String CBBM1) {
        this.CBBM1 = CBBM1
    }

    String getCBBMMS() {
        return CBBMMS
    }

    void setCBBMMS(String CBBMMS) {
        this.CBBMMS = CBBMMS
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

    String getZT() {
        return ZT
    }

    void setZT(String ZT) {
        this.ZT = ZT
    }

    String getAJZTMS() {
        return AJZTMS
    }

    void setAJZTMS(String AJZTMS) {
        this.AJZTMS = AJZTMS
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

    String getFGZL() {
        return FGZL
    }

    void setFGZL(String FGZL) {
        this.FGZL = FGZL
    }

    String getFGZLMS() {
        return FGZLMS
    }

    void setFGZLMS(String FGZLMS) {
        this.FGZLMS = FGZLMS
    }

    String getHYCY() {
        return HYCY
    }

    void setHYCY(String HYCY) {
        this.HYCY = HYCY
    }

    String getSYCX() {
        return SYCX
    }

    void setSYCX(String SYCX) {
        this.SYCX = SYCX
    }

    String getXLA() {
        return XLA
    }

    void setXLA(String XLA) {
        this.XLA = XLA
    }

    String getAJLY() {
        return AJLY
    }

    void setAJLY(String AJLY) {
        this.AJLY = AJLY
    }

    String getSPCX() {
        return SPCX
    }

    void setSPCX(String SPCX) {
        this.SPCX = SPCX
    }

    String getLASTUPDATE() {
        return LASTUPDATE
    }

    void setLASTUPDATE(String LASTUPDATE) {
        this.LASTUPDATE = LASTUPDATE
    }

    String getDSR() {
        return DSR
    }

    void setDSR(String DSR) {
        this.DSR = DSR
    }

    @Override
    String toString() {
        return "CaseModel{" +
                "AHDM='" + AHDM + '\'' +
                ", AH='" + AH + '\'' +
                ", FYDM='" + FYDM + '\'' +
                ", FYDMMS='" + FYDMMS + '\'' +
                ", AJMC='" + AJMC + '\'' +
                ", AJLB='" + AJLB + '\'' +
                ", DZ='" + DZ + '\'' +
                ", AJLXBS='" + AJLXBS + '\'' +
                ", AJLXMC='" + AJLXMC + '\'' +
                ", LARQ=" + LARQ +
                ", JARQ=" + JARQ +
                ", AYMS='" + AYMS + '\'' +
                ", JAFS='" + JAFS + '\'' +
                ", JAFSMS='" + JAFSMS + '\'' +
                ", CBBM1='" + CBBM1 + '\'' +
                ", CBBMMS='" + CBBMMS + '\'' +
                ", CBR='" + CBR + '\'' +
                ", CBRMS='" + CBRMS + '\'' +
                ", ZT='" + ZT + '\'' +
                ", AJZTMS='" + AJZTMS + '\'' +
                ", SPZ='" + SPZ + '\'' +
                ", SPZMS='" + SPZMS + '\'' +
                ", SJY='" + SJY + '\'' +
                ", SJYMS='" + SJYMS + '\'' +
                ", FGZL='" + FGZL + '\'' +
                ", FGZLMS='" + FGZLMS + '\'' +
                ", HYCY='" + HYCY + '\'' +
                ", SYCX='" + SYCX + '\'' +
                ", XLA='" + XLA + '\'' +
                ", AJLY='" + AJLY + '\'' +
                ", SPCX='" + SPCX + '\'' +
                ", LASTUPDATE='" + LASTUPDATE + '\'' +
                ", DSR='" + DSR + '\'' +
                '}'
    }
}
