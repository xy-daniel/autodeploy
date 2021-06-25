package com.hxht.autodeploy.sync.tongdahai.model.user

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlType

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Request")
@XmlType()
class UserRequestModel {

    @XmlElement(name = "BMDM")
    private String deptUid //部门代码
    @XmlElement(name = "YHDM")
    private String userUid //用户代码
    @XmlElement(name = "YHXM")
    private String userName //用户姓名
    @XmlElement(name = "XZQM")
    private String isDownloadSignature //是否下载签明 1：是    2：否    为空时默认：否
    @XmlElement(name = "PAGENUM")
    private String pageNum //页码 为空时默认：1
    String getDeptUid() {
        return deptUid
    }

    void setDeptUid(String deptUid) {
        this.deptUid = deptUid
    }

    String getUserUid() {
        return userUid
    }

    void setUserUid(String userUid) {
        this.userUid = userUid
    }

    String getUserName() {
        return userName
    }

    void setUserName(String userName) {
        this.userName = userName
    }

    String getIsDownloadSignature() {
        return isDownloadSignature
    }

    void setIsDownloadSignature(String isDownloadSignature) {
        this.isDownloadSignature = isDownloadSignature
    }

    String getPageNum() {
        return pageNum
    }

    void setPageNum(String pageNum) {
        this.pageNum = pageNum
    }
}
