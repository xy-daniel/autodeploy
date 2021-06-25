package com.hxht.autodeploy

import grails.converters.JSON

/**
 * 法院列表 created by daniel in 2021.04.19
 */
class DistanceCourt {

    //唯一主键
    String uid

    //法院名称
    String name

    //短名称
    String shortName

    //分级码
    String code

    //服务地址
    String service

    //所有的子节点
    static hasMany = [children: DistanceCourt]

    //父节点
    static belongsTo = [parent: DistanceCourt]

    static constraints = {
        parent nullable: true
    }

    static mapping = {
        uid comment: "唯一编号", index: true
        name comment: "法院名称"
        service comment: "服务地主"
        children comment: "所有子节点"
        parent comment: "父节点"
        comment: "法院列表"
    }


    @Override
    String toString() {
        return ([
                id: id,
                uid: uid,
                name: name,
                shortName: shortName,
                code: code,
                service: service,
                parent: parent.toString()
        ] as JSON) .toString()
    }
}
