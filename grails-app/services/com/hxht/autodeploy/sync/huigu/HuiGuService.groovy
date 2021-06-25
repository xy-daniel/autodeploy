package com.hxht.autodeploy.sync.huigu

import com.hxht.techcrt.PlanStatus
import com.hxht.techcrt.court.manager.info.CaseInfoService
import com.hxht.techcrt.court.manager.info.DepartmentService
import com.hxht.techcrt.court.manager.info.EmployeeService
import com.hxht.techcrt.court.plan.PlanService
import com.hxht.techcrt.court.*
import com.hxht.techcrt.service.huigu.AnalysisXml
import com.hxht.techcrt.service.sync.huigu.WebServiceUtil_HuiGu
import com.hxht.techcrt.service.sync.huigu.entity.Case
import com.hxht.techcrt.service.sync.huigu.entity.Dept
import com.hxht.techcrt.service.sync.huigu.entity.Users
import com.hxht.techcrt.service.sync.huigu.pojo.RJRemotePlan
import com.hxht.techcrt.sync.util.WebServiceNormalService
import com.hxht.techcrt.utils.UUIDGenerator
import grails.core.GrailsApplication
import grails.events.EventPublisher
import grails.gorm.transactions.Transactional
import org.dom4j.Document
import org.dom4j.DocumentHelper
import org.dom4j.Element
import org.grails.web.json.JSONObject
import org.springframework.util.ObjectUtils

import java.text.DateFormat
import java.text.ParseException

/**
 * 2021.04.26 >>> 格式化日志 daniel
 */
@Transactional
class HuiGuService implements EventPublisher {

    GrailsApplication grailsApplication
    DepartmentService departmentService
    EmployeeService employeeService
    CaseInfoService caseInfoService
    PlanService planService
    WebServiceNormalService webServiceNormalService

    /**
     * 同步部门数据（能够获取到数据并且添加和更新目前一切正常）
     */
    void synchroDepartment() {
        log.info("[HuiGuService.synchroDepartment] 开始同步部门数据.")
        //获取数据
        WebServiceUtil_HuiGu wsClient = WebServiceUtil_HuiGu.newinstance()
        String deptXmlString = wsClient.callws(grailsApplication.config.getProperty('syncData.huigu.url'), "synchroDepartment", AnalysisXml.makeWsParam())
        //没有获取到数据
        if (ObjectUtils.isEmpty(deptXmlString)) {
            log.error("[HuiGuService.synchroDepartment] 没有获取到部门数据,同步失败.")
            return
        }
        log.info("[HuiGuService.synchroDepartment] 获取部门数据成功,数据如下:\n${deptXmlString}")
        //数据解析----Dept为原始数据部门对象
        List<Dept> deptList = AnalysisXml.resolveDept(deptXmlString)
        if (ObjectUtils.isEmpty(deptList)) {
            log.error("[HuiGuService.synchroDepartment] 没有解析到deptList(原始数据部门对象列表),同步失败.")
            return
        }
        log.info("[HuiGuService.synchroDepartment] 开始循环deptList(原始数据),数量:${deptList.size()}.")
        for (Dept dept : deptList) {
            Department localDept = Department.findBySynchronizationId(dept.interfaceId)
            Department newLocalDept
            //如果没有查询到
            if (ObjectUtils.isEmpty(localDept)) {
                log.info("[HuiGuService.synchroDepartment] 本地部门不存在:${dept.toString()},现在将会进行新增操作.")
                newLocalDept = departmentService.addDept4HuiGu(dept)
            } else {
                if (!Objects.equals(localDept.name, dept.deptname)) {
                    log.info("[HuiGuService.synchroDepartment] 本地部门:${localDept}与慧谷数据不一致,现在只更新部门名称.")
                    departmentService.updateDept4HuiGu(localDept, dept.deptname, null)
                }
            }
            if (newLocalDept) {
                log.info("[HuiGuService.synchroDepartment] 部门添加后开始对父级数据进行处理.")
                if (dept.interfacePid) {
                    log.info("[HuiGuService.synchroDepartment] 原始数据含有interfacePid:${dept.interfacePid},下面根据这个interfacePid查看本地是否含有这样的部门信息.")
                    def localParentDept = Department.findBySynchronizationId(dept.interfacePid)
                    if (localParentDept) {
                        newLocalDept.parent = localParentDept
                        newLocalDept.save(flush: true)
                    }
                }
            } else {
                log.info("[HuiGuService.synchroDepartment] 部门更新后对父级数据进行处理.")
                if (localDept.parent) {
                    log.info("[HuiGuService.synchroDepartment] 本地部门数据存在父级数据,但是存在不同的数据.")
                    if (dept.interfacePid != localDept.parent.synchronizationId) {
                        departmentService.updateDept4HuiGu(localDept, null, dept.interfacePid)
                    }
                } else {
                    log.info("[HuiGuService.synchroDepartment] 本地部门数据不存在父级数据.")
                    if (dept.interfacePid) {
                        departmentService.updateDept4HuiGu(localDept, null, dept.interfacePid)
                    }
                }
            }
        }
    }

