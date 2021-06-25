package com.hxht.autodeploy.api

import com.hxht.techcrt.CollegialType
import com.hxht.techcrt.CopyVideoLog
import com.hxht.techcrt.DataStatus
import com.hxht.techcrt.DeviceType
import com.hxht.techcrt.court.*
import com.hxht.techcrt.utils.DateUtils
import com.hxht.techcrt.utils.http.HttpUtil
import com.hxht.techcrt.utils.UUIDGenerator
import grails.converters.JSON
import grails.core.GrailsApplication
import grails.events.EventPublisher
import grails.gorm.transactions.Transactional
import grails.plugin.springwebsocket.WebSocket
import org.apache.commons.io.FileUtils
import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.ftp.FTPReply
import org.springframework.web.multipart.MultipartFile
import ws.schild.jave.MultimediaObject

@Transactional()
class ApiOfflinePlanService implements WebSocket, EventPublisher{
    GrailsApplication grailsApplication

    /**
     * 将排期，庭审保存
     * @param planInfo
     * @param courtroom
     * @param judge
     * @param secretary
     * @return 庭审对象
     */
    def planOffline(PlanInfo planInfo, Courtroom courtroom, Employee judge, Employee secretary, Integer status, String uuid4trial) {
        //为排期赋值状态
        planInfo.status = status
        planInfo.save(flush: true)
        if (planInfo.hasErrors()) {
            def msg = "[ApiOfflinePlanService planOffline]离线开庭后保存排期失败,错误信息:[${planInfo.errors}]"
            log.error(msg)
            throw new RuntimeException(msg)
        }
        def trialInfo = new TrialInfo()
        trialInfo.uid = uuid4trial
        trialInfo.planInfo = planInfo
        trialInfo.startDate = new Date()
        trialInfo.courtroom = courtroom
        trialInfo.judge = judge
        trialInfo.secretary = secretary
        trialInfo.status = status
        trialInfo.active = DataStatus.SHOW
        trialInfo.save(flush: true)
        if (trialInfo.hasErrors()) {
            def msg = "[ApiOfflinePlanService planOffline]离线庭审失败,保存trial 失败 errors [${trialInfo.errors}]"
            log.error(msg)
            throw new RuntimeException(msg)
        }
        trialInfo
    }

    /**
     * @return 排期id
     */
    def saveCasePlan(CaseInfo caseInfo, PlanInfo planInfo, def collegialArr, Courtroom courtroom, Employee judge, Employee secretary, Integer status, String uuid4trial) {
        caseInfo.uid = UUIDGenerator.nextUUID()
        caseInfo.active = DataStatus.SHOW
        caseInfo.save(flush: true)
        if (caseInfo.hasErrors()) {
            log.error("[ApiOfflinePlanService.saveCasePlan]保存离线案件时发生错误,错误信息:[${caseInfo.errors}]")
            throw new RuntimeException()
        }
        def collegialList = []
        if (collegialArr instanceof String) {
            collegialArr = [collegialArr]
        }
        for (def c : collegialArr) {
            def collegial = new Collegial()
            def typeIndex = c.indexOf("(")
            def cname = c.substring(0, (typeIndex > 0) ? typeIndex : c.length())
            collegial.name = cname
            if (c.indexOf(CollegialType.getString(CollegialType.PERSIDING_JUDGE)) > 0) {
                collegial.type = CollegialType.PERSIDING_JUDGE
            } else if (c.indexOf(CollegialType.getString(CollegialType.JUDGE)) > 0) {
                collegial.type = CollegialType.JUDGE
            } else if (c.indexOf(CollegialType.getString(CollegialType.PEOPLE_ASSESSOR)) > 0) {
                collegial.type = CollegialType.PEOPLE_ASSESSOR
            }
            if (!collegial.type) {
                collegial.type = CollegialType.OTHER
            }
            collegial.save(flush: true)
            if (collegial.hasErrors()) {
                log.info("临时离线案件时ApiOfflinePlanService.saveCasePlan collegial [${collegial.errors}]")
                throw new RuntimeException()
            }
            collegialList.add(collegial)
        }
        planInfo.collegial = collegialList
        planInfo.active = DataStatus.SHOW
        planInfo.uid = UUIDGenerator.nextUUID()
        planInfo.status = status
        planInfo.caseInfo = caseInfo
        planInfo.save(flush: true)
        if (planInfo.hasErrors()) {
            log.info("保存离线案件时ApiOfflinePlanService.saveCasePlan planInfo [${planInfo.errors}]")
            throw new RuntimeException()
        }
        def trialInfo = new TrialInfo()
        trialInfo.uid = uuid4trial
        trialInfo.planInfo = planInfo
        trialInfo.startDate = new Date()
        trialInfo.courtroom = courtroom
        trialInfo.judge = judge
        trialInfo.secretary = secretary
        trialInfo.startDate = planInfo.startDate
        trialInfo.endDate = planInfo.endDate
        trialInfo.status = status
        trialInfo.active = DataStatus.SHOW
        trialInfo.save(flush: true)
        if (trialInfo.hasErrors()) {
            def msg = "[ApiOfflinePlanService saveCasePlan]离线庭审失败,保存trial 失败 errors [${trialInfo.errors}]"
            log.error(msg)
            throw new RuntimeException(msg)
        }
        trialInfo
    }


    /**
     * @return 排期id
     */
    def saveCasePlanTrial(def caseInfo, def planInfo, def trialInfoList) {
        caseInfo.active = DataStatus.SHOW
        caseInfo.save(flush: true)
        if (caseInfo.hasErrors()) {
            log.error("[ApiOfflinePlanService.saveCasePlan]保存离线案件时发生错误,错误信息:[${caseInfo.errors}]")
            throw new RuntimeException()
        }

        planInfo.caseInfo = caseInfo
        planInfo.active = DataStatus.SHOW
        planInfo.save(flush: true)
        if (planInfo.hasErrors()) {
            log.info("保存离线案件时ApiOfflinePlanService.saveCasePlan planInfo [${planInfo.errors}]")
            throw new RuntimeException()
        }
        def trialList = new ArrayList<TrialInfo>()
        for (def trialInfo : trialInfoList) {
            def trial = TrialInfo.findByUid(trialInfo.uid)
            if (!trial){
                trial = new TrialInfo()
            }
            trial.uid = trialInfo.uid
            trial.planInfo = planInfo
            trial.status = trialInfo.status as Integer
            if (trialInfo.courtroomId){
                trial.courtroom = Courtroom.get(trialInfo.courtroomId as long)
            }else{
                trial.courtroom = planInfo.courtroom
            }
            if (trialInfo.judgeId){
                trial.judge = Employee.get(trialInfo.judgeId as long)
            }else{
                trial.judge = planInfo.judge
            }
            if (trialInfo.secretaryId){
                trial.secretary = Employee.get(trialInfo.secretaryId as long)
            }else{
                trial.secretary = planInfo.secretary
            }
            if (trialInfo.startDate){
                trial.startDate = DateUtils.str2Date(trialInfo.startDate)
            }
            if (trialInfo.endDate){
                trial.endDate = DateUtils.str2Date(trialInfo.endDate)
            }
            trial.status = trialInfo.status as Integer
            trial.active = DataStatus.SHOW


            trial.save(flush: true)
            if (trial.hasErrors()) {
                def msg = "[ApiOfflinePlanService saveCasePlan]离线庭审失败,保存trial 失败 errors [${trial.errors}]"
                log.error(msg)
                throw new RuntimeException(msg)
            }
            trialList.add(trial)
            //向CMP推送数据
            this.notify("pushCmpCaseAndPlanAndTrial",null,null,trial.id)
        }
        //向CMP平台推送数据
        this.notify("pushCmpCaseAndPlanAndTrial",null,planInfo.id,null)
        //返回trialList
        trialList

    }

    /**
     * 该目录不存在
     */
    public static final String DIR_NOT_EXIST = "该目录不存在"

    /**
     * "该目录下没有文件
     */
    public static final String DIR_CONTAINS_NO_FILE = "该目录下没有文件"
    /**
     * FTP端口
     **/
    private int ftpPort = 21
    /**
     * FTP用户名
     **/
    private String ftpUsername = "ftp"
    /**
     * FTP密码
     **/
    private String ftpPassword = "ftp"
    /**
     * FTP基础目录
     **/
//    private String basePath = "mnt/HD0/"
    private String basePath = ""

