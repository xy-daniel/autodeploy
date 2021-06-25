package com.hxht.autodeploy.remote

import com.hxht.techcrt.CourtroomStatus
import com.hxht.techcrt.court.Courtroom
import com.hxht.techcrt.utils.CtrlCommandUtil
import com.hxht.techcrt.court.manager.info.courtroom.CourtroomService
import com.hxht.techcrt.enums.RespType
import com.hxht.techcrt.utils.ManagerHostApiUtil
import grails.gorm.transactions.Transactional
import org.grails.cxf.utils.GrailsCxfEndpoint

import javax.jws.WebMethod
import javax.jws.WebParam
import javax.jws.WebResult
import javax.jws.soap.SOAPBinding

/**
 * 深圳多方远程庭审服务提供接口  by Arctic in 2019.12.10
 * 2021.05.20 >>> 高院调用中院进行远程提讯代码更新 daniel
 */
@Transactional
@SOAPBinding(style = SOAPBinding.Style.RPC)
@GrailsCxfEndpoint
class IRemoteTrailService {

    CourtroomService courtroomService
    /**
     * 判断法庭是否被占用
     * @param ycTssId 远程庭室ID，接口根据此参数返回此庭室占用状态
     * @param bdTssName 本端庭室名称
     * @param bdFyName 本端法院名称
     * @return 成功 ：<Result Version="1.0" code="success"></Result>
     *         失败 ：<Result Version="1.0" code="error" message="法庭被占用"></Result>
     */
    @WebMethod
    @WebResult
    String isCourtOccupied(@WebParam(targetNamespace = "http://com.hxht.techcrt.ws") String param) {
        log.info("IRemoteTrailService isCourtOccupied param=\n${param}")
        def xml  = new XmlParser().parseText(param)
        def ycTssId = xml.YcTssid.text()
        def courtroom = courtroomService.getCourtroom(ycTssId)
        if (courtroom!=null && courtroom.status== CourtroomStatus.NORMAL){
            return "<Result Version=\"1.0\" code=\"success\"></Result>"
        }
        log.info(RespType.COURT_OCCUPIED.msg)
        return "<Result Version=\"1.0\" code=\"error\" message=\"${RespType.COURT_OCCUPIED.msg}\"></Result>"
    }

    /**
     * 开始远程连接---->客户端调用这个方法传输流信息，服务方进行解码拉流
     * @param BdFyName 本端法院名称
     * @param BdTssid 本端庭室id
     * @param BdTssName 本端庭室名称
     * @param YcFyname 远端法院名称
     * @param YcTssname 远程庭室名称
     * @param YcTssid 远程庭室id
     * @param YcSxdyId 时序电源ID，默认不存在此设备
     * @param BdTd 本端通道----4个下级属性
     *      @param ID 通道ID
     *      @param Mc 通道名称
     *      @param URL 通道URL
     *      @param Xylx 协议类型，rtsp/h323
     * @return  成功 ：<Result Version="1.0" code="success"></Result>
     *          失败 ：<Result Version="1.0" code="error" message="连接失败，失败原因XXX "></Result>
     */
    @WebMethod
    @WebResult
    def startDecode(@WebParam(targetNamespace = "http://com.hxht.techcrt.ws") String param){
        try {
            log.info("IRemoteTrailService startDecode param=\n${param}")
            def xml  = new XmlParser().parseText(param)
            def ycTssId = xml.YcTssid.text()                    //远程庭室id
            def url = xml.BdTd.URL.text()                       //通道URL---->码流
            def courtroom = Courtroom.get(ycTssId as Long)
            def getDecoderProfilesRs = ManagerHostApiUtil.getDecoderProfiles("http://${courtroom.deviceIp}", 0)
            def setDecoderProfileRs = ManagerHostApiUtil.setDecoderProfile("http://${courtroom.deviceIp}",getDecoderProfilesRs.token, getDecoderProfilesRs.profileDescription, true, url)
            if (setDecoderProfileRs==0){
                def startDecodeStatus = courtroomService.startDecode(ycTssId)
                if (startDecodeStatus){
                    CtrlCommandUtil.ctrlCommand(courtroom.deviceIp, 8060, "TV0-DECODE1.")
                    return "<Result Version=\"1.0\" code=\"success\"></Result>"
                }else{
                    log.info(RespType.START_COURT_STATUS_ERROR.msg)
                    return "<Result Version=\"1.0\" code=\"error\" message=\"${RespType.START_COURT_STATUS_ERROR.msg}\"></Result>"
                }
            }else{
                log.info(RespType.DECODE_ERROR.msg)
                return "<Result Version=\"1.0\" code=\"error\" message=\"${RespType.DECODE_ERROR.msg}\"></Result>"
            }
        }catch(Exception e){
            e.printStackTrace()
            throw new RuntimeException()
        }
    }

    /**
     * 断开远程连接---->客户端调用这个方法清空流信息
     * @param BdFyName 本端法院名称
     * @param BdTssid 本端庭室id
     * @param BdTssName 本端庭室名称
     * @param YcFyname 远程法院名称
     * @param YcTssname 远程庭室名称
     * @param YcTssid 本端庭室id
     * @param Protocol 使用协议（rtsp/h323）
     * @return 成功 ：<Result Version="1.0" code="success"></Result>
     *         失败 ：<Result Version="1.0" code="error" message="断开失败，失败原因XXX "></Result>
     */
    @WebMethod
    @WebResult
    def stopDecode(@WebParam(targetNamespace = "http://com.hxht.techcrt.ws") String param){
        try {
            log.info("IRemoteTrailService stopDecode param=\n${param}")
            def xml  = new XmlParser().parseText(param)
            def ycTssId = xml.YcTssid.text()
            //获取此法庭
            def courtroom = Courtroom.get(ycTssId as Long)
            //庭审主机
            def getDecoderProfilesRs = ManagerHostApiUtil.getDecoderProfiles("http://${courtroom.deviceIp}", 0)
            def setDecoderProfileRs = ManagerHostApiUtil.setDecoderProfile("http://${courtroom.deviceIp}", getDecoderProfilesRs.token, getDecoderProfilesRs.profileDescription, false, "")
            if (setDecoderProfileRs==0){
                def stopDecodeStatus = courtroomService.stopDecode(ycTssId)
                if (stopDecodeStatus){
                    CtrlCommandUtil.ctrlCommand(courtroom.deviceIp, 8060, "TV0-HCHM.")
                    return "<Result Version=\"1.0\" code=\"success\"></Result>"
                }else{
                    log.info(RespType.STOP_FAIL.msg)
                    return "<Result Version=\"1.0\" code=\"error\" message=\"${RespType.STOP_FAIL.msg}\"></Result>"
                }
            }else{
                log.info(RespType.CLEAR_DECODE_ERROR.msg)
                return "<Result Version=\"1.0\" code=\"error\" message=\"${RespType.CLEAR_DECODE_ERROR.msg}\"></Result>"
            }
        } catch(Exception e) {
            e.printStackTrace()
            throw new RuntimeException()
        }
    }
}
