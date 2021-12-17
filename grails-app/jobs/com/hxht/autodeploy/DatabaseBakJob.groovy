package com.hxht.autodeploy

import com.hxht.autodeploy.device.DataFile
import com.hxht.autodeploy.device.DataTable
import com.hxht.autodeploy.utils.SshUtil

class DatabaseBakJob {

    static triggers = {
        cron cronExpression: "0 0 0 1/1 * ? " //每天执行一次
    }

    def execute() {
        def dataTableList = DataTable.all
        dataTableList.each {
            String filePath = SshUtil.mysqldump(it)
            new DataFile(
                    database: it,
                    address: filePath,
                    size: Math.floor(new File(filePath).size() / 1024 / 1024) as long
            ).save(flush: true)
        }
    }
}
