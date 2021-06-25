package com.hxht.autodeploy.court.trial

import com.hxht.techcrt.DataStatus
import com.hxht.techcrt.PlanStatus
import com.hxht.techcrt.PositionStatus
import com.hxht.techcrt.court.Courtroom
import com.hxht.techcrt.court.Employee
import com.hxht.techcrt.court.TrialInfo
import cn.hutool.core.date.DateUtil
import grails.gorm.transactions.Transactional
import grails.web.mapping.LinkGenerator
import grails.web.servlet.mvc.GrailsParameterMap
import org.hibernate.sql.JoinType

import java.text.SimpleDateFormat

@Transactional
class TrialsService {
    LinkGenerator grailsLinkGenerator
    
    def planListQueryData(){
        def judgeList = Employee.findAllByPosition(PositionStatus.JUDGE)
        def secretaryList = Employee.findAllByPosition(PositionStatus.SECRETARY)
        def courtroomList = Courtroom.findAll()
        def date = new Date()
        def dateStr
        if (date) {
            dateStr = new SimpleDateFormat("yyyy/MM/dd").format(date)
        }
        [judgeList: judgeList, secretaryList: secretaryList, courtroomList: courtroomList, date: dateStr]
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
        def start = params.int("start") ?: 0// 起始
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
                if (status != null) {
                    eq("status", status)
                }
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
            if (status == null){
                or{
                    eq("status", PlanStatus.ADJOURN)
                    eq("status", PlanStatus.CLOSED)
                    eq("status", PlanStatus.ARCHIVED)
                }
            }
        }
        def trialList = TrialInfo.createCriteria().list {
            createAlias('planInfo', 'p', JoinType.LEFT_OUTER_JOIN)
            createAlias('p.caseInfo', 'c', JoinType.LEFT_OUTER_JOIN)
            and {
                setMaxResults(length)
                setFirstResult(start)
                eq("active", DataStatus.SHOW)
                if (id) eq("id", id)
                if (startDate) ge("startDate", startDate)//计划开庭时间
                if (endDate) le("startDate", endDate)//计划闭庭时间
//                if (status != null) eq("status", status)
                if (status != null) {
                    eq("status", status)
                }
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
            if (status == null){
                or{
                    eq("status", PlanStatus.ADJOURN)
                    eq("status", PlanStatus.CLOSED)
                    eq("status", PlanStatus.ARCHIVED)
                }
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
        model.put("recordsTotal", count)//数据总条数
        model.put("recordsFiltered", count)//显示的条数
        model.put("data", dataList)
        model
    }

    Map getTrialModel(TrialInfo trialInfo) {
        def caseInfo = trialInfo.planInfo.caseInfo//案件信息
        def courtroom = trialInfo.courtroom//法庭信息
        //获取庭审视频信息
        [
                id       : trialInfo.id,
                courtroomName: courtroom?.name,
                notename : trialInfo.note ? trialInfo.note.substring(trialInfo.note?.lastIndexOf('/') + 1) : null,
                type     : caseInfo?.type,
                status   : PlanStatus.getString(trialInfo.status),
                archives : caseInfo?.archives,
                name     : caseInfo?.name,
                judgeName    : trialInfo.judge?.name,
                secretaryName : trialInfo.secretary?.name,
                note     : grailsLinkGenerator.link(uri: "/api/client/trial/down/note/${trialInfo.id}", absolute: true),
                startDate: trialInfo.startDate?.format('yyyy/MM/dd HH:mm:ss'),
                endDate  : trialInfo.endDate?.format('yyyy/MM/dd HH:mm:ss'),
        ]
    }

    def getChn(TrialInfo trialInfo){
        def courtroom = trialInfo.courtroom//法庭信息
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
            videoInfo: videoList
        ]
    }
    
}
