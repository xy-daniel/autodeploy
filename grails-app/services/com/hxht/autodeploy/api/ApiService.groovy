package com.hxht.autodeploy.api

import cn.hutool.core.date.DateUtil
import com.hxht.techcrt.CollegialType
import com.hxht.techcrt.CourtroomStatus
import com.hxht.techcrt.DataStatus
import com.hxht.techcrt.DistanceCourt
import com.hxht.techcrt.PlanStatus
import com.hxht.techcrt.Resp
import com.hxht.techcrt.User
import com.hxht.techcrt.court.*
import com.hxht.techcrt.court.plan.PlanService
import com.hxht.techcrt.enums.RespType
import com.hxht.techcrt.utils.CfgUtil
import com.hxht.techcrt.utils.JwtUtil
import com.hxht.techcrt.utils.CtrlCommandUtil
import com.hxht.techcrt.utils.UUIDGenerator
import com.hxht.techcrt.utils.comm.StoreCommUtil
import com.hxht.techcrt.utils.http.RemoteHttpUtil
import grails.converters.JSON
import grails.core.GrailsApplication
import grails.events.EventPublisher
import grails.gorm.transactions.Transactional
import grails.plugin.springwebsocket.WebSocket
import grails.web.mapping.LinkGenerator
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import org.hibernate.sql.JoinType
import org.springframework.web.multipart.MultipartFile

/**
 * 书记员客户端接口调用服务
 * 2021.03.22 >>> 闭庭时自动断开远程连接 daniel
 * 2021.04.19 >>> 修改远程提讯策略 daniel
 * 2021.04.24 >>> 远程法院列表根据分级码排序 daniel
 * 2021.05.12 >>> 批注websocket添加name属性 daniel
 * 2021.05.13 >>> 修改闭庭断开远程连接第二个连接无法断开的问题 daniel
 * 2021.05.21 >>> 修改方法名称、删除未使用方法、保存案件方法与PlanService中的整合为一个方法(优化3) daniel
 * 2021.05.26 >>> 取消并案开庭,休闭归使用plan方法 daniel
 * 2021.06.10 >>> 闭庭切换合成画面修改为断开远程时操作 daniel
 */
@Transactional()
class ApiService implements WebSocket, EventPublisher {
    GrailsApplication grailsApplication
    LinkGenerator grailsLinkGenerator
    PlanService planService

    /**
     * 根据用户名密码获取用户信息
     * @param username
     * @param password
     * @return user
     */
    def getUserByUsernameAndPassword(String username, String password) {
        User.findByUsernameAndPasswordAndEnabledAndaccountLockedAndAccountExpired(username, password, true, false, false)
    }

    /**
     * 获取当前登录用户
     * @return user
     */
    def currentUser() {
        def webUtils = WebUtils.retrieveGrailsWebRequest()
        def request = webUtils.getCurrentRequest()
        def auth = request.getHeader("Authorization")
        if (auth != null) {
            if (auth.startsWith("Bearer ")) {
                auth = auth.replace("Bearer ", "")
            }
            def secret = grailsApplication.config.getProperty('jwt.info.secret')
            def claims = JwtUtil.parse(auth, secret)
            return User.get(claims.get("id") as long)
        }
        return null
    }

