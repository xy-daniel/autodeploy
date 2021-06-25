package com.hxht.autodeploy.court.manager

import com.hxht.techcrt.DistanceCourt
import com.hxht.techcrt.Resp
import com.hxht.techcrt.court.Courtroom
import com.hxht.techcrt.enums.RespType
import com.hxht.techcrt.utils.UUIDGenerator
import grails.gorm.transactions.Transactional
import grails.web.servlet.mvc.GrailsParameterMap

/**
 * 远程法院Service created by daniel in 2021.04.19
 * 2021.04.20 >>> 判断法院编辑时的所选择上级法院是否是可选择法院 daniel
 * 2021.04.20 >>> 添加删除法院功能 daniel
 * 2021.04.24 >>> 远程法院列表根据分级码排序 daniel
 */
@Transactional
class DistanceCourtService {

    /**
     * 远程法院列表
     * @param draw 标记
     * @param start 开始地址
     * @param length 长度
     * @param search 关键词搜索
     * @param id 上级法院id
     * @return 远程法院列表
     */
    def list(int draw, int start, int length, String search, Long id) {
        DistanceCourt dc = DistanceCourt.get(id)
        def model = [:]
        model.put("draw", draw)
        def distanceCourtList = DistanceCourt.findAllByParent(dc, [sort: "code", order: "esc"])
        if (!dc) {
            distanceCourtList = DistanceCourt.findAllByParentIsNull([sort: "code", order: "esc"])
        }
        if (search != "") {
            for (int i = distanceCourtList.size() - 1; i >= 0; i--) {
                if (!(distanceCourtList[i].name.contains(search) || distanceCourtList[i].shortName.contains(search) || distanceCourtList[i].code.contains(search))) {
                    distanceCourtList.remove(i)
                }
            }
        }
        def count = distanceCourtList.size()
        def modelDataList = []
        distanceCourtList = distanceCourtList.subList(start, start + length <= count ? start + length : count)
        for (def distanceCourt : distanceCourtList) {
            def data = [:]
            data.put("id", distanceCourt.id)
            data.put("name", distanceCourt.name)
            data.put("shortName", distanceCourt.shortName)
            data.put("code", distanceCourt.code)
            data.put("service", distanceCourt.service)
            data.put("parentName", distanceCourt.parent ? distanceCourt.parent.name : "无")
            modelDataList.add(data)
        }
        model.put("recordsTotal", count)//数据总条数
        model.put("recordsFiltered", count)//显示的条数
        model.put("data", modelDataList)
        model
    }

    /**
     * 执行远程法院添加操作
     * @param params 参数
     * @return 响应码
     */
    def addSave(GrailsParameterMap params) {
        String code = params.get("code")
        def distanceCourtVo = DistanceCourt.findByCode(code)
        if (distanceCourtVo) {
            return Resp.toJson(RespType.DATA_ALREADY_EXIST)
        }
        def distanceCourt = new DistanceCourt(params)
        distanceCourt.uid = UUIDGenerator.nextUUID()
        distanceCourt.parent = DistanceCourt.get(params.long("parent"))
        distanceCourt.save(flush: true)
        if (distanceCourt.hasErrors()) {
            log.error("[DistanceCourtService.addSave] 添加远程法院失败,失败信息:${distanceCourt.errors}")
            return Resp.toJson(RespType.FAIL)
        }
        return Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 执行远程法院修改操作
     * @param params 参数
     * @return 响应码
     */
    def editSave(GrailsParameterMap params) {
        //需要修改的节点
        def distanceCourt = DistanceCourt.get(params.long("id"))
        if (distanceCourt.code != params.code && DistanceCourt.findAllByCode(params.code).size() > 0) {
            return Resp.toJson(RespType.DATA_ALREADY_EXIST)
        }
        //所选节点
        def distanceCourtView = DistanceCourt.get(params.long("parent"))
        //判断所选节点是否是现在节点的子节点即判断所选节点溯源是否是需要修改的节点
        boolean result = isParent(distanceCourtView, distanceCourt)
        if (!result) {
            return Resp.toJson(RespType.BUSINESS_VALID_FAIL)
        }
        println result.toString()
        distanceCourt.name = params.name
        distanceCourt.shortName = params.shortName
        distanceCourt.code = params.code
        distanceCourt.service = params.service
        distanceCourt.parent = distanceCourtView
        distanceCourt.save(flush: true)
        if (distanceCourt.hasErrors()) {
            log.error("[DistanceCourtService.editSave] 修改远程法庭事变,失败信息:${distanceCourt.errors}")
            return Resp.toJson(RespType.FAIL)
        }
        return Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 判断所选节点溯源是否是需要修改的节点
     * @param distanceCourtView 所选节点
     * @param distanceCourt 需要修改的节点
     * @return 是否是父节点
     */
    boolean isParent(DistanceCourt distanceCourtView, DistanceCourt distanceCourt) {
        //没有选择节点或者没有找到相同的节点成功
        if (!distanceCourtView) {
            return true
        }
        //当前所选节点不会是原来当前节点
        def viewParent = distanceCourtView.parent
        if (!viewParent) {
            return true
        } else {
            if (viewParent == distanceCourt) {
                return false
            } else {
                isParent(viewParent, distanceCourt)
            }
        }
    }

    /**
     * 根据所选id删除远程法院
     * @param idsStr 所选id
     * @return 响应码
     */
    def del(String idsStr) {
        if (!idsStr) {
            return Resp.toJson(RespType.DATA_NOT_EXIST)
        }
        Set<String> serviceSet = new HashSet<>()
        def courtroomList = Courtroom.findAll()
        courtroomList.each {
            if (it.distance1) {
                serviceSet.add(it.distance1.split(",")[0])
            }
            if (it.distance2) {
                serviceSet.add(it.distance2.split(",")[0])
            }
        }
        def idsArr = idsStr.split(",")
        int flag = 0
        for (String id : idsArr) {
            def dc = DistanceCourt.get(id as Long)
            if (dc.children.size() == 0) {
                boolean isDelete = true
                for (int i = 0; i < serviceSet.size(); i++) {
                    if (serviceSet[i] == dc.service) {
                        isDelete = false
                    }
                }
                if (isDelete) {
                    dc.delete(flush: true)
                    if (dc.hasErrors()) {
                        log.error("[DistanceCourtService.del] 删除远程法院时发生错误,错误信息:${dc.errors}")
                        return Resp.toJson(RespType.FAIL)
                    }
                }
            } else {
                flag++
            }
        }
        return Resp.toJson(RespType.SUCCESS, flag)
    }
}
