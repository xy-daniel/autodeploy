package com.hxht.autodeploy.court.manager

import com.hxht.techcrt.MountDisk
import grails.gorm.transactions.Transactional

/**
 * 磁盘列表配置操作
 */
@Transactional
class MountDiskService {
    /**
     * 挂载磁盘路径列表
     * @param draw  标志
     * @param start  起始坐标
     * @param length  长度
     * @param search  搜索关键词
     * @return  List<MountDist>
     */
    def list(int draw, int start, int length, String search) {
        def model = [:]
        model.put("draw", draw)
        def count = MountDisk.createCriteria().count() {
            if (search) {
                or {
                    like("urlMount", "%${search}%")
                }
            }
        }
        def dataList = MountDisk.createCriteria().list {
            and {
                setMaxResults(length)
                setFirstResult(start)
            }
            or {
                like("urlMount", "%${search}%")
            }
            order("dateCreated","desc")
        } as List<MountDisk>
        def modelDataList = []
        for (def mountDisk : dataList) {
            def data = [:]
            data.put("id", mountDisk.id)
            data.put("urlMount", mountDisk.urlMount)
            data.put("totalSpace", mountDisk.totalSpace)
            data.put("usableSpace", mountDisk.usableSpace)
            data.put("freeSpace", mountDisk.freeSpace)
            modelDataList.add(data)
        }
        model.put("recordsTotal", count)//数据总条数
        model.put("recordsFiltered", count)//显示的条数
        model.put("data", modelDataList)
        model
    }

    /**
     * 挂载磁盘路径添加
     */
    def addSave(MountDisk mountDisk){
        mountDisk.save(flush:true)
        if (mountDisk.hasErrors()) {
            def msg = "[MountDiskService addSave MountDisk.save]磁盘列表添加保存失败 errors [${mountDisk.errors}]"
            log.error(msg)
            throw new RuntimeException(msg)
        }
    }

    /**
     * 磁盘列表删除
     * @param ids  多个id
     */
    def del(String[] ids){
        for (String id : ids){
            //获取磁盘路径
            def mountDisk = MountDisk.get(id)
            //执行删除
            mountDisk.delete(flush:true)
            if (mountDisk.hasErrors()) {
                def msg = "[MountDiskService del]磁盘列表删除失败 errors [${mountDisk.errors}]"
                log.error(msg)
                throw new RuntimeException(msg)
            }
        }
    }
}
