package com.hxht.autodeploy.api

import com.hxht.techcrt.Resp
import com.hxht.techcrt.enums.RespType
import org.grails.web.json.JSONArray

/**
 * 临时数据接口
 */
class SourceController {
    SourcesService sourcesService

    /**
     * 部门接口
     */
    def department(){
        def department = request.JSON as JSONArray
        def rs = sourcesService.saveDepartment(department)
        if (rs==0){
            render Resp.toJson(RespType.SUCCESS)
        }else{
            def msg = "临时数据保存部门接口出错"
            log.error("[SourceController.department]${msg}")
            render Resp.toJson(RespType.FAIL, msg)
        }
    }

    /**
     * 人员接口
     */
    def employee() {
        def employee = request.JSON as JSONArray
        def rs = sourcesService.saveEmployee(employee)
        if (rs==0){
            render Resp.toJson(RespType.SUCCESS)
        }else{
            def msg = "临时数据保存人员接口出错"
            log.error("[SourceController.employee]${msg}")
            render Resp.toJson(RespType.FAIL, msg)
        }
    }

    /**
     * 法庭接口
     */
    def courtroom(){
        def courtroom = request.JSON as JSONArray
        def rs = sourcesService.saveCourtroom(courtroom)
        if (rs.code==0){
            render Resp.toJson(RespType.SUCCESS)
        }else{
            def msg = "临时数据保存法庭接口出错"
            log.error("[SourceController.courtroom]${msg}")
            render Resp.toJson(RespType.FAIL, msg)
        }
    }

    /**
     * 集中处理排期、案件、庭审、视频
     */
    def handlePlan(){
        try{
            def data = request.JSON as JSONArray
            def json = data.get(0)
            sourcesService.handlePlan(json)
            render Resp.toJson(RespType.SUCCESS)
        }catch(e){
            e.printStackTrace()
            def msg = "临时数据接口保存排期出错！${e.message}"
            log.error("[SourceController.handlePlan]${msg}")
            render Resp.toJson(RespType.FAIL, msg)
        }
    }
}