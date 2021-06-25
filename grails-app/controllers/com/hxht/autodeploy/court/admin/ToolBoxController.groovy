package com.hxht.autodeploy.court.admin

import cn.hutool.core.date.DateUtil
import com.hxht.techcrt.DataStatus
import com.hxht.techcrt.PlanStatus
import com.hxht.techcrt.court.CaseInfo
import com.hxht.techcrt.court.Courtroom
import com.hxht.techcrt.court.PlanInfo
import com.hxht.techcrt.court.TrialInfo
import com.hxht.techcrt.court.VideoInfo
import com.hxht.techcrt.utils.UUIDGenerator
import com.hxht.techcrt.utils.http.HttpUtil
import grails.converters.JSON
import grails.core.GrailsApplication
import grails.plugin.rtdatasources.RuntimeDataSourceService
import grails.plugin.springsecurity.annotation.Secured
import grails.plugin.springwebsocket.WebSocket
import org.apache.tomcat.jdbc.pool.DataSource
import org.apache.tomcat.jdbc.pool.DataSource as JdbcDataSource
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement
import groovy.sql.Sql

import java.text.SimpleDateFormat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * 管理员工具
 * 2021.04.20 >>> 修改书记员更新包上传路径 daniel
 */
@Secured(['ROLE_SUPER'])
class ToolBoxController implements WebSocket {
    RuntimeDataSourceService runtimeDataSourceService
    ToolBoxService toolBoxService
    GrailsApplication grailsApplication

    def about() {
        def tcVersion = grailsApplication.config.getProperty('tc.version')
        def streamVersion = HttpUtil.simplestGet("http://127.0.0.1:8791/version")
        def storeRespVersion = HttpUtil.simplestGet("http://127.0.0.1:2420/version")
        [tcVersion: tcVersion, streamVersion: streamVersion, storeRespVersion: storeRespVersion]
    }

    def migrate() {}

    def videoDuration() {}

    def pushShowVideoPlatform() {}

    @MessageMapping("/data_video_duration")
    @SendTo("/topic/data_video_duration")
    protected calVideoDuration(String data) {
        def json = JSON.parse data
        def videoPath = json.videoPath.toString()
        toolBoxService.dataVideoDuration(videoPath)
    }

    @MessageMapping("/showVideoPlatform")
    @SendTo("/topic/showVideoPlatform")
    protected showVideoPlatformWs() {
    }

    def showVideoPlatform() {
        toolBoxService.showVideoPlatform()
    }

    @MessageMapping("/data_import")
    @SendTo("/topic/data_import")
    protected dataImport(String data) {
        def json = JSON.parse data
        def jdbcUrl = json.jdbcUrl
        def u = json.username
        def p = json.password
//        runtimeDataSourceService.removeDataSource 'myDataSource'
        def runtimeDataSource = runtimeDataSourceService.addDataSource('myDataSource', JdbcDataSource) {
            driverClassName = "com.mysql.jdbc.Driver"
            url = jdbcUrl
            username = u
            password = p
        }
        convertAndSend "/topic/data_import", "Data import begins."
        def sql = new Sql(runtimeDataSource)
        try {
            toolBoxService.dataImportDict(sql)//导入字典
            toolBoxService.dataImportCaseType()//导入案件类型
            toolBoxService.dataImportCourtroom(sql)//导入法庭
            toolBoxService.dataImportDept(sql)//导入部门
            toolBoxService.dataImportEmployee(sql)//导入用户
            toolBoxService.dataImportCaseInfo(sql)//导入案件
            toolBoxService.dataImportPlanInfo(sql)//导入排期
            toolBoxService.dataImportTrialInfo(sql)//导入审判
            toolBoxService.dataImportVideoInfo(sql)//导入视频
        } catch (e) {
            e.printStackTrace()
            convertAndSend "/topic/data_import", "Stopped by ERROR!"
        } finally {
            convertAndSend "/topic/data_import", "Finished."
        }
    }

    /**
     * 山东威海法院使用sybase数据库直接读取数据
     * @param data
     * @return
     */
    @MessageMapping("/shandong_import")
    @SendTo("/topic/shandong_data_import")
    protected shandongDataImport(String data) {
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
            String url = "jdbc:jtds:sybase://192.168.0.203:5000/pubs2";// 数据库名
            Properties sysProps = System.getProperties();
            sysProps.put("user", "sa"); // 设置数据库访问用户名
            sysProps.put("password", "password"); // 密码
            Connection conn = DriverManager.getConnection(url, sysProps);
            Statement stmt = conn
                    .createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                            ResultSet.CONCUR_UPDATABLE);
            String sql = "select * from titles"; // 表
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                System.out.println(rs.getString(2)); // 取得第二列的值
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * 前往视频数据恢复页面
     */
    def revideo() {}

    /**
     * 测试数据删除
     */
    def deleteVideo() {}

