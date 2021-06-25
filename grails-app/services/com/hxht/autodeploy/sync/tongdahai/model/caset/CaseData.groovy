package com.hxht.autodeploy.sync.tongdahai.model.caset

import com.hxht.techcrt.sync.tongdahai.adapter.SecondEncodeDateAdapter

import javax.xml.bind.annotation.*
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Data")
@XmlType()
class CaseData {

    @XmlAttribute(name = "ServerTime")
    @XmlJavaTypeAdapter(SecondEncodeDateAdapter.class)
    private Date serverTime

    @XmlAttribute(name = "Count")
    private String Count

    @XmlAttribute(name = "CurPageNum")
    private String CurPageNum

    @XmlAttribute(name = "TotalPageNum")
    private String TotalPageNum

    @XmlElement(name = "EAJ")
    private List<CaseModel> list

    CaseData(Date serverTime, String count, String curPageNum, String totalPageNum, List<CaseModel> list) {
        this.serverTime = serverTime
        Count = count
        CurPageNum = curPageNum
        TotalPageNum = totalPageNum
        this.list = list
    }

    CaseData() {
    }

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

    List<CaseModel> getList() {
        return list
    }

    void setList(List<CaseModel> list) {
        this.list = list
    }

    @Override
    String toString() {
        return "UserData{" +
                "serverTime=" + serverTime +
                ", Count='" + Count + '\'' +
                ", CurPageNum='" + CurPageNum + '\'' +
                ", TotalPageNum='" + TotalPageNum + '\'' +
                ", list=" + list +
                '}'
    }
}
