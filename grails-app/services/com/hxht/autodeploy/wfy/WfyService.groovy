package com.hxht.autodeploy.wfy

import com.hxht.techcrt.Dict
import com.hxht.techcrt.court.PlanInfo
import com.hxht.techcrt.court.TrialInfo
import com.hxht.techcrt.utils.WfyRestUtil
import com.hxht.techcrt.utils.WordUtil
import grails.converters.JSON
import grails.core.GrailsApplication
import grails.gorm.transactions.Transactional
import grails.util.Holders
import org.grails.web.json.JSONArray

/**
 * 对接中国移动微法院互联网开庭平台
 * 科技法庭庭审系统，作为Client端，发送Http请求消息到互联网开庭管理平台
 *
 */
@Transactional
class WfyService {
    GrailsApplication grailsApplication
    /**
     * 庭审网关标识
     */
    private static String MOBILE_PHONE
    /**
     * 控制是否执行微法院代码开官
     */
    private static Integer FLAG

    WfyService() {
        def grailsApplication = Holders.grailsApplication
        MOBILE_PHONE = grailsApplication.config.getProperty('weifayuan.interface.username')
        FLAG = grailsApplication.config.getProperty('weifayuan.flag') as Integer
    }
    /**
     * 5.1.1.案件排期创建庭审
     * 科技法庭庭审系统、互联网开庭管理平台，都从审判系统获取排期。
     * 根据案号创建庭审
     */
    @Deprecated
    def createMediationMeeting() {
        if (FLAG == 1) {

        }
    }
    /**
     * 5.1.2.拉取庭审网关入会
     * 法官在正式开庭前发起“预调试”操作进行设备调试时，庭审系统调用本接口。
     * 	在开庭操作前的一段时间内，可调用本接口。建议20分钟。
     * 	开庭管理平台收到接口请求后，向庭审网关发送WS通知；庭审网关收到WS通知后加入会议中。
     * 	可以发起多次对本接口的调用
     * 输入参数
     *{*   "courtCode":"15023",      法院代码
     *   "caseNumberCode":"",     内网案号
     *   "caseOrder":"1",           庭次
     *   "mobilePhone": "15930680000"，   庭审网关标识
     *   "reverseUrlPrefix":"http://ip:端口/应用名"   通知URL的前缀
     *}*/
    def putIntoRoom(PlanInfo planInfo) {
        if (FLAG == 1) {
            def url = "/peaceIm/intranet/putIntoRoom"
            def caseInfo = planInfo.caseInfo
            def archives = caseInfo.archives
            def reverseUrlPrefix = Dict.findByCode("CURRENT_SERVICE_PATH").val
            def courtCode = Dict.findByCode("CURRENT_COURT").val

            planInfo.wfyPlan = 1
            planInfo.save(flush:true)
            if(planInfo.hasErrors()){
                //将排期设为微法院排期失败
                log.error("[WfyService]将排期设为微法院排期失败，方法[putIntoRoom]，errors[${planInfo.errors}],排期id[${planInfo.id}]")
                return false
            }
            def planList = PlanInfo.findAllByCaseInfo(caseInfo, [sort: "dateCreated", order: "asc"])//根据创建时间模拟庭次
            def caseOrder = 0
            for (def p : planList) {
                caseOrder++
                if (p.id == planInfo.id) {
                    break
                }
            }
            def reqModel = [
                    courtCode       : courtCode,
                    caseNumberCode  : archives,
                    caseOrder       : "${caseOrder}",
                    mobilePhone     : MOBILE_PHONE,
                    reverseUrlPrefix: reverseUrlPrefix
            ]
            def resp = WfyRestUtil.post(url, reqModel)
            if (resp?.code == 1000) {//成功
                log.info("[WfyService]发送http请求成功，方法[putIntoRoom]，请求数据[${reqModel as JSON}]，返回信息[${resp?.json}]")
                return true
            } else {
                log.error("[WfyService]发送http请求失败，方法[putIntoRoom]，请求数据[${reqModel as JSON}]，返回信息[${resp?.json}]")
                return false
            }
        }

    }
    /**
     * 5.1.3.开庭
     * 法官发起开庭操作时，庭审系统调用本接口。
     *{*   "courtCode":"15023",      法院代码
     *   "caseNumberCode":"",     内网案号
     *   "caseOrder":"1",           庭次
     *   "reverseUrlPrefix":"http://ip:端口/应用名"   通知URL的前缀
     *}*/
    def holdCourt(Long trialInfoId) {
        if (FLAG == 1) {
            def url = "/peaceIm/intranet/holdCourt"
            def trialInfo = TrialInfo.get(trialInfoId)
            def planInfo = trialInfo.planInfo
            if(planInfo.wfyPlan != 1){
                return
            }
            def caseInfo = planInfo.caseInfo
            def courtCode = Dict.findByCode("CURRENT_COURT").val
            def reverseUrlPrefix = Dict.findByCode("CURRENT_SERVICE_PATH").val
            def planList = PlanInfo.findAllByCaseInfo(caseInfo, [sort: "dateCreated", order: "asc"])//根据创建时间模拟庭次
            def caseOrder = 0
            for (def p : planList) {
                caseOrder++
                if (p.id == trialInfo.planInfo.id) {
                    break
                }
            }
            def archives = caseInfo.archives
            def reqModel = [
                    courtCode       : courtCode,
                    caseNumberCode  : archives,
                    caseOrder       : "${caseOrder}",
                    mobilePhone     : MOBILE_PHONE,
                    reverseUrlPrefix: reverseUrlPrefix
            ]
            def resp = WfyRestUtil.post(url, reqModel)
            if (resp?.code == 1000) {//成功
                log.info("[WfyService]发送http请求成功，方法[holdCourt]，请求数据[${reqModel as JSON}]，返回信息[${resp?.json}]")
                startRecordLive(trialInfo)//开庭成功,通知开始录制视频.
            } else {
                log.error("[WfyService]发送http请求失败，方法[holdCourt]，请求数据[${reqModel as JSON}]，返回信息[${resp?.json}]")
            }
        }
    }
    /**
     * 5.1.4.媒体开关
     * 法官控制庭审参与人的媒体开关
     *{*   "courtCode":"15023",      法院代码
     *   "caseNumberCode":"",     内网案号
     *   "caseOrder":"1"           庭次
     *   "mediaType":"VOICE",
     *   "userName":"zhangsan",   姓名
     *   "mobilePhone":"13900000000",    手机号码
     *   "flag":true
     *}*/
    def switchMedia() {
        if (FLAG == 1) {
            def url = "/peaceIm/intranet/switchMedia"
        }
    }
    /**
     * 5.1.5.开始录制视频
     * 通知互联网开庭管理平台开始录制视频
     *{*   "courtCode":"15023",      法院代码
     *   "caseNumberCode":"",     内网案号
     *   "caseOrder":"1"           庭次
     *}*/
    def startRecordLive(TrialInfo trialInfo) {
        if (FLAG == 1) {
            def url = "/peaceIm/intranet/startRecordLive"
            def planInfo = trialInfo.planInfo
            if(planInfo.wfyPlan != 1){
                return
            }
            def caseInfo = planInfo.caseInfo
            def courtCode = Dict.findByCode("CURRENT_COURT").val
            def planList = PlanInfo.findAllByCaseInfo(caseInfo, [sort: "dateCreated", order: "asc"])//根据创建时间模拟庭次
            def caseOrder = 0
            for (def p : planList) {
                caseOrder++
                if (p.id == trialInfo.planInfo.id) {
                    break
                }
            }
            def archives = caseInfo.archives
            def reqModel = [
                    courtCode     : courtCode,
                    caseNumberCode: archives,
                    caseOrder     : "${caseOrder}"
            ]
            def resp = WfyRestUtil.post(url, reqModel)
            if (resp?.code == 1000) {//成功
                log.info("[WfyService]发送http请求成功，方法[startRecordLive]，请求数据[${reqModel as JSON}]，返回信息[${resp?.json}]")
                return true
            } else {
                log.error("[WfyService]发送http请求失败，方法[startRecordLive]，请求数据[${reqModel as JSON}]，返回信息[${resp?.json}]")
                return false
            }
        }
    }

