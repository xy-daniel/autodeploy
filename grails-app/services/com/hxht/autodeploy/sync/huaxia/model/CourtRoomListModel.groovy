package com.hxht.autodeploy.sync.huaxia.model

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlType

/**
 * 2021.03.18 >>> 华夏推送法庭列表实体创建 daniel
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "params")
@XmlType()
class CourtRoomListModel {
    @XmlElement(name = "ft")
    private List<CourtRoomModel> list

    CourtRoomListModel() {}

    CourtRoomListModel(List<CourtRoomModel> list) {
        this.list = list
    }

    List<CourtRoomModel> getList() {
        return list
    }

    void setList(List<CourtRoomModel> list) {
        this.list = list
    }
}
