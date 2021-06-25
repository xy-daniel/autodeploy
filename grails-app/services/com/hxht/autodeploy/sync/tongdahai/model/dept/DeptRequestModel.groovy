package com.hxht.autodeploy.sync.tongdahai.model.dept

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlType

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Request")
@XmlType()
class DeptRequestModel {

    private String BMDM //部门代码
    private String PAGENUM //页码 默认为1

    DeptRequestModel(String BMDM, String PAGENUM) {
        this.BMDM = BMDM
        this.PAGENUM = PAGENUM
    }

    DeptRequestModel() {
    }

    String getBMDM() {
        return BMDM
    }

    void setBMDM(String BMDM) {
        this.BMDM = BMDM
    }

    String getPAGENUM() {
        return PAGENUM
    }

    void setPAGENUM(String PAGENUM) {
        this.PAGENUM = PAGENUM
    }

    @Override
    String toString() {
        return "DeptRequestModel{" +
                "BMDM='" + BMDM + '\'' +
                ", PAGENUM='" + PAGENUM + '\'' +
                '}';
    }
}
