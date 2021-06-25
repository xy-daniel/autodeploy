package com.hxht.autodeploy.sync.util

import com.hxht.techcrt.Dict
import com.hxht.techcrt.PushLog
import com.hxht.techcrt.User
import com.hxht.techcrt.court.CaseInfo
import com.hxht.techcrt.court.Courtroom
import com.hxht.techcrt.court.Employee
import com.hxht.techcrt.court.PlanInfo
import com.hxht.techcrt.enums.PUSH_CODE
import com.hxht.techcrt.service.sync.huigu.entity.Users
import com.hxht.techcrt.util.http.HttpClientResponse
import com.hxht.techcrt.utils.PinYinUtils
import com.hxht.techcrt.utils.UUIDGenerator
import grails.converters.JSON
import grails.web.context.ServletContextHolder
import org.dom4j.Document
import org.dom4j.Element
import org.dom4j.io.SAXReader
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.RequestMethod
import javax.annotation.PostConstruct
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat

class PushUtil {
    private static Logger log = LoggerFactory.getLogger(PushUtil.class)
    public static List<String> CENTRAL_URL_LIST = Collections.synchronizedList(new ArrayList<String>())

    static boolean initCentralUrlList() {
        return initCentralUrlList(false)
    }

    @PostConstruct
    static boolean initCentralUrlList(boolean isFile) {
        log.info("[PushUtil.initCentralUrlList]---->准备获取cmp推送地址")
        if (isFile) {
            try {
                log.info("initCentralUrlList 请求本地url列表")
                String path = PushUtil.class.getProtectionDomain().getCodeSource().getLocation().getFile()
                log.info("initCentralUrlList 文件路径:${path}")
                path = URLDecoder.decode(path, "UTF-8") // 转换处理中文及空格
                log.info("initCentralUrlList 转换中文及空格字后的路径:${path}")
                File file = new File(path)
                log.info("initCentralUrlList文件路径："+file.getParent() + "//upload.json")
                File upload = new File(file.getParent() + "upload.json")
                log.info("initCentralUrlList实际路径："+file.getParent() + "//upload.json")
                if (upload.exists() && upload.isFile()) {
                    log.info("文件存在,直接将数据加载到内存中")
                    //存在
                    InputStream input = new FileInputStream(upload)
                    InputStreamReader isr = new InputStreamReader(input, StandardCharsets.UTF_8)
                    BufferedReader br = new BufferedReader(isr)
                    StringBuilder stringBuilder = new StringBuilder()
                    String line
                    while ((line = br.readLine()) != null) {
                        stringBuilder.append(line.trim())
                    }
                    isr.close()
                    br.close()
                    return handleUrlList(stringBuilder.toString())
                } else {
                    log.info("文件不存在")
                    return handleNetworkUrl()
                }
            } catch (Exception e) {
                log.error("处理本地upload.json文件时出错，错误信息：{}", e.getMessage())
            }
        } else {
            return handleNetworkUrl()
        }
        return false
    }

