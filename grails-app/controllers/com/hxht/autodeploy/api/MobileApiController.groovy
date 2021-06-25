package com.hxht.autodeploy.api

import cn.hutool.core.date.DateUtil
import com.hxht.techcrt.DataStatus
import com.hxht.techcrt.PlanStatus
import com.hxht.techcrt.Resp
import com.hxht.techcrt.court.Courtroom
import com.hxht.techcrt.court.PlanInfo
import com.hxht.techcrt.enums.RespType
import com.hxht.techcrt.mem.SignatureService
import org.springframework.web.multipart.MultipartHttpServletRequest

/**
 * 移动端接口
 * 2021.04.29 >>> 修改移动端获取案件信息接口,按照开庭时间先后顺序只返回当前庭闭庭状态的案件信息  daniel
 */
class MobileApiController {
    SignatureService signatureService

    def signaturePlanList() {
        /**
         * GET 安卓端获取今日排期列表
         */
        if (request.method == "GET") {
            try {
                def courtroom = Courtroom.get(params.long("id"))
                //获取当天的排期案件
                def today = DateUtil.beginOfDay(new Date())
                def tomor = DateUtil.endOfDay(new Date())
                def planList
                if (!courtroom) {
                    planList = PlanInfo.findAllByStartDateBetweenAndActive(
                            today,
                            tomor,
                            DataStatus.SHOW,
                            [sort: "startDate", order: "desc"]
                    )
                } else {
                    planList = PlanInfo.findAllByCourtroomAndStartDateBetweenAndStatusAndActive(
                            courtroom,
                            today,
                            tomor,
                            PlanStatus.CLOSED,
                            DataStatus.SHOW,
                            [sort: "startDate", order: "desc"]
                    )
                }
                def dataList = []
                for (def planInfo : planList) {
                    dataList.add([
                            id      : planInfo.id,
                            archives: planInfo.caseInfo?.archives,
                            name    : planInfo.caseInfo?.name
                    ])
                }
                render Resp.toJson(RespType.SUCCESS, dataList)
            } catch (e) {
                e.printStackTrace()
                def msg = "安卓端获取今日排期时出错！${e.message}"
                log.error("[ApiController.signaturePlanList]${msg}")
                render Resp.toJson(RespType.FAIL, msg)
            }
        }
    }

    def signaturePersonList() {
        /**
         * GET 根据排期id获取签名人员列表
         * id 排期id
         */
        if (request.method == "GET") {
            try {
                //获取当天的排期案件
                def planInfo = PlanInfo.get(params.long("id", 0))
                def signatureList = planInfo.distanceSignature?.split(",")
                render Resp.toJson(RespType.SUCCESS, signatureList)
            } catch (e) {
                e.printStackTrace()
                def msg = "安卓端获取排期签名人员列表时出错！${e.message}"
                log.error("[ApiController.signaturePersonList]${msg}")
                render Resp.toJson(RespType.FAIL, msg)
            }
        }
    }

    def signaturePersonSubmit() {
        /**
         * POST 提交人员签名图片文件
         */
        if (request.method == "POST") {
            try {
                if (request instanceof MultipartHttpServletRequest) {
                    def planId = params.long("id")
                    def name = params.name as String
                    def file = request.getFile("file")
                    if (file && name && planId) {
                        def path = grailsApplication.config.getProperty('tc.signature.path')
                        def signature = signatureService.saveSignature(planId, name, file, path)
                        if (signature) {
                            render Resp.toJson(RespType.SUCCESS)
                            return
                        }
                    }
                }
                render Resp.toJson(RespType.FAIL, "提交数据参数有误!")
            } catch (e) {
                e.printStackTrace()
                def msg = "安卓端保存文件时出错！${e.message}"
                log.error("[ApiController.signaturePersonSubmit]${msg}")
                render Resp.toJson(RespType.FAIL, msg)
            }
        }
    }
}