    /**
     * 同步员工数据（能够获取到数据并且添加和更新目前一切正常）
     */
    void synchroUserInfo() {
        log.info("[HuiGuService.synchroUserInfo] 开始同步用户数据.")
        String synchroUserInfo = WebServiceUtil_HuiGu.newinstance()
                .callws(grailsApplication.config.getProperty('syncData.huigu.url'), "synchroUserInfo", AnalysisXml.makeWsParam())
        if (ObjectUtils.isEmpty(synchroUserInfo)) {
            log.error("[HuiGuService.synchroUserInfo] 用户数据不存在,同步结束.")
            return
        }
        log.info("[HuiGuService.synchroUserInfo] 获取synchroUserInfo成功,数据如下:\n${synchroUserInfo}.")
        List<Users> employeeList = AnalysisXml.resolveUsers(synchroUserInfo)
        if (ObjectUtils.isEmpty(employeeList)) {
            log.error("[HuiGuService.synchroUserInfo] 解析员工数据失败,任务结束.")
            return
        }
        for (Users employee : employeeList) {
            if (employee.username == "admin" || employee.userid.length() == 32) {
                log.warn("[HuiGuService.synchroUserInfo] 数据不正常,不对他执行任何操作,退出本次循环.")
                continue
            }
            Employee localEmployee = Employee.findBySynchronizationId(employee.interfaceId)
            if (ObjectUtils.isEmpty(localEmployee)) {
                log.info("[HuiGuService.synchroUserInfo] 本地不存在此员工:${employee.toString()},现在将会进行新增操作.")
                employeeService.addEmployee4HuiGu(employee.username, employee.deptId, employee.interfaceId)
                continue
            }
            if (!(localEmployee.name == employee.username && Objects.equals(localEmployee.dept, Department.findBySynchronizationId(employee.deptId)))) {
                log.info("[HuiGuService.synchroUserInfo] 更新员工:${employee.userid}.")
                employeeService.updateEmployee4HuiGu(localEmployee, employee.username, employee.deptId)
            }
        }
        log.info("[HuiGuService.synchroUserInfo] 更新员工完成.")
    }

