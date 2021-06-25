package com.hxht.autodeploy.api

import com.hxht.techcrt.CourtRemoteStatus
import com.hxht.techcrt.CourtroomStatus
import com.hxht.techcrt.Resp
import com.hxht.techcrt.enums.RespType
import com.hxht.techcrt.court.Courtroom
import com.hxht.techcrt.utils.IpUtil
import com.hxht.techcrt.utils.ManagerHostApiUtil
import com.hxht.techcrt.utils.CtrlCommandUtil
import grails.converters.JSON
import org.grails.web.json.JSONObject

/**
 * 多方远程服务端接口 created by daniel in 2020.01.01
 * 2021.04.19 >>> 修改远程提讯策略 daniel
 * 2021.04.21 >>> 添加获取法庭名称接口 daniel
 * 2021.04.29 >>> 增加远程提讯不支持时的状态 daniel
 */
class RemoteServiceController {

    /**
     * 自己的系统调用自己的系统，传输参数判断法庭是否可用
     * 选择法庭之后判断该法庭现在是否可用
     */
    def isCourtOccupied() {
        try {
            def params = request.getParameterValues("params") as JSONObject
            log.info("RemoteServiceController isCourtOccupied param=${params}")
            //获取该远程法庭
            Courtroom courtroom = Courtroom.get(params.get("id") as long)
            //如果此远程法庭不是正常状态
            if (!courtroom) {
                log.info("[RemoteServiceController.isCourtOccupied]---->服务端法庭不存在")
                render Resp.toJson(RespType.SERVICE_COURT_NOT_EXIST)
                return
            }
            if (courtroom.status != CourtroomStatus.NORMAL) {
                log.info("[RemoteServiceController.isCourtOccupied]---->服务端法庭被占用")
                render Resp.toJson(RespType.COURT_OCCUPIED)
                return
            }
            if (!(courtroom.rtsp && courtroom.rtsp1)) {
                log.info("[RemoteServiceController.isCourtOccupied]---->服务端法庭流地址不完整")
                render Resp.toJson(RespType.RTSP_NOT_ALLOW)
                return
            }
            log.info("[RemoteServiceController.isCourtOccupied]---->服务端法庭可用")
            render Resp.toJson(RespType.SUCCESS)
        } catch (e) {
            e.printStackTrace()
            log.error("[RemoteServiceController.isCourtOccupied]---->书记员判断远端法庭现在是否可用时出错${e.message}")
            render Resp.toJson(RespType.COURT_OCCUPIED)
        }
    }

    /**
     * 开始远程链接----判断应该是哪个解码通道
     */
    def startConnect() {
        try {
            log.info("[RemoteServiceController.startConnect]---->服务端开始执行远程连接")
            def params = request.getParameterValues("params") as JSONObject
            log.info("[RemoteServiceController.startConnect]---->客户端参数params=${params}")
            //客户端视频流地址
            def url = params.get("url") as String
            //通道
            def num = params.get("num") as Integer
            //服务端法庭
            def courtroom = Courtroom.get(params.get("id") as long)
            if (!courtroom) {
                log.info("[RemoteServiceController.startConnect]---->开始远程连接时没有找到对应的远程法庭")
                render Resp.toJson(RespType.START_FAIL)
                return
            }
            //获取服务端法庭第num个解码通道
            def getDecoderProfilesRs = ManagerHostApiUtil.getDecoderProfiles("http://${courtroom.deviceIp}", num)
            def setDecoderProfileRs = -1
            if (!getDecoderProfilesRs) {
                log.info("tcp主机${courtroom.deviceIp}")
                log.info("[RemoteServiceController.startConnect]---->执行旧版本数据，设置到dec${num}")
                //执行旧版本数据----设置到dec0的解码器上
                setDecoderProfileRs = ManagerHostApiUtil.setDecodeStreamUrl("dec${num}.stream_url=${url}\ndec${num}.start_rtsp=1\ndec${num}.rtspMode=tcp\ndec${num}.aud_mode=right\nnet_recv${num + 1}.cache=500\n", courtroom.deviceIp)
            } else {
                log.info("[RemoteServiceController.startConnect]---->通道token:${getDecoderProfilesRs.token},通道profileDescription:${getDecoderProfilesRs.profileDescription}")
                setDecoderProfileRs = ManagerHostApiUtil.setDecoderProfile("http://" + courtroom.deviceIp, getDecoderProfilesRs.token, getDecoderProfilesRs.profileDescription, true, url)
            }
            if (setDecoderProfileRs == 0) {
                CtrlCommandUtil.ctrlCommand(courtroom.deviceIp, 8060, "TV0-DECODE${num + 1}.")
                log.info("[RemoteServiceController.startConnect]---->设置成功")
                courtroom.remote = CourtRemoteStatus.REMOTE
                courtroom.status = CourtroomStatus.OCCUPIED
                courtroom.save(flush: true)
                log.info("[RemoteServiceController.startConnect]---->服务端开始远程连接成功")
                render Resp.toJson(RespType.SUCCESS)
            } else {
                if (setDecoderProfileRs == 404) {
                    log.info("[RemoteServiceController.startConnect]---->服务端庭审主机不支持")
                    render Resp.toJson(RespType.DEVICE_NOT_SUPPORT)
                } else {
                    log.info("[RemoteServiceController.startConnect]---->开始远程连接时设置解码器失败")
                    render Resp.toJson(RespType.START_FAIL)
                }
            }
        } catch (e) {
            e.printStackTrace()
            log.error("[RemoteServiceController.startConnect]---->开始远程连接时出现异常${e.message}")
            render Resp.toJson(RespType.START_FAIL)
        }
    }

