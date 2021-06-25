package com.hxht.autodeploy.leader

import cn.hutool.core.date.DateUtil
import com.alibaba.fastjson.JSON
import com.hxht.techcrt.CourtroomStatus
import com.hxht.techcrt.DataStatus
import com.hxht.techcrt.PlanStatus
import com.hxht.techcrt.PositionStatus
import com.hxht.techcrt.court.Courtroom
import com.hxht.techcrt.court.Employee
import com.hxht.techcrt.court.PlanInfo
import com.hxht.techcrt.court.PlanTrial
import com.hxht.techcrt.court.TrialInfo
import com.hxht.techcrt.court.VideoInfo
import com.hxht.techcrt.mem.DeviceIsOnlineService

import grails.core.GrailsApplication
import grails.events.EventPublisher
import grails.gorm.transactions.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import org.hibernate.sql.JoinType

import java.text.SimpleDateFormat

@Transactional
class LeaderService implements EventPublisher{
    GrailsApplication grailsApplication
    DeviceIsOnlineService deviceIsOnlineService

    def list(GrailsParameterMap params) {
        def draw = params.int("draw") ?: 1// 记录操作的次数 每次加1
        def start = params.int("start") ?: 0// 起始
        def length = params.int("length") ?: 20// 每页显示的size

        //获取查询参数
        def archives = params.archives as String //案号
        def name = params.name as String //案件名称
        def courtroom = Courtroom.get(params.long("courtroom")) //法庭
        def judge = Employee.get(params.long("judge")) //法官
        def secretary = Employee.get(params.long("secretary")) //书记员
        def startDate = params.date("startDate", "yyyy/MM/dd")//计划开庭时间开始范围
        def status = params.int("status")//庭审状态
        def endDate = params.date("endDate", "yyyy/MM/dd")//计划开庭时间结束范围
        if (endDate) {
            endDate = DateUtil.offsetDay(endDate, 1)//结束时间为后一天的零点发
        }
        //获取时间内所有庭审记录
        def model = [:]
        model.put("draw", draw)
        //获取所有案件排期数量
        def count = PlanInfo.createCriteria().count() {
            createAlias('caseInfo', 'c', JoinType.LEFT_OUTER_JOIN)
            and {
                eq("active", DataStatus.SHOW)
                if (archives) like("c.archives", "%${archives}%")//案号
                if (name) like("c.name", "%${name}%")//案件名称
                if (courtroom) eq("courtroom", courtroom)//法庭
                if (judge) eq("judge", judge)//法官
                if (status) eq("status", status)//状态
                if (secretary) eq("secretary", secretary)//书记员
                if (startDate) ge("startDate", startDate)//计划开庭时间
                if (endDate) le("startDate", endDate)//计划闭庭时间

            }
        }
        //获取所有案件排期列表
        def planList = PlanInfo.createCriteria().list {
            createAlias('caseInfo', 'c', JoinType.LEFT_OUTER_JOIN)
            and {
                eq("active", DataStatus.SHOW)
                if (archives) like("c.archives", "%${archives}%")//案号
                if (name) like("c.name", "%${name}%")//案件名称
                if (courtroom) eq("courtroom", courtroom)//法庭
                if (judge) eq("judge", judge)//法官
                if (status) eq("status", status)//状态
                if (secretary) eq("secretary", secretary)//书记员
                if (startDate) ge("startDate", startDate)//计划开庭时间
                if (endDate) le("startDate", endDate)//计划闭庭时间
                setMaxResults(length)
                setFirstResult(start)

            }
        } as List<PlanInfo>

        def dataList = []
        def num = 0
        for (def planInfo : planList) {
            num = num+1
            dataList.add([
                    archives : planInfo.caseInfo.archives,
                    courtroom: planInfo.courtroom?.name,
                    caseName : planInfo.caseInfo?.name,
                    caseType : planInfo.caseInfo.type as String,
                    startDate: planInfo.startDate.format('yyyy/MM/dd HH:mm'),
                    judge    : planInfo.judge?.name,
                    secretary: planInfo.secretary?.name,
                    id       : planInfo.id,
                    num:num
            ])
        }

        model.put("recordsTotal", count)//数据总条数
        model.put("recordsFiltered", count)//显示的条数
        model.put("data", dataList)
        model
    }
    def query(GrailsParameterMap params) {
        def draw = params.int("draw") ?: 1// 记录操作的次数 每次加1
        def start = params.int("start") ?: 0// 起始
        def length = params.int("length") ?: 20// 每页显示的size

        //获取查询参数
        def archives = params.archives as String //案号
        def name = params.name as String //案件名称
        def courtroom = Courtroom.get(params.long("courtroom")) //法庭
        def judge = Employee.get(params.long("judge")) //法官
        def secretary = Employee.get(params.long("secretary")) //书记员
        def startDate = params.date("startDate", "yyyy/MM/dd")//计划开庭时间开始范围
        def endDate = params.date("endDate", "yyyy/MM/dd")//计划开庭时间结束范围
        if (endDate) {
            endDate = DateUtil.offsetDay(endDate, 1)//结束时间为后一天的零点发
        }
        //获取时间内所有庭审记录
        def model = [:]
        model.put("draw", draw)
        //获取所有案件排期数量
        def count = PlanInfo.createCriteria().count() {
            createAlias('caseInfo', 'c', JoinType.LEFT_OUTER_JOIN)
            and {
                eq("active", DataStatus.SHOW)
                eq("status", PlanStatus.PLAN)//状态
                if (archives) like("c.archives", "%${archives}%")//案号
                if (name) like("c.name", "%${name}%")//案件名称
                if (courtroom) eq("courtroom", courtroom)//法庭
                if (judge) eq("judge", judge)//法官
                if (secretary) eq("secretary", secretary)//书记员
                if (startDate) ge("startDate", startDate)//计划开庭时间
                if (endDate) le("startDate", endDate)//计划闭庭时间
            }
        }
        //获取所有案件排期列表
        def planList = PlanInfo.createCriteria().list {
            createAlias('caseInfo', 'c', JoinType.LEFT_OUTER_JOIN)
            and {
                eq("active", DataStatus.SHOW)
                eq("status", PlanStatus.PLAN)//状态
                if (archives) like("c.archives", "%${archives}%")//案号
                if (name) like("c.name", "%${name}%")//案件名称
                if (courtroom) eq("courtroom", courtroom)//法庭
                if (judge) eq("judge", judge)//法官
                if (secretary) eq("secretary", secretary)//书记员
                if (startDate) ge("startDate", startDate)//计划开庭时间
                if (endDate) le("startDate", endDate)//计划闭庭时间
                setMaxResults(length)
                setFirstResult(start)
            }
        } as List<PlanInfo>

        def dataList = []
        def num = 0
        for (def planInfo : planList) {
            num = num+1
            dataList.add([
                    archives : planInfo.caseInfo.archives,
                    courtroom: planInfo.courtroom?.name,
                    caseName : planInfo.caseInfo?.name,
                    caseType : planInfo.caseInfo.type as String,
                    startDate: planInfo.startDate.format('yyyy/MM/dd HH:mm'),
                    judge    : planInfo.judge?.name,
                    secretary    : planInfo.secretary?.name,
                    status       : PlanStatus.getString(planInfo.status),
                    id       : planInfo.id,
                    num      : num
            ])
        }
        model.put("recordsTotal", count)//数据总条数
        model.put("recordsFiltered", count)//显示的条数
        model.put("data", dataList)
        model
    }
    def liveShow(TrialInfo trialInfo){
        //根据trialinfo得到planinfo
        def planInfo = trialInfo.planInfo
        //根据planinfo得到courtroom信息
        def courtroom = planInfo.courtroom
        //获取cfg信息
        def cfg = JSON.parse(courtroom.cfg)
        //定义一个list作为最后结果
        def videoList = []
        //判断排期状态
        if (trialInfo.status == PlanStatus.SESSION){
            log.info("[LeaderService.liveShow]---->庭审正在直播中，返回直播地址")
            for (def encode : cfg.encode) {
                def url = "http://${courtroom.liveIp}:8791/${encode.encodeip}/${encode.number}.flv"
                videoList.add([
                        number: encode.number,
                        name  : encode.name,
                        url   : url
                ])
            }
        }else {
            log.info("[LeaderService.liveShow]---->庭审已经休庭或闭庭中，返回点播视频地址")
            def chnList = []
            def videoInfoListForNum = VideoInfo.findAllByTrialInfo(trialInfo)
            for (def video : videoInfoListForNum){
                chnList.add([
                        number: video.channelNum,
                        name: video.channelName
                ])
            }

            for (def chn : chnList) {
                def videoMap = [:]
                videoMap.put("number", chn.number)
                videoMap.put("name", chn.name)
                def videoinfolist = VideoInfo.findAllByTrialInfoAndChannelNum(trialInfo, chn.number as String, [sort: "startRecTime", order: "asc"])
                def trialVideoList = []
                def totalLength = 0
                for (def i = 0; i < videoinfolist.size(); i++) {
                    def videoInfo = videoinfolist[i]
                    trialVideoList.add([
                            serial      : i,
                            length      : videoInfo.length,
                            startRecTime: videoInfo.startRecTime?.format("HH:mm"),
                            imagesUrl   : grailsApplication.config.getProperty('tc.trial.images.path'),
                            url         : "http://${courtroom.storeIp}:8200/${videoInfo.fileName}"
                    ])
                    totalLength += videoInfo.length
                    this.notify("screenShot",videoInfo.id)
                }
                videoMap.put("videoUrl",trialVideoList)
                videoMap.put("totalLength",totalLength)
                videoList.add(videoMap)
            }
        }
        videoList
    }
    def getTrialVideoList(PlanInfo planInfo) {
        List<TrialInfo> trialInfoList = []
        if (planInfo.combinedPlan) {//存在并案的排期
            def planTrialList = PlanTrial.findAllByPlanInfo(planInfo)//获取所有排期对应的庭审
            for (PlanTrial planTrial : planTrialList) { //将庭审放入到集合
                trialInfoList.add(planTrial.trialInfo)
            }
        } else {//不存在并案的情况
            trialInfoList = TrialInfo.findAllByPlanInfoAndActive(planInfo, DataStatus.SHOW, [sort: "status", order: "asc"])
        }
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
    def courtroomList(int draw, int start, int length, String search) {
        def model = [:]
        model.put("draw", draw)
        def count = Courtroom.createCriteria().count() {
            if (search) {
                or{
                    like("name", "%${search}%")
                    like("liveIp", "%${search}%")
                    like("livePort", "%${search}%")
                    like("deviceIp", "%${search}%")
                    like("storeIp", "%${search}%")
                    if (PlanStatus.getCode(search) != null) {
                        eq("status", CourtroomStatus.getCode(search))
                    }
                }
            }
        }
        def courtroomList = Courtroom.createCriteria().list {
            and {
                setMaxResults(length)
                setFirstResult(start)
            }
            if (search) {
                or{
                    like("name", "%${search}%")
                    like("liveIp", "%${search}%")
                    like("livePort", "%${search}%")
                    like("deviceIp", "%${search}%")
                    like("storeIp", "%${search}%")
                    if (PlanStatus.getCode(search) != null) {
                        eq("status", CourtroomStatus.getCode(search))
                    }
                }
            }
            order("sequence", "desc")
        } as List<Courtroom>

        def dataList = []
        def num = 0
        for (def courtroom : courtroomList) {
            num = num+1
            dataList.add([
                    name     :courtroom.name,
                    isConnect:deviceIsOnlineService.isConnect(courtroom.id),
                    status   :CourtroomStatus.getString(courtroom.status),
                    id       : courtroom.id,
                    num      : num,
            ])
        }

        model.put("recordsTotal", count)//数据总条数
        model.put("recordsFiltered", count)//显示的条数
        model.put("data", dataList)
        model
    }
    def trialList(GrailsParameterMap params) {
        def draw = params.int("draw") ?: 1// 记录操作的次数 每次加1
        def start = params.int("start") ?: 0// 起始
        def length = params.int("length") ?: 20// 每页显示的size
        //获取查询参数
        def archives = params.archives as String //案号
        def name = params.name as String //案件名称
        def courtroom = Courtroom.get(params.long("courtroom")) //法庭
        def judge = Employee.get(params.long("judge")) //法官
        def secretary = Employee.get(params.long("secretary")) //书记员
        def startDate = params.date("startDate", "yyyy/MM/dd")//计划开庭时间开始范围
        def endDate = params.date("endDate", "yyyy/MM/dd")//计划开庭时间结束范围
        if (endDate) {
            endDate = DateUtil.offsetDay(endDate, 1)//结束时间为后一天的零点
        }

        //获取时间内所有庭审记录
        def model = [:]
        model.put("draw", draw)
        def count = PlanInfo.createCriteria().count() {
            createAlias('caseInfo', 'c', JoinType.LEFT_OUTER_JOIN)
            and {
                eq("active", DataStatus.SHOW)
                eq("status", PlanStatus.SESSION)//状态
                if (archives) like("c.archives", "%${archives}%")//案号
                if (name) like("c.name", "%${name}%")//案件名称
                if (courtroom) eq("courtroom", courtroom)//法庭
                if (judge) eq("judge", judge)//法官
                if (secretary) eq("secretary", secretary)//书记员
                if (startDate) ge("startDate", startDate)//计划开庭时间
                if (endDate) le("startDate", endDate)//计划闭庭时间
            }
        }
        //获取所有案件排期列表
        def planList = PlanInfo.createCriteria().list {
            createAlias('caseInfo', 'c', JoinType.LEFT_OUTER_JOIN)
            and {
                eq("active", DataStatus.SHOW)
                eq("status", PlanStatus.SESSION)//状态
                if (archives) like("c.archives", "%${archives}%")//案号
                if (name) like("c.name", "%${name}%")//案件名称
                if (courtroom) eq("courtroom", courtroom)//法庭
                if (judge) eq("judge", judge)//法官
                if (secretary) eq("secretary", secretary)//书记员
                if (startDate) ge("startDate", startDate)//计划开庭时间
                if (endDate) le("startDate", endDate)//计划闭庭时间
                setMaxResults(length)
                setFirstResult(start)
            }
        } as List<PlanInfo>


        def dataList = []
        def num = 0
        for (def planInfo : planList) {
            num = num + 1
            dataList.add([
                    archives : planInfo.caseInfo.archives,
                    courtroom: planInfo.courtroom?.name,
                    caseName : planInfo.caseInfo?.name,
                    caseType : planInfo.caseInfo.type as String,
                    startDate: planInfo.startDate.format('yyyy/MM/dd HH:mm'),
                    judge    : planInfo.judge?.name,
                    secretary    : planInfo.secretary?.name,
                    status       : PlanStatus.getString(planInfo.status),
                    id       : planInfo.id,
                    num      : num
            ])
        }
        model.put("recordsTotal", count)//数据总条数
        model.put("recordsFiltered", count)//显示的条数
        model.put("data", dataList)
        model

    }
    def trialVideoList(GrailsParameterMap params) {
        def draw = params.int("draw") ?: 1// 记录操作的次数 每次加1
        def start = params.int("start") ?: 0// 起始
        def length = params.int("length") ?: 20// 每页显示的size
        //获取查询参数
        def archives = params.archives as String //案号
        def name = params.name as String //案件名称
        def courtroom = Courtroom.get(params.long("courtroom")) //法庭
        def judge = Employee.get(params.long("judge")) //法官
        def secretary = Employee.get(params.long("secretary")) //书记员
        def startDate = params.date("startDate", "yyyy/MM/dd")//计划开庭时间开始范围
        def endDate = params.date("endDate", "yyyy/MM/dd")//计划开庭时间结束范围
        if (endDate) {
            endDate = DateUtil.offsetDay(endDate, 1)//结束时间为后一天的零点
        }

        //获取时间内所有庭审记录
        def model = [:]
        model.put("draw", draw)
        def count = PlanInfo.createCriteria().count() {
            createAlias('caseInfo', 'c', JoinType.LEFT_OUTER_JOIN)
            or {
                eq("status", PlanStatus.ADJOURN)
                eq("status", PlanStatus.CLOSED)
            }
            and {
                eq("active", DataStatus.SHOW)
                if (archives) like("c.archives", "%${archives}%")//案号
                if (name) like("c.name", "%${name}%")//案件名称
                if (courtroom) eq("courtroom", courtroom)//法庭
                if (judge) eq("judge", judge)//法官
                if (secretary) eq("secretary", secretary)//书记员
                if (startDate) ge("startDate", startDate)//计划开庭时间
                if (endDate) le("startDate", endDate)//计划闭庭时间
            }
        }
        //获取所有案件排期列表
        def planList = PlanInfo.createCriteria().list {
            createAlias('caseInfo', 'c', JoinType.LEFT_OUTER_JOIN)
            or {
                eq("status", PlanStatus.ADJOURN)
                eq("status", PlanStatus.CLOSED)
            }
            and {
                eq("active", DataStatus.SHOW)
                if (archives) like("c.archives", "%${archives}%")//案号
                if (name) like("c.name", "%${name}%")//案件名称
                if (courtroom) eq("courtroom", courtroom)//法庭
                if (judge) eq("judge", judge)//法官
                if (secretary) eq("secretary", secretary)//书记员
                if (startDate) ge("startDate", startDate)//计划开庭时间
                if (endDate) le("startDate", endDate)//计划闭庭时间
                setMaxResults(length)
                setFirstResult(start)
            }
        } as List<PlanInfo>


        def dataList = []
        def num = 0
        for (def planInfo : planList) {
            num = num + 1
            dataList.add([
                    archives : planInfo.caseInfo.archives,
                    courtroom: planInfo.courtroom?.name,
                    caseName : planInfo.caseInfo?.name,
                    caseType : planInfo.caseInfo.type as String,
                    startDate: planInfo.startDate.format('yyyy/MM/dd HH:mm'),
                    judge    : planInfo.judge?.name,
                    secretary    : planInfo.secretary?.name,
                    status       : PlanStatus.getString(planInfo.status),
                    id       : planInfo.id,
                    num      : num
            ])
        }
        model.put("recordsTotal", count)//数据总条数
        model.put("recordsFiltered", count)//显示的条数
        model.put("data", dataList)
        model
    }
    def planListQueryData(GrailsParameterMap params){
        def judgeList = Employee.findAllByPosition(PositionStatus.JUDGE)
        def secretaryList = Employee.findAllByPosition(PositionStatus.SECRETARY)
        def courtroomList = Courtroom.findAll()
        def date = params.date("date", "dd/MM/yyyy")
        def date1 = null
        if (date) {
            date1 = new SimpleDateFormat("yyyy/MM/dd").format(date)
        }
        [judgeList: judgeList, secretaryList: secretaryList, courtroomList: courtroomList, date: date1]
    }

}