    /**
     * 对接慧谷排期数据
     */
    void synchroPlan() {
        log.info("[HuiGuService.synchroPlan] 开始同步排期数据.")
        String planXmlString = WebServiceUtil_HuiGu.newinstance().callPlan(grailsApplication.config.getProperty('syncData.huigu.url'), "synchroPQInfo", AnalysisXml.makePlanWsParam())
        if (ObjectUtils.isEmpty(planXmlString)) {
            log.error("[HuiGuService.synchroPlan] 没有获取到排期数据,同步结束.")
            return
        }
        log.info("[HuiGuService.synchroPlan] 获取到synchroPQInfo,数据如下:\n${planXmlString}")
        List<Element> pqinfoEs = pqinfoEs(planXmlString)
        if (ObjectUtils.isEmpty(pqinfoEs)) {
            log.info("[HuiGuService.synchroPlan] pqinfoEs转义后为空值,任务结束.")
            return
        }
        //遍历pqinfoEs---->pqinfoE
        for (Element pqinfoE : pqinfoEs) {
            try {
                Element caseXml = pqinfoE.element("AJXX")
                //将xml转换成原始案件Case类型
                Case xmlCase = getXmlCase(caseXml)
                //如果此案件转换后不存在,跳过本条数据
                if (!xmlCase) {
                    log.error("[HuiGuService.synchroPlan] 案件标识/AJBS为空,跳过本条数据.")
                    continue
                }
                //根据案由查询案件类型,如果查询到返回此数据,否则获取数据库中的第一条案件类型
                CaseType casetype = casetype(xmlCase.caseno)
                if (!casetype) {
                    log.error("[HuiGuService.synchroPlan] 本地没有查询到任何案件类型,请至少添加一个案件类型,任务结束.")
                    return
                }
                log.info("[HuiGuService.synchroPlan] 根据案由查找案件类型或者取第一条案件类型为${casetype.name}.")
                // 根据原始数据的同步ID查询本地数据库中的案件
                CaseInfo localCase = CaseInfo.findBySynchronizationId(xmlCase.interfaceId)
                //如果根据这个查不到本地数据
                if (ObjectUtils.isEmpty(localCase)) {
                    //这样再查一遍
                    CaseInfo oldCase = CaseInfo.findBySynchronizationId(caseXml.elementText("NDH") + "-" + caseXml.elementText("BZZH") + "-" + caseXml.elementText("AJBH"))
                    //如果这样查查到了这样一个数据
                    if (!ObjectUtils.isEmpty(oldCase)) {
                        oldCase.setSynchronizationId(caseXml.elementText("AJBS"))
                        log.info("[HuiGuService.synchroPlan] case存在旧数据的interface——id,现在将会直接更新,然后进行下一步判断.")
                        oldCase.setActive(1)
                        oldCase.save(flush: true)
                        if (oldCase.hasErrors()) {
                            log.error("[HuiGuService.synchroPlan] 更新已有案件数据时失败,失败信息:\n${oldCase.errors}.")
                        } else {
                            localCase = oldCase
                        }
                    }
                }
                //本地数据库中没有查询到对应的案件信息,添加这个数据
                if (ObjectUtils.isEmpty(localCase)) {
                    log.info("[HuiGuService.synchroPlan] 本地案件[${xmlCase.toString()}]不存在,现在将会新增case.")
                    def caseInfoNew = caseInfoService.addCaseInfo(xmlCase, casetype)
                    //添加成功
                    if (caseInfoNew) {
                        localCase = caseInfoNew
                        String type = webServiceNormalService.getCategoryByArchives(xmlCase.caseno)
                        log.info("[HuiGuService.synchroPlan] 推测类型为:${type}.")
                        localCase.syncId = type
                        //向CMP推送数据
                        this.notify("pushCmpCaseAndPlanAndTrial", localCase.id, null, null)
                    } else {
                        log.error("[HuiGuService.synchroPlan] 案号为${xmlCase.getCaseno()}的案件添加失败.")
                    }
                } else {
                    //存在判断信息是否已经改变
                    if (localCase.name != xmlCase.casename || localCase.archives != xmlCase.caseno || localCase.filingDate != xmlCase.casedate || localCase.summary != xmlCase.casedesc || localCase.accuser != xmlCase.accuse || localCase.accused != xmlCase.accused || localCase.type != casetype || localCase.department.synchronizationId != xmlCase.deptId || localCase.active != xmlCase.flag) {
                        localCase = caseInfoService.updateCaseInfo(localCase, xmlCase, casetype)
                        if (localCase) {
                            String type = webServiceNormalService.getCategoryByArchives(xmlCase.caseno)
                            log.info("[HuiGuService.synchroPlan] 推测类型为:${type}.")
                            localCase.syncId = type
                            //向CMP推送数据
                            this.notify("pushCmpCaseAndPlanAndTrial", localCase.id, null, null)
                        }
                    }
                }
                //无论添加还是更新,失败后localCase都为null,那么跳过这条数据
                if (!localCase) {
                    continue
                }
                //从pqinfoE(排期信息)中获取ktxxlistE(开庭信息列表)元素
                Element ktxxlistE = pqinfoE.element("KTXXLIST")
                //从ktxxlistE(开庭信息列表)中根据KTXX名称获取ktxxEs(开庭信息)
                List<Element> ktxxEs = ktxxlistE.elements("KTXX")
                //如果ktxxEs不是空
                if (!ktxxEs.isEmpty()) {
                    //获取ktxxEs中最后一个数据
                    Element ktxxE = ktxxEs.get(ktxxEs.size() - 1)
                    if (ObjectUtils.isEmpty(ktxxE.elementText("KTJLID"))) {
                        log.error("[HuiGuService.synchroPlan] 排期KTJLID为空,本条记录直接跳过.")
                        continue
                    }
                    //原始plan数据整理
                    RJRemotePlan plan = plan(caseXml, ktxxE, xmlCase.uid)
                    //从本地数据库中获取localPlan---->根据原始plan的第三方ID查询本地数据库
                    PlanInfo localPlan = PlanInfo.findBySynchronizationId(plan.interfaceplanId)
                    //为了兼容老数据,查询旧版本的interface_id
                    if (ObjectUtils.isEmpty(localPlan)) {
                        PlanInfo oldPlan = PlanInfo.findBySynchronizationId(xmlCase.interfaceId + "-" + ktxxE.elementText("XH"))
                        if (!ObjectUtils.isEmpty(oldPlan)) {
                            //数据库存在旧数据plan
                            oldPlan.synchronizationId = ktxxE.elementText("KTJLID") + "-" + ktxxE.elementText("XH")
                            log.info("[HuiGuService.synchroPlan] plan存在旧数据的interface——id,现在将会直接更新,然后进行下一步判断.")
                            localPlan = oldPlan
                        }
                    }
                    //好多种方式没有查询到本地排期数据
                    if (ObjectUtils.isEmpty(localPlan)) {
                        log.info("[HuiGuService.synchroPlan] 本地plan:${plan.toString()}不存在,现在将会进行新增操作.")
                        localPlan = planService.addPlan4HuiGu(plan, localCase)
                        //向CMP推送数据
                        this.notify("pushCmpCaseAndPlanAndTrial", null, localPlan.id, null)
                    } else {
                        //只允许修改排期状态的排期
                        if (localPlan.status == PlanStatus.PLAN) {
                            //现在判断排期是否和原数据相同,不相同才会更新
                            if (localPlan.judge.uid != plan.judgeId || localPlan.endDate != plan.endDate || localPlan.startDate != plan.startDate || localPlan.caseInfo != localCase || localPlan.secretary.uid != plan.secretaryId || localPlan.courtroom.uid != plan.courtroomId || localPlan.allowPlay != plan.allowplay || localPlan.status != plan.status) {
                                log.info("[HuiGuService.synchroPlan] 现在我们不管他们是不是不一样了,反正同步主键是相同的我们就认为他是同一条数据,不然也没有其他的数据可以进行判断了.")
                                localPlan = planService.updatePlan4HuiGu(localPlan, plan, localCase)
                                //向CMP推送数据
                                this.notify("pushCmpCaseAndPlanAndTrial", null, localPlan.id, null)
                            }
                        } else {
                            log.info("[HuiGuService.synchroPlan] 该排期已经不是排期状态,禁止修改.")
                        }
                    }
                }
            } catch (Exception ex) {
                log.error("[HuiGuService.synchroPlan] 单条排期数据结果转换失败,失败信息:\n${ex.getMessage()}")
            }
        }
    }

