package com.hxht.autodeploy.sync.luohu

import cn.hutool.core.date.DateUtil
import com.alibaba.fastjson.JSONObject
import com.hxht.techcrt.CollegialType
import com.hxht.techcrt.DataStatus
import com.hxht.techcrt.PlanStatus
import com.hxht.techcrt.court.admin.ToolBoxService
import com.hxht.techcrt.court.CaseInfo
import com.hxht.techcrt.court.CaseType
import com.hxht.techcrt.court.Collegial
import com.hxht.techcrt.court.Courtroom
import com.hxht.techcrt.court.Department
import com.hxht.techcrt.court.Employee
import com.hxht.techcrt.court.PlanInfo
import com.hxht.techcrt.utils.MD5Utils
import com.hxht.techcrt.utils.Spcx
import com.hxht.techcrt.utils.UUIDGenerator
import grails.gorm.transactions.Transactional


@Transactional
class LuoHuPlanInfoService {
    ToolBoxService toolBoxService
    def planSyncId

    /**
     * 预定案件排期信息
     * <p>
     * 注意此处，每一条数据都是一条事物，要保证排期，案件，人员编号，法庭事物一致性
     */
    def getCaseAndPlan(JSONObject obj,Courtroom room) {
        try {
            // 处理部门
            def dept = dept(obj.getJSONObject("pqbm"))
            // 处理书记员
            def secretary = secretary(obj.getJSONObject("sjy"),dept)
            //处理法官
            def zsfg = obj.getJSONObject("zsfg")
            if (zsfg.size() == 0) {//如果主审法官不存在就去合议庭去第一个元素
                zsfg = obj.getJSONArray("hyt_List").getJSONObject(0)
            }
            //法官目前待定
            def judge = judge(zsfg,dept)

            com.alibaba.fastjson.JSONArray ahs = obj.getJSONArray("ah_Info")
            if (ahs.size() == 0) {//不存在对应的案件信息
                log.info("处理罗湖法院对接接口排期++++" + obj.getString("pqid") + "号,没有对应的案件信息,不在处理")
            }
            if (ahs.size() > 1) {//一条排期有多个案件的情况
                log.info("处理罗湖法院对接接口--排期:(+++"+ obj.toJSONString() +"+++)信息存在多个案件,返回的本排期对应的案件数量:" + ahs.size())
            }
            //循环遍历所有案件 每个案件对应一个排期
            def ah_Info = new com.alibaba.fastjson.JSONObject()
            for (int k = 0; k < ahs.size(); k++) {
                ah_Info = ahs.getJSONObject(k)
                sub(ah_Info, secretary, judge, dept, room, obj)
            }

        } catch (Exception e) {
            log.error("处理罗湖法院对接接口---处理单个排期:" + obj.toJSONString() + "出错," + e.getMessage())
        }
    }


    private void sub(com.alibaba.fastjson.JSONObject ah_Info, Employee secretary, Employee judege, Department dept, Courtroom courtroom, com.alibaba.fastjson.JSONObject obj) throws Exception {

        def caseinformation = null
        //案号
        def archives = ah_Info.getString("ah")
        def casein = CaseInfo.findByArchives(archives)
        if (casein == null) {//不存在
            def caseInfo = new CaseInfo()
            caseinformation = saveCase(ah_Info,caseInfo,dept, obj)
        } else {
            // 处理排期编号
            def pqid = MD5Utils.getMD5(archives, false, 32)//生成32位小写md5
            def caseinfo = CaseInfo.findBySynchronizationId(pqid)
            //存在相同案号,判断是本地立案还是东软的案件
            if (caseinfo) {
                //是东软导入的案件,进行更新
                caseinformation = saveCase(ah_Info,caseinfo,dept, obj)
            } else {
               //说明不用更新
                caseinformation = casein
            }
        }

        def sycPlan = MD5Utils.getMD5(archives, false, 32) + "-" + Spcx.getSpcx(ah_Info.getString("spcx")) + "" //排期的案号 + 庭审的庭次
        def plan = PlanInfo.findBySynchronizationId(sycPlan)
        planSyncId = sycPlan
        if (plan){
            if (plan.status == PlanStatus.PLAN){//如果是排期状态则更新排期
                savePlan(secretary, judege, courtroom, obj,plan, sycPlan ,caseinformation)
            }
        }else{
            def planInfo = new PlanInfo()
            savePlan(secretary, judege, courtroom, obj,planInfo, sycPlan ,caseinformation)
        }

    }

