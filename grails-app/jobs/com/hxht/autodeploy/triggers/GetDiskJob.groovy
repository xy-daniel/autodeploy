package com.hxht.autodeploy.triggers

import cn.hutool.core.date.DateUtil
import com.hxht.techcrt.Alarm
import com.hxht.techcrt.court.manager.info.AlarmService

import java.nio.charset.StandardCharsets

/**
 *  每日凌晨两点向服务器创建一个文件，并读取文件内容，确保服务器磁盘可写可读
 */
class GetDiskJob {

    AlarmService alarmService


    static triggers = {
        cron cronExpression: "0 00 02 * * ? *"//凌晨两点执行一次
        //cron cronExpression: "15 * * * * ?"
    }

    def execute() {
        File file = new File("/usr/local/movies/test.txt")
        FileOutputStream outputStream = null
        BufferedReader reader = null
        try {
            if (file.exists()) {
                file.delete()
            }
            outputStream = new FileOutputStream(file, true)
            outputStream.write(("当前测试时间为：" + DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss")).getBytes(StandardCharsets.UTF_8))
            //开始读取
            StringBuffer sbf = new StringBuffer()
            reader = new BufferedReader(new FileReader(file))
            String tempStr
            while ((tempStr = reader.readLine()) != null) {
                sbf.append(tempStr)
            }
            reader.close()
            log.info("磁盘正常写入读取：" + sbf.toString())
            if (sbf.size() > 0) {
                file.delete()
            } else {
                //记录报警信息
                alarmService.addSave(new Alarm(alarmType: 2, alarmInfo: "磁盘写入失败,请检查磁盘！"))
            }
        } catch (IOException e) {
            e.printStackTrace()
        } finally {
            if (reader != null) {
                reader.close()
            }
            if (outputStream != null) {
                outputStream.close()
            }
        }
    }
}