    /**
     * 5.1.6.查看录制状态
     * 查看互联网开庭管理平台视频录制状态
     *{*   "courtCode":"15023",      法院代码
     *   "caseNumberCode":"",     内网案号
     *   "caseOrder":"1"           庭次
     *}*/
    def queryRecordLive() {
        if (FLAG == 1) {
            def url = "/peaceIm/intranet/queryRecordLive"
        }
    }

    /**
     * 5.1.7.结束录制视频
     * 通知互联网开庭管理平台结束录制视频
     *{*   "courtCode":"15023",      法院代码
     *   "caseNumberCode":"",     内网案号
     *   "caseOrder":"1"           庭次
     *}*/
    def endRecordLive(TrialInfo trialInfo) {
        if (FLAG == 1) {
            def url = "/peaceIm/intranet/endRecordLive"
            def planInfo = trialInfo.planInfo
            if(planInfo.wfyPlan != 1){
                return
            }
            def caseInfo = planInfo.caseInfo
            def courtCode = Dict.findByCode("CURRENT_COURT").val
            def planList = PlanInfo.findAllByCaseInfo(caseInfo, [sort: "dateCreated", order: "asc"])//根据创建时间模拟庭次
            def caseOrder = 0
            for (def p : planList) {
                caseOrder++
                if (p.id == trialInfo.planInfo.id) {
                    break
                }
            }
            def archives = caseInfo.archives
            def reqModel = [
                    courtCode     : courtCode,
                    caseNumberCode: archives,
                    caseOrder     : "${caseOrder}"
            ]
            def resp = WfyRestUtil.post(url, reqModel)
            if (resp?.code == 1000) {//成功
                log.info("[WfyService]发送http请求成功，方法[endRecordLive]，请求数据[${reqModel as JSON}]，返回信息[${resp?.json}]")
            } else {
                log.error("[WfyService]发送http请求失败，方法[endRecordLive]，请求数据[${reqModel as JSON}]，返回信息[${resp?.json}]")
            }
        }
    }

