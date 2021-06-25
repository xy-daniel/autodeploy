package com.hxht.autodeploy.court.manager

import cn.hutool.core.io.FileUtil
import com.hxht.techcrt.Dict
import com.hxht.techcrt.Resp
import com.hxht.techcrt.SecretaryVersion
import com.hxht.techcrt.api.ApiController
import com.hxht.techcrt.enums.RespType
import com.hxht.techcrt.utils.ZipUtil
import groovy.json.JsonSlurper
import org.springframework.web.multipart.MultipartHttpServletRequest


/**
 * 书记员客户端配置上传
 */
class ClerkClientController {

    ClerkClientService clerkClientService
    /**
     * 书记员客户端各法庭版本列表
     */
    def list() {
        def tcVersion = grailsApplication.config.getProperty('tc.version')
        def secretaryVersion = SecretaryVersion.findAll()
        def system_config = Dict.findByCode("SYSTEM_CONFIG")
        def sjyLastVersion = system_config.ext1
        def fgLastVersion = system_config.ext2
        def dsrLastVersion = system_config.ext3
        [
                tcVersion       : tcVersion,
                secretaryVersion: secretaryVersion,
                sjyLastVersion  : sjyLastVersion == null ? "尚未上传更新包" : sjyLastVersion,
                fgLastVersion   : fgLastVersion == null ? "尚未上传更新包" : fgLastVersion,
                dsrLastVersion  : dsrLastVersion == null ? "尚未上传更新包" : dsrLastVersion
        ]
    }
    
    /**
     * 上传用于书记员更新包
     */
    def uploadUpdatePackage() {
        if (request.method == "POST") {
            try {
                if (request instanceof MultipartHttpServletRequest) {
                    def file = request.getFile("file")
                    if (!file) {
                        render Resp.toJson(RespType.FAIL)
                        return
                    }
                    /****判断文件夹是否存在不存在则新建***/
                    def fileBase = new File(ApiController.UPDATE_PACKAGE_STORE_BASE_ADDRESS)
                    def fileSjy = new File(ApiController.SJY_UPDATE_PACKAGE_STORE_BASE_ADDRESS)
                    def fileFg = new File(ApiController.FG_UPDATE_PACKAGE_STORE_BASE_ADDRESS)
                    def fileDsr = new File(ApiController.DSR_UPDATE_PACKAGE_STORE_BASE_ADDRESS)
                    if (!fileBase.exists()){
                        fileBase.mkdirs()
                        fileBase.setWritable(true,false)
                        fileBase.setReadable(true,false)
                    }
                    if (!fileSjy.exists()){
                        fileSjy.mkdirs()
                        fileSjy.setWritable(true,false)
                        fileSjy.setReadable(true,false)
                    }
                    if (!fileFg.exists()){
                        fileFg.mkdirs()
                        fileFg.setWritable(true,false)
                        fileFg.setReadable(true,false)
                    }
                    if (!fileDsr.exists()){
                        fileDsr.mkdirs()
                        fileDsr.setWritable(true,false)
                        fileDsr.setReadable(true,false)
                    }
                    //更新包上传路径
                    def path = ApiController.UPDATE_PACKAGE_STORE_BASE_ADDRESS
                    //获取文件名
                    def fileName = file.getOriginalFilename()
                    //文件后缀名
                    def fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1)
                    //文件后缀名不是zip上传失败
                    if (fileSuffix != "zip") {
                        render Resp.toJson(RespType.FAIL)
                        return
                    }
                    def sfile = new File(path + fileName)
                    //转移到临时文件夹
                    file.transferTo(sfile)
                    //解压缩
                    ZipUtil.decompression(path + fileName, path)
                    //解析version.txt
                    def versionTxt = new File(path + fileName.substring(0, fileName.lastIndexOf(".")) + File.separator + "version.txt")
                    def txtContext = new JsonSlurper().parseText(versionTxt.text)
                    def systemConfig = Dict.findByCode("SYSTEM_CONFIG")
                    def type = txtContext.type as String
                    if (!type) {
                        systemConfig.ext1 = txtContext.version + "/" + fileName
                        //转移
                        FileUtil.copy(sfile, new File(ApiController.SJY_UPDATE_PACKAGE_STORE_BASE_ADDRESS + fileName), true)
                    } else {
                        if (type == "sjy") {
                            systemConfig.ext1 = txtContext.version + "/" + fileName
                            FileUtil.copy(sfile, new File(ApiController.SJY_UPDATE_PACKAGE_STORE_BASE_ADDRESS + fileName), true)
                        }
                        if (type == "fg") {
                            systemConfig.ext2 = txtContext.version + "/" + fileName
                            FileUtil.copy(sfile, new File(ApiController.FG_UPDATE_PACKAGE_STORE_BASE_ADDRESS + fileName), true)
                        }
                        if (type == "dsr") {
                            systemConfig.ext3 = txtContext.version + "/" + fileName
                            FileUtil.copy(sfile, new File(ApiController.DSR_UPDATE_PACKAGE_STORE_BASE_ADDRESS + fileName), true)
                        }
                    }
                    clerkClientService.saveDict(systemConfig)
                    render Resp.toJson(RespType.SUCCESS)
                } else {
                    log.error("[ToolBoxController.uploadUpdatePackage] 上传书记员软件更新包出错")
                    render Resp.toJson(RespType.FAIL)
                }
            } catch (e) {
                e.printStackTrace()
                log.error("[ToolBoxController.uploadUpdatePackage] 上传书记员软件更新包出错")
                render Resp.toJson(RespType.FAIL)
            }
        }
    }
    
}