    private static boolean handleNetworkUrl() {
        log.info("获取地址开始")
        InputStream inputStream = ServletContextHolder.getServletContext().getResourceAsStream("/court_list/cmp.xml")
        SAXReader sr = new SAXReader()
        InputStreamReader reader = new InputStreamReader(inputStream)
        BufferedReader bufferedReader = new BufferedReader(reader)
        Document doc = sr.read(bufferedReader)
        Element root = doc.getRootElement()
        String addr = root.elementText("ADDR")
        String sync_url = ""
        if (addr == "huigu"){
            sync_url = root.elementText("HUIGU")
        }
        if (addr == "rongji"){
            sync_url = root.elementText("RONGJI")
        }
        log.info("请求地址：${sync_url}")
        if (!StringUtils.isEmpty(sync_url)) {
            log.info("handleNetworkUrl 现在将会对目标地址{}进行请求url上传列表", sync_url)
            HttpClientResponse resp = SyncUtils.sync(sync_url, RequestMethod.POST, null)
            log.info("远程获取upload地址请求成功，服务器返回状态码：{}", resp.getCode())
            log.info("服务器返回的数据${resp.getResponseText()}")
            boolean success = false
            JSONObject result = new JSONObject(resp.getResponseText())
            log.info("转换后的JSONObject====${result.toString()}")
            if (result.get("code") == 0) {
                log.info("获取到可以转换的数据")
                JSONArray jsonArray = result.getJSONArray("data")
                for (Object object : jsonArray) {
                    JSONObject jsonObject = new JSONObject(object.toString())
                    if (Objects.equals(Dict.findByCode("CURRENT_COURT").ext3, jsonObject.getString("code"))) {
                        JSONArray urlList = jsonObject.getJSONArray("url")
                        if (urlList.size() != 0) {
                            this.CENTRAL_URL_LIST.clear()
                            for (Object url : urlList) {
                                this.CENTRAL_URL_LIST.add(url.toString())
                            }
                            success = true
                        }
                    }
                }
            }
            log.info("根据处理urllist决定是否写入文件upload  ：{}。", success)
            if (success) {
                try {
                    String filePath = PushUtil.class.getProtectionDomain().getCodeSource().getLocation().getFile()
                    filePath = URLDecoder.decode(filePath, "UTF-8") // 转换处理中文及空格
                    log.info(filePath)
                    File file = new File(filePath)
                    log.info(file.getParent() + "//upload.json")
                    File upload = new File(file.getParent() + "//upload.json")
                    if (!upload.exists()) {
                        boolean su = upload.createNewFile()
                        log.info("创建文件upload.json是否成功：{}", su)
                    }
                    OutputStream out = new FileOutputStream(upload)
                    out.write(resp.getResponseText().getBytes())
                    out.flush()
                    out.close()
                    return true
                } catch (Exception e) {
                    log.error("写入upload.json文件时出错，url可能会产生丢失！错误信息：{}", e.getMessage())
                }
            }
        }
        return false
    }

    private static boolean handleUrlList(String str) {
        JSONObject result = new JSONObject(str)
        int code = (int) result.get("code")
        if (code != 0) {
            return false
        }
        log.info("准备处理str字符串，法院code：{}。", Dict.findByCode("CURRENT_COURT").ext3)
        try {
            JSONArray jsonArray = result.getJSONArray("data")
            for (Object object : jsonArray) {
                JSONObject jsonObject = new JSONObject(object.toString())
                if (Objects.equals(Dict.findByCode("CURRENT_COURT").ext3, jsonObject.getString("code"))) {
                    JSONArray urlList = jsonObject.getJSONArray("url")
                    if (urlList.size() == 0) {
                        return false
                    }
                    this.CENTRAL_URL_LIST.clear()
                    for (Object url : urlList) {
                        this.CENTRAL_URL_LIST.add(url.toString())
                    }
                    log.info("处理url——list成功。")
                    return true
                }
            }
            log.error("处理url——list失败,可能是数据列表中不包含code的信息。")
            return false
        } catch (Exception e) {
            log.error("处理url——list失败，{}", e.getMessage())
            return false
        }
    }

    static void submit(final String url, final Object obj) {
        try{
            log.info("push请求接收，将会进行提交本次数据。url：{} ， 数据：{}", url, obj)
            for (final String centralUrl : this.CENTRAL_URL_LIST) {
                PushLog.withNewSession {
                    //推送日志
                    def pushLog = new PushLog(
                            addressIp: centralUrl,
                            data: (obj as JSON) as String,
                            isSuccess: false,
                            url: url
                    )
                    pushLog.save(flush: true)
                    if (pushLog.hasErrors()){
                        log.error("[PushUtil submit] 添加推送日志失败，失败信息：{}", pushLog.errors)
                    }
                    log.info("插入数据日志操作准备完毕，key：" + pushLog.id)
                    pushData(pushLog.id, centralUrl, url, obj)
                }

            }
        }catch(e){
            log.error("cmp推送push失败，错误信息为: ${e.getMessage()}")
        }
    }

