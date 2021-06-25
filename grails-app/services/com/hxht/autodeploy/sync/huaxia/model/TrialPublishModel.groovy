package com.hxht.autodeploy.sync.huaxia.model

import com.hxht.techcrt.sync.huaxia.adapter.*

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlType
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter

/**
 * 2021.03.17 >>> 华夏推送排期实体创建 daniel
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ajxx")
@XmlType(propOrder = ["ajbs", "tc", "ajlb", "ah", "spzhmc", "ay", "sycx", "dsr", "cbrbs", "cbr", "cbbmbs", "cbbm", "jyaq", "larq", "kssj", "jssj", "ftbs", "ktdd", "gkkt", "spzbs", "spz", "hytcybs", "hytcy", "sjybs", "sjy", "fydm", "szftcs", "ktlx", "ycktfs", "ftyt", "sfzb", "sfsskl"])
class TrialPublishModel {

    private String ajbs     //案件标识
    private String tc       //庭次
    private String spzhmc   //审判字号名称
    private String ajlb     //案件类别
    private String ah       //案号
    private String ay       //案由
    private String sycx     //适用程序
    private String dsr      //当事人
    private String cbrbs    //承办人标识
    private String cbr      //承办人
    private String cbbmbs   //承办部门标识
    private String cbbm     //承办部门
    private String jyaq     //简要案情
    @XmlJavaTypeAdapter(DayDateAdapter.class)
    private Date larq       //立案日期
    @XmlJavaTypeAdapter(SecondDateAdapter.class)
    private Date kssj       //计划开庭时间
    @XmlJavaTypeAdapter(SecondDateAdapter.class)
    private Date jssj       //计划结束时间
    private String ftbs     //法庭标识
    private String ktdd     //开庭地点
    private String gkkt     //是否公开开庭
    private String spzbs    //审判长标识
    private String spz      //审判长姓名
    private String hytcybs  //合议庭其他成员标识【非必需】
    private String hytcy    //合议庭其他成员【非必需】
    private String sjybs    //书记员标识
    private String sjy      //书记员姓名
    private String fydm     //法院代码
    private String szftcs   //数字法庭厂商
    private String ktlx     //开庭类型
    private String ycktfs   //远程开庭方式
    private String ftyt     //法庭用途
    private String sfzb     //是否直播
    private String sfsskl   //是否实时刻录

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

    String getSpzhmc() {
        return spzhmc
    }

    void setSpzhmc(String spzhmc) {
        this.spzhmc = spzhmc
    }

    String getAjlb() {
        return ajlb
    }

    void setAjlb(String ajlb) {
        this.ajlb = ajlb
    }

    String getAh() {
        return ah
    }

    void setAh(String ah) {
        this.ah = ah
    }

    String getAy() {
        return ay
    }

    void setAy(String ay) {
        this.ay = ay
    }

    String getSycx() {
        return sycx
    }

    void setSycx(String sycx) {
        this.sycx = sycx
    }

    String getDsr() {
        return dsr
    }

    void setDsr(String dsr) {
        this.dsr = dsr
    }

    String getCbrbs() {
        return cbrbs
    }

    void setCbrbs(String cbrbs) {
        this.cbrbs = cbrbs
    }

    String getCbr() {
        return cbr
    }

    void setCbr(String cbr) {
        this.cbr = cbr
    }

    String getCbbmbs() {
        return cbbmbs
    }

    void setCbbmbs(String cbbmbs) {
        this.cbbmbs = cbbmbs
    }

    String getCbbm() {
        return cbbm
    }

    void setCbbm(String cbbm) {
        this.cbbm = cbbm
    }

    String getJyaq() {
        return jyaq
    }

    void setJyaq(String jyaq) {
        this.jyaq = jyaq
    }

    Date getLarq() {
        return larq
    }

    void setLarq(Date larq) {
        this.larq = larq
    }

    Date getKssj() {
        return kssj
    }

    void setKssj(Date kssj) {
        this.kssj = kssj
    }

    Date getJssj() {
        return jssj
    }

    void setJssj(Date jssj) {
        this.jssj = jssj
    }

    String getFtbs() {
        return ftbs
    }

    void setFtbs(String ftbs) {
        this.ftbs = ftbs
    }

    String getKtdd() {
        return ktdd
    }

    void setKtdd(String ktdd) {
        this.ktdd = ktdd
    }

    String getGkkt() {
        return gkkt
    }

    void setGkkt(String gkkt) {
        this.gkkt = gkkt
    }

    String getSpzbs() {
        return spzbs
    }

    void setSpzbs(String spzbs) {
        this.spzbs = spzbs
    }

    String getSpz() {
        return spz
    }

    void setSpz(String spz) {
        this.spz = spz
    }

    String getHytcybs() {
        return hytcybs
    }

    void setHytcybs(String hytcybs) {
        this.hytcybs = hytcybs
    }

    String getHytcy() {
        return hytcy
    }

    void setHytcy(String hytcy) {
        this.hytcy = hytcy
    }

    String getSjybs() {
        return sjybs
    }

    void setSjybs(String sjybs) {
        this.sjybs = sjybs
    }

    String getSjy() {
        return sjy
    }

    void setSjy(String sjy) {
        this.sjy = sjy
    }

    String getFydm() {
        return fydm
    }

    void setFydm(String fydm) {
        this.fydm = fydm
    }

    String getSzftcs() {
        return szftcs
    }

    void setSzftcs(String szftcs) {
        this.szftcs = szftcs
    }

    String getKtlx() {
        return ktlx
    }

    void setKtlx(String ktlx) {
        this.ktlx = ktlx
    }

    String getYcktfs() {
        return ycktfs
    }

    void setYcktfs(String ycktfs) {
        this.ycktfs = ycktfs
    }

    String getFtyt() {
        return ftyt
    }

    void setFtyt(String ftyt) {
        this.ftyt = ftyt
    }

    String getSfzb() {
        return sfzb
    }

    void setSfzb(String sfzb) {
        this.sfzb = sfzb
    }

    String getSfsskl() {
        return sfsskl
    }

    void setSfsskl(String sfsskl) {
        this.sfsskl = sfsskl
    }
}
