package com.hxht.autodeploy.mem

import com.hxht.techcrt.court.mem.Signature
import com.hxht.techcrt.utils.Base64ImgUtl
import grails.gorm.transactions.Transactional
import org.springframework.web.multipart.MultipartFile

@Transactional("mem")
class SignatureService {

    def findById(Long id) {
        return Signature.get(id)
    }

    def findAllSignatureByPlanInfoId(Long planId) {
        return Signature.findAllByPlanId(planId, [sort: "dateCreated", order: "desc"])
    }

    /**
     * 保存上传的签名文件
     * @param planId 排期id
     * @param name 签名人姓名
     * @param file 签名图片文件
     * @param path 文件保存路径
     * @return 成功返回signature 失败返回null
     */
    def saveSignature(long planId, String name, MultipartFile file, String path) {
        def filePath = "${planId}/${new Date().format("yyyyMMddHHmmssSSSS")}_${name}"
        def sfile = new File(path, filePath)
        if (!sfile.exists()) {
            sfile.getParentFile().mkdirs()
        }
        file.transferTo(sfile)
        def signature = Signature.findByPlanIdAndName(planId, name)
        if(!signature){
            signature = new Signature()
        }
        signature.planId = planId
        signature.name = name
        signature.path = filePath
        signature.size = file.size
        signature.type = file.getOriginalFilename().tokenize(".")[-1] ?: ""
        signature.save(flush: true)
        if(!signature.hasErrors()){
            signature
        }else{
            null
        }
    }
    /**
     * 保存上传的签名base64图片
     * @param planId 排期id
     * @param name 签名人姓名
     * @param base64Img 签名图片文件
     * @param path 文件保存路径
     * @return 成功返回signature 失败返回null
     */
    def saveSignature(long planId, String name, String base64Img, String path) {
        def filePath = "${planId}/${new Date().format("yyyyMMddHHmmssSSSS")}_${name}"
        def file = Base64ImgUtl.generateImage(base64Img, path+ filePath)
        def signature = Signature.findByPlanIdAndName(planId, name)
        if(!signature){
            signature = new Signature()
        }
        signature.planId = planId
        signature.name = name
        signature.path = filePath
        signature.size = file.size()
        signature.type = "png"
        signature.save(flush: true)
        if(!signature.hasErrors()){
            signature
        }else{
            null
        }
    }
}
