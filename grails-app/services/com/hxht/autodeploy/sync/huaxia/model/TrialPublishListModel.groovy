package com.hxht.autodeploy.sync.huaxia.model

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlType

/**
 * 2021.03.18 >>> 华夏排期列表列表实体类创建 daniel
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "params")
@XmlType()
class TrialPublishListModel {
    @XmlElement(name = "ajxx")
    private List<TrialPublishModel> list

    TrialPublishListModel() {
    }

    TrialPublishListModel(List<TrialPublishModel> list) {
        this.list = list
    }

    List<TrialPublishModel> getList() {
        return list
    }

    void setList(List<TrialPublishModel> list) {
        this.list = list
    }
}
