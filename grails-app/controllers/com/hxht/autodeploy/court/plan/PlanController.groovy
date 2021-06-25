package com.hxht.autodeploy.court.plan

import com.hxht.techcrt.CopyVideoLog
import com.hxht.techcrt.DataStatus
import com.hxht.techcrt.ModelType
import com.hxht.techcrt.PlanStatus
import com.hxht.techcrt.PositionStatus
import com.hxht.techcrt.Resp
import com.hxht.techcrt.User
import com.hxht.techcrt.UserRole
import com.hxht.techcrt.api.ApiService
import com.hxht.techcrt.court.*
import com.hxht.techcrt.enums.PlayStatus
import com.hxht.techcrt.enums.RespType
import com.hxht.techcrt.utils.IpUtil
import com.hxht.techcrt.utils.UUIDGenerator
import com.hxht.techcrt.utils.WordUtil
import com.hxht.techcrt.utils.ZipUtil
import grails.converters.JSON
import grails.events.EventPublisher
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springwebsocket.WebSocket
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.web.multipart.MultipartFile
import pl.touk.excel.export.WebXlsxExporter
import ws.schild.jave.MultimediaObject

/**
 * 排期模块功能
 * 2021.03.22 >>> 闭庭时自动断开远程连接 daniel
 * 2021.04.26 >>> 后台休庭时同闭庭一样清空解码器 daniel
 * 2021.05.10 >>> 休庭不断开远程提讯 daniel
 * 2021.05.21 >>> 保存案件方法与ApiService中的整合为一个方法、异步通知放置到service(优化3) daniel
 * 2021.05.26 >>> 取消并案开庭、调整代码顺序 daniel
 * 2021.06.16 >>> 互联网开庭统计 daniel
 */
class PlanController implements WebSocket, EventPublisher {

    SpringSecurityService springSecurityService
    PlanService planService
    ChatRecordService chatRecordService
    ApiService apiService

    /**
     * 排期列表
     */
    def list() {
        if (request.method == "GET") {
            def result = planService.planListQueryData(params, request)
            return [
                    judgeList: result.judgeList,
                    secretaryList: result.secretaryList,
                    courtroomList: result.courtroomList,
                    date: result.date,
                    contentPath: result.contentPath
            ]
        }
        if (request.method == "POST") {
            render planService.list(params) as JSON
        }
    }

    /**
     * 并案排期
     */
    def combinedPlan() {
        //获取需要并案的排期参数
        def scheIdsStr = params.get("schIds")
        //检验排期参数
        if (!scheIdsStr || StringUtils.split(scheIdsStr as String, ",").length == 1) {
            render Resp.toJson(RespType.DATA_NOT_EXIST)
            return
        }
        render planService.combinedPlan(scheIdsStr as String)
    }

    /**
     * 取消并案排期
     */
    def cancelCombinedPlan() {
        def planInfo = PlanInfo.get(params.id as long)
        if (!planInfo) {
            render Resp.toJson(RespType.DATA_NOT_EXIST)
            return
        }
        render planService.cancelCombinedPlan(planInfo)
    }

    /**
     * 批量导入排期 websocket
     */
    @MessageMapping("/plan/data_import")
    @SendTo("/topic/plan/data_import")
    protected static importExcelWs(String data) {}

    /**
     * Excel导入为排期
     */
    def importPlanExcel() {
        render planService.importPlanExcel(params.get("file") as MultipartFile)
    }

    /**
     * 导出排期为Excel
     */
    def exportPlanExcel() {
        //根据查询条件获取所有排期
        def planList = planService.list(params).get("data")
        //设置excel表头
        def headers = ['案号', '案件名称', '排期法庭', '主审法官', '书记员', '排期开庭时间', '庭审状态', '开庭模式']
        def fileName = "排期表格.xlsx"
        def withProperties = [
                'caseArchives', 'caseName', 'courtroom', 'judge', 'secretary', 'startDate', 'status', 'model'
        ]
        new WebXlsxExporter().with {
            setResponseHeaders(response, URLEncoder.encode(fileName, "UTF-8"))
            fillHeader(headers)
            add(planList, withProperties)
            save(response.outputStream)
        }
    }

    /**
     * 排期添加页面
     */
    def add() {
        def caseTypeList = CaseType.findAll()
        def judgeList = Employee.findAllByPosition(PositionStatus.JUDGE)
        def secretaryList = Employee.findAllByPosition(PositionStatus.SECRETARY)
        def courtroomList = Courtroom.findAll()
        [caseTypeList: caseTypeList, judgeList: judgeList, secretaryList: secretaryList, courtroomList: courtroomList]
    }

