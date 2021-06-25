package com.hxht.autodeploy.api

import com.hxht.techcrt.DataStatus
import com.hxht.techcrt.court.CaseInfo
import com.hxht.techcrt.court.PlanInfo
import com.hxht.techcrt.court.PlanTrial
import com.hxht.techcrt.court.TrialInfo
import com.hxht.techcrt.court.plan.PlanService
import com.hxht.techcrt.util.http.HttpClientJsonRequest
import com.hxht.techcrt.util.http.HttpClientResponse
import com.hxht.techcrt.util.http.HttpClientUtil
import com.hxht.techcrt.utils.FileUtil
import grails.converters.JSON
import grails.core.GrailsApplication
import grails.gorm.transactions.Transactional
import org.apache.commons.io.FileUtils
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject
import org.springframework.util.ObjectUtils
import org.springframework.web.bind.annotation.RequestMethod
import com.hxht.techcrt.util.word2pdfUtil

@Transactional
class ApiTaiChiService {
    GrailsApplication grailsApplication
    PlanService planService
    /**
     * 根据庭审获取页面信息
     * @param trialInfo
     * @return
     */
    def showVideo(TrialInfo trialInfo) {
        planService.showVideo(trialInfo)
        /*def planInfo = trialInfo.planInfo
        def courtroom = planInfo.courtroom
        def cfg = JSON.parse(courtroom.cfg)
        def videoList = [] //最后返回结果集
        //根据通道号和Trial 获取视频
        log.info("[apiTaiChiService.showVideo]---->庭审已经休庭或闭庭中，返回点播视频地址")
        //根据trial查询出所有的videoInfo
        def chnList = []
        def videoInfoListForNum = VideoInfo.findAllByTrialInfo(trialInfo)
        //遍历所有的videoInfo显示所有这个庭审的通道号和通道名称
        for (def video:videoInfoListForNum){
            chnList.add([
                    number : video.channelNum,
                    name: video.channelName
            ])
        }
        //s使用hashset去掉重复
        def set = new HashSet<>(chnList);
        chnList = new LinkedList<>(set);
        def url = [:]
        for (def encode : chnList) {
            def videoMap = [:]
            videoMap.put("number", encode.number)
            videoMap.put("name", encode.name)
            def videoInfoList = VideoInfo.findAllByTrialInfoAndChannelNumAndChannelName(trialInfo, encode.number as String,  encode.name as String, [sort: "startRecTime", order: "asc"])
            def trialVideoList = []
            def totalLength = 0
            for (def i = 0; i < videoInfoList.size(); i++) {
                def videoInfo = videoInfoList[i]
                trialVideoList.add([
                        serial      : i,
                        length      : videoInfo.length,
                        startRecTime: videoInfo.startRecTime?.format("HH:mm"),
                        imagesUrl   : grailsApplication.config.getProperty('tc.trial.images.path'),
                        url         : "http://${courtroom.storeIp}:8200/${videoInfo.fileName}"
                ])
                totalLength += videoInfo.length
            }
            videoMap.put("videoUrl", trialVideoList)
            videoMap.put("totalLength", totalLength)

            videoList.add(videoMap)
        }
        videoList*/
    }

    /**
     * 根据排期获取庭审与视频信息
     * @param planInfo
     */
    def getTrialVideoList(PlanInfo planInfo) {
        List<TrialInfo> trialInfoList = []
        if (planInfo.combinedPlan) {//存在并案的排期
            def planTrialList = PlanTrial.findAllByPlanInfo(planInfo)//获取所有排期对应的庭审
            for (PlanTrial planTrial : planTrialList) { //将庭审放入到集合
                trialInfoList.add(planTrial.trialInfo)
            }
        } else {//不存在并案的情况
            trialInfoList = TrialInfo.findAllByPlanInfoAndActive(planInfo, DataStatus.SHOW, [sort: "status", order: "asc"])
        }
        def trialList = []
        for (def ti : trialInfoList) {
            //获取庭审所有视频
            def videoInfoList = ti.videoInfo
            def videoList = []
            for (def videoInfo : videoInfoList) {
                videoList.add([
                        name: videoInfo.channelName,
                ])
            }
            trialList.add([
                    id       : ti.id,
                    startDate: ti.startDate?.format('yyyy/MM/dd HH:mm'),
                    endDate  : ti.endDate?.format('yyyy/MM/dd HH:mm'),
                    collegial: planInfo.collegial,
                    status   : ti.status,
                    videoList: videoList
            ])
        }
        trialList
    }

