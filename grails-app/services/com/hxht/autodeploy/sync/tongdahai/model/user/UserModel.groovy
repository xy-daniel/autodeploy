package com.hxht.autodeploy.sync.tongdahai.model.user

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlType

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "RY")
@XmlType()
class UserModel {

    private String FYDM //法院代码
    private String YHDM //用户代码
    private String YHBM //所在机构代码
    private String XM //姓名
    private String MD5 //登录口令MD5编码
    private String LXFS //联系方式
    private String SJHM //手机号码
    private String DZ //地址
    private String DZYX //电子邮箱
    private String SFZHM //身份证号码
    private String XB //性别
    private String CSRQ //出生日期
    private String MZ //民族
    private String WHCD //文化程序
    private String ZZMM //政治面貌
    private String XZJB //行政级别
    private String ZW //法律职务
    private String FGDJ //法官等级
    private String FJDJ //法警等级
    private String ZSBZ //正式编制 1是2否
    private String SFPSY //是否陪审员 1是2否
    private String SFJY //是否禁用 1是2否
    private String SFSPZ //是否审判长 1是2否
    private String PXH //排序号
    private String ZWMC //用户职务
    private String GXBM //分管部门 多个部门用半角逗号分隔，例如：32010000,32010001,32010002
    private String USERID //映射用户ID 预留字段
    private String BMMC // 部门名称
    private String YHQM //用户签名图片
    private String YHID //用户登录ID T_USER.YHID
    private String FJM //分级码

    String getFYDM() {
        return FYDM
    }

    void setFYDM(String FYDM) {
        this.FYDM = FYDM
    }

    String getYHDM() {
        return YHDM
    }

    void setYHDM(String YHDM) {
        this.YHDM = YHDM
    }

    String getYHBM() {
        return YHBM
    }

    void setYHBM(String YHBM) {
        this.YHBM = YHBM
    }

    String getXM() {
        return XM
    }

    void setXM(String XM) {
        this.XM = XM
    }

    String getMD5() {
        return MD5
    }

    void setMD5(String MD5) {
        this.MD5 = MD5
    }

    String getLXFS() {
        return LXFS
    }

    void setLXFS(String LXFS) {
        this.LXFS = LXFS
    }

    String getSJHM() {
        return SJHM
    }

    void setSJHM(String SJHM) {
        this.SJHM = SJHM
    }

    String getDZ() {
        return DZ
    }

    void setDZ(String DZ) {
        this.DZ = DZ
    }

    String getDZYX() {
        return DZYX
    }

    void setDZYX(String DZYX) {
        this.DZYX = DZYX
    }

    String getSFZHM() {
        return SFZHM
    }

    void setSFZHM(String SFZHM) {
        this.SFZHM = SFZHM
    }

    String getXB() {
        return XB
    }

    void setXB(String XB) {
        this.XB = XB
    }

    String getCSRQ() {
        return CSRQ
    }

    void setCSRQ(String CSRQ) {
        this.CSRQ = CSRQ
    }

    String getMZ() {
        return MZ
    }

    void setMZ(String MZ) {
        this.MZ = MZ
    }

    String getWHCD() {
        return WHCD
    }

    void setWHCD(String WHCD) {
        this.WHCD = WHCD
    }

    String getZZMM() {
        return ZZMM
    }

    void setZZMM(String ZZMM) {
        this.ZZMM = ZZMM
    }

    String getXZJB() {
        return XZJB
    }

    void setXZJB(String XZJB) {
        this.XZJB = XZJB
    }

    String getZW() {
        return ZW
    }

    void setZW(String ZW) {
        this.ZW = ZW
    }

    String getFGDJ() {
        return FGDJ
    }

    void setFGDJ(String FGDJ) {
        this.FGDJ = FGDJ
    }

    String getFJDJ() {
        return FJDJ
    }

    void setFJDJ(String FJDJ) {
        this.FJDJ = FJDJ
    }

    String getZSBZ() {
        return ZSBZ
    }

    void setZSBZ(String ZSBZ) {
        this.ZSBZ = ZSBZ
    }

    String getSFPSY() {
        return SFPSY
    }

    void setSFPSY(String SFPSY) {
        this.SFPSY = SFPSY
    }

    String getSFJY() {
        return SFJY
    }

    void setSFJY(String SFJY) {
        this.SFJY = SFJY
    }

    String getSFSPZ() {
        return SFSPZ
    }

    void setSFSPZ(String SFSPZ) {
        this.SFSPZ = SFSPZ
    }

    String getPXH() {
        return PXH
    }

    void setPXH(String PXH) {
        this.PXH = PXH
    }

    String getZWMC() {
        return ZWMC
    }

    void setZWMC(String ZWMC) {
        this.ZWMC = ZWMC
    }

    String getGXBM() {
        return GXBM
    }

    void setGXBM(String GXBM) {
        this.GXBM = GXBM
    }

    String getUSERID() {
        return USERID
    }

    void setUSERID(String USERID) {
        this.USERID = USERID
    }

    String getBMMC() {
        return BMMC
    }

    void setBMMC(String BMMC) {
        this.BMMC = BMMC
    }

    String getYHQM() {
        return YHQM
    }

    void setYHQM(String YHQM) {
        this.YHQM = YHQM
    }

    String getYHID() {
        return YHID
    }

    void setYHID(String YHID) {
        this.YHID = YHID
    }

    String getFJM() {
        return FJM
    }

    void setFJM(String FJM) {
        this.FJM = FJM
    }

    @Override
    String toString() {
        return "UserModel{" +
                "FYDM='" + FYDM + '\'' +
                ", YHDM='" + YHDM + '\'' +
                ", YHBM='" + YHBM + '\'' +
                ", XM='" + XM + '\'' +
                ", MD5='" + MD5 + '\'' +
                ", LXFS='" + LXFS + '\'' +
                ", SJHM='" + SJHM + '\'' +
                ", DZ='" + DZ + '\'' +
                ", DZYX='" + DZYX + '\'' +
                ", SFZHM='" + SFZHM + '\'' +
                ", XB='" + XB + '\'' +
                ", CSRQ='" + CSRQ + '\'' +
                ", MZ='" + MZ + '\'' +
                ", WHCD='" + WHCD + '\'' +
                ", ZZMM='" + ZZMM + '\'' +
                ", XZJB='" + XZJB + '\'' +
                ", ZW='" + ZW + '\'' +
                ", FGDJ='" + FGDJ + '\'' +
                ", FJDJ='" + FJDJ + '\'' +
                ", ZSBZ='" + ZSBZ + '\'' +
                ", SFPSY='" + SFPSY + '\'' +
                ", SFJY='" + SFJY + '\'' +
                ", SFSPZ='" + SFSPZ + '\'' +
                ", PXH='" + PXH + '\'' +
                ", ZWMC='" + ZWMC + '\'' +
                ", GXBM='" + GXBM + '\'' +
                ", USERID='" + USERID + '\'' +
                ", BMMC='" + BMMC + '\'' +
                ", YHQM='" + YHQM + '\'' +
                ", YHID='" + YHID + '\'' +
                ", FJM='" + FJM + '\'' +
                '}'
    }
}
