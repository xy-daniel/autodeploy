package com.hxht.autodeploy.sync.tongdahai.model.plan

import com.hxht.techcrt.sync.tongdahai.model.RespResult

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlType

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Response")
@XmlType()
class RespPlan {

    @XmlElement(name = "Result")
    private RespResult result

    @XmlElement(name = "Data")
    private PlanData data

    RespResult getResult() {
        return result
    }

    void setResult(RespResult result) {
        this.result = result
    }

    PlanData getData() {
        return data
    }

    void setData(PlanData data) {
        this.data = data
    }

    @Override
    String toString() {
        return "RespPlan{" +
                "result=" + result +
                ", data=" + data +
                '}'
    }
}