    /**
     * 本地数据库查询第一个案件类型
     * @return
     */
    CaseType casetype(String caseno) {
        //在本地获取第一个案件类型对象
        def resultType = null
        def cts = CaseType.findAll()
        for (CaseType ct : cts) {
            if (ct.shortName != null && caseno.indexOf(ct.shortName) > -1) {
                resultType = ct
            }
        }
        if (!resultType) {
            resultType = cts[0]
        }
        return resultType
    }

    /**
     * 数据转义
     */
    def pqinfoEs(String planXmlString) {
        try {
            log.info("[HuiGuService.pqinfoEs] 开始转义result.")
            planXmlString = AnalysisXml.escapeXml(planXmlString)
            log.info("[HuiGuService.pqinfoEs] 使用cxf排期获取数据返回结果result:${planXmlString}.")
            Document pdoc = DocumentHelper.parseText(planXmlString)
            Element root = pdoc.getRootElement()
            if ("1" != root.elementText("RETURNSTATUS")) {
                log.error("[HuiGuService.pqinfoEs] 同步规定时间段排期数据失败:${root.elementText("RETURNMSG")}.")
                return
            }
            Element pqinfolistE = root.element("PQINFOLIST")
            List<Element> pqinfoEs = pqinfolistE.elements("PQINFO")
            log.info("[HuiGuService.pqinfoEs] 本次同步到${pqinfoEs.size()}条数据.")
            return pqinfoEs
        } catch (Exception e) {
            log.error("[HuiGuService.pqinfoEs] 同步规定时间段排期数据结果转换失败:${e.getMessage()}")
        }
    }

