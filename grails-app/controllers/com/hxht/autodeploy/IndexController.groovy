package com.hxht.autodeploy

import org.apache.commons.lang.SystemUtils

import com.hxht.autodeploy.enums.Resp
import com.hxht.autodeploy.enums.RespType
import com.hxht.autodeploy.app.App
import com.hxht.autodeploy.device.Device
import com.hxht.autodeploy.device.DataTable

class IndexController {

    /**
     * 控制台首页
     * @return page
     */
    def index() {
        [
                "os"       : SystemUtils.OS_NAME + "_" + SystemUtils.OS_ARCH,
                "device"   : Device.count,
                "dataTable": DataTable.count,
                "app"      : App.count,
                "task"     : Task.countByDel("no")
        ]
    }

    /**
     * 监测分析数据
     */
    def memory_space() {
        render Resp.toJson(RespType.SUCCESS, OSAnalysisJob.oSAnalysisData)
    }

}
