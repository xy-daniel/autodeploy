package com.hxht.autodeploy.api

import cn.hutool.core.codec.Base32
import com.hxht.techcrt.DataStatus
import com.hxht.techcrt.PlanStatus
import com.hxht.techcrt.court.CaseInfo
import com.hxht.techcrt.court.PlanInfo
import com.hxht.techcrt.court.PlanTrial
import com.hxht.techcrt.court.TrialInfo
import com.hxht.techcrt.court.VideoInfo
import com.hxht.techcrt.court.manager.info.courtroom.CtrlService
import grails.converters.JSON
import grails.core.GrailsApplication
import grails.gorm.transactions.Transactional
import grails.web.servlet.mvc.GrailsParameterMap

/**
 *  2021.05.10 >>> 为东软提供直播点播接口 daniel
 */
@Transactional
class ShowVideoService {

    GrailsApplication grailsApplication
    CtrlService ctrlService

    /**
     * 直播服务
     */
    def zhibo(GrailsParameterMap params) {
        //获取排期,排期必须存在（首先根据id获取,没有id根据案号获取）
        def planInfo = PlanInfo.findByCaseInfo(CaseInfo.findByArchives(Base32.decodeStr(params.get("ah")).trim()))
        //排期不存在,无法直播,直接返回排期不存在
        if (!planInfo) {
            return "排期不存在"
        }
        if (planInfo.trialInfo.size() == 0) {
            return "庭次不存在"
        }

        //获取法庭
        def courtroom = planInfo.courtroom
        //判断法庭是否存在cfg,不存在则赋予默认值
        if (!courtroom.cfg) {
            courtroom.cfg = ctrlService.getCfg(courtroom) as JSON
        }
        def cfg = JSON.parse(courtroom.cfg)
        //视频列表
        def videoInfoList = new ArrayList<VideoInfo>()
        def flag = false
        //获取庭次
        def trialInfo
        //排期处于开庭状态,获取正在开庭的庭次
        if (planInfo.status == PlanStatus.SESSION) {
            flag = true
            trialInfo = TrialInfo.findByPlanInfoAndStatusAndActive(planInfo, PlanStatus.SESSION, DataStatus.SHOW)
            //排期不是开庭状态
        } else if (planInfo.status == PlanStatus.ADJOURN || planInfo.status == PlanStatus.CLOSED || planInfo.status == PlanStatus.ARCHIVED) {
            trialInfo = TrialInfo.findAllByPlanInfoAndActive(planInfo, DataStatus.SHOW).last()
            videoInfoList = VideoInfo.findAllByTrialInfo(trialInfo)
        }
        def chnList = getAllChannel(cfg)
        if (!flag) {
            chnList = getChannel(chnList, videoInfoList)
        }
        def data = [
                planId      : planInfo.caseInfo.archives,
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
                judge       : planInfo.judge?.name ?: "无数据"
        ]
        [data: data, chnList: chnList, trialList: getTrialVideoList(planInfo)]
    }

