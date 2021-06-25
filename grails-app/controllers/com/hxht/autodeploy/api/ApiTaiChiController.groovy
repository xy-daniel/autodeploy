package com.hxht.autodeploy.api

import cn.hutool.core.date.DateUtil
import com.hxht.techcrt.DataStatus
import com.hxht.techcrt.Dict
import com.hxht.techcrt.PlanStatus
import com.hxht.techcrt.Resp
import com.hxht.techcrt.court.CaseInfo
import com.hxht.techcrt.court.PlanInfo
import com.hxht.techcrt.court.TrialInfo
import com.hxht.techcrt.court.VideoInfo
import com.hxht.techcrt.court.manager.SystemController
import com.hxht.techcrt.court.manager.info.courtroom.CtrlService
import com.hxht.techcrt.enums.RespType
import grails.converters.JSON
import grails.core.GrailsApplication

/**
 * 太极接口
 */
class ApiTaiChiController {
    GrailsApplication grailsApplication
    ApiTaiChiService apiTaiChiService
    CtrlService ctrlService
    
    def verifyHtml() {
        def ah = params.ah
        log.info("请求api/verifyHtml参数ah="+ah)
        Map<String, Object> result = new HashMap<>()
        if (!ah) {
            result.put("code", "failed")
            result.put("msg", RespType.PARAMETER_NULL)
            return result
        }
        result.put("code", "success")
        result.put("msg", "请求成功")
        try {
            def archives = CaseInfo.findByArchives(ah)
            if (archives){
                def planInfo = PlanInfo.findByCaseInfo(archives)
                if (SystemController.currentCourt.ext3 == "J30"){//深圳中院部署
                    def lineDate = DateUtil.parse("2020-08-20 00:00:00", "yyyy-MM-dd HH:mm:ss")
                    if (planInfo.startDate > lineDate){
                        result.put("url", "http://${grailsApplication.config.getProperty('taichi.showVideo.showUrlNew')}/api/livingHtml?ah=" + URLEncoder.encode(ah, "UTF-8"))
                    }else{
                        result.put("url","http://${grailsApplication.config.getProperty('taichi.showVideo.showUrlOld')}/tcs/232/api/LivingHtml?ah=" + URLEncoder.encode(ah, "UTF-8"))
                    }
                }else{
                    result.put("url", "http://${Dict.findByCode("CURRENT_SERVICE_IP").val}:${Dict.findByCode("CURRENT_SERVICE_PORT").val}/tc/api/livingHtml?ah=" + URLEncoder.encode(ah, "UTF-8"))
                }
            }else{
                result.put("url","http://${grailsApplication.config.getProperty('taichi.showVideo.showUrlOld')}/tcs/232/api/LivingHtml?ah=" + URLEncoder.encode(ah, "UTF-8"))
            }

        } catch (UnsupportedEncodingException e) {
            log.error("url encode failed")
            result.put("code", "failed")
            result.put("msg", "url encode failed")
            e.printStackTrace()
        }
        render result as JSON
    }

