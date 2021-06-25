package com.hxht.autodeploy.court.manager.info.courtroom

import cn.hutool.http.HttpRequest
import com.hxht.techcrt.Resp
import com.hxht.techcrt.court.Courtroom
import com.hxht.techcrt.enums.RespType
import com.hxht.techcrt.utils.SignUtil
import grails.converters.JSON
import org.grails.web.json.JSONObject

/**
 * 上报远程法庭信息控制器 by Arctic in 2019.12.02
 */
class RemoteController {

    CourtroomService courtroomService

    /**
     * 上报远程法庭信息
     * @return  回调信息  0上报成功  其他上报失败  message失败信息
     */
    def synrooms() {
        def remoteUrl = grailsApplication.config.getProperty('tc.remote.report.path') + "/rtrc/interface/synrooms"
        //需要上传的法庭主键字符串按,分割
        def courtStr = params.get("data") as String
        //按,分割的法庭主键数组
        def courtArr = courtStr.split(",")
        def rooms = []
        for (String id:courtArr){
            def courtroom = Courtroom.get(id as Long)
            def room = [
                id: id as Long,
                name: courtroom.name,
                valid: courtroom.status,
                summary: "",
                jkurl: "",
                devices: [
                    hosts: [
                            name: "AT2",
                            type: courtroom.deviceType,
                            ip: courtroom.deviceIp,
                            port: 80
                    ],
                    h323Devs: [],
                    remoteEncoders: [
                        [
                            ip : courtroom.liveIp,
                            port : courtroom.livePort,
                            username : "",
                            password : "",
                            type : "AT2",
                            streamName : "${courtroom.deviceIp}/r"
                        ]
                    ]
                ]
            ]
            rooms.add(room)
        }
        def source = "12"
        def corpId = "J30"
        def model = [
                source: source,
                sign: SignUtil.getSign(source, "ropIn"),
                corpId: corpId,
                interfaceUrl: grailsApplication.config.getProperty('tc.remote.local.wsdl-path-prefix') + "/tc/services/iremotetrail?wsdl",
                jcfwUrl: "",
                rooms: rooms
        ]
        HashMap<String, String> form = new HashMap<>()
        form.put("params", (model as JSON).toString())
        def resp = new JSONObject(
                HttpRequest.post(remoteUrl).form(form).timeout(20000).execute().body()
        )
        log.info("[RemoteController] 上传远程法庭返回值：${resp}")
        if (resp.code == 0){
            courtroomService.handleRemote(courtArr)
            render Resp.toJson(RespType.SUCCESS)
        }else{
            render Resp.toJson(RespType.FAIL, resp.message)
        }
    }

    /**
     * 获取所有法庭展示在页面上，在页面判断法庭是否已经是远程法庭，如果已经是远程法庭就打上对号
     */
    def index(){
        //获取所有法庭
        [courts:Courtroom.findAll()]
    }
}
