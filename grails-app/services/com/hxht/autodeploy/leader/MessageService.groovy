package com.hxht.autodeploy.leader

import cn.hutool.core.date.DateField
import cn.hutool.core.date.DateUtil
import com.hxht.techcrt.DataStatus
import com.hxht.techcrt.PlanStatus
import com.hxht.techcrt.court.Courtroom
import com.hxht.techcrt.court.PlanInfo
import com.hxht.techcrt.utils.DateUtils
import grails.converters.JSON
import grails.gorm.transactions.Transactional

@Transactional
class MessageService {

    def planDayCount(Integer year, Integer month, Integer day,Courtroom courtroom) {
        //获取当前日期
        def now = new Date()
        def first = DateUtils.getMonthFirstDay(year, month)
        def last = DateUtils.getMonthLastDay(year, month)

        def dateList = DateUtil.rangeToList(first, last, DateField.DAY_OF_YEAR)

        def dataList = []
        for (def date : dateList) {
            //一天的开始
            def before = date
            //一天的结束
            def end = DateUtil.offsetDay(before, 1)
            //排期数量
            def count = PlanInfo.createCriteria().count() {
                and {
                    eq("active", DataStatus.SHOW)
                    ge("startDate", before)//计划开庭时间
                    le("startDate", end)//计划闭庭时间
                    eq("courtroom",courtroom)
                }
            }
            def today = before.format("yyyyMMdd") == now.format("yyyyMMdd")
            if (count == 0 && !today) {
                continue
            }
            def color = "#2d353c"
            if (today) {
                color = "#00ACAC"
            }
            dataList.add([
                    before.format('d/M/yyyy'),
                    "${before.format('M月d日')}有${count}个排期",
                    '#',
                    color,
                    ''
            ])
        }
        dataList
    }
    /**
     * 获取指定日期排期列表
     * @param date 指定日期
     * @param length 获取多少数据，数据的长度
     */
    def planDayItem(Date date, length, Courtroom courtroom) {
        if (!date) {
            date = DateUtil.beginOfDay(new Date())
        }
        def first = date
        def last = DateUtil.offsetDay(date, 1)
        def dataList = []
        def planList = PlanInfo.createCriteria().list {
            and {
                setMaxResults(length)
                setFirstResult(0)
                eq("active", DataStatus.SHOW)
                eq("courtroom", courtroom)
                ge("startDate", first)//计划开庭时间
                le("startDate", last)//计划闭庭时间
            }
            order("startDate", "asc")
        } as List<PlanInfo>
        for (def planInfo : planList) {
            dataList.add(getPlanModel(planInfo))
        }
        dataList
    }

    Map getPlanModel(PlanInfo planInfo) {
        def caseInfo = planInfo.caseInfo//案件信息
        def courtroom = planInfo.courtroom//法庭信息
        def text = "[${courtroom?.name}] ${caseInfo?.archives} ${caseInfo?.name}"
        [
                id       : planInfo.id,
                status   : PlanStatus.getString(planInfo.status),
                text     : text,
                startDate: planInfo.startDate?.format('HH:mm')
        ]
    }
    /**
     * 获取指定法庭数据
     * @param courtroom
     */
    def courtroomData(Courtroom courtroom) {
        def date = DateUtil.beginOfDay(new Date())
        //获取正在开庭的排期
        def first = date
        def last = DateUtil.offsetDay(date, 1)
        def planInfo = PlanInfo.findByActiveAndStatusAndCourtroom(DataStatus.SHOW, PlanStatus.SESSION, courtroom)
        if (!planInfo) {//如果没有正在开庭的排期，获取法庭选择当天时间后最近的一个排期
            def planList = PlanInfo.findAllByActiveAndCourtroomAndStatusAndStartDateGreaterThan(DataStatus.SHOW, courtroom, PlanStatus.PLAN,first)
            if (planList){
                planInfo = planList[0]
            }
        }
        if (!planInfo) {
            return
        }
        def cfg = JSON.parse(courtroom.cfg)
        def live = null
        def encode = cfg.encode[0]
        if (encode) {
//            live = "http://${courtroom.liveIp}:8300/hls/${encode.encodeip}/${encode.number}/s.m3u8"
            live = "http://${courtroom.liveIp}:8791/${encode.encodeip}/${encode.number}.flv"
        }
        def caseInfo = planInfo.caseInfo
        [
                planInfo  : planInfo.id,
                allowPlay : planInfo.allowPlay,
                status    : PlanStatus.getString(planInfo.status),
                live      : live,
                archives  : caseInfo.archives,
                name      : caseInfo.name,
                filingDate: caseInfo.filingDate?.format("yyyy/MM/dd HH:mm"),
                courtroom : courtroom.name,
                caseType  : caseInfo.type?.name,
                startDate : planInfo.startDate?.format("yyyy/MM/dd HH:mm"),
                endDate   : planInfo.endDate?.format("yyyy/MM/dd HH:mm"),
                courtroom : courtroom.name,
                type      : caseInfo.type?.name,
                accuser   : caseInfo.accuser,
                accused   : caseInfo.accused,
                collegial : planInfo.collegial*.info.join(","),
                secretary : planInfo.secretary?.name,
                summary   : caseInfo.summary ?: "无数据",
                judge     : planInfo.judge?.name ?: "无数据"
        ]
    }
    /**
     * 返回指定时间段空闲法庭列表
     */
    def courtroomFree(Date first, Date last) {
        def courtroomList = Courtroom.findAllByActive(DataStatus.SHOW, [sort: "sequence", order: "desc"])
        def courtroomFreeList = []
        for (def courtroom : courtroomList) {
            def count =  PlanInfo.createCriteria().count() {
                and {
                    eq("active", DataStatus.SHOW)
                    ge("startDate", first)//计划开庭时间
                    le("startDate", last)//计划闭庭时间
                    eq("courtroom", courtroom)//排期法庭
                }
            }
            if (count == 0) {//指定时间段内法庭空闲
                courtroomFreeList.add(courtroom)
            }
        }
        courtroomFreeList
    }
}
