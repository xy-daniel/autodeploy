package com.hxht.autodeploy.court.manager

import com.hxht.techcrt.DistanceCourt
import grails.converters.JSON

/**
 * 远程法院Controller created by daniel in 2021.04.19
 * 2021.04.20 >>> 添加删除法院功能 daniel
 */
class DistanceCourtController {

    DistanceCourtService distanceCourtService

    /**
     * 远程法院列表
     */
    def list() {
        if (request.method == "POST") {
            render distanceCourtService.list(
                    params.int("draw") ?: 1,
                    params.int("start") ?: 0,
                    params.int("length") ?: 10,
                    params.get("search[value]") as String,
                    params.long("id")) as JSON
        }
    }

    /**
     * 响应远程法院添加页面
     * @param id 上级远程法院id
     * @return 上级远程法院id、远程法院列表
     */
    def add(Long id) {
        [parents: DistanceCourt.findAll(), id: id]
    }

    /**
     * 执行远程法院添加操作
     * @return 响应码
     */
    def addSave() {
        render distanceCourtService.addSave(params)
    }

    /**
     * 响应远程法院编辑页面
     * @return 当前远程大院、所有远程法院列表
     */
    def edit() {
        [dc: DistanceCourt.get(params.long("id")), parents: DistanceCourt.findAll()]
    }

    /**
     * 执行远程法院修改操作
     * @return 响应码
     */
    def editSave() {
        render distanceCourtService.editSave(params)
    }

    /**
     * 执行删除
     */
    def del(){
        render distanceCourtService.del(params.get("ids"))
    }
}
