package com.hxht.autodeploy.sync.huaxia.model

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlType

/**
 * 2021.03.24 >>> 闭庭录像信息ftp基础信息模板创建 daniel
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "lxwjxzdz")
@XmlType(propOrder = ["xzfs", "ftpurl", "ftpzh", "ftpmm"])
class RecordFileDownloadModel {

    String xzfs     //下载方式
    String ftpurl   //ftp地址
    String ftpzh    //ftp账号
    String ftpmm    //ftp密码

    String getXzfs() {
        return xzfs
    }

    void setXzfs(String xzfs) {
        this.xzfs = xzfs
    }

    String getFtpurl() {
        return ftpurl
    }

    void setFtpurl(String ftpurl) {
        this.ftpurl = ftpurl
    }

    String getFtpzh() {
        return ftpzh
    }

    void setFtpzh(String ftpzh) {
        this.ftpzh = ftpzh
    }

    String getFtpmm() {
        return ftpmm
    }

    void setFtpmm(String ftpmm) {
        this.ftpmm = ftpmm
    }
}