    /**
     * 处理案件
     */
    def resetCase() {
        def myDataSource
        try {
            myDataSource = grailsApplication.mainContext.getBean("myDataSource")
        } catch (e) {
            println e.getMessage()
            myDataSource = runtimeDataSourceService.addDataSource('myDataSource', DataSource) {
                driverClassName = "com.mysql.jdbc.Driver"
                url = "jdbc:mysql://146.12.1.61:3306/dcs?useUnicode=true&characterEncoding=utf8&allowMultiQueries=true&zeroDateTimeBehavior=convertToNull"
                username = "root"
                password = "dcs2011"
            }
        }
        def sql = new Sql(myDataSource)
        def sqlStr = "select CONCAT(interface_id,\"!!!\",caseno,\"@@@\",casename,\"###\",summary,\"QWE\",accuse,\"WER\",accused,\"&&&\",casedate) as result from `case`"
        def caseData = sql.rows(sqlStr)
        render "<h1 style='color:red'>-------------------------开始处理案件:${caseData.size()}-------------------------</h1>"
        for (int i = 0; i < caseData.size(); i++) {
            def lineTxt = caseData[i]?.result
            if (!lineTxt) {
                continue
            }
            if (lineTxt.startsWith("!!!")) {
                render "<p style='color:green'>接口数据不存在,跳过.</p>"
                continue
            }
            if (lineTxt.contains("!!!@@@")) {
                render "<p style='color:green'>案号不存在,跳过.</p>"
                continue
            }
            String id = lineTxt.split("!!!")[0]
            String caseno = lineTxt.split("!!!")[1].split("@@@")[0]
            String casename = lineTxt.split("@@@")[1].split("###")[0]
            String summary = lineTxt.split("###")[1].split("QWE")[0]
            String yg = lineTxt.split("QWE")[1].split("WER")[0]
            if (!yg) {
                render "<p style='color:green'>原告不存在.</p>"
            }
            String bg = lineTxt.split("WER")[1].split("&&&")[0]
            if (!bg) {
                render "<p style='color:green'>被告不存在.</p>"
            }
            String dateStr = lineTxt.split("&&&")[1]
            CaseInfo caseInfo = CaseInfo.findBySynchronizationId(id)
            if (!caseInfo) {
                caseInfo = CaseInfo.findByArchives(caseno)
                if (!caseInfo) {
                    render "<p style='color:blue'>根据id和案号案号查询案件都不存在.</p>"
                    caseInfo = new CaseInfo(
                            uid: UUIDGenerator.nextUUID(),
                            synchronizationId: id,
                            archives: caseno,
                            name: casename,
                            summary: summary,
                            accuser: yg,
                            accused: bg,
                            active: DataStatus.SHOW,
                            filingDate: DateUtil.parse(dateStr, "yyyy-MM-dd HH:mm:ss")
                    )

                } else {
                    render "<p style='color:blue'>根据id查询案件不存在,但是案号查询存在,修改id.</p>"
                    caseInfo.synchronizationId = id
                }
                caseInfo.save()
                if (caseInfo.hasErrors()) {
                    log.info("修改案件的同步主键出错，错误信息:${caseInfo.errors}")
                }
            }
        }
        render "<h1 style='color:red'>--------------------------------------------------案件处理结束--------------------------------------------------</h1>"
    }

    /**
     * 处理排期
     */
    def resetPlan() {
        def myDataSource
        try {
            myDataSource = grailsApplication.mainContext.getBean("myDataSource")
        } catch (e) {
            println e.getMessage()
            myDataSource = runtimeDataSourceService.addDataSource('myDataSource', DataSource) {
                driverClassName = "com.mysql.jdbc.Driver"
                url = "jdbc:mysql://192.168.0.202:3306/dcs_shenzhen?useUnicode=true&characterEncoding=utf8&allowMultiQueries=true&zeroDateTimeBehavior=convertToNull"
                username = "root"
                password = "dcs2011"
            }
        }
        def sql = new Sql(myDataSource)
        render "<h1 style='color:red'>--------------------------------------------------开始查询排期数量--------------------------------------------------</h1>"
        def countSqlStr = "select count(*) as count from `plan`"
        def countData = sql.rows(countSqlStr).get(0).count as Integer
        render "<h1 style='color:red'>--------------------------------------------------查询排期数量完成:${countData},每次1000可以循环${Math.floor(countData / 1000) + 1}次----------------</h1>"
        //将请求传递到子线程
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes()
        RequestContextHolder.setRequestAttributes(servletRequestAttributes, true)
        //创建线程池
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(20)
        for (int i = 0; i < countData; i += 1000) {
            //取一个线程执行方法
            fixedThreadPool.execute({ ->
                PlanInfo.withNewSession {
                    log.info("线程名称:${Thread.currentThread().getName()},执行,开始节点:${i}")
                    resetPlanSingle(i, myDataSource)
                }
            })
            //线程阻塞
            sleep(1000)
        }
        //关闭线程池
        fixedThreadPool.shutdown()
        render "<h1 style='color:red'>--------------------------------------------------排期处理结束--------------------------------------------------</h1>"
    }

