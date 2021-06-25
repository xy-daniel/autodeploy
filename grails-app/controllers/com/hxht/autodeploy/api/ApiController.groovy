package com.hxht.autodeploy.api

import cn.hutool.core.date.DateUtil
import com.hxht.techcrt.*
import com.hxht.techcrt.court.*
import com.hxht.techcrt.court.plan.ChatRecordService
import com.hxht.techcrt.court.plan.PlanService
import com.hxht.techcrt.enums.RespType
import com.hxht.techcrt.mem.SignatureService
import com.hxht.techcrt.utils.*
import com.hxht.techcrt.utils.comm.StoreCommUtil
import com.hxht.techcrt.utils.http.RemoteHttpUtil
import grails.converters.JSON
import grails.events.EventPublisher
import grails.plugin.springwebsocket.WebSocket
import org.springframework.web.multipart.MultipartHttpServletRequest

/**
 * 书记员客户端对接接口
 * 2021.03.22 >>> 闭庭时自动断开远程连接 daniel
 * 2021.04.19(2021.04.23) >>> 修改远程提讯策略 daniel
 * 2021.04.20 >>> 修改书记员更新包下载路径 daniel
 * 2021.04.23 >>> 休庭、归档时自动断开远程连接,跳过法官软件登录验证 daniel
 * 2021.04.24 >>> 远程法院列表根据分级码排序 daniel
 * 2021.04.29 >>> 增加远程提讯不支持时的状态 daniel
 * 2021.05.10 >>> 休庭、归档不断开远程提讯 daniel
 * 2021.05.12 >>> 修改批注文件上传功能 daniel
 * 2021.05.21 >>> 书记员客户端获取临时立案排期数据添加合议庭成员类型(坪山功能修改2)、修改方法名称(优化3) daniel
 * 2021.05.26 >>> 取消并案开庭,休闭归使用plan方法 daniel
 * 2021.06.16 >>> 互联网开庭统计 daniel
 */
class ApiController implements EventPublisher, WebSocket {

    ApiService apiService
    SignatureService signatureService
    ChatRecordService chatRecordService
    ApiTaiChiService apiTaiChiService
    PlanService planService

    //占用法庭
    public static Map<Long, Date> OCCUPIED_COURTROOM = new HashMap<>()

    public static final String UPDATE_PACKAGE_STORE_BASE_ADDRESS = File.separator + "home" + File.separator + "hxht" + File.separator + "update" + File.separator
    public static final String SJY_UPDATE_PACKAGE_STORE_BASE_ADDRESS = File.separator + "home" + File.separator + "hxht" + File.separator + "sjy" + File.separator
    public static final String FG_UPDATE_PACKAGE_STORE_BASE_ADDRESS = File.separator + "home" + File.separator + "hxht" + File.separator + "fg" + File.separator
    public static final String DSR_UPDATE_PACKAGE_STORE_BASE_ADDRESS = File.separator + "home" + File.separator + "hxht" + File.separator + "dsr" + File.separator

    /**
     * 书记员正常下线执行此方法，放开法庭的占用
     */
    def logout() {
        def courtroom = Courtroom.get(params.courtroom as long)
        if (!courtroom) {
            render Resp.toJson(RespType.FAIL)
            return
        }
        def occupiedCourtroomMap = OCCUPIED_COURTROOM
        //如果存在此法庭的键值对
        if (occupiedCourtroomMap.containsKey(courtroom.id)) {
            occupiedCourtroomMap.remove(courtroom.id)
        }
        OCCUPIED_COURTROOM.remove(courtroom.id)
        render Resp.toJson(RespType.SUCCESS)

    }