    /**
     * 点播服务
     */
    def dianbo(GrailsParameterMap params) {
        //获取庭次
        def tc = params.int("tc")
        //庭次不存在设置默认值为1
        if (!tc) {
            tc = 1
        }
        log.info("案号:${Base32.decodeStr(params.get("ah")).trim()},结束.")
        //获取排期
        def planInfo = PlanInfo.findByCaseInfo(CaseInfo.findByArchives(Base32.decodeStr(params.get("ah")).trim()))
        //既然是点播,在这儿就必须有排期,排期不存在则返回排期不存在.
        if (!planInfo) {
            return "排期不存在"
        }
        //获取trialInfo,在网页点播
        def trialInfo
        try {
            //根据排期获取庭次
            trialInfo = TrialInfo.get(getTrialVideoList(planInfo).get((tc as int) - 1)["id"] as long)
        } catch (IndexOutOfBoundsException exception) {
            //数组
            exception.printStackTrace()
            return "庭次不存在"
        }
        //获取法庭
        def courtroom = planInfo.courtroom
        //判断法庭是否存在cfg,不存在则赋予默认值
        if (!courtroom.cfg) {
            courtroom.cfg = ctrlService.getCfg(courtroom) as JSON
        }
        def cfg = JSON.parse(courtroom.cfg)
        //视频列表
        def videoInfoList = VideoInfo.findAllByTrialInfo(trialInfo)
        def chnList = getChannel(getAllChannel(cfg), videoInfoList)
        def data = [
                planId      : planInfo.id,
                trialId     : trialInfo.id ?: "",
                caseArchives: planInfo.caseInfo.archives,
                caseName    : planInfo.caseInfo.name,
                caseType    : planInfo.caseInfo.type,
                courtroom   : trialInfo.courtroom ?: planInfo.courtroom,
                accuser     : planInfo.caseInfo.accuser ?: "无数据",
                accused     : planInfo.caseInfo.accused ?: "无数据",
                filingDate  : planInfo.caseInfo.filingDate?.format('yyyy/MM/dd HH:mm'),
                startDate   : trialInfo.startDate?.format('yyyy/MM/dd HH:mm') ?: planInfo.startDate?.format('yyyy/MM/dd HH:mm'),
                endDate     : trialInfo.endDate?.format('yyyy/MM/dd HH:mm') ?: planInfo.endDate?.format('yyyy/MM/dd HH:mm'),
                collegial   : planInfo.collegial.size() == 0 ? "无数据" : planInfo.collegial,
                secretary   : trialInfo.secretary ?: planInfo.secretary,
                summary     : planInfo.caseInfo.summary ?: "无数据",
                detail      : planInfo.caseInfo.detail ?: "无数据",
                status      : trialInfo.status ?: planInfo.status,
                allowPlay   : planInfo.allowPlay,
                judge       : planInfo.judge?.name ?: "无数据"
        ]
        [data: data, chnList: chnList, trialList: getTrialVideoList(planInfo)]
    }

    /**
     * 获取所有通道
     * @param cfg 法庭配置
     * @return 所有解码通道
     */
    def getAllChannel(def cfg) {
        def chnList = []
        for (def encode : cfg.encode) {
            chnList.add([
                    number: encode.number,
                    name  : encode.name
            ])
        }
        return chnList
    }

    /**
     * 获取视频通道
     * @param chnList 所有解码通道
     * @param videoInfoList 所有视频列表
     * @return 所有视频通道
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
     * 根据排期获取庭审与视频信息
     * @param planInfo
     */
    def getTrialVideoList(PlanInfo planInfo) {
        List<TrialInfo> trialInfoList = []
        if (planInfo.combinedPlan) {//存在并案的排期
            def planTrialList = PlanTrial.findAllByPlanInfo(planInfo)//获取所有排期对应的庭审
            for (PlanTrial planTrial : planTrialList) { //将庭审放入到集合
                trialInfoList.add(planTrial.trialInfo)
            }
        } else {//不存在并案的情况
            trialInfoList = TrialInfo.findAllByPlanInfoAndActive(planInfo, DataStatus.SHOW)
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
                    ah       : Base32.encode(ti.planInfo.caseInfo.archives),
                    tc       : trialInfoList.indexOf(ti) + 1,
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
        def planInfo = trialInfo.planInfo
        def courtroom = planInfo.courtroom
        def cfg = JSON.parse(courtroom.cfg)
        def videoList = [] //最后返回结果集
        if (trialInfo.status == PlanStatus.SESSION) {
            for (def encode : cfg.encode) {
                def url = "http://${courtroom.liveIp}:8791/${encode.encodeip}/${encode.number}.flv"
                videoList.add([
                        number: encode.number,
                        name  : encode.name,
                        url   : url
                ])
            }
        } else {
            def allChannel = getAllChannel(cfg)
            def videoChannelList = getChannel(allChannel, VideoInfo.findAllByTrialInfo(trialInfo))
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
                }
                videoMap.put("videoUrl", trialVideoList)
                videoMap.put("totalLength", totalLength)
                videoList.add(videoMap)
            }
        }
        videoList
    }
}
