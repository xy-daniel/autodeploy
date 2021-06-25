package com.hxht.autodeploy.api

import cn.hutool.core.date.DateUtil
import com.hxht.techcrt.CourtroomStatus
import com.hxht.techcrt.DataStatus
import com.hxht.techcrt.court.Courtroom
import com.hxht.techcrt.court.PlanInfo

class ApiNoticeService {

    /**
     * 获取全部法庭
     */
    def courtroomAll() {
        def courtroomList = Courtroom.findAllByStatusNotEqualAndActive(CourtroomStatus.STOP, DataStatus.SHOW)
        def dataList = []
        for (def courtroom : courtroomList) {
            dataList.add([
                    uid     : courtroom.uid,
                    name    : courtroom.name,
                    deviceIp: courtroom.deviceIp,
                    storeIp : courtroom.storeIp
            ])
        }
        dataList
    }
    /**
     * 获取前7 后7天排期数据
     * @param courtroomId
     */
    def getPlan() {
        def dataList = []
        def startTime = DateUtil.beginOfDay(new Date())
        def planList = PlanInfo.createCriteria().list {
            and {
                ge("startDate", DateUtil.offsetDay(startTime, -7))
                le("startDate", DateUtil.offsetDay(startTime, 7))
            }
        } as List<PlanInfo>
        for (def planInfo : planList) {
            try {
                dataList.add([
                        caseUid      : planInfo.caseInfo?.uid,
                        caseArchives : planInfo.caseInfo?.archives,
                        caseName     : planInfo.caseInfo?.name,
                        caseAccuser  : planInfo.caseInfo?.accuser,
                        caseAccused  : planInfo.caseInfo?.accused,
                        uid          : planInfo.uid,
                        courtroomUid : planInfo.courtroom?.uid,
                        startDate    : planInfo.startDate?.format("yyyy-MM-dd HH:mm:ss"),
                        endDate      : planInfo.endDate?.format("yyyy-MM-dd HH:mm:ss"),
                        status       : planInfo.status,
                        undertakeUid : planInfo.undertake?.uid,
                        undertakeName: planInfo.undertake?.name,
                        judgeUid     : planInfo.judge?.uid,
                        judgeName    : planInfo.judge?.name,
                        secretaryUid : planInfo.secretary?.uid,
                        secretaryName: planInfo.secretary?.name,

                ])
            } catch (e) {
                e.printStackTrace()
                log.error("ApiNoticeService getPlan 格式化错误 ${e.message}")
            }

        }
        dataList
    }

}
