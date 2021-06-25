package com.hxht.autodeploy.sync.huaxia.model

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlType

/**
 * 2021.03.17 >>> 华夏推送用户列表实体创建 daniel
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "params")
@XmlType()
class UserListModel {

    @XmlElement(name = "yh")
    private List<UserModel> list

    UserListModel() {
    }

    UserListModel(List<UserModel> list) {
        this.list = list
    }

    List<UserModel> getList() {
        return list
    }

    void setList(List<UserModel> list) {
        this.list = list
    }
}