    /**
     * 从原始数据中心获取xmlCase
     * @return xmlCase
     */
    def getXmlCase(Element caseXml) {
        Case xmlCase = new Case()
        if (ObjectUtils.isEmpty(caseXml.elementText("AJBS"))) {
            return null
        }
        //uid
        xmlCase.setUid(UUIDGenerator.nextUUID())
        //同步主键
        xmlCase.setInterfaceId(caseXml.elementText("AJBS"))
        //案件编号
        xmlCase.setCaseno(caseXml.elementText("AHQC"))
        //案件名称
        xmlCase.setCasename(caseXml.elementText("DYYG") + "诉" + caseXml.elementText("DYBG") + ":" + caseXml.elementText("LAAYNAME"))
        //将多余信息目前先写进party.
        JSONObject party = new JSONObject()
        party.put("NDH", caseXml.elementText("NDH"))
        party.put("BZZH", caseXml.elementText("BZZH"))
        party.put("AJBH", caseXml.elementText("AJBH"))
        party.put("AJLB", caseXml.elementText("AJLB"))
        party.put("SYCX", caseXml.elementText("SYCX"))
        xmlCase.setParty(party as String)
        //案由
        xmlCase.setCasedesc(caseXml.elementText("LAAYNAME"))
        //原告人
        xmlCase.setAccuse(caseXml.elementText("DYYG"))
        //被告人
        xmlCase.setAccused(caseXml.elementText("DYBG"))
        //立案日期
        try {
            xmlCase.setCasedate(DateFormat.getDateInstance().parse(caseXml.elementText("LARQ")))
        } catch (ParseException e) {
            log.error("转换立案日期出错,错误信息:\n${e.getMessage()}")
        }
        // 部门ID
        xmlCase.setDeptId(caseXml.elementText("CBBM"))
        return xmlCase
    }

    /**
     * 将xml数据转换成Plan对象
     * @param caseXml 总xml
     * @param localCase 本地案件
     * @param ktxxE
     * @return
     */
    def plan(Element caseXml, Element ktxxE, String uuid) {
        //新建原始数据Plan
        RJRemotePlan plan = new RJRemotePlan()
        // 为新建的plan设置UID
        plan.setUid(UUIDGenerator.nextUUID())
        // 为新建的排期设置对应的案件---->直接设置的新添加的或者更新的数据的案件ID
        plan.setCaseId(uuid)
        // 排期编号
        plan.setInterfaceplanId(ktxxE.elementText("KTJLID") + "-" + ktxxE.elementText("XH"))
        //法庭名称
        String courtroomName = ktxxE.elementText("FTMC")
        //下面为排期设置法庭ID
        Courtroom localCourtroom = Courtroom.findByName(courtroomName)
        if (!ObjectUtils.isEmpty(localCourtroom)) {
            plan.setCourtroomId(localCourtroom.uid)
        } else {
            log.warn("[HuiGuService.plan] 警告,本排期未找到对应法庭,现在会匹配数据库随机法庭数据.")
            def cs = Courtroom.findAll()
            if (!cs) {
                log.error("[HuiGuService.plan] 本地没有设置任何法庭,请先设置对应的法庭,任务结束.")
                return
            }
            plan.setCourtroomId(cs.get(0).uid)
        }
        // plan设置开庭时间
        plan.setStartDate(DateFormat.getDateTimeInstance().parse(ktxxE.elementText("KTRQ") + " " + ktxxE.elementText("KSSJ")))
        // plan设置结束时间
        plan.setEndDate(DateFormat.getDateTimeInstance().parse(ktxxE.elementText("KTRQ") + " " + ktxxE.elementText("JSSJ")))
        //plan设置法官
        if (!ObjectUtils.isEmpty(caseXml.elementText("CBRBM"))) {
            Employee localJudge = Employee.findBySynchronizationId(caseXml.elementText("CBRBM"))
            if (!ObjectUtils.isEmpty(localJudge)) {
                plan.setJudgeId(localJudge.uid)
            }
        }
        // plan设置书记员
        if (!ObjectUtils.isEmpty(caseXml.elementText("SJYDM"))) {
            Employee localSecretary = Employee.findBySynchronizationId(caseXml.elementText("SJYDM"))
            if (!ObjectUtils.isEmpty(localSecretary)) {
                plan.setSecretaryId(localSecretary.uid)
            }
        }
        // 为原始数据plan设置状态
        plan.setStatus(0)
        // 为原始数据plan设置是否允许直播
        plan.setAllowplay((byte) 0)
        plan
    }
}