    /**
     * 5.1.8.通知庭审网关离开房间
     * 通知庭审离开房间接口（休庭时触发）
     *{*   "courtCode":"15023",      法院代码
     *   "caseNumberCode":"",     内网案号
     *   "caseOrder":"1"           庭次
     *}*/
    def leaveRoom(Long trialInfoId) {
        if (FLAG == 1) {
            def url = "/peaceIm/intranet/leaveRoom"
            def trialInfo = TrialInfo.get(trialInfoId)
            def planInfo = trialInfo.planInfo
            if(planInfo.wfyPlan != 1){
                return
            }
            def caseInfo = planInfo.caseInfo
            def courtCode = Dict.findByCode("CURRENT_COURT").val
            def planList = PlanInfo.findAllByCaseInfo(caseInfo, [sort: "dateCreated", order: "asc"])//根据创建时间模拟庭次
            def caseOrder = 0
            for (def p : planList) {
                caseOrder++
                if (p.id == trialInfo.planInfo.id) {
                    break
                }
            }
            def archives = caseInfo.archives
            def reqModel = [
                    courtCode     : courtCode,
                    caseNumberCode: archives,
                    caseOrder     : "${caseOrder}"
            ]
            def resp = WfyRestUtil.post(url, reqModel)
            if (resp?.code == 1000) {//成功
                log.info("[WfyService]发送http请求成功，方法[leaveRoom]，请求数据[${reqModel as JSON}]，返回信息[${resp?.json}]")
            } else {
                log.error("[WfyService]发送http请求失败，方法[leaveRoom]，请求数据[${reqModel as JSON}]，返回信息[${resp?.json}]")
            }
        }
    }

    /**
     * 5.1.9.闭庭
     * 法官进行闭庭操作时，庭审网关调用本接口。
     *{*   "courtCode":"15023",      法院代码
     *   "caseNumberCode":"",     内网案号
     *   "caseOrder":"1"           庭次
     *}*/
    def closeCourt(Long trialInfoId) {
        if (FLAG == 1) {
            def url = "/peaceIm/intranet/closeCourt"
            def trialInfo = TrialInfo.get(trialInfoId)
            def planInfo = trialInfo.planInfo
            if(planInfo.wfyPlan != 1){
                return
            }
            def caseInfo = planInfo.caseInfo
            def courtCode = Dict.findByCode("CURRENT_COURT").val
            def planList = PlanInfo.findAllByCaseInfo(caseInfo, [sort: "dateCreated", order: "asc"])//根据创建时间模拟庭次
            def caseOrder = 0
            for (def p : planList) {
                caseOrder++
                if (p.id == trialInfo.planInfo.id) {
                    break
                }
            }
            def archives = caseInfo.archives
            def reqModel = [
                    courtCode     : courtCode,
                    caseNumberCode: archives,
                    caseOrder     : "${caseOrder}"
            ]

            def resp = WfyRestUtil.post(url, reqModel)
            if (resp?.code == 1000) {//成功
                log.info("[WfyService]发送http请求成功，方法[closeCourt]，请求数据[${reqModel as JSON}]，返回信息[${resp?.json}]")
            } else {
                log.error("[WfyService]发送http请求失败，方法[closeCourt]，请求数据[${reqModel as JSON}]，返回信息[${resp?.json}]")
            }
        }
    }