    /**
     * 断开远程连接
     */
    def stopConnect() {
        try {
            //获取客户端参数
            def params = request.getParameterValues("params") as JSONObject
            log.info("[RemoteServiceController.stopConnect] ip为${request.remoteAddr}的A服务器请求断开远程连接,参数:params=${params}")
            //获取此法庭
            def courtroom = Courtroom.get(params.get("id") as long)
            //ping庭审主机,能ping通执行下面的庭审主机操作，否则只执行法庭复位操作
            String ping = "庭审主机未开启"
            if (IpUtil.ping(courtroom.deviceIp)) {
                def num = params.get("num") as Integer
                def getDecoderProfilesRs = ManagerHostApiUtil.getDecoderProfiles("http://${courtroom.deviceIp}", num)
                def setDecoderProfileRs = -1
                if (!getDecoderProfilesRs) {
                    setDecoderProfileRs = ManagerHostApiUtil.setDecodeStreamUrl("dec${num}.stream_url=\ndec${num}.start_rtsp=0\n", courtroom.deviceIp)
                } else {
                    setDecoderProfileRs = ManagerHostApiUtil.setDecoderProfile("http://${courtroom.deviceIp}", getDecoderProfilesRs.token, getDecoderProfilesRs.profileDescription, false, "")
                }
                if (setDecoderProfileRs != 0) {
                    log.info("[RemoteServiceController.stopConnect] 服务端断开远程连接时清除解码器失败")
                    render Resp.toJson(RespType.STOP_FAIL)
                    return
                }
                //法庭设置为远程状态并成功---->失败会产生异常进Catch
                CtrlCommandUtil.ctrlCommand(courtroom.deviceIp, 8060, "TV0-HCHM.")
                ping = ""
            }
            courtroom.status = CourtroomStatus.NORMAL
            courtroom.save(flush: true)
            log.info("[RemoteServiceController.stopConnect] 服务端断开远程连接成功")
            render Resp.toJson(RespType.SUCCESS, ping)
        } catch (e) {
            e.printStackTrace()
            log.error("[RemoteServiceController.stopConnect]---->断开远程连接时出现异常${e.message}")
            render Resp.toJson(RespType.STOP_FAIL)
        }
    }

    /**
     * 客户端失败时的回调函数
     */
    def recovery() {
        //获取客户端参数
        def params = request.getParameterValues("params") as JSONObject
        log.info("[RemoteServiceController.recovery] B服务端恢复法庭状态,参数:params=${params}.")
        //获取此法庭
        def courtroom = Courtroom.get(params.get("id") as long)
        courtroom.status = CourtroomStatus.NORMAL
        courtroom.save(flush: true)
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 为A端书记员软件提供法庭接口
     */
    def courtroom() {
        log.info("[RemoteServiceController.courtroom] ip为${request.remoteAddr}的书记员客户端开始获取远程法庭.")
        def courtroomList = Courtroom.findAllByIsCalled(1, [sort: "sequence", order: "esc"])
        def data = []
        courtroomList.each {
            data.add([
                    id    : it.id,
                    name  : it.name,
                    rtsp1 : it.rtsp,
                    rtsp2 : it.rtsp1,
                    status: it.status == CourtroomStatus.NORMAL && it.isCalled == 1 ? "可用" : "占用"
            ])
        }
        log.info("[RemoteServiceController.courtroom] 返回法庭参数:${data as JSON}.")
        render Resp.toJson(RespType.SUCCESS, data)
    }

    /**
     * A服务器获取B服务器法院法庭名称
     */
    def courtroomName() {
        //获取客户端参数
        def params = request.getParameterValues("params") as JSONObject
        log.info("[RemoteServiceController.courtroomName] ip为${request.remoteAddr}的A服务器获取法庭名称,参数:params=${params}.")
        //获取此法庭
        def courtroom = Courtroom.get(params.get("id") as long)
        log.info("[RemoteServiceController.courtroomName] 返回A服务器法庭名称:${courtroom.name}.")
        def data = [uid: courtroom.uid, name: courtroom.name, rtsp1: courtroom.rtsp, rtsp2: courtroom.rtsp1]
        render Resp.toJson(RespType.SUCCESS, data)
    }
}
