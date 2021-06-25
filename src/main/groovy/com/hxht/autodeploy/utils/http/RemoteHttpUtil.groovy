package com.hxht.autodeploy.utils.http


import cn.hutool.http.HttpRequest
import com.hxht.techcrt.court.Courtroom
import com.hxht.techcrt.utils.ManagerHostApiUtil
import org.grails.web.json.JSONObject

/**
 * 用于远程提讯的Http工具类 create by arctic
 */
class RemoteHttpUtil {

    static def remotePost(def model, String url){
        HashMap<String, String> form = new HashMap<>()
        form.put("params", (model as JSONObject).toString())
        new JSONObject(
                HttpRequest.post(url).form(form).timeout(20000).execute().body()
        )
    }

    /**
     * 向庭审主机设置指令
     * @param start 是否是开始连接
     * @param courtroom 本地法庭
     * @param url 流地址
     * @param enable 新版庭审主机控制开启或者关闭
     * @param num 通道号
     * @param start_rtsp 老版庭审主机控制开启或关闭
     * @return 返回值
     */
    static def send(boolean start, Courtroom courtroom, String url, boolean enable, Integer num, Integer start_rtsp) {
        //获取服务端法庭第num个解码通道
        def getDecoderProfilesRs = ManagerHostApiUtil.getDecoderProfiles("http://${courtroom.deviceIp}", num)
        def setDecoderProfileRs
        if (!getDecoderProfilesRs) {
            if (start) {
                setDecoderProfileRs = ManagerHostApiUtil.setDecodeStreamUrl("dec${num}.stream_url=${url}\ndec${num}.start_rtsp=${start_rtsp}\ndec${num}.rtspMode=tcp\ndec${num}.aud_mode=right\nnet_recv${num+1}.cache=500\n", courtroom.deviceIp)
            } else {
                setDecoderProfileRs = ManagerHostApiUtil.setDecodeStreamUrl("dec${num}.stream_url=${url}\ndec${num}.start_rtsp=${start_rtsp}\n", courtroom.deviceIp)
            }
        } else {
            setDecoderProfileRs = ManagerHostApiUtil.setDecoderProfile("http://${courtroom.deviceIp}", getDecoderProfilesRs.token, getDecoderProfilesRs.profileDescription, enable, url)
        }
        setDecoderProfileRs
    }
}
