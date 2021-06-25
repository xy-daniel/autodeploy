package com.hxht.autodeploy.sync.DongRuan

import cn.hutool.core.date.DateUtil
import com.hxht.techcrt.Ajlb
import com.hxht.techcrt.AnYou
import com.hxht.techcrt.DataStatus
import com.hxht.techcrt.PlanStatus
import com.hxht.techcrt.User
import com.hxht.techcrt.court.CaseInfo
import com.hxht.techcrt.court.CaseType
import com.hxht.techcrt.court.Collegial
import com.hxht.techcrt.court.Courtroom
import com.hxht.techcrt.court.Department
import com.hxht.techcrt.court.Employee
import com.hxht.techcrt.court.PlanInfo
import com.hxht.techcrt.court.manager.SystemController
import com.hxht.techcrt.utils.PinYinUtils
import com.hxht.techcrt.utils.UUIDGenerator
import grails.gorm.transactions.Transactional
import org.dom4j.Element

class DongRuanPlanService {

    def planMap(Element element, List<Element> hytrecordsList, List<Element> dsrrecordsList, List<Element> ktrecordsList, Courtroom room) {
        //根据案件编号找出， 合议庭数据，当事人信息
        try {
            //案件编号获取
            def ajbh = element.elementText("ajbh")
            def map = [
                    secretary_id: element.elementText("sjybh"),  //书记员
                    dept_id     : element.elementText("cbbmbh"),//部门编号
                    caseno      : element.elementText("ah"), //案号
                    courtroom_id: room.getUid()//法庭
            ] as HashMap<String, Object>
            //开始匹配合议庭成员列表
            def collegialList = []
            for (def hytelement : hytrecordsList) {
                def hytbh = hytelement.elementText("ajbh")
                if (ajbh == hytbh) {//判断此条数据是否是当前案件数据
                    def role = hytelement.elementText("ryjs") //获取合议庭成员角色名称
                    def rybh = hytelement.elementText("rybh")
                    def ryxm = hytelement.elementText("ryxm")
                    switch (role) {
                        case "审判长":
                            def collegial = Collegial.withNewSession {
                                Collegial.findBySynchronizationId(rybh)
                            }
                            if (!collegial) {
                                collegial = new Collegial(
                                        name: ryxm,
                                        type: 1,//审判长角色
                                        synchronizationId: rybh
                                )
                            }
                            collegialList.add(collegial)
                            if (SystemController.currentCourt.ext3 == "J30") {//深圳中院承办人对应新系统承办人，审判长对应法官
                                map.put("judge_id", hytelement.elementText("rybh"))
                            }else{
                                map.put("undertake", hytelement.elementText("rybh"))
                            }
                            break
                        case "合议庭成员":
                            def collegial = Collegial.withNewSession {
                                Collegial.findBySynchronizationId(rybh)
                            }
                            if (!collegial) {
                                collegial = new Collegial(
                                        name: ryxm,
                                        type: 2,//审判长角色
                                        synchronizationId: rybh
                                )
                            }
                            collegialList.add(collegial)
                            break
                        case "承办人":
                            if (SystemController.currentCourt.ext3 == "J30") {//深圳中院承办人对应新系统承办人，审判长对应法官
                                map.put("undertake", hytelement.elementText("rybh"))
                            }else{
                                map.put("judge_id", hytelement.elementText("rybh"))
                            }
                            break
                        default:
                            break
                    }
                }
            }
            map.put("collegialList", collegialList)
            //原告,被告
            def accuse = ""
            def accused = ""
            for (def dsrelement : dsrrecordsList) {
                def dsr = dsrelement.elementText("ajbh")
                if (ajbh == dsr) {
                    def sslx = dsrelement.element("dsrmc").attributeValue("ssdwmc")//诉讼类型
                    if (sslx) {
                        def dsrmc = dsrelement.elementText("dsrmc")
                        def dsrbh = dsrelement.elementText("dsrbh")
                        if (sslx == "原告" || sslx == "申请人" || sslx == "上诉人") {//原告
                            //如果是第一个要特殊处理
                            if ("1" == dsrbh) {
                                accuse = accuse + dsrmc
                            } else {
                                accuse = accuse + "," + dsrmc
                            }
                        } else if (sslx == "被告" || sslx == "被告人" || sslx == "被申请人" || sslx == "被上诉人") {
                            //被告
                            if ("1" == dsrbh) {
                                accused = accused + dsrmc
                            } else {
                                accused = accused + "," + dsrmc
                            }
                        }
                    }

                }

            }
            map.put("accuse", accuse)
            map.put("accused", accused)
            this.planCaseFt(element, ktrecordsList, map)
        } catch (e) {
            e.printStackTrace()
            log.error("同步东软数据错误[getCaseAndPlan]，循环处理单个案件排期出错：${e.getMessage()}")
        }
    }