    /**
     * 排期添加页面--案件列表
     */
    def caseInfoList() {
        if (request.method == "POST") {
            def draw = params.int("draw") ?: 1// 记录操作的次数 每次加1
            def start = params.int("start") ?: 0// 起始
            def length = params.int("length") ?: 20// 每页显示的size
            def search = params.get("search[value]") as String//搜索内容
            def model = planService.caseInfoList(draw, start, length, search)
            render model as JSON
        }
    }

    /**
     * 排期添加页面--通过主键获取案件详情
     */
    def getCaseById() {
        if (request.method == "GET") {
            def caseInfo = CaseInfo.get(params.long("id"))
            def model = [
                    id                : caseInfo.id,
                    uid               : caseInfo.uid,
                    archives          : caseInfo.archives,
                    name              : caseInfo.name,
                    type              : caseInfo.type,
                    summary           : caseInfo.summary,
                    detail            : caseInfo.detail,
                    accuser           : caseInfo.accuser,
                    prosecutionCounsel: caseInfo.prosecutionCounsel,
                    accused           : caseInfo.accused,
                    counselDefence    : caseInfo.counselDefence,
                    filingDate        : caseInfo.filingDate?.format('yyyy/MM/dd HH:mm'),
            ]
            render Resp.toJson(RespType.SUCCESS, model)
        }
    }

    /**
     * 检查案号是否重复
     */
    def getPlanByArchives() {
        render Resp.toJson(
                RespType.SUCCESS,
                CaseInfo.findAllByArchives(params.get("archives") as String).size()
        )
    }

    /**
     * 添加排期-保存信息
     */
    def addSave() {
        def caseInfo = CaseInfo.get(params.long("caseId"))
        if (!caseInfo) {
            caseInfo = new CaseInfo()
            caseInfo.uid = UUIDGenerator.nextUUID()
        }
        caseInfo.properties = params
        caseInfo.active = DataStatus.SHOW
        def planInfo = new PlanInfo()
        planInfo.active = DataStatus.SHOW
        planInfo.uid = UUIDGenerator.nextUUID()
        planInfo.caseInfo = caseInfo
        planInfo.status = PlanStatus.PLAN
        planInfo.properties = params
        planService.save(caseInfo, planInfo, params.collegialName)
        redirect(controller: "plan", action: "list")
    }

    /**
     * 排期编辑页面
     */
    def edit() {
        def planInfo = PlanInfo.get(params.long("id"))
        def caseTypeList = CaseType.findAll()
        def judgeList = Employee.findAllByPosition(PositionStatus.JUDGE)
        def secretaryList = Employee.findAllByPosition(PositionStatus.SECRETARY)
        def courtroomList = Courtroom.findAll()
        [planInfo: planInfo, caseTypeList: caseTypeList, judgeList: judgeList, secretaryList: secretaryList, courtroomList: courtroomList]
    }

    /**
     * 修改排期-保存信息
     */
    def editSave() {
        def planInfo = PlanInfo.get(params.long("id"))
        def caseInfo = CaseInfo.get(planInfo.caseInfo.id)
        if (!params.distanceArraigned) {
            params.distanceArraigned = 0
        }
        caseInfo.properties = params
        planInfo.properties = params
        def collegial = params.collegialName
        planService.save(caseInfo, planInfo, collegial)
        //向CMP推送数据
        this.notify("pushCmpCaseAndPlanAndTrial", planInfo.caseInfo.id, planInfo.id, null)
        redirect(controller: "plan", action: "list")
    }