    static int pushData(final long key, final String centralUrl, final String url, final Object obj) {
        //正式推送
        HttpClientResponse response = SyncUtils.sync(centralUrl + url, RequestMethod.POST, obj)
        println("${centralUrl + url}推送数据响应码：" + response.getCode())
        if (response.getCode() != 200) {
            log.error("上传同步数据失败,url:{},服务器错误代码:{},数据:{},错误信息：{}", centralUrl + url, response.getCode(), obj.toString(), response.getResponseText())
        } else {
            log.info("上传同步数据成功，现在准备判断服务器返回的响应码")
            JSONObject result = new JSONObject(response.getResponseText())
            int code = result.code
            //code：0---->同步成功。
            if (code == PUSH_CODE.SUCCESS.CODE()) {
                log.info(PUSH_CODE.SUCCESS.MESSAGE())
                def pushLog = PushLog.get(key)
                pushLog.isSuccess = true
                pushLog.save(flush: true)
                if (pushLog.hasErrors()){
                    log.error("[PushUtil updatePushLog] 修改推送日志失败，失败信息：{}", pushLog.errors)
                }
                return 0
            }
            //code：1---->提交失败，本次提交在任务中重试提交。
            if (code == PUSH_CODE.NORMAL_FAILED.CODE()) {
                log.error(PUSH_CODE.NORMAL_FAILED.MESSAGE())
            }
            //code：4010---->缺少法庭信息,需要重新查找法庭信息补充提交。
            if (code == PUSH_CODE.COURTROOM_FAILED.CODE()) {
                log.error(PUSH_CODE.COURTROOM_FAILED.MESSAGE())
                //数据补偿
                List<JSONObject> data4010 = obj as List<JSONObject>
                for (JSONObject json : data4010) {
                    String courtroomUid = json.courtRoomUid
                    if (StringUtils.isEmpty(courtroomUid)) {
                        log.error("未找到法庭,错误补偿失败.")
                    } else {
                        int temp = codePostCourtroom(courtroomUid, centralUrl)
                        if (temp == 0) {
                            log.info("补偿法庭成功,现在重试本次的请求.")
                            return pushData(key, centralUrl, url, obj)
                        } else {
                            log.error("补偿法庭失败.")
                        }
                    }
                }
            }
            //code：4020---->缺少案件信息,需要重新查找案件信息补充提交。
            if (code == PUSH_CODE.CASE_FAILED.CODE()) {
                log.error(PUSH_CODE.CASE_FAILED.MESSAGE())
                //数据补偿
                List<JSONObject> data4020 = obj as List<JSONObject>
                for (JSONObject json : data4020) {
                    String caseUid = json.getString("caseUid")
                    if (StringUtils.isEmpty(caseUid)) {
                        log.error("未找到案件,错误补偿失败.")
                    } else {
                        int temp = codePostCase(caseUid, centralUrl)
                        if (temp == 0) {
                            log.info("补偿案件成功,现在重试本次的请求.")
                            return pushData(key, centralUrl, url, obj)
                        } else {
                            log.error("补偿案件失败.")
                        }
                    }
                }
            }
//            //code：4030
            if (code == PUSH_CODE.PLAN_FAILED.CODE()) {
                log.error(PUSH_CODE.PLAN_FAILED.MESSAGE())
                //数据补偿
                List<JSONObject> data4030 = obj as List<JSONObject>
                for (JSONObject json : data4030) {
                    String planUid = json.getString("planUid")
                    if (StringUtils.isEmpty(planUid)) {
                        log.error("未找到排期,错误补偿失败.")
                    } else {
                        int temp = codePostPlan(planUid, centralUrl)
                        if (temp == 0) {
                            log.info("补偿排期成功,现在重试本次的请求.")
                            return pushData(key, centralUrl, url, obj)
                        } else {
                            log.error("补偿排期失败.")
                        }
                    }
                }
            }
//            //code：4040
//            if (code == PUSH_CODE.TRIAL_FAILED.CODE()) {
//                log.error(PUSH_CODE.TRIAL_FAILED.MESSAGE())
//                //数据补偿
//                List<JSONObject> data4040 = obj as List<JSONObject>
//                for (JSONObject json : data4040) {
//                    String trialUid = json.getString("trialUid")
//                    if (StringUtils.isEmpty(trialUid)) {
//                        log.error("未找到排期,错误补偿失败.")
//                    } else {
//                        int temp = codePostTrial(trialUid, centralUrl)
//                        if (temp == 0) {
//                            log.info("补偿开庭记录成功,现在重试本次的请求.")
//                            return pushData(key, centralUrl, url, obj)
//                        } else {
//                            log.error("补偿开庭记录失败.")
//                        }
//                    }
//                }
//            }
//            //code：4050
            if (code == PUSH_CODE.VIDEO_FAILED.CODE()) {
                log.error(PUSH_CODE.VIDEO_FAILED.MESSAGE())
                log.info("code 缺少视频信息,大概是服务器傻掉了.")
            }
            //code：4060---->缺少法官信息,需要重新查找法官补充提交。
            if (code == PUSH_CODE.JUDGE_FAILED.CODE()) {
                log.error(PUSH_CODE.JUDGE_FAILED.MESSAGE())
                //数据补偿
                List<JSONObject> data4060 = obj as List<JSONObject>
                for (JSONObject json : data4060) {
                    String judgeUserUid = json.getString("judgeUserUid")
                    if (StringUtils.isEmpty(judgeUserUid)) {
                        log.error("未找到法官,错误补偿失败.")
                    } else {
                        int temp = codePostUser(judgeUserUid, centralUrl)
                        if (temp == 0) {
                            log.info("补偿法官成功,现在重试本次的请求.")
                            return pushData(key, centralUrl, url, obj)
                        } else {
                            log.error("补偿法官失败.")
                            throw new RuntimeException("补偿法官失败.")
                        }
                    }
                }
            }
            //code：4070----.缺少书记员信息,需要重新查找书记员补充提交。
            if (code == PUSH_CODE.SECRETARY_FAILED.CODE()) {
                log.error(PUSH_CODE.SECRETARY_FAILED.MESSAGE())
                //数据补偿
                List<JSONObject> data4070 = obj as List<JSONObject>
                for (JSONObject json : data4070) {
                    String secretaryUserUid = json.getString("secretaryUserUid")
                    if (StringUtils.isEmpty(secretaryUserUid)) {
                        log.error("未找到书记员,错误补偿失败.")
                    } else {
                        int temp = codePostUser(secretaryUserUid, centralUrl)
                        if (temp == 0) {
                            log.info("补偿书记员成功,现在重试本次的请求.")
                            return pushData(key, centralUrl, url, obj)
                        } else {
                            log.error("补偿书记员失败.")
                        }
                    }
                }
            }
        }
        return 1
    }

