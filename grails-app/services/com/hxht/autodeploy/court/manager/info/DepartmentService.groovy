package com.hxht.autodeploy.court.manager.info

import com.hxht.techcrt.court.Department
import com.hxht.techcrt.service.sync.huigu.entity.Dept
import com.hxht.techcrt.utils.UUIDGenerator
import grails.gorm.transactions.Transactional

/**
 * 部门管理业务操作 by Arctic in 2019.10.25
 */
@Transactional
class DepartmentService {

    /**
     * 部门列表
     * @param draw  标志
     * @param start  起始坐标
     * @param length  长度
     * @param search  搜索关键词
     * @param id  上级部门主键
     * @return  List<Department>
     */
    def list(int draw,int start, int length, String search, long id) {
        Department d = Department.get(id)
        def model = [:]
        model.put("draw", draw)
        def count = Department.createCriteria().count() {
            if (d){
                eq("parent",d)
            }
            or {
                like("name", "%${search}%")
            }
        } as List<Department>
        def dataList = Department.createCriteria().list() {
            if (d){
                eq("parent",d)
            }
            and {
                setMaxResults(length)
                setFirstResult(start)
            }
            or {
                like("name", "%${search}%")
            }
        } as List<Department>
        def modelDataList = []
        for (def department : dataList) {
            //id--机构名称
            def data = [:]
            data.put("id", department.id)
            data.put("name", department.name)
            if (d!=null){
                data.put("fromDept", Department.get(id).name)
            }else{
                data.put("fromDept", "无")
            }
            modelDataList.add(data)
        }
        model.put("recordsTotal", count)//数据总条数
        model.put("recordsFiltered", count)//显示的条数
        model.put("data", modelDataList)
        model
    }

    /**
     * 部门添加
     * @param name  部门名称
     * @param department  父级部门
     */
    def addSave(String name, Department department){
        new Department(
                uid: UUIDGenerator.nextUUID(),
                version: 0,
                name: name,
                parent: department
        ).save(flush:true)
    }

    /**
     * 编辑保存
     * @param dept  需要更新的实体（信息已经完成修改）
     */
    def editSave(Department dept){
        dept.save(flush: true)
        if (dept.hasErrors()){
            log.error("[DepartmentService editSave] 更新部门时出错\n错误信息：${dept.errors}")
            throw new RuntimeException("更新部门时出错")
        }
    }

    def del(Department dept){
        dept.delete(flush: true)
        if (dept.hasErrors()){
            log.error("[DepartmentService del] 删除部门时出错\n错误信息：${dept.errors}")
            throw new RuntimeException("删除部门时出错")
        }
    }

    /**
     * 部门添加(对接慧谷数据)
     * @param name  部门名称
     * @param syncId  部门同步ID
     * @return  添加的新部门
     */
    def addDept4HuiGu(Dept dept){
        def newLocalDept = new Department(
                uid: UUIDGenerator.nextUUID(),
                name: dept.getDeptname(),
                synchronizationId: dept.getInterfaceId()
        ).save(flush: true)
        if (newLocalDept.hasErrors()){
            log.error("[DepartmentService addDept4HuiGu] 将从慧谷获取到的部门数据添加到本地时出错\n错误信息：${newLocalDept.errors}")
            throw new RuntimeException("添加部门时出错")
        }
        newLocalDept
    }

    def updateDept4HuiGu(Department localDept, String deptname, String interfacePid){
        if (deptname){
            localDept.name = deptname
        }
//        if (deptPid){
//            localDept.parent = Department.get(deptPid)
//        }
        if (interfacePid){
            def getLocalDept = Department.findBySynchronizationId(interfacePid)
            if (getLocalDept){
                localDept.parent = getLocalDept
            }
        }
        localDept.save(flush: true)
        if (localDept.hasErrors()){
            throw new RuntimeException("更新部门信息失败")
        }
    }
}
