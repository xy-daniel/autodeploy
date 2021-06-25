package com.hxht.autodeploy.court.manager.info

import com.hxht.techcrt.Resp
import com.hxht.techcrt.court.Department
import com.hxht.techcrt.court.Employee
import com.hxht.techcrt.enums.RespType
import grails.converters.JSON

/**
 * 部门管理控制器  by Arctic in 2019.10.25
 */
class DepartmentController {

    DepartmentService departmentService

    /**
     * 部门列表
     */
    def list() {
        if (request.method == "POST") {
            def draw = params.int("draw") ?: 1// 记录操作的次数 每次加1
            def start = params.int("start") ?: 0// 起始
            def length = params.int("length") ?: 10// 每页显示的size
            def search = params.get("search[value]") as String//搜索内容
            def id = params.long("id")
            def model = departmentService.list(draw,start,length,search,id)
            render model as JSON
        }
    }

    /**
     * 前往部门添加页面
     */
    def add(Long id){
        [departmentList: Department.findAll(), id: id]
    }

    /**
     * 根据部门名称获取部门数量
     */
    def getDepartmentByname(){
        render Resp.toJson(RespType.SUCCESS, Department.countByName(params.get("name") as String))
    }

    /**
     * 执行部门保存操作
     */
    def addSave(){
        def name = params.get("name") as String
        def department = Department.get(params.long("departmentId"))
        //只需要部门名称
        if (!name){
            log.info("[DepartmentController addSave] 添加部门name参数不存在")
            render Resp.toJson(RespType.FAIL)
            return
        }
        def count = Department.countByName(params.get("name") as String)
        if (count!=0){
            log.info("[DepartmentController addSave] 以${name}为名称的部门已存在")
            render Resp.toJson(RespType.DATA_ALREADY_EXIST)
            return
        }
        departmentService.addSave(name, department)
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 执行部门删除操作
     */
    def del(){
        def departmentIdStr = params.get("departmentIds") as String
        if (!departmentIdStr) {
            log.info("[DepartmentController del] 需要删除的部门ID不存在")
            render Resp.toJson(RespType.DATA_NOT_EXIST)
            return
        }
        def departmentIdArr = departmentIdStr.split(",")
        def num = 0
        for (String departmentId : departmentIdArr){
            def dept = Department.get(departmentId as long)
            //根据主键查询这个部门是否含有员工是否含有子部门---->有不允许删除，没有允许删除
            def empCount = Employee.countByDept(dept)
            def deptsCount = dept.depts.size()
            if (empCount>0 || deptsCount>0){
                num++
            }else{
                departmentService.del(dept)
            }
        }
        if (num>0){
            log.info("[DepartmentController del] 需要删除的部门中有一部分存在用户或者有子部门，已自动拒绝删除")
            render Resp.toJson(RespType.FAIL)
            return
        }
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 前往部门更新页面
     */
    def edit(){
        def dept = Department.get(params.long("id"))
        [departmentList:Department.findAll(),dept:dept]
    }

    /**
     * 执行部门更新操作
     */
    def editSave(){
        def id = params.long("deptId")
        def name = params.get("name") as String
        //父级部门
        def department = Department.get(params.long("departmentId"))
        //更新
        if (!(id && name)){
            log.info("[DepartmentController editSave] 需要更新的部门name和id不存在")
            render Resp.toJson(RespType.FAIL)
            return
        }
        def count = Department.countByName(params.get("name") as String)
        def dept = Department.get(id)
        if (count != 0 && name != dept.name){
            log.info("[DepartmentController editSave] 需要更新的部门name不允许")
            render Resp.toJson(RespType.DATA_ALREADY_EXIST)
            return
        }
        dept.name = name
        dept.parent = department
        departmentService.editSave(dept)
        render Resp.toJson(RespType.SUCCESS)
    }
}
