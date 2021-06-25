package autodeploy

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?" {
            constraints {
                // apply constraints here
            }
        }
        //庭审流程接口 书记员客户端 法官助手接口 在用
        group "/api/client", {
            "/token?"(controller: "api", action: "token")//GET 用户登陆获取token
            "/current"(controller: "api", action: "current")//GET 获取当前登陆用户信息，解析token
            "/courtroom/$id?"(controller: "api", action: "courtroom")//GET 获取法庭
            "/plan/$id?"(controller: "api", action: "plan")//GET 获取排期-需要登陆
            "/close/"(controller: "api", action: "clientCloselive")//POST修改直播权限关闭
            "/open/"(controller: "api", action: "clientOpenlive")//POST修改直播权限开启
            "/remoteCourt/"(controller: "api", action: "remoteCourt")//获取所有的远程法院
            "/remoting/"(controller: "api", action: "remoting")//获取正在连接的远程法庭信息
            "/isCourtOccupied/"(controller: "api", action: "isCourtOccupied")//判断此远程法庭现在是否被占用
            "/startConnect/"(controller: "api", action: "startConnect")//开始远程连接
            "/stopConnect/"(controller: "api", action: "stopConnect")//断开远程连接
            "/chatRecord/"(controller: "api", action: "allChatRecord")//获取聊天记录
            "/setPlanChooseStatus/"(controller: "api", action: "setPlanChooseStatus")//书记员设置排期选择状态
            "/getPlanChooseStatus/"(controller: "api", action: "getPlanChooseStatus")//法官获取书记员排期选择状态
            "/checkVersion/"(controller: "api", action: "checkVersion")//上报当前书记员客户端版本，并校验是否需要更新
            "/lastVersion/"(controller: "api", action: "lastVersion")//下载最新版书记员软件
            "/logout/"(controller: "api", action: "logout")//登出接口
            "/heart/"(controller: "api", action: "heart")//心跳接口
            "/statisSpeech/"(controller: "api", action: "statisSpeech")//统计语音识别
            "/alarm/"(controller: "api", action: "alarm")//内存-磁盘报警接口
            "/warn/$id?"(controller: "bailiff", action: "warn")//报警
            "/quit/$id?"(controller: "bailiff", action: "quit")//取消报警
            "/combinePlan/"(controller: "api", action: "combinePlan")//书记员客户端处并案
            "/cancelCombinePlan/"(controller: "api", action: "cancelCombinePlan")//取消书记员客户端处并案
        }

        //书记员客户端对接接口
        group "/api/client/trial", {
            "/"(controller: "api", action: "trial")//GET 获取庭审-需要登陆
            "/note/$id?"(controller: "api", action: "trialNote")//POST 提交笔录-需要登陆
            "/down/note/$id?"(controller: "api", action: "downTrialNote")//POST 下载笔录-需要登陆
            "/closed/$id?"(controller: "api", action: "tiralClosed")//POST 提交闭庭操作-需要登陆
            "/adjourn/$id?"(controller: "api", action: "tiralAdjourn")//POST 提交休庭操作-需要登陆
            "/open/$id?"(controller: "api", action: "trialOpen")//POST 提交开庭操作-需要登陆
            "/archived/$id?"(controller: "api", action: "trialArchived")//POST 提交归档操
            "/comment/$id?"(controller: "api", action: "uploadComment")//POST 批注图片上传
            "/getComment/$id/$picName?"(controller: "api", action: "getComment")//Get 批注图片下载
            "/getTrialId/$id?"(controller: "api", action: "getOpenTrailId")//POST 提交开庭操作-需要登陆
            "/remoteTrial/"(controller: "api", action: "remoteTrial")//POST 查看是否存在庭审
        }

        //给书记员远程签名用的公开接口
        group "/api/client/signature", {
            "/list/$id"(controller: "api", action: "clientSignaturePersonList")//GET获取签名数据
            "/down/$id"(controller: "api", action: "clientSignatureDownload")//下载签名图片
            "/plan/list/$id"(controller: "mobileApi", action: "signaturePlanList")//GET获取今日排期列表
            "/person/list/$id"(controller: "mobileApi", action: "signaturePersonList")//GET获取排期签名人员列表
            "/person/submit/$id?"(controller: "mobileApi", action: "signaturePersonSubmit")//POST提交签名数据
        }

        //书记员客户端临时立案接口
        group "/api/client/temporary", {
            "/case/"(controller: "api", action: "clientTemporaryCase")////POST保存临时立案接口
            "/get/all/"(controller: "api", action: "clientTemporaryGetAll")//GET获取所有法院法庭等信息
        }

        //笔录签名接口
        group "/api/mobile/signature", {
            "/plan/list"(controller: "mobileApi", action: "signaturePlanList")//GET获取今日排期列表
            "/person/list/$id"(controller: "mobileApi", action: "signaturePersonList")//GET获取排期签名人员列表
            "/person/submit/$id?"(controller: "mobileApi", action: "signaturePersonSubmit")//POST提交签名数据
        }

        //获取庭显接口
        group "/api/notice", {
            "/courtroom"(controller: "apiNotice", action: "courtroom")//获取全部法庭接口
            "/plan"(controller: "apiNotice", action: "plan")//根据法庭获取排期列表
        }
        //中恒信接口
        group "/zhongheng", {
            "/getCourtRoomList/"(controller: "zhongheng", action: "getCourtRoomList")//中恒获取法庭信息
            "/getCourtScheduledInfo/"(controller: "zhongheng", action: "getCourtScheduledInfo")//中恒根据多条件查询排期
        }
        //多方远程服务端接口
        group "/api/remoteService", {
            "/isCourtOccupied/"(controller: "remoteService", action: "isCourtOccupied")//获取远程法庭占用情况
            "/startConnect/"(controller: "remoteService", action: "startConnect")//远程法庭连接
            "/stopConnect/"(controller: "remoteService", action: "stopConnect")//远程法庭断开
            "/recovery/"(controller: "remoteService", action: "recovery")//失败回调
            "/courtroom/"(controller: "remoteService", action: "courtroom")//远程法庭名称
            "/name/"(controller: "remoteService", action: "courtroomName")//远程法庭名称
        }

        //对接太极接口
        group "/api", {
            "/verifyHtml/"(controller: "apiTaiChi", action: "verifyHtml")
            "/livingHtml/"(controller: "apiTaiChi", action: "show")
            "/taiChi/showVideo/$id?"(controller: "apiTaiChi", action: "showVideo")
        }

        group "/api", {
            "/zhibo"(controller: "showVideo", action: "zhibo")
            "/dianbo"(controller: "showVideo", action: "dianbo")
            "/showVideo"(controller: "showVideo", action: "showVideo")
        }

        //语音控制开关接口
        group "/api/volume/control", {
            "/"(controller: "apiVolumeControls", action: "edit")
            "/send/"(controller: "apiVolumeControls", action: "send")
            "/editConfig/"(controller: "apiVolumeControls", action: "editConfig")
        }

        //传唤软件接口
        group "/api/device", {
            "/allCourtroom"(controller: "device", action: "allCourtroom")
            "/getLitigant/$id?"(controller: "device", action: "getLitigant")
            "/call/$deviceUid?"(controller: "device", action: "call")
            "/allDevice"(controller: "device", action: "allDevice")
        }

        //评查系统调用所有历史闭庭数据接口
        "/api/getPlanToVc/"(controller: "apiVerification", action: "planDataToVc")

        //书记员客户端离线开庭的信息上传接口
        "/api/offLine/save/"(controller: "apiOfflinePlan", action: "offlineSave")

        //存储用api接口
        "/api/store/video/$id?(.$format)?"(controller: "storeApi", action: "video")

        "/case/list?/$id?(.$format)?"(controller: "plan", action: "caseInfoList")//案件列表
        "/case?/$id?(.$format)?"(controller: "plan", action: "getCaseById")//根据主键获取案件

        //日志
        "/log/system/list?/$id?(.$format)?"(controller: "log", action: "logList")//系统操作日志列表
        "/log/login/list?/$id?(.$format)?"(controller: "log", action: "logLoginList")//登录记录列表
        "/log/system/delLogs?/$id?(.$format)?"(controller: "log", action: "delSystemLogs")//删除系统操作日志

        //部门编辑保存
        "/department/edit/save?/$id?(.$format)?"(controller: "department", action: "editSave")

        //人员编辑保存
        "/employee/edit/save?/$id?(.$format)?"(controller: "employee", action: "editSave")

        //案件类型编辑保存
        "/caseType/edit/save?/$id?(.$format)?"(controller: "caseType", action: "editSave")

        //编码器
        "/ctrl/encode/add?/$id?(.$format)?"(controller: "ctrl", action: "addEncode")
        "/ctrl/ycEncode/add?/$id?(.$format)?"(controller: "ctrl", action: "addYcEncode")
        "/ctrl/encode/edit?/$id?(.$format)?"(controller: "ctrl", action: "editEncode")
        "/ctrl/ycEncode/edit?/$id?(.$format)?"(controller: "ctrl", action: "editYcEncode")
        //解码器
        "/ctrl/decode/add?/$id?(.$format)?"(controller: "ctrl", action: "addDecode")
        "/ctrl/decode/edit?/$id?(.$format)?"(controller: "ctrl", action: "editDecode")
        //VIDEO矩阵
        "/ctrl/video/add?/$id?(.$format)?"(controller: "ctrl", action: "addVideo")
        "/ctrl/video/edit?/$id?(.$format)?"(controller: "ctrl", action: "editVideo")
        //VGA矩阵
        "/ctrl/vga/add?/$id?(.$format)?"(controller: "ctrl", action: "addVga")
        "/ctrl/vga/edit?/$id?(.$format)?"(controller: "ctrl", action: "editVga")
        //输出控制
        "/ctrl/out/add?/$id?(.$format)?"(controller: "ctrl", action: "addOut")
        "/ctrl/out/edit?/$id?(.$format)?"(controller: "ctrl", action: "editOut")
        //红外控制
        "/ctrl/irctrl/add?/$id?(.$format)?"(controller: "ctrl", action: "addIrctrl")
        "/ctrl/irctrl/edit?/$id?(.$format)?"(controller: "ctrl", action: "editIrctrl")
        "/ctrl/irctrl/button?/$id/$uuid?(.$format)?"(controller: "ctrl", action: "irctrlBtns")
        "/ctrl/irctrl/button/add?/$id/$uuid?(.$format)?"(controller: "ctrl", action: "addIrctrlBtns")
        "/ctrl/irctrl/button/edit?/$id/$irctrlUid?(.$format)?"(controller: "ctrl", action: "editIrctrlBtns")
        //音量控制
        "/ctrl/sound/add?/$id?(.$format)?"(controller: "ctrl", action: "addSound")
        "/ctrl/sound/edit?/$id?(.$format)?"(controller: "ctrl", action: "editSound")
        //综合控制
        "/ctrl/total/add?/$id?(.$format)?"(controller: "ctrl", action: "addTotal")
        "/ctrl/total/edit?/$id?(.$format)?"(controller: "ctrl", action: "editTotal")
        //强电控制
        "/ctrl/power/add?/$id?(.$format)?"(controller: "ctrl", action: "addPower")
        "/ctrl/power/edit?/$id?(.$format)?"(controller: "ctrl", action: "editPower")
        "/ctrl/power/button?/$id/$uuid?(.$format)?"(controller: "ctrl", action: "powerBtns")
        "/ctrl/power/button/add?/$id/$uuid?(.$format)?"(controller: "ctrl", action: "addPowerBtns")
        "/ctrl/power/button/edit?/$id/$powerUid?(.$format)?"(controller: "ctrl", action: "editPowerBtns")
        //新电源控制
        "/ctrl/powerNew/add?/$id?(.$format)?"(controller: "ctrl", action: "addPowerNew")
        "/ctrl/powerNew/edit?/$id?(.$format)?"(controller: "ctrl", action: "editPowerNew")
        "/ctrl/powerNew/button?/$id/$uuid?(.$format)?"(controller: "ctrl", action: "powerNewBtns")
        "/ctrl/powerNew/button/add?/$id/$uuid?(.$format)?"(controller: "ctrl", action: "addPowerNewBtns")
        "/ctrl/powerNew/button/edit?/$id/$powerNewUid?(.$format)?"(controller: "ctrl", action: "editPowerNewBtns")
        //摄像头控制
        "/ctrl/camera/add?/$id?(.$format)?"(controller: "ctrl", action: "addCamera")
        "/ctrl/camera/edit?/$id?(.$format)?"(controller: "ctrl", action: "editCamera")
        //摄像头控制指令
        "/ctrl/camera/buttons?/$id?(.$format)?"(controller: "ctrl", action: "buttons")
        "/ctrl/buttons/add?/$id?(.$format)?"(controller: "ctrl", action: "addButtons")
        "/ctrl/buttons/edit?/$id?(.$format)?"(controller: "ctrl", action: "editButtons")
        //摄像头预置位
        "/ctrl/camera/presets?/$id?(.$format)?"(controller: "ctrl", action: "presets")
        "/ctrl/persets/add?/$id?(.$format)?"(controller: "ctrl", action: "addPresets")
        "/ctrl/presets/edit?/$id?(.$format)?"(controller: "ctrl", action: "editPresets")

        "/"(controller: "index")
        "500"(controller: "error")
        "404"(controller: "error")
        "405"(controller: "error")
        "300"(controller: "authority")
    }
}
