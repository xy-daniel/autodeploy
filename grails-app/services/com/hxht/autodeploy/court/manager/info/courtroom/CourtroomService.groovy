package com.hxht.autodeploy.court.manager.info.courtroom

import com.hxht.techcrt.CourtRemoteStatus
import com.hxht.techcrt.CourtroomStatus
import com.hxht.techcrt.PlanStatus
import com.hxht.techcrt.court.Courtroom
import com.hxht.techcrt.mem.DeviceIsOnlineService
import grails.gorm.transactions.Transactional

/**
 * 2021.04.19 >>> 增加远程提讯开关 daniel
 */
@Transactional
class CourtroomService {

    DeviceIsOnlineService deviceIsOnlineService

    def list(int draw, int start, int length, String search) {
        def model = [:]
        model.put("draw", draw)
        def count = Courtroom.createCriteria().count() {
            if (search) {
                or{
                    like("name", "%${search}%")
                    like("liveIp", "%${search}%")
                    like("livePort", "%${search}%")
                    like("deviceIp", "%${search}%")
                    like("storeIp", "%${search}%")
                    if (PlanStatus.getCode(search) != null) {
                        eq("status", CourtroomStatus.getCode(search))
                    }
                }
            }
        }
        def dataList = Courtroom.createCriteria().list {
            and {
                setMaxResults(length)
                setFirstResult(start)
            }
            if (search) {
                or{
                    like("name", "%${search}%")
                    like("liveIp", "%${search}%")
                    like("livePort", "%${search}%")
                    like("deviceIp", "%${search}%")
                    like("storeIp", "%${search}%")
                    if (PlanStatus.getCode(search) != null) {
                        eq("status", CourtroomStatus.getCode(search))
                    }
                }
            }
            order("sequence", "asc")
        } as List<Courtroom>
        def modelDataList = []
        for (def courtroom : dataList) {
            if (courtroom.remote == null){
                courtroom.remote = CourtRemoteStatus.NOTREMOTE
                courtroom.save(flush: true)
            }
            def data = [:]
            data.put("id", courtroom.id)
            data.put("name", courtroom.name)
            data.put("liveIp", courtroom.liveIp)
            data.put("livePort", courtroom.livePort)
            data.put("deviceIp", courtroom.deviceIp)
            data.put("isConnect", deviceIsOnlineService.isConnect(courtroom.id))
            data.put("storeIp", courtroom.storeIp)
            if (courtroom.status == null){
                courtroom.status = CourtroomStatus.NORMAL
                courtroom.save(flush: true)
            }
            data.put("status", CourtroomStatus.getString(courtroom.status))
            data.put("isCalled", courtroom.isCalled)
            modelDataList.add(data)
        }
        model.put("recordsTotal", count)//数据总条数
        model.put("recordsFiltered", count)//显示的条数
        model.put("data", modelDataList)
        model
    }

    def save(Courtroom courtroom) {
        courtroom.save(flush: true)
        if(courtroom.hasErrors()){
            log.info("保存法庭时出错 CourtroomService.save courtroom.errors [${courtroom.errors}]")
            throw new RuntimeException()
        }
    }

    /**
     * 获取法庭信息
     * @param id  法庭主键
     * @return  法庭信息
     */
    def getCourtroom(def id){
        Courtroom.get(id as Long)
    }

    /**
     * 开始远程连接---->将正常状态的法庭切换到远程提讯状态
     * @param id  法庭主键
     * @return  boolean修改是否成功 ture/false
     */
    def startDecode(def id){
        def courtroom = Courtroom.get(id as Long)
        if (courtroom){
            courtroom.status = CourtroomStatus.OCCUPIED
            courtroom.save(flush:true)
            if (courtroom.hasErrors()){
                log.info("开始远程连接更新法庭信息时出错 CourtroomService.startDecode courtroom [${courtroom.errors}]")
                throw new RuntimeException()
            }
            return true
        }else{
            return false
        }
    }

    /**
     * 断开远程连接---->将远程提讯状态的法庭切换到正常状态
     * @param id  法庭主键
     * @return  boolean修改是否成功 ture/false
     */
    def stopDecode(def id){
        def courtroom = Courtroom.get(id as Long)
        if (courtroom){
            courtroom.status = CourtroomStatus.NORMAL
            courtroom.save(flush:true)
            if (courtroom.hasErrors()){
                log.info("断开远程连接更新法庭信息时出错 CourtroomService.stopDecode courtroom [${courtroom.errors}]")
                throw new RuntimeException()
            }
            return true
        }else{
            return false
        }
    }

    def handleRemote(def courtArr){
        def courts = Courtroom.findAllByRemote(CourtRemoteStatus.REMOTE)
        for (Courtroom courtroom:courts){
            courtroom.remote = CourtRemoteStatus.NOTREMOTE
            courtroom.save(flush:true)
            if (courtroom.hasErrors()){
                log.info("保存法庭是否是远程时出错CourtroomService.handleRemote courtroom [${courtroom.errors}]")
                throw new RuntimeException()
            }
        }
        for (String id:courtArr){
            Courtroom court = Courtroom.get(id as Long)
            court.remote = CourtRemoteStatus.REMOTE
            court.save(flush:true)
            if (court.hasErrors()){
                log.info("保存法庭是否是远程时出错CourtroomService.handleRemote court [${court.errors}]")
                throw new RuntimeException()
            }
        }
    }
}