    /**
     * 请求太极的电子卷宗功能
     * [{"name":"文件名称", // 庭审笔录.doc或庭审笔录.pdf
     * "content":"base64文件", // 文件的base64编码
     * "ah":"案号", // 与综合业务系统案号相同
     * "fileNo":"文件编号",// 数字法庭文件编号，须保证其系统内唯一
     * "documentType":"文档类型", //默认填写"1"
     * "source":"文件来源"} ] // 默认填写"szft"
     *
     * @return
     */
    def taichiDossier(Long  trialInfoId) {
        //深圳中院用的对接太极接口 目前只有深圳中院在用
        if (grailsApplication.config.getProperty("tc.deployPlace") == "shenzhen") {
            def trialInfo = TrialInfo.get(trialInfoId)
            def planInfo = trialInfo.planInfo
            def caseInfo = planInfo.caseInfo
            try {
                log.info("进入太极taichiDossier接口---------------")
                if (!trialInfo){
                    log.info("对接太极接口未找到庭审记录并返回！")
                    return false
                }
                if (!caseInfo){
                    log.info("对接太极接口未找到案件信息并返回！")
                    return false
                }
                //没有pdf文件情况下生成PDF文件
                def notePath = trialInfo.note
                def pdfPath
                def path = grailsApplication.config.getProperty('tc.trial.note.path')
                if (!notePath.endsWith(".pdf")){
                    log.info("开始生成pdf文件 路径：${path + trialInfo.note} 当前时间" + new Date())
                    pdfPath = path + notePath.substring(0,notePath.lastIndexOf(".")) + ".pdf"
                    word2pdfUtil.word2pdf(path + notePath, pdfPath)
                    log.info("生成pdf文件完成 当前时间" + new Date())
                }
                //确认存在文件 构造request param
                HttpClientJsonRequest request = this.letParam(planInfo, caseInfo)
                if (!ObjectUtils.isEmpty(request)) {
                    HttpClientResponse response = HttpClientUtil.post(request)
                    if (response.getCode() == 200) {
                        def result = JSON.parse(response.getResponseText())
                        if (result.success == true) {
                            if (!notePath.endsWith(".pdf")){
                                log.info("请求成功后删除生成的pdf文件！")
                                def pdfPathFile = new File(pdfPath)
                                FileUtils.deleteQuietly(pdfPathFile)
                            }
                            log.info("对接太极接口归档 请求成功并返回！")
                            return true
                        }
                    }
                    log.error("对接太极接口失败！响应码为 ${response.getCode()}，响应错误信息为：${response.getResponseText()}")
                    return false
                }
                log.error("请求参数未能创建成功！请查看日志。")
                return false
            } catch (Exception e) {
                log.error("太极接口对接出错，错误信息为: ${e.getMessage()}")
                return false
            }
        }else{
            return true
        }
    }

