package com.hxht.autodeploy.utils

import cn.hutool.core.io.IORuntimeException
import com.hxht.techcrt.utils.http.HttpUtil

import java.nio.charset.StandardCharsets

/**
 * 庭审主机接口调用  by Arctic in 2019.12.10
 * 2021.04.29 >>> 增加远程提讯不支持时的状态 daniel
 */
class ManagerHostApiUtil {

    /**
     * 播放命令TV0-token----法庭所有画面切换为远程3通道
     *      TV0_：由于是远程提讯我们没有开庭，可能在该法庭只有几个记录人员，因此在所有的电视视频上同时显示远程提讯画面
     *      token：使用远端3通道若被占用执行覆盖,已经写死使用token通道，如果有其他需要需要更改请联系开发人员
     * @param enable  enable
     * @param url  码流
     * @param commitUrl  接口地址
     * @param token  通道号
     * @param profileDescription  通道名称
     * @return  0成功  非0失败
     */
    static Integer setDecoderProfile(String commitUrl,String token,String profileDescription, boolean enable, String url){
        final String postUrl =  commitUrl +"/activeProtocol.action"
        def model = [
            method : "setDecoderProfile",
            params: [
                decoderProfile : [
                    token : token,
                    active: "RTSPClient",
                    profileDescription: profileDescription,
                    enable: enable,
                    audioChannels: "stereo",
                    cacheTime: 500,
                    dataContentType: "videoAudio",
                    h264RefFrame: 3,
                    rtspPara : [
                            url: url,
                            rtspPullStreamType: "TCP"
                    ],
                    sipPara : [token: "SIP_1"],
                    h323Para : [token: "H323_1"]
                ]
            ]
        ]
        def result = HttpUtil.postToJson(postUrl, model)
        if (result) {
            result.result.params.retCode
        } else {
            404
        }
    }

    /**
     * 根据数值获取解码通道
     * @param commitUrl  接口地址
     * @return  Map---->{token, decoderProfiles}
     */
    static Map<String,String> getDecoderProfiles(String commitUrl, Integer num){
        try {
            //庭审主机请求地址
            final String postUrl =  commitUrl +"/activeProtocol.action"
            def model = [
                    method : "getDecoderProfiles"
            ]
            def result = HttpUtil.postToJson(postUrl, model)
            if (result == null){
                return null
            }
            def data = result.result.params.decoderProfiles
            Map<String, String> dataMap = new HashMap<>()
            dataMap.put("token", data.get(num).token)
            dataMap.put("profileDescription", data.get(num).profileDescription)
            dataMap
        } catch (IORuntimeException e) {
            println "[ManagerHostApiUtil.getDecoderProfiles] 查询新版本主机通达失败,错误信息:${e.message}"
            null
        }
    }

    /**
     * 设置1.0版本主机配置信息
     */
    static int setDecodeStreamUrl(String dataStr, String ip){
        //计算指令码的byte数组
        String insCode = "0003"
        byte[] dataLast= dataHandle(insCode, dataStr)
        //创建socket连接
        Socket client = new Socket(ip , 6180)
        client.setSoTimeout(5000)
        OutputStream out = client.getOutputStream()
        out.write(dataLast)
        InputStream inputStream = client.getInputStream()
        int read = inputStream.read()
        out.close()
        client.close()
        read
    }

    /**
     * 获取解码通道成功与否决定使用哪一个方法获取值
     */
    static String getDeviceVersion(String ip){
        //获取第一个解码通道
        if (getDecoderProfiles("http://${ip}",0)){
            //新版本主机
            Map<String,String> dataMap = getBaseInfoProfile("http://${ip}")
            return "${dataMap.get("softwareVersion")}/${dataMap.get("hardwareVersion")}"
        }else{
            //旧版本主机
            return "${getOldDeviceValue("sys._swver\n", ip)}/${getOldDeviceValue("sys._hwver\n", ip)}"
        }
    }

    /**
     * 获取3.0版本主机基础信息
     */
    static Map<String,String> getBaseInfoProfile(String commitUrl){
        //庭审主机请求地址
        final String postUrl =  commitUrl +"/activeProtocol.action"
        def model = [
                method : "getBaseInfoProfile"
        ]
        def result = HttpUtil.postToJson(postUrl, model)
        if (result == null){
            return null
        }
        def data = result.result.params.baseInfoProfile
        Map<String, String> dataMap = new HashMap<>()
        dataMap.put("softwareVersion", data.softwareVersion)
        dataMap.put("hardwareVersion", data.hardwareVersion)
        dataMap
    }

    /**
     * 获取1.0版本主机配置信息
     */
    static def getOldDeviceValue(String dataStr, String ip){
        //计算指令码的byte数组
        String insCode = "0002"
        //创建socket连接
        byte[] dataLast= dataHandle(insCode, dataStr)
        Socket client = new Socket(ip , 6180)
        client.setSoTimeout(5000)
        OutputStream out = new DataOutputStream(client.getOutputStream())
        out.write(dataLast)
        InputStream inputStream = new DataInputStream(client.getInputStream())
        byte[] resultBytes = toByteArray(inputStream)
        String strRead = new String(resultBytes)
        strRead = String.copyValueOf(strRead.toCharArray(), 0, resultBytes.length)
        out.close()
        client.close()
        //最终结果
        strRead.split("=")[1] == "\n"?"":strRead.split("=")[1].split("\n")[0]
    }

    /**
     * 将两个bytes数组合并成成一个bytes数组
     */
    static def combineBytes(byte[] byte1, byte[] byte2){
        byte[] bytesResult = new byte[byte2.length + byte1.length]
        System.arraycopy(byte1, 0, bytesResult, 0, byte1.length)
        System.arraycopy(byte2, 0, bytesResult, byte1.length, byte2.length)
        return bytesResult
    }

    /**
     * 字节流转换为字节数组
     */
    static byte[] toByteArray(DataInputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream()
        byte[] buffer = new byte[1024]
        int n = 0
        if(-1 != (n = input.read(buffer))){
            output.write(buffer)
        }
        return output.toByteArray()
    }

    /**
     * 字符串转换为字节数组
     * @param str
     * @return
     */
    static byte[] stringToByteArray(String str){
        byte[] lengthCodeByte = new byte[str.length() / 2]
        for (int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2)
            lengthCodeByte[i] = (byte) Integer.parseInt(subStr, 16)
        }
        lengthCodeByte
    }

    /**
     * 字节数组处理
     * @param insCode
     * @param dataStr
     * @return
     */
    static byte[] dataHandle(String insCode, String dataStr){
        byte[] insCodeByte = stringToByteArray(insCode)

        //计算数据长度
        int length = dataStr.length()
        String lengthCode = (length>=16?"00":"000")+Integer.toHexString(length)
        byte[] lengthCodeByte = stringToByteArray(lengthCode)

        //数据字节数组
        byte[] dataByte = dataStr.getBytes(StandardCharsets.UTF_8)

        //字节数组拼接
        byte[] bytes2 = combineBytes(insCodeByte, lengthCodeByte)
        combineBytes(bytes2, dataByte)
    }

    /**
     * 执行静音操作
     * @param chn 通道
     * @param isMute 是否执行
     * @param postUrl 地址
     */
    static def setAudioProcessorInputIsMuteProfile(def chn, def isMute ,def postUrl){
        def model = [
                method : "setAudioProcessorInputIsMuteProfile",
                params: [
                        audioProcessorInputIsMuteProfile:[
                                token:"APIChn_${chn}_1",
                                isMute:isMute
                        ]
                ]
        ]
        HttpUtil.postToJson(postUrl, model)
    }
}
