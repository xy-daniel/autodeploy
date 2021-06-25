package com.hxht.autodeploy.async

import com.hxht.techcrt.court.TrialInfo
import grails.core.GrailsApplication
import grails.gorm.transactions.Transactional

@Transactional
class ApiVideoPlatformService {
    GrailsApplication grailsApplication

    /**
     * 异步执行深圳总点播平台推送排期数据
     * @return
     */
    def planToVideoPlatform(TrialInfo trialInfo) {
        // 目前只有深圳在用总平台系统
        if (grailsApplication.config.getProperty("tc.deployPlace") == "shenzhen") {
            log.info("开始执行向深圳总平台系统推送排期数据接口！trialId为：" +  trialInfo.id)
            def planInfo = trialInfo.planInfo
//            def caseInfo = planInfo.caseInfo
//            def videoInfoList = trialInfo.videoInfo
            //查询排期是否为并案排期
            //这儿可以使用ApiService。getModel或修改后使用
//            def planCaseList = PlanCase.findAllByPlanInfo(planInfo) //后台并案
//            def planInfoCombineList
//            if (planInfo.combinedPlan){
//                planInfoCombineList = PlanInfo.findAllByCombinedPlan(planInfo.combinedPlan) //书记员客户端的并案
//            }
//            都不存在则为非并案排期
//            if (!planCaseList && !planInfoCombineList){
//
//            }
//            值存在的话为庭审后台台处并案
//            if (planCaseList){
//
//            }
//            存在则为书记员客户端并案
//            if (planInfoCombineList){
//
//            }
        }
    }

}