    def planCaseFt(Element element, List<Element> ktdata, Map<String, Object> map) throws Exception {
        try {
            //保存案件
            def bh = element.elementText("ajbh")  //案件编号获取
            def caseInfo = saveCaseInfo(element, map, bh)
            //注意-------- 一个案件可能存在多个排期
            for (def ktElement : ktdata) {
                //保存排期
                savePlanInfo(map, bh, caseInfo, ktElement)
            }
        } catch (Exception e) {
            log.error("同步东软数据错误[DongRuanPlanService]，处理单个案件排期数据出错，案件${map.get("caseno")}：${e.getMessage()}")
        }
    }

    def getCaseTypeByCaseno(String archives) {
        //根据返回的类型判断是刑事 民事等
        def caseTypeList = CaseType.findAll()
        for (def caseType : caseTypeList) {
            if (caseType.shortName) {
                if (archives.contains(caseType.shortName)) {
                    return caseType
                }
            }
        }
    }

    //保存案由
    @Transactional
    def saveAnyou(Element element){
        //先根据aybh查询，是否存在
        def aybh = element.elementText("aybh")
        if (!aybh) {
            return
        }
        def anYou = AnYou.findByAybh(aybh)
        if (!anYou) {
            //说明不存在,插入
            anYou = new AnYou(
                    aybh: aybh,
                    aymc: element.elementText("aymc"),
                    ayfdm: element.elementText("ayfdm")
            )
            anYou.save(flush: true)
            if (anYou.hasErrors()) {
                log.error("同步东软数据保存案由错误[DongRuanPlanService.saveAnyou]，入库排期数据出错：${anYou.errors}")
            }
        }

    }

    //保存案由
    @Transactional
    def saveAjlb(Element element){
        //先根据ajlbbh查询，是否存在
        def ajlbbh = element.elementText("ajlbbh")
        def spcxbh = element.elementText("spcxbh")
        if (!ajlbbh || !spcxbh) {
            return
        }
        def ajlb = Ajlb.findByAjlbbhAndSpcxbh(ajlbbh, spcxbh)
        if (!ajlb) {
            ajlb = new Ajlb(
                    ajlbbh: ajlbbh,
                    spcxbh: spcxbh,
                    ajlbmc: element.elementText("ajlbmc"),
                    spcxmc: element.elementText("spcxmc")
            )
            ajlb.save(flush: true)
            if (ajlb.hasErrors()) {
                log.error("同步东软数据保存案由错误[DongRuanPlanService.saveAjlb]，入库排期数据出错：${ajlb.errors}")
            }
        }

    }

    //保存部门
    @Transactional
    def saveDepartment(String uid, String name){
        def dept = Department.findByUid(uid)   //在toolbox中对比的是uid 和原来用的同步保存到的字段也是uid 所有用uid判断
        if (!dept) { // 先判断部门不是存在，存在更新，不存在插入
            dept = new Department()
            dept.uid = uid
        }
        dept.name = name
        dept.save(flush: true)
        if (dept.hasErrors()) {
            log.error("同步东软数据保存案由错误[DongRuanPlanService.saveDepartment]，入库排期数据出错：${dept.errors}")
        }
    }

