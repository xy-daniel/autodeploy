package com.hxht.autodeploy.sync.tongdahai.model.dept

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
class DeptData {

    @XmlAttribute(name = "ServerTime")
    @XmlJavaTypeAdapter(SecondEncodeDateAdapter.class)
    private Date serverTime

    @XmlAttribute(name = "Count")
    private String Count

    @XmlAttribute(name = "CurPageNum")
    private String CurPageNum

    @XmlAttribute(name = "TotalPageNum")
    private String TotalPageNum

    @XmlElement(name = "ZZJG")
    private List<DeptModel> list


    DeptData(Date serverTime, String count, String curPageNum, String totalPageNum, List<DeptModel> list) {
        this.serverTime = serverTime
        Count = count
        CurPageNum = curPageNum
        TotalPageNum = totalPageNum
        this.list = list
    }

    DeptData() {
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

    List<DeptModel> getList() {
        return list
    }

    void setList(List<DeptModel> list) {
        this.list = list
    }

    @Override
    String toString() {
        return "DeptData{" +
                "serverTime=" + serverTime +
                ", Count='" + Count + '\'' +
                ", CurPageNum='" + CurPageNum + '\'' +
                ", TotalPageNum='" + TotalPageNum + '\'' +
                ", list=" + list +
                '}';
    }
}
