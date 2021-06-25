package com.hxht.autodeploy.court.plan

import cn.hutool.core.date.DateUtil
import com.hxht.techcrt.CollegialType
import com.hxht.techcrt.CopyVideoLog
import com.hxht.techcrt.DataStatus
import com.hxht.techcrt.ModelType
import com.hxht.techcrt.MountDisk
import com.hxht.techcrt.PlanStatus
import com.hxht.techcrt.PositionStatus
import com.hxht.techcrt.Resp
import com.hxht.techcrt.TrialTask
import com.hxht.techcrt.User
import com.hxht.techcrt.WsResp
import com.hxht.techcrt.court.*
import com.hxht.techcrt.court.manager.info.courtroom.CtrlService
import com.hxht.techcrt.enums.RespType
import com.hxht.techcrt.enums.WsRespType
import com.hxht.techcrt.service.sync.huigu.pojo.RJRemotePlan
import com.hxht.techcrt.utils.CfgUtil
import com.hxht.techcrt.utils.UUIDGenerator
import com.hxht.techcrt.utils.comm.StoreCommUtil
import grails.converters.JSON
import grails.core.GrailsApplication
import grails.events.EventPublisher
import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springwebsocket.WebSocket
import grails.web.servlet.mvc.GrailsParameterMap
import groovy.sql.Sql
import org.apache.commons.lang3.StringUtils
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.grails.plugins.excelimport.ExcelImportService
import org.hibernate.sql.JoinType
import org.springframework.web.multipart.MultipartFile

import javax.servlet.http.HttpServletRequest
import java.text.SimpleDateFormat

/**
 * 2021.05.21 >>> 保存案件方法与ApiService中的整合为一个方法、异步通知放置到service(优化3) daniel
 * 2021.05.26 >>> 取消并案开庭,合并休庭闭庭方法,调整代码顺序 daniel
 * 2021.06.16 >>> 互联网开庭统计 daniel
 * 2021.06.17 >>> 排期列表页面查询法庭查询条件排序 daniel
 */
@Transactional
class PlanService implements WebSocket, EventPublisher {

    GrailsApplication grailsApplication
    SpringSecurityService springSecurityService
    PlanService planService
    ChatRecordService chatRecordService
    CtrlService ctrlService
    def dataSource
    ExcelImportService excelImportService

    /**
     * 排期列表查询框数据
     */
    def planListQueryData(GrailsParameterMap params, HttpServletRequest request) {
        def judgeList = Employee.findAllByPosition(PositionStatus.JUDGE)
        def secretaryList = Employee.findAllByPosition(PositionStatus.SECRETARY)
        def courtroomList = Courtroom.findAll([sort: "sequence", order: "asc"])
        def date = params.date("date", "dd/MM/yyyy")
        def date1 = null
        if (date) {
            date1 = new SimpleDateFormat("yyyy/MM/dd").format(date)
        }
        [judgeList: judgeList, secretaryList: secretaryList, courtroomList: courtroomList, date: date1, contentPath: request.contextPath]
    }