    def show() {
        def trials = TrialInfo.get(params.long("trial")) //指定了trial进行播放
        if (trials){
            def info = showByTrial(trials)
            return info
        }
        def ah = params.ah as String
        log.info("请求api/LivingHtml参数ah="+ah)
        if (!ah) {
            log.error("太极接口请求参数案号为空，并返回")
            return
        }
        try {
            //查询案号
            def caseInfo
            if (ah.indexOf("（") != -1){
                caseInfo = CaseInfo.findByArchives(ah)
                if (!caseInfo){
                    ah = ah.replace("（","(")
                    ah = ah.replace("）",")")
                    caseInfo = CaseInfo.findAllByArchives(ah)
                }
            }else {
                caseInfo = CaseInfo.findByArchives(ah)
                if (!caseInfo){
                    ah = ah.replace("(","（")
                    ah = ah.replace(")","）")
                    caseInfo = CaseInfo.findAllByArchives(ah)
                }
            }
            if (!caseInfo) {
                log.info("请求失败！, 根据案号没有查询到对应的案件信息.")
                return
            }

            def chnList = []
            List<VideoInfo> videoList
            List status = new ArrayList()
            status.add(PlanStatus.CLOSED)
            status.add(PlanStatus.ADJOURN)
            status.add(PlanStatus.ARCHIVED)
            def planInfoList = PlanInfo.findAllByCaseInfoAndStatusInList(caseInfo, status)
            for (def planInfo : planInfoList){
                def trialInfoList = TrialInfo.findAllByPlanInfoAndActive(planInfo, DataStatus.SHOW, [sort: "status", order: "asc"])
                for (def trialInfo : trialInfoList){
                    def videoInfoList = VideoInfo.findAllByTrialInfo(trialInfo)
                    //遍历所有的videoInfo显示所有这个庭审的通道号和通道名称
                    for (def video:videoInfoList){
                        chnList.add([
                                number : video.channelNum,
                                name: video.channelName
                        ])
                    }
                }
            }
            //s使用hashset去掉重复
            def set = new HashSet<>(chnList)
            chnList = new LinkedList<>(set)            
            
            def planInfo = planInfoList.get(0)
            def trialInfo = TrialInfo.findByPlanInfo(planInfo)
            if (!planInfo.courtroom.cfg) {
                planInfo.courtroom.cfg = ctrlService.getCfg(planInfo.courtroom) as JSON
            }
            def cfg = JSON.parse(planInfo.courtroom.cfg)
            def chnListOrder = []
            //直播通道
            for (def encode : cfg.encode) {
                for (def chn: chnList){
                    if (chn.name == encode.name && chn.number == encode.number){
                        chnListOrder.add([
                                number: encode.number,
                                name  : encode.name
                        ])
                    }
                }
            }
            for (def chn: chnList){
                def flag = 0
                for (def chnOrder: chnListOrder){
                    if (chnOrder.name == chn.name && chnOrder.number == chn.number){
                        flag = 1
                    }
                }
                if (flag == 0){
                    chnListOrder.add([
                            number: chn.number,
                            name  : chn.name
                    ])
                }
            }
            
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
                    collegial   : planInfo.collegial.size() == 0?"无数据":planInfo.collegial,
                    secretary   : trialInfo?.secretary ?: planInfo.secretary,
                    summary     : planInfo.caseInfo.summary ?: "无数据",
                    detail      : planInfo.caseInfo.detail ?: "无数据",
                    status      : trialInfo?.status ?:planInfo.status,
                    allowPlay   : planInfo.allowPlay,
                    judge       : planInfo.judge?.name ?: "无数据"
            ]
            def trialList = apiTaiChiService.getTrialVideoList(planInfo)
            [data: data, chnList: chnListOrder, trialList: trialList]
        } catch (Exception e) {
            log.error("apiTaiChiController请求失败,错误信息: ${e.getMessage()}")
            log.error("请求失败，错误信息: ${e.getMessage()}.")
        }
    }

    def showVideo(){
        def trialInfo = TrialInfo.get(params.long("id"))//有视频的话一定会有trial
        if(trialInfo){
            def video = apiTaiChiService.showVideo(trialInfo)
            return render(Resp.toJson(RespType.SUCCESS, [status: trialInfo.status, video: video]))
        }
        render Resp.toJson(RespType.FAIL)
    }

    def showByTrial(TrialInfo trialInfo) {
        def planInfo = trialInfo.planInfo
        def chnList = []
        //根据trial查询出所有的videoInfo
        def videoInfoList = VideoInfo.findAllByTrialInfo(trialInfo)
        //遍历所有的videoInfo显示所有这个庭审的通道号和通道名称
        for (def video:videoInfoList){
            chnList.add([
                    number : video.channelNum,
                    name: video.channelName
            ])
        }
        //s使用hashset去掉重复
        def set = new HashSet<>(chnList)
        chnList = new LinkedList<>(set)
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
                collegial   : planInfo.collegial.size() == 0?"无数据":planInfo.collegial,
                secretary   : trialInfo?.secretary ?: planInfo.secretary,
                summary     : planInfo.caseInfo.summary ?: "无数据",
                detail      : planInfo.caseInfo.detail ?: "无数据",
                status      : trialInfo?.status ?:planInfo.status,
                allowPlay   : planInfo.allowPlay,
                judge       : planInfo.judge?.name ?: "无数据"
        ]
        def trialList = apiTaiChiService.getTrialVideoList(planInfo)
        [data: data, chnList: chnList, trialList: trialList]
    }
}
