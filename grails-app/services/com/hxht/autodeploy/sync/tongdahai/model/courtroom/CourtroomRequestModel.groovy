package com.hxht.autodeploy.sync.tongdahai.model.courtroom

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlType

/**
 * 通达海法庭请求数据模板
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Request")
@XmlType
class CourtroomRequestModel{
    /**
     * 法庭编号
     */
    private String FTBH
    /**
     * 页码 默认1
     */
    private String PAGENUM

    String getFtbh() {
        return ftbh
    }


    String getFTBH() {
        return FTBH
    }

    void setFTBH(String FTBH) {
        this.FTBH = FTBH
    }

    String getPAGENUM() {
        return PAGENUM
    }

    void setPAGENUM(String PAGENUM) {
        this.PAGENUM = PAGENUM
    }

    @Override
    String toString() {
        return "CourtroomRequestModel{" +
                "FTBH='" + FTBH + '\'' +
                ", PAGENUM='" + PAGENUM + '\'' +
                '}';
    }
}