    /**
     * 排期列表
     */
    def list(GrailsParameterMap params) {
        //获取表格参数
        def draw = params.int("draw") ?: 1// 记录操作的次数 每次加1
        def start = params.int("start") ?: 0// 起始
        def length = params.int("length") ?: 20// 每页显示的size
        //获取查询参数
        def archives = params.archives as String //案号
        def name = params.name as String //案件名称
        def courtroom = Courtroom.get(params.long("courtroom")) //法庭
        def judge = Employee.get(params.long("judge")) //法官
        def secretary = Employee.get(params.long("secretary")) //书记员
        def status = params.int("status")//庭审状态
        def startDate = params.date("startDate", "yyyy/MM/dd")//计划开庭时间开始范围
        def endDate = params.date("endDate", "yyyy/MM/dd")//计划开庭时间结束范围
        if (endDate) {
            endDate = DateUtil.endOfDay(endDate)//结束时间为后一天的零点->当天最后一秒的时间
        }
        def pageType = params.pageType as String
        def allPlan = params.int("allPlan")
        def modelData = params.int("model")

        List<String> archiveList = new ArrayList<>()
        if (archives) {
            def caseInfoList = CaseInfo.findAllByArchivesLike("%${archives}%")
            for (CaseInfo caseInfo:caseInfoList) {
                def planInfoList = PlanInfo.findAllByCaseInfo(caseInfo)
                for (PlanInfo planInfo:planInfoList) {
                    def planTrial = PlanTrial.findByPlanInfo(planInfo)
                    if (planTrial) {
                        archiveList.add(PlanTrial.findAllByTrialInfo(planTrial.trialInfo)[0].planInfo.caseInfo.archives)
                    }
                }
            }
        }

        //获取时间内所有排期记录
        def model = [:]
        model.put("draw", draw)
        def count = PlanInfo.createCriteria().count() {
            createAlias('caseInfo', 'c', JoinType.LEFT_OUTER_JOIN)
            and {
                eq("active", DataStatus.SHOW)
                //案号
                if (archives) {
                    or {
                        like("c.archives", "%${archives}%")
                        if (archiveList.size() > 0) {
                            for (String ar:archiveList) {
                                eq("c.archives", ar)
                            }
                        }
                    }
                }
                //案件名称
                if (name) {
                    like("c.name", "%${name}%")
                }
                //法庭
                if (courtroom) {
                    eq("courtroom", courtroom)
                }
                //法官
                if (judge) {
                    eq("judge", judge)
                }
                //书记员
                if (secretary) {
                    eq("secretary", secretary)
                }
                //计划开庭时间
                if (startDate) {
                    ge("startDate", startDate)
                }
                //计划闭庭时间
                if (endDate) {
                    le("startDate", endDate)
                }
                //状态
                if (status || status == 0) {
                    eq("status", status)
                }
                if (modelData || modelData == 0) {
                    eq("model", modelData)
                }
            }
            //第二套页面
            if (grailsApplication.config.getProperty("pageVersion") == 'v2') {
                //庭审预告只展示排期数据
                if ("trialNotice" == pageType) {
                    and {
                        eq("status", PlanStatus.PLAN)
                    }
                }
                //庭审直播只展示开庭数据
                if ("trialLive" == pageType) {
                    and {
                        eq("status", PlanStatus.SESSION)
                    }
                }
                //庭审点播不展示开庭和排期数据
                if ("trialVideo" == pageType) {
                    and {
                        ne("status", PlanStatus.PLAN)
                        ne("status", PlanStatus.SESSION)
                    }
                }
            }
        }
        //查询
        Sql dataSql = new Sql(dataSource)
        def sbuffer = new StringBuffer()
        sbuffer.append("SELECT  DISTINCT" + "  plan.id  ")
        sbuffer.append("FROM  " + "  plan_info plan  ")
        sbuffer.append(
                " LEFT OUTER JOIN case_info ca1 ON plan.case_info_id = ca1.id " +
                        " WHERE plan.active =" + DataStatus.SHOW)
        //第二套页面
        if (grailsApplication.config.getProperty("pageVersion") == 'v2') {
            //庭审预告
            if ("trialNotice" == pageType) {
                sbuffer.append(" AND plan.status = " + PlanStatus.PLAN)
            }
            //庭审直播
            if ("trialLive" == pageType) {
                sbuffer.append(" AND plan.status = " + PlanStatus.SESSION)
            }
            //庭审点播
            if ("trialVideo" == pageType) {
                sbuffer.append(" AND plan.status != " + PlanStatus.PLAN + " AND plan.status != " + PlanStatus.SESSION)
            }
        }
        //案号
        if (archives) {
            sbuffer.append(" AND (ca1.archives LIKE '" + "%${archives}%" + "'")
            if (archiveList.size() > 0) {
                for (String ar:archiveList) {
                    sbuffer.append(" OR ca1.archives = '${ar}'" )
                }
            }
            sbuffer.append(")")
        }
        sbuffer.append(" ")
        //委内编号
        if (name) {
            sbuffer.append(" AND ca1.name LIKE '" + "%${name}%" + "'")
        }
        //法庭
        if (courtroom) {
            sbuffer.append(" AND plan.courtroom_id = ${courtroom.id}")
        }
        //法官
        if (judge) {
            sbuffer.append(" AND plan.judge_id = ${judge.id}")
        }
        //书记员
        if (secretary) {
            sbuffer.append(" AND plan.secretary_id = ${secretary.id}")
        }
        //计划开庭时间
        if (startDate) {
            def timePattern = "yyyy-MM-dd HH:mm:ss"
            SimpleDateFormat sdf = new SimpleDateFormat(timePattern)
            String startDateStr = sdf.format(startDate)
            sbuffer.append(" AND plan.start_date >= '" + startDateStr + "'")
        }
        //计划闭庭时间
        if (endDate) {
            def timePattern = "yyyy-MM-dd HH:mm:ss"
            SimpleDateFormat sdf = new SimpleDateFormat(timePattern)
            String endDateStr = sdf.format(endDate)
            sbuffer.append(" AND plan.start_date <= '" + endDateStr + "'")
        }
        //状态
        if (status || status == 0) {
            sbuffer.append(" AND plan.STATUS = " + status)
        }
        if (modelData || modelData == 0) {
            sbuffer.append(" AND plan.model = " + modelData)
        }
        //第二套页面
        if (grailsApplication.config.getProperty("pageVersion") == 'v2') {
            //庭审点播
            if ("trialVideo" == pageType) {
                sbuffer.append(" order by   " +
                        "    case   " +
                        "      when plan.STATUS='" + PlanStatus.ARCHIVED + "' then 1  " +
                        "      when plan.STATUS='" + PlanStatus.CLOSED + "' then 2  " +
                        "      when plan.STATUS='" + PlanStatus.ADJOURN + "' then 3  " +
                        "    else 4  " +
                        "    end,  " +
                        "        plan.start_date DESC   ")
            } else {
                sbuffer.append(" order by   " +
                        "        plan.start_date DESC   ")
            }
        } else {
            sbuffer.append(" order by   " +
                    "    case   " +
                    "      when plan.STATUS='" + PlanStatus.SESSION + "' then 1  " +
                    "      when plan.STATUS='" + PlanStatus.PLAN + "' then 2  " +
                    "      when plan.STATUS='" + PlanStatus.ADJOURN + "' then 3  " +
                    "      when plan.STATUS='" + PlanStatus.CLOSED + "' then 4  " +
                    "    else 5  " +
                    "    end,  " +
                    "        plan.start_date DESC   ")
        }
        //为数据导出Excel时现在条数进行分页
        if (!(allPlan && allPlan == 1)) {
            sbuffer.append("  limit " + start + "," + length)
        }
        //执行查询操作
        def dataList = dataSql.rows(sbuffer.toString())

        def modelDataList = []
        //查询出的数量
        def dataSize = dataList.size()
        if (allPlan && allPlan == 1) {
            convertAndSend("/topic/plan/data_export", WsResp.toJson(WsRespType.MESSAGE, "共导出${dataSize}条数据"))
        }
        for (def n = 0; n < dataSize; n++) {
            //获取Plan
            def planInfo = PlanInfo.get(dataList[n].id)
            def data = [:]
            data.put("id", planInfo.id)
            data.put("caseArchives", planInfo.caseInfo?.archives)
            data.put("caseName", planInfo.caseInfo?.name)
            data.put("courtroom", planInfo.courtroom?.name)
            data.put("judge", planInfo.judge?.name)
            data.put("secretary", planInfo.secretary?.name)
            data.put("startDate", planInfo.startDate?.format('yyyy/MM/dd HH:mm'))
            String statusStr = PlanStatus.getString(planInfo.status)
            //查trial
            def trial = PlanTrial.findByPlanInfo(planInfo)?.trialInfo
            if (trial) {
                def planTrialList = PlanTrial.findAllByTrialInfo(trial)
                if (planTrialList.get(0).planInfo.id == planInfo.id) {
                    statusStr += "(并案)"
                }
            }
            data.put("status", statusStr)
            if (!planInfo.model) {
                planInfo.model = ModelType.LOCALE
                planInfo.save(flush: true)
                if (planInfo.hasErrors()) {
                    log.error("[PlanService.list] 为排期添加开庭模式默认值时失败,错误信息:${planInfo.hasErrors()}")
                    throw new RuntimeException("为排期添加开庭模式默认值时失败")
                }
            }
            data.put("model", ModelType.getString(planInfo.model))
            modelDataList.add(data)
            if (allPlan && allPlan == 1) {
                def dataExport = [:]
                dataExport.put("total", dataSize)
                dataExport.put("i", n + 1)
                convertAndSend("/topic/plan/data_export", WsResp.toJson(WsRespType.PROGRESSBAR, dataExport))
            }
        }

        model.put("recordsTotal", count)//数据总条数
        model.put("recordsFiltered", count)//显示的条数
        model.put("data", modelDataList)
        model
    }