    /**
     * 刪除排期
     */
    def del() {
        //获取参数
        def scheIdsStr = params.get("schIds")
        //参数校验
        if (!scheIdsStr) {
            render Resp.toJson(RespType.DATA_NOT_EXIST)
            return
        }
        //参数解析
        List<PlanInfo> planInfoList = new ArrayList<>()
        for (String scheId : scheIdsStr.split(",")) {
            PlanInfo planInfo = PlanInfo.get(scheId as long)
            if (!planInfo || PlanTrial.findByPlanInfo(planInfo)) {
                continue
            }
            planInfoList.add(planInfo)
        }
        //执行操作
        planService.del(planInfoList)
        //返回结果
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 设置为互联网开庭模式
     * @return Json
     */
    def internet() {
        planService.setupModel(params.id as Long, ModelType.INTERNET)
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 设置为本地开庭模式
     * @return Json
     */
    def locale() {
        planService.setupModel(params.id as Long, ModelType.LOCALE)
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 排期详情页面
     */
    def show() {
        planService.show(params)
    }

    /**
     *  进入页面之后
     *  查询这个用户这个排期的所有观看记录
     *  if(有被管理员禁止的数据) {*      直接返回被禁止强制其退出页面
     *} else {*     添加一个新的观看记录
     *}*/
    def videoStatus() {
        def planId = params.long("planId")
        def userId = (springSecurityService.currentUser as User).id
        def videoRecords = VideoRecord.findAllByPlanIdAndUserId(planId, userId)
        for (VideoRecord videoRecord : videoRecords) {
            if (videoRecord.playStatus == PlayStatus.ADMIN_DISCONNECT) {
                render Resp.toJson(RespType.FAIL)
                return
            }
        }
        new VideoRecord(
                userId: userId,
                planId: planId,
                ip: IpUtil.getIpAddress(request),
                playStatus: PlayStatus.CONNECTING
        ).save(flush: true)
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 将该用户对应的排期观看记录修改为正在连接的状态
     */
    def editVideoStatus() {
        //获取排期主键
        def planId = params.long("planId")
        //获取用户主键
        def userId = (springSecurityService.currentUser as User).id
        //根据排期主键和用户主键查询相对应的视频查看记录---->查询所有的记录找到最后一条
        def dataList = VideoRecord.createCriteria().list {
            eq("userId", userId)
            eq("planId", planId)
            order("dateCreated", "desc")
        } as List<VideoRecord>
        def videoRecord = dataList.get(0)
        //如果既不是新的也没有被禁用则修改状态为正在连接和ip地址
        if (videoRecord.playStatus != PlayStatus.DISCONNECT) {
            render Resp.toJson(RespType.FAIL)
            return
        }
        videoRecord.playStatus = PlayStatus.CONNECTING
        videoRecord.ip = IpUtil.getIpAddress(request)
        videoRecord.save(flush: true)
        if (videoRecord.hasErrors()) {
            render Resp.toJson(RespType.FAIL)
            return
        }
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 视频详情
     */
    def showVideo() {
        def trialInfo = TrialInfo.get(params.long("id"))
        if (!trialInfo) {
            render Resp.toJson(RespType.FAIL)
            return
        }
        def video = planService.showVideo(trialInfo)
        render(Resp.toJson(RespType.SUCCESS, [status: trialInfo.status, video: video]))
    }

    /**
     * 修改控制台的排期状态开庭
     */
    def editConsoleOpen() {
        log.info("[PlanController.editConsoleOpen] 接收到的数据：[${params as JSON}]")
        def planInfo = PlanInfo.get(params.long("planId"))
        def trialInfo = TrialInfo.get(params.long("trialId"))
        if (!planInfo) {
            log.error("[PlanController.editConsoleOpen] 开庭失败,未获取到排期.")
            render(contentType: "application/json", text: Resp.toJson(RespType.DATA_NOT_EXIST))
            return
        }
        //当前审理庭的所有正在开庭的庭审
        def trialList = TrialInfo.findAllByCourtroomAndStatus(planInfo.courtroom, PlanStatus.SESSION)
        for (def trial : trialList) {
            planService.planClose(trial, PlanStatus.CLOSED, null)
            //排期闭庭后抛出排期事件
            this.notify("stopTrial", trial.id)
            //向CMP系统推送开庭trial和plan信息
            this.notify("pushCmpCaseAndPlanAndTrial", null, trial.planInfo.id, trial.id)
        }
        def trial = planService.planOpen(planInfo, trialInfo)//开庭操作
        this.notify("pushCmpCaseAndPlanAndTrial", null, null, trial.id)
        convertAndSend("/topic/flush", "open")
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 修改控制台的排期状态休庭
     */
    def editConsoleAdjourn() {
        log.info("[PlanController.editConsoleAdjourn] 接收到的数据：[${params as JSON}]")
        def trialInfo = TrialInfo.get(params.long("trialId"))
        if (!trialInfo) {
            log.error("[PlanController.editConsoleAdjourn] 休庭失败,未获取到庭审")
            render(contentType: "application/json", text: Resp.toJson(RespType.DATA_NOT_EXIST))
            return
        }
        //已休庭
        if (trialInfo.status == PlanStatus.ADJOURN) {
            return render(Resp.toJson(RespType.FAIL))
        }
        def planInfo = trialInfo.planInfo
        def trial = planService.planClose(trialInfo, PlanStatus.ADJOURN, null)//休庭操作
        //排期休庭后抛出排期事件
        this.notify("adjournTrial", trialInfo.id)
        this.notify("pushCmpCaseAndPlanAndTrial", null, planInfo.id, null)
        this.notify("pushCmpCaseAndPlanAndTrial", null, null, trial.id)
        convertAndSend("/topic/flush", "adjourn")
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 修改控制台的排期状态闭庭
     */
    def editConsoleClose() {
        log.info("[PlanController.editConsoleClose] 接收到的数据：[${params as JSON}]")
        def trialInfo = TrialInfo.get(params.long("trialId"))
        if (!trialInfo) {
            log.error("[PlanController.editConsoleClose] 闭庭失败,未获取到庭审.")
            render(contentType: "application/json", text: Resp.toJson(RespType.DATA_NOT_EXIST))
            return
        }
        //已闭庭
        if (trialInfo.status == PlanStatus.CLOSED) {
            return render(Resp.toJson(RespType.FAIL))
        }
        def planInfo = trialInfo.planInfo
        def trial = planService.planClose(trialInfo, PlanStatus.CLOSED,null)//闭庭操作
        //排期闭庭后抛出排期事件
        this.notify("stopTrial", trial.id)
        this.notify("pushCmpCaseAndPlanAndTrial", null, planInfo.id, null)
        this.notify("pushCmpCaseAndPlanAndTrial", null, null, trial.id)
        convertAndSend("/topic/flush", "close")
        //断开远程提讯
        apiService.stopConnect(trial.planInfo.courtroom)
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 并案排期详情页面切换案件信息
     */
    def getCaseInfo() {
        def caseId = params.long("caseId")
        def planInfo = PlanInfo.get(params.long("planId"))
        def trialId = params.long("trialId")
        def trialInfo = null
        if (trialId) {
            trialInfo = TrialInfo.get(trialId)
        }
        def caseInfo
        if (caseId != 0) {
            caseInfo = CaseInfo.get(caseId)
        } else {
            caseInfo = planInfo.caseInfo
        }
        def data = [
                caseName  : caseInfo.name,
                caseType  : caseInfo.type.name,
                courtroom : (trialInfo?.courtroom ?: planInfo.courtroom).name,
                accuser   : caseInfo.accuser ?: "无数据",
                accused   : caseInfo.accused ?: "无数据",
                judge     : planInfo.judge?.name ?: "无数据",
                filingDate: caseInfo.filingDate?.format('yyyy/MM/dd HH:mm'),
                startDate : trialInfo?.startDate?.format('yyyy/MM/dd HH:mm') ?: planInfo.startDate?.format('yyyy/MM/dd HH:mm'),
                endDate   : trialInfo?.endDate?.format('yyyy/MM/dd HH:mm') ?: planInfo.endDate?.format('yyyy/MM/dd HH:mm'),
                collegial : planInfo.collegial,
                secretary : (trialInfo?.secretary ?: planInfo.secretary).name,
                gy        : caseInfo.summary ?: "无数据",
                detail    : caseInfo.detail ?: "无数据",
        ]
        render Resp.toJson(RespType.SUCCESS, data)
    }

    /**
     * 获取笔录信息
     */
    def note() {
        def path = grailsApplication.config.getProperty('tc.trial.note.path')
        def trialInfo = TrialInfo.get(params.long("id"))
        if (!trialInfo) {
            render Resp.toJson(RespType.FAIL, "未找到庭次信息")
            return
        }
        def note = trialInfo.note ? trialInfo.note : trialInfo.noteWord
        if (!note) {
            render Resp.toJson(RespType.FAIL, "未找到笔录信息")
            return
        }
        try {
            def file = new File("${path}", trialInfo.note)
            render Resp.toJson(RespType.SUCCESS, WordUtil.readWordToHtml(file))
        } catch (e) {
            e.printStackTrace()
            log.error("[PlanController.note] 浏览笔录信息出错！${e.message}")
            render Resp.toJson(RespType.FAIL)
        }
    }

    /**
     * 控制台的上传笔录
     */
    def uploadConsoleNode() {
        def trialInfo = TrialInfo.get(params.long("id"))
        def file = params.get("file_data") as MultipartFile
        if (!file) {
            log.error("[PlanController.uploadConsoleNode] 控制台上传笔录时未获取到文件。")
            render Resp.toJson(RespType.FAIL)
            return
        }
        def path = grailsApplication.config.getProperty('tc.trial.note.path')
        def filePath = "${trialInfo.id}_${file.getOriginalFilename()}"
        def serverFile = new File("${path}/", filePath)
        if (!serverFile.exists()) {
            serverFile.getParentFile().mkdirs()
        }
        file.transferTo(serverFile)
        trialInfo.note = filePath
        trialInfo.save(flush: true)
        if (trialInfo.hasErrors()) {
            def msg = "[PlanService.uploadConsoleNode] 控制台上传笔录时保存庭次失败."
            log.error(msg + "错误信息:${trialInfo.errors}")
            throw new RuntimeException(msg)
        }

        if (trialInfo.hasErrors()) {
            log.error("[PlanController.uploadConsoleNode] 上传笔录文件,保存庭审记录失败,错误信息：${trialInfo.errors}")
            render Resp.toJson(RespType.FAIL)
            return
        }
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 下载笔录
     */
    def downTrialNote() {
        def path = grailsApplication.config.getProperty('tc.trial.note.path')
        def trialInfo = TrialInfo.get(params.long("id"))
        if (!trialInfo) {
            render Resp.toJson(RespType.DATA_NOT_EXIST)
            return
        }
        try {
            def file = new File("${path}", trialInfo.note)
            response.contentType = 'application/octet-stream'
            response.setHeader("Content-disposition", "attachment;filename=${URLEncoder.encode(trialInfo.note, "UTF-8")}")
            response.outputStream << new FileInputStream(file)
            response.outputStream.flush()
        } catch (Exception e) {
            e.printStackTrace()
            log.error("[PlanController.downTrialNote] 庭审控制下载笔录时候时出错,错误信息：${e.message}")
            render Resp.toJson(RespType.FAIL)
        }
    }

    /**
     * 聊天记录删除
     */
    def messageDel() {
        def planId = params.long("planId")
        def uuidsStr = params.get("uuids") as String
        def userRole = UserRole.findAllByUser(springSecurityService.currentUser as User)
        def flag = false
        for (UserRole ur : userRole) {
            def auth = ur.role.authority
            if (auth == "ROLE_SUPER" || auth == "ROLE_ADMIN") {
                flag = true
            }
        }
        if (!flag) {
            render Resp.toJson(RespType.NO_AUTHORIZED)
            return
        }
        if (!(uuidsStr || planId)) {
            render Resp.toJson(RespType.FAIL)
            return
        }
        def uuidsArr = uuidsStr.split(",")
        def chatRecord = chatRecordService.getChatRecord(planId)
        for (String uuidStr : uuidsArr) {
            for (int i = 0; i < chatRecord.chatRecord.size(); i++) {
                if (chatRecord.chatRecord[i].uuid == uuidStr) {
                    chatRecord.chatRecord.remove(i)
                }
            }
        }
        def record = ChatRecord.findByPlanId(planId)
        record.chatRecord = chatRecord as JSON
        record.save(flush: true)
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 下载视频截图图片
     */
    def getComment() {
        def uploadUrl = grailsApplication.config.getProperty('tc.trial.images.path')
        def path = "${uploadUrl}/${params.get("trialId")}/${params.get("picName")}"
        File file = new File(path)
        if (!file.exists()) {
            render(Resp.toJson(RespType.FAIL))
            return
        }
        ZipUtil.download(response, file, path)
    }

    /**
     * 视频下载
     */
    def download() {
        def url = params.get("url")
        def filename = "33.mp4"
        try {
            response.setContentType("application/octet-stream")
            response.setHeader("content-disposition", "attachmentfilename=" + filename)
            def uri = new URL(url)
            def ins = uri.openStream()
            def len
            def buffer = new byte[1024]
            def out = response.getOutputStream()
            while ((len = ins.read(buffer)) > 0) {
                out.write(buffer, 0, len)//将缓冲区的数据输出到客户端浏览器
            }
            ins.close()
        } catch (Exception e) {
            e.printStackTrace()
            log.error("[PlanController.download] 下载文件出错,错误信息：${e.getMessage()}")
        }
    }


    /**
     * 禁止此用户观看此用户正在观看的排期详情
     */
    def stopVideo() {
        //获取排期主键
        def planId = params.long("planId")
        //获取用户主键
        def userId = (springSecurityService.currentUser as User).id
        //根据排期主键和用户主键查询相对应的视频查看记录---->查询所有的记录找到最后一条
        def dataList = VideoRecord.createCriteria().list {
            eq("userId", userId)
            eq("planId", planId)
            eq("playStatus", PlayStatus.CONNECTING)
        } as List<VideoRecord>
        for (VideoRecord vr : dataList) {
            vr.playStatus = PlayStatus.DISCONNECT
            vr.save(flush: true)
        }
        convertAndSend("/queue/editVideoStatus", "1")
        render Resp.toJson(RespType.SUCCESS, dataList)
    }

    /**
     * 从直播授权页面跳转到对应的排期详情页面
     */
    def planDetail() {
        redirect(
                controller: "plan",
                action: "show",
                id: PlanInfo.get(VideoRecord.get(params.long("id")).planId).id
        )
    }

    /**
     * 深圳中院恢复数据
     */
    def recoverData(){
        def file = new File("/usr/local/movies/movies1/mp4_bak")
        def files = file.listFiles()
        for (def f: files){
            def fileName = f.getName()
            def planInfo = PlanInfo.findByUid(fileName)
            println("正在执行${fileName}")
            
            if (planInfo){
                def trialInfo = TrialInfo.findByPlanInfo(planInfo)
                if (!trialInfo){
                    println("未找到到对应的庭审 排期的uid为${fileName}")
                    continue
                }
                def videoInfoList = VideoInfo.findAllByTrialInfo(trialInfo)

                def deviceMap = [:]
                for (def videoInfo: videoInfoList){
                    def  sss = videoInfo.fileName.substring(videoInfo.fileName.lastIndexOf("/")+1).split("_")
                    deviceMap.put(sss[0] + "_" + sss[3], videoInfo.channelName)
                    def cvl = CopyVideoLog.findByVideoInfo(videoInfo)
                    if (cvl){
                        cvl.delete(flush: true)
                    }
                    //删除视频记录
                    videoInfo.delete(flush: true)
                }
                Date startDate = trialInfo.startDate
                
                def cfg = JSON.parse(trialInfo.courtroom.cfg)//获取通道信息配置
                for (def encode : cfg.encode) {
                    deviceMap.put(encode.encodeip + "_" + encode.number, encode)
                }
                
                def dateFiles = f.listFiles()
                for (def dateFile: dateFiles) {
                    for (def video : dateFile.listFiles()) {
                        def fileStr = video.getPath().replace("/mp4_bak","")

                        def name = video.getName()
                        String[] arrayName = name.split("_")
                        println("开始转移视频文件${name}")
                        
                        def multiObj = new MultimediaObject(video)
                        //获取视频时长
                        def length = multiObj.getInfo().getDuration()
                        Date endDate = new Date(startDate.getTime() + length)
                        def filePath = fileStr.replace("/usr/local/movies/", "")

                        def fileF = new File(fileStr)
                        if (!fileF.getParentFile().exists()){
                            fileF.getParentFile().mkdirs()
                            fileF.getParentFile().setReadable(true)
                            fileF.getParentFile().setWritable(true)
                        }
                        FileUtils.copyFile(video, fileF)
                        
                        //视频转移成功写入数据库
                        def videoInfo = new VideoInfo(
                                uid: UUIDGenerator.nextUUID(),
                                channelNum: arrayName[3],    //通道号
                                channelName: deviceMap.get(arrayName[0] + "_" + arrayName[3]),    //通道名称
                                startRecTime: startDate,    //开始录像时间
                                endRecTime: endDate,    //结束录像时间
                                fileName: filePath,    //视频文件名称
                                size: new FileInputStream(fileF).getChannel().size(),//文件大小
                                length: length / 1000,    //录像时长
                                active: DataStatus.SHOW,    //数据状态
                                trialInfo: trialInfo    //所属庭审
                        ).save(flush: true)
                        
                        //转移完后删除原视频
                        video.delete()
                    }
                }
                
                
            }else{
                println("未找到对应的排期，uid为${fileName}")
            }
        }

        for (def f: files){
            def ss = 0
            for (def sa: f.listFiles()){
                for (def dd: sa.listFiles())
                    if (dd.getPath().endsWith(".mp4")){
                        ss = 1
                        break
                    }
            }
            if (ss == 0){
                println("删除${f.getPath()}")
                FileUtils.deleteQuietly(f)
            }
        }
        
    }
    
    
}
