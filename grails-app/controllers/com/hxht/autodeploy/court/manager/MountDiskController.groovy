package com.hxht.autodeploy.court.manager

import com.hxht.techcrt.LogSystemUtil
import com.hxht.techcrt.MountDisk
import com.hxht.techcrt.Resp
import com.hxht.techcrt.enums.RespType
import grails.converters.JSON

/**
 * 配置挂载磁盘
 */
class MountDiskController {

    MountDiskService mountDiskService

    /**
     * 磁盘路径配置列表
     * @return view="user.list"
     */
    def list() {
        if (request.method == "POST") {
            def draw = params.int("draw") ?: 1// 记录操作的次数 每次加1
            def start = params.int("start") ?: 0// 起始
            def length = params.int("length") ?: 20// 每页显示的size
            def search = params.get("search[value]") as String//搜索内容
            def model = mountDiskService.list(draw, start, length, search)
            render model as JSON
        }
    }

    /**
     * 前往添加磁盘路径配置页面
     */
    def add() {}

    /**
     * 保存磁盘路径配置
     */
    def addSave() {
        def urlMount = params.get("urlMount") as String
        def sfile = new File(urlMount)
        def totalSpace = sfile.getTotalSpace()   //总空间
        def usableSpace = sfile.getUsableSpace() //未使用空间空间
        def freeSpace = totalSpace - usableSpace //使用空间空间
        log.info("空间大小 总 已使用 剩余" + totalSpace + "，" + usableSpace + "，" + freeSpace)
        if (!urlMount || !totalSpace || !usableSpace || !freeSpace) {
            render Resp.toJson(RespType.DATA_NOT_EXIST)
            return
        }
        //添加
        def mountDisk = new MountDisk()
        mountDisk.urlMount = urlMount
        mountDisk.freeSpace = freeSpace
        mountDisk.totalSpace = totalSpace
        mountDisk.usableSpace = usableSpace
        mountDiskService.addSave(mountDisk)
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 删除部分磁盘路径配置
     * @return RespType.data
     */
    def del() {
        LogSystemUtil.log(LogSystemUtil.INFO, "删除部分磁盘路径配置")
        def mountIdStr = params.get("mountDiskIds") as String
        if (!mountIdStr) {
            render Resp.toJson(RespType.DATA_NOT_EXIST)
            return
        }
        def mountIdArr = mountIdStr.split(",")
        mountDiskService.del(mountIdArr)
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 前往修改磁盘路径配置信息页面
     */
    def edit() {
        //获取磁盘路径配置
        def mountDisk = MountDisk.get(params.long("id"))
        [mountDisk: mountDisk]
    }

    /**
     * 更新磁盘路径配置信息
     */
    def editSave() {
        //获取这个磁盘路径配置信息
        def urlMount = params.get("urlMount") as String
        def sfile = new File(urlMount)
        def mountId = params.long("mountId")
        def totalSpace = sfile.getTotalSpace()
        def usableSpace = sfile.getUsableSpace()
        def freeSpace = totalSpace - usableSpace
        if (!mountId || !urlMount || !totalSpace || !usableSpace || !freeSpace){
            render Resp.toJson(RespType.FAIL)
            return
        }
        def mountDisk = MountDisk.get(mountId)
        mountDisk.urlMount = urlMount
        mountDisk.freeSpace = freeSpace
        mountDisk.totalSpace = totalSpace
        mountDisk.usableSpace = usableSpace
        //执行修改
        mountDiskService.addSave(mountDisk)
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 根据磁盘路径名获取信息
     * @return RespType.data
     */
    def getMountByUrlMountname() {
        def count = MountDisk.countByUrlMount(params.get("urlMount") as String)
        render Resp.toJson(RespType.SUCCESS, count)
    }
}