    /**
     * 将排期开庭
     * @param planInfo
     * @param courtroom
     * @param judge
     * @param secretary
     * @return 庭审对象
     */
    def planOpen(PlanInfo planInfo, Courtroom courtroom, Employee judge, Employee secretary, String trialId) {
        TrialInfo trialInfo
        if (trialId) { //如果书记员客户端穿了trialId那么则是复庭
            trialInfo = TrialInfo.get(trialId as long)
            if (!trialInfo) {
                log.info("书记员传值休庭后开庭的庭审id在本地数据库未找到，庭审id为：${trialId}")
                return
            }
        }
        //plan状态修改成功，设置trial状态
        if (trialInfo) {//如果trial存在则为复庭。
            //判断复庭时间间隔
            def sixTime = DateUtil.offsetHour(DateUtil.beginOfDay(new Date()), 6)//获取六点的时间点
            def sixTimeNextDay = DateUtil.offsetDay(sixTime, 1) //获取第二天的六点
            if (sixTime.getTime() <= trialInfo.startDate.getTime() && trialInfo.startDate.getTime() <= sixTimeNextDay.getTime()) {
                //复庭时间在当天，可以复庭
                if (trialInfo.status != PlanStatus.SESSION) {//trial状态不为开庭状态将状态改为开庭
                    trialInfo.status = PlanStatus.SESSION
                    trialInfo.save()
                    if (trialInfo.hasErrors()) {
                        def msg = "[ApiService planOpen]复庭失败,保存trial 失败 errors [${trialInfo.errors}]"
                        log.error(msg)
                        throw new RuntimeException(msg)
                    }
                    //排期复庭后抛出排期事件
                    this.notify("resumeTrial", trialInfo.id)
                }
            }
        }
        if (!trialInfo || trialInfo.status != PlanStatus.SESSION) {//如果trial不存在，或者状态不为开庭，那么新建trial，为新开庭审
            trialInfo = new TrialInfo()
            trialInfo.uid = UUIDGenerator.nextUUID()
            trialInfo.planInfo = planInfo
            trialInfo.startDate = new Date()
            trialInfo.courtroom = courtroom
            trialInfo.judge = judge
            trialInfo.secretary = secretary
            trialInfo.status = PlanStatus.SESSION
            trialInfo.active = DataStatus.SHOW
            trialInfo.save()
            if (trialInfo.hasErrors()) {
                def msg = "[ApiService planOpen]开庭失败,保存trial 失败 errors [${trialInfo.errors}]"
                log.error(msg)
                throw new RuntimeException(msg)
            }
            //排期开庭后抛出排期事件
            this.notify("startTrial", trialInfo.id)
        }
        //执行开庭操作，将plan状态设置为开庭，法庭等其他参数设置为实际开庭数据。
        planInfo.status = PlanStatus.SESSION
        planInfo.courtroom = courtroom
        planInfo.judge = judge
        planInfo.secretary = secretary
        planInfo.startDate = trialInfo.startDate
        planInfo.save()
        if (planInfo.hasErrors()) {
            def msg = "[ApiService planOpen]开庭失败,保存plan 失败 errors [${planInfo.errors}]"
            log.error(msg)
            throw new RuntimeException(msg)
        }
        log.info("开庭通知存储之前时间：" + new Date().format('yyyy/MM/dd HH:mm:ss'))
        //开庭成功 通知存储进行视频录制
        def chnList = CfgUtil.getEncodeToStore(courtroom)
        def status = StoreCommUtil.start(planInfo.id, trialInfo.id, courtroom.storeIp, chnList).status//与存储通信 开庭 开始录像
        if (status != "ok") {
            def msg = "[ApiService planOpen]开庭失败,存储 开始录像失败 storeIp: [${courtroom.storeIp}] chnList: [${chnList}] status: [${status}]"
            log.error(msg)
            throw new RuntimeException(msg)
        }
        log.info("开庭通知存储成功后时间：" + new Date().format('yyyy/MM/dd HH:mm:ss'))
        //向CMP系统推送开庭trial和plan信息
        this.notify("pushCmpCaseAndPlanAndTrial", null, planInfo.id, trialInfo.id)
        trialInfo
    }

