package com.hxht.autodeploy.sync.tongdahai.model

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlType

@XmlAccessorType(XmlAccessType.FIELD)//控制字段或属性的序列化。FIELD表示JAXB将自动绑定Java类中的每个非静态的（static）、非瞬态的（由@XmlTransient标注）字段到XML。其他值还有XmlAccessType.PROPERTY和XmlAccessType.NONE。
@XmlRootElement(name = "Result")//根元素名称
@XmlType
class RespResult {
    @XmlElement(name = "Code")
    private String code
    @XmlElement(name = "Msg")
    private String msg

    String getCode() {
        return code
    }

    void setCode(String code) {
        this.code = code
    }

    String getMsg() {
        return msg
    }

    void setMsg(String msg) {
        this.msg = msg
    }

    @Override
    String toString() {
        return "RespResult{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
