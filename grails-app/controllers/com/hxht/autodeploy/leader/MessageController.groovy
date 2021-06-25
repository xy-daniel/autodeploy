package com.hxht.autodeploy.leader

import com.hxht.techcrt.CourtroomStatus
import com.hxht.techcrt.DataStatus
import com.hxht.techcrt.PlanStatus
import com.hxht.techcrt.Resp
import com.hxht.techcrt.court.Courtroom
import com.hxht.techcrt.court.PlanInfo
import com.hxht.techcrt.enums.RespType
import com.hxht.techcrt.utils.NodeUtil

/**
 * 2021.04.22 >>> 删除远程法庭相关代码
 */
class MessageController {
    MessageService messageService

    //主页
    def index() {}

    /**
     * 获取法庭列表
     */
    def getCourtroom() {
        def courtroom = params.get("courtroom") as String
        def model = [:]
        def courtroomLocal
        def courtroomName
        List list = new ArrayList()
        list.add(CourtroomStatus.NORMAL)
        list.add(CourtroomStatus.OCCUPIED)
        if (courtroom) {
            courtroomName = courtroom.trim()//去除两端空格
            if ("开" == courtroomName || "开庭" == courtroomName) {
                courtroomLocal = Courtroom.findAllByStatusInList(list, [sort: "sequence", order: "desc"])
            } else {
                courtroomLocal = Courtroom.findAllByStatusAndNameLike(DataStatus.SHOW, "%" + courtroomName + "%", [sort: "sequence", order: "desc"])
            }
        } else {
            courtroomLocal = Courtroom.findAllByStatusInList(list, [sort: "sequence", order: "desc"])
        }
        def dataListLocal = new ArrayList<NodeUtil>()
        for (def cou : courtroomLocal) {
            def planInfo = PlanInfo.findByActiveAndStatusAndCourtroom(DataStatus.SHOW, PlanStatus.SESSION, cou)
            if (planInfo) {
                def openCourt = cou.name + "（开庭）"
                dataListLocal.add(new NodeUtil(cou.id, openCourt, null))//有开庭的情况则显示开庭
            } else {
                if (!("开" == courtroomName || "开庭" == courtroomName)) {
                    dataListLocal.add(new NodeUtil(cou.id, cou.name, null))
                }
            }
        }
        model = [
                courtroomLocal   : dataListLocal,
                courtroomLocalNum: courtroomLocal.size(),
        ]
        render Resp.toJson(RespType.SUCCESS, model)
    }

    /**
     * 首页获取每日排期数量
     */
    def planDayCount() {
        def year = params.int("year")
        def month = params.int("month")
        def day = params.int("day")
        def courtroom = Courtroom.get(params.long("courtroom"))
        def dataList = messageService.planDayCount(year, month, day, courtroom)
        render Resp.toJson(RespType.SUCCESS, dataList)
    }

    /**
     * 首页排期列表
     */
    def planDayItem() {
        def date = params.date("date", "dd/MM/yyyy")
        def courtroom = Courtroom.get(params.long("courtroom"))
        def length = params.int("length")
        def dataList = messageService.planDayItem(date, length, courtroom)
        render Resp.toJson(RespType.SUCCESS, dataList)
    }

    /**
     * 获取指定法庭数据
     */
    def courtroomData() {
        render Resp.toJson(RespType.SUCCESS, messageService.courtroomData(Courtroom.get(params.long("courtroom"))))
    }
}
