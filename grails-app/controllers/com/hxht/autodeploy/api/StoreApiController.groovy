package com.hxht.autodeploy.api

import com.hxht.techcrt.DataStatus
import com.hxht.techcrt.Resp
import com.hxht.techcrt.court.Courtroom
import com.hxht.techcrt.court.TrialInfo
import com.hxht.techcrt.court.VideoInfo
import com.hxht.techcrt.enums.RespType
import com.hxht.techcrt.utils.UUIDGenerator
import com.hxht.techcrt.utils.http.RemoteHttpUtil
import grails.converters.JSON
import grails.events.EventPublisher

/**
 * 存储用接口
 * 2021.04.23 >>> 修改远程提讯录像保存方式 daniel
 */
class StoreApiController implements EventPublisher {
    static Object lockVideoInfo = new Object()

    def video() {
        if (request.method == "POST") {
            log.info("[StoreApiController.video] 存储视频信息,参数:params=${params}")
            def trialInfo = TrialInfo.get(params.long("trial"))
            if (!trialInfo) {
                log.info("[StoreApiController.video] 无法关联到庭审记录.")
                render Resp.toJson(RespType.FAIL, "无法关联到庭审记录")
                return
            }
            //获取当前法庭配置信息
            def chnUid = params.chnUid
            def cfg = JSON.parse(trialInfo.courtroom.cfg)
            def current = null //当前解码器
            for (def encode : cfg.encode) {
                if (encode.uuid == chnUid) {
                    current = encode
                }
            }
            if (!current) {
                log.info("[StoreApiController.video] 无法获取本地解码器,以处理远程地址流处理.")
                Courtroom courtroom = trialInfo.courtroom
                def distance = courtroom.distance
                if (distance) {
                    String[] serviceArr = distance.split("///")
                    log.info("[StoreApiController.video] 通道uid:${chnUid}")
                    for (int i=0; i<serviceArr.size(); i++) {
                        log.info("[StoreApiController.video] 服务参数：${serviceArr[i]}")
                        String[] paramsArr = serviceArr[i].split(",")
                        //判断此服务参数是否是service1
                        if (paramsArr[2].split("=")[0] == "status1") {
                            log.info("[StoreApiController.video] 以远程图像1处理")
                            def resp = RemoteHttpUtil.remotePost([
                                    id: paramsArr[1].split("=")[1]
                            ], "${paramsArr[0].split("=")[1]}/api/remoteService/name")
                            if (resp && resp.data.uid as String == chnUid as String) {
                                log.info("[StoreApiController.video] 确认远程图像1通道")
                                current = [
                                        number: "jmtd1",
                                        name  : "远程图像(1)"
                                ]
                                break
                            }
                        }
                        if (paramsArr[2].split("=")[0] == "status2") {
                            log.info("[StoreApiController.video] 以远程图像2处理")
                            def resp = RemoteHttpUtil.remotePost([
                                    id: paramsArr[1].split("=")[1]
                            ], "${paramsArr[0].split("=")[1]}/api/remoteService/name")
                            if (resp && resp.data.uid as String == chnUid as String) {
                                log.info("[StoreApiController.video] 确认远程图像2通道")
                                current = [
                                        number: "jmtd2",
                                        name  : "远程图像(2)"
                                ]
                                break
                            }
                        }
                    }
                    if (!current) {
                        log.info("[StoreApiController.video] 没有找到对应的通道，以远程图像显示.")
                        current = [
                                number: "jmtd",
                                name  : "远程图像"
                        ]
                    }
                } else {
                    log.info("[StoreApiController.video] 没有进行远程连接，此视频以远程图像形式出现")
                    current = [
                            number: "wz",
                            name  : "未知图像"
                    ]
                }
            }
            synchronized (lockVideoInfo){
                def video = new VideoInfo(params)
                video.uid = UUIDGenerator.nextUUID()
                video.channelNum = current.number
                video.channelName = current.name
                video.mediaType = current.interfaceType
                video.resolution = current.vidRes
                video.mediaStreamSize = current.vidMode
                video.active = DataStatus.SHOW
                video.trialInfo = trialInfo
                log.info("[StoreApiController.video] 视频信息:${video}")
                video.save(flush: true)
                if (video.hasErrors()) {
                    log.error("[StoreApiController.video] 保存视频信息出错,错误信息:\n${video.errors}")
                    render Resp.toJson(RespType.FAIL, "保存视频信息出错")
                    return
                }
                //通知事件进行截图
                this.notify("screenShot", video.id)
                //异步向评查系统推送视频地址
                this.notify("videoInfoToVc", video.id, trialInfo.id)
                //通知音频格式转换
                this.notify("audioToVideo", video.id)
                //深圳点播平台推送系统视频点播数据
                this.notify("pushSvVideoData", trialInfo.id, video.id)
                render Resp.toJson(RespType.SUCCESS)
            }
        }
    }
}