    /**
     * 5.1.10.获取笔录信息
     *{*   "courtCode":"15023",      法院代码
     *   "caseNumberCode":"",     内网案号
     *   "caseOrder":"1"           庭次
     *}*/
    def getClerkRecord() {
        if (FLAG == 1) {
            def url = "/peaceIm/intranet/getClerkRecord"
        }
    }

    /**
     * 5.1.11.发送笔录
     * 发送笔录
     *{*   "courtCode":"15023",      法院代码
     *   "caseNumberCode":"",     内网案号
     *   "caseOrder":"1"           庭次
     *   "content": " 1111111111111111111",
     *    "selectPersonIds": [
     *{*        "userName": "zhangsan",
     *        "mobilePhone": "1390001111"
     *},
     *{*        "userName": "lisi",
     *        "mobilePhone": "1390006666"
     *}* ]
     *}*/
    def sendClerkRecord(TrialInfo trialInfo) {
        if (FLAG == 1) {
            def url = "/peace/intranet/sendClerkRecord"
            def planInfo = trialInfo.planInfo
            if(planInfo.wfyPlan != 1){
                return false
            }
            def caseInfo = planInfo.caseInfo
            def courtCode = Dict.findByCode("CURRENT_COURT").val
            def planList = PlanInfo.findAllByCaseInfo(caseInfo, [sort: "dateCreated", order: "asc"])//根据创建时间模拟庭次
            def caseOrder = 0
            for (def p : planList) {
                caseOrder++
                if (p.id == trialInfo.planInfo.id) {
                    break
                }
            }
            def archives = caseInfo.archives //获取案号
            def path = grailsApplication.config.getProperty('tc.trial.note.path')//从doc文件中获取笔录内容
            def file = new File("${path}", trialInfo.note)
            def content = WordUtil.readWordContent(file)

            //因为本地没有参与人信息，这里利用getLitigantInfo接口先获取参与人信息
            def selectPersonIds = getLitigantInfo(courtCode, archives, "${caseOrder}")

            def reqModel = [
                    courtCode      : courtCode,
                    caseNumberCode : archives,
                    caseOrder      : "${caseOrder}",
                    content        : content,
                    selectPersonIds: selectPersonIds

            ]
            def resp = WfyRestUtil.post(url, reqModel)
            if (resp?.code == 1000) {//成功
                log.info("[WfyService]发送http请求成功，方法[sendClerkRecord]，请求数据[${reqModel as JSON}]，返回信息[${resp?.json}]")
                return true
            } else {
                log.error("[WfyService]发送http请求失败，方法[sendClerkRecord]，请求数据[${reqModel as JSON}]，返回信息[${resp?.json}]")
                return false
            }
        }
    }

    /**
     * 5.1.14.获取参与人在线状态
     * 获取参与人在线状态
     * @param courtCode 法院代码
     * @param caseNumberCode 内网案号
     * @param caseOrder 庭次
     *{*   "courtCode":"15023",      法院代码
     *   "caseNumberCode":"",     内网案号
     *   "caseOrder":"1"           庭次
     *} */
    private getLitigantInfo(String courtCode, String caseNumberCode, String caseOrder) {
        if (FLAG == 1) {
            def url = "/peace/intranet/getLitigantInfo"

            def reqModel = [
                    courtCode     : courtCode,
                    caseNumberCode: caseNumberCode,
                    caseOrder     : caseOrder

            ]
            def resp = WfyRestUtil.post(url, reqModel)
            if (resp?.code == 1000) {//成功
                log.info("[WfyService]发送http请求成功，方法[getLitigantInfo]，请求数据[${reqModel as JSON}]，返回信息[${resp?.json}]")
            } else {
                log.error("[WfyService]发送http请求失败，方法[getLitigantInfo]，请求数据[${reqModel as JSON}]，返回信息[${resp?.json}]")
            }
            //获取参与人列表
            def userList = resp?.json?.data as JSONArray
            return userList
        }
    }


}