    //2.1部门信息
    def dept(JSONObject dept1) {
        def dept = new Department()
        try {
            def id = dept1.getString("id")
            if (!id) {
                return dept
            }
            def name = dept1.getString("name")
            if (!name) {
                return dept
            }
            // 先判断部门不是存在，存在更新，不存在插入
            def returnDept = Department.findBySynchronizationId(id)
            if (!returnDept){
                //根据人员名称查找
                def returnName = Department.findByName(name)
                if (returnName){
                    return returnName
                }else{
                    dept.name = dept1.getString("name")
                    dept.uid = UUIDGenerator.nextUUID()
                    dept.synchronizationId = id
                    dept.save(flush: true)
                    if (dept.hasErrors()) {
                        log.error("LuoHuPlanInfoService.flush: true 处理罗湖法院对接接口++保存不存在的部门出错------[${dept.errors}]")
                        throw new RuntimeException()
                    }
                }
            }else{
                return returnDept
            }
        } catch (Exception e) {
            log.error("处理罗湖法院对接接口---处理部门出错:" + dept1.toJSONString() + "," + e.getMessage())
        }
        dept
    }

    //2.3人员
    def secretary(JSONObject secretary, Department dept) {
        def employee = new Employee()
        try {
            def name = secretary.getString("name")
            if (!name) {
                return employee
            }
            def uid = secretary.getString("id")
            if (!uid) {
                return employee
            }
            // 处理数据

            //userid根据人员姓名，获取首字母拼接
            // 根据人员编号来判断用户是否存在-----------
            def returnUser = Employee.findBySynchronizationId(uid)
            if (!returnUser) {
                def returnName = Employee.findByName(name)
                if (returnName){
                    return returnName
                }else{
                    employee.name = name
                    employee.position = 6
                    employee.uid = UUIDGenerator.nextUUID()
                    employee.dept = dept
                    employee.synchronizationId = uid
                    employee.save(flush: true)
                    if (employee.hasErrors()) {
                        log.error("LuoHuPlanInfoService.secretary 处理罗湖法院对接接口--保存不存在的人员出错------[${employee.errors}]")
                        throw new RuntimeException()
                    }
                    toolBoxService.importUser(employee)
                }
            } else {
                return returnUser;
            }
        } catch (Exception e) {
            log.error("处理罗湖法院对接接口--处理书记员," + secretary.toJSONString() + ",出错:" + e.getMessage())
        }
        employee
    }

    //2.3人员
    def judge(JSONObject judge, Department dept) {
        def employee = new Employee()
        try {
            def roleName = judge.getString("roleName")//承办人设置为法官
            if (!roleName) {
                return employee
            }
            def uid = judge.getString("userId")
            if (!uid) {
                return employee
            }
            String name = judge.getString("userName")
            if (!name) {
                return employee
            }

            def returnUser = Employee.findBySynchronizationId(uid)
            if (!returnUser) {
                def returnName = Employee.findByName(name)
                if (returnName){
                    return returnName
                }else{
                    employee.name = name
                    employee.position = 2
                    employee.uid = UUIDGenerator.nextUUID()
                    employee.dept = dept
                    employee.synchronizationId = uid
                    employee.save(flush: true)
                    if (employee.hasErrors()) {
                        log.error("LuoHuPlanInfoService.judge 处理罗湖法院对接接口+++保存不存在的人员出错------[${employee.errors}]")
                        throw new RuntimeException()
                    }
                    toolBoxService.importUser(employee)
                }
            } else {
                return returnUser
            }
        } catch (Exception e) {
            log.error("处理罗湖法院对接接口---处理法官出错," + judge.toJSONString() + ",错误信息:" + e.getMessage())
        }
        employee
    }

    //查找案件类型
    def caseType(String anhao) {
        if (anhao){
            anhao = anhao.replaceAll("粤","")
            anhao = anhao.replaceAll("号","")
            anhao = anhao.replaceAll("（","")
            anhao = anhao.replaceAll("）","")
            anhao = anhao.replaceAll("\\d+","")
            anhao = anhao.trim()
            CaseType.findByShortName(anhao)
        }
    }

