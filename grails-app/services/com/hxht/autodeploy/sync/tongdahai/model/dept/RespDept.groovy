package com.hxht.autodeploy.sync.tongdahai.model.dept

import com.hxht.techcrt.sync.tongdahai.model.RespResult

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlType

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Response")
@XmlType()
class RespDept {

    @XmlElement(name = "Result")
    private RespResult result

    @XmlElement(name="Data")
    private DeptData data

    RespDept() {
    }

    RespDept(RespResult result, DeptData data) {
        this.result = result
        this.data = data
    }

    RespResult getResult() {
        return result
    }

    void setResult(RespResult result) {
        this.result = result
    }

    DeptData getData() {
        return data
    }

    void setData(DeptData data) {
        this.data = data
    }

    @Override
    String toString() {
        return "RespDept{" +
                "result=" + result +
                ", data=" + data +
                '}';
    }
}