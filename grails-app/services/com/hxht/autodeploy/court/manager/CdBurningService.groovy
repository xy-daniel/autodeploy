package com.hxht.autodeploy.court.manager

import com.hxht.techcrt.court.mem.CdBurning
import grails.gorm.transactions.Transactional

/**
 * 光盘刻录列表配置操作
 */
@Transactional("mem")
class CdBurningService {
    /**
     * 光盘刻录路径列表
     * @param draw  标志
     * @param start  起始坐标
     * @param length  长度
     * @param search  搜索关键词
     * @return  List<CdBurning>
     */
    def list(int draw, int start, int length, String search) {
        def model = [:]
        model.put("draw", draw)
        def count = CdBurning.createCriteria().count() {
            if (search) {
                or {
                    like("url", "%${search}%")
                }
            }
        }
        def dataList = CdBurning.createCriteria().list {
            and {
                setMaxResults(length)
                setFirstResult(start)
            }
            if (search){
                or {
                    like("url", "%${search}%")
                }
            }
            order("orderNum","asc")
        } as List<CdBurning>
        def modelDataList = []
        for (def cDBurning : dataList) {
            def data = [:]
            data.put("id", cDBurning.id)
            data.put("url", cDBurning.url)
            data.put("orderNum", cDBurning.orderNum)
            modelDataList.add(data)
        }
        model.put("recordsTotal", count)//数据总条数
        model.put("recordsFiltered", count)//显示的条数
        model.put("data", modelDataList)
        model
    }

    /**
     * 光盘刻录路径添加
     */
    def addSave(CdBurning cDBurning){
        cDBurning.save(flush:true)
        if (cDBurning.hasErrors()) {
            def msg = "[CdBurningService addSave CdBurning.save]光盘刻录列表添加保存失败 errors [${cDBurning.errors}]"
            log.error(msg)
            throw new RuntimeException(msg)
        }
    }

    /**
     * 光盘刻录列表删除
     * @param ids  多个id
     */
    def del(String[] ids){
        for (String id : ids){
            //获取光盘刻录路径
            def cDBurning = CdBurning.get(id)
            //执行删除
            cDBurning.delete(flush:true)
            if (cDBurning.hasErrors()) {
                def msg = "[CdBurningService del]光盘刻录列表删除失败 errors [${cDBurning.errors}]"
                log.error(msg)
                throw new RuntimeException(msg)
            }
        }
    }
    
    def countUrl(String url){
        CdBurning.countByUrl(url)
    }
    
}
