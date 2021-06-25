package com.hxht.autodeploy.wfy

import com.hxht.techcrt.Resp
import com.hxht.techcrt.court.CaseInfo
import com.hxht.techcrt.court.PlanInfo
import com.hxht.techcrt.enums.RespType
import com.hxht.techcrt.mem.SignatureService
import grails.converters.JSON
import org.grails.web.json.JSONObject

/**
 * 对接中国移动微法院互联网开庭管理平台
 * 5.2.1 接收接口
 */
class BmController {
    SignatureService signatureService
    /**
     * 用于接收签名图片
     * 互联网端当事人进行签名操作后，将签名图片提交
     * 接收参数格式
     *{  "courtCode":"1502",      法院代码
     *   "caseNumberCode":"",     内网案号
     *   "caseOrder":"1"           庭次
     *   "userName": "zhangsan", 参与人姓名
     *   "mobilePhone": "13900001111", 参与人手机号码
     *   "signPic":"经base64编码的签名图片"
     *}*/
    def uploadRecordSignPic() {
        def flag = grailsApplication.config.getProperty('weifayuan.flag') as Integer
        if (flag == 1) {
            def model = [
                    code   : -1,
                    message: "提交失败"
            ]
            try{
                def json = request.JSON as JSONObject
                log.info("[BmController.uploadRecordSignPic]接口被调用,接收到数据[${json.toString()}]")
                def archives = json.caseNumberCode as String //案号
                def name = json.userName as String //签名人姓名
                def signPic = json.signPic as String //签名图片
                def caseOrder = Integer.parseInt(json.caseOrder as String) //庭次
                //根据案号获取排期
                def caseInfo = CaseInfo.findByArchives(archives)
                if (!caseInfo) {
                    model.put("message", "未找到此案件案件信息,请检查案号是否正确.")
                    render model as JSON
                    return
                }
                def planInfo = null
                def planList = PlanInfo.findAllByCaseInfo(caseInfo, [sort: "dateCreated", order: "asc"])//根据创建时间模拟庭次
                if (planList.size() > 0) {
                    //存在排期,判断庭次是否正确
                    for (def i = 0; i < planList.size(); i++) {
                        if ((i + 1) == caseOrder) {
                            //庭次对应上了
                            planInfo = planList[i]
                        }
                    }
                    if (!planInfo) {
                        //如果没有匹配到排期,那么以最后一个排期为准
                        planInfo = planList.last()
                    }
                    def path = grailsApplication.config.getProperty('tc.signature.path')
                    def signature = signatureService.saveSignature(planInfo.id, name, signPic, path)
                    if (signature) {
                        model.put("code", 1000)
                        model.put("message", null)
                        render model as JSON
                    } else {
                        model.put("message", "提交失败.")
                        render model as JSON
                    }
                } else {
                    model.put("message", "未找到此案件排期信息,请检查排期是否存在.")
                    render model as JSON
                }
            }catch(e){
                e.printStackTrace()
                log.error("[BmController.uploadRecordSignPic]接口内部错误[${e.message}]")
                render model as JSON
            }

        } else {
            render Resp.toJson(RespType.FAIL,"接口功能未开启。")
        }
    }
    /**
     * 当参与人在线状态发生改变时被调用
     * 接收参数格式
     *{  "courtCode":"15023",      法院代码
     *   "caseNumberCode":"",     内网案号
     *   "caseOrder":"1",           庭次
     *   "userName": "zhangsan",
     *   "mobilePhone": "13900001111",
     *   "status":"ONLINE"
     *}*/
    def noticeLitigantInfo() {
        def flag = grailsApplication.config.getProperty('weifayuan.flag') as Integer
        if (flag == 1) {
            def json = request.JSON as JSONObject
            log.info("[BmController.noticeLitigantInfo]接口被调用,接收到数据[${json.toString()}]")
            def model = [
                    code   : 1000,
                    message: null
            ]
            render model as JSON
        }else{
            render Resp.toJson(RespType.FAIL,"接口功能未开启。")
        }
    }
}
