package com.hxht.autodeploy.leader

import com.hxht.techcrt.DataStatus

import com.hxht.techcrt.PlanStatus
import com.hxht.techcrt.PositionStatus
import com.hxht.techcrt.Resp
import com.hxht.techcrt.User
import com.hxht.techcrt.api.LeaderApiService
import com.hxht.techcrt.court.CaseInfo
import com.hxht.techcrt.court.Courtroom
import com.hxht.techcrt.court.Employee
import com.hxht.techcrt.court.PlanInfo
import com.hxht.techcrt.court.PlanTrial
import com.hxht.techcrt.court.TrialInfo
import com.hxht.techcrt.court.VideoInfo
import com.hxht.techcrt.court.VideoRecord
import com.hxht.techcrt.court.manager.info.courtroom.CtrlService
import com.hxht.techcrt.court.plan.ChatRecordService
import com.hxht.techcrt.enums.PlayStatus
import com.hxht.techcrt.enums.RespType
import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityService

/**
 * 2021.04.22 >>> 删除远程法庭相关代码 daniel
 */
class LeaderController {
    LeaderService leaderService
    CtrlService ctrlService
    SpringSecurityService springSecurityService
    ChatRecordService chatRecordService
    LeaderApiService leaderApiService

    def list() {
        if (request.method == "GET") {
            def courtroomList = Courtroom.findAll()
            def secretaryList = Employee.findAllByPosition(PositionStatus.SECRETARY)
            def judgeList = Employee.findAllByPosition(PositionStatus.JUDGE)
            return ["judgeList": judgeList, "secretaryList": secretaryList, "courtroomList": courtroomList]
        }
        if (request.method == "POST") {
            def model = leaderService.list(params)
            render model as JSON
        }
    }

    def query() {
        if (request.method == "GET") {
            def courtroomList = Courtroom.findAll()
            def secretaryList = Employee.findAllByPosition(PositionStatus.SECRETARY)
            def judgeList = Employee.findAllByPosition(PositionStatus.JUDGE)
            return ["judgeList": judgeList, "secretaryList": secretaryList, "courtroomList": courtroomList]
        }
        if (request.method == "POST") {
            def model = leaderService.query(params)
            render model as JSON
        }
    }

    def show() {
        def planInfo = PlanInfo.get(params.long("id"))
        def trialInfo = TrialInfo.get(params.long("trial")) //指定了trial进行播放
        if (!planInfo) {
            planInfo = trialInfo.planInfo
        }
        def courtroom = planInfo.courtroom
        if (!courtroom.cfg) {
            courtroom.cfg = ctrlService.getCfg(courtroom) as JSON
        }
        def cfg = JSON.parse(courtroom.cfg)
        def videoInfoList = new ArrayList<VideoInfo>()
        def chnList = []
        def flag = false
        if (!trialInfo) {
            if (planInfo.status == PlanStatus.SESSION) {//排期处于开庭状态
                flag = true
                //正在开庭的为显示trial
                trialInfo = TrialInfo.findByPlanInfoAndStatusAndActive(planInfo, PlanStatus.SESSION, DataStatus.SHOW)
            } else if (planInfo.status == PlanStatus.ADJOURN || planInfo.status == PlanStatus.CLOSED) {//排期休庭或者闭庭了
                def trialInfoList = TrialInfo.findAllByPlanInfoAndActive(planInfo, DataStatus.SHOW, [sort: "status", order: "asc"])
                if (trialInfoList) {
                    trialInfo = trialInfoList.get(0)
                    videoInfoList = VideoInfo.findAllByTrialInfo(trialInfo)
                }
            }
        } else {
            //根据trial查询出所有的videoInfo
            videoInfoList = VideoInfo.findAllByTrialInfo(trialInfo)
        }
        if (flag) {
            //切换视频所有频道
            for (def encode : cfg.encode) {
                chnList.add([
                        number: encode.number,
                        name  : encode.name
                ])
            }
        } else {
            //遍历所有的videoInfo显示所有这个庭审的通道号和通道名称
            for (def video : videoInfoList) {
                if (video.channelName.indexOf("合成") > -1) {
                    chnList.add([
                            number: video.channelNum,
                            name  : video.channelName
                    ])
                    break
                }
            }
            for (def video : videoInfoList) {
                if (video.channelName.indexOf("合成") > -1) {
                    continue
                }
                chnList.add([
                        number: video.channelNum,
                        name  : video.channelName
                ])
            }
            //使用hashset去掉重复
            def set = new HashSet<>(chnList)
            chnList = new LinkedList<>(set)
        }
        def users = leaderApiService.currentUser as User
        def data = [
                planId      : planInfo.id,
                trialId     : trialInfo?.id ?: "",
                caseArchives: planInfo.caseInfo.archives,
                caseName    : planInfo.caseInfo.name,
                caseType    : planInfo.caseInfo.type,
                courtroom   : trialInfo?.courtroom ?: planInfo.courtroom,
                accuser     : planInfo.caseInfo.accuser ?: "无数据",
                accused     : planInfo.caseInfo.accused ?: "无数据",
                filingDate  : planInfo.caseInfo.filingDate?.format('yyyy/MM/dd HH:mm'),
                startDate   : trialInfo?.startDate?.format('yyyy/MM/dd HH:mm') ?: planInfo.startDate?.format('yyyy/MM/dd HH:mm'),
                endDate     : trialInfo?.endDate?.format('yyyy/MM/dd HH:mm') ?: planInfo.endDate?.format('yyyy/MM/dd HH:mm'),
                collegial   : planInfo.collegial.size() == 0 ? "无数据" : planInfo.collegial,
                secretary   : trialInfo?.secretary ?: planInfo.secretary,
                summary     : planInfo.caseInfo.summary ?: "无数据",
                detail      : planInfo.caseInfo.detail ?: "无数据",
                status      : trialInfo?.status ?: planInfo.status,
                allowPlay   : planInfo.allowPlay,
                judge       : planInfo.judge?.name ?: "无数据",
                userId      : (leaderApiService.currentUser as User).id
        ]
        def trialList = leaderService.getTrialVideoList(planInfo)
        //获取当前用户信息
        def userName = users.username
        List<CaseInfo> caseList = new ArrayList<>()
        def planTrialList = PlanTrial.findAllByTrialInfo(PlanTrial.findByPlanInfo(planInfo)?.trialInfo)
        if (planTrialList.size() > 0) {
            for (PlanTrial pt : planTrialList) {
                if (pt.planInfo.caseInfoId == planInfo.caseInfoId) {
                    continue
                }
                caseList.add(pt.planInfo.caseInfo)
            }
        }
        [data: data, userName: userName, chatRecord: chatRecordService.getChatRecord(planInfo.id).chatRecord, chnList: chnList, trialList: trialList, caseList: caseList]
    }

