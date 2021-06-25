package com.hxht.autodeploy.utils

import com.hxht.techcrt.court.Courtroom
import com.hxht.techcrt.utils.http.RemoteHttpUtil
import grails.converters.JSON

/**
 * 解析法庭配置工具类
 * 2021.04.23 >>> 修改远程提讯录像通知方式 daniel
 */
class CfgUtil {

    static getEncodeToStore(Courtroom courtroom) {
        String ip = courtroom.liveIp
        String port = courtroom.livePort
        def cfg = JSON.parse(courtroom.cfg)
        //端口默认554
        if (port == null || port == "") {
            port = "554"
        }
        def chnList = []
        //遍历编码通道将所有编码通道通知存储
        for (def encode : cfg.encode) {
            def record = encode.record
            if (record && record == "1") {//将所有可录制的视频通知存储
                chnList.add([
                        uid   : encode.uuid,
                        name  : encode.name,
                        number: encode.number,
                        url   : "rtsp://${ip}:${port}/${encode.encodeip}/${encode.number}"
                ])
            }
        }
        //获取现有解码通道将所有解码通道通知存储
        String distance = courtroom.distance
        if (distance) {
            String[] distanceArr = distance.split("///")
            for (int i=0; i<distanceArr.size(); i++) {
                String service = distanceArr[i]
                if (service.contains("no")) {
                    continue
                }
                String[] serviceArr = service.split(",")
                def resp = RemoteHttpUtil.remotePost([
                        id: serviceArr[1].split("=")[1]
                ], "${serviceArr[0].split("=")[1]}/api/remoteService/name")
                if (serviceArr[2] == "status1=yes") {
                    chnList.add([
                            uid   : resp.data.uid,
                            name  : "远程图像(1)",
                            number: "jmtd1",
                            url   : resp.data.rtsp1
                    ])
                }
                if (serviceArr[2] == "status2=yes") {
                    chnList.add([
                            uid   : resp.data.uid,
                            name  : "远程图像(2)",
                            number: "jmtd2",
                            url   : resp.data.rtsp2
                    ])
                }
            }
        }
        println "==========录像通道=========="
        println chnList.toString()
        println "==========录像通道=========="
        chnList
    }
}
