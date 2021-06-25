package com.hxht.autodeploy.court.index

import com.hxht.techcrt.CourtroomOnlineStatus
import com.hxht.techcrt.CourtroomStatus
import com.hxht.techcrt.DataStatus
import com.hxht.techcrt.PlanStatus
import com.hxht.techcrt.Resp
import com.hxht.techcrt.court.Courtroom
import com.hxht.techcrt.court.PlanInfo
import com.hxht.techcrt.court.mem.CourtroomIsOnline
import com.hxht.techcrt.enums.RespType
import com.hxht.techcrt.utils.NodeUtil
import grails.gorm.transactions.Transactional

/**
 * 今日庭审（主页）
 * 2021.04.22 删除远程法庭相关代码 daniel
 */
class IndexController {
    IndexService indexService

    /**
     * 今日庭审（主页）页面，其他处理异步处理
     */
    def index() {}

    /**
     * 异步展示左侧庭审列表
     */
    def getCourtroom() {
        def courtroom = params.get("courtroom") as String
        def courtroomLocal
        def courtroomName = ""
        List list = new ArrayList()
        list.add(CourtroomStatus.NORMAL)
        list.add(CourtroomStatus.OCCUPIED)
        if (courtroom) {
            courtroomName = courtroom.trim()
            if ("开" == courtroomName || "开庭" == courtroomName) {
                courtroomLocal = Courtroom.findAllByStatusInList(list, [sort: "sequence", order: "asc"])
            } else {
                courtroomLocal = Courtroom.findAllByStatusInListAndNameLike(list, "%" + courtroomName + "%", [sort: "sequence", order: "asc"])
            }
        } else {
            courtroomLocal = Courtroom.findAllByStatusInList(list, [sort: "sequence", order: "asc"])
        }
        def dataListLocal = new ArrayList<NodeUtil>()
        for (def cou : courtroomLocal) {
            def planInfo = PlanInfo.findByActiveAndStatusAndCourtroom(DataStatus.SHOW, PlanStatus.SESSION, cou)
            def courtRoomOnline = this.courtOnline(cou.id)
            
            if (planInfo) {
                def openCourt = cou.name + "（开庭）"
                if (courtRoomOnline && courtRoomOnline == "在线状态"){
                    dataListLocal.add(new NodeUtil(cou.id, openCourt, "fas fa-circle fa-fw f-s-9 text-green",null))//有开庭的情况则显示开庭
                }else{
                    dataListLocal.add(new NodeUtil(cou.id, openCourt, "fas fa-circle fa-fw f-s-9 text-danger",null))//有开庭的情况则显示开庭
                }
                
            } else {
                if ("开" != courtroomName && "开庭" != courtroomName) {
                    if (courtRoomOnline && courtRoomOnline == "在线状态"){
                        dataListLocal.add(new NodeUtil(cou.id, cou.name,"fas fa-circle fa-fw f-s-9 text-green" , null))
                    }else{
                        dataListLocal.add(new NodeUtil(cou.id, cou.name, "fas fa-circle fa-fw f-s-9 text-danger",null))
                    }
                    
                }
            }
        }
        def model = [
                courtroomLocal   : dataListLocal,
                courtroomLocalNum: courtroomLocal.size()
        ]
        render Resp.toJson(RespType.SUCCESS, model)
    }

    @Transactional("mem")
    def courtOnline(long id){
        def online = CourtroomIsOnline.findByCourtroomId(id)
        if (online){
            return CourtroomOnlineStatus.getString(online.status)
        }
        return null
    }

    /**
     * 首页获取每日排期数量
     */
    def planDayCount() {
        def numList = indexService.planDayCount(
                params.int("year"),
                params.int("month"),
                params.int("day"),
                Courtroom.get(params.long("courtroom")))
        render Resp.toJson(RespType.SUCCESS, numList)
    }

    /**
     * 首页排期列表
     */
    def planDayItem() {
        def planList = indexService.planDayItem(
                params.date("date", "dd/MM/yyyy"),
                params.int("length"),
                Courtroom.get(params.long("courtroom")))
        render Resp.toJson(RespType.SUCCESS, planList)
    }

    /**
     * 获取指定法庭数据
     */
    def courtroomData() {
        def data = indexService.courtroomData(Courtroom.get(params.long("courtroom")))
        render Resp.toJson(RespType.SUCCESS, data)
    }
    
    def courtroomIsOnline() {
        def isConnect = indexService.courtroomIsOnline(Courtroom.get(params.long("courtroom")))
        def model = [
                isConnect: isConnect
        ]
        render Resp.toJson(RespType.SUCCESS, model)
    }

}