    //保存法庭
    @Transactional
    def saveCourtroom(String uid, String name){
        def courtroom = Courtroom.findByUid(uid)
        if (!courtroom) {
            courtroom = new Courtroom(
                    uid: uid,
                    status: DataStatus.SHOW,
                    active: DataStatus.SHOW
            )
        }
        courtroom.name = name
        courtroom.save(flush: true)
        if (courtroom.hasErrors()) {
            log.error("同步东软数据保存案由错误[DongRuanPlanService.saveCourtroom]，入库排期数据出错：${courtroom.errors}")
        }
    }

    //保存人员
    @Transactional
    def saveEmployeeAndUsr(Element element){
        def name = element.elementText("ryxm")
        def uid = element.elementText("rybh")
        if (!uid || !name) {
            return
        }
        def employee = Employee.findByUid(uid)
        if (!employee) {
            employee = new Employee()
            employee.uid = uid
            employee.position = 255
            employee.name = name
            employee.dept = Department.findByUid(element.elementText("ssbmbh"))
            employee.save(flush: true)
            if (employee.hasErrors()) {
                log.error("同步东软数据保存案由错误[DongRuanPlanService.saveEmployee]，入库排期数据出错：${employee.errors}")
            }
            //为这名员工创建账号
            def username = PinYinUtils.getHanziPinYin(employee.name)
            if (username?.contains("lu:")) {
                username = username.replace("lu:", "lv")
            }
            if (User.findByUsername(username)) {
                return
            }
            def user = new User()
            user.uid = UUIDGenerator.nextUUID()
            user.enabled = true //账号启用
            user.accountExpired = false //账号过期
            user.accountLocked = false//账号锁定
            user.username = username
            user.password = '123456'//默认初始密码
            user.realName = employee.name//姓名
            user.employee = employee.id
            user.save(flush: true)
            if (user.hasErrors()) {
                log.error("同步东软数据保存案由错误[DongRuanPlanService.saveUser]，入库排期数据出错：${user.errors}")
            }
        }
    }

    @Transactional
    def saveCaseInfo(Element element, Map<String, Object> map,String bh) throws Exception{
        try {
            log.info("同步东软数据，保存单条排期-案号:${map.get("caseno")}")
            def ajlbbh = element.elementText("ajlbbh") //案件类别编号，查询几审 根据案件类别编号获取案件类别
            def ajlb = Ajlb.findByAjlbbh(ajlbbh)
            def aybh = element.elementText("aybh") //根据案由编号获取案由
            def anYou = AnYou.findByAybh(aybh)

            /*******处理案件类型*************/
            def typename = ajlb?.ajlbmc
            def caseType
            if (!typename) {
                //如果案件类别不存在则判断案号中的代字进行判断
                caseType = getCaseTypeByCaseno(map.get("caseno") as String)
                if (!caseType) {
                    caseType = CaseType.first()
                }
            } else {
                //根据typename查询，是否有相同类型
                caseType = CaseType.findByName(typename)
                if (!caseType) {
                    caseType = CaseType.findByShortName(typename)
                }
                if (!caseType) {
                    caseType = CaseType.findByNameLike("%${typename}%")
                }
                if (!caseType) {
                    caseType = getCaseTypeByCaseno(map.get("caseno") as String)
                }
                if (!caseType) {
                    caseType = CaseType.findAll().get(0)
                }
            }
            /*****************处理案件*********************************/
            // 根据案件编号查询案件是否存在
            def caseInfo = CaseInfo.findBySynchronizationId(bh)
            def cas = CaseInfo.findByArchives(map.get("caseno") as String)
            if (!caseInfo) {
                caseInfo = cas
                if (!caseInfo){
                    caseInfo = new CaseInfo()
                    caseInfo.uid = UUIDGenerator.nextUUID()
                    caseInfo.synchronizationId = bh
                }
            }
            // 案件名称--也设置为案由--先这样处理，
            caseInfo.archives = map.get("caseno")
            caseInfo.name = map.get("caseno")
            caseInfo.summary = anYou?.getAymc()
            caseInfo.caseCause = anYou?.getAymc()
            caseInfo.type = caseType
            if (map.get("dept_id")) {
                caseInfo.department = Department.findByUid(map.get("dept_id") as String)
            }
            if (map.get("accuse")) {
                caseInfo.accuser = map.get("accuse")
            }
            if (map.get("accused")) {
                caseInfo.accused = map.get("accused")
            }
            caseInfo.active = DataStatus.SHOW
            //如果为东软排期传过来的案件则进行更新，如为系统创建的相同案号则不进行更新操作
            if (!cas || cas.synchronizationId) {
                caseInfo.save(flush: true)
                if (caseInfo.hasErrors()) {
                    log.error("同步东软数据错误[DongRuanPlanService]，入库案件数据出错：${caseInfo.errors}")
                    return
                }
            }
            return caseInfo

        } catch (Exception e) {
            log.error("同步东软数据错误[DongRuanPlanService.saveCaseInfo]，处理单个案件数据出错，案件${map.get("caseno")}：${e.getMessage()}")
        }

    }

