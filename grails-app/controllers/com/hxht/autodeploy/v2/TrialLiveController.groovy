package com.hxht.autodeploy.v2

import com.hxht.techcrt.court.plan.PlanService
import grails.core.GrailsApplication

/**
 * 庭审直播
 * 2021.06.17 >>> v2版本互联网开庭 daniel
 */
class TrialLiveController {

    PlanService planService
    GrailsApplication grailsApplication

    /**
     * 庭审直播
     * @return GET:查询跳进及页面渲染 POST:排期列表
     */
    def list() {
        if (grailsApplication.config.getProperty('pageVersion') == 'v2') {
            if (request.method == "GET") {
                def result = planService.planListQueryData(params, request)
                return [
                        judgeList: result.judgeList,
                        secretaryList: result.secretaryList,
                        courtroomList: result.courtroomList,
                        date: result.date,
                        contentPath: result.contextPath
                ]
            }
        } else {
            render(view: "/error")
        }
    }

    /**
     * 详情
     * @return
     */
    def show() {
        if (grailsApplication.config.getProperty('pageVersion') == 'v2') {
            planService.show(params)
        } else {
            render(view: "/error")
        }
    }
}