package com.hxht.autodeploy.court.manager

import com.hxht.techcrt.LogSystemUtil
import com.hxht.techcrt.court.mem.CdBurning
import com.hxht.techcrt.Resp
import com.hxht.techcrt.enums.RespType
import grails.converters.JSON
import grails.gorm.transactions.Transactional

/**
 * 配置光盘刻录路径
 */
@Transactional("mem")
class CdBurningController {

    CdBurningService cdBurningService

    /**
     * 光盘刻录路径配置列表
     * @return view="user.list"
     */
    def list() {
        if (request.method == "POST") {
            def draw = params.int("draw") ?: 1// 记录操作的次数 每次加1
            def start = params.int("start") ?: 0// 起始
            def length = params.int("length") ?: 20// 每页显示的size
            def search = params.get("search[value]") as String//搜索内容
            def model = cdBurningService.list(draw, start, length, search)
            render model as JSON
        }
    }

    /**
     * 前往添加光盘刻录路径配置页面
     */
    def add() {}

    /**
     * 保存光盘刻录路径配置
     */
    def addSave() {
        def url = params.get("url") as String
        def orderNum = params.get("orderNum") as Integer
        if (!url || !orderNum) {
            render Resp.toJson(RespType.DATA_NOT_EXIST)
            return
        }
        //添加
        def cdBurning = new CdBurning()
        cdBurning.url = url
        cdBurning.orderNum = orderNum
        cdBurningService.addSave(cdBurning)
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 删除部分光盘刻录路径配置
     * @return RespType.data
     */
    def del() {
        LogSystemUtil.log(LogSystemUtil.INFO, "删除部分光盘刻录路径配置")
        def cdBurningIdStr = params.get("cdBurningIds") as String
        if (!cdBurningIdStr) {
            render Resp.toJson(RespType.DATA_NOT_EXIST)
            return
        }
        def cdBurningArr = cdBurningIdStr.split(",")
        cdBurningService.del(cdBurningArr)
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 前往修改光盘刻录路径配置信息页面
     */
    def edit() {
        //获取光盘刻录路径配置
        def cdBurning = CdBurning.get(params.long("id"))
        [cdBurning: cdBurning]
    }

    /**
     * 更新光盘刻录路径配置信息
     */
    def editSave() {
        //获取这个光盘刻录路径配置信息
        def url = params.get("url") as String
        def cdBurningId = params.long("cdBurningId")
        def orderNum = params.get("orderNum") as Integer
        if (!cdBurningId || !url || !orderNum){
            render Resp.toJson(RespType.FAIL)
            return
        }
        def cdBurning = CdBurning.get(cdBurningId)
        cdBurning.url = url
        cdBurning.orderNum = orderNum
        //执行修改
        cdBurningService.addSave(cdBurning)
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 根据光盘刻录路径名获取信息
     * @return RespType.data
     */
    def getCdBurningByUrl() {
        def url = params.get("url") as String
        def count = cdBurningService.countUrl(url)
        render Resp.toJson(RespType.SUCCESS, count)
    }
}