    @Transactional
    def savePlanInfo(Map<String, Object> map, String bh,CaseInfo caseInfo,Element ktElement) {
        try {
            //根据案件编号，找到对应的排期数据
            def pqbh = ktElement.elementText("ajbh")
            def ktbh = ktElement.elementText("ktbh")
            if (bh == pqbh) { //是对应的排期数据
                String ktcs = ktElement.elementText("ktcs")//开庭庭次
                // 先处理排期数据(根据synchronizationId判断该案件排期是否存在)
                def plan = PlanInfo.findBySynchronizationId(bh + "-" + ktcs + ktbh)
                if (plan) {
                    //排期状态下进行更新
                    if (plan.status != PlanStatus.PLAN) {
                        return
                    }
                }else{
                    plan = PlanInfo.findBySynchronizationId(bh + "-" + ktcs)
                    if (plan) {
                        //排期状态下进行更新
                        if (plan.status != PlanStatus.PLAN) {
                            return
                        }
                    }else{
                        plan = new PlanInfo()
                        plan.uid = UUIDGenerator.nextUUID()
                        plan.synchronizationId = bh + "-" + ktcs + ktbh
                    }
                }
                plan.courtroom = Courtroom.findByUid(map.get("courtroom_id") as String)
                // 预定日期
                def ktrq = ktElement.elementText("ktrq")
                //预定开始日期+时间
                plan.startDate = DateUtil.parse(ktrq + " " + ktElement.elementText("ydkssj"), "yyyy-MM-dd HH:mm:ss")
                //预定开始日期+时间
                plan.endDate = DateUtil.parse(ktrq + " " + ktElement.elementText("ydjssj"), "yyyy-MM-dd HH:mm:ss")
                plan.undertake = Employee.findByUid(map.get("undertake") as String)
                plan.judge = Employee.findByUid(map.get("judge_id") as String)
                plan.secretary = Employee.findByUid(map.get("secretary_id") as String)
                plan.collegial = map.get("collegialList") as List<Collegial>
                //是否撤销
                def ktzt = ktElement.elementText("ktzt")
                if ("撤消" == ktzt) {
                    plan.active = DataStatus.DEL
                } else {
                    plan.active = DataStatus.SHOW
                }
                plan.status = PlanStatus.PLAN
                plan.caseInfo = caseInfo
                plan.save(flush: true)
                if (plan.hasErrors()) {
                    log.error("同步东软数据错误[DongRuanPlanService]，入库排期数据出错：${plan.errors}")
                }
            }
        } catch (Exception e) {
            log.error("同步东软数据错误[DongRuanPlanService.savePlanInfo]，处理单个案件数据出错，案件${map.get("caseno")}：${e.getMessage()}")
        }

    }

}
