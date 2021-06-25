package com.hxht.autodeploy.sync.tongdahai.model.user

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
class UserData {

    @XmlAttribute(name = "ServerTime")
    @XmlJavaTypeAdapter(SecondEncodeDateAdapter.class)
    private Date serverTime

    @XmlAttribute(name = "Count")
    private String Count

    @XmlAttribute(name = "CurPageNum")
    private String CurPageNum

    @XmlAttribute(name = "TotalPageNum")
    private String TotalPageNum

    @XmlElement(name = "RY")
    private List<UserModel> list

    UserData(Date serverTime, String count, String curPageNum, String totalPageNum, List<UserModel> list) {
        this.serverTime = serverTime
        Count = count
        CurPageNum = curPageNum
        TotalPageNum = totalPageNum
        this.list = list
    }

    UserData() {
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

    List<UserModel> getList() {
        return list
    }

    void setList(List<UserModel> list) {
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
