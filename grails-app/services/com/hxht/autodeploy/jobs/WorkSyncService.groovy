package com.hxht.autodeploy.jobs

import com.hxht.techcrt.Manufacturer
import com.hxht.techcrt.sync.DongRuan.DongRuanService
import com.hxht.techcrt.sync.beiming.BeiMingService
import com.hxht.techcrt.sync.huigu.HuiGuService
import com.hxht.techcrt.sync.luohu.LuohuService
import com.hxht.techcrt.sync.rongji.RongJiService
import com.hxht.techcrt.sync.shandong.ShanDongService
import com.hxht.techcrt.sync.tongdahai.TongdahaiService
import grails.core.GrailsApplication

class WorkSyncService {

    GrailsApplication grailsApplication
    DongRuanService dongRuanService
    HuiGuService huiGuService
    LuohuService luohuService
    RongJiService rongJiService
    ShanDongService shanDongService
    TongdahaiService tongdahaiService
    BeiMingService beiMingService

    def sync () {
        String work = grailsApplication.config.getProperty('syncData.project_work')
        log.info("[WorkSyncService.sync] 开始执行定时器：${work}")
        long startTime = System.currentTimeMillis()
        if (work.contains(Manufacturer.DONGRUAN)) {
            log.info("同步东软数据，导入一个星期的排期")
            dongRuanService.getAyData()
            log.info("同步东软数据，获取案由完成")
            dongRuanService.getAjlbData()
            log.info("同步东软数据，案件类别审判程序数据完成")
            dongRuanService.getDeptData()
            log.info("同步东软数据，部门数据完成")
            dongRuanService.getCourtRoom()
            log.info("同步东软数据，法庭数据完成")
            dongRuanService.getUserData()
            log.info("同步东软数据，用户数据完成")
            dongRuanService.getCaseAndPlan()
            log.info("同步东软数据，案件排期数据完成")
        }
        if (work.contains(Manufacturer.HUIGU)) {
            huiGuService.synchroDepartment()
            huiGuService.synchroUserInfo()
            huiGuService.synchroPlan()
        }
        if (work.contains(Manufacturer.LUOHU)) {
            luohuService.saveCourtroom()
            luohuService.saveCasePlan()
        }
        if (work.contains(Manufacturer.RONGJI)) {
            rongJiService.startRongji()
        }
        if (work.contains(Manufacturer.SHANDONG)) {
            shanDongService.getCaseAndPlan()
        }
        if (work.contains(Manufacturer.TONGDAHAI)) {
            //1.2.3.初始化webservice服务
            tongdahaiService.initService()
            tongdahaiService.sync()
        }
        if (work.contains(Manufacturer.BEIMING)) {
            log.info("同步北明数据，导入一个星期的排期")
            beiMingService.getDeptForBeiMing()
            log.info("同步北明数据，获取部门完成")
            beiMingService.getUserForBeiMing()
            log.info("同步北明数据，获取人员完成")
            beiMingService.getPlanForBeiMing()
            log.info("同步北明数据，后去排期数据完成")
        }
        long executeTime = (System.currentTimeMillis() - startTime) / 1000
        log.info("[WorkSyncService.sync] 定时器执行完成，耗时：${executeTime}秒")
    }
}
