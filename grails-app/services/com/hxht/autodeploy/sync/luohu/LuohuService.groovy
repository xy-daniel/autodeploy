package com.hxht.autodeploy.sync.luohu

import com.hxht.techcrt.court.Courtroom
import com.hxht.techcrt.sync.util.http.HttpClientUtils
import grails.core.GrailsApplication

class LuohuService {
    LuoHuCourtRoomService luoHuCourtRoomService
    LuoHuPlanInfoService luoHuPlanInfoService
    GrailsApplication grailsApplication

    //对接罗湖法院法庭接口
    def saveCourtroom(){
        // 获取所有法庭
        try {
            def courturl = grailsApplication.config.getProperty('syncData.luohu.courturl')
            def array = HttpClientUtils.httpPost(courturl)
//            JSONArray array = JSONObject.parseObject(HttpClientUtils.courtrooms).getJSONArray("resData")
            // 同时把数据插入到数据库中,插入法庭编号，法庭名称
            for (def i = 0 ; i < array.size(); i++) {
                def r = array.getJSONObject(i)
                luoHuCourtRoomService.addCourtRoom(r)
            }
        } catch (Exception e) {
            log.error("处理罗湖法院对接接口法庭出错:" + e.getMessage())
        }
    }

    //对接罗湖法院排期接口
    def saveCasePlan(){
        def planurl = grailsApplication.config.getProperty('syncData.luohu.planurl')
        //获取所有的法庭
        def courtRoomList = Courtroom.withNewSession {
            Courtroom.findAllByLuohuFlagIsNotNull()
        }
//        def courtRoomList = Courtroom.findAllByName("第八法庭")
        int sum = 0;
        for (def room : courtRoomList) {//根据法庭，循环查询每个法庭的案件
            try {
                def array = HttpClientUtils.httpPost(planurl, room.name)
//                JSONArray array = JSONObject.parseObject(HttpClientUtils.plans).getJSONArray("resData")
                if (array.size() == 0) {//查询不到排期数据
                    continue
                }
                /** 注意此处，每一条数据都是一条事物，要保证排期，案件，人员编号，法庭事物一致性*/
                if (array.size() > 0) {
                    sum = sum + array.size()
                    log.info("处理罗湖法院对接接口排期")
                    for (int i = 0; i < array.size(); i++) {
                        luoHuPlanInfoService.getCaseAndPlan(array.getJSONObject(i),room)
                    }
                }
            }catch (Exception e) {
                log.error("处理罗湖法院对接接口--获取预定案件排期失败:" + e.getMessage())
            }
        }
        log.info("处理罗湖法院对接接口--本次总排期数:" + sum)
    }
}