    /**
     * 处理1000个排期
     * @param startRows 开始节点
     */
    def resetPlanSingle(int startRows, def myDataSource) {
        def sql = new Sql(myDataSource)
        def planSqlStr = "select CONCAT(uid,\"AAA\",interfaceplan_id,\"QQQ\",case_id,\"WWW\",start_date,\"EEE\",end_date) as result from `plan` limit " + startRows + ",1000"
        def planData = sql.rows(planSqlStr)
        for (int i = 0; i < planData.size(); i++) {
            def lineTxt = planData[i]?.result
            if (!lineTxt) {
                continue
            }
            lineTxt = lineTxt as String
            if (lineTxt.contains("AAAQQQ")) {
//                println "<p style='color:green'>接口数据不存在,跳过.</p>"
                continue
            }
            if (lineTxt.contains("QQQWWW")) {
//                println "<p style='color:green'>案件信息不存在,跳过.</p>"
                continue
            }
            String uid = lineTxt.split("AAA")[0]
            String id = lineTxt.split("AAA")[1].split("QQQ")[0]
            String caseId = lineTxt.split("QQQ")[1].split("WWW")[0]
            String startDate = lineTxt.split("WWW")[1].split("EEE")[0]
            String endDate = lineTxt.split("EEE")[1]
            PlanInfo planInfo = PlanInfo.findBySynchronizationId(id)
            if (!planInfo) {
                CaseInfo caseInfo = CaseInfo.findBySynchronizationId(caseId)
                if (caseInfo) {
                    log.info("<排期不存在,但是案件存在,处理.")
                    //舍弃书记员、法官、承办人等数据
                    planInfo = new PlanInfo(
                            uid: UUIDGenerator.nextUUID(),
                            caseInfo: caseInfo,
                            status: PlanStatus.CLOSED,
                            synchronizationId: id,
                            active: DataStatus.SHOW,
                            startDate: DateUtil.parse(startDate, "yyyy-MM-dd HH:mm:ss"),
                            endDate: DateUtil.parse(endDate, "yyyy-MM-dd HH:mm:ss"),
                            courtroom: Courtroom.first(),
                            allowPlay: 0
                    )
                    planInfo.save()
                    if (planInfo.hasErrors()) {
                        log.error("[添加plan时出错，错误信息:${planInfo.errors}]")
                    }
                    //添加完排期之后添加trialInfo,对应排期
                    log.info("开始添加Trial")
                    def trialInfo = new TrialInfo(
                            uid: UUIDGenerator.nextUUID(),
                            startDate: DateUtil.parse(startDate, "yyyy-MM-dd HH:mm:ss"),
                            endDate: DateUtil.parse(endDate, "yyyy-MM-dd HH:mm:ss"),
                            status: PlanStatus.CLOSED,
                            active: DataStatus.SHOW,
                            synchronizationId: id,
                            planInfo: planInfo,
                            courtroom: Courtroom.first()
                    )
                    trialInfo.save()
                    if (trialInfo.hasErrors()) {
                        log.error("[添加trial时出错，错误信息:${trialInfo.errors}]")
                    }
                    //添加完trialInfo之后太那几videoInfo
                    def videoSql = "select uid,chn as channelNum,channelsname as channelName,filename as fileName from recordinfo where plan_id = '${uid}'"
                    def videoData = sql.rows(videoSql)
                    log.info("${uid}:开始添加Video:${videoData.size()}")
                    for (int j = 0; j < videoData.size(); j++) {
                        if (!videoData[j]) {
                            log.info("视频数据为空，跳过.")
                            continue
                        }
                        def videoUid = videoData[j].uid
                        def channelNum = videoData[j].channelNum
                        def channelName = videoData[j].channelName
                        def fileName = videoData[j].fileName
                        def nameArr = fileName.split("_")
                        def sdf = new SimpleDateFormat("yyyyMMddHHmmss")
                        Date startRecTime = sdf.parse(nameArr[nameArr.size() - 3] + nameArr[nameArr.size() - 2])
                        def videoInfo = new VideoInfo(
                                uid: UUIDGenerator.nextUUID(),
                                active: DataStatus.SHOW,
                                synchronizationId: videoUid,
                                channelNum: channelNum,
                                channelName: channelName,
                                fileName: fileName,
                                startRecTime: startRecTime,
                                trialInfo: trialInfo
                        )
                        videoInfo.save()
                        if (videoInfo.hasErrors()) {
                            log.error("[添加video时出错，错误信息:${videoInfo.errors}]")
                        }
                    }
                } else {
                    log.info("${uid}:排期不存在,案件也不存在,废弃.")
                }
            }
        }
    }
}