    private static int codePostCourtroom(String courtroomUid, final String centralUrl) {
        def courtroom = Courtroom.findByUid(courtroomUid)
        log.info("法庭信息查找成功,courtroom: {}", courtroom.toString())
        List<JSONObject> syncCourtroom = new ArrayList<>()
        final JSONObject object = new JSONObject()
        object.put("uid", courtroom.uid)
        object.put("courtCode", Dict.findByCode("CURRENT_COURT").ext3)
        object.put("courtName", Dict.findByCode("CURRENT_COURT").ext1)
        object.put("name", courtroom.name)
        object.put("deviceIp", courtroom.deviceIp)
        object.put("storeIp", courtroom.storeIp)
        object.put("ftpUser", "FTP2011")
        object.put("ftpPsw", "HXHT")
        object.put("ftpPort", "21")
        object.put("cfg", courtroom.cfg)
        object.put("deviceType", 4)
        object.put("mode", 0)
        object.put("remote", courtroom.remote)
        object.put("storeSwitch", 1)
        object.put("storeFashion", 1)
        object.put("status", 1)
        object.put("active", 1)
        syncCourtroom.add(object)
        def pushLog = new PushLog(
                addressIp: centralUrl,
                data: (syncCourtroom as JSON) as String,
                isSuccess: false,
                url: PushHandler.pushCourtroom
        )
        pushLog.save(flush: true)
        if (pushLog.hasErrors()){
            log.error("[PushUtil submit] 添加推送日志失败，失败信息：{}", pushLog.errors)
        }
        return pushData(pushLog.id, centralUrl, PushHandler.pushCourtroom, syncCourtroom)
    }

