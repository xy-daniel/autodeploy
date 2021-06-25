package com.hxht.autodeploy.court.manager.info


import com.hxht.techcrt.PositionStatus
import com.hxht.techcrt.Resp
import com.hxht.techcrt.court.Department
import com.hxht.techcrt.court.Employee
import com.hxht.techcrt.court.TrialInfo
import com.hxht.techcrt.enums.RespType
import com.hxht.techcrt.utils.UUIDGenerator
import grails.converters.JSON

/**
 * 人员功能
 */
class EmployeeController {

    EmployeeService employeeService

    /**
     * 人员列表
     */
    def list() {
        if (request.method == "POST") {
            def draw = params.int("draw") ?: 1// 记录操作的次数 每次加1
            def start = params.int("start") ?: 0// 起始
            def length = params.int("length") ?: 10// 每页显示的size
            def search = params.get("search[value]") as String//搜索内容
            def model = employeeService.list(draw,start,length,search)
            render model as JSON
        }
    }

    /**
     * 前往职员编辑页面
     */
    def edit(){
        [employee: Employee.get(params.long("id")), depts: Department.findAll()]
    }

    /**
     * 人员编辑保存
     */
    def editSave(){
        //职员主键
        def empId = params.long("empId")
        //职员姓名
        def name = params.get("name") as String
        //职位
        def position = params.get("position")
        //部门主键
        def deptId = params.long("deptId")
        if (!(empId && name && deptId)){
            render Resp.toJson(RespType.FAIL)
            return
        }
        //根据职员主键获取职员信息
        def emp = Employee.get(empId)
        //更新职员信息
        emp.name = name
        emp.position = PositionStatus.getCode(position)
        emp.dept = Department.get(deptId)
        employeeService.editSave(emp)
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 前往新职员添加页面
     */
    def add(){
        [depts:Department.findAll()]
    }

    /**
     * 执行新职员添加操作
     */
    def addSave(){
        def name = params.get("name") as String
        def position = params.int("position")
        def deptId = params.long("deptId")
        if (!(name && deptId && position)){
            render Resp.toJson(RespType.FAIL)
            return
        }
        //查询所属部门
        def dept = Department.get(deptId)
        //新建职员
        def emp = new Employee(
                version: 0,
                position: position,
                dept: dept,
                name: name,
                uid: UUIDGenerator.nextUUID()
        )
        employeeService.addSave(emp)
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 多职员删除
     */
    def del(){
        def empIdsStr = params.get("empIds")
        if (!empIdsStr) {
            render Resp.toJson(RespType.DATA_NOT_EXIST)
            return
        }
        def empIdsArr = empIdsStr.split(",")
        def num = 0
        for (String empId : empIdsArr){
            //判断职员是否在trails_info中有信息
            def emp = Employee.get(empId as long)
            def count = TrialInfo.countByJudgeOrSecretary(emp, emp)
            if (count>0){
                num++
            }else{
                Employee.get(empId as long).delete(flush: true)
            }
        }
        if (num>0){
            render Resp.toJson(RespType.FAIL)
        }else{
            render Resp.toJson(RespType.SUCCESS)
        }
    }
}
