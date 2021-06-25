package com.hxht.autodeploy.sync.huaxia.model

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlType

/**
 * 2021.03.18 >>> 华夏推送部门列表实体创建 daniel
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "params")
@XmlType()
class DeptListModel {

    @XmlElement(name = "bm")
    private List<DeptModel> list

    DeptListModel() {
    }

    DeptListModel(List<DeptModel> list) {
        this.list = list
    }

    List<DeptModel> getList() {
        return list
    }

    void setList(List<DeptModel> list) {
        this.list = list
    }

}