    /**
     * 本地字符编码
     **/
    private static String localCharset = "GBK"

    /**
     * FTP协议里面，规定文件名编码为iso-8859-1
     **/
    private static String serverCharset = "ISO-8859-1"

    /**
     * UTF-8字符编码
     **/
    private static final String CHARSET_UTF8 = "UTF-8"

    /**
     * OPTS UTF8字符串常量
     **/
    private static final String OPTS_UTF8 = "OPTS UTF8"

    /**
     * 设置缓冲区大小4M
     **/
    private static final int BUFFER_SIZE = 1024 * 1024 * 4

    /**
     * FTPClient对象
     **/
    private static FTPClient ftpClient = null

    /**
     * 连接FTP服务器
     *
     * @param address  地址，如：127.0.0.1
     * @param port     端口，如：21
     * @param username 用户名，如：root
     * @param password 密码，如：root
     */
    private static void login(String address, int port, String username, String password) {
        ftpClient = new FTPClient()
        try {
            ftpClient.connect(address, port)
            ftpClient.login(username, password)
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE)
            //限制缓冲区大小
            ftpClient.setBufferSize(BUFFER_SIZE)
            int reply = ftpClient.getReplyCode()
            if (!FTPReply.isPositiveCompletion(reply)) {
                closeConnect()
                println "FTP服务器连接失败"
            }
        } catch (Exception e) {
            println "FTP服务器连接失败,错误信息：\n${e.message}"
        }
    }

    /**
     * FTP服务器路径编码转换
     *
     * @param ftpPath FTP服务器路径
     * @return String
     */
    private static String changeEncoding(String ftpPath) {
        if (FTPReply.isPositiveCompletion(ftpClient.sendCommand(OPTS_UTF8, "ON"))) {
            localCharset = CHARSET_UTF8
        }
        new String(ftpPath.getBytes(localCharset), serverCharset)
    }

    /**
     * 关闭FTP连接
     */
    private static void closeConnect() {
        if (ftpClient != null && ftpClient.isConnected()) {
            ftpClient.logout()
            ftpClient.disconnect()
        }
    }

    /**
     * 书记员离线开庭后的视频下载
     * @param planId  排期id
     * @param deviceIp  庭审主机ip
     * @param taskId  视频签名
     */
    def downloadVideo(Long trialInfoId, Integer version){
        def trialInfo = TrialInfo.get(trialInfoId)
        def planId = trialInfo.planInfoId
        def deviceMap = [:]
        def cfg = JSON.parse(trialInfo.courtroom.cfg)//获取通道信息配置
        for (def encode : cfg.encode) {
            deviceMap.put(encode.encodeip + "_" + encode.number, encode)
        }
        if (version == 1){
            log.info("[ApiOfflinePlanService.downloadVideo]---->ftp下载")
            //ftp:deviceIp/mnt/HD0/trialInfo.uid/xxx.mp4
            //ftp地址
            def ftpAddress = trialInfo.courtroom.deviceIp
            log.info("[ApiOfflinePlanService.downloadVideo]---->ftp下载视频文件庭审主机地址:${ftpAddress}")
            //文件夹名称
            def directoryName = trialInfo.uid
            log.info("[ApiOfflinePlanService.downloadVideo]---->ftp下载视频文件文件夹名称:${directoryName}")
            log.info("[ApiOfflinePlanService.downloadVideo]---->ftp开始登录")
            login(ftpAddress, ftpPort, ftpUsername, ftpPassword)
            if (ftpClient){
                try {
                    log.info("[ApiOfflinePlanService.downloadVideo]---->ftp登录成功")
                    log.info("[ApiOfflinePlanService.downloadVideo]---->ftp判断是否存在此文件夹")
                    String path = changeEncoding(basePath + directoryName)
                    log.info("[ApiOfflinePlanService.downloadVideo]---->ftp文件夹路径:${path}")
                    if (!ftpClient.changeWorkingDirectory(path)) {
                        log.error("[ApiOfflinePlanService.downloadVideo]---->" + basePath + directoryName + DIR_NOT_EXIST + "下载结束")
                        VideoDownFailRecord.withNewSession {
                            def vdfr = new VideoDownFailRecord(
                                    taskId: trialInfo.uid,
                                    trialInfo: trialInfo,
                                    handlerSuccess: false,
                                    ver : DeviceType.FTP //ftp主机
                            ).save(flush: true)
                            if (vdfr.hasErrors()){
                                log.error("[ApiOfflinePlanService.downloadVideo]ftp文件夹不存在，记录错误信息VideoDownFailRecord失败.错误信息：${vdfr.errors}" )
                            }
                        }
                        //此时应该把记录记录在失败的数据表格中
                        return Boolean.FALSE
                    }
                    //如果能走到这儿说明文件夹存在，此时应该去获取文件夹中的文件名称
                    log.info("[ApiOfflinePlanService.downloadVideo]---->文件夹存在，准备去获取文件夹中的文件名称")
                    //遍历文件名称把文件下载下来
                    ftpClient.enterLocalPassiveMode()  // 设置被动模式，开通一个端口来传输数据
                    String[] fs = ftpClient.listNames()
                    log.info("[ApiOfflinePlanService.downloadVideo]---->文件夹中存在文件,文件个数为：${fs.size()},文件名称为：${fs.toString()}")
                    if (fs == null || fs.length == 0) {
                        log.error("[ApiOfflinePlanService.downloadVideo]---->" + basePath + directoryName + DIR_CONTAINS_NO_FILE + "下载结束")
                        VideoDownFailRecord.withNewSession {
                            def vdfr = new VideoDownFailRecord(
                                    taskId: trialInfo.uid,
                                    trialInfo: trialInfo,
                                    handlerSuccess: false,
                                    ver : DeviceType.FTP //ftp主机
                            ).save(flush: true)
                            if (vdfr.hasErrors()){
                                log.error("[ApiOfflinePlanService.downloadVideo]ftp文件夹存在，但是文件夹内没有任何MP4文件，记录错误信息VideoDownFailRecord失败.错误信息：${vdfr.errors}" )
                            }
                        }
                        return Boolean.FALSE
                    }
                    for (String ff : fs) {
                        log.info("[ApiOfflinePlanService.downloadVideo]---->未经转载ftp文件名称")
                        String ftpName = new String(ff.getBytes(serverCharset), localCharset)
                        log.info("[ApiOfflinePlanService.downloadVideo]---->文件名称${ftpName}")
                        //排期目录
                        File planDirect = new File("/usr/local/movies/${planId}")
                        if (!planDirect.exists()){
                            log.info("[ApiOfflinePlanService.downloadVideo]---->排期：${planId}文件夹不存在,准备创建文件夹")
                            planDirect.mkdirs()
                            log.info("[ApiOfflinePlanService.downloadVideo]---->排期：${planId}文件夹不存在,创建文件夹成功")
                        }else{
                            log.info("[ApiOfflinePlanService.downloadVideo]---->排期：${planId}文件夹存在,无需创建")
                        }
                        planDirect.setReadable(true, false)
                        planDirect.setWritable(true,false)
                        planDirect.setExecutable(true,false)
                        //日期目录
                        def arr = ff.split("_")
                        def ip = arr[0]
                        def chnNum = (arr[1] as Integer) + 1
                        def directDate = arr[2].substring(0,4) + "-" + arr[2].substring(4,6) + "-" + arr[2].substring(6,8)
                        File dateDirect = new File("/usr/local/movies/${planId}/${directDate}")
                        if (!dateDirect.exists()){
                            log.info("[ApiOfflinePlanService.downloadVideo]---->日期：${directDate}文件夹不存在,准备创建文件夹")
                            dateDirect.mkdirs()
                            log.info("[ApiOfflinePlanService.downloadVideo]---->日期：${directDate}文件夹不存在,创建文件夹成功")
                        }else{
                            log.info("[ApiOfflinePlanService.downloadVideo]---->日期：${directDate}文件夹存在,无需创建")
                        }
                        dateDirect.setReadable(true, false)
                        dateDirect.setWritable(true,false)
                        dateDirect.setExecutable(true,false)
                        Date startDate = trialInfo.startDate
                        try {
                            def fileDate = arr[2].substring(0,8) + "_" + arr[2].substring(8,14)
                            def fileName = ip + "_" + fileDate + "_" + chnNum + ".mp4"
                            File file = new File("/usr/local/movies/${planId}/${directDate}/${fileName}")
                            if (!file.exists()){
                                log.info("[ApiOfflinePlanService.downloadVideo]---->${fileName}文件不存在，准备执行下载")
                                OutputStream os = new FileOutputStream(file)
                                //执行下载
                                ftpClient.retrieveFile(ff, os)
                                log.info("[ApiOfflinePlanService.downloadVideo]---->ftp文件：${ftpName}以${fileName}为文件名下载完成")
                            }else{
                                log.info("[ApiOfflinePlanService.downloadVideo]---->${fileName}文件存在")
                            }
                            //视频信息分析
                            File source = new File("/usr/local/movies/${planId}/${directDate}/${fileName}")
                            source.setReadable(true, false)
                            source.setWritable(true,false)
                            source.setExecutable(true,false)
                            def multiObj = new MultimediaObject(source)
                            //获取视频时长
                            def length = multiObj.getInfo().getDuration()
                            Date endDate = new Date(startDate.getTime() + length)
                            def filePath = "${planId}/${directDate}/${fileName}"
                            VideoInfo.withNewSession {
                                def v = VideoInfo.findByTrialInfoAndFileName(trialInfo, filePath)
                                if (!v){
                                    //视频下载成功写入数据库
                                    def videoInfo = new VideoInfo(
                                            uid: UUIDGenerator.nextUUID(),
                                            channelNum: chnNum,    //通道号
                                            channelName: deviceMap.get(ip + "_" + chnNum)?.name,    //通道名称
                                            startRecTime: startDate,    //开始录像时间
                                            endRecTime: endDate,    //结束录像时间
                                            fileName: filePath,    //视频文件名称
                                            size: new FileInputStream(source).getChannel().size(),//文件大小
                                            length: length / 1000,    //录像时长
                                            active: DataStatus.SHOW,    //数据状态
                                            trialInfo: trialInfo    //所属庭审
                                    ).save(flush: true)
                                    if (videoInfo.hasErrors()){
                                        log.error("[ApiOfflinePlanService downloadVideo]保存视频信息时失败.\n错误信息：${videoInfo.errors}")
                                    }
                                }
                            }
                        } catch (Exception e) {
                            log.error(e.getMessage())
                            VideoDownFailRecord.withNewSession {
                                def vdfr = new VideoDownFailRecord(
                                        trialInfo: trialInfo,
                                        videoMessage: "${trialInfo.uid}/${ftpName}",
                                        startRecTime: startDate,
                                        handlerSuccess: false,
                                        ver : DeviceType.FTP //ftp主机
                                ).save()
                                if (vdfr.hasErrors()){
                                    log.error("[ApiOfflinePlanService downloadVideo]ftp保存单个视频文件失败后存储错误信息VideoDownFailRecord失败.错误信息：${vdfr.errors}" )
                                }
                            }
                        }
                    }
                }catch(Exception error){
                    log.error("[ApiOfflinePlanService.downloadVideo]---->ftp登陆成功,但是在执行下载过程之前出现问题,错误信息：\n${error.getMessage()}")
                    //数据库中的这个数据不存在新增一条数据，存在不做改变
                    VideoDownFailRecord.withNewSession {
                        def vdfr = new VideoDownFailRecord(
                                taskId: trialInfo.uid,
                                trialInfo: trialInfo,
                                handlerSuccess: false,
                                ver : DeviceType.FTP //ftp主机
                        ).save(flush: true)
                        if (vdfr.hasErrors()){
                            log.error("[ApiOfflinePlanService.downloadVideo]---->ftp登陆成功,但是在执行下载过程之前出现问题,存储错误信息VideoDownFailRecord失败.错误信息：${vdfr.errors}" )
                        }
                    }
                }finally {
                    closeConnect()
                }
            }else{
                log.error("[ApiOfflinePlanService.downloadVideo]---->ftp登录失败，将数据记录在数据库中")
                //数据库中的这个数据不存在新增一条数据，存在不做改变
                VideoDownFailRecord.withNewSession {
                    def vdfr = new VideoDownFailRecord(
                            taskId: trialInfo.uid,
                            trialInfo: trialInfo,
                            handlerSuccess: false,
                            ver : DeviceType.FTP //ftp主机
                    ).save(flush: true)
                    if (vdfr.hasErrors()){
                        log.error("[ApiOfflinePlanService.downloadVideo]ftp登录失败后存储错误信息VideoDownFailRecord失败.错误信息：${vdfr.errors}" )
                    }
                }
            }
        }else{
            Date initTime = trialInfo.startDate
            int i = 0
            def videoList = []
            while (true){
                def model = [
                        method: "getVideoFileByTaskIdProfiles",
                        params: [
                                taskId: "${trialInfo.uid}",
                                offset: i,
                                limit : 1
                        ]
                ]
                try {
                    def resp = HttpUtil.postToJson("http://${trialInfo.courtroom.deviceIp}/web/page/activeProtocol.action", model)
                    def params = resp?.result?.params
                    def videoFileProfilesSize = params?.videoFileProfiles?.size()
                    if (params?.retCode == 0 && videoFileProfilesSize > 0) {
                        def videoFileProfiles = params?.videoFileProfiles?.get(0)
                        def token = videoFileProfiles.token as String
                        def recordUrl = videoFileProfiles.recordUrl as String
                        def cArr = recordUrl.split("/")
                        def ip = cArr[2]
                        def c = cArr[cArr.length - 1]
                        def name = videoFileProfiles.name as String
                        def nameArr = name.split("_")
                        def y = nameArr[1]
                        def mf = nameArr[2]
                        def mfArr = mf.split("\\.")
                        def m = mfArr[0]
                        def f = mfArr[1]
                        videoList.add([
                                ip      : ip,
                                c       : c,
                                token   : token,
                                fileName: "${ip}_${y}_${m}_${c}.${f}"
                        ])
                        i++
                    }else{
                        break
                    }
                } catch (e){
                    //数据库中的这个数据不存在新增一条数据，存在不做改变
                    VideoDownFailRecord.withNewSession {
                        def vdfr = new VideoDownFailRecord(
                                taskId: trialInfo.uid,
                                trialInfo: trialInfo,
                                handlerSuccess: false,
                                ver : DeviceType.HTTP //http主机
                        ).save(flush: true)
                        if (vdfr.hasErrors()){
                            log.error("[ApiOfflinePlanService downloadVideo]根据taskId获取视频失败后存储错误信息VideoDownFailRecord失败.错误信息：${vdfr.errors}" )
                        }
                    }
                    log.error("[ApiOfflinePlanService downloadVideo]根据taskId获取视频失败.\n参数：[model : ${model as JSON}, planId : ${planId}" +
                            "\n错误信息：[${e.printStackTrace()}]" )
                    return
                }
            }
            def filePrefix = grailsApplication.config.getProperty('tc.trial.note.path')
            for (def video : videoList) {
                try {
                    def videoDate = (video.fileName as String).split("_")[1]
                    def url = "http://${trialInfo.courtroom.deviceIp}/web/page/activeProtocol.action/download?fileType=videoFile&token=${video.token}"
                    def file = "${filePrefix}${planId}/${videoDate}/${video.fileName}"
//                    def file = "F://usr//local//movies//${planId}//${videoDate}//${video.fileName}"
                    FileUtils.copyURLToFile(new URL(url), new File(file))
                    File source = new File(file)
                    def multiObj = new MultimediaObject(source)
                    //获取视频时长
                    def length = multiObj.getInfo().getDuration()
                    Date endDate = new Date(initTime.getTime() + length)
                    def fileName = "${planId}/${videoDate}/${video.fileName}"
                    VideoInfo.withNewSession {
                        def v = VideoInfo.findByTrialInfoAndFileName(trialInfo, fileName)
                        if (!v){
                            //视频下载成功写入数据库
                            def videoInfo = new VideoInfo(
                                    uid: UUIDGenerator.nextUUID(),
                                    channelNum: video.c,    //通道号
                                    channelName: deviceMap.get(video.ip + "_" + video.c)?.name,    //通道名称
                                    startRecTime: initTime,    //开始录像时间
                                    endRecTime: endDate,    //结束录像时间
                                    fileName: "${planId}/${videoDate}/${video.fileName}",    //视频文件名称
                                    size: new FileInputStream(source).getChannel().size(),//文件大小
                                    length: length / 1000,    //录像时长
                                    active: DataStatus.SHOW,    //数据状态
                                    trialInfo: trialInfo    //所属庭审
                            ).save(flush: true)
                            if (videoInfo.hasErrors()){
                                log.error("[ApiOfflinePlanService downloadVideoByTaskId]保存视频信息时失败.\n" +
                                        "错误信息：${videoInfo.errors}")
                            }
                        }
                    }
                    initTime = endDate
                } catch (e) {
                    VideoDownFailRecord.withNewSession {
                        def vdfr = new VideoDownFailRecord(
                                trialInfo: trialInfo,
                                videoMessage: "${video.ip}/${video.c}/${video.token}/${video.fileName}",
                                startRecTime: initTime,
                                handlerSuccess: false,
                                ver : DeviceType.HTTP //ftp主机
                        ).save()
                        if (vdfr.hasErrors()){
                            log.error("[ApiOfflinePlanService downloadVideo]根据taskId获取视频失败后存储错误信息VideoDownFailRecord失败.错误信息：${vdfr.errors}" )
                        }
                    }
                    log.error("[ApiOfflinePlanService downloadVideo] 视频下载或视频信息入库失败.\n" +
                            "视频信息：token=${video.token},\n" +
                            "错误信息：errors=[${e.printStackTrace()}]")
                }
            }
        }
    }

    def uploadFile(String path, TrialInfo trialInfo, MultipartFile file){
        //庭审主键_文件名
        def filePath = "${trialInfo.id}_${file.getOriginalFilename()}"
        def sfile = new File("${path}/${trialInfo.planInfo.id}/", filePath)
        if (!sfile.exists()) {
            sfile.getParentFile().mkdirs()
        }
        file.transferTo(sfile)
        def data = trialInfo.comment
        def getPath = "/tc/api/client/trial/getComment/${trialInfo.planInfo.id}/${filePath}" as String
        if (!data){
            trialInfo.comment = getPath
        }else{
            if (data.indexOf("${getPath}") == -1){
                trialInfo.comment += ",${getPath}"
            }
        }
        saveTrial(trialInfo)
        if (trialInfo.hasErrors()) {
            log.error("保存离线庭审记录时时出错ApiOfflinePlanService.uploadFile trialInfo [${trialInfo.errors}]")
            throw new RuntimeException()
        }
        //这个地址不仅需要存储到数据库而且需要发送给书记员软件
        getPath
    }

    def handleMoreFailVideo(VideoDownFailRecord vdfrFromDb){
        TrialInfo trialInfo = vdfrFromDb.trialInfo
        def planId = trialInfo.planInfoId
        def deviceMap = [:]
        def cfg = JSON.parse(trialInfo.courtroom.cfg)//获取通道信息配置
        for (def encode : cfg.encode) {
            deviceMap.put(encode.encodeip + "_" + encode.number, encode)
        }
        Date initTime = trialInfo.startDate
        int i = 0
        def videoList = []
        while (true){
            def model = [
                    method: "getVideoFileByTaskIdProfiles",
                    params: [
                            taskId: "${trialInfo.uid}",
                            offset: i,
                            limit : 1
                    ]
            ]
            try {
                def resp = HttpUtil.postToJson("http://${trialInfo.courtroom.deviceIp}/web/page/activeProtocol.action", model)
                def params = resp?.result?.params
                def videoFileProfilesSize = params?.videoFileProfiles?.size()
                if (params?.retCode == 0 && videoFileProfilesSize > 0) {
                    def videoFileProfiles = params?.videoFileProfiles?.get(0)
                    def token = videoFileProfiles.token as String
                    def recordUrl = videoFileProfiles.recordUrl as String
                    def cArr = recordUrl.split("/")
                    def ip = cArr[2]
                    def c = cArr[cArr.length - 1]
                    def name = videoFileProfiles.name as String
                    def nameArr = name.split("_")
                    def y = nameArr[1]
                    def mf = nameArr[2]
                    def mfArr = mf.split("\\.")
                    def m = mfArr[0]
                    def f = mfArr[1]
                    videoList.add([
                            ip      : ip,
                            c       : c,
                            token   : token,
                            fileName: "${ip}_${y}_${m}_${c}.${f}"
                    ])
                    i++
                }else{
                    break
                }
                vdfrFromDb.handlerSuccess = true
                vdfrFromDb.save(flush: true)
                if (vdfrFromDb.hasErrors()){
                    log.error("[ApiOfflinePlanService downloadVideoByTaskId] 视频重新下载获取视频列表信息成功后,修改失败记录失败.\n" +
                            "错误信息：${vdfrFromDb.errors}")
                    throw new RuntimeException()
                }
            } catch (e){
                log.error("[ApiOfflinePlanService downloadVideo]根据taskId获取视频失败.\n参数：[model : ${model as JSON}, planId : ${planId}" +
                        "\n错误信息：[${e.printStackTrace()}]" )
                return
            }
        }
        def filePrefix = grailsApplication.config.getProperty('tc.trial.note.path')
        for (def video : videoList) {
            try {
                def videoDate = (video.fileName as String).split("_")[1]
                def url = "http://${trialInfo.courtroom.deviceIp}/web/page/activeProtocol.action/download?fileType=videoFile&token=${video.token}"
                def file = "${filePrefix}${planId}/${videoDate}/${video.fileName}"
//                def file = "F://usr//local//movies//${planId}//${videoDate}//${video.fileName}"
                FileUtils.copyURLToFile(new URL(url), new File(file))
                File source = new File(file)
                def multiObj = new MultimediaObject(source)
                //获取视频时长
                def length = multiObj.getInfo().getDuration()
                Date endDate = new Date(initTime.getTime() + length)
                //视频下载成功写入数据库
                def videoInfo = new VideoInfo(
                        uid: UUIDGenerator.nextUUID(),
                        channelNum: video.c,    //通道号
                        channelName: deviceMap.get(video.ip + "_" + video.c)?.name,    //通道名称
                        startRecTime: initTime,    //开始录像时间
                        endRecTime: endDate,    //结束录像时间
                        fileName: "${planId}/${videoDate}/${video.fileName}",    //视频文件名称
                        size: new FileInputStream(source).getChannel().size(),//文件大小
                        length: length / 1000,    //录像时长
                        active: DataStatus.SHOW,    //数据状态
                        trialInfo: trialInfo    //所属庭审
                ).save(flush: true)
                if (videoInfo.hasErrors()){
                    log.error("[ApiOfflinePlanService downloadVideoByTaskId]保存视频信息时失败.\n" +
                            "错误信息：${videoInfo.errors}")
                }
                initTime = endDate
            } catch (e) {
                def vdfr = new VideoDownFailRecord(
                        trialInfo: trialInfo,
                        videoMessage: "${video.ip}/${video.c}/${video.token}/${video.fileName}",
                        startRecTime: initTime,
                        handlerSuccess: false
                ).save()
                if (vdfr.hasErrors()){
                    log.error("[ApiOfflinePlanService downloadVideo]根据taskId获取视频失败后存储错误信息VideoDownFailRecord失败.错误信息：${vdfr.errors}" )
                }
                log.error("[ApiOfflinePlanService downloadVideo] 视频下载或视频信息入库失败.\n" +
                        "视频信息：token=${video.token},\n" +
                        "错误信息：errors=[${e.printStackTrace()}]")
            }
        }
    }

    def handleSingleFailVideo(VideoDownFailRecord vdfr){
        def cfg = JSON.parse(vdfr.trialInfo.courtroom.cfg)//获取通道信息配置
        def deviceMap = [:]
        for (def encode : cfg.encode) {
            deviceMap.put(encode.encodeip + "_" + encode.number, encode)
        }
        def videoMessageArr = vdfr.videoMessage.split("/")
        def video = [
                ip      : videoMessageArr[0],
                c       : videoMessageArr[1],
                token   : videoMessageArr[2],
                fileName: videoMessageArr[3]
        ]
        def filePrefix = grailsApplication.config.getProperty('tc.trial.note.path')
        try {
            def videoDate = video.fileName.split("_")[1]
            def url = "http://${vdfr.trialInfo.courtroom.deviceIp}/web/page/activeProtocol.action/download?fileType=videoFile&token=${video.token}"
            def file = "${filePrefix}${vdfr.trialInfo.planInfoId}/${videoDate}/${video.fileName}"
//            def file = "F://usr//local//movies//${vdfr.trialInfo.planInfoId}//${videoDate}//${video.fileName}"
            def multiObj
            def length
            Date endDate
            File source
            File beforeDownFile = new File(file)
            if (beforeDownFile.exists()){
                multiObj = new MultimediaObject(beforeDownFile)
                //获取视频时长
                length = multiObj.getInfo().getDuration()
                endDate = new Date(vdfr.startRecTime.getTime() + length)
                source = beforeDownFile
            }else{
                FileUtils.copyURLToFile(new URL(url), new File(file))
                source = new File(file)
                multiObj = new MultimediaObject(source)
                //获取视频时长
                length = multiObj.getInfo().getDuration()
                endDate = new Date(vdfr.startRecTime.getTime() + length)
            }
            videoInfoSave(video, deviceMap, vdfr.startRecTime, endDate, vdfr, source, videoDate, length)
            vdfr.handlerSuccess = true
            vdfr.save(flush: true)
            if (vdfr.hasErrors()){
                log.error("[ApiOfflinePlanService handleSingleFailVideo]视频重新下载成功后,修改失败记录失败.\n" +
                        "错误信息：${vdfr.errors}")
                throw new RuntimeException()
            }
        } catch (e) {
            log.error("[ApiOfflinePlanService downloadVideo] 重新下载视频或视频信息入库失败.\n" +
                    "视频token信息：token=${video.token}")
            e.printStackTrace()
        }
    }

    def handleMoreFailFTPVideo(VideoDownFailRecord vdfrFromDb){
        def trialInfo = vdfrFromDb.trialInfo
        def planId = trialInfo.planInfoId
        def deviceMap = [:]
        def cfg = JSON.parse(trialInfo.courtroom.cfg)//获取通道信息配置
        for (def encode : cfg.encode) {
            deviceMap.put(encode.encodeip + "_" + encode.number, encode)
        }
        log.info("[ApiOfflinePlanService.handleMoreFailFTPVideo]---->ftp下载")
        //ftp地址
        def ftpAddress = trialInfo.courtroom.deviceIp
        log.info("[ApiOfflinePlanService.handleMoreFailFTPVideo]---->ftp下载视频文件庭审主机地址:${ftpAddress}")
        //文件夹名称
        def directoryName = trialInfo.uid
        log.info("[ApiOfflinePlanService.handleMoreFailFTPVideo]---->ftp下载视频文件文件夹名称:${directoryName}")
        log.info("[ApiOfflinePlanService.handleMoreFailFTPVideo]---->ftp开始登录")
        login(ftpAddress, ftpPort, ftpUsername, ftpPassword)
        if (ftpClient){
            try {
                log.info("[ApiOfflinePlanService.handleMoreFailFTPVideo]---->ftp登录成功")
                log.info("[ApiOfflinePlanService.handleMoreFailFTPVideo]---->ftp判断是否存在此文件夹")
                String path = changeEncoding(basePath + directoryName)
                log.info("[ApiOfflinePlanService.handleMoreFailFTPVideo]---->ftp文件夹路径:${path}")
                if (!ftpClient.changeWorkingDirectory(path)) {
                    log.error("[ApiOfflinePlanService.handleMoreFailFTPVideo]---->" + basePath + directoryName + DIR_NOT_EXIST + "下载结束")
                    //此时应该把记录记录在失败的数据表格中
                    return Boolean.FALSE
                }
                //如果能走到这儿说明文件夹存在，此时应该去获取文件夹中的文件名称
                log.info("[ApiOfflinePlanService.handleMoreFailFTPVideo]---->文件夹存在，准备去获取文件夹中的文件名称")
                //遍历文件名称把文件下载下来
                ftpClient.enterLocalPassiveMode()  // 设置被动模式，开通一个端口来传输数据
                String[] fs = ftpClient.listNames()
                log.info("[ApiOfflinePlanService.handleMoreFailFTPVideo]---->文件夹中存在文件,文件个数为：${fs.size()},文件名称为：${fs.toString()}")
                if (fs == null || fs.length == 0) {
                    log.error("[ApiOfflinePlanService.handleMoreFailFTPVideo]---->" + basePath + directoryName + DIR_CONTAINS_NO_FILE + "下载结束")
                    return Boolean.FALSE
                }else{
                    vdfrFromDb.handlerSuccess = true
                    vdfrFromDb.save(flush: true)
                    if (vdfrFromDb.hasErrors()){
                        log.error("[ApiOfflinePlanService handleMoreFailFTPVideo] 视频重新下载获取视频列表信息成功后,修改失败记录失败.\n错误信息：${vdfrFromDb.errors}")
                        throw new RuntimeException()
                    }
                }
                for (String ff : fs) {
                    log.info("[ApiOfflinePlanService.handleMoreFailFTPVideo]---->未经转载ftp文件名称")
                    String ftpName = new String(ff.getBytes(serverCharset), localCharset)
                    log.info("[ApiOfflinePlanService.handleMoreFailFTPVideo]---->文件名称${ftpName}")
                    //排期目录
                    File planDirect = new File("/usr/local/movies/${planId}")
                    if (!planDirect.exists()){
                        log.info("[ApiOfflinePlanService.handleMoreFailFTPVideo]---->排期：${planId}文件夹不存在,准备创建文件夹")
                        planDirect.mkdirs()
                        log.info("[ApiOfflinePlanService.handleMoreFailFTPVideo]---->排期：${planId}文件夹不存在,创建文件夹成功")
                    }else{
                        log.info("[ApiOfflinePlanService.handleMoreFailFTPVideo]---->排期：${planId}文件夹存在,无需创建")
                    }
                    planDirect.setReadable(true, false)
                    planDirect.setWritable(true,false)
                    planDirect.setExecutable(true,false)
                    //日期目录
                    def arr = ff.split("_")
                    def ip = arr[0]
                    def chnNum = (arr[1] as Integer) + 1
                    def directDate = arr[2].substring(0,4) + "-" + arr[2].substring(4,6) + "-" + arr[2].substring(6,8)
                    File dateDirect = new File("/usr/local/movies/${planId}/${directDate}")
                    if (!dateDirect.exists()){
                        log.info("[ApiOfflinePlanService.handleMoreFailFTPVideo]---->日期：${directDate}文件夹不存在,准备创建文件夹")
                        dateDirect.mkdirs()
                        log.info("[ApiOfflinePlanService.handleMoreFailFTPVideo]---->日期：${directDate}文件夹不存在,创建文件夹成功")
                    }else{
                        log.info("[ApiOfflinePlanService.handleMoreFailFTPVideo]---->日期：${directDate}文件夹存在,无需创建")
                    }
                    dateDirect.setReadable(true, false)
                    dateDirect.setWritable(true,false)
                    dateDirect.setExecutable(true,false)
                    Date startDate = vdfrFromDb.startRecTime
                    try {
                        def fileDate = arr[2].substring(0,8) + "_" + arr[2].substring(8,14)
                        def fileName = ip + "_" + fileDate + "_" + chnNum + ".mp4"
                        File file = new File("/usr/local/movies/${planId}/${directDate}/${fileName}")
                        if (!file.exists()){
                            log.info("[ApiOfflinePlanService.handleMoreFailFTPVideo]---->${fileName}文件不存在，准备执行下载")
                            OutputStream os = new FileOutputStream(file)
                            //执行下载
                            ftpClient.retrieveFile(ff, os)
                            log.info("[ApiOfflinePlanService.handleMoreFailFTPVideo]---->ftp文件：${ftpName}以${fileName}为文件名下载完成")
                        }else{
                            log.info("[ApiOfflinePlanService.handleMoreFailFTPVideo]---->${fileName}文件存在")
                        }
                        //视频信息分析
                        File source = new File("/usr/local/movies/${planId}/${directDate}/${fileName}")
                        source.setReadable(true, false)
                        source.setWritable(true,false)
                        source.setExecutable(true,false)
                        def multiObj = new MultimediaObject(source)
                        //获取视频时长
                        def length = multiObj.getInfo().getDuration()
                        Date endDate = new Date(startDate.getTime() + length)
                        def filePath = "${planId}/${directDate}/${fileName}"
                        VideoInfo.withNewSession {
                            def v = VideoInfo.findByTrialInfoAndFileName(trialInfo, filePath)
                            if (!v){
                                //视频下载成功写入数据库
                                def videoInfo = new VideoInfo(
                                        uid: UUIDGenerator.nextUUID(),
                                        channelNum: chnNum,    //通道号
                                        channelName: deviceMap.get(ip + "_" + chnNum)?.name,    //通道名称
                                        startRecTime: startDate,    //开始录像时间
                                        endRecTime: endDate,    //结束录像时间
                                        fileName: filePath,    //视频文件名称
                                        size: new FileInputStream(source).getChannel().size(),//文件大小
                                        length: length / 1000,    //录像时长
                                        active: DataStatus.SHOW,    //数据状态
                                        trialInfo: trialInfo    //所属庭审
                                ).save(flush: true)
                                if (videoInfo.hasErrors()){
                                    log.error("[ApiOfflinePlanService handleMoreFailFTPVideo]保存视频信息时失败.\n错误信息：${videoInfo.errors}")
                                    throw new RuntimeException()
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage())
                        VideoDownFailRecord.withNewSession {
                            def vdfr = new VideoDownFailRecord(
                                    trialInfo: trialInfo,
                                    videoMessage: "${trialInfo.uid}/${ftpName}",
                                    startRecTime: startDate,
                                    handlerSuccess: false,
                                    ver : DeviceType.FTP //ftp主机
                            ).save()
                            if (vdfr.hasErrors()){
                                log.error("[ApiOfflinePlanService handleMoreFailFTPVideo]ftp保存单个视频文件失败后存储错误信息VideoDownFailRecord失败.错误信息：${vdfr.errors}" )
                                throw new RuntimeException()
                            }
                        }
                    }
                }
            }catch(Exception error){
                log.error("[ApiOfflinePlanService.handleMoreFailFTPVideo]---->ftp登陆成功,但是在执行下载过程中出现问题,错误信息：\n${error.getMessage()}")
            }finally {
                closeConnect()
            }
        }else{
            log.error("[ApiOfflinePlanService.handleMoreFailFTPVideo]---->ftp登录失败,数据不作修改")
        }
    }

    //单独的文件失败
    def handleSingleFailFTPVideo(VideoDownFailRecord vdfr){
        def cfg = JSON.parse(vdfr.trialInfo.courtroom.cfg)//获取通道信息配置
        def deviceMap = [:]
        for (def encode : cfg.encode) {
            deviceMap.put(encode.encodeip + "_" + encode.number, encode)
        }
        def trialInfo = vdfr.trialInfo
        def planId = trialInfo.planInfoId
        log.info("[ApiOfflinePlanService.handleSingleFailFTPVideo]---->ftp下载")
        //ftp地址
        def ftpAddress = trialInfo.courtroom.deviceIp
        log.info("[ApiOfflinePlanService.handleSingleFailFTPVideo]---->ftp下载视频文件庭审主机地址:${ftpAddress}")
        //文件夹名称
        def directoryName = trialInfo.uid
        log.info("[ApiOfflinePlanService.handleSingleFailFTPVideo]---->ftp下载视频文件文件夹名称:${directoryName}")
        log.info("[ApiOfflinePlanService.handleSingleFailFTPVideo]---->ftp开始登录")
        login(ftpAddress, ftpPort, ftpUsername, ftpPassword)
        if (ftpClient){
            try {
                log.info("[ApiOfflinePlanService.handleSingleFailFTPVideo]---->ftp登录成功")
                log.info("[ApiOfflinePlanService.handleSingleFailFTPVideo]---->ftp判断是否存在此文件夹")
                String path = changeEncoding(basePath + directoryName)
                log.info("[ApiOfflinePlanService.handleSingleFailFTPVideo]---->ftp文件夹路径:${path}")
                if (!ftpClient.changeWorkingDirectory(path)) {
                    log.error("[ApiOfflinePlanService.handleSingleFailFTPVideo]---->" + basePath + directoryName + DIR_NOT_EXIST + "下载结束")
                    //此时应该把记录记录在失败的数据表格中
                    return Boolean.FALSE
                }
                //如果能走到这儿说明文件夹存在，此时应该去获取文件夹中的文件名称
                log.info("[ApiOfflinePlanService.handleSingleFailFTPVideo]---->文件夹存在，准备判断文件是否存在")
                //遍历文件名称把文件下载下来
                ftpClient.enterLocalPassiveMode()  // 设置被动模式，开通一个端口来传输数据
                String[] fs = ftpClient.listNames()
                log.info("[ApiOfflinePlanService.handleSingleFailFTPVideo]---->文件夹中,文件个数为：${fs.size()},文件名称为：${fs.toString()}")
                if (fs == null || fs.length == 0) {
                    log.error("[ApiOfflinePlanService.handleSingleFailFTPVideo]---->" + basePath + directoryName + DIR_CONTAINS_NO_FILE + "下载结束")
                    return Boolean.FALSE
                }
                boolean flag = false
                def ftpName = vdfr.videoMessage.split("/")[1]
                for (String str:fs){
                    if (str == ftpName){
                        flag = true
                    }
                }
                if (!flag){
                    log.info("[ApiOfflinePlanService.handleSingleFailFTPVideo]---->ftp文件夹${directoryName}下没有${ftpName}文件,下载结束")
                    return Boolean.FALSE
                }
                log.info("[ApiOfflinePlanService.handleSingleFailFTPVideo]---->ftp文件夹${directoryName}下存在${ftpName}文件,准备下载")
                log.info("[ApiOfflinePlanService.handleSingleFailFTPVideo]---->未经转载ftp文件名称:${ftpName}")
                ftpName = new String(ftpName.getBytes(serverCharset), localCharset)
                log.info("[ApiOfflinePlanService.handleSingleFailFTPVideo]---->文件名称${ftpName}")
                //排期目录
                File planDirect = new File("/usr/local/movies/${planId}")
                if (!planDirect.exists()){
                    log.info("[ApiOfflinePlanService.handleSingleFailFTPVideo]---->排期：${planId}文件夹不存在,准备创建文件夹")
                    planDirect.mkdirs()
                    log.info("[ApiOfflinePlanService.handleSingleFailFTPVideo]---->排期：${planId}文件夹不存在,创建文件夹成功")
                }else{
                    log.info("[ApiOfflinePlanService.handleSingleFailFTPVideo]---->排期：${planId}文件夹存在,无需创建")
                }
                planDirect.setReadable(true, false)
                planDirect.setWritable(true,false)
                planDirect.setExecutable(true,false)
                //日期目录
                def arr = ftpName.split("_")
                def ip = arr[0]
                def chnNum = (arr[1] as Integer) + 1
                def directDate = arr[2].substring(0,4) + "-" + arr[2].substring(4,6) + "-" + arr[2].substring(6,8)
                File dateDirect = new File("/usr/local/movies/${planId}/${directDate}")
                if (!dateDirect.exists()){
                    log.info("[ApiOfflinePlanService.handleSingleFailFTPVideo]---->日期：${directDate}文件夹不存在,准备创建文件夹")
                    dateDirect.mkdirs()
                    log.info("[ApiOfflinePlanService.handleSingleFailFTPVideo]---->日期：${directDate}文件夹不存在,创建文件夹成功")
                }else{
                    log.info("[ApiOfflinePlanService.handleSingleFailFTPVideo]---->日期：${directDate}文件夹存在,无需创建")
                }
                dateDirect.setReadable(true, false)
                dateDirect.setWritable(true,false)
                dateDirect.setExecutable(true,false)
                Date startDate = vdfr.startRecTime
                try {
                    def fileDate = arr[2].substring(0,8) + "_" + arr[2].substring(8,14)
                    def fileName = ip + "_" + fileDate + "_" + chnNum + ".mp4"
                    File file = new File("/usr/local/movies/${planId}/${directDate}/${fileName}")

                    if (!file.exists()){
                        log.info("[ApiOfflinePlanService.handleSingleFailFTPVideo]---->${fileName}文件不存在，准备执行下载")
                        OutputStream os = new FileOutputStream(file)
                        //执行下载
                        ftpClient.retrieveFile(ftpName, os)
                        log.info("[ApiOfflinePlanService.handleSingleFailFTPVideo]---->ftp文件：${ftpName}以${fileName}为文件名下载完成")
                    }else{
                        log.info("[ApiOfflinePlanService.handleSingleFailFTPVideo]---->${fileName}文件存在")
                    }
                    //视频信息分析
                    File source = new File("/usr/local/movies/${planId}/${directDate}/${fileName}")
                    source.setReadable(true, false)
                    source.setWritable(true,false)
                    source.setExecutable(true,false)
                    def multiObj = new MultimediaObject(source)
                    //获取视频时长
                    def length = multiObj.getInfo().getDuration()
                    Date endDate = new Date(startDate.getTime() + length)
                    def filePath = "${planId}/${directDate}/${fileName}"
                    def v = VideoInfo.findByTrialInfoAndFileName(trialInfo, filePath)
                    if (!v){
                        //视频下载成功写入数据库
                        def videoInfo = new VideoInfo(
                                uid: UUIDGenerator.nextUUID(),
                                channelNum: chnNum,    //通道号
                                channelName: deviceMap.get(ip + "_" + chnNum)?.name,    //通道名称
                                startRecTime: startDate,    //开始录像时间
                                endRecTime: endDate,    //结束录像时间
                                fileName: filePath,    //视频文件名称
                                size: new FileInputStream(source).getChannel().size(),//文件大小
                                length: length / 1000,    //录像时长
                                active: DataStatus.SHOW,    //数据状态
                                trialInfo: trialInfo    //所属庭审
                        ).save(flush: true)
                        if (videoInfo.hasErrors()){
                            log.error("[ApiOfflinePlanService handleSingleFailFTPVideo]保存视频信息时失败.\n错误信息：${videoInfo.errors}")
                            throw new RuntimeException()
                        }else{
                            log.info("[ApiOfflinePlanService handleSingleFailFTPVideo]---->保存视频信息完成,准备修改下载失败记录")
                            vdfr.handlerSuccess = true
                            vdfr.save(flush: true)
                            if (vdfr.hasErrors()){
                                log.error("[ApiOfflinePlanService handleSingleFailFTPVideo]视频重新下载成功后,修改失败记录失败.\n错误信息：${vdfr.errors}")
                                throw new RuntimeException()
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("[ApiOfflinePlanService.handleSingleFailFTPVideo]---->下载视频过程中出现错误，错误信息为：\n${e.getMessage()}")
                }
            }catch(Exception error){
                log.error("[ApiOfflinePlanService.handleSingleFailFTPVideo]---->ftp登陆成功,但是在执行下载过程中出现问题,错误信息：\n${error.getMessage()}")
            }finally {
                closeConnect()
            }
        }else{
            log.info("[ApiOfflinePlanService.handleSingleFailFTPVideo]---->ftp登录失败")
        }
    }

    def videoInfoSave(
            def video,
            def deviceMap,
            Date startRecTime,
            Date endRecTime,
            VideoDownFailRecord vdfr,
            File source,
            String videoDate,
            long length){
        def videoInfo = new VideoInfo(
                uid: UUIDGenerator.nextUUID(),
                channelNum: video.c,    //通道号
                channelName: deviceMap.get(video.ip + "_" + video.c)?.name,    //通道名称
                startRecTime: startRecTime,    //开始录像时间
                endRecTime: endRecTime,    //结束录像时间
                fileName: "${vdfr.trialInfo.planInfoId}/${videoDate}/${video.fileName}",    //视频文件名称
                size: new FileInputStream(source).getChannel().size(),//文件大小
                length: length / 1000,    //录像时长
                active: DataStatus.SHOW,    //数据状态
                trialInfo: vdfr.trialInfo    //所属庭审
        ).save(flush: true)
        if (videoInfo.hasErrors()){
            log.error("[ApiOfflinePlanService videoInfoSave]保存视频信息时失败.\n" +
                    "错误信息：${videoInfo.errors}")
            throw new RuntimeException()
        }
    }

    /**
     * toolBox重新下载视频
     * @param id 排期主键
     * @param uuid 庭次uid
     * @return 输出信息
     */
    def reVideo (def id, def uuid, def version, def basepath) {
        String msg = "排期：${id}，庭次：${uuid}，版本：${version}，路径：${basepath},"
        if (basepath != "" && basepath != "/mnt/HD0/") {
            msg += "路径请置空或修改为/mnt/HD0/."
            log.info("[ApiOfflinePlanService.reVideo] ${msg}")
            return msg
        }
        if (!(id && uuid && version)) {
            msg += "参数不完整."
            log.info("[ApiOfflinePlanService.reVideo] ${msg}")
            return msg
        }
        if (version != "1" && version != "4") {
            msg += "版本信息不正确."
            log.info("[ApiOfflinePlanService.reVideo] ${msg}")
            return msg
        }
        uuid = uuid as String
        def planInfo = PlanInfo.get(id as long)
        if (!planInfo) {
            msg += "没有查询到对应的排期."
            log.info("[ApiOfflinePlanService.reVideo] ${msg}")
            return msg
        }
        //删除排期相关庭次
        List<TrialInfo> trialInfoList = TrialInfo.findAllByPlanInfo(planInfo)
        def size = trialInfoList.size()
        if (size == 0) {
            msg += "此排期没有开过庭."
            log.info("[ApiOfflinePlanService.reVideo] ${msg}")
            return msg
        }
        if (size != 1) {
            msg += "此排期开过两次及两次以上的庭次，请记录下id特殊处理."
            log.info("[ApiOfflinePlanService.reVideo] ${msg}")
            return msg
        }
        //删除此排期对应的VideoInfo
        def trialInfo = trialInfoList.get(0)
        List<VideoInfo> videoInfoList = VideoInfo.findAllByTrialInfo(trialInfo)
        for (VideoInfo videoInfo:videoInfoList) {
            def cvl = CopyVideoLog.findByVideoInfo(videoInfo)
            if (cvl) {
                msg += "此排期对应的现有视频记录有关联外键，请手动关闭copy_video_log表的关联外键再次执行此操作."
                log.info("[ApiOfflinePlanService.reVideo] ${msg}")
                return msg
            }
            //删除视频记录
            videoInfo.delete(flush:true)
            if (videoInfo.hasErrors()) {
                log.error("[ApiOfflinePlanService.reVideo] 删除原有视频记录失败.错误信息：${trialInfo.errors}")
                throw new RuntimeException()
            }
        }
        trialInfo.uid = uuid
        trialInfo.save(flush: true)
        if (trialInfo.hasErrors()) {
            log.error("[ApiOfflinePlanService.reVideo] 修改现有trialInfo.uid失败.错误信息：${trialInfo.errors}")
            throw new RuntimeException()
        }

        def planId = trialInfo.planInfoId
        def deviceMap = [:]
        def cfg = JSON.parse(trialInfo.courtroom.cfg)//获取通道信息配置
        for (def encode : cfg.encode) {
            deviceMap.put(encode.encodeip + "_" + encode.number, encode)
        }

        if (version == "4") {
            log.info("[ApiOfflinePlanService.reVideo] 开始执行新版本主机视频下载")
            Date initTime = trialInfo.startDate
            int i = 0
            def videoList = []
            while (true){
                def model = [
                        method: "getVideoFileByTaskIdProfiles",
                        params: [
                                taskId: "${trialInfo.uid}",
                                offset: i,
                                limit : 1
                        ]
                ]
                def resp = HttpUtil.postToJson("http://${trialInfo.courtroom.deviceIp}/web/page/activeProtocol.action", model)
                def params = resp?.result?.params
                def videoFileProfilesSize = params?.videoFileProfiles?.size()
                if (params?.retCode == 0 && videoFileProfilesSize > 0) {
                    def videoFileProfiles = params?.videoFileProfiles?.get(0)
                    def token = videoFileProfiles.token as String
                    def recordUrl = videoFileProfiles.recordUrl as String
                    def cArr = recordUrl.split("/")
                    def ip = cArr[2]
                    def c = cArr[cArr.length - 1]
                    def name = videoFileProfiles.name as String
                    def nameArr = name.split("_")
                    def y = nameArr[1]
                    def mf = nameArr[2]
                    def mfArr = mf.split("\\.")
                    def m = mfArr[0]
                    def f = mfArr[1]
                    videoList.add([
                            ip      : ip,
                            c       : c,
                            token   : token,
                            fileName: "${ip}_${y}_${m}_${c}.${f}"
                    ])
                    i++
                }else{
                    break
                }
            }
            def filePrefix = grailsApplication.config.getProperty('tc.trial.note.path')
            for (def video : videoList) {
                def videoDate = (video.fileName as String).split("_")[1]
                def url = "http://${trialInfo.courtroom.deviceIp}/web/page/activeProtocol.action/download?fileType=videoFile&token=${video.token}"
                def file = "${filePrefix}${planId}/${videoDate}/${video.fileName}"
                FileUtils.copyURLToFile(new URL(url), new File(file))
                File source = new File(file)
                def multiObj = new MultimediaObject(source)
                //获取视频时长
                def length = multiObj.getInfo().getDuration()
                Date endDate = new Date(initTime.getTime() + length)
                def fileName = "${planId}/${videoDate}/${video.fileName}"
                //视频下载成功写入数据库
                def videoInfo = new VideoInfo(
                        uid: UUIDGenerator.nextUUID(),
                        channelNum: video.c,    //通道号
                        channelName: deviceMap.get(video.ip + "_" + video.c)?.name,    //通道名称
                        startRecTime: initTime,    //开始录像时间
                        endRecTime: endDate,    //结束录像时间
                        fileName: "${planId}/${videoDate}/${video.fileName}",    //视频文件名称
                        size: new FileInputStream(source).getChannel().size(),//文件大小
                        length: length / 1000,    //录像时长
                        active: DataStatus.SHOW,    //数据状态
                        trialInfo: trialInfo    //所属庭审
                ).save(flush: true)
                if (videoInfo.hasErrors()){
                    log.error("[ApiOfflinePlanService.reVideo] 保存视频信息时失败.错误信息：${videoInfo.errors}")
                    throw new RuntimeException()
                }
                log.info("[ApiOfflinePlanService.reVideo] 视频下载并保存到数据库成功")
                initTime = endDate
            }
            msg += "新版本主机重新下载视频执行成功."
            log.info("[ApiOfflinePlanService.reVideo] ${msg}")
            return msg
        }else{
            log.info("[ApiOfflinePlanService.revideo]---->ftp下载")
            //ftp地址
            def ftpAddress = trialInfo.courtroom.deviceIp
            log.info("[ApiOfflinePlanService.revideo]---->ftp下载视频文件庭审主机地址:${ftpAddress}")
            //文件夹名称
            def directoryName = trialInfo.uid
            log.info("[ApiOfflinePlanService.revideo]---->ftp下载视频文件文件夹名称:${directoryName}")
            log.info("[ApiOfflinePlanService.revideo]---->ftp开始登录")
            login(ftpAddress, ftpPort, ftpUsername, ftpPassword)
            if (!ftpClient) {
                msg += "ftp登录失败"
                log.error("[ApiOfflinePlanService.revideo]---->ftp登录失败")
                return msg
            }
            try {
                //默认为空，如果有判断是否是"/mnt/HD0"
                if (basepath == "/mnt/HD0/") {
                    basePath = basepath
                }
                log.info("[ApiOfflinePlanService.revideo]---->ftp登录成功")
                log.info("[ApiOfflinePlanService.revideo]---->ftp判断是否存在此文件夹")
                String path = changeEncoding(basePath + directoryName)
                log.info("[ApiOfflinePlanService.revideo]---->ftp文件夹路径:${path}")
                if (!ftpClient.changeWorkingDirectory(path)) {
                    msg += basePath + directoryName + DIR_NOT_EXIST + "下载结束"
                    log.error("[ApiOfflinePlanService.revideo]---->" + msg)
                    return msg
                }
                log.info("[ApiOfflinePlanService.revideo]---->文件夹存在，准备去获取文件夹中的文件名称")
                ftpClient.enterLocalPassiveMode()
                String[] fs = ftpClient.listNames()
                log.info("[ApiOfflinePlanService.revideo]---->文件夹中存在文件,文件个数为：${fs.size()},文件名称为：${fs.toString()}")
                if (fs == null || fs.length == 0) {
                    msg += basePath + directoryName + DIR_CONTAINS_NO_FILE + "下载结束"
                    log.error("[ApiOfflinePlanService.revideo]---->" + msg)
                    return msg
                }
                for (String ff : fs) {
                    log.info("[ApiOfflinePlanService.revideo]---->未经转载ftp文件名称")
                    String ftpName = new String(ff.getBytes(serverCharset), localCharset)
                    log.info("[ApiOfflinePlanService.revideo]---->文件名称${ftpName}")
                    //排期目录
                    File planDirect = new File("/usr/local/movies/${planId}")
                    if (!planDirect.exists()){
                        log.info("[ApiOfflinePlanService.revideo]---->排期：${planId}文件夹不存在,准备创建文件夹")
                        planDirect.mkdirs()
                        log.info("[ApiOfflinePlanService.revideo]---->排期：${planId}文件夹不存在,创建文件夹成功")
                    }else{
                        log.info("[ApiOfflinePlanService.revideo]---->排期：${planId}文件夹存在,无需创建")
                    }
                    planDirect.setReadable(true, false)
                    planDirect.setWritable(true,false)
                    planDirect.setExecutable(true,false)
                    //日期目录
                    def arr = ff.split("_")
                    def ip = arr[0]
                    def chnNum = (arr[1] as Integer) + 1
                    def directDate = arr[2].substring(0,4) + "-" + arr[2].substring(4,6) + "-" + arr[2].substring(6,8)
                    File dateDirect = new File("/usr/local/movies/${planId}/${directDate}")
                    if (!dateDirect.exists()){
                        log.info("[ApiOfflinePlanService.revideo]---->日期：${directDate}文件夹不存在,准备创建文件夹")
                        dateDirect.mkdirs()
                        log.info("[ApiOfflinePlanService.revideo]---->日期：${directDate}文件夹不存在,创建文件夹成功")
                    }else{
                        log.info("[ApiOfflinePlanService.revideo]---->日期：${directDate}文件夹存在,无需创建")
                    }
                    dateDirect.setReadable(true, false)
                    dateDirect.setWritable(true,false)
                    dateDirect.setExecutable(true,false)
                    Date startDate = trialInfo.startDate
                    def fileDate = arr[2].substring(0,8) + "_" + arr[2].substring(8,14)
                    def fileName = ip + "_" + fileDate + "_" + chnNum + ".mp4"
                    File file = new File("/usr/local/movies/${planId}/${directDate}/${fileName}")
                    if (!file.exists()){
                        log.info("[ApiOfflinePlanService.revideo]---->${fileName}文件不存在，准备执行下载")
                        OutputStream os = new FileOutputStream(file)
                        //执行下载
                        ftpClient.retrieveFile(ff, os)
                        log.info("[ApiOfflinePlanService.revideo]---->ftp文件：${ftpName}以${fileName}为文件名下载完成")
                    }else{
                        log.info("[ApiOfflinePlanService.revideo]---->${fileName}文件存在")
                        file.delete()
                        log.info("[ApiOfflinePlanService.revideo]---->${fileName}文件已删除不存在，准备执行下载")
                        OutputStream os = new FileOutputStream(file)
                        //执行下载
                        ftpClient.retrieveFile(ff, os)
                        log.info("[ApiOfflinePlanService.revideo]---->ftp文件：${ftpName}以${fileName}为文件名下载完成")
                    }
                    //视频信息分析
                    File source = new File("/usr/local/movies/${planId}/${directDate}/${fileName}")
                    source.setReadable(true, false)
                    source.setWritable(true,false)
                    source.setExecutable(true,false)
                    def multiObj = new MultimediaObject(source)
                    //获取视频时长
                    def length = multiObj.getInfo().getDuration()
                    Date endDate = new Date(startDate.getTime() + length)
                    def filePath = "${planId}/${directDate}/${fileName}"
                    //视频下载成功写入数据库
                    def videoInfo = new VideoInfo(
                            uid: UUIDGenerator.nextUUID(),
                            channelNum: chnNum,    //通道号
                            channelName: deviceMap.get(ip + "_" + chnNum)?.name,    //通道名称
                            startRecTime: startDate,    //开始录像时间
                            endRecTime: endDate,    //结束录像时间
                            fileName: filePath,    //视频文件名称
                            size: new FileInputStream(source).getChannel().size(),//文件大小
                            length: length / 1000,    //录像时长
                            active: DataStatus.SHOW,    //数据状态
                            trialInfo: trialInfo    //所属庭审
                    ).save(flush: true)
                    if (videoInfo.hasErrors()){
                        log.error("[ApiOfflinePlanService revideo]保存视频信息时失败.\n错误信息：${videoInfo.errors}")
                        throw new RuntimeException()
                    }
                }
                msg += "旧版本主机重新视频执行成功."
                log.info("[ApiOfflinePlanService.reVideo] ${msg}")
                return msg
            }finally {
                closeConnect()
            }
        }
    }
}
