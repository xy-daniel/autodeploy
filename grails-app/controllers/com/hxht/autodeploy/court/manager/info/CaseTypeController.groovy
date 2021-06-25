package com.hxht.autodeploy.court.manager.info

import com.hxht.techcrt.Resp
import com.hxht.techcrt.court.CaseType
import com.hxht.techcrt.enums.RespType
import grails.converters.JSON

/**
 * 案件类型相关功能
 */
class CaseTypeController {

    CaseTypeService caseTypeService

    /**
     * 案件类型列表
     */
    def list() {
        if (request.method == "POST") {
            def draw = params.int("draw") ?: 1// 记录操作的次数 每次加1
            def start = params.int("start") ?: 0// 起始
            def length = params.int("length") ?: 10// 每页显示的size
            def search = params.get("search[value]") as String//搜索内容
            def id = params.long("id")
            def model = caseTypeService.list(draw, start, length, search, id)
            render model as JSON
        }
    }

    /**
     * 通过案件类型名称查询案件类型数量
     */
    def getTypeByname(){
        render Resp.toJson(RespType.SUCCESS, CaseType.countByName(params.get("name") as String))
    }

    /**
     * 前往案件类型编辑页面
     */
    def edit(){
        def ct = CaseType.get(params.long("id"))
        def types = CaseType.findAll()
        [ct:ct, types:types]
    }

    /**
     * 执行案件类型更新操作
     */
    def editSave(){
        def ctId = params.long("ctId")
        //可以为空
        def shortName = params.get("shortName") as String
        def code = params.get("code") as String
        def name = params.get("name") as String
        //可以为空
        def typeId = params.long("typeId")
        if (!(ctId && name && code.length()==4)){
            render Resp.toJson(RespType.FAIL)
            return
        }
        caseTypeService.editSave(ctId, shortName, code, name, typeId)
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 前往案件类型添加页面
     */
    def add() {
        [types: CaseType.findAll(), id: params.long("id")]
    }

    /**
     * 执行案件类型添加操作
     */
    def addSave(){
        def shortName = params.get("shortName") as String
        def code = params.get("code") as String
        def name = params.get("name") as String
        def typeId = params.long("typeId")
        if (!(code && name && code.length()==4)){
            render Resp.toJson(RespType.FAIL)
            return
        }
        caseTypeService.addSave(shortName, code, name, typeId)
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 多案件类型删除
     */
    def del(){
        def typeIdsStr = params.get("typeIds") as String
        if (!typeIdsStr) {
            render Resp.toJson(RespType.DATA_NOT_EXIST)
            return
        }
        render caseTypeService.delTypes(typeIdsStr)
    }
}
