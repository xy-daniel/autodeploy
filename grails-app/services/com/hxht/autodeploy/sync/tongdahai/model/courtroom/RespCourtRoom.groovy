package com.hxht.autodeploy.sync.tongdahai.model.courtroom

import com.hxht.techcrt.sync.tongdahai.model.RespResult

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlType

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Response")
@XmlType
class RespCourtRoom {

    @XmlElement(name = "Result")
    private RespResult result

    @XmlElement(name="Data")
    private CourtRoomData data

    RespResult getResult() {
        return result
    }

    void setResult(RespResult result) {
        this.result = result
    }

    CourtRoomData getData() {
        return data
    }

    void setData(CourtRoomData data) {
        this.data = data
    }

    @Override
    String toString() {
        return "RespCourtRoom{" +
                "result=" + result +
                ", data=" + data +
                '}';
    }
}