    /**
     * 并案排期
     */
    def combinePlan(GrailsParameterMap params) {
        //排期
        def planId = params.get("id") as String
        if (!planId) {
            def msg = "[ApiService.combinePlan] 并案排期失败，未传值任何排期，排期id为空。"
            log.error(msg)
            return Resp.toJson(RespType.FAIL, msg)
        }
        //法庭
        def courtroom = Courtroom.get(params.long("courtroom"))
        if (!courtroom) {
            def msg = "[ApiService.combinePlan] 并案排期失败，未获取到开庭法庭。"
            log.error(msg)
            return Resp.toJson(RespType.FAIL, msg)
        }
        def planIdList = planId.split(",")
        if (planIdList.size() <= 1) {
            def msg = "[ApiService.combinePlan] 排期为一个无需并案！"
            return Resp.toJson(RespType.FAIL, msg)
        }

        PlanInfo planInfoDto = PlanInfo.get(planIdList[0] as long)
        //新建庭审信息
        TrialInfo trialInfo = new TrialInfo(
                uid: UUIDGenerator.nextUUID(),
                startDate: new Date(),
                courtroom: courtroom,
                judge: planInfoDto.judge,
                secretary: planInfoDto.secretary,
                planInfo: planInfoDto,
                status: PlanStatus.PLAN,
                active: DataStatus.SHOW
        )
        trialInfo.save()
        if (trialInfo.hasErrors()) {
            def msg = "[ApiService.combinePlan] 并案排期新建庭次失败."
            log.error(msg + "错误信息:\n${trialInfo.errors}")
            throw new RuntimeException(msg)
        }
        //排期信息
        def combinedPlanUid = UUIDGenerator.nextUUID()
        for (def i = 0; i < planIdList.size(); i++) {
            PlanInfo planInfo = PlanInfo.get(planIdList[i] as long)
            if (planInfo.combinedPlan) {
                def msg = "[ApiService.combinePlan] 并案排期此排期已经并案，不能重复并案！"
                log.error(msg)
                return Resp.toJson(RespType.FAIL, msg)
            }
            planInfo.combinedPlan = combinedPlanUid
            planInfo.courtroom = courtroom
            planInfo.judge = planInfoDto.judge
            planInfo.secretary = planInfoDto.secretary
            planInfo.startDate = trialInfo.startDate
            if (i != 0) {
                planInfo.active = DataStatus.HIDE
            }
            planInfo.save()
            if (planInfo.hasErrors()) {
                def msg = "[ApiService.combinePlan] 并案排期更新排期失败."
                log.error(msg + "错误信息:${planInfo.errors}")
                throw new RuntimeException(msg)
            }
            def planTrial = new PlanTrial(
                    planInfo: planInfo,
                    trialInfo: trialInfo
            )
            planTrial.save()
        }
        return Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 取消并案排期
     */
    def cancelCombinePlan(GrailsParameterMap params) {
        //排期
        def planId = params.get("id") as long
        if (!planId) {
            def msg = "[ApiService.cancelCombinePlan] 取消并案排期失败，未传值任何排期，排期id为空。"
            log.error(msg)
            return Resp.toJson(RespType.FAIL, msg)
        }
        def planInfo = PlanInfo.get(planId)
        if (planInfo.status != PlanStatus.PLAN) {//如果开过庭则不能取消
            def msg = "[ApiService.cancelCombinePlan] 此排期已经开过庭不能取消，并返回，庭审状态为${planInfo.status}。"
            log.error(msg)
            return Resp.toJson(RespType.FAIL, msg)

        }
        if (!planInfo.combinedPlan) {
            def msg = "[ApiService.cancelCombinePlan] 未获取到combinedPlan，并返回，庭审状态为${planInfo.status}。"
            log.error(msg)
            return Resp.toJson(RespType.FAIL, msg)
        }
        def planList = PlanInfo.findAllByCombinedPlan(planInfo.combinedPlan)
        if (planList.size() < 2) {
            def msg = "[ApiService.cancelCombinePlan] 此排期未并案不能取消，并返回。"
            log.error(msg)
            return Resp.toJson(RespType.FAIL, msg)
        }
        //将所有排期取消并案
        for (def plan : planList) {
            plan.combinedPlan = null
            plan.active = DataStatus.SHOW
            plan.save()
            if (plan.hasErrors()) {
                log.info("保存排期时出错ApiService.cancelCombinePlan [${plan.errors}]")
                throw new RuntimeException()
            }
            def planTrial = PlanTrial.findByPlanInfo(plan)
            //删除planTrial排期庭审对应关系
            planTrial.delete()
        }
        //并删除创建的trial
        def trialInfo = TrialInfo.findByPlanInfo(planInfo)
        if (trialInfo) {
            trialInfo.delete()
            log.info("ApiService.cancelCombinePlan 删除trialInfo信息成功${trialInfo.id}")
        } else {
            log.error("ApiService.cancelCombinePlan 未找到对应的trialInfo 删除失败！")
        }
        Resp.toJson(RespType.SUCCESS)
    }

    def savePlan(PlanInfo planInfo) {
        planInfo.save()
        if (planInfo.hasErrors()) {
            log.info("保存排期时出错ApiService.savePlan [${planInfo.errors}]")
            throw new RuntimeException()
        }
    }

    def saveTrial(TrialInfo trialInfo) {
        trialInfo.save()
        if (trialInfo.hasErrors()) {
            def msg = "[ApiService saveTrial]保存trial 失败 errors [${trialInfo.errors}]"
            log.error(msg)
            throw new RuntimeException(msg)
        }
    }

    /**
     * @return 排期信息
     */
    def plan(GrailsParameterMap params) {
        def id = params.long("id")
        def startDate = params.date("startDate", "yyyy/MM/dd")//计划开庭时间开始范围
        def endDate = params.date("endDate", "yyyy/MM/dd")//计划开庭时间结束范围
        endDate = DateUtil.offsetDay(endDate, 1)
        def filingDate = params.date("filingDate", "yyyy/MM/dd") //立案日期
        def archives = params.archives as String //案号
        def name = params.name as String //案件名称
        def type = params.type as String //案件类型
        def accuser = params.accuser as String //原告
        def accused = params.accused as String //被告
        def courtroom = Courtroom.get(params.long("courtroom")) //法庭
        def judge = Employee.get(params.long("judge")) //法官
        def secretary = Employee.get(params.long("secretary")) //书记员
        def status = params.int("status")//庭审状态

        def page = params.int("page") ?: 0// 起始
        def length = params.int("length") ?: 20// 每页显示的size
        //获取时间内所有庭审记录
        def model = [:]
        def count = PlanInfo.createCriteria().count() {
            createAlias('caseInfo', 'c', JoinType.LEFT_OUTER_JOIN)
            and {
                eq("active", DataStatus.SHOW)
                if (id) eq("id", id)
                if (startDate) ge("startDate", startDate)//计划开庭时间
                if (endDate) le("startDate", endDate)//计划闭庭时间

                if (status != null) eq("status", status)
                if (filingDate) eq("filingDate", filingDate)//立案日期
                if (archives) like("c.archives", "%${archives}%")//案号
                if (name) like("c.name", "%${name}%")//案件名称
                if (type) like("c.type", type)//案件类型
                if (courtroom) eq("courtroom", courtroom)//法庭
                if (accuser) like("c.accuser", "%${accuser}%")//原告
                if (accused) like("c.accused", "%${accused}%")//被告
                if (judge) like("judge", judge)//法官
                if (secretary) like("secretary", secretary)//书记员
            }
        }
        def planList = PlanInfo.createCriteria().list {
            createAlias('caseInfo', 'c', JoinType.LEFT_OUTER_JOIN)
            and {
                setMaxResults(length)
                setFirstResult(page * length)
                eq("active", DataStatus.SHOW)
                if (id) eq("id", id)
                if (startDate) ge("startDate", startDate)//计划开庭时间
                if (endDate) le("startDate", endDate)//计划闭庭时间

                if (status != null) eq("status", status)
                if (filingDate) eq("filingDate", filingDate)//立案日期
                if (archives) like("c.archives", "%${archives}%")//案号
                if (name) like("c.name", "%${name}%")//案件名称
                if (type) like("c.type", type)//案件类型
                if (courtroom) eq("courtroom", courtroom)//法庭
                if (accuser) like("c.accuser", "%${accuser}%")//原告
                if (accused) like("c.accused", "%${accused}%")//被告
                if (judge) like("judge", judge)//法官
                if (secretary) like("secretary", secretary)//书记员
            }
            order("startDate", "desc")
            order("status", "asc")
        } as List<PlanInfo>
        def dataList = []
        for (def planInfo : planList) {
            try {
                dataList.add(getPlanModel(planInfo))
            } catch (e) {
                log.info("[ApiService.plan] 处理id=(${planInfo.id})的排期时出现错误,错误信息:${e.message}")
            }
        }
        model.put("total", count)//数据总条数
        model.put("page", page)//当前页码
        model.put("length", length)//每页长度
        model.put("plan", dataList)
        model
    }

    /**
     * 处理排期
     * @param planInfo 需要处理的排期
     * @return 结果
     */
    Map getPlanModel(PlanInfo planInfo) {
        //查询排期是否为并案排期 如果存在则将并案排期放到一个对象返回
        if (planInfo.combinedPlan) {
            def planInfoList = PlanInfo.findAllByCombinedPlanAndIdNotEqual(planInfo.combinedPlan, planInfo.id)
            def caseList = []
            //先将主排期加到第一位置
            def caseInfoMaster = planInfo.caseInfo
            
            //取合议庭成员中的审判长 不存在则用排期中的法官
            def judgeName = null
            for (def coll: planInfo.collegial){
                if (coll.type && coll.type == CollegialType.PERSIDING_JUDGE){
                    judgeName = coll.name
                }
            }
            if (!judgeName){
                judgeName = planInfo.judge?.name
            }
            caseList.add(
                    [
                            caseId    : caseInfoMaster.id,
                            archives  : caseInfoMaster.archives,
                            name      : caseInfoMaster.name,
                            caseType  : caseInfoMaster.type,
                            id        : planInfo.id,
                            uid       : planInfo.uid,
                            caseSyncId: caseInfoMaster.synchronizationId,
                            courtroom : [
                                    id  : planInfo.courtroom?.id,
                                    name: planInfo.courtroom?.name
                            ],
                            status    : planInfo.status,
                            judge     : [
                                    id  : planInfo.judge?.id,
                                    name: judgeName
                            ],
                            secretary : [
                                    id  : planInfo.secretary?.id,
                                    name: planInfo.secretary?.name
                            ],
                            collegial : planInfo.collegial*.info,
                            accuser   : caseInfoMaster?.accuser,
                            accused   : caseInfoMaster?.accused,
                            startDate : planInfo.startDate?.format('yyyy/MM/dd HH:mm:ss'),
                            endDate   : planInfo.endDate?.format('yyyy/MM/dd HH:mm:ss'),
                            reason    : caseInfoMaster?.caseCause
                    ])
            for (def planInfoCombine : planInfoList) {
                def caseInfoCombine = planInfoCombine.caseInfo
                caseList.add(
                        [
                                archives: caseInfoCombine.archives,
                        ])
            }
            return [planInfoCombine: caseList]
        }
        //处理未并案排期
        def caseInfo = planInfo.caseInfo//案件信息
        def courtroom = planInfo.courtroom//法庭信息
        def result = [
                id        : planInfo.id,
                uid       : planInfo.uid,
                caseSyncId: caseInfo.synchronizationId,
                courtroom : [
                        id  : courtroom?.id,
                        name: courtroom?.name
                ],
                caseType  : caseInfo?.type,
                status    : planInfo.status,
                archives  : caseInfo?.archives,
                name      : caseInfo?.name,
                judge     : [
                        id  : planInfo.judge?.id,
                        name: planInfo.judge?.name
                ],
                secretary : [
                        id  : planInfo.secretary?.id,
                        name: planInfo.secretary?.name
                ],
                collegial : planInfo.collegial*.info,
                accuser   : caseInfo?.accuser,
                accused   : caseInfo?.accused,
                startDate : planInfo.startDate?.format('yyyy/MM/dd HH:mm:ss'),
                endDate   : planInfo.endDate?.format('yyyy/MM/dd HH:mm:ss')
        ]
        return result
    }

    /**
     * 分页获取庭审列表
     * @param status 庭审状态
     * @param start 起始时间
     * @param end 结束时间
     * @param page 当前页码
     * @param length 每页长度
     * @param search 搜索内容
     * @return 庭审信息
     */
    def trial(GrailsParameterMap params) {
        def id = params.long("id")
        def startDate = params.date("startDate", "yyyy/MM/dd")//计划开庭时间开始范围
        def endDate = params.date("endDate", "yyyy/MM/dd")//计划开庭时间结束范围
        if (endDate) {
            endDate = DateUtil.endOfDay(endDate)//获取天数的最后时间
        }
        def filingDate = params.date("filingDate", "yyyy/MM/dd") //立案日期
        def archives = params.archives as String //案号
        def name = params.name as String //案件名称
        def type = params.type as String //案件类型
        def accuser = params.accuser as String //原告
        def accused = params.accused as String //被告
        def courtroom = Courtroom.get(params.long("courtroom")) //法庭
        def judge = Employee.get(params.long("judge")) //实际开庭法官
        def secretary = Employee.get(params.long("secretary")) //实际开庭书记员
        def status = params.int("status")//庭审状态

        def page = params.int("page") ?: 0// 起始
        def length = params.int("length") ?: 20// 每页显示的size


        //获取时间内所有庭审记录
        def model = [:]

        def count = TrialInfo.createCriteria().count() {
            createAlias('planInfo', 'p', JoinType.LEFT_OUTER_JOIN)
            createAlias('p.caseInfo', 'c', JoinType.LEFT_OUTER_JOIN)
            and {
                eq("active", DataStatus.SHOW)
                if (id) eq("id", id)
                if (startDate) ge("startDate", startDate)//计划开庭时间
                if (endDate) le("startDate", endDate)//计划闭庭时间
                if (status != null) eq("status", status)
                if (filingDate) eq("filingDate", filingDate)//立案日期
                if (archives) like("c.archives", "%${archives}%")//案号
                if (name) like("c.name", "%${name}%")//案件名称
                if (type) like("c.type", type)//案件类型
                if (courtroom) eq("courtroom", courtroom)//法庭
                if (accuser) like("c.accuser", "%${accuser}%")//原告
                if (accused) like("c.accused", "%${accused}%")//被告
                if (judge) like("judge", judge)//法官
                if (secretary) like("secretary", secretary)//书记员
            }
        }
        def trialList = TrialInfo.createCriteria().list {
            createAlias('planInfo', 'p', JoinType.LEFT_OUTER_JOIN)
            createAlias('p.caseInfo', 'c', JoinType.LEFT_OUTER_JOIN)
            and {
                setMaxResults(length)
                setFirstResult(page * length)
                eq("active", DataStatus.SHOW)
                if (id) eq("id", id)
                if (startDate) ge("startDate", startDate)//计划开庭时间
                if (endDate) le("startDate", endDate)//计划闭庭时间
                if (status != null) eq("status", status)
                if (filingDate) eq("filingDate", filingDate)//立案日期
                if (archives) like("c.archives", "%${archives}%")//案号
                if (name) like("c.name", "%${name}%")//案件名称
                if (type) like("c.type", type)//案件类型
                if (courtroom) eq("courtroom", courtroom)//法庭
                if (accuser) like("c.accuser", "%${accuser}%")//原告
                if (accused) like("c.accused", "%${accused}%")//被告
                if (judge) like("judge", judge)//法官
                if (secretary) like("secretary", secretary)//书记员
            }
            order("startDate", "desc")
            order("status", "asc")
        } as List<TrialInfo>
        def dataList = []
        for (def trialInfo : trialList) {
            try {
                dataList.add(getTrialModel(trialInfo))
            } catch (e) {
                e.printStackTrace()
            }

        }
        model.put("total", count)//数据总条数
        model.put("page", page)//当前页码
        model.put("length", length)//每页长度
        model.put("trial", dataList)
        model
    }

    Map getTrialModel(TrialInfo trialInfo) {
        def caseInfo = trialInfo.planInfo.caseInfo//案件信息
        def courtroom = trialInfo.courtroom//法庭信息
        //获取庭审视频信息
        def videoInfoList = trialInfo.videoInfo.sort({ a, b ->
            a.startRecTime <=> b.startRecTime
        })
        def tempMap = [:]
        for (def videoInfo : videoInfoList) {
            def url = "http://${courtroom.storeIp}:8200/${videoInfo.fileName}"
            def tempList = tempMap.get(videoInfo.channelName)
            if (!tempList) {
                tempList = []
            }
            tempList.add([
                    id       : videoInfo.id,
                    startDate: videoInfo.startRecTime,
                    endDate  : videoInfo.endRecTime,
                    url      : url
            ])
            tempMap.put(videoInfo.channelName, tempList)
        }
        def videoList = []
        tempMap.each { entry ->
            videoList.add([
                    name : entry.key,
                    video: entry.value
            ])
        }
        [
                id       : trialInfo.id,
                courtroom: [
                        id  : courtroom?.id,
                        name: courtroom?.name
                ],
                notename : trialInfo.note ? trialInfo.note.substring(trialInfo.note?.lastIndexOf('/') + 1) : null,
                type     : caseInfo?.type,
                status   : trialInfo.status,
                archives : caseInfo?.archives,
                name     : caseInfo?.name,
                judge    : [
                        id  : trialInfo.judge?.id,
                        name: trialInfo.judge?.name
                ],
                secretary: [
                        id  : trialInfo.secretary?.id,
                        name: trialInfo.secretary?.name
                ],
                note     : grailsLinkGenerator.link(uri: "/api/client/trial/down/note/${trialInfo.id}", absolute: true),
                startDate: trialInfo.startDate?.format('yyyy/MM/dd HH:mm:ss'),
                endDate  : trialInfo.endDate?.format('yyyy/MM/dd HH:mm:ss'),
                videoInfo: videoList
        ]
    }

    def uploadFile(String path, TrialInfo trialInfo, MultipartFile file) {
        //庭审主键_文件名
        def filePath = "${trialInfo.id}_${file.getOriginalFilename()}"
        def serverFile = new File("${path}/${trialInfo.planInfo.id}/", filePath)
        if (!serverFile.exists()) {
            serverFile.getParentFile().mkdirs()
        }
        file.transferTo(serverFile)
        def data = trialInfo.comment
        def getPath = "/api/client/trial/getComment/${trialInfo.planInfo.id}/${filePath}" as String
        if (!data) {
            trialInfo.comment = getPath
        } else {
            if (data.indexOf("${getPath}") == -1) {
                trialInfo.comment += ",${getPath}"
            }
        }
        saveTrial(trialInfo)
        if (trialInfo.hasErrors()) {
            log.error("保存庭审记录时时出错ApiService.uploadFile trialInfo [${trialInfo.errors}]")
            throw new RuntimeException()
        }
        //这个地址不仅需要存储到数据库而且需要发送给书记员软件
        getPath
    }

    def send2Clerk(String path, String name) {
        def url = [
                name: name,
                url : path
        ]
        convertAndSend("/queue/comment", (url as JSON) as String)
    }

    /**
     * 保存临时立案
     */
    def saveTempCaseInfo(CaseInfo caseInfo, GrailsParameterMap params) {
        def planInfo = new PlanInfo()
        planInfo.active = DataStatus.SHOW
        planInfo.uid = params.uuid? params.uuid : UUIDGenerator.nextUUID()
        planInfo.caseInfo = caseInfo
        planInfo.status = PlanStatus.PLAN
        def date = new Date()
        planInfo.startDate = date
        planInfo.endDate = date
        planInfo.properties = params
        planService.save(caseInfo, planInfo, params.collegialName)
    }

    /**
     * 闭庭时断开远程连接
     * @param courtroom 需要断开的法庭
     */
    void stopConnect(Courtroom courtroom) {
        try {
            log.info("[ApiService.stopConnect] ${courtroom.name}闭庭成功开始断开远程连接,链接信息:${courtroom.distance}")
            String distance = courtroom.distance
            if (distance) {
                String[] distanceArr = distance.split("///")
                for (int i = 0; i < distanceArr.size(); i++) {
                    String service = distanceArr[i]
                    if (service.contains("no")) {
                        continue
                    }
                    String[] serviceArr = service.split(",")
                    int num = 0
                    if (service.contains("status2")) {
                        num = 1
                    }
                    def resp = RemoteHttpUtil.remotePost([
                            id : serviceArr[1].split("=")[1],
                            num: num
                    ], "${serviceArr[0].split("=")[1]}/api/remoteService/stopConnect")
                    if (resp.code == 0) {
                        def setDecoderProfileRs = RemoteHttpUtil.send(false, courtroom, "", false, num, 0)
                        log.info("[ApiService.stopConnect] 第${num + 1}通道本地处理结果：${setDecoderProfileRs}")
                    }
                }
                CtrlCommandUtil.ctrlCommand(courtroom.deviceIp, 8060, "TV0-HCHM.")
                courtroom.distance = ""
                courtroom.status = CourtroomStatus.NORMAL
                courtroom.save()
            }
        } catch (ignored) {
            log.error("[ApiService.stopConnect] 闭庭时断开远程连接异常,为避免影响闭庭将异常忽略.")
        }
    }

    /**
     * 补充远程法院的子节点
     * @param it 远程法院
     * @return 远程法院信息
     */
    def getChildren(DistanceCourt it) {
        //查询子数据
        def children = DistanceCourt.findAllByParent(it, [sort: "code", order: "esc"])
        if (children.size() == 0) {
            return [
                    id     : it.id,
                    name   : it.name,
                    service: it.service
            ]
        } else {
            def data = []
            for (int i = 0; i < children.size(); i++) {
                data.add(getChildren(children[i]))
            }
            return [
                    id      : it.id,
                    name    : it.name,
                    service : it.service,
                    children: data
            ]
        }
    }
}