    /**
     * 书记员每30秒执行此方法，记录书记员最后在线时间
     */
    def heart() {
        def courtroom = Courtroom.get(params.courtroom as long)
        if (!courtroom) {
            render Resp.toJson(RespType.FAIL)
            return
        }
        OCCUPIED_COURTROOM.put(courtroom.id, new Date())
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 获取TOKEN接口
     */
    def token() {
        def username = params.username as String
        def password = params.password as String
        def courtroom = Courtroom.get(params.long("courtroom"))
        //判断是否是福建地区的法院
        if (grailsApplication.config.getProperty("tc.deployPlace") == "fujian" && courtroom) {
            def user = apiService.getUserByUsernameAndPassword(username, password)
            if (!user) {
                render Resp.toJson(RespType.USERNAME_PASSWORD_ERROR)
                return
            }
            //被占用法庭列表
            def occupiedCourtroomMap = OCCUPIED_COURTROOM
            //如果存在此法庭的键值对
            if (occupiedCourtroomMap.containsKey(courtroom.id) && (new Date().getTime()) - occupiedCourtroomMap.get(courtroom.id).getTime() < 60 * 1000) {
                render Resp.toJson(RespType.COURT_OCCUPIED, "${courtroom.name}已经被占用，无法登录。")
                return
            } else {
                occupiedCourtroomMap.put(courtroom.id, new Date())
            }
            def secret = grailsApplication.config.getProperty('jwt.info.secret')
            def expires = grailsApplication.config.getProperty('jwt.info.expires') as long
            def accessToken = JwtUtil.create(user, secret, expires)
            render Resp.toJson(RespType.SUCCESS, accessToken)
        } else {
            def user = apiService.getUserByUsernameAndPassword(username, password)
            if (user) {
                def secret = grailsApplication.config.getProperty('jwt.info.secret')
                def expires = grailsApplication.config.getProperty('jwt.info.expires') as long
                String accessToken = JwtUtil.create(user, secret, expires)
                render Resp.toJson(RespType.SUCCESS, accessToken)
                return
            }
            render Resp.toJson(RespType.USERNAME_PASSWORD_ERROR)
        }
    }

    /**
     * 获取当前登陆用户信息
     */
    def current() {
        def user = apiService.currentUser()
        def userMap = [
                id      : user.employee ?: 1,
                username: user.username,
                realName: user.realName
        ]
        render Resp.toJson(RespType.SUCCESS, userMap)
    }

    /**
     * 获取法庭信息
     */
    def courtroom() {
        if (request.method == "GET") {
            try {
                def id = params.long("id")
                if (id) {
                    def court = Courtroom.get(id)
                    render Resp.toJson(RespType.SUCCESS, court)
                } else {
                    def courtroomList = Courtroom.findAllByActive(DataStatus.SHOW, [sort: "sequence", order: "desc"])
                    def dataList = []
                    def dict = Dict.findByCode("CURRENT_COURT")
                    def courtName = dict.ext1//项目所在法院名称
                    def courtNo = dict.ext3//法院代码
                    for (def courtroom : courtroomList) {
                        def status = 0 //法庭中没有开庭状态的排期为0 有的话为1
                        def plan = PlanInfo.findByCourtroomAndStatus(courtroom, PlanStatus.SESSION)
                        if (plan) {
                            status = 1
                        }
                        dataList.add([
                                id       : courtroom.id,
                                name     : courtroom.name,
                                status   : status,
                                courtRoom: courtroom.uid,
                                courtName: courtName,
                                courtNo  : courtNo
                        ])
                    }
                    render Resp.toJson(RespType.SUCCESS, dataList)
                }
            } catch (e) {
                e.printStackTrace()
                log.error("[ApiController.courtroom] 书记员获取法庭信息时出错,错误信息:${e.message}")
                render Resp.toJson(RespType.FAIL)
            }
        }
    }

    /**
     * 上传笔录文件
     */
    def trialNote() {
        if (request.method == "POST") {
            try {
                def trialInfo = TrialInfo.get(params.long("id"))
                if (request instanceof MultipartHttpServletRequest) {
                    def file = request.getFile("note")
                    if (file) {
                        def path = grailsApplication.config.getProperty('tc.trial.note.path')
                        def filePath = "${trialInfo.id}_${file.getOriginalFilename()}"
                        def sfile = new File("${path}/${trialInfo.planInfo.id}/", filePath)
                        if (!sfile.getAbsoluteFile().getParentFile().exists()) {
                            sfile.getParentFile().mkdirs()
                        }
                        file.transferTo(sfile)
                        trialInfo.note = "${trialInfo.planInfo.id}/${filePath}"
                        if (!sfile.getAbsolutePath().endsWith("pdf")) {
                            trialInfo.noteWord = "${trialInfo.planInfo.id}/${filePath}"
                        }
                        apiService.saveTrial(trialInfo)
                        if (!trialInfo.hasErrors()) {
                            //笔录修改后抛出事件
                            this.notify("refreshNote", trialInfo.id)
                            render Resp.toJson(RespType.SUCCESS)
                            return
                        }
                    }
                    def msg = "[ApiController.trialNote] 上传笔录文件，保存庭审记录失败。接收到的数据：[${params as JSON}]\n错误信息[${trialInfo.errors}]"
                    log.error(msg)
                    render Resp.toJson(RespType.FAIL, msg)
                } else {
                    def msg = "[ApiController.trialNote] 上传笔录文件，非法请求方式。接收到的数据：[${params as JSON}]"
                    log.error(msg)
                    render Resp.toJson(RespType.FAIL, msg)
                }
            } catch (e) {
                e.printStackTrace()
                log.error("[ApiController.trialNote] 书记员上传笔录文件时出错,错误信息:${e.message}")
                render Resp.toJson(RespType.FAIL)
            }
        }
    }

    /**
     * 下载笔录
     */
    def downTrialNote() {
        def path = grailsApplication.config.getProperty('tc.trial.note.path')
        def trialInfo = TrialInfo.get(params.long("id"))
        if (trialInfo) {
            def file = new File("${path}", trialInfo.note)
            response.contentType = 'application/octet-stream'
            response.setHeader("Content-disposition", "attachment;filename=${URLEncoder.encode(trialInfo.note, "UTF-8")}")
            response.outputStream << new FileInputStream(file)
            response.outputStream.flush()
        }
    }

    /**
     * 统计语音识别
     */
    def statisSpeech() {
        def isCourt = params.isCourt
        def trialId = params.trialId
        if (!(isCourt && trialId)) {
            log.error("[ApiController.statisSpeech] 语音识别统计接收到的参数为空，参数isCourt：${isCourt},trialId:${trialId}")
            render Resp.toJson(RespType.FAIL)
            return
        }
        def trialInfo = TrialInfo.get(trialId as long)
        if (!trialInfo) {
            log.error("[ApiController.statisSpeech] 统计语音识别未查找到对应的trial，trialId为：${trialId}")
            render Resp.toJson(RespType.FAIL)
            return
        }
        //将传过来的值保存到trial中
        trialInfo.isCourtSpeech = isCourt as Integer
        apiService.saveTrial(trialInfo)
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     *  获取排期接口
     */
    def plan() {
        try {
            render Resp.toJson(RespType.SUCCESS, apiService.plan(params))
        } catch (e) {
            log.error("[ApiController.plan] 书记员客户端获取排期时出错,错误信息:\n${e.message}")
            render Resp.toJson(RespType.FAIL)
        }
    }

    /**
     * 书记员客户端合并排期
     */
    def combinePlan() {
        try {
            log.info("[ApiController.combinePlan] 开始并案，当前时间为：${new Date()}")
            log.info("[ApiController.combinePlan] 书记员客户端请求并案开始---请求参数：[${params as JSON}]")
            def combinedResult = apiService.combinePlan(params)
            log.info("[ApiController.combinePlan] 并案结束，返回客户端成功信息，当前时间为：" + new Date())
            log.info("[ApiController.combinePlan] 书记员客户端并案请求结束，返回的信息为:${combinedResult}")
            render combinedResult
        } catch (e) {
            e.printStackTrace()
            log.error("[ApiController.combinePlan] 并案排期失败,错误信息:${e.message}")
            render Resp.toJson(RespType.FAIL)
        }
    }

    /**
     * 书记员客户端取消取消并案排期
     */
    def cancelCombinePlan() {
        try {
            log.info("[ApiController.cancelCombinePlan] 取消并案，当前时间为：${new Date()}")
            log.info("[ApiController.cancelCombinePlan] 书记员客户端请求取消并案开始---请求参数：[${params as JSON}]")
            def cancelResult = apiService.cancelCombinePlan(params)
            log.info("[ApiController.cancelCombinePlan] 取消并案结束，返回客户端成功信息，当前时间为：" + new Date())
            render cancelResult
        } catch (e) {
            e.printStackTrace()
            log.error("[ApiController.combinePlan] 并案排期失败,错误信息:${e.message}")
            render Resp.toJson(RespType.FAIL)
        }
    }

    /**
     * 开庭接口
     */
    def trialOpen() {
        if (request.method == "POST") {
            try {
                log.info("[ApiController.trialOpen] 开始开庭，当前时间为：${new Date()}")
                log.info("[ApiController.trialOpen] 书记员客户端请求开庭开始---请求参数：[${params as JSON}]")
                //庭次
                def trialId = params.get("trailId")//开庭判断庭审是否有休庭要开庭的庭审
                //排期
                def planInfo = PlanInfo.get(params.get("id") as long)
                //排期不存在
                if (!planInfo) {
                    log.error("[ApiController.trialOpen] 开庭失败，未获取到排期。")
                    render Resp.toJson(RespType.FAIL)
                    return
                }
                //法庭
                def courtroom = Courtroom.get(params.long("courtroom"))
                if (!courtroom) {
                    log.error("[ApiController.trialOpen] 开庭失败，未获取到开庭法庭。")
                    render Resp.toJson(RespType.FAIL)
                    return
                }
                //当前审理庭的所有正在开庭的庭审 并将所有开庭的庭审关掉
                def trialList = TrialInfo.findAllByCourtroomAndStatus(courtroom, PlanStatus.SESSION)
                for (def trial : trialList) {
                    planService.planClose(trial, PlanStatus.CLOSED, null)
                    //排期闭庭后抛出排期事件
                    this.notify("stopTrial", trial.id)
                    //向CMP系统推送开庭trial和plan信息
                    this.notify("pushCmpCaseAndPlanAndTrial", null, trial.planInfo.id, trial.id)
                }
                def judge = Employee.get(params.long("judge"))//获取法官
                def secretary = Employee.get(params.long("secretary"))//获取书记员
                if (!planInfo) {
                    log.error("[ApiController.trialOpen] 开庭失败，未获取到排期。")
                    render Resp.toJson(RespType.FAIL)
                    return
                }
                if (!judge) {
                    judge = planInfo.judge
                }
                if (!secretary) {
                    secretary = planInfo.secretary
                }
                //增加开庭模式参数
                planInfo.model = params.int("internet")
                TrialInfo trialInfo = apiService.planOpen(planInfo, courtroom, judge, secretary, trialId as String)
                log.info("[ApiController.trialOpen] 开庭结束，返回客户端成功信息，当前时间为：" + new Date())
                log.info("[ApiController.trialOpen] 书记员客户端开庭请求结束，返回的trialId:${trialInfo.id}")
                convertAndSend("/topic/flush", "open")
                render Resp.toJson(RespType.SUCCESS, [id: trialInfo.id])
            } catch (e) {
                e.printStackTrace()
                log.error("[ApiController.trialOpen] 开庭失败,错误信息:${e.message}")
                render Resp.toJson(RespType.FAIL)
            }
        }
    }

    /**
     * 闭庭接口
     */
    def tiralClosed() {
        if (request.method == "POST") {
            try {
                log.info("[ApiController.tiralClosed] 书记员客户端请求闭庭开始---请求参数：[${params as JSON}]")
                def trialInfo = TrialInfo.get(params.long("id"))
                if (!trialInfo) {
                    log.error("[ApiController.tiralClosed] 闭庭失败，未获取到庭审。")
                    render Resp.toJson(RespType.FAIL)
                    return
                }
                //书记员向庭审主机发送的文件夹名称，与后台进行同步
                def taskId = params.get("taskid") as String
                log.info("书记员向庭审主机发送的文件夹名称，与后台进行同步。同步的Id为${taskId}")
                trialInfo = planService.planClose(trialInfo, PlanStatus.CLOSED, taskId)
                //排期闭庭后抛出排期事件
                this.notify("stopTrial", trialInfo.id)
                this.notify("pushCmpCaseAndPlanAndTrial", null, trialInfo.planInfo.id, trialInfo.id)
                log.info("[ApiCOntroller.trialClosed] 闭庭成功返回客户端的时间：" + new Date())
                convertAndSend("/topic/flush", "close")
                apiService.stopConnect(trialInfo.planInfo.courtroom)
                render Resp.toJson(RespType.SUCCESS)
            } catch (e) {
                e.printStackTrace()
                log.error("[ApiController.tiralClosed] 闭庭失败，错误信息：${e.message}")
                render Resp.toJson(RespType.FAIL)
            }
        }
    }

    /**
     * 休庭接口
     */
    def tiralAdjourn() {
        if (request.method == "POST") {
            try {
                log.debug("[ApiController.tiralAdjourn] 书记员客户端请求休庭开始---请求参数：[${params as JSON}]")
                def trialInfo = TrialInfo.get(params.long("id"))
                if (!trialInfo) {
                    log.error("[ApiController.tiralAdjourn] 休庭失败，未获取到庭审。")
                    render Resp.toJson(RespType.DATA_NOT_EXIST)
                    return
                }
                //书记员向庭审主机发送的文件夹名称，与后台进行同步
                def taskId = params.get("taskid") as String
                log.info("书记员向庭审主机发送的文件夹名称，与后台进行同步。同步的Id为${taskId}")
                trialInfo = planService.planClose(trialInfo, PlanStatus.ADJOURN, taskId)//休庭操作
                //排期休庭后抛出排期事件
                this.notify("adjournTrial", trialInfo.id)
                //向CMP平台推送数据
                this.notify("pushCmpCaseAndPlanAndTrial", null, trialInfo.planInfo.id, trialInfo.id)
                log.debug("[ApiController.tiralAdjourn] 书记员客户端请求休庭结束")
                convertAndSend("/topic/flush", "adjourn")
                render Resp.toJson(RespType.SUCCESS)
            } catch (e) {
                e.printStackTrace()
                log.error("[ApiController.tiralAdjourn] 休庭失败，错误信息：${e.message}")
                render Resp.toJson(RespType.FAIL)
            }
        }
    }

    /**
     * 归档接口
     */
    def trialArchived() {
        if (request.method == "POST") {
            try {
                log.info("开始归档，当前时间为：" + new Date())
                log.info("[ApiController.trialArchived] 书记员客户端请求归档开始---请求参数：[${params as JSON}]")
                def trialInfo = TrialInfo.get(params.long("id"))
                if (!trialInfo) {
                    log.error("[ApiController.trialArchived] 归档失败，未获取到庭审。")
                    render Resp.toJson(RespType.FAIL)
                    return
                }
                //闭庭向太极接口推送word和pdf和图片,没有pdf情况下生成Pdf文件。
                def taichi = apiTaiChiService.taichiDossier(trialInfo.id)
                if (!taichi) {
                    render Resp.toJson(RespType.FAIL)
                    return
                }
                planService.planClose(trialInfo, PlanStatus.ARCHIVED, null)//归档操作
                //向CMP系统推送开庭trial和plan信息
                this.notify("pushCmpCaseAndPlanAndTrial", null, trialInfo.planInfo.id, trialInfo.id)
                log.debug("[ApiController.trialArchived] 书记员客户端请求归档结束.")
                log.info("归档成功返回客户端的时间：" + new Date())
                render Resp.toJson(RespType.SUCCESS)
            } catch (e) {
                e.printStackTrace()
                log.error("[ApiController.trialArchived] 归档失败，错误信息：${e.message}")
                render Resp.toJson(RespType.FAIL)
            }
        }
    }

    /**
     * 庭审接口
     */
    def trial() {
        if (request.method == "GET") {
            try {
                def data = apiService.trial(params)
                render Resp.toJson(RespType.SUCCESS, data)
            } catch (e) {
                e.printStackTrace()
                log.error("[ApiController trial] 书记员客户端获取庭审时出错，错误信息：${e.message}")
                render Resp.toJson(RespType.FAIL)
            }
        }
    }

    /**
     * 书记员客户端下载签名图片
     */
    def clientSignatureDownload() {
        try {
            def sig = signatureService.findById(params.long("id"))
            if (!sig) {
                render Resp.toJson(RespType.FAIL, "签名不存在！")
                return
            }
            def path = grailsApplication.config.getProperty('tc.signature.path')
            File file = new File("${path}${sig.path}")
            response.contentType = 'application/octet-stream'
            response.setHeader("Content-disposition", "attachment;filename=${URLEncoder.encode("${sig.name}.${sig.type}", "UTF-8")}")
            response.outputStream << new FileInputStream(file)
            response.outputStream.flush()
        } catch (e) {
            e.printStackTrace()
            log.error("[ApiController.clientSignatureDownload] 书记员客户端获取签名图片出错，错误信息：${e.message}")
            render Resp.toJson(RespType.FAIL)
        }
    }

    /**
     * GET 书记员客户端根据排期uid获取签名人员数据接口
     */
    def clientSignaturePersonList() {
        if (request.method == "GET") {
            try {
                def uid = params.id as String
                def planInfo = PlanInfo.findByUid(uid)
                if (!planInfo) {
                    render Resp.toJson(RespType.DATA_NOT_EXIST)
                    return
                }
                def sigList = signatureService.findAllSignatureByPlanInfoId(planInfo.id)
                def dataList = []
                for (def sig : sigList) {
                    dataList.add([
                            id       : sig.id,
                            name     : sig.name,
                            signature: "/api/client/signature/down/${sig.id}"
                    ])
                }
                def model = [
                        planUid: uid,
                        data   : dataList
                ]
                render Resp.toJson(RespType.SUCCESS, model)
            } catch (e) {
                e.printStackTrace()
                log.error("[ApiController.clientSignaturePersonList] 书记员客户端获取排期签名人员列表时出错！${e.message}")
                render Resp.toJson(RespType.FAIL)
            }
        }
    }

    /**
     * 书记员客户端临时立案接口
     */
    def clientTemporaryCase() {
        if (request.method == "POST") {
            try {
                if (!params.archives) {
                    render Resp.toJson(RespType.DATA_NOT_EXIST)
                    return
                }
                //案号存在情况下 判断案件是否存在相同案件，存在的话则用数据库中存在的案件
                def caseInfo = CaseInfo.findByArchives(params.archives as String)
                if (!caseInfo) {
                    //判断法官存在但是没有在数据库中找到则返回参数不合法code=120
                    if (params.judge) {
                        if (params.judge == "0") {
                            params.judge = null
                        } else {
                            def employee = Employee.get(params.judge as long)
                            if (!employee) {
                                render Resp.toJson(RespType.BUSINESS_VALID_FAIL)
                                return
                            }
                        }
                    }
                    if (params.type) {
                        def caseType = CaseType.get(params.type as long)
                        if (!caseType) {
                            params.type = null
                        }
                    }
                    caseInfo = new CaseInfo()
                    caseInfo.uid = params.uuid? params.uuid : UUIDGenerator.nextUUID()
                    caseInfo.filingDate = new Date()
                    caseInfo.properties = params
                    caseInfo.active = DataStatus.SHOW
                }
                render Resp.toJson(RespType.SUCCESS, apiService.saveTempCaseInfo(caseInfo, params))
            } catch (e) {
                e.printStackTrace()
                log.error("[ApiController.clientTemporaryCase] 书记员客户端保存临时案件排期时出错，错误信息：${e.message}")
                render Resp.toJson(RespType.FAIL)
            }
        }
    }

    /**
     * 获取所有法官list，法院和案件类型信息
     */
    def clientTemporaryGetAll() {
        if (request.method == "GET") {
            //获取所有法官
            def judgeList = Employee.findAll()
            //获取当前法院信息
            def confCourtDict = Dict.findByCode("CURRENT_COURT")
            def court = confCourtDict.ext2
            //获取所有案件类型
            def caseTypeList = CaseType.findAll()
            def collegialTypeList = []
            collegialTypeList.add([
                    id  : CollegialType.PERSIDING_JUDGE as String,
                    name: CollegialType.getString(CollegialType.PERSIDING_JUDGE)
            ])
            collegialTypeList.add([
                    id  : CollegialType.JUDGE as String,
                    name: CollegialType.getString(CollegialType.JUDGE)
            ])
            collegialTypeList.add([
                    id  : CollegialType.PEOPLE_ASSESSOR as String,
                    name: CollegialType.getString(CollegialType.PEOPLE_ASSESSOR)
            ])
            collegialTypeList.add([
                    id  : CollegialType.OTHER as String,
                    name: CollegialType.getString(CollegialType.OTHER)
            ])
            def dataArr =
                    [
                            judgeList        : judgeList,
                            court            : court,
                            caseTypeList     : caseTypeList,
                            collegialTypeList: collegialTypeList
                    ]
            render Resp.toJson(RespType.SUCCESS, dataArr)
        }
    }

    /**
     * 向书记员客户端提供直播权限修改接口关闭
     */
    def clientCloselive() {
        if (request.method == "POST") {
            try {
                def planInfo = PlanInfo.get(params.long("id"))
                if (!planInfo) {
                    log.error("[ApiController.clientCloselive] 客户端修改直播权限接口失败，未获取到排期。接收到的数据：[${params as JSON}]")
                    render Resp.toJson(RespType.FAIL)
                    return
                }
                planInfo.allowPlay = 1
                apiService.savePlan(planInfo)
                render Resp.toJson(RespType.SUCCESS)
            } catch (e) {
                e.printStackTrace()
                log.error("[ApiController.clientCloselive] 记员客户端保存排期直播权限时出错，错误信息：${e.message}")
                render Resp.toJson(RespType.FAIL)
            }
        }
    }

    /**
     * 向书记员客户端提供直播权限修改接口开启
     */
    def clientOpenlive() {
        if (request.method == "POST") {
            try {
                def planInfo = PlanInfo.get(params.long("id"))
                if (!planInfo) {
                    log.error("[ApiController.clientOpenlive] 客户端修改直播权限接口失败，未获取到排期。接收到的数据：[${params as JSON}]")
                    render Resp.toJson(RespType.FAIL)
                    return
                }
                planInfo.allowPlay = 0
                apiService.savePlan(planInfo)
                render Resp.toJson(RespType.SUCCESS)
            } catch (e) {
                e.printStackTrace()
                log.error("[ApiController.clientOpenlive] 书记员客户端保存排期直播权限时出错，错误信息：${e.message}")
                render Resp.toJson(RespType.FAIL)
            }
        }
    }

    /**
     * 书记员客户端获取当前法庭是否有开庭庭审
     */
    def remoteTrial() {
        if (request.method == "GET") {
            try {
                def courtroomId = params.long("courtroomId")
                if (!courtroomId) {
                    render Resp.toJson(RespType.BUSINESS_VALID_FAIL)
                    return
                }
                def courtroom = Courtroom.get(courtroomId)
                if (!courtroom) {
                    render Resp.toJson(RespType.DATA_NOT_ALLOWED)
                    return
                }
                def dataList = []
                def trialList = TrialInfo.findAllByCourtroom(courtroom)
                for (def trial : trialList) {
                    if (trial.status == PlanStatus.SESSION) {
                        try {
                            dataList.add(apiService.getTrialModel(trial))
                        } catch (e) {
                            e.printStackTrace()
                        }
                        render Resp.toJson(RespType.SUCCESS, [trial: dataList])
                        return
                    }
                }
                render Resp.toJson(RespType.SUCCESS, [trial: dataList])
            } catch (e) {
                e.printStackTrace()
                log.error("[ApiController.remoteTrial] 书记员客户端获取当前法庭是否有开庭庭审失败，错误信息：${e.message}")
                render Resp.toJson(RespType.FAIL)
            }
        }
    }
//=================================================三方远程 Start==============================================
    /**
     * 书记员客户端获取远程法院列表
     */
    def remoteCourt() {
        if (request.method == "GET") {
            try {
                def courtroomList = Courtroom.findAll()
                courtroomList.each {
                    if (it.distance == null) {
                        it.distance = ""
                        it.save(flush: true)
                    }
                }
                def distanceCourtList = DistanceCourt.findAllByParentIsNull([sort: "code", order: "esc"])
                println distanceCourtList
                def data = []
                distanceCourtList.each {
                    data.add(apiService.getChildren(it))
                }
                render Resp.toJson(RespType.SUCCESS, data)
            } catch (e) {
                e.printStackTrace()
                log.error("[ApiController.remoteCourtroom] 书记员获取远程法庭列表时出现错误，错误信息：${e.message}")
                render Resp.toJson(RespType.FAIL)
            }
        }
    }

    /**
     * 书记员客户端获取此法庭正在连接的远程法庭
     */
    def remoting() {
        if (request.method == "GET") {
            try {
                def model = []
                int mode = 1
                String distance = Courtroom.get(params.long("id")).distance
                if (distance) {
                    String[] distanceArr = distance.split("///")
                    for (int i = 0; i < distanceArr.size(); i++) {
                        String service = distanceArr[i]
                        if (service.contains("no")) {
                            continue
                        }
                        String[] serviceArr = service.split(",")
                        def resp = RemoteHttpUtil.remotePost([
                                id: serviceArr[1].split("=")[1]
                        ], "${serviceArr[0].split("=")[1]}/api/remoteService/name")
                        log.info("[ApiController.remoting] 获取法庭名称响应值:${resp}")
                        model.add([
                                channel  : service.contains("service1") ? 1 : 2,
                                service  : serviceArr[0].split("=")[1],
                                id       : serviceArr[1].split("=")[1] as Integer,
                                courtName: DistanceCourt.findByService(serviceArr[0].split("=")[1]).name,
                                courtRoom: resp.data.name
                        ])
                        mode = distance.contains("status2=yes") ? 2 : 1
                    }
                }
                def resp = [mode: mode, room: model]
                log.info("[ApiController.remoting] 返回书记员数据:${resp}")
                render Resp.toJson(RespType.SUCCESS, resp)
            } catch (e) {
                e.printStackTrace()
                log.error("[ApiController.remoting] 书记员获取正在连接的远程法庭时出现错误，错误信息：${e.message}")
                render Resp.toJson(RespType.FAIL)
            }
        }
    }

    /**
     * 开始远程连接
     */
    def startConnect() {
        if (request.method == "GET") {
            def service1 = null
            def dist1 = null
            def rtsp1 = null
            def service2 = null
            def dist2 = null
            def rtsp2 = null
            def result = 500
            try {
                //获取参数
                service1 = params.get("service1")
                dist1 = params.long("dist1")
                rtsp1 = params.get("rtsp1")
                service2 = params.get("service2")
                dist2 = params.long("dist2")
                rtsp2 = params.get("rtsp2")
                //===============本地法庭判断===============
                def courtroom = Courtroom.get(params.long("id"))
                if (!courtroom) {
                    log.info("[ApiController.startConnect] id为${params.long("id")}的法庭不存在.")
                    render Resp.toJson(RespType.COURTROOM_NOT_EXSIT)
                    return
                }
                if (!courtroom.rtsp || !courtroom.rtsp1) {
                    log.info("[ApiController.startConnect] ${courtroom.name}没有设置完整送远程地址")
                    render Resp.toJson(RespType.RTSP_NOT_ALLOW)
                    return
                }
                if (courtroom.status == CourtroomStatus.STOP || courtroom.status == CourtroomStatus.ERROR) {
                    log.info("[ApiController.startConnect] ${courtroom.name}停止使用或不正常")
                    render Resp.toJson(RespType.LOCAL_COURT_NOT_USE)
                    return
                }
                if (courtroom.distance.contains("status1=yes") && courtroom.distance.contains("status2=yes")) {
                    log.info("[ApiController.startConnect] ${courtroom.name}无空闲解码器")
                    render Resp.toJson(RespType.NOT_DECODE)
                    return
                }
                String liveIp = courtroom.liveIp
                String deviceIp = courtroom.deviceIp
                log.info("[ApiController.startConnect] ${courtroom.name}的服务器地址为${liveIp},设备地址为${deviceIp}")
                if (!liveIp) {
                    log.info("[ApiController.startConnect] ${courtroom.name}没有设置直播地址")
                    render Resp.toJson(RespType.LIVE_IP_NOT_FOUND)
                    return
                }
                if (!deviceIp) {
                    log.info("[ApiController.startConnect] ${courtroom.name}没有设置庭审设备地址")
                    render Resp.toJson(RespType.DEVICE_IP_NOT_FOUND)
                    return
                }
                //===============本地法庭判断===============
                if (service1) {
                    log.info("[ApiController.startConnect] 开始判断service1是否可用")
                    service1 = service1 as String
                    //判断是否是自己调用自己
                    if (service1.contains(liveIp) && dist1 == courtroom.id) {
                        log.info("[ApiController.startConnect] ${courtroom.name}不允许调用自己为远程审理庭")
                        render Resp.toJson(RespType.NOT_ALLOW_USE_SELF)
                        return
                    }
                    //判断解码通道1是否被占用
                    if (courtroom.distance.contains("status1=yes")) {
                        log.info("[ApiController.startConnect] ${courtroom.name}解码通达1被占用")
                        render Resp.toJson(RespType.NOT_DECODE)
                        return
                    }
                    //===============判断是否可用===============
                    def resp = RemoteHttpUtil.remotePost([
                            id: dist1
                    ], "${service1}/api/remoteService/isCourtOccupied")
                    if (resp.code != 0) {
                        def respData = [code: 100]
                        if (resp.code == 501) {
                            log.info("[ApiController.startConnect] service1服务端被占用")
                            respData.put("msg", "第一个法庭被占用")
                        } else if (resp.code == 502) {
                            log.info("[ApiController.startConnect] service1服务端送远程地址不完整")
                            respData.put("msg", "远程法庭送远程地址不完整")
                        } else {
                            log.info("[ApiController.startConnect] service1服务端查询出错")
                            respData.put("msg", "第一个法庭查询失败")
                        }
                        render respData as JSON
                        return
                    }
                    //===============远程1法庭判断===============
                }
                //两个都存在或者第二个存在
                if (service2) {
                    log.info("[ApiController.startConnect] 开始判断service2是否可用")
                    service2 = service2 as String
                    //判断是否是自己调用自己
                    if ((service2.contains(liveIp) && dist2 == courtroom.id)) {
                        log.info("[ApiController.startConnect] ${courtroom.name}不允许调用自己为远程审理庭")
                        render Resp.toJson(RespType.NOT_ALLOW_USE_SELF)
                        return
                    }
                    //判断解码通道2是否被占用
                    if (courtroom.distance.contains("status2=yes")) {
                        log.info("[ApiController.startConnect] ${courtroom.name}解码通道2被占用")
                        render Resp.toJson(RespType.NOT_DECODE)
                        return
                    }
                    //判断远端是否可用
                    def resp = RemoteHttpUtil.remotePost([
                            id: dist2
                    ], "${service2}/api/remoteService/isCourtOccupied")
                    if (resp.code != 0) {
                        def respData = [code: 100]
                        if (resp.code == 501) {
                            log.info("[ApiController.startConnect] service2服务端法庭被占用")
                            respData.put("msg", "第二个法庭被占用")
                        } else if (resp.code == 502) {
                            log.info("[ApiController.startConnect] service2服务端法庭送远程地址不完整")
                            respData.put("msg", "远程法庭送远程地址不完整")
                        } else {
                            log.info("[ApiController.startConnect] service2服务端法庭端查询失败")
                            respData.put("msg", "第二个法庭查询失败")
                        }
                        render respData as JSON
                        return
                    }
                    //判断本地法庭庭审主机版本，1.0不允许连接，3.0允许连接
                    def version = ManagerHostApiUtil.getDeviceVersion(courtroom.deviceIp)
                    if (version.endsWith("v1.0")) {
                        log.info("[ApiController.startConnect] 版本号为${version}的T系列主机不支持三方远程提讯，请确认设备机型")
                        render Resp.toJson(RespType.DEVICE_TYPE_NOT_ALLOW)
                        return
                    }
                }
                //获取当前正在开庭的庭次
                def trialInfo = TrialInfo.findByCourtroomAndStatus(courtroom, PlanStatus.SESSION)
                log.info("[ApiController.startConnect] 当前法庭庭次信息:${trialInfo}")
                //===============处理service1===============
                if (service1) {
                    def resp = RemoteHttpUtil.remotePost([id: dist1, url: courtroom.rtsp, num: 0], "${service1}/api/remoteService/startConnect")
                    if (resp.code != 0) {
                        if (resp.code == 514) {
                            log.info("[ApiController.startConnect] 服务端庭审主机不支持")
                            render Resp.toJson(RespType.SERVICE_DEVICE_NOT_SUPPORT)
                            return
                        }
                        log.info("[ApiController.startConnect] service1远端设置失败")
                        render Resp.toJson(RespType.START_FAIL)
                        return
                    }
                    log.info("[ApiController.startConnect]---->service1服务端设置成功")
                    def setDecoderProfileRs = RemoteHttpUtil.send(true, courtroom, rtsp1, true, 0, 1)
                    if (setDecoderProfileRs != 0) {
                        if (setDecoderProfileRs == 404) {
                            result = 404
                        }
                        log.info("[ApiController.startConnect] service1本地设置失败")
                        throw new RuntimeException("service1本地设置失败")
                    }
                    log.info("[ApiController.startConnect]---->service1本地设置成功")
                    String service = "service1=${service1},dist1=${dist1},status1=yes///"
                    if (courtroom.distance.contains(service)) {
                        //如果这个通达已经存在不发生任何变化
                        log.info("[ApiController.startConnect] ${service}已经存在")
                    } else {
                        if (courtroom.distance.contains("service1=${service1},dist1=${dist1},status1=no///")) {
                            log.info("[ApiController.startConnect] ${service}不存在，但是存在no，进行替换")
                            courtroom.distance = courtroom.distance.replace("service1=${service1},dist1=${dist1},status1=no///", service)
                        } else {
                            log.info("[ApiController.startConnect] ${service}不存在，no也不存在,执行增加操作")
                            courtroom.distance += service
                            if (trialInfo) {
                                log.info("[ApiController.startConnect] 当前法庭有正在开庭的排期，推送给存储视频流地址.")
                                def name = RemoteHttpUtil.remotePost([
                                        id: dist1
                                ], "${service1}/api/remoteService/name")
                                //推送rtsp
                                def chnList = []
                                chnList.add([
                                        uid   : name.data.uid,
                                        name  : "远程图像(1)",
                                        number: "jmtd1",
                                        url   : rtsp1
                                ])
                                StoreCommUtil.start(trialInfo.planInfo.id, trialInfo.id, courtroom.storeIp, chnList)
                            }
                        }
                    }
                }
                //===============处理service1===============
                //===============处理service2===============
                if (service2) {
                    def resp = RemoteHttpUtil.remotePost([
                            id : dist2,
                            url: courtroom.rtsp1,
                            num: 1
                    ], "${service2}/api/remoteService/startConnect")
                    if (resp.code != 0) {
                        if (resp.code == 514) {
                            log.info("[ApiController.startConnect] 服务端庭审主机不支持")
                            render Resp.toJson(RespType.SERVICE_DEVICE_NOT_SUPPORT)
                            return
                        }
                        log.info("[ApiController.startConnect] service2服务端设置失败")
                        render Resp.toJson(RespType.START_FAIL)
                        return
                    }
                    log.info("[ApiController.startConnect]---->service2服务端设置成功")
                    def setDecoderProfileRs = RemoteHttpUtil.send(true, courtroom, rtsp2, true, 1, 1)
                    if (setDecoderProfileRs != 0) {
                        if (setDecoderProfileRs == 404) {
                            result = 404
                        }
                        log.info("[ApiController.startConnect]---->service2本地设置失败")
                        throw new RuntimeException("service2本地设置失败")
                    }
                    log.info("[ApiController.startConnect]---->service2本地设置成功")
                    String service = "service2=${service2},dist2=${dist2},status2=yes///"
                    if (courtroom.distance.contains(service)) {
                        //如果这个通达已经存在不发生任何变化
                        log.info("[ApiController.startConnect] ${service}已经存在")
                    } else {
                        if (courtroom.distance.contains("service2=${service2},dist2=${dist2},status2=no///")) {
                            log.info("[ApiController.startConnect] ${service}不存在，但是存在no，进行替换")
                            courtroom.distance = courtroom.distance.replace("service2=${service2},dist2=${dist2},status2=no///", service)
                        } else {
                            log.info("[ApiController.startConnect] ${service}不存在，no也不存在,执行增加操作")
                            courtroom.distance += service
                            if (trialInfo) {
                                log.info("[ApiController.startConnect] 当前法庭有正在开庭的排期，推送给存储视频流地址.")
                                def name = RemoteHttpUtil.remotePost([
                                        id: dist2
                                ], "${service2}/api/remoteService/name")
                                //推送rtsp
                                def chnList = []
                                chnList.add([
                                        uid   : name.data.uid,
                                        name  : "远程图像(2)",
                                        number: "jmtd2",
                                        url   : rtsp2
                                ])
                                StoreCommUtil.start(trialInfo.planInfo.id, trialInfo.id, courtroom.storeIp, chnList)
                            }
                        }
                    }
                }
                //===============处理service2===============
                //===============电视机切换===============
                String instruction
                //第二个存在则认为是三方远程
                if (courtroom.distance.contains("status2=yes")) {
                    log.info("[APiController.startConnect] status2=yes存在,判定为三方远程.")
                    instruction = "3FTX."
                } else {
                    log.info("[APiController.startConnect] status2=yes不存在,判定为双方远程.")
                    instruction = "TV0-DECODE1."
                }
                CtrlCommandUtil.ctrlCommand(courtroom.deviceIp, 8060, instruction)
                //===============电视机切换===============
                //===============操作法庭状态===============
                courtroom.status = CourtroomStatus.OCCUPIED
                courtroom.save(flush: true)
                if (courtroom.hasErrors()) {
                    log.info("[ApiController.startConnect] 保存本地法庭信息失败")
                    throw new RuntimeException("保存本地法庭信息失败")
                }
                log.info("[ApiController.startConnect] 客户端开始远程全部连接成功")
                render Resp.toJson(RespType.SUCCESS)
            } catch (e) {
                log.error("[ApiController.startConnect] 客户端开始远程连接时出现异常,错误信息:${e.message}")
                if (service1 && dist1) {
                    RemoteHttpUtil.remotePost([id: dist1], "${service1}/api/remoteService/recovery")
                }
                if (service2 && dist2) {
                    RemoteHttpUtil.remotePost([id: dist2], "${service2}/api/remoteService/recovery")
                }
                if (result == 404) {
                    render Resp.toJson(RespType.DEVICE_NOT_SUPPORT)
                } else {
                    render Resp.toJson(RespType.START_FAIL)
                }
            }
        }
    }

    /**
     * 断开远程连接
     */
    def stopConnect() {
        if (request.method == "GET") {
            try {
                def courtroom = Courtroom.get(params.long("id"))
                def service1 = params.get("service1")
                def dist1 = params.get("dist1")
                if (service1 && dist1) {
                    def resp = RemoteHttpUtil.remotePost([
                            id : dist1,
                            num: 0
                    ], "${service1}/api/remoteService/stopConnect")
                    if (resp.code != 0) {
                        log.info("[ApiController.stopConnect] service1服务端断开失败.")
                        render Resp.toJson(RespType.STOP_FAIL)
                        return
                    }
                    log.info("[ApiController.stopConnect] service1服务端断开成功.")
                    def setDecoderProfileRs = RemoteHttpUtil.send(false, courtroom, "", false, 0, 0)
                    if (setDecoderProfileRs != 0) {
                        log.info("[ApiController.stopConnect] service1客户端断开失败.")
                        render Resp.toJson(RespType.STOP_FAIL)
                        return
                    }
                    log.info("[ApiController.stopConnect] service1客户端断开成功.")
                    String serviceYes = "service1=${service1},dist1=${dist1},status1=yes///"
                    String serviceNo = "service1=${service1},dist1=${dist1},status1=no///"
                    if (courtroom.distance.contains(serviceYes)) {
                        courtroom.distance = courtroom.distance.replace(serviceYes, serviceNo)
                    }
                }
                def service2 = params.get("service2")
                def dist2 = params.get("dist2")
                if (service2 && dist2) {
                    def resp = RemoteHttpUtil.remotePost([
                            id : dist2,
                            num: 1
                    ], "${service2}/api/remoteService/stopConnect")
                    if (resp.code != 0) {
                        log.info("[ApiController.stopConnect] service2服务端断开失败.")
                        render Resp.toJson(RespType.STOP_FAIL)
                        return
                    }
                    log.info("[ApiController.stopConnect] service2服务端断开成功.")
                    def setDecoderProfileRs = RemoteHttpUtil.send(false, courtroom, "", false, 1, 0)
                    if (setDecoderProfileRs != 0) {
                        log.info("[ApiController.stopConnect] service2客户端断开失败.")
                        render Resp.toJson(RespType.STOP_FAIL)
                        return
                    }
                    log.info("[ApiController.stopConnect] service2客户端断开成功.")
                    String serviceYes = "service2=${service2},dist2=${dist2},status2=yes///"
                    String serviceNo = "service2=${service2},dist2=${dist2},status2=no///"
                    if (courtroom.distance.contains(serviceYes)) {
                        //数据存在修改
                        courtroom.distance = courtroom.distance.replace(serviceYes, serviceNo)
                    }
                }
                courtroom.status = CourtroomStatus.NORMAL
                String instruction = "TV0-HCHM."
                //第二个存在则认为是三方远程(既然是断开那肯定至多存在一个)
                if (courtroom.distance.contains("status1=yes")) {
                    instruction = "TV0-DECODE1."
                    courtroom.status = CourtroomStatus.OCCUPIED
                }
                if (courtroom.distance.contains("status2=yes")) {
                    instruction = "TV0-DECODE2."
                    courtroom.status = CourtroomStatus.OCCUPIED
                }
                log.info("[ApiController.stopConnect] 客户端断开连接指令:${instruction}")
                CtrlCommandUtil.ctrlCommand(courtroom.deviceIp, 8060, instruction)
                courtroom.save(flush: true)
                log.info("[ApiController.stopConnect]---->客户端断开远程连接成功")
                render Resp.toJson(RespType.SUCCESS)
            } catch (e) {
                e.printStackTrace()
                log.error("[ApiController.stopConnect] 客户端断开连接时出现错误回调服务端失败信息，错误信息：${e.message}")
                render Resp.toJson(RespType.STOP_FAIL)
            }
        }
    }
//==================================================三方远程 End===============================================
// ---------------------------------即时通讯接口  START  arctic 2020.02.21-----------------------------------------
    /**
     * 获取当前排期所有的聊天记录显示在书记员客户端
     */
    def allChatRecord() {
        if (request.method == "GET") {
            try {
                render Resp.toJson(RespType.SUCCESS, chatRecordService.getChatRecord(params.long("planId")).chatRecord)
            } catch (e) {
                e.printStackTrace()
                log.error("[ApiController.allChatRecord] 书记员获取排期对应聊天记录时出现错误，错误信息：${e.message}")
                render Resp.toJson(RespType.FAIL)
            }
        }
    }

    /**
     * 书记员设置排期选择状态
     */
    def setPlanChooseStatus() {
        if (request.method == "GET") {
            try {
                def courtroomId = params.long("courtroomId")
                def planId = params.long("planId")
                if (!(courtroomId && planId)) {
                    render Resp.toJson(RespType.BUSINESS_VALID_FAIL)
                    return
                }
                def courtroom = Courtroom.get(courtroomId)
                def plan = PlanInfo.get(planId)
                if (!(courtroom && plan)) {
                    render Resp.toJson(RespType.DATA_NOT_ALLOWED)
                    return
                }
                //获取此法庭所有被选择的排期记录
                def records = JudgePlanCheckRecord.findAllByCourtroomIdAndStatus(courtroomId, JudgePlanCheckStatus.CHOOSE)
                if (records.size() != 0) {
                    for (JudgePlanCheckRecord record : records) {
                        record.status = JudgePlanCheckStatus.NOT_CHOOSE
                        record.save(flush: true)
                    }
                }
                def recordsThisPlan = JudgePlanCheckRecord.findByCourtroomIdAndPlanId(courtroomId, planId)
                if (recordsThisPlan) {
                    recordsThisPlan.status = JudgePlanCheckStatus.CHOOSE
                    recordsThisPlan.save(flush: true)
                } else {
                    new JudgePlanCheckRecord(
                            courtroomId: courtroomId,
                            planId: planId,
                            status: JudgePlanCheckStatus.CHOOSE
                    ).save(flush: true)
                }
                render Resp.toJson(RespType.SUCCESS)
            } catch (e) {
                e.printStackTrace()
                log.error("[ApiController.allChatRecord] 书记员设置排期选择状态时发生异常，异常信息：${e.message}")
                render Resp.toJson(RespType.FAIL)
            }
        }
    }

    /**
     * 法官获取书记员排期选择状态
     */
    def getPlanChooseStatus() {
        if (request.method == "GET") {
            try {
                def courtroomId = params.long("courtroomId")
                if (!courtroomId) {
                    render Resp.toJson(RespType.BUSINESS_VALID_FAIL)
                    return
                }
                def courtroom = Courtroom.get(courtroomId)
                if (!courtroom) {
                    render Resp.toJson(RespType.DATA_NOT_ALLOWED)
                    return
                }
                def judgePlanRecord = JudgePlanCheckRecord.findByCourtroomIdAndStatus(courtroomId, JudgePlanCheckStatus.CHOOSE)
                if (!judgePlanRecord) {
                    render Resp.toJson(RespType.DATA_NOT_EXIST)
                    return
                }
                def planInfo = PlanInfo.get(judgePlanRecord.planId)
                def caseInfo = planInfo.caseInfo
                def data = [
                        //排期id
                        planId   : planInfo.id,
                        //案号
                        archives : caseInfo.archives,
                        //案件名称
                        name     : caseInfo.name,
                        //案件类型
                        caseType : caseInfo.type?.name,
                        //主审法官名称
                        judge    : planInfo.judge?.name,
                        //书记员名称
                        secretary: planInfo.secretary?.name,
                        //法庭名称
                        courtroom: planInfo.courtroom.name,
                        //原告
                        accuser  : caseInfo.accuser,
                        //被告
                        accused  : caseInfo.accused
                ]
                render Resp.toJson(RespType.SUCCESS, data)

            } catch (e) {
                e.printStackTrace()
                log.error("[ApiController.getPlanChooseStatus] 法官获取排期选择状态时发生异常，异常信息：${e.message}")
                render Resp.toJson(RespType.FAIL)
            }
        }
    }
    //---------------------------------即时通讯接口  END  arctic 2020.02.21-------------------------------------------
    //---------------------------------法官助手上传批注图片 START Arctic 2020.03.11--------------------------------------
    /**
     * 下载批注图片
     */
    def getComment() {
        //批注图片对应庭审主键
        def trialId = params.long("id")
        //批注图片名称
        def picName = params.get("picName")
        String path = File.separator + "usr" + File.separator + "local" + File.separator + "tc" + File.separator + "comment" + File.separator + trialId + File.separator + picName
        FileUtil.download(response, path)
    }

    /**
     * 上传批注图片
     */
    def uploadComment() {
        if (request.method == "POST") {
            try {
                def trialInfo = TrialInfo.get(params.long("id"))
                if (request instanceof MultipartHttpServletRequest) {
                    def file = request.getFile("comment")
                    if (file) {
                        //批注图片上传地址----部署到linux服务器中这儿需要改
                        def path = grailsApplication.config.getProperty('tc.trial.comment.path')
                        def result = apiService.uploadFile(path, trialInfo, file)
                        apiService.send2Clerk(result, params.get("name") ?: "")
                        render Resp.toJson(RespType.SUCCESS)
                        return
                    }
                    def msg = "[ApiController.uploadComment] 上传批注图片文件，保存庭审记录失败。接收到的数据：[${params as JSON}]\n错误信息[${trialInfo.errors}]"
                    log.error(msg)
                    render Resp.toJson(RespType.FAIL)
                } else {
                    def msg = "[ApiController.uploadComment] 上传批注图片文件，非法请求方式。接收到的数据：[${params as JSON}]"
                    log.error(msg)
                    render Resp.toJson(RespType.FAIL)
                }
            } catch (e) {
                e.printStackTrace()
                log.error("[ApiController.uploadComment] 法官上传批注图片时出错，错误信息：${e.message}")
                render Resp.toJson(RespType.FAIL)
            }
        }
    }

    /**
     * 法官登录时获取此法庭正在开庭的庭次
     */
    def getOpenTrailId() {
        if (request.method == "GET") {
            try {
                def planInfo = PlanInfo.get(params.long("planId"))
                println planInfo.toString()
                def trails = planInfo.trialInfo
                for (TrialInfo trial : trails) {
                    if (!trial.endDate) {
                        render Resp.toJson(RespType.SUCCESS, [trialId: trial.id])
                        return
                    }
                }
                render Resp.toJson(RespType.DATA_NOT_EXIST)
            } catch (e) {
                e.printStackTrace()
                log.error("[ApiController.getOpenTrailId] 法官上传批注图片时出错，错误信息：${e.message}")
                render Resp.toJson(RespType.FAIL)
            }
        }
    }
//---------------------------------------法官助手上传批注图片 END Arctic 2020.03.11------------------------------------------
//-------------------------------------------书记员客户端版本号校验--------------------------------------------------
    //统计各法庭客户端版本
    def checkVersion() {
        try {
            //软件类型
            def type = params.get("type")
            //软件版本
            def version = params.get("version") as String
            //法庭
            def courtroom = Courtroom.get(params.long("courtroomId"))
            if (!(version && courtroom)) {
                render Resp.toJson(RespType.DATA_NOT_ALLOWED)
                return
            }
            //通过法庭查看软件版本
            def sv = SecretaryVersion.findByCourtroom(courtroom)
            if (!sv) {
                sv = new SecretaryVersion()
                sv.courtroom = courtroom
            }
            if (!type) {
                sv.serviceVersion = version
            } else {
                if (type == "sjy") {
                    sv.serviceVersion = version
                }
                if (type == "fg") {
                    sv.fgSoftVersion = version
                }
                if (type == "dsr") {
                    sv.dsrSoftVersion = version
                }
            }
            if (courtroom.deviceIp) {
                if (IpUtil.ping(courtroom.deviceIp)) {
                    //庭审主机可以通，去获取版本信息
                    sv.deviceVersion = ManagerHostApiUtil.getDeviceVersion(courtroom.deviceIp)
                }
            }
            sv.save(flush: true)
            //升级维护
            String fileName = ""
            def lastVersion
            if (!type) {
                lastVersion = Dict.findByCode("SYSTEM_CONFIG").ext1?.split("/")
            } else {
                if (type == "sjy") {
                    lastVersion = Dict.findByCode("SYSTEM_CONFIG").ext1?.split("/")
                }
                if (type == "fg") {
                    lastVersion = Dict.findByCode("SYSTEM_CONFIG").ext2?.split("/")
                }
                if (type == "dsr") {
                    lastVersion = Dict.findByCode("SYSTEM_CONFIG").ext3?.split("/")
                }
            }
            if (lastVersion && lastVersion[0] && lastVersion[0] != version) {
                fileName = lastVersion[1]
            }
            def data = [:]
            data.put("fileName", fileName)
            data.put("path", "api/client/lastVersion?type=${type ?: ""}")
            //版本号发生改变后为true
            render Resp.toJson(RespType.SUCCESS, data)
        } catch (e) {
            e.printStackTrace()
            log.error("[ApiController checkVersion] 添加或更新当前书记员版本时出错, 错误信息：${e.message}")
            render Resp.toJson(RespType.FAIL)
        }
    }

    /**
     * 下载最新版本的书记员软件
     */
    def lastVersion() {
        def type = params.get("type")
        def system_config = Dict.findByCode("SYSTEM_CONFIG")
        def path = ""
        if (!type) {
            path = SJY_UPDATE_PACKAGE_STORE_BASE_ADDRESS + system_config.ext1?.split("/")[1]
        } else {
            type = type as String
            if (type == "sjy") {
                path = SJY_UPDATE_PACKAGE_STORE_BASE_ADDRESS + system_config.ext1?.split("/")[1]
            }
            if (type == "fg") {
                path = FG_UPDATE_PACKAGE_STORE_BASE_ADDRESS + system_config.ext2?.split("/")[1]
            }
            if (type == "dsr") {
                path = DSR_UPDATE_PACKAGE_STORE_BASE_ADDRESS + system_config.ext3?.split("/")[1]
            }
        }
        FileUtil.download(response, path)
    }

    /**
     * 内存/磁盘报警接口
     */
    def alarm() {
        if (request.method == "GET") {
            def alarmList = Alarm.findAllByDateCreatedBetween(DateUtil.beginOfDay(new Date()), DateUtil.endOfDay(new Date()), [sort: "dateCreated", order: "desc"])
            def list = new ArrayList()
            def set = new HashSet<Integer>()
            for (def alarm : alarmList) {
                if (!set.contains(alarm.alarmType) && alarm.alarmType == 2) { // 只对本地存储视频磁盘空间不足进行报警
                    set.add(alarm.alarmType)
                    list.add(alarm)
                }
            }
            JSON.registerObjectMarshaller(Date) {
                return it?.format("yyyy-MM-dd HH:mm:ss")
            }
            render Resp.toJson(RespType.SUCCESS, list)
        }
    }
}