    private static int codePostCase(String uid, final String centralUrl) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        def caseInfo = CaseInfo.findByUid(uid)
        if (caseInfo) {
            log.info("案件信息查找成功,case: " + caseInfo.toString())
            List<JSONObject> syncCase = new ArrayList<>()
            JSONObject temp = new JSONObject()
            temp.put("uid", caseInfo.uid)
            temp.put("courtCode", Dict.findByCode("CURRENT_COURT").ext3)
            temp.put("archives", caseInfo.archives)
            temp.put("name", caseInfo.name)
            temp.put("typeId", caseInfo.type.code)
            temp.put("detail", caseInfo.detail)
            temp.put("accuse", caseInfo.accuser)
            temp.put("accuseLawer", caseInfo.prosecutionCounsel)
            temp.put("accused", caseInfo.accused)
            temp.put("accusedLawer", caseInfo.counselDefence)
            temp.put("party", null)
            if (caseInfo.filingDate){
                temp.put("filingDate", simpleDateFormat.format(caseInfo.filingDate))
            }
            temp.put("status", caseInfo.active)
            temp.put("summary", caseInfo.summary)
            temp.put("active", 1)
            syncCase.add(temp)
            def pushLog = new PushLog(
                    addressIp: centralUrl,
                    data: (syncCase as JSON) as String,
                    isSuccess: false,
                    url: PushHandler.pushCase
            )
            pushLog.save(flush: true)
            if (pushLog.hasErrors()){
                log.error("[PushUtil submit] 添加推送日志失败，失败信息：{}", pushLog.errors)
            }
            return pushData(pushLog.id, centralUrl, PushHandler.pushCase, syncCase)
        }
        return 1
    }

    private static int codePostPlan(String uid, final String centralUrl) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        def plan = PlanInfo.findByUid(uid)
        List<JSONObject> syncPlan = new ArrayList<>()
        if (plan) {
            log.info("排期信息查找成功,plan: " + plan.toString())
            JSONObject temp = new JSONObject()
            temp.put("uid", plan.uid)
            temp.put("courtCode", Dict.findByCode("CURRENT_COURT").ext3)
            temp.put("courtRoomUid", plan.courtroom.uid)
            temp.put("caseUid", plan.caseInfo.uid)
            temp.put("judgeUserUid", plan.judge.uid)
            temp.put("secretaryUserUid", plan.secretary.uid)
            temp.put("startDate", simpleDateFormat.format(plan.startDate))
            if (plan.getEndDate()) {
                temp.put("endDate", simpleDateFormat.format(plan.endDate))
            }
            temp.put("status", plan.status)
            temp.put("allowPlay", plan.allowPlay)
            temp.put("active", 1)
            syncPlan.add(temp)
            def pushLog = new PushLog(
                    addressIp: centralUrl,
                    data: (syncPlan as JSON) as String,
                    isSuccess: false,
                    url: PushHandler.pushPlan
            )
            pushLog.save(flush: true)
            if (pushLog.hasErrors()){
                log.error("[PushUtil submit] 添加推送日志失败，失败信息：{}", pushLog.errors)
            }
            return pushData(pushLog.id, centralUrl, PushHandler.pushPlan, syncPlan)
        }
        return 1
    }

