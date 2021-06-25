package com.hxht.autodeploy.service.sync.huigu.pojo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * SynchroFTInfo
 *
 * @author arctic
 * @date 2020/5/9
 **/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "synchroFTInfo", propOrder = {
        "xml"
})
public class SynchroFTInfo {

    protected String xml;

    /**
     * 获取xml属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getXml() {
        return xml;
    }

    /**
     * 设置xml属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setXml(String value) {
        this.xml = value;
    }
}
