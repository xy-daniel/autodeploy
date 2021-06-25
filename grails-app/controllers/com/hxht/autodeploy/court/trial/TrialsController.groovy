package com.hxht.autodeploy.court.trial

import com.hxht.techcrt.Resp
import com.hxht.techcrt.court.TrialInfo
import com.hxht.techcrt.court.VideoInfo
import com.hxht.techcrt.court.mem.CdBurning
import com.hxht.techcrt.enums.RespType
import com.hxht.techcrt.utils.IpUtil
import com.hxht.techcrt.utils.http.HttpUtil
import grails.converters.JSON
import grails.gorm.transactions.Transactional

class TrialsController {
    
    TrialsService trialsService
    /**
     * 庭审trial列表
     */
    def list() {
        if (request.method == "GET") {
            return trialsService.planListQueryData()
        }

        if (request.method == "POST") {
            def model = trialsService.trial(params)
            render model as JSON
        }
    }

    /**
     * 根据庭审trial 获取对应的通道数量
     */
    def getChn() {
        if (request.method == "GET") {
            def trialId = params.long("trialId")
            if (!trialId){
                def msg = "获取对应的通道数量getChn接口请求的trialId为空"
                log.error(msg)
                render Resp.toJson(RespType.FAIL, msg)
                return
            }
            def trialInfo = TrialInfo.get(trialId)
            render Resp.toJson(RespType.SUCCESS, trialsService.getChn(trialInfo))
        }
    }
    
    /**
     * 向刻录服务器发送刻录指令
     */
    @Transactional("mem")
    def postConburn() {
        if (request.method == "POST") {
            try {
                def trialId = params.long("trialId")
                def burnNum = params.int("burnNum")
                def chnName = params.get("chnName")
                if (!trialId || !burnNum || !chnName){
                    return
                }
                def trialInfo = TrialInfo.get(trialId)
                def videoInfoList = VideoInfo.findAllByTrialInfoAndChannelName(trialInfo,chnName)
                def filelist = []
                for (def video: videoInfoList){
                    filelist.add("http://${trialInfo.courtroom.storeIp}:8200/${video.fileName}")
                }
                def burnCmd = [
                        copy     : burnNum,
                        filelist : filelist,
                        noteUrl  : grailsLinkGenerator.link(uri: "/api/client/trial/down/note/${trialId}", absolute: true),
                        notename : trialInfo.note ? trialInfo.note.substring(trialInfo.note?.lastIndexOf('/') + 1) : null,
                        planname : trialInfo.planInfo.caseInfo.archives,
                        startDate: trialInfo.startDate?.format('yyyy/MM/dd HH:mm:ss')
                ]
                //ping刻录服务器是否在线 在线的话进行发送指令
                def cdBurnList = CdBurning.findAll([sort: "orderNum", order: "asc"])
                render Resp.toJson(RespType.SUCCESS)
                return
                for (def cdBurn : cdBurnList) {
                    //如果服务器可以ping通 则进行发送通知
                    if (IpUtil.ping(cdBurn.url.substring(7,cdBurn.url.lastIndexOf(":")))){
                        def result = HttpUtil.simplePost(cdBurn.url, burnCmd)
                        if (result.contains("刻录请求成功")){
                            render Resp.toJson(RespType.SUCCESS)
                            return 
                        }
                    }
                }
            } catch (e) {
                e.printStackTrace()
                log.error("[TrialsController.postConburn] 向刻录服务器发送刻录指令时出错,错误信息:${e.message}")
                render Resp.toJson(RespType.FAIL)
                return
            }
        }
    }
}