//    private static int codePostTrial(String uid, final String centralUrl) {
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//        String sql = "select * from `trial` where uid ='" + uid + "'"
//        List<Trial> trials = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Trial.class))
//        List<JSONObject> syncTrial = new ArrayList<>()
//        if (!ObjectUtils.isEmpty(trials)) {
//            log.info("开庭记录查找成功,trial: " + trials.get(0).toString())
//            Trial trial = trials.get(0)
//            JSONObject temp = new JSONObject()
//            temp.put("uid", trial.getUid())
//            temp.put("courtCode", Dict.findByCode("CURRENT_COURT").ext3)
//            temp.put("planUid", trial.getPlanId())
//            temp.put("judgeUserUid", trial.getJudgeId())
//            temp.put("secretaryUserUid", trial.getSecretaryId())
//            if (!ObjectUtils.isEmpty(trial.getStartDate())) {
//                temp.put("startDate", simpleDateFormat.format(trial.getStartDate()))
//            }
//            if (!ObjectUtils.isEmpty(trial.getRestDate())) {
//                temp.put("restDate", simpleDateFormat.format(trial.getRestDate()))
//            }
//            temp.put("endDate", simpleDateFormat.format(trial.getEndDate()))
//            if (!ObjectUtils.isEmpty(trial.getArchDate())) {
//                temp.put("archDate", simpleDateFormat.format(trial.getArchDate()))
//            }
//            temp.put("courtrec", trial.getCourtrec())
//            temp.put("status", trial.getJudgeProcess())
//            temp.put("active", "1")
//            syncTrial.add(temp)
//            long key = insertPushLog(centralUrl, PushHandler.pushTrial, syncTrial)
//            return pushData(key, centralUrl, PushHandler.pushTrial, syncTrial)
//        }
//        return 1
//    }

    private static int codePostUser(String uid, final String centralUrl) {
        def employee = Employee.findByUid(uid)
        def userLocal = User.findByEmployee(employee.id)
        if (!userLocal){
            log.error("本地用户User查询不到，请添加用户后重试，现在自动添加用户")
            def user = new User()
            user.uid = UUIDGenerator.nextUUID()
            user.enabled = true //账号启用
            user.accountExpired = false //账号过期
            user.accountLocked = false//账号锁定
            user.username = PinYinUtils.getHanziPinYin(employee.name)//账号
            user.password = '123456'//密码
            user.realName = employee.name//姓名
            user.employee = employee.id
            user.save(flush: true)
            userLocal = user
        }
        Users user = new Users()
        user.setUid(employee.uid)
        user.setUserid(userLocal.username)
        user.setUsername(userLocal.realName)
        user.setInterfaceId(employee.synchronizationId)
        user.setRegtime(userLocal.dateCreated)
        if (employee && userLocal) {
            log.info("查找用户成功,user: " + user.toString())
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            List<JSONObject> syncUser = new ArrayList<>()
            JSONObject object = new JSONObject()
            object.put("uid", user.getUid())
            object.put("courtCode", Dict.findByCode("CURRENT_COURT").ext3)
            object.put("username", user.getUserid())
            object.put("password", user.getPassword())
            object.put("realName", user.getUsername())
            object.put("rongjiUid", user.getInterfaceId())
            if (user.getRegtime()) {
                object.put("regTime", simpleDateFormat.format(user.getRegtime()))
            }
            object.put("accountLocked", true)
            syncUser.add(object)
            def pushLog = new PushLog(
                    addressIp: centralUrl,
                    data: (syncUser as JSON) as String,
                    isSuccess: false,
                    url: PushHandler.pushUser
            )
            pushLog.save(flush: true)
            if (pushLog.hasErrors()){
                log.error("[PushUtil codePostUser] 添加推送日志失败，失败信息：{}", pushLog.errors)
            }
            return pushData(pushLog.id, centralUrl, PushHandler.pushUser, syncUser)
        }
        return 1
    }
}
