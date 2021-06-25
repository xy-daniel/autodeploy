package com.hxht.autodeploy.court.manager.info

import com.hxht.techcrt.Resp
import com.hxht.techcrt.court.CaseType
import com.hxht.techcrt.enums.RespType
import grails.gorm.transactions.Transactional

@Transactional
class CaseTypeService {

    /**
     * 案件类型列表
     * @param draw  标志
     * @param start  起始坐标
     * @param length  长度
     * @param search  搜索关键词
     * @param id  上级部门主键
     * @return  List<Department>
     */
    def list(int draw,int start, int length, String search, long id) {
        CaseType d = CaseType.get(id)
        def model = [:]
        model.put("draw", draw)
        def count = CaseType.createCriteria().count() {
            if (d){
                eq("parent",d)
            }
            if (search) {
                or {
                    like("shortName", "%${search}%")
                    like("code", "%${search}%")
                    like("name", "%${search}%")
                }
            }
        }
        def dataList = CaseType.createCriteria().list() {
            if (d){
                eq("parent",d)
            }
            and {
                setMaxResults(length)
                setFirstResult(start)
            }
            if (search){
                or {
                    like("shortName", "%${search}%")
                    like("code", "%${search}%")
                    like("name", "%${search}%")
                }
            }
        } as List<CaseType>
        def modelDataList = []
        for (def caseType : dataList) {
            def data = [:]
            data.put("id", caseType.id)
            data.put("shortName", (caseType.shortName==null || caseType.shortName=="")?"无":caseType.shortName)
            data.put("code", caseType.code)
            data.put("name", caseType.name)
            if (d!=null){
                data.put("fromCaseType", d.name)
            }else{
                data.put("fromCaseType", "无")
            }
            modelDataList.add(data)
        }
        model.put("recordsTotal", count)//数据总条数
        model.put("recordsFiltered", count)//显示的条数
        model.put("data", modelDataList)
        model
    }

    /**
     * 案件类型添加操作
     * @param shortName 短字
     * @param code 代码
     * @param name 名称
     * @param typeId 父类型主键
     * @return 是否
     */
    def addSave (String shortName, String code, String name, Long typeId) {
        def ct = new CaseType(
                shortName: shortName,
                code: code,
                name: name,
                parent: CaseType.get(typeId)
        )
        ct.save(flush: true)
        if (ct.hasErrors()) {
            log.error("[CaseTypeService.addSave] 添加案件类型失败，错误信息:${ct.errors}")
            throw new RuntimeException("添加案件类型失败")
        }
    }

    /**
     * 案件类型编辑操作
     * @param ctId
     * @param shortName
     * @param code
     * @param name
     * @param typeId
     */
    def editSave (Long ctId, String shortName, String code, String name, Long typeId) {
        def type = CaseType.get(ctId)
        type.shortName = shortName
        type.code = code
        type.name = name
        type.parent = CaseType.get(typeId)
        type.save(flush: true)
        if (type.hasErrors()){
            log.error("[CaseTypeService.editSave] 修改案件类型时异常，异常信息：${type.errors}")
            throw new RuntimeException("修改案件类型时异常")
        }
    }

    def delTypes (String typeIdsStr) {
        def typeIdsArr = typeIdsStr.split(",")
        def num = 0
        for (String typeId : typeIdsArr){
            //根据抓紧获取这个案件类型
            def type = CaseType.get(typeId as long)
            //根据这个案件类型查询这个案件类型下面的子类型
            def count = CaseType.countByParent(type)
            if (count>0){
                //如果存在子类型不进行删除
                num++
            }else{
                //如果不存在子类型则进行删除
                type.delete(flush: true)
                if (type.hasErrors()) {
                    log.error("[CaseTypeService.delTypes] 删除案件类型时异常，异常信息：${type.errors}")
                    throw new RuntimeException("删除案件类型时异常")
                }
            }
        }
        if (num>0){
            //如果存在部分类型无法删除则给与提示
            return Resp.toJson(RespType.FAIL)
        }else{
            //所选案件类型完全删除
            return Resp.toJson(RespType.SUCCESS)
        }
    }
}
