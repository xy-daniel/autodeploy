package com.hxht.autodeploy.sync.tongdahai.model.caset

import com.hxht.techcrt.sync.tongdahai.model.RespResult

import javax.xml.bind.annotation.*

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Response")
@XmlType()
class RespCase {

    @XmlElement(name = "Result")
    private RespResult result

    @XmlElement(name="Data")
    private CaseData data

    RespCase(RespResult result, CaseData data) {
        this.result = result
        this.data = data
    }

    RespCase() {
    }

    RespResult getResult() {
        return result
    }

    void setResult(RespResult result) {
        this.result = result
    }

    CaseData getData() {
        return data
    }

    void setData(CaseData data) {
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
