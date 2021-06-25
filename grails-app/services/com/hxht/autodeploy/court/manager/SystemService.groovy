package com.hxht.autodeploy.court.manager

import com.hxht.techcrt.Dict
import grails.gorm.transactions.Transactional

/**
 * 系统标题配置操作Service
 * 2021.04.26 >>> 分离羁押室语音模板设置和法庭报警音频设置 daniel
 * 2021.06.16 >>> 互联网开庭统计 daniel
 */
@Transactional
class SystemService {

    /**
     * 标题修改保存
     * @param systemTitle 系统名称
     * @return 无
     */
    def currentCourtEditSave(String systemTitle) {
        def court = Dict.findByCode("CURRENT_COURT")
        court.ext5 = systemTitle
        court.save(flush: true)
        if (court.hasErrors()) {
            log.error("[SystemService.currentCourtEditSave] 系统标题修改失败,错误信息:\n${court.errors}]")
            throw new RuntimeException()
        }
        SystemController.currentCourt = Dict.findByCode("CURRENT_COURT")
    }

    /**
     * 是否允许互联网开庭
     * @param allowInternet 0:禁用 1:允许
     * @return 无
     */
    def isInternetEditSave(String allowInternet) {
        def dict = Dict.findByCode("IS_INTERNET")
        dict.val = allowInternet
        dict.save(flush: true)
        if (dict.hasErrors()) {
            log.error("[SystemService.isInternetEditSave] 修改是否允许互联网开庭时失败,错误信息:\n${dict.errors}")
            throw new RuntimeException()
        }
    }
}