    def liveShow() {
        def trialInfo = TrialInfo.get(params.long("id"))
        if (trialInfo) {
            def video = leaderService.liveShow(trialInfo)
            return render(Resp.toJson(RespType.SUCCESS, [status: trialInfo.status, video: video]))
        }
        render Resp.toJson(RespType.FAIL)
    }

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
            log.error("下载文件出错:" + e.getMessage())
        }
    }

    def getComment() {
        //获取图片地址
        def uploadUrl = grailsApplication.config.getProperty('tc.trial.images.path')
        //视频图片对应庭审主键
        def trialId = params.get("trialId")
        //批注图片名称
        def picName = params.get("picName")
        File file = new File("${uploadUrl}/${trialId}/${picName}")
        if (file.exists()) {
            //下载
            InputStream fis = new BufferedInputStream(new FileInputStream("${uploadUrl}/${trialId}/${picName}"))
            byte[] buffer = new byte[fis.available()]
            fis.read(buffer)
            fis.close()
            OutputStream toClient = new BufferedOutputStream(response.getOutputStream())
            response.reset()
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(file.getName(), "UTF-8"))
            response.addHeader("Content-Length", "" + file.length())
            response.setContentType("application/octet-stream")
            toClient.write(buffer)
            toClient.flush()
            toClient.close()
        } else {
            return render(Resp.toJson(RespType.FAIL))
        }
    }

    def downTrialNote() {
        def trialInfo = TrialInfo.get(params.long("id"))
        if (trialInfo) {
            if (trialInfo.note) {
                try {
                    def file = new File(trialInfo.note)
                    def filelist = file.readLines()
                    render Resp.toJson(RespType.SUCCESS, filelist)
                } catch (Exception e) {
                    e.printStackTrace()
                    def msg = "庭审控制下载笔录时候时出错！${e.message}"
                    log.error("[LeaderController.downTrialNote]${msg}")
                    render Resp.toJson(RespType.FAIL, msg)
                }
            } else {
                def message = "没有笔录文件"
                render Resp.toJson(RespType.SUCCESS, message)
            }

        }

    }

    def trialList() {
        if (request.method == "GET") {
            def courtroomList = Courtroom.findAll()
            def secretaryList = Employee.findAllByPosition(PositionStatus.SECRETARY)
            def judgeList = Employee.findAllByPosition(PositionStatus.JUDGE)
            return ["judgeList": judgeList, "secretaryList": secretaryList, "courtroomList": courtroomList]
        }

        if (request.method == "POST") {
            def model = leaderService.trialList(params)
            render model as JSON
        }
    }

    def trialVideoList() {
        if (request.method == "GET") {
            def courtroomList = Courtroom.findAll()
            def secretaryList = Employee.findAllByPosition(PositionStatus.SECRETARY)
            def judgeList = Employee.findAllByPosition(PositionStatus.JUDGE)
            return ["judgeList": judgeList, "secretaryList": secretaryList, "courtroomList": courtroomList]
        }

        if (request.method == "POST") {
            def model = leaderService.trialVideoList(params)
            render model as JSON
        }
    }

    def courtroomStatus() {
        if (request.method == "POST") {
            def draw = params.int("draw") ?: 1// 记录操作的次数 每次加1
            def start = params.int("start") ?: 0// 起始
            def length = params.int("length") ?: 20// 每页显示的size
            def search = params.get("search[value]") as String//搜索内容
            def model = leaderService.courtroomList(draw, start, length, search)
            render model as JSON
        } else {
            ["isAllow": grailsApplication.config.getProperty('tc.remote.report.is-allow')]
        }
    }

    def videoshow() {
        //根据法庭主键获取法庭信息
        def courtroom = Courtroom.get(params.long("id"))
        if (!courtroom) {
            redirect(controller: "courtroom", action: "list")
            return
        }
        if (!courtroom.cfg) {
            courtroom.cfg = ctrlService.getCfg(courtroom) as JSON
        }
        def cfg = JSON.parse(courtroom.cfg)
        def chnList = []
        for (def encode : cfg.encode) {
            chnList.add([
                    number: encode.number,
                    name  : encode.name
            ])
        }
        //根据法庭获取相关正在开庭的排期---->分为正在开庭还是没有正在开庭
        def planInfo = PlanInfo.findByCourtroomAndStatus(courtroom, PlanStatus.SESSION)
        //如果有正在开庭的planInfo，那我们就去获取与其相关的trailInfo
        if (planInfo) {
            def trialInfo = null
            //排期处于开庭状态
            if (planInfo.status == PlanStatus.SESSION) {
                //正在开庭的为显示trial
                trialInfo = TrialInfo.findByPlanInfoAndStatusAndActive(planInfo, PlanStatus.SESSION, DataStatus.SHOW)
            } else if (planInfo.status == PlanStatus.ADJOURN || planInfo.status == PlanStatus.CLOSED) {//排期休庭或者闭庭了
                def trialInfoList = TrialInfo.findAllByPlanInfoAndActive(planInfo, DataStatus.SHOW, [sort: "status", order: "asc"])
                trialInfo = trialInfoList.get(0)
            }
            def data = [
                    planId      : planInfo.id,
                    trialId     : trialInfo?.id ?: "",
                    caseArchives: planInfo.caseInfo.archives,
                    caseName    : planInfo.caseInfo.name,
                    caseType    : planInfo.caseInfo.type,
                    courtroom   : trialInfo?.courtroom ?: planInfo.courtroom,
                    accuser     : planInfo.caseInfo.accuser ?: "无数据",
                    accused     : planInfo.caseInfo.accused ?: "无数据",
                    filingDate  : planInfo.caseInfo.filingDate?.format('yyyy/MM/dd HH:mm'),
                    startDate   : trialInfo?.startDate?.format('yyyy/MM/dd HH:mm') ?: planInfo.startDate?.format('yyyy/MM/dd HH:mm'),
                    endDate     : trialInfo?.endDate?.format('yyyy/MM/dd HH:mm') ?: planInfo.endDate?.format('yyyy/MM/dd HH:mm'),
                    collegial   : planInfo.collegial,
                    secretary   : trialInfo?.secretary ?: planInfo.secretary,
                    summary     : planInfo.caseInfo.summary ?: "无数据",
                    detail      : planInfo.caseInfo.detail ?: "无数据",
                    status      : trialInfo?.status ?: planInfo.status
            ]
            [data: data, chnList: chnList, courtroom: courtroom]
        } else {
            //没有查找到正在开庭的排期
            def data = [
                    planId: "",
            ]
            [data: data, chnList: chnList, courtroom: courtroom]
        }
    }

    def showVideo() {
        //房间主键一定存在
        def courtroom = Courtroom.get(params.long("id"))
        def cfg = JSON.parse(courtroom.cfg)
        def videoList = []
        //法庭没有开庭的案件
        for (def encode : cfg.encode) {
            def url = "http://${courtroom.liveIp}:8791/${encode.encodeip}/${encode.number}.flv"
            videoList.add([
                    number: encode.number,
                    name  : encode.name,
                    url   : url
            ])
        }
        render Resp.toJson(RespType.SUCCESS, [video: videoList])
    }
}