    //保存案件信息
    def saveCase(com.alibaba.fastjson.JSONObject ah_Info,CaseInfo caseInfo,Department dept,com.alibaba.fastjson.JSONObject obj){
        //案由
        def laay = ah_Info.getString("laay")
        caseInfo.summary = laay
        // 案件名称--也设置为案由--先这样处理，
        caseInfo.name = laay
        //案号
        def archives = ah_Info.getString("ah")
        caseInfo.archives = archives
        // 处理排期编号
        String  pqid = MD5Utils.getMD5(archives, false, 32)//生成32位小写md5
        //审判次数
        def spcx = Spcx.getSpcx(ah_Info.getString("spcx")) + ""
        //处理案件类别
        CaseType type = caseType(archives)
        caseInfo.type = type
        //原告,被告
        String accuse = "";
        String accused = "";
        com.alibaba.fastjson.JSONArray dsr_array = ah_Info.getJSONArray("dsr")
        if (dsr_array.size() > 0) {
            int yuangao = 0
            int beigao = 0
            for (int j = 0; j < dsr_array.size(); j++) {
                com.alibaba.fastjson.JSONObject dsr = dsr_array.getJSONObject(j)
                String ssdw = dsr.getString("ssdw")
                if (ssdw != null && !"".equals(ssdw)) {
                    String name = dsr.getString("name")
                    if (ssdw.equals("原告") || ssdw.equals("申请人") || ssdw.equals("上诉人")) {
                        yuangao++;
                        //原告
                        //如果是第一个要特殊处理
                        if (j == dsr_array.size() - 1) {
                            if (yuangao > 1) {
                                accuse = accuse + "," + name;
                            } else {
                                accuse = accuse + name;
                            }

                        } else {
                            if ("".equals(accuse)) {
                                accuse = accuse + name;
                            } else {
                                accuse = accuse + "," + name;
                            }

                        }
                    }
                    if (ssdw.equals("被告") || ssdw.equals("被告人") || ssdw.equals("被申请人") || ssdw.equals("被上诉人")) {
                        beigao++;
                        //被告
                        if (j == dsr_array.size() - 1) {
                            if (beigao > 1) {
                                accused = accused + "," + name;
                            } else {
                                accused = accused + name;
                            }
                        } else {
                            if ("".equals(accused)) {
                                accused = accused + name;
                            }
                            accused = accused + "," + name;
                        }
                    }
                }
            }
        }

        caseInfo.accuser = accuse
        caseInfo.accused = accused
        caseInfo.department = dept
        //立案日期
//        caseInfo.filingDate = DateFormat.getDateInstance().parse(ah_Info.getString("larq"))
        // 预定日期
        def ktrq = obj.getString("ktrq")
        caseInfo.filingDate = DateUtil.parse(ktrq + " " + obj.getString("kssj"), "yyyy-MM-dd HH:mm:ss")
        caseInfo.active = DataStatus.SHOW //使用
        caseInfo.synchronizationId = pqid
        caseInfo.uid = UUIDGenerator.nextUUID()
        caseInfo.save(flush: true)
        if (caseInfo.hasErrors()) {
            log.error("LuoHuPlanInfoService.saveCase 处理罗湖法院对接接口---保存不存在的案件信息出错------[${caseInfo.errors}]")
            throw new RuntimeException()
        }
        caseInfo
    }

    //保存审理庭列表
    def saveCollegial(JSONObject hyt){
        def collegial = new Collegial()
        collegial.name = hyt.getString("userName")
        def type = hyt.getString("roleName")
        if (CollegialType.getCode(type) == CollegialType.PERSIDING_JUDGE) {
            collegial.type = CollegialType.PERSIDING_JUDGE
        } else if (CollegialType.getCode(type) == CollegialType.JUDGE) {
            collegial.type = CollegialType.JUDGE
        } else if (CollegialType.getCode(type) == CollegialType.PEOPLE_ASSESSOR) {
            collegial.type = CollegialType.PEOPLE_ASSESSOR
        }
        if (!collegial.type) {
            collegial.type = CollegialType.OTHER
        }
        collegial.save(flush: true)
        if (collegial.hasErrors()) {
            log.error("处理罗湖法院对接接口+++保存合议庭时出错luohuplaninfoservice.saveCollegial collegial [${collegial.errors}]")
            throw new RuntimeException()
        }
        collegial
    }

    //保存排期信息
    def savePlan(Employee secretary, Employee judege, Courtroom courtroom, com.alibaba.fastjson.JSONObject obj,PlanInfo planInfo, String sycPlan,CaseInfo caseInfo){
        planInfo.judge = judege
        planInfo.secretary = secretary
        planInfo.courtroom = courtroom
        planInfo.synchronizationId = sycPlan //排期的案号 + 庭审的庭次
        planInfo.uid = UUIDGenerator.nextUUID()
        //合议庭成员
        def hytList = obj.getJSONArray("hyt_List")

        if (hytList){
            def collegialList = Collegial.findAllBySynchronizationId(planSyncId)
            if (collegialList){
                planInfo.collegial = collegialList
            }else{
                for (int k = 0; k < hytList.size(); k++) {
                    def hyt = hytList.getJSONObject(k)
                    def colleage = saveCollegial(hyt)
                    collegialList.add(colleage)
                }
                planInfo.collegial = collegialList
            }

        }
        // 预定日期
        def ktrq = obj.getString("ktrq")
        //预定开始日期+时间
        planInfo.startDate = DateUtil.parse(ktrq + " " + obj.getString("kssj"), "yyyy-MM-dd HH:mm:ss")
        //预定结束时间
        planInfo.endDate = DateUtil.parse(ktrq + " " + obj.getString("jssj"), "yyyy-MM-dd HH:mm:ss")
        planInfo.status = PlanStatus.PLAN
        planInfo.active = DataStatus.SHOW
        planInfo.allowPlay = 0
        planInfo.caseInfo = caseInfo
        planInfo.save(flush: true)
        if (planInfo.hasErrors()) {
            def msg = "[luohuplaninfoservice.sub]处理罗湖法院对接接口---保存plan 失败 errors [${planInfo.errors}]"
            log.error(msg)
            throw new RuntimeException(msg)
        }
    }

}
