package com.hxht.autodeploy.sync.tongdahai.model.dept

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlType

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ZZJG")
@XmlType()
class DeptModel {

    private String FYDM
    private String BMDM
    private String BMID
    private String MC
    private String FBM
    private String JC
    private String LXR
    private String LXDH
    private String DZ
    private String PCFT
    private String SFJY
    private String PXH
    private String FJM



    DeptModel() {
    }

    DeptModel(String FYDM, String BMDM, String BMID, String MC, String FBM, String JC, String LXR, String LXDH, String DZ, String PCFT, String SFJY, String PXH, String FJM) {
        this.FYDM = FYDM
        this.BMDM = BMDM
        this.BMID = BMID
        this.MC = MC
        this.FBM = FBM
        this.JC = JC
        this.LXR = LXR
        this.LXDH = LXDH
        this.DZ = DZ
        this.PCFT = PCFT
        this.SFJY = SFJY
        this.PXH = PXH
        this.FJM = FJM
    }

    String getFYDM() {
        return FYDM
    }

    void setFYDM(String FYDM) {
        this.FYDM = FYDM
    }

    String getBMDM() {
        return BMDM
    }

    void setBMDM(String BMDM) {
        this.BMDM = BMDM
    }

    String getBMID() {
        return BMID
    }

    void setBMID(String BMID) {
        this.BMID = BMID
    }

    String getMC() {
        return MC
    }

    void setMC(String MC) {
        this.MC = MC
    }

    String getFBM() {
        return FBM
    }

    void setFBM(String FBM) {
        this.FBM = FBM
    }

    String getJC() {
        return JC
    }

    void setJC(String JC) {
        this.JC = JC
    }

    String getLXR() {
        return LXR
    }

    void setLXR(String LXR) {
        this.LXR = LXR
    }

    String getLXDH() {
        return LXDH
    }

    void setLXDH(String LXDH) {
        this.LXDH = LXDH
    }

    String getDZ() {
        return DZ
    }

    void setDZ(String DZ) {
        this.DZ = DZ
    }

    String getPCFT() {
        return PCFT
    }

    void setPCFT(String PCFT) {
        this.PCFT = PCFT
    }

    String getSFJY() {
        return SFJY
    }

    void setSFJY(String SFJY) {
        this.SFJY = SFJY
    }

    String getPXH() {
        return PXH
    }

    void setPXH(String PXH) {
        this.PXH = PXH
    }

    String getFJM() {
        return FJM
    }

    void setFJM(String FJM) {
        this.FJM = FJM
    }

    @Override
    String toString() {
        return "DeptModel{" +
                "FYDM='" + FYDM + '\'' +
                ", BMDM='" + BMDM + '\'' +
                ", BMID='" + BMID + '\'' +
                ", MC='" + MC + '\'' +
                ", FBM='" + FBM + '\'' +
                ", JC='" + JC + '\'' +
                ", LXR='" + LXR + '\'' +
                ", LXDH='" + LXDH + '\'' +
                ", DZ='" + DZ + '\'' +
                ", PCFT='" + PCFT + '\'' +
                ", SFJY='" + SFJY + '\'' +
                ", PXH='" + PXH + '\'' +
                ", FJM='" + FJM + '\'' +
                '}';
    }
}
