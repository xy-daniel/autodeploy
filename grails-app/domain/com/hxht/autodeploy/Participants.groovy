package com.hxht.autodeploy

import com.hxht.techcrt.court.CaseInfo

/**
 * 接口对接参与人
 */
class Participants {

    //参与人姓名
    String name
    //参与人手机号
    String iphoneNumber
    //参与人身份证号
    String idCardNumber
    //参与人类型（1:原告人 2:原告人律师 3:被告人 4:被告人律师）
    Integer type
    //对应案件
    CaseInfo about

    static constraints = {
        //身份证号唯一
        idCardNumber unique: true
    }

    static mapping = {
        autoTimestamp(true)
        name comment: "参与人姓名"
        iphoneNumber comment: "参与人手机号"
        idCardNumber comment: "参与人身份证号"
        type comment: "案件类型"
        about comment: "所属案件"
        comment "各厂商参与人对接表"
    }
}
