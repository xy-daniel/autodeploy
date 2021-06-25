package com.hxht.autodeploy.court.manager

import com.hxht.techcrt.User
import com.hxht.techcrt.UserRole
import com.hxht.techcrt.court.PlanInfo
import com.hxht.techcrt.court.VideoRecord
import com.hxht.techcrt.enums.PlayStatus
import grails.gorm.transactions.Transactional

import java.text.SimpleDateFormat

/**
 * 直播授权服务
 */
@Transactional
class VideoRecordService {

    /**
     * 正在观看直播的记录表列表
     * @param draw 标志
     * @param start 起始坐标
     * @param length 长度
     * @param search 搜索关键词
     * @return List<VideoRecord>
     */
    def list(int draw, int start, int length, String search) {
        def model = [:]
        model.put("draw", draw)
        def count = VideoRecord.createCriteria().count() {
            if (search) {
                or {
                }
            }
        }
        def dataList = VideoRecord.createCriteria().list {
            and {
                setMaxResults(length)
                setFirstResult(start)
            }
            or {
            }
            order("playStatus", "esc")
            order("dateCreated", "desc")
        } as List<VideoRecord>
        def modelDataList = []
        for (def vr : dataList) {
            def data = [:]
            data.put("id", vr.id)
            data.put("userName", User.get(vr.userId).username)
            data.put("archives", PlanInfo.get(vr.planId).caseInfo.archives)
            data.put("caseName", PlanInfo.get(vr.planId).caseInfo.name)
            data.put("ip", vr.ip)
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
            data.put("playStatus", PlayStatus.getString(vr.playStatus))
            data.put("id_status", vr.id + "_" + vr.playStatus)
            //结束时间
            data.put("time", sdf.format(vr.lastUpdated))
            modelDataList.add(data)
        }
        model.put("recordsTotal", count)//数据总条数
        model.put("recordsFiltered", count)//显示的条数
        model.put("data", modelDataList)
        model
    }

    def getVr(Long id){
        def vr = VideoRecord.get(id)
        def urs = UserRole.findAllByUser(User.get(vr.userId))
        def flag = false
        for (UserRole ur:urs){
            def authority = ur.role.authority
            if (authority == "ROLE_SUPER" || authority == "ROLE_ADMIN"){
               flag = true
            }
        }
        if (flag){
            vr
        }else{
            vr.playStatus = PlayStatus.ADMIN_DISCONNECT
            vr.save(flush: true)
            vr
        }
    }
}
