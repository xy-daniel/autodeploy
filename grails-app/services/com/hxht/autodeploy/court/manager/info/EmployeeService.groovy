package com.hxht.autodeploy.court.manager.info

import com.hxht.techcrt.PositionStatus
import com.hxht.techcrt.court.Department
import com.hxht.techcrt.court.Employee
import com.hxht.techcrt.court.admin.ToolBoxService
import com.hxht.techcrt.User
import com.hxht.techcrt.utils.UUIDGenerator
import grails.gorm.transactions.Transactional

@Transactional
class EmployeeService {

    ToolBoxService toolBoxService

    /**
     * 人员列表
     * @param draw 标志
     * @param start 起始坐标
     * @param length 长度
     * @param search 搜索关键词
     */
    def list(int draw, int start, int length, String search) {
        def model = [:]
        model.put("draw", draw)
        def positionVo = PositionStatus.getCode(search)
        def count = Employee.createCriteria().count() {
            if (search) {
                or {
                    //名字相似
                    like("name", "%${search}%")
                    //部门相同
                    if (positionVo==PositionStatus.JUDGE || positionVo==PositionStatus.SECRETARY || positionVo==PositionStatus.POLICE){
                        eq("position", positionVo)
                    }
                }
            }
        }
        def dataList = Employee.createCriteria().list {
            and {
                setMaxResults(length)
                setFirstResult(start)
            }

            or {
                //名字相似
                like("name", "%${search}%")
                //部门相同
                if (positionVo==PositionStatus.JUDGE || positionVo==PositionStatus.SECRETARY || positionVo==PositionStatus.POLICE){
                    eq("position", positionVo)
                }
            }
        } as List<Employee>
        def modelDataList = []
        for (def employee : dataList) {
            //id--人员姓名--所属部门名称
            def data = [:]
            data.put("id", employee.id)
            data.put("name", employee.name)
            if (employee.position == null || employee.position == ""){
                employee.position = PositionStatus.getCode("其他")
                employee.save(flush: true)
            }
            data.put("position", PositionStatus.getString(employee.position))
            data.put("deptName", employee.dept?.name)
            modelDataList.add(data)
        }
        model.put("recordsTotal", count)//数据总条数
        model.put("recordsFiltered", count)//显示的条数
        model.put("data", modelDataList)
        model
    }

    /**
     * 更新职员信息
     * @param emp 需要更新信息的职员(职员属性在controller已经更新)
     */
    def editSave(Employee emp) {
        emp.save(flush: true)
    }

    /**
     * 添加新职员
     * @param emp 职员信息-->属性在controller已添加
     */
    def addSave(Employee emp) {
        emp.save(flush: true)
    }

    /**
     * 慧谷数据对接，添加员工
     * @param name  员工姓名
     * @param deptId  员工所属部门
     * @param interfaceId  员工原始数据同步ID
     */
    def addEmployee4HuiGu(String name, String deptId, String interfaceId){
        def employee = new Employee(
                uid: UUIDGenerator.nextUUID(),
                name: name,
                position: 255,
                dept:  Department.findBySynchronizationId(deptId),
                synchronizationId: interfaceId
        ).save(flush: true)
        if (employee.hasErrors()){
            log.error("[EmployeeService addEmployee4HuiGu] 添加从慧谷获取的人员数据出错,错误信息:{}", employee.errors)
            throw new RuntimeException("对接慧谷数据,新增员工失败")
        }
        //人员添加成功之后添加用户
        toolBoxService.importUser(employee)
    }

    def updateEmployee4HuiGu(Employee localEmployee, String name, String deptId){
        log.info("员工姓名{},部门{}", name, Department.findBySynchronizationId(deptId)?:name)
        localEmployee.name = name
        localEmployee.dept = Department.findBySynchronizationId(deptId)
        localEmployee.save(flush: true)
        if (localEmployee.hasErrors()){
            log.error("[EmployeeService updateEmployee4HuiGu] 更新从慧谷获取的人员数据出错,错误信息:{}", localEmployee.errors)
            throw new RuntimeException("对接慧谷数据,更新员工信息失败")
        }
        //更新时判断用户是否存在，如果不存在则新增一个User
        def user = User.findByEmployee(localEmployee.id)
        if (!user){
            log.info("User用户不存在，添加一个新的")
            log.info("[EmployeeService updateEmployee4HuiGu] 更新人员时发现没有对应的用户，为id为{}的人员添加用户", localEmployee.id)
            toolBoxService.importUser(localEmployee)
        }
        log.info("User用户已经存在")
    }
}
