package com.hxht.autodeploy.sync.tongdahai.model.user

import com.hxht.techcrt.sync.tongdahai.model.RespResult

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlType

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Response")
@XmlType()
class RespUser {

    @XmlElement(name = "Result")
    private RespResult result

    @XmlElement(name="Data")
    private UserData data

    RespUser(RespResult result, UserData data) {
        this.result = result
        this.data = data
    }

    RespUser() {
    }

    RespResult getResult() {
        return result
    }

    void setResult(RespResult result) {
        this.result = result
    }

    UserData getData() {
        return data
    }

    void setData(UserData data) {
        this.data = data
    }

    @Override
    String toString() {
        return "RespUser{" +
                "result=" + result +
                ", data=" + data +
                '}'
    }
}
