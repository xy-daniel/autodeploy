package com.hxht.autodeploy

import com.hxht.techcrt.court.Courtroom

class SecretaryVersion {

    //所属法庭
    Courtroom courtroom

    //书记员软件版本
    String serviceVersion

    //法官软件版本
    String fgSoftVersion

    //当事人软件版本
    String dsrSoftVersion

    //庭审设备版本(硬件版本/软件版本)
    String deviceVersion

    static constraints = {
        serviceVersion nullable: true
        fgSoftVersion nullable: true
        dsrSoftVersion nullable: true
        deviceVersion nullable: true
    }

    static mapping = {
        autoTimestamp(true)
        courtroom comment: "所属法庭"
        serviceVersion comment: "书记员版本"
        fgSoftVersion comment: "法官版本"
        dsrSoftVersion comment: "当事人版本"
        deviceVersion comment: "庭审设备硬件版本/软件版本"
    }
}
