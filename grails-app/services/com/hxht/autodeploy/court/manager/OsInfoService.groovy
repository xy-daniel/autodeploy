package com.hxht.autodeploy.court.manager

import com.hxht.techcrt.Dict
import com.hxht.techcrt.util.os.OSUtils
import grails.gorm.transactions.Transactional

@Transactional
class OsInfoService {

    def list(int draw) {
        def model = [:]
        model.put("draw", draw)
        //def memMap = ["memFree": "6666088KB", "memTotal": "12084112KB", "usage": "33.04961092714136%", "memUsed": "5418024KB"]
        //内存
        def memMap = OSUtils.memoryUsage()
        //磁盘
        def url = "/usr/local/movies"
        def file = new File(url)
        //磁盘总空间
        def totalSpace = file.getTotalSpace()
        //磁盘剩余空间
        def usableSpace = file.getUsableSpace()
        memMap.put("diskTotal", totalSpace as String)
        memMap.put("diskFree", usableSpace as String)
        memMap.put("address", url)
        def modelDataList = []
        modelDataList.add(memMap)
        model.put("recordsTotal", 1)
        model.put("recordsFiltered", 1)
        model.put("data", modelDataList)
        model
    }

    def editSave(def diskAlarm) {
        def dict = Dict.findByCode("CURRENT_MEM_ALARM")
        //dict.val = memAlarm
        dict.ext1 = diskAlarm
        dict.save(flush: true)
        if (dict.hasErrors()) {
            log.error("设置内存/磁盘报警空间失败。[${dict.errors}]")
        }
    }
}