package com.hxht.autodeploy.court.manager

import com.hxht.techcrt.Dict
import com.hxht.techcrt.ModelType
import com.hxht.techcrt.Resp
import com.hxht.techcrt.enums.RespType
import com.hxht.techcrt.utils.IpUtil
import org.springframework.web.multipart.MultipartHttpServletRequest

/**
 * 系统配置
 * 2021.03.26 >>> 修改获取服务器ip的方法 daniel
 * 2021.04.26 >>> 分离羁押室语音模板设置和法庭报警音频设置 daniel
 * 2021.06.16 >>> 互联网开庭统计 daniel
 */
class SystemController {

    SystemService systemService
    //获取当前法院配置信息
    public static Dict currentCourt

    /**
     * 系统名称及是否允许互联网开庭配置页面
     * @return Gsp Page
     */
    def edit() {
        def isInternet = Dict.findByCode("IS_INTERNET")
        if (!isInternet) {
            isInternet = new Dict(
                    name: "是否开启互联网开庭功能",
                    notes: "是否开启互联网开庭功能",
                    code: "IS_INTERNET",
                    val: ModelType.LOCALE as String,
                    type: "String",
                    parent: Dict.findByCode("SYSTEM_CONFIG")
            ).save(flush: true)
        }
        [
                court        : Dict.findByCode("CURRENT_COURT"),
                allowInternet: isInternet.val
        ]
    }

    /**
     * 保存修改后的系统名称及是否允许互联网开庭
     * @return Resp.toJson(RespType.SUCCESS)
     */
    def editSave() {
        def systemTitle = params.get("systemTitle")
        def allowInternet = params.get("allowInternet") as String
        if (!systemTitle) {
            log.error("[SystemController.editSave] 系统名称不存在.")
            render Resp.toJson(RespType.PARAMETER_NULL)
            return
        }
        systemService.currentCourtEditSave(systemTitle as String)
        systemService.isInternetEditSave(allowInternet)
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 语音传唤编辑页面
     */
    def voiceEdit() {
        def callModel = Dict.findByCode("CALL_MODEL")
        if (!callModel) {
            new Dict(
                    name: "语音传唤模板",
                    notes: "语音传唤模板",
                    code: "CALL_MODEL",
                    type: "String",
                    val: "请{user}到{courtroom}"
            ).save(flush: true)
            callModel = Dict.findByCode("CALL_MODEL")
        }
        [callModel: callModel]
    }

    /**
     * 报警音频编辑页面
     */
    def warnEdit() {
        def audioFile = Dict.findByCode("AUDIO_FILE")
        if (!audioFile) {
            audioFile = new Dict(
                    name: "法警室警报音频",
                    notes: "法警室警报音频",
                    code: "AUDIO_FILE",
                    type: "String",
                    val: "示例:http://127.0.0.1:8200/***.mp3"
            )
            audioFile.save(flush: true)
        }
        [audioUrl: audioFile.val]
    }


    /**
     * 语音传唤模板修改
     */
    def voiceEditSave() {
        def callModel = Dict.findByCode("CALL_MODEL")
        callModel.val = params.get("callModel")
        callModel.save(flush: true)
        if (callModel.hasErrors()) {
            log.error("[SystemController.voiceEditSave] 保存语音传唤模板时失败,错误信息:\n${callModel.errors}")
            render Resp.toJson(RespType.FAIL)
        }
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 报警音频修改
     */
    def warnEditSave() {
        if (request instanceof MultipartHttpServletRequest) {
            def audioFile = request.getFile("audioFile")
            def fileName = audioFile.getOriginalFilename()
            if (fileName.substring(fileName.lastIndexOf(".") + 1) != "mp3") {
                log.info("[SystemController.editSave] 上传的音频文件格式不正确")
            } else {
                //文件上传目录
                def file = new File(File.separator + "usr" + File.separator + "local" + File.separator + "movies" +
                        File.separator + fileName)
                audioFile.transferTo(file)
                log.info("[SystemController.editSave] 上传音频文件成功")
                def audioFileDict = Dict.findByCode("AUDIO_FILE")
                audioFileDict.val = "http://${IpUtil.serviceIp}:8200/${fileName}"
                audioFileDict.save(flush: true)
                log.info("[SystemController.editSave] 保存音频文件路径成功")
            }
        }
        render Resp.toJson(RespType.SUCCESS)
    }
}