    def letParam(PlanInfo planInfo, CaseInfo caseInfo) {
        def fileNo = planInfo.id
        def ah = caseInfo.archives
        def TAICHI_NOTE = grailsApplication.config.getProperty('taichi.url')
        HttpClientJsonRequest request = new HttpClientJsonRequest(TAICHI_NOTE, RequestMethod.POST)
        request.addHeader("Content-Type", "application/json;charset=utf-8")
        def path = grailsApplication.config.getProperty('tc.trial.note.path')
        String base = path + fileNo
        log.info("要查询的目录:"+base)
        File base_file = new File(base)
        //获取pdf
        File[] files_pdf=base_file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if(pathname.getAbsolutePath().endsWith(".pdf")){
                    return true
                }
                return false
            }
        })
        //获取word
        File[] files_word=base_file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if(pathname.getAbsolutePath().endsWith(".doc")){
                    return true
                }
                return false
            }
        })
        //获取word
        File[] files_word2007=base_file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if(pathname.getAbsolutePath().endsWith(".docx")){
                    return true
                }
                return false
            }
        })
        //获取所有的图片
        File[] files_img=base_file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if(pathname.getAbsolutePath().endsWith(".jepg")){
                    return true
                }
                return false
            }
        })
        def jsonArray = new JSONArray()
        try {
            //封装pdf
            for(File file_pdf:files_pdf){
                log.info("pdf="+file_pdf.getAbsolutePath())
                def jsonObject_pdf = new JSONObject()
                jsonObject_pdf.put("name", ah+".pdf")
                jsonObject_pdf.put("content", FileUtil.encodeBase64File(file_pdf.getAbsolutePath()))
                jsonObject_pdf.put("ah", ah)
                jsonObject_pdf.put("fileNo", fileNo)
                jsonObject_pdf.put("documentType", "1")
                jsonObject_pdf.put("source", "szft")
                jsonObject_pdf.put("appid", 'hxht')
                jsonArray.add(jsonObject_pdf)
            }
            //封装word
            for(File file_word:files_word){
                log.info("word2007版本以下="+file_word.getAbsolutePath())
                def jsonObject_pdf = new JSONObject()
                jsonObject_pdf.put("name", ah+".doc")
                jsonObject_pdf.put("content", FileUtil.encodeBase64File(file_word.getAbsolutePath()))
                jsonObject_pdf.put("ah", ah)
                jsonObject_pdf.put("fileNo", fileNo)
                jsonObject_pdf.put("documentType", "1")
                jsonObject_pdf.put("source", "szft")
                jsonObject_pdf.put("appid", 'hxht')
                jsonArray.add(jsonObject_pdf)
            }
            //封装word2007版本以上
            for(File file_word2007:files_word2007){
                log.info("word2007版本以上="+file_word2007.getAbsolutePath())
                def jsonObject_pdf = new JSONObject()
                jsonObject_pdf.put("name", ah+".docx")
                jsonObject_pdf.put("content", FileUtil.encodeBase64File(file_word2007.getAbsolutePath()))
                jsonObject_pdf.put("ah", ah)
                jsonObject_pdf.put("fileNo", fileNo)
                jsonObject_pdf.put("documentType", "1")
                jsonObject_pdf.put("source", "szft")
                jsonObject_pdf.put("appid", 'hxht')
                jsonArray.add(jsonObject_pdf)
            }

            //封装jepg
            for(File file_img:files_img){
                log.info("img="+file_img.getAbsolutePath())
                def jsonObject_img = new JSONObject()
                jsonObject_img.put("name", file_img.getName())
                jsonObject_img.put("content", FileUtil.encodeBase64File(file_img.getAbsolutePath()))
                jsonObject_img.put("ah", ah)
                jsonObject_img.put("fileNo", fileNo)
                jsonObject_img.put("documentType", "2")// 证据
                jsonObject_img.put("source", "szft")
                jsonObject_img.put("appid", "hxht")
                jsonArray.add(jsonObject_img)
            }
            request.setJsonObject(jsonArray)
//            log.info("请求参数:{}",jsonArray.toString())
            log.info("ready to request {},", request.getEntity())
            return request
        } catch (Exception e) {
            log.error("param create error ,error message :{}", e.getMessage())
            e.printStackTrace()
            return null
        }
    }
}