    /**
     * 并案排期
     */
    def combinedPlan(String scheIdsStr) {
        //将参数转换为排期列表
        List<PlanInfo> planInfoList = new ArrayList<>()
        def scheIdsArr = StringUtils.split(scheIdsStr, ",")
        for (String scheId : scheIdsArr) {
            planInfoList.add(PlanInfo.get(scheId as long))
        }
        //选择的都是排期状态并且未并案的,无需检验
        //获取第一个排期
        def firstPlan = planInfoList.get(0)
        //新建庭次信息存储到PlanTrial用于判定主排期
        TrialInfo trialInfo = new TrialInfo(
                uid: UUIDGenerator.nextUUID(),
                startDate: new Date(),
                courtroom: firstPlan.courtroom,
                judge: firstPlan.judge,
                secretary: firstPlan.secretary,
                planInfo: planInfoList.get(0),
                status: PlanStatus.PLAN,
                active: DataStatus.SHOW
        )
        if (trialInfo.hasErrors()) {
            def msg = "[ApiService.combinePlan] 并案排期失败,保存庭次失败."
            log.error(msg + "错误信息:${trialInfo.errors}")
            throw new RuntimeException(msg)
        }
        //处理需要并案的排期
        def combinedPlanUid = UUIDGenerator.nextUUID()
        for (int i = 0; i < planInfoList.size(); i++) {
            PlanInfo it = planInfoList.get(i)

            it.combinedPlan = combinedPlanUid
            it.courtroom = firstPlan.courtroom
            it.judge = firstPlan.judge
            it.secretary = firstPlan.secretary
            it.startDate = trialInfo.startDate
            if (i != 0) {
                it.active = DataStatus.HIDE
            }
            it.save()
            if (it.hasErrors()) {
                def msg = "[PlanService.combinePlan] 并案排期失败,更新排期失败."
                log.error(msg + ",错误信息:${it.errors}")
                throw new RuntimeException(msg)
            }
            def planTrial = new PlanTrial(
                    planInfo: it,
                    trialInfo: trialInfo
            )
            planTrial.save()
        }
        return Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 取消并案排期
     */
    def cancelCombinedPlan(PlanInfo planInfo) {
        def trialInfo = PlanTrial.findByPlanInfo(planInfo).trialInfo
        def planTrialList = PlanTrial.findAllByTrialInfo(trialInfo)
        planTrialList.each {
            def plan = it.planInfo
            plan.combinedPlan = null
            plan.active = DataStatus.SHOW
            plan.save()
            if (plan.hasErrors()) {
                def msg = "[PlanService.cancelCombinedPlan] 取消并案排期失败,更新排期失败."
                log.error(msg + ",错误信息:${plan.errors}")
                throw new RuntimeException(msg)
            }
            it.delete()
            if (it.hasErrors()) {
                def msg = "[PlanService.cancelCombinedPlan] 取消并案排期失败,删除并案信息失败."
                log.error(msg + ",错误信息:${it.errors}")
                throw new RuntimeException(msg)
            }
        }
        trialInfo.delete()
        if (trialInfo.hasErrors()) {
            def msg = "[PlanService.cancelCombinedPlan] 取消并案排期失败,删除庭次信息失败."
            log.error(msg + ",错误信息:${trialInfo.errors}")
            throw new RuntimeException(msg)
        }
        return Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 排期导入
     */
    def importPlanExcel(MultipartFile file) {
        InputStream inputStream = null
        def data = [:]
        try {
            //获取文件,此处可通过上传获取文件
            //定义表头
            Map CONFIG_BOOK_COLUMN_MAP = [
                    sheet    : '排期',
                    startRow : 1,
                    columnMap: [
                            'A': 'ah', //案号(必填)
                            'B': 'ajmc', //案件名称
                            'E': 'ajlx', //案件类型(参考国家案件类型标准)
                            'F': 'ygr', //原告人
                            'G': 'bgr', //被告人
                            'H': 'larq', //立案日期(yyyy/MM/dd HH:mm)
                            'I': 'ft', //法庭
                            'J': 'fg', //法官(系统需存在)
                            'K': 'sjy', //书记员(系统需存在)
                            'L': 'cbr', //承办人(系统需存在)
                            'C': 'jhktsj', //计划开庭时间(必填,yyyy/MM/dd HH:mm)
                            'D': 'jhbtsj', //计划闭庭时间(必填,yyyy/MM/dd HH:mm)
                    ]
            ]
            //将文件放到输入流中
            Workbook workbook = WorkbookFactory.create(file.getInputStream())
            //解析Excel的行存入list
            List list = excelImportService.columns(workbook, CONFIG_BOOK_COLUMN_MAP) as List
            def rows = list.size()
            convertAndSend("/topic/plan/data_import", WsResp.toJson(WsRespType.MESSAGE, "共导入${rows}条数据"))
            list.eachWithIndex { v, i ->
                log.info(v as String)
                i = i + 1
                convertAndSend("/topic/plan/data_import", WsResp.toJson(WsRespType.PROGRESSBAR, [total: rows, i: i]))
                def ah = v.ah
                if (ah != null && ah != "") {
                    ah = ah as String
                } else {
                    ah = null
                }
                def jhktsj = v.jhktsj
                if (jhktsj != null && jhktsj != "") {
                    jhktsj = jhktsj as String
                    jhktsj = jhktsj.split("\"")[1]
                    if (jhktsj.length() == 10)
                        jhktsj += " 00:00"
                } else {
                    jhktsj = null
                }
                def jhbtsj = v.jhbtsj
                if (jhbtsj != null && jhktsj != "") {
                    jhbtsj = jhbtsj as String
                    jhbtsj = jhbtsj.split("\"")[1]
                    if (jhbtsj.length() == 10)
                        jhbtsj += " 00:00"
                } else {
                    jhbtsj = null
                }
                if (jhktsj) {
                    jhktsj = DateUtil.parse(jhktsj, "yyyy/MM/dd HH:mm")
                }
                if (jhbtsj) {
                    jhbtsj = DateUtil.parse(jhbtsj, "yyyy/MM/dd HH:mm")
                }
                if (!(ah && jhktsj && jhbtsj)) {
                    log.error("第${i}条数据格式不正确,数据详情：${v}")
                    throw new RuntimeException("第${i}条数据格式不正确")
                }
                def ajmc = v.ajmc
                def ajlx = v.ajlx
                def ygr = v.ygr
                def bgr = v.bgr
                def larq = v.larq
                if (larq != null && larq != "") {
                    larq = larq as String
                    larq = larq.split("\"")[1]
                    if (larq.length() == 10) {
                        larq += " 00:00"
                    }
                    larq = DateUtil.parse(larq, "yyyy/MM/dd HH:mm")
                    if (larq == null) {
                        log.info("第${i}条数据立案日期不正确")
                    }
                } else {
                    larq = null
                }
                def caseInfo = CaseInfo.findByArchives(ah as String)
                if (!caseInfo) {
                    caseInfo = new CaseInfo()
                    caseInfo.uid = UUIDGenerator.nextUUID()
                    caseInfo.archives = ah as String
                    caseInfo.name = ajmc as String
                    caseInfo.type = (ajlx != null && ajlx != "") ? CaseType.findByName(ajlx.toString().trim()) : null
                    caseInfo.accuser = ygr != null && ygr != "" ? ygr as String : ""
                    caseInfo.accused = bgr != null && bgr != "" ? bgr as String : ""
                    caseInfo.filingDate = larq
                    caseInfo.active = DataStatus.SHOW
                    caseInfo.save()
                    if (caseInfo.hasErrors()) {
                        log.error("第${i}条案件保存失败,错误信息：${caseInfo.errors}")
                        throw new RuntimeException("第${i}条案件保存失败")
                    }
                }
                def planInfo = PlanInfo.findByCaseInfoAndCourtroomAndStartDateAndActive(caseInfo, Courtroom.findByName(v.ft as String), jhktsj, DataStatus.SHOW)
                if (!planInfo) {
                    planInfo = new PlanInfo()
                    planInfo.uid = UUIDGenerator.nextUUID()
                    planInfo.courtroom = Courtroom.findByName(v.ft as String)
                    planInfo.judge = Employee.findByName(v.fg as String)
                    planInfo.secretary = Employee.findByName(v.sjy as String)
                    planInfo.undertake = Employee.findByName(v.cbr as String)
                    planInfo.caseInfo = caseInfo
                    planInfo.startDate = jhktsj
                    planInfo.endDate = jhbtsj
                    planInfo.status = PlanStatus.PLAN
                    planInfo.active = DataStatus.SHOW
                    planInfo.save()
                    if (planInfo.hasErrors()) {
                        log.error("第${i}条排期保存失败,错误信息：${planInfo.errors}")
                        throw new RuntimeException("第${i}条排期保存失败")
                    }
                } else {
                    log.info("排期已存在")
                }
            }
            data.put("status", 0)
            data.put("message", "导入成功")
            return data as JSON
        } finally {
            //关闭输入流
            if (inputStream != null) {
                inputStream.close()
            }
        }
    }

    /**
     * 分页获取案件列表
     */
    def caseInfoList(int draw, int start, int length, String search) {
        def model = [:]
        model.put("draw", draw)
        def count = CaseInfo.createCriteria().count() {
            eq("active", DataStatus.SHOW)
            if (search) {
                or {
                    like("archives", "%${search}%")
                    like("name", "%${search}%")
                    like("accuser", "%${search}%")
                    like("accused", "%${search}%")
                }
            }
        }
        def dataList = CaseInfo.createCriteria().list {
            and {
                setMaxResults(length)
                setFirstResult(start)
                eq("active", DataStatus.SHOW)
            }
            if (search) {
                or {
                    like("archives", "%${search}%")
                    like("name", "%${search}%")
                    like("accuser", "%${search}%")
                    like("accused", "%${search}%")
                }
            }
            order("filingDate", "desc")
        } as List<CaseInfo>
        def modelDataList = []
        for (def caseInfo : dataList) {
            def data = [:]
            data.put("id", caseInfo.id)
            data.put("archives", caseInfo.archives)
            data.put("name", caseInfo.name)
            data.put("accuser", caseInfo.accuser)
            data.put("accused", caseInfo.accused)
            data.put("filingDate", caseInfo.filingDate?.format('yyyy/MM/dd HH:mm'))
            modelDataList.add(data)
        }
        model.put("recordsTotal", count)//数据总条数
        model.put("recordsFiltered", count)//显示的条数
        model.put("data", modelDataList)
        model
    }

    /**
     * 保存排期
     */
    def save(CaseInfo caseInfo, PlanInfo planInfo, def collegialArr) {
        caseInfo.save()
        if (caseInfo.hasErrors()) {
            log.info("保存排期时出错PlanService.save caseInfo [${caseInfo.errors}]")
            throw new RuntimeException()
        }
        def collegialList = []
        if (collegialArr && collegialArr instanceof String) {
            collegialArr = collegialArr.split(",")
        }
        for (def c : collegialArr) {
            def collegial = new Collegial()
            def typeIndex = c.indexOf("(")
            def cname = c.substring(0, (typeIndex > 0) ? typeIndex : c.length())
            collegial.name = cname
            if (c.indexOf(CollegialType.getString(CollegialType.PERSIDING_JUDGE)) > 0) {
                collegial.type = CollegialType.PERSIDING_JUDGE
            } else if (c.indexOf(CollegialType.getString(CollegialType.JUDGE)) > 0) {
                collegial.type = CollegialType.JUDGE
            } else if (c.indexOf(CollegialType.getString(CollegialType.PEOPLE_ASSESSOR)) > 0) {
                collegial.type = CollegialType.PEOPLE_ASSESSOR
            }
            if (!collegial.type) {
                collegial.type = CollegialType.OTHER
            }
            collegial.save()
            if (collegial.hasErrors()) {
                log.info("保存排期时出错PlanService.save collegial [${collegial.errors}]")
                throw new RuntimeException()
            }
            collegialList.add(collegial)
        }
        planInfo.collegial = collegialList
        planInfo.save()
        if (planInfo.hasErrors()) {
            log.info("保存排期时出错PlanService.save planInfo [${planInfo.errors}]")
            throw new RuntimeException()
        }
        //立案排期后抛出排期事件
        this.notify("createPlan", planInfo.id)
        //向CMP平台推送数据
        this.notify("pushCmpCaseAndPlanAndTrial", caseInfo.id, planInfo.id, null)
        planInfo.id
    }

    /**
     * 删除排期
     */
    void del(List<PlanInfo> planInfoList) {
        planInfoList.each {
            it.active = DataStatus.DEL
            it.save()
            if (it.hasErrors()) {
                String msg = "[PlanService.del] 更新排期时出错."
                log.info(msg + "错误信息:\n${it.errors}")
                throw new RuntimeException(msg)
            }
            //向CMP推送数据
            this.notify("pushCmpCaseAndPlanAndTrial", null, it.id, null)
        }
    }

    /**
     * 设置排期开庭模式
     * @param id 排期主键
     * @param model 开庭模式标识
     */
    def setupModel(Long id, Integer model) {
        def planInfo = PlanInfo.get(id)
        planInfo.model = model
        planInfo.save()
        if (planInfo.hasErrors()) {
            log.info("[PlanService.setupModel] 设置开庭模式失败,错误信息:${planInfo.errors}")
            throw new RuntimeException("设置开庭模式失败")
        }
    }

    /**
     * 进入详情页面需要渲染的数据
     */
    def show(GrailsParameterMap params) {
        //排期
        def planInfo = PlanInfo.get(params.long("id"))
        //庭次
        def trialInfo = TrialInfo.get(params.long("trial")) //指定了trial进行播放
        //排期怒存在从庭次中获取
        if (!planInfo) {
            planInfo = trialInfo.planInfo
        }
        //法庭
        def courtroom = planInfo.courtroom
        //法庭配置不存在
        if (!courtroom.cfg) {
            courtroom.cfg = ctrlService.getCfg(courtroom) as JSON
        }
        def cfg = JSON.parse(courtroom.cfg)
        //视频列表
        def videoInfoList = new ArrayList<VideoInfo>()
        //标识是否正在开庭
        //根据排期进入详情页面没有庭次信息
        if (!trialInfo) {
            if (planInfo.status == PlanStatus.SESSION) {
                //正在开庭
                trialInfo = TrialInfo.findByPlanInfoAndStatusAndActive(planInfo, PlanStatus.SESSION, DataStatus.SHOW)
            } else {
                //非正在开庭
                def trialInfoList = TrialInfo.findAllByPlanInfoAndActiveAndStatusNotEqual(planInfo, DataStatus.SHOW,
                        PlanStatus.PLAN, [sort: "startDate", order: "desc"])
                if (trialInfoList) {
                    trialInfo = trialInfoList.get(0)
                    videoInfoList = VideoInfo.findAllByTrialInfo(trialInfo)
                }
            }
        } else {
            //根据trial查询出所有的videoInfo
            videoInfoList = VideoInfo.findAllByTrialInfo(trialInfo)
        }
        //获取所有通道
        def chnList = getAllChannel(cfg)
        //不是正在开庭的获取
        if (planInfo.status != PlanStatus.SESSION) {
            chnList = getChannel(chnList, videoInfoList)
        }
        //数据处理
        def users = springSecurityService.currentUser as User
        def data = [
                planId      : planInfo.id,
                trialId     : trialInfo?.id ?: "",
                caseArchives: planInfo.caseInfo.archives,
                caseName    : planInfo.caseInfo.name,
                caseType    : planInfo.caseInfo.type,
                courtroom   : trialInfo?.courtroom ?: planInfo.courtroom,
                accuser     : planInfo.caseInfo.accuser ?: "无数据",
                accused     : planInfo.caseInfo.accused ?: "无数据",
                filingDate  : planInfo.caseInfo.filingDate?.format('yyyy/MM/dd HH:mm'),
                startDate   : trialInfo?.startDate?.format('yyyy/MM/dd HH:mm') ?: planInfo.startDate?.format('yyyy/MM/dd HH:mm'),
                endDate     : trialInfo?.endDate?.format('yyyy/MM/dd HH:mm') ?: planInfo.endDate?.format('yyyy/MM/dd HH:mm'),
                collegial   : planInfo.collegial.size() == 0 ? "无数据" : planInfo.collegial,
                secretary   : trialInfo?.secretary ?: planInfo.secretary,
                summary     : planInfo.caseInfo.summary ?: "无数据",
                detail      : planInfo.caseInfo.detail ?: "无数据",
                status      : trialInfo?.status ?: planInfo.status,
                allowPlay   : planInfo.allowPlay,
                judge       : planInfo.judge?.name ?: "无数据",
                userId      : users.id
        ]
        //获取排期庭次
        def trialList = planService.getTrialVideoList(planInfo)
        //获取并案列表及
        List<CaseInfo> caseList = new ArrayList<>()
        def planTrialList = PlanTrial.findAllByTrialInfo(PlanTrial.findByPlanInfo(planInfo)?.trialInfo)
        if (planTrialList.size() > 0) {
            for (PlanTrial pt : planTrialList) {
                if (pt.planInfo.caseInfoId == planInfo.caseInfoId) {
                    continue
                }
                caseList.add(pt.planInfo.caseInfo)
            }
        }
        def flag = params.get("flag")
        if(!flag){
            flag = '0'
        }
        def encode = cfg.encode
        def ycEncode = cfg.ycEncode
        def decode = cfg.decode
        def videoMatrix = cfg.videoMatrix
        def vgaMatrix = cfg.vgaMatrix
        def outMatrix = cfg.outMatrix
        def soundMatrix = cfg.soundMatrix
        def total = cfg.total
        def power = cfg.power
        def irctrl = cfg.irctrl
        def camera = cfg.camera
        def powerNew = cfg.powerNew
        LinkedHashSet<Object> soundGroup = new LinkedHashSet<>()//音量控制组
        for (def sound : soundMatrix){
            soundGroup.add(sound?.group?.split("_")[0])
        }
        [data: data, userName: users.username, chatRecord: chatRecordService.getChatRecord(planInfo.id).chatRecord, 
         chnList: chnList, trialList: trialList, caseList: caseList,courtroom: courtroom, encode: encode, ycEncode: ycEncode, 
         decode: decode, videoMatrix: videoMatrix, vgaMatrix: vgaMatrix, outMatrix: outMatrix, soundMatrix: soundMatrix,
         total: total, power: power, powerNew: powerNew, irctrl: irctrl, camera: camera,
         flag: flag, soundGroup: soundGroup]
    }

    /**
     * 根据排期获取庭审与视频信息
     */
    def getTrialVideoList(PlanInfo planInfo) {
        List<TrialInfo> trialInfoList = TrialInfo.findAllByPlanInfoAndActiveAndStatusNotEqual(planInfo, DataStatus.SHOW,
                PlanStatus.PLAN, [sort: "startDate", order: "desc"])
        def trialList = []
        for (def ti : trialInfoList) {
            //获取庭审所有视频
            def videoInfoList = ti.videoInfo
            def videoList = []
            for (def videoInfo : videoInfoList) {
                videoList.add([
                        name: videoInfo.channelName,
                ])
            }
            trialList.add([
                    id       : ti.id,
                    startDate: ti.startDate?.format('yyyy/MM/dd HH:mm'),
                    endDate  : ti.endDate?.format('yyyy/MM/dd HH:mm'),
                    collegial: planInfo.collegial,
                    status   : ti.status,
                    videoList: videoList
            ])
        }
        trialList
    }

    /**
     * 根据庭审获取页面信息
     */
    def showVideo(TrialInfo trialInfo) {
        //排期
        def planInfo = trialInfo.planInfo
        //法庭
        def courtroom = planInfo.courtroom
        //配置
        def cfg = JSON.parse(courtroom.cfg)
        //视频列表
        def videoList = []
        if (trialInfo.status == PlanStatus.SESSION) {
            //开庭
            log.info("[PlanService.showVideo]---->庭审正在开庭.")
            for (def encode : cfg.encode) {
                def url = "http://${courtroom.liveIp}:8791/${encode.encodeip}/${encode.number}.flv"
                videoList.add([
                        number: encode.number,
                        name  : encode.name,
                        url   : url
                ])
            }
            log.info("[PlanService.showVideo]---->返回直播地址:${videoList}")
        } else {
            log.info("[PlanService.showVideo]---->庭审已休庭或闭庭.")
            //获取所有通道
            def allChannel = this.getAllChannel(cfg)
            //获取视频通道
            def videoChannelList = getChannel(allChannel, VideoInfo.findAllByTrialInfo(trialInfo))
            //循环视频通道获取视频
            for (def videoChannel : videoChannelList) {
                def videoMap = [:]
                videoMap.put("number", videoChannel.number)
                videoMap.put("name", videoChannel.name)
                def videoInfoList = VideoInfo.findAllByTrialInfoAndChannelNumAndChannelName(trialInfo, videoChannel.number as String, videoChannel.name as String, [sort: "startRecTime", order: "asc"])
                def trialVideoList = []
                def totalLength = 0
                for (def i = 0; i < videoInfoList.size(); i++) {
                    def videoInfo = videoInfoList[i]
                    trialVideoList.add([
                            serial      : i,
                            length      : videoInfo.length,
                            startRecTime: videoInfo.startRecTime?.format("HH:mm"),
                            imagesUrl   : grailsApplication.config.getProperty('tc.trial.images.path'),
                            url         : "http://${courtroom.storeIp}:8200/${videoInfo.fileName}"
                    ])
                    totalLength += videoInfo.length
                    //通知事件进行截图
                    this.notify("screenShot", videoInfo.id)
                }
                videoMap.put("videoUrl", trialVideoList)
                videoMap.put("totalLength", totalLength)
                videoList.add(videoMap)
            }
        }
        videoList
    }

    /**
     * 保存控制台的排期状态开庭
     */
    def planOpen(PlanInfo planInfo, TrialInfo trialInfo) {
        if (!trialInfo || trialInfo.status == PlanStatus.CLOSED) {
            //庭审不存在或者庭审为闭庭时创建新庭审
            trialInfo = new TrialInfo(
                    uid: UUIDGenerator.nextUUID(),
                    planInfo: planInfo,
                    startDate: new Date(),
                    courtroom: planInfo.courtroom,
                    judge: planInfo.judge,
                    secretary: planInfo.secretary,
                    active: DataStatus.SHOW
            )
        }
        trialInfo.status = PlanStatus.SESSION
        trialInfo.save()
        if (trialInfo.hasErrors()) {
            def msg = "[planService.planOpen] 庭审控制开庭时保存庭次出错."
            log.error(msg + "错误信息:\n${trialInfo.errors}")
            throw new RuntimeException(msg)
        }
        planInfo.status = PlanStatus.SESSION
        planInfo.startDate = trialInfo.startDate
        planInfo.save()
        if (planInfo.hasErrors()) {
            def msg = "[PlanService.planOpen] 庭审控制开庭时保存排期出错."
            log.info(msg + "错误信息:\n[${planInfo.errors}]")
            throw new RuntimeException(msg)
        }
        //排期开庭后抛出排期事件
        this.notify("startTrial", trialInfo.id)
        //开庭成功 通知存储进行视频录制
        def chnList = CfgUtil.getEncodeToStore(planInfo.courtroom)
        def status = StoreCommUtil.start(planInfo.id, trialInfo.id, planInfo.courtroom.storeIp, chnList).status
        //与存储通信 开庭 开始录像
        if (status != "ok") {
            def msg = "[planService.planOpen] 庭审控制开庭时保存开启存储出错."
            log.error(msg + "storeIp: [${planInfo.courtroom.storeIp}] chnList: [${chnList}] status: [${status}]")
            throw new RuntimeException(msg)
        }
        trialInfo
    }

    /**
     * 保存控制台的排期状态休庭、闭庭
     */
    def planClose(TrialInfo trialInfo, Integer planStatus,String taskId) {
        trialInfo.endDate = new Date()
        trialInfo.status = planStatus
        trialInfo.save()
        if (trialInfo.hasErrors()) {
            def msg = "[PlanService.planClose] 庭审控制休闭庭时保存庭次失败."
            log.error(msg + "错误信息:\n${trialInfo.errors}")
            throw new RuntimeException(msg)
        }
        PlanInfo planInfo = trialInfo.planInfo
        planInfo.status = planStatus
        planInfo.active = DataStatus.SHOW
        planInfo.endDate = trialInfo.endDate
        planInfo.save()
        if (planInfo.hasErrors()) {
            def msg = "[PlanService.planClose] 庭审控制休闭庭时保存排期失败."
            log.error(msg + "错误信息:\n${planInfo.errors}")
            throw new RuntimeException(msg)
        }
        if (taskId){
            //对应庭审主机和书记员客户端处的命名方式
            def trialTask = new TrialTask(
                taskId: taskId,
                trialId: trialInfo.id
            ).save()
            if (trialTask.hasErrors()) {
                def msg = "[PlanService.planClose] 保存TrialTask失败."
                log.error(msg + "错误信息:\n${trialTask.errors}")
                throw new RuntimeException(msg)
            }
        }
        //休庭成功 通知存储停止录制
        def status = StoreCommUtil.stop(trialInfo.id, trialInfo.courtroom.storeIp).status
        if (status != "ok") {
            def msg = "[PlanService.planClose] 庭审控制休闭庭时停止存储失败."
            log.error(msg + "storeIp: [${trialInfo.courtroom.storeIp}] status: [${status}]")
            throw new RuntimeException(msg)
        }
        trialInfo
    }

    /**
     * 根据点播视频列表获取视频通道
     */
    def getChannel(def chnList, def videoInfoList) {
        def videoChnList = []
        for (def chn : chnList) {
            List<VideoInfo> deletedVideo = new ArrayList<>()
            for (def video : videoInfoList) {
                if (video.channelNum == chn.number && video.channelName == chn.name) {
                    videoChnList.add([
                            number: video.channelNum,
                            name  : video.channelName
                    ])
                    deletedVideo.add(video)
                }
            }
            for (VideoInfo videoInfo : deletedVideo) {
                videoInfoList.remove(videoInfo)
            }
            deletedVideo.clear()
        }
        for (def video : videoInfoList) {
            videoChnList.add([
                    number: video.channelNum,
                    name  : video.channelName
            ])
        }
        //s使用hashset去掉重复
        LinkedHashSet<Object> videoList = new LinkedHashSet<>(videoChnList)
        return videoList
    }

    /**
     * 获取所有通道
     */
    def getAllChannel(def cfg) {
        def chnList = []
        //直播通道
        for (def encode : cfg.encode) {
            chnList.add([
                    number: encode.number,
                    name  : encode.name
            ])
        }
        return chnList
    }

    /**
     * 根据案号删除所有关联数据--管理员工具用
     */
    def deleteVideo(def archives) {
        String msg = "案号:${archives},"
        try {
            if (!archives) {
                msg += "不能为空."
                log.info("[PlanService.deleteVideo] ${msg}")
                return msg
            }
            def caseInfo = CaseInfo.findByArchives(archives)
            if (!caseInfo) {
                msg += "案号对应案件不存在."
                log.info("[PlanService.deleteVideo] ${msg}")
                return msg
            }
            
            PlanInfo planCombine
            def planInfoList = PlanInfo.findAllByCaseInfo(caseInfo)
            for (PlanInfo planInfo : planInfoList) {
                //如果有并案情况
                if (planInfo.combinedPlan){
                    planCombine = planInfo
                    break
                }
            }
            //是并案的案件
            if (planCombine){
                def trialInfo = PlanTrial.findByPlanInfo(planCombine).trialInfo
                def planTrialList = PlanTrial.findAllByTrialInfo(trialInfo)
                //删除视频记录
                def mountDiskList = MountDisk.findAll()
                for (def mountDist: mountDiskList){
                    def file = new File(mountDist.urlMount+"/" + trialInfo.planInfo.id)
                    if (file.exists()){
                        log.info("[PlanService.deleteVideo] 文件夹${file.getPath()}存在,准备删除.")
                        boolean isDelete = file.deleteDir()
                        log.info("[PlanService.deleteVideo] 文件夹${file.getPath()}删除状态：${isDelete}.")
                    }
                }
                String fileName = "/usr/local/movies/" + trialInfo.planInfo.id
                def file = new File(fileName)
                if (file.exists()) {
                    log.info("[PlanService.deleteVideo] 文件夹${fileName}存在,准备删除.")
                    boolean isDelete = file.deleteDir()
                    log.info("[PlanService.deleteVideo] 文件夹${fileName}删除状态：${isDelete}.")
                }
                
                def videoInfoList = VideoInfo.findAllByTrialInfo(trialInfo)
                for (VideoInfo videoInfo : videoInfoList) {
                    def cvl = CopyVideoLog.findByVideoInfo(videoInfo)
                    if (cvl) {
                        cvl.delete()
                    }
                    videoInfo.delete()
                }
                
                for (def planTrial: planTrialList){
                    planTrial.delete()
                }
                trialInfo.delete()
                for (def planTrial: planTrialList){
                    planTrial.planInfo.delete()
                }
                caseInfo.delete()
                
                log.info("并案删除成功！")
                msg += "并案删除成功！"
            }else{
                msg += this.deletePlan(planInfoList,caseInfo)
            }
        } catch (e) {
            e.printStackTrace()
            msg += "服务端出现异常"
        }
        return msg
    }
    
    def deletePlan(def planInfoList, def caseInfo){
        def msg
        try{
            for (PlanInfo planInfo : planInfoList) {
                def mountDiskList = MountDisk.findAll()
                for (def mountDist: mountDiskList){
                    def file = new File(mountDist.urlMount+"/"+planInfo.id)
                    if (file.exists()){
                        log.info("[PlanService.deletePlan] 文件夹${file.getAbsolutePath()}存在,准备删除.")
                        boolean isDelete = file.deleteDir()
                        log.info("[PlanService.deletePlan] 文件夹${file.getAbsolutePath()}删除状态：${isDelete}.")
                    }
                }
                String fileName = "/usr/local/movies/" + planInfo.id
                def file = new File(fileName)
                if (file.exists()) {
                    log.info("[PlanService.deletePlan] 文件夹${fileName}存在,准备删除.")
                    boolean isDelete = file.deleteDir()
                    log.info("[PlanService.deletePlan] 文件夹${fileName}删除状态：${isDelete}.")
                }
                def trialInfoList = TrialInfo.findAllByPlanInfo(planInfo)
                for (TrialInfo trialInfo : trialInfoList) {
                    def videoInfoList = VideoInfo.findAllByTrialInfo(trialInfo)
                    for (VideoInfo videoInfo : videoInfoList) {
                        def cvl = CopyVideoLog.findByVideoInfo(videoInfo)
                        if (cvl) {
                            cvl.delete()
                        }
                        videoInfo.delete()
                    }
                    trialInfo.delete()
                }
                planInfo.delete()
            }
            caseInfo.delete()
            msg = "删除成功."
            log.info("[PlanService.deletePlan] ${msg}")
        } catch (e) {
            e.printStackTrace()
            msg = "服务端出现异常"
        }
        return msg
    }

    def addPlan4HuiGu(RJRemotePlan plan, CaseInfo caseInfo) {
        PlanInfo planInfoNew = new PlanInfo(
                uid: plan.uid,
                judge: Employee.findByUid(plan.judgeId),
                endDate: plan.endDate,
                startDate: plan.startDate,
                caseInfo: caseInfo,
                secretary: Employee.findByUid(plan.secretaryId),
                courtroom: Courtroom.findByUid(plan.courtroomId),
                allowPlay: plan.allowplay,
                synchronizationId: plan.interfaceplanId,
                status: plan.status,
                active: 1
        )
        planInfoNew.save()
        if (planInfoNew.hasErrors()) {
            log.error("对接慧谷数据,添加排期失败,错误信息如下：\n{}", planInfoNew.errors)
            null
        }
        planInfoNew
    }

    def updatePlan4HuiGu(PlanInfo localPlan, RJRemotePlan plan, CaseInfo caseInfo) {
        if (localPlan.status != PlanStatus.PLAN) {
            log.info("此排期已经不是排期状态,禁止修改")
            return
        }
        localPlan.judge = Employee.findByUid(plan.judgeId)
        localPlan.endDate = plan.endDate
        localPlan.startDate = plan.startDate
        localPlan.caseInfo = caseInfo
        localPlan.secretary = Employee.findByUid(plan.secretaryId)
        localPlan.courtroom = Courtroom.findByUid(plan.courtroomId)
        localPlan.allowPlay = plan.allowplay
        localPlan.status = plan.status
        localPlan.active = 1
        localPlan.save()
        if (localPlan.hasErrors()) {
            log.error("对接慧谷数据,更新排期失败,失败信息:{}", localPlan.errors)
            null
        }
        localPlan
    }

    /**
     * 分页获取法庭排期列表
     */
//    def courtroomlist(int draw, int start, int length, String search, long planinfoid) {
//        def courtroom = Courtroom.get(planinfoid)
//        def model = [:]
//        model.put("draw", draw)
//        def count = PlanInfo.createCriteria().count() {
//            and {
//                eq("active", DataStatus.SHOW)
//                eq("courtroom", courtroom)
//            }
//            or {
//                eq("status", 0)
//                eq("status", 1)
//            }
//
//            if (search) {
//                createAlias('caseInfo', 'c', JoinType.LEFT_OUTER_JOIN)
//                createAlias('courtroom', 'cr', JoinType.LEFT_OUTER_JOIN)
//                createAlias('judge', 'j', JoinType.LEFT_OUTER_JOIN)
//                createAlias('secretary', 's', JoinType.LEFT_OUTER_JOIN)
//                or {
//                    like("c.archives", "%${search}%")
//                    like("c.name", "%${search}%")
//                    like("cr.name", "%${search}%")
//                    like("j.name", "%${search}%")
//                    like("s.name", "%${search}%")
//                    if (PlanStatus.getCode(search) != null) {
//                        eq("status", PlanStatus.getCode(search))
//                    }
//                }
//            }
//        }
//        def dataList = PlanInfo.createCriteria().list {
//            and {
//                setMaxResults(length)
//                setFirstResult(start)
//                eq("active", DataStatus.SHOW)
//                eq("courtroom", courtroom)
//            }
//            or {
//                eq("status", 0)
//                eq("status", 1)
//            }
//            if (search) {
//                createAlias('caseInfo', 'c', JoinType.LEFT_OUTER_JOIN)
//                createAlias('courtroom', 'cr', JoinType.LEFT_OUTER_JOIN)
//                createAlias('judge', 'j', JoinType.LEFT_OUTER_JOIN)
//                createAlias('secretary', 's', JoinType.LEFT_OUTER_JOIN)
//                or {
//                    like("c.archives", "%${search}%")
//                    like("c.name", "%${search}%")
//                    like("cr.name", "%${search}%")
//                    like("j.name", "%${search}%")
//                    like("s.name", "%${search}%")
//                    if (PlanStatus.getCode(search) != null) {
//                        eq("status", PlanStatus.getCode(search))
//                    }
//                }
//
//            }
//            order("startDate", "desc")
//            order("status", "asc")
//        } as List<PlanInfo>
//        def modelDataList = []
//        for (def planInfo : dataList) {
//            def data = [:]
//            data.put("id", planInfo.id)
//            data.put("caseArchives", planInfo.caseInfo?.archives)
//            data.put("caseName", planInfo.caseInfo?.name)
//            data.put("courtroom", planInfo.courtroom?.name)
//            data.put("judge", planInfo.judge?.name)
//            data.put("secretary", planInfo.secretary?.name)
//            data.put("startDate", planInfo.startDate?.format('yyyy/MM/dd HH:mm'))
//            data.put("status", PlanStatus.getString(planInfo.status))
//            modelDataList.add(data)
//        }
//        model.put("recordsTotal", count)//数据总条数
//        model.put("recordsFiltered", count)//显示的条数
//        model.put("data", modelDataList)
//        model
//    }
}
