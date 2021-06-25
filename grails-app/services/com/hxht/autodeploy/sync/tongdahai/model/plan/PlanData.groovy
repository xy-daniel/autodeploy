package com.hxht.autodeploy.sync.tongdahai.model.plan

import com.hxht.techcrt.sync.tongdahai.adapter.SecondEncodeDateAdapter

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlType
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Data")
@XmlType()
class PlanData {

    @XmlAttribute(name = "ServerTime")
    @XmlJavaTypeAdapter(SecondEncodeDateAdapter.class)
    private Date serverTime

    @XmlAttribute(name = "Count")
    private String Count

    @XmlAttribute(name = "CurPageNum")
    private String CurPageNum

    @XmlAttribute(name = "TotalPageNum")
    private String TotalPageNum

    @XmlElement(name = "EAJ_FTSY")
    private List<PlanModel> list

    Date getServerTime() {
        return serverTime
    }

    void setServerTime(Date serverTime) {
        this.serverTime = serverTime
    }

    String getCount() {
        return Count
    }

    void setCount(String count) {
        Count = count
    }

    String getCurPageNum() {
        return CurPageNum
    }

    void setCurPageNum(String curPageNum) {
        CurPageNum = curPageNum
    }

    String getTotalPageNum() {
        return TotalPageNum
    }

    void setTotalPageNum(String totalPageNum) {
        TotalPageNum = totalPageNum
    }

    List<PlanModel> getList() {
        return list
    }

    void setList(List<PlanModel> list) {
        this.list = list
    }

    @Override
    String toString() {
        return "PlanData{" +
                "serverTime=" + serverTime +
                ", Count='" + Count + '\'' +
                ", CurPageNum='" + CurPageNum + '\'' +
                ", TotalPageNum='" + TotalPageNum + '\'' +
                ", list=" + list +
                '}'
    }
}
