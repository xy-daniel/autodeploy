package com.hxht.autodeploy.sync.luohu

import com.alibaba.fastjson.JSONObject
import com.hxht.techcrt.CourtroomStatus
import com.hxht.techcrt.DataStatus
import com.hxht.techcrt.court.*
import com.hxht.techcrt.utils.UUIDGenerator
import grails.gorm.transactions.Transactional

@Transactional
class LuoHuCourtRoomService {
    /**
     * 获取法庭数据
     */
    def addCourtRoom(JSONObject r) {
        // 获取所有法庭
        try {
                def id = r.getString("id")
                def name = r.getString("name")
                // 根据name判断是否存在
                def courtroom = Courtroom.findByName(name)
                if (courtroom){ //存在
                    courtroom.luohuFlag = "1"
                    courtroom.sycLuoHuId = id
                    courtroom.active = DataStatus.SHOW
                    courtroom.status = CourtroomStatus.NORMAL
                    courtroom.save(flush: true)
                    if (courtroom.hasErrors()) {
                        log.info("LuoHuApiService.addCourtRoom 处理罗湖法院对接接口保存存在的法庭出错------[${courtroom.errors}]")
                        throw new RuntimeException()
                    }
                }else{
                    def room = new Courtroom()
                    room.luohuFlag = "1"
                    room.sycLuoHuId = id
                    room.uid = UUIDGenerator.nextUUID()
                    room.active = DataStatus.SHOW
                    room.status = CourtroomStatus.NORMAL
                    room.name = name
                    room.save(flush: true)
                    if (room.hasErrors()) {
                        log.info("LuoHuApiService.addCourtRoom 处理罗湖法院对接接口保存不存在的法庭出错------[${room.errors}]")
                        throw new RuntimeException()
                    }
                }
            } catch (Exception e) {
                log.error("处理罗湖法院对接接口法庭出错:" + e.getMessage())
            }
    }


}
