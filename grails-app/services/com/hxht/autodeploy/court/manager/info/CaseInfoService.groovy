package com.hxht.autodeploy.court.manager.info

import com.hxht.techcrt.court.CaseInfo
import com.hxht.techcrt.court.CaseType
import com.hxht.techcrt.court.Department
import com.hxht.techcrt.service.sync.huigu.entity.Case
import grails.gorm.transactions.Transactional

/**
 * 案件服务 created by arctic in 2020.05.20
 */
@Transactional
class CaseInfoService {

    def addCaseInfo(Case xmlCase, CaseType caseType){
        caseType = CaseType.get(caseType.id)
        CaseInfo caseInfoNew = new CaseInfo(
                uid: xmlCase.uid,
                synchronizationId: xmlCase.interfaceId,
                filingDate: xmlCase.casedate,
                summary: xmlCase.casedesc,
                accuser: xmlCase.accuse,
                accused: xmlCase.accused,
                name: xmlCase.casename,
                archives: xmlCase.caseno,
                type: caseType,
                department: Department.findBySynchronizationId(xmlCase.deptId),
                active: 1
        )
        caseInfoNew.save(flush: true)
        if (caseInfoNew.hasErrors()) {
            log.error("uid= {} 的案件插入失败,错误信息：\n{}", xmlCase.uid, caseInfoNew.errors)
            return null
        }
        return caseInfoNew
    }

    def updateCaseInfo(CaseInfo localCase, Case xmlCase, CaseType caseType){
        log.info("case数据在本地数据库中存在,现在将会直接更新")
        xmlCase = xmlCase.checkCase()
        xmlCase.setUid(localCase.uid)
        localCase.filingDate = xmlCase.casedate
        localCase.summary = xmlCase.casedesc
        localCase.accuser = xmlCase.accuse
        localCase.accused = xmlCase.accused
        localCase.name = xmlCase.casename
        localCase.archives = xmlCase.caseno
        localCase.type = caseType
        localCase.department = Department.findBySynchronizationId(xmlCase.deptId)
        localCase.active = xmlCase.flag?:1
        localCase.save(flush: true)
        if (localCase.hasErrors()){
            log.error("对接慧谷数据，案件信息更新失败,失败信息:{}", localCase.errors)
            return null
        }
        return localCase
    }
}
