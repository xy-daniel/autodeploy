package com.hxht.autodeploy.court.manager.info.courtroom

import com.hxht.techcrt.Resp
import com.hxht.techcrt.court.Courtroom
import com.hxht.techcrt.enums.RespType
import com.hxht.techcrt.utils.CtrlCommandUtil
import com.hxht.techcrt.utils.UUIDGenerator
import grails.converters.JSON

/**
 * 法庭中控控制器  by arctic in 2019.11.14
 */
class CtrlController {
    CtrlService ctrlService
    CourtroomService courtroomService

    /**
     * 法庭配置主页
     */
    def index() {
        def courtroom = Courtroom.get(params.long("id"))
        def flag = params.get("flag")
        if(!flag){
            flag = '0'
        }
        def encode = null //解码器
        def ycEncode = null //解码器
        def decode = null   //编码器
        def videoMatrix = null   //VIDEO矩阵
        def vgaMatrix = null   //VGA矩阵
        def outMatrix = null   //输出控制
        def soundMatrix = null   //音量控制
        def total = null   //综合控制
        def power = null   //强电控制
        def powerNew = null   //新电源控制
        def irctrl = null   //红外控制
        def camera = null   //摄像机控制
        if (courtroom?.cfg) {
            def cfg = JSON.parse(courtroom.cfg)
            encode = cfg.encode
            ycEncode = cfg.ycEncode
            decode = cfg.decode
            videoMatrix = cfg.videoMatrix
            vgaMatrix = cfg.vgaMatrix
            outMatrix = cfg.outMatrix
            soundMatrix = cfg.soundMatrix
            total = cfg.total
            power = cfg.power
            irctrl = cfg.irctrl
            camera = cfg.camera
            powerNew = cfg.powerNew
        }
        LinkedHashSet<Object> soundGroup = new LinkedHashSet<>()//音量控制组
        for (def sound : soundMatrix){
            soundGroup.add(sound?.group?.split("_")[0])
        }
        [courtroom: courtroom, encode: encode, ycEncode: ycEncode, decode: decode, videoMatrix: videoMatrix, vgaMatrix: vgaMatrix,
         outMatrix: outMatrix, soundMatrix: soundMatrix, total: total, power: power, powerNew: powerNew, powerNew: powerNew, 
         irctrl: irctrl, camera: camera, flag: flag, soundGroup: soundGroup]
    }

    /**
     * 设备控制输出
     * @return
     */
    def show() {
        this.index()
    }
    
    /**
     * 庭审主机发送指令
     * @return
     */
    def tcpCommand() {
        if (request.method == "POST") {
            try {
                def command = params.get("command")
                def ip = params.get("ip")
                if (!command || !ip){
                    render Resp.toJson(RespType.FAIL,"庭审主机发送的指令或者ip为空 请求参数为：${params.toString()}")
                }
                CtrlCommandUtil.ctrlCommand(ip, 8060, command)
                render Resp.toJson(RespType.SUCCESS)
            } catch (e) {
                e.printStackTrace()
                log.error("[CtrlController.tcpCommand] 向庭审主机发送指令出错！，错误信息：${e.message}")
                render Resp.toJson(RespType.FAIL)
            }
        }
    }

    /**
     * 向电源控制主机发送tcp指令
     * @return
     */
    def tcpCommandPowerNew() {
        if (request.method == "POST") {
            try {
                def command = params.get("command")
                def ip = params.get("ip")
                if (!command || !ip){
                    render Resp.toJson(RespType.FAIL,"发送的指令或者ip为空 请求参数为：${params.toString()}")
                }
                CtrlCommandUtil.ctrlCommandPowerNew(ip, 8080, command)
                render Resp.toJson(RespType.SUCCESS)
            } catch (e) {
                e.printStackTrace()
                log.error("[CtrlController.tcpCommand] 向庭审主机发送指令出错！，错误信息：${e.message}")
                render Resp.toJson(RespType.FAIL)
            }
        }
    }
    
    /**
     * 添加编码器
     */
    def addEncode() {
        def courtroom = Courtroom.get(params.long("id"))
        [courtroom: courtroom]
    }
    /**
     * 添加远程编码器
     * @return  view
     */
    def addYcEncode(){
        def courtroom = Courtroom.get(params.long("id"))
        [courtroom: courtroom]
    }

    /**
     * 添加编码器  执行
     */
    def addSaveEncode() {
        def courtroom = Courtroom.get(params.long("id"))
        def cfg = ctrlService.getCfg(courtroom)
        //为所有不存在排序的编码通道设置order=1
        boolean flag = false
        for (def encode:cfg.encode) {
            if (!encode.order) {
                encode.order = "1"
                flag = true
            }
        }
        //如果经过初始化则保存后重新获取法庭配置
        if (flag) {
            courtroom.cfg = cfg as JSON
            courtroomService.save(courtroom)
            courtroom = Courtroom.get(params.long("id"))
            cfg = ctrlService.getCfg(courtroom)
        }
        //用于循环
        def encodeList = cfg.encode as List
        //用于存储
        def cfgSaveList = cfg.encode as List
        boolean cfgSaveFlag = false
        for (int i=0; i<encodeList.size(); i++) {
            if (Integer.parseInt(encodeList[i].order as String) > Integer.parseInt(params.order as String)) {
                cfgSaveList.add(i,[
                        uuid    : UUIDGenerator.nextUUID(),
                        name    : params.name,     //本地名称
                        encodeip: params.encodeip, //编码器ip
                        number  : params.number,   //通道名称
                        record  : params.record,   //录制允诺
                        order   : params.order     //排序
                ])
                cfgSaveFlag = true
                break
            }
        }
        cfg.encode = cfgSaveList
        if (!cfgSaveFlag) {
            cfg.encode.add([
                    uuid    : UUIDGenerator.nextUUID(),
                    name    : params.name,  //本地名称
                    encodeip: params.encodeip,  //编码器ip
                    number  : params.number,  //通道名称
                    record  : params.record  //录制允诺
            ])
        }
        courtroom.cfg = cfg as JSON
        courtroomService.save(courtroom)
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 执行添加远程编码器
     */
    def addSaveYcEncode(){
        def courtroom = Courtroom.get(params.long("id"))
        def cfg = ctrlService.getCfg(courtroom)
        cfg.ycEncode.add([
                uuid    : UUIDGenerator.nextUUID(),
                name    : params.name,  //本地名称
                encodeip: params.encodeip,  //编码器ip
                number  : params.number,  //通道名称
        ])
        courtroom.cfg = cfg as JSON
        courtroomService.save(courtroom)
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 修改编码器
     */
    def editEncode(){
        def courtroom = Courtroom.get(params.long("id"))
        def uuid = params.get("uuid") as String
        def name="",encodeip="",number="",record="",order=""  //编码器名称--ip--通道--录制应允--排序
        for(int i=0; i<JSON.parse(courtroom.cfg).encode.size(); i++){
            def encode = JSON.parse(courtroom.cfg).encode[i]
            if (encode.uuid==uuid){
                name = encode.name
                encodeip = encode.encodeip
                number = encode.number
                record = encode.record
                order = encode.order
                break
            }
        }
        [roomId:courtroom.id, uuid:uuid, name:name, encodeip:encodeip, number:number, record:record, order:order]
    }

    def editYcEncode(){
        def courtroom = Courtroom.get(params.long("id"))
        def uuid = params.get("uuid") as String
        def name="",encodeip="",number=""  //编码器名称--ip--通道--录制应允
        for(int i=0; i<JSON.parse(courtroom.cfg).ycEncode.size(); i++){
            def encode = JSON.parse(courtroom.cfg).ycEncode[i]
            if (encode.uuid==uuid){
                name = encode.name
                encodeip = encode.encodeip
                number = encode.number
                break
            }
        }
        [roomId:courtroom.id, uuid:uuid, name:name, encodeip:encodeip, number:number]
    }

    /**
     * 修改编码器  执行
     */
    def editSaveEncode(){
        def courtroom = Courtroom.get(params.long("id"))
        def uuid = params.get("uuid") as String
        def name = params.get("name") as String
        def encodeip = params.get("encodeip") as String
        def number = params.get("number") as String
        def record = params.get("record") as String
        def order = params.order as String
        if (!(courtroom && uuid && name && encodeip && number && record && order)){
            render Resp.toJson(RespType.FAIL)
            return
        }
        /**
         * 1.获取法庭配置
         * 2.为所有不存在排序的编码通道设置order=1，重新获取法庭配置
         * 3.先把数据改正然后提取当前encode,删除当前encode
         * 4.执行添加操作
         */
        def cfg = ctrlService.getCfg(courtroom)

        //为所有不存在排序的编码通道设置order=1
        boolean flag = false
        for (def encode:cfg.encode) {
            if (!encode.order) {
                encode.order = "1"
                flag = true
            }
        }
        //如果经过初始化则保存后重新获取法庭配置
        if (flag) {
            courtroom.cfg = cfg as JSON
            courtroomService.save(courtroom)
            courtroom = Courtroom.get(params.long("id"))
            cfg = ctrlService.getCfg(courtroom)
        }

        def currentEncode = []
        for (int i=0; i<cfg.encode.size(); i++){
            if (cfg.encode[i].uuid == uuid){
                cfg.encode[i].name = name
                cfg.encode[i].encodeip = encodeip
                cfg.encode[i].number = number
                cfg.encode[i].record = record
                cfg.encode[i].order = order
                currentEncode = cfg.encode[i]
                cfg.encode.remove(i)
                break
            }
        }

        //用于循环
        def encodeList = cfg.encode as List
        //用于存储
        def cfgSaveList = cfg.encode as List
        boolean cfgSaveFlag = false
        for (int i=0; i<encodeList.size(); i++) {
            if (Integer.parseInt(encodeList[i].order as String) > Integer.parseInt(params.order as String)) {
                cfgSaveList.add(i,currentEncode)
                cfgSaveFlag = true
                break
            }
        }
        cfg.encode = cfgSaveList
        if (!cfgSaveFlag) {
            cfg.encode.add(currentEncode)
        }

        courtroom.cfg = cfg as JSON
        courtroomService.save(courtroom)
        render Resp.toJson(RespType.SUCCESS)
    }

    def editSaveYcEncode(){
        def courtroom = Courtroom.get(params.long("id"))
        def uuid = params.get("uuid") as String
        def name = params.get("name") as String
        def encodeip = params.get("encodeip") as String
        def number = params.get("number") as String
        if (courtroom && uuid && name && encodeip && number){
            //法庭配置
            def cfg = ctrlService.getCfg(courtroom)
            for (int i=0; i<cfg.ycEncode.size(); i++){
                if (cfg.ycEncode[i].uuid == uuid){
                    //执行修改
                    cfg.ycEncode[i].name = name
                    cfg.ycEncode[i].encodeip = encodeip
                    cfg.ycEncode[i].number = number
                    break
                }
            }
            courtroom.cfg = cfg as JSON
            courtroomService.save(courtroom)
            render Resp.toJson(RespType.SUCCESS)
        }else{
            render Resp.toJson(RespType.FAIL)
        }
    }

    /**
     * 删除部分编码器  执行
     */
    def delEncodes(){
        def uuidsStr = params.get("uuids") as String
        if (!uuidsStr) {
            render Resp.toJson(RespType.DATA_NOT_EXIST)
            return
        }
        def courtroom = Courtroom.get(params.long("id"))
        if (uuidsStr && courtroom){
            //uuidStr数组
            def uuidsArr = uuidsStr.split(",")
            //法庭配置
            def cfg = ctrlService.getCfg(courtroom)
            ctrlService.delEncodes(uuidsArr, cfg)
            courtroom.cfg = cfg as JSON
            courtroomService.save(courtroom)
            render Resp.toJson(RespType.SUCCESS)
        }else{
            render Resp.toJson(RespType.FAIL)
        }
    }

    def delYcEncodes(){
        def uuidsStr = params.get("uuids") as String
        if (!uuidsStr) {
            render Resp.toJson(RespType.DATA_NOT_EXIST)
            return
        }
        def courtroom = Courtroom.get(params.long("id"))
        if (uuidsStr && courtroom){
            //uuidStr数组
            def uuidsArr = uuidsStr.split(",")
            //法庭配置
            def cfg = ctrlService.getCfg(courtroom)
            ctrlService.delYcEncodes(uuidsArr, cfg)
            courtroom.cfg = cfg as JSON
            courtroomService.save(courtroom)
            render Resp.toJson(RespType.SUCCESS)
        }else{
            render Resp.toJson(RespType.FAIL)
        }
    }

    /**
     * 添加解码器
     */
    def addDecode() {
        def courtroom = Courtroom.get(params.long("id"))
        [courtroom: courtroom]
    }

    /**
     * 添加解码器  执行
     */
    def addSaveDecode() {
        def courtroom = Courtroom.get(params.long("id"))
        def cfg = ctrlService.getCfg(courtroom)
        cfg.decode.add([
                uuid    : UUIDGenerator.nextUUID(),
                name    : params.name,  //本地名称
                decodeip: params.decodeip,  //解码器ip
                number  : params.number  //通道名称
        ])
        courtroom.cfg = cfg as JSON
        courtroomService.save(courtroom)
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 修改解码器
     */
    def editDecode(){
        def courtroom = Courtroom.get(params.long("id"))
        def uuid = params.get("uuid") as String
        def name="",decodeip="",number=""  //解码器名称--ip--通道
        for(int i=0; i<JSON.parse(courtroom.cfg).decode.size(); i++){
            def decode = JSON.parse(courtroom.cfg).decode[i]
            if (decode.uuid==uuid){
                name = decode.name
                decodeip = decode.decodeip
                number = decode.number
                break
            }
        }
        [roomId:courtroom.id, uuid:uuid, name:name, decodeip:decodeip, number:number]
    }

    /**
     * 修改解码器  执行
     */
    def editSaveDecode(){
        def courtroom = Courtroom.get(params.long("id"))
        def uuid = params.get("uuid") as String
        def name = params.get("name") as String
        def decodeip = params.get("decodeip") as String
        def number = params.get("number") as String
        if (courtroom && uuid && name && decodeip && number){
            //法庭配置
            def cfg = ctrlService.getCfg(courtroom)
            for (int i=0; i<cfg.decode.size(); i++){
                if (cfg.decode[i].uuid == uuid){
                    //执行修改
                    cfg.decode[i].name = name
                    cfg.decode[i].decodeip = decodeip
                    cfg.decode[i].number = number
                    break
                }
            }
            courtroom.cfg = cfg as JSON
            courtroomService.save(courtroom)
            render Resp.toJson(RespType.SUCCESS)
        }else{
            render Resp.toJson(RespType.FAIL)
        }
    }

    /**
     * 删除部分解码器  执行
     */
    def delDecodes(){
        def uuidsStr = params.get("uuids") as String
        if (!uuidsStr) {
            render Resp.toJson(RespType.DATA_NOT_EXIST)
            return
        }
        def courtroom = Courtroom.get(params.long("id"))
        if (uuidsStr && courtroom){
            //uuidStr数组
            def uuidsArr = uuidsStr.split(",")
            //法庭配置
            def cfg = ctrlService.getCfg(courtroom)
            ctrlService.delDecodes(uuidsArr, cfg)
            courtroom.cfg = cfg as JSON
            courtroomService.save(courtroom)
            render Resp.toJson(RespType.SUCCESS)
        }else{
            render Resp.toJson(RespType.FAIL)
        }
    }

    /**
     * 添加VIDEO矩阵
     */
    def addVideo() {
        def courtroom = Courtroom.get(params.long("id"))
        [courtroom: courtroom]
    }

    /**
     * 添加VIDEO矩阵  执行
     */
    def addSaveVideo() {
        def courtroom = Courtroom.get(params.long("id"))
        def cfg = ctrlService.getCfg(courtroom)
        cfg.videoMatrix.add([
                uuid : UUIDGenerator.nextUUID(),
                //名称
                name : params.name,
                //指令
                codeDown : params.codeDown,
                //可见状态
                visible: params.visible
        ])
        courtroom.cfg = cfg as JSON
        courtroomService.save(courtroom)
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 修改VIDEO矩阵
     */
    def editVideo(){
        def courtroom = Courtroom.get(params.long("id"))
        def uuid = params.get("uuid") as String
        def name="",codeDown="",visible=""  //名称--控制指令--可见状态
        for(int i=0; i<JSON.parse(courtroom.cfg).videoMatrix.size(); i++){
            def videoMatrix = JSON.parse(courtroom.cfg).videoMatrix[i]
            if (videoMatrix.uuid==uuid){
                name = videoMatrix.name
                codeDown = videoMatrix.codeDown
                visible = videoMatrix.visible
                break
            }
        }
        [roomId:courtroom.id, uuid:uuid, name:name, codeDown:codeDown, visible:visible]
    }

    /**
     * 更新VIDEO矩阵  执行
     */
    def editSaveVideo(){
        def courtroom = Courtroom.get(params.long("id"))
        def uuid = params.get("uuid") as String
        //名称
        def name = params.get("name") as String
        //控制指令
        def codeDown = params.get("codeDown") as String
        //可见状态
        def visible = params.get("visible") as String
        if (courtroom && uuid && name && codeDown && visible){
            //法庭配置
            def cfg = ctrlService.getCfg(courtroom)
            for (int i=0; i<cfg.videoMatrix.size(); i++){
                if (cfg.videoMatrix[i].uuid == uuid){
                    //执行修改
                    cfg.videoMatrix[i].name = name
                    cfg.videoMatrix[i].codeDown = codeDown
                    cfg.videoMatrix[i].visible = visible
                    break
                }
            }
            courtroom.cfg = cfg as JSON
            courtroomService.save(courtroom)
            render Resp.toJson(RespType.SUCCESS)
        }else{
            render Resp.toJson(RespType.FAIL)
        }
    }

    /**
     * 删除部分VIDEO矩阵  执行
     */
    def delVideos(){
        def uuidsStr = params.get("uuids") as String
        if (!uuidsStr) {
            render Resp.toJson(RespType.DATA_NOT_EXIST)
            return
        }
        def courtroom = Courtroom.get(params.long("id"))
        if (uuidsStr && courtroom){
            //uuidStr数组
            def uuidsArr = uuidsStr.split(",")
            //法庭配置
            def cfg = ctrlService.getCfg(courtroom)
            ctrlService.delVideos(uuidsArr, cfg)
            courtroom.cfg = cfg as JSON
            courtroomService.save(courtroom)
            render Resp.toJson(RespType.SUCCESS)
        }else{
            render Resp.toJson(RespType.FAIL)
        }
    }

    /**
     * 添加VGA矩阵
     */
    def addVga() {
        def courtroom = Courtroom.get(params.long("id"))
        [courtroom: courtroom]
    }

    /**
     * 添加VGA矩阵  执行
     */
    def addSaveVga() {
        def courtroom = Courtroom.get(params.long("id"))
        def cfg = ctrlService.getCfg(courtroom)
        cfg.vgaMatrix.add([
                uuid : UUIDGenerator.nextUUID(),
                //名称
                name : params.name,
                //指令
                codeDown : params.codeDown,
                //可见状态
                visible: params.visible
        ])
        courtroom.cfg = cfg as JSON
        courtroomService.save(courtroom)
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 修改VGA矩阵
     */
    def editVga(){
        def courtroom = Courtroom.get(params.long("id"))
        def uuid = params.get("uuid") as String
        def name="",codeDown="",visible=""  //名称--控制指令--可见状态
        for(int i=0; i<JSON.parse(courtroom.cfg).vgaMatrix.size(); i++){
            def vgaMatrix = JSON.parse(courtroom.cfg).vgaMatrix[i]
            if (vgaMatrix.uuid==uuid){
                name = vgaMatrix.name
                codeDown = vgaMatrix.codeDown
                visible = vgaMatrix.visible
                break
            }
        }
        [roomId:courtroom.id, uuid:uuid, name:name, codeDown:codeDown, visible:visible]
    }

    /**
     * 修改VGA矩阵  执行
     */
    def editSaveVga(){
        def courtroom = Courtroom.get(params.long("id"))
        def uuid = params.get("uuid") as String
        //名称
        def name = params.get("name") as String
        //控制指令
        def codeDown = params.get("codeDown") as String
        //可见状态
        def visible = params.get("visible") as String
        if (courtroom && uuid && name && codeDown && visible){
            //法庭配置
            def cfg = ctrlService.getCfg(courtroom)
            for (int i=0; i<cfg.vgaMatrix.size(); i++){
                if (cfg.vgaMatrix[i].uuid == uuid){
                    //执行修改
                    cfg.vgaMatrix[i].name = name
                    cfg.vgaMatrix[i].codeDown = codeDown
                    cfg.vgaMatrix[i].visible = visible
                    break
                }
            }
            courtroom.cfg = cfg as JSON
            courtroomService.save(courtroom)
            render Resp.toJson(RespType.SUCCESS)
        }else{
            render Resp.toJson(RespType.FAIL)
        }
    }

    /**
     * 删除部分VGA矩阵  执行
     */
    def delVgas(){
        def uuidsStr = params.get("uuids") as String
        if (!uuidsStr) {
            render Resp.toJson(RespType.DATA_NOT_EXIST)
            return
        }
        def courtroom = Courtroom.get(params.long("id"))
        if (uuidsStr && courtroom){
            //uuidStr数组
            def uuidsArr = uuidsStr.split(",")
            //法庭配置
            def cfg = ctrlService.getCfg(courtroom)
            ctrlService.delVgas(uuidsArr, cfg)
            courtroom.cfg = cfg as JSON
            courtroomService.save(courtroom)
            render Resp.toJson(RespType.SUCCESS)
        }else{
            render Resp.toJson(RespType.FAIL)
        }
    }

    /**
     * 添加输出控制
     */
    def addOut() {
        def courtroom = Courtroom.get(params.long("id"))
        [courtroom: courtroom]
    }

    /**
     * 添加输出控制  执行
     */
    def addSaveOut() {
        def courtroom = Courtroom.get(params.long("id"))
        def cfg = ctrlService.getCfg(courtroom)
        cfg.outMatrix.add([
                uuid : UUIDGenerator.nextUUID(),
                //名称
                name : params.name,
                //指令
                codeDown : params.codeDown,
                //可见状态
                visible: params.visible
        ])
        courtroom.cfg = cfg as JSON
        courtroomService.save(courtroom)
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 修改输出控制
     */
    def editOut(){
        def courtroom = Courtroom.get(params.long("id"))
        def uuid = params.get("uuid") as String
        def name="",codeDown="",visible=""  //名称--控制指令--可见状态
        for(int i=0; i<JSON.parse(courtroom.cfg).outMatrix.size(); i++){
            def outMatrix = JSON.parse(courtroom.cfg).outMatrix[i]
            if (outMatrix.uuid==uuid){
                name = outMatrix.name
                codeDown = outMatrix.codeDown
                visible = outMatrix.visible
                break
            }
        }
        [roomId:courtroom.id, uuid:uuid, name:name, codeDown:codeDown, visible:visible]
    }

    /**
     * 修改输出控制  执行
     */
    def editSaveOut(){
        def courtroom = Courtroom.get(params.long("id"))
        def uuid = params.get("uuid") as String
        //名称
        def name = params.get("name") as String
        //控制指令
        def codeDown = params.get("codeDown") as String
        //可见状态
        def visible = params.get("visible") as String
        if (courtroom && uuid && name && codeDown && visible){
            //法庭配置
            def cfg = ctrlService.getCfg(courtroom)
            for (int i=0; i<cfg.outMatrix.size(); i++){
                if (cfg.outMatrix[i].uuid == uuid){
                    //执行修改
                    cfg.outMatrix[i].name = name
                    cfg.outMatrix[i].codeDown = codeDown
                    cfg.outMatrix[i].visible = visible
                    break
                }
            }
            courtroom.cfg = cfg as JSON
            courtroomService.save(courtroom)
            render Resp.toJson(RespType.SUCCESS)
        }else{
            render Resp.toJson(RespType.FAIL)
        }
    }

    /**
     * 删除部分输出控制  执行
     */
    def delOuts(){
        def uuidsStr = params.get("uuids") as String
        if (!uuidsStr) {
            render Resp.toJson(RespType.DATA_NOT_EXIST)
            return
        }
        def courtroom = Courtroom.get(params.long("id"))
        if (uuidsStr && courtroom){
            //uuidStr数组
            def uuidsArr = uuidsStr.split(",")
            //法庭配置
            def cfg = ctrlService.getCfg(courtroom)
            ctrlService.delOuts(uuidsArr, cfg)
            courtroom.cfg = cfg as JSON
            courtroomService.save(courtroom)
            render Resp.toJson(RespType.SUCCESS)
        }else{
            render Resp.toJson(RespType.FAIL)
        }
    }

    /**
     * 添加红外控制
     */
    def addIrctrl() {
        def courtroom = Courtroom.get(params.long("id"))
        [courtroom: courtroom]
    }

    /**
     * 添加红外控制  执行
     */
    def addSaveIrctrl() {
        def courtroom = Courtroom.get(params.long("id"))
        def cfg = ctrlService.getCfg(courtroom)
        cfg.irctrl.add([
                uuid : UUIDGenerator.nextUUID(),
                //名称
                name : params.name,
                //可见状态
                visible: params.visible
        ])
        courtroom.cfg = cfg as JSON
        courtroomService.save(courtroom)
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 修改红外控制
     */
    def editIrctrl(){
        def courtroom = Courtroom.get(params.long("id"))
        def uuid = params.get("uuid") as String
        def name="",codeDown="",visible=""  //名称--控制指令--可见状态
        for(int i=0; i<JSON.parse(courtroom.cfg).irctrl.size(); i++){
            def irctrl = JSON.parse(courtroom.cfg).irctrl[i]
            if (irctrl.uuid==uuid){
                name = irctrl.name
                visible = irctrl.visible
                break
            }
        }
        [roomId:courtroom.id, uuid:uuid, name:name, visible:visible]
    }

    /**
     * 红外控制按钮列表
     */
    def irctrlBtns () {
        def courtroom = Courtroom.get(params.long("id"))
        def uuid = params.get("uuid") as String
        def button = null
        if (courtroom?.cfg) {
            def cfg = JSON.parse(courtroom.cfg)
            for (int i=0; i<cfg.irctrl.size(); i++){
                if (cfg.irctrl[i].uuid == uuid){
                    button = cfg.irctrl[i].buttons
                }
            }
        }
        [courtroom: courtroom,uuid: uuid, button: button]
    }

    /**
     * 前往添加红外控制页面
     */
    def addIrctrlBtns () {
        //法庭
        def courtroom = Courtroom.get(params.long("id"))
        //红外控制的一个的uuid
        def uuid = params.get("uuid") as String
        [courtroom: courtroom,uuid: uuid]
    }

    /**
     * 修改红外控制按钮
     */
    def editIrctrlBtns () {
        //法庭
        def courtroom = Courtroom.get(params.long("id"))
        //红外控制的一个的uuid
        def irctrlUid = params.get("irctrlUid") as String
        //此按钮的uuid
        def uuid = params.get("uuid") as String
        def name = "", codeDown = ""  //名称--可见状态--控制指令
        for (int i = 0; i < JSON.parse(courtroom.cfg).irctrl.size(); i++) {
            def irctrl = JSON.parse(courtroom.cfg).irctrl[i]
            if (irctrl.uuid == irctrlUid) {
                def buttons = irctrl.buttons
                for (int j=0; j<buttons.size();j++){
                    if (buttons[j].uuid == uuid) {
                        name = buttons[j].name
                        codeDown = buttons[j].codeDown
                    }
                }
                break
            }
        }
        [courtroom: courtroom,irctrlUid: irctrlUid, uuid: uuid, name: name, codeDown: codeDown]
    }

    /**
     * 添加新的红外控制按钮
     */
    def addSaveIrctrlBtn(){
        //法庭
        def courtroom = Courtroom.get(params.long("id"))
        //红外控制的一个的uuid
        def uuid = params.get("uuid") as String
        //按钮名称
        def name = params.get("name") as String
        //控制指令
        def codeDown = params.get("codeDown") as String
        if (!(uuid && name && codeDown)){
            render Resp.toJson(RespType.FAIL)
            return
        }
        def cfg = ctrlService.getCfg(courtroom)
        for (int i=0; i<cfg.irctrl.size(); i++){
            if (cfg.irctrl[i].uuid == uuid){
                if (!cfg.irctrl[i].buttons){
                    cfg.irctrl[i].buttons = []
                }
                cfg.irctrl[i].buttons.add([
                        uuid         : UUIDGenerator.nextUUID(),
                        //名称
                        name         : params.name,
                        //可见状态
                        codeDown     : params.codeDown
                ])
            }
        }
        courtroom.cfg = cfg as JSON
        courtroom.save(flush: true)
        render Resp.toJson(RespType.SUCCESS)
    }
    /**
     * 编辑红外控制按钮
     */
    def editSaveIrctrlBtn(){
        //法庭
        def courtroom = Courtroom.get(params.long("id"))
        //红外控制的一个的uuid
        def irctrlUid = params.get("irctrlUid") as String
        //此按钮的uuid
        def uuid = params.get("uuid") as String
        //按钮名称
        def name = params.get("name") as String
        //控制指令
        def codeDown = params.get("codeDown") as String
        if (!(courtroom && irctrlUid && uuid && name && codeDown)){
            render Resp.toJson(RespType.FAIL)
            return
        }
        def cfg = ctrlService.getCfg(courtroom)
        for (int i = 0; i < cfg.irctrl.size(); i++) {
            if (cfg.irctrl[i].uuid == irctrlUid) {
                def buttons = cfg.irctrl[i].buttons
                for (int j=0;j<buttons.size();j++){
                    if (buttons[j].uuid == uuid){
                        buttons[j].name = name
                        buttons[j].codeDown = codeDown
                    }
                }
                break
            }
        }
        courtroom.cfg = cfg as JSON
        courtroom.save(flush: true)
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 修改红外控制  执行
     */
    def editSaveIrctrl(){
        def courtroom = Courtroom.get(params.long("id"))
        def uuid = params.get("uuid") as String
        //名称
        def name = params.get("name") as String
        //可见状态
        def visible = params.get("visible") as String
        if (courtroom && uuid && name && visible){
            //法庭配置
            def cfg = ctrlService.getCfg(courtroom)
            for (int i=0; i<cfg.irctrl.size(); i++){
                if (cfg.irctrl[i].uuid == uuid){
                    //执行修改
                    cfg.irctrl[i].name = name
                    cfg.irctrl[i].visible = visible
                    break
                }
            }
            courtroom.cfg = cfg as JSON
            courtroomService.save(courtroom)
            render Resp.toJson(RespType.SUCCESS)
        }else{
            render Resp.toJson(RespType.FAIL)
        }
    }

    /**
     * 删除部分输出控制  执行
     */
    def delIrctrls(){
        def uuidsStr = params.get("uuids") as String
        if (!uuidsStr) {
            render Resp.toJson(RespType.DATA_NOT_EXIST)
            return
        }
        def courtroom = Courtroom.get(params.long("id"))
        if (uuidsStr && courtroom){
            //uuidStr数组
            def uuidsArr = uuidsStr.split(",")
            //法庭配置
            def cfg = ctrlService.getCfg(courtroom)
            ctrlService.delIrctrls(uuidsArr, cfg)
            courtroom.cfg = cfg as JSON
            courtroomService.save(courtroom)
            render Resp.toJson(RespType.SUCCESS)
        }else{
            render Resp.toJson(RespType.FAIL)
        }
    }
    /**
     * 删除部分红外控制按钮
     */
    def delIrctrlBtns() {
        def courtroom = Courtroom.get(params.long("id"))
        def irctrlUid = params.get("irctrlUid") as String
        def uuidsStr = params.get("uuids") as String
        if (!(courtroom && irctrlUid && uuidsStr)) {
            render Resp.toJson(RespType.FAIL)
            return
        }
        //uuidStr数组
        def uuidsArr = uuidsStr.split(",")
        //法庭配置
        def cfg = ctrlService.getCfg(courtroom)
        //遍历irctrl
        for (int i=0; i<cfg.irctrl.size(); i++){
            //找到对应的irctrl
            if (cfg.irctrl[i].uuid == irctrlUid){
                //遍历uuids
                for (String uuid:uuidsArr){
                    //遍历这个irctrl对应的buttons
                    for (int j=0; j<cfg.irctrl[i].buttons.size(); j++){
                        if (cfg.irctrl[i].buttons[j].uuid == uuid){
                            cfg.irctrl[i].buttons.remove(j)
                        }
                    }
                }
                break
            }
        }
        courtroom.cfg = cfg as JSON
        courtroom.save(flush: true)
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 添加音量控制
     */
    def addSound() {
        def courtroom = Courtroom.get(params.long("id"))
        [courtroom: courtroom]
    }

    /**
     * 添加音量控制  执行
     */
    def addSaveSound() {
        def courtroom = Courtroom.get(params.long("id"))
        def cfg = ctrlService.getCfg(courtroom)
        cfg.soundMatrix.add([
                uuid : UUIDGenerator.nextUUID(),
                //名称
                name : params.name,
                //指令
                codeDown : params.codeDown,
                //指令
                group : params.group,
                //可见状态
                visible: params.visible
        ])
        courtroom.cfg = cfg as JSON
        courtroomService.save(courtroom)
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 更新音量控制
     */
    def editSound(){
        def courtroom = Courtroom.get(params.long("id"))
        def uuid = params.get("uuid") as String
        def name="",codeDown="",group="",visible=""  //名称--控制指令--可见状态
        for(int i=0; i<JSON.parse(courtroom.cfg).soundMatrix.size(); i++){
            def soundMatrix = JSON.parse(courtroom.cfg).soundMatrix[i]
            if (soundMatrix.uuid==uuid){
                name = soundMatrix.name
                codeDown = soundMatrix.codeDown
                group = soundMatrix.group
                visible = soundMatrix.visible
                break
            }
        }
        [roomId:courtroom.id, uuid:uuid, name:name, codeDown:codeDown, group:group, visible:visible]
    }

    /**
     * 更新红外控制  执行
     */
    def editSaveSound(){
        def courtroom = Courtroom.get(params.long("id"))
        def uuid = params.get("uuid") as String
        //名称
        def name = params.get("name") as String
        //控制指令
        def codeDown = params.get("codeDown") as String
        //分组
        def group = params.get("group") as String
        //可见状态
        def visible = params.get("visible") as String
        if (courtroom && uuid && name && codeDown && group && visible){
            //法庭配置
            def cfg = ctrlService.getCfg(courtroom)
            for (int i=0; i<cfg.soundMatrix.size(); i++){
                if (cfg.soundMatrix[i].uuid == uuid){
                    //执行修改
                    cfg.soundMatrix[i].name = name
                    cfg.soundMatrix[i].codeDown = codeDown
                    cfg.soundMatrix[i].group = group
                    cfg.soundMatrix[i].visible = visible
                    break
                }
            }
            courtroom.cfg = cfg as JSON
            courtroomService.save(courtroom)
            render Resp.toJson(RespType.SUCCESS)
        }else{
            render Resp.toJson(RespType.FAIL)
        }
    }

    /**
     * 删除部分输出控制  执行
     */
    def delSounds(){
        def uuidsStr = params.get("uuids") as String
        if (!uuidsStr) {
            render Resp.toJson(RespType.DATA_NOT_EXIST)
            return
        }
        def courtroom = Courtroom.get(params.long("id"))
        if (uuidsStr && courtroom){
            //uuidStr数组
            def uuidsArr = uuidsStr.split(",")
            //法庭配置
            def cfg = ctrlService.getCfg(courtroom)
            ctrlService.delSounds(uuidsArr, cfg)
            courtroom.cfg = cfg as JSON
            courtroomService.save(courtroom)
            render Resp.toJson(RespType.SUCCESS)
        }else{
            render Resp.toJson(RespType.FAIL)
        }
    }

    /**
     * 添加综合控制
     */
    def addTotal() {
        def courtroom = Courtroom.get(params.long("id"))
        [courtroom: courtroom]
    }

    /**
     * 添加综合控制  执行
     */
    def addSaveTotal() {
        def courtroom = Courtroom.get(params.long("id"))
        def cfg = ctrlService.getCfg(courtroom)
        cfg.total.add([
                uuid : UUIDGenerator.nextUUID(),
                //名称
                name : params.name,
                //控制指令
                codeDown : params.codeDown,
                //发送时间
                sendTime : params.sendTime,
                //发送优先级
                sendPriority : params.sendPriority,
                //可见状态
                visible: params.visible
        ])
        courtroom.cfg = cfg as JSON
        courtroomService.save(courtroom)
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 更新综合控制
     */
    def editTotal(){
        def courtroom = Courtroom.get(params.long("id"))
        def uuid = params.get("uuid") as String
        def name="",codeDown="", sendTime="",sendPriority="",visible=""  //名称--控制指令--可见状态
        for(int i=0; i<JSON.parse(courtroom.cfg).total.size(); i++){
            def total = JSON.parse(courtroom.cfg).total[i]
            if (total.uuid==uuid){
                name = total.name
                codeDown = total.codeDown
                sendTime = total.sendTime
                sendPriority = total.sendPriority
                visible = total.visible
                break
            }
        }
        [roomId:courtroom.id, uuid:uuid, name:name, codeDown:codeDown, sendTime:sendTime, sendPriority:sendPriority, visible:visible]
    }

    /**
     * 更新综合控制  执行
     */
    def editSaveTotal(){
        def courtroom = Courtroom.get(params.long("id"))
        def uuid = params.get("uuid") as String
        //名称
        def name = params.get("name") as String
        //控制指令
        def codeDown = params.get("codeDown") as String
        //发送时间
        def sendTime = params.get("sendTime") as String
        //发送优先等级
        def sendPriority = params.get("sendPriority") as String
        //可见状态
        def visible = params.get("visible") as String
        if (courtroom && uuid && name && codeDown && sendTime && sendPriority && visible){
            //法庭配置
            def cfg = ctrlService.getCfg(courtroom)
            for (int i=0; i<cfg.total.size(); i++){
                if (cfg.total[i].uuid == uuid){
                    //执行修改
                    cfg.total[i].name = name
                    cfg.total[i].codeDown = codeDown
                    cfg.total[i].sendTime = sendTime
                    cfg.total[i].sendPriority = sendPriority
                    cfg.total[i].visible = visible
                    break
                }
            }
            courtroom.cfg = cfg as JSON
            courtroomService.save(courtroom)
            render Resp.toJson(RespType.SUCCESS)
        }else{
            render Resp.toJson(RespType.FAIL)
        }
    }

    /**
     * 删除部分输出控制  执行
     */
    def delTotals(){
        def uuidsStr = params.get("uuids") as String
        if (!uuidsStr) {
            render Resp.toJson(RespType.DATA_NOT_EXIST)
            return
        }
        def courtroom = Courtroom.get(params.long("id"))
        if (uuidsStr && courtroom){
            //uuidStr数组
            def uuidsArr = uuidsStr.split(",")
            //法庭配置
            def cfg = ctrlService.getCfg(courtroom)
            ctrlService.delTotals(uuidsArr, cfg)
            courtroom.cfg = cfg as JSON
            courtroomService.save(courtroom)
            render Resp.toJson(RespType.SUCCESS)
        }else{
            render Resp.toJson(RespType.FAIL)
        }
    }

    /**
     * 添加强电控制
     */
    def addPower() {
        def courtroom = Courtroom.get(params.long("id"))
        [courtroom: courtroom]
    }

    /**
     * 添加强电控制  执行
     */
    def addSavePower() {
        def courtroom = Courtroom.get(params.long("id"))
        def cfg = ctrlService.getCfg(courtroom)
        cfg.power.add([
                uuid : UUIDGenerator.nextUUID(),
                //名称
                name : params.name,
                //可见状态
                visible: params.visible,
                buttons       : []
        ])
        courtroom.cfg = cfg as JSON
        courtroomService.save(courtroom)
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 更新强电控制
     */
    def editPower(){
        def courtroom = Courtroom.get(params.long("id"))
        def uuid = params.get("uuid") as String
        def name="",visible=""  //名称--可见状态
        for(int i=0; i<JSON.parse(courtroom.cfg).power.size(); i++){
            def power = JSON.parse(courtroom.cfg).power[i]
            if (power.uuid==uuid){
                name = power.name
                visible = power.visible
                break
            }
        }
        [roomId:courtroom.id, uuid:uuid, name:name, visible:visible]
    }

    /**
     * 更新输出控制  执行
     */
    def editSavePower(){
        def courtroom = Courtroom.get(params.long("id"))
        def uuid = params.get("uuid") as String
        //名称
        def name = params.get("name") as String
        //可见状态
        def visible = params.get("visible") as String
        if (courtroom && uuid && name && visible){
            //法庭配置
            def cfg = ctrlService.getCfg(courtroom)
            for (int i=0; i<cfg.power.size(); i++){
                if (cfg.power[i].uuid == uuid){
                    //执行修改
                    cfg.power[i].name = name
                    cfg.power[i].visible = visible
                    break
                }
            }
            courtroom.cfg = cfg as JSON
            courtroomService.save(courtroom)
            render Resp.toJson(RespType.SUCCESS)
        }else{
            render Resp.toJson(RespType.FAIL)
        }
    }

    /**
     * 强电控制按钮列表
     */
    def powerBtns () {
        def courtroom = Courtroom.get(params.long("id"))
        def uuid = params.get("uuid") as String
        def button = null
        if (courtroom?.cfg) {
            def cfg = JSON.parse(courtroom.cfg)
            for (int i=0; i<cfg.power.size(); i++){
                if (cfg.power[i].uuid == uuid){
                    button = cfg.power[i].buttons
                }
            }
        }
        [courtroom: courtroom,uuid: uuid, button: button]
    }

    /**
     * 前往添加强电控制页面
     */
    def addPowerBtns () {
        //法庭
        def courtroom = Courtroom.get(params.long("id"))
        //强电控制的一个的uuid
        def uuid = params.get("uuid") as String
        [courtroom: courtroom,uuid: uuid]
    }

    /**
     * 修改强电控制按钮
     */
    def editPowerBtns () {
        //法庭
        def courtroom = Courtroom.get(params.long("id"))
        //强电控制的一个的uuid
        def powerUid = params.get("powerUid") as String
        //此按钮的uuid
        def uuid = params.get("uuid") as String
        def name = "", codeDown = ""  //名称--可见状态--控制指令
        for (int i = 0; i < JSON.parse(courtroom.cfg).power.size(); i++) {
            def power = JSON.parse(courtroom.cfg).power[i]
            if (power.uuid == powerUid) {
                def buttons = power.buttons
                for (int j=0; j<buttons.size();j++){
                    if (buttons[j].uuid == uuid) {
                        name = buttons[j].name
                        codeDown = buttons[j].codeDown
                    }
                }
                break
            }
        }
        [courtroom: courtroom,powerUid: powerUid, uuid: uuid, name: name, codeDown: codeDown]
    }

    /**
     * 添加新的强电控制按钮
     */
    def addSavePowerBtn(){
        //法庭
        def courtroom = Courtroom.get(params.long("id"))
        //强电控制的一个的uuid
        def uuid = params.get("uuid") as String
        //按钮名称
        def name = params.get("name") as String
        //控制指令
        def codeDown = params.get("codeDown") as String
        if (!(uuid && name && codeDown)){
            render Resp.toJson(RespType.FAIL)
            return
        }
        def cfg = ctrlService.getCfg(courtroom)
        for (int i=0; i<cfg.power.size(); i++){
            if (cfg.power[i].uuid == uuid){
                cfg.power[i].buttons.add([
                        uuid         : UUIDGenerator.nextUUID(),
                        //名称
                        name         : params.name,
                        //可见状态
                        codeDown     : params.codeDown
                ])
            }
        }
        courtroom.cfg = cfg as JSON
        courtroom.save(flush: true)
        render Resp.toJson(RespType.SUCCESS)
    }

    def editSavePowerBtn(){
        //法庭
        def courtroom = Courtroom.get(params.long("id"))
        //强电控制的一个的uuid
        def powerUid = params.get("powerUid") as String
        //此按钮的uuid
        def uuid = params.get("uuid") as String
        //按钮名称
        def name = params.get("name") as String
        //控制指令
        def codeDown = params.get("codeDown") as String
        if (!(courtroom && powerUid && uuid && name && codeDown)){
            render Resp.toJson(RespType.FAIL)
            return
        }
        def cfg = ctrlService.getCfg(courtroom)
        for (int i = 0; i < cfg.power.size(); i++) {
            if (cfg.power[i].uuid == powerUid) {
                def buttons = cfg.power[i].buttons
                for (int j=0;j<buttons.size();j++){
                    if (buttons[j].uuid == uuid){
                        buttons[j].name = name
                        buttons[j].codeDown = codeDown
                    }
                }
                break
            }
        }
        courtroom.cfg = cfg as JSON
        courtroom.save(flush: true)
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 删除部分输出控制  执行
     */
    def delPowers(){
        def uuidsStr = params.get("uuids") as String
        if (!uuidsStr) {
            render Resp.toJson(RespType.DATA_NOT_EXIST)
            return
        }
        def courtroom = Courtroom.get(params.long("id"))
        if (uuidsStr && courtroom){
            //uuidStr数组
            def uuidsArr = uuidsStr.split(",")
            //法庭配置
            def cfg = ctrlService.getCfg(courtroom)
            ctrlService.delPowers(uuidsArr, cfg)
            courtroom.cfg = cfg as JSON
            courtroomService.save(courtroom)
            render Resp.toJson(RespType.SUCCESS)
        }else{
            render Resp.toJson(RespType.FAIL)
        }
    }

    /**
     * 删除部分强电控制按钮
     */
    def delPowerBtns() {
        def courtroom = Courtroom.get(params.long("id"))
        def powerUid = params.get("powerUid") as String
        def uuidsStr = params.get("uuids") as String
        if (!(courtroom && powerUid && uuidsStr)) {
            render Resp.toJson(RespType.FAIL)
            return
        }
        //uuidStr数组
        def uuidsArr = uuidsStr.split(",")
        //法庭配置
        def cfg = ctrlService.getCfg(courtroom)
        //遍历power
        for (int i=0; i<cfg.power.size(); i++){
            //找到对应的power
            if (cfg.power[i].uuid == powerUid){
                //遍历uuids
                for (String uuid:uuidsArr){
                    //遍历这个power对应的buttons
                    for (int j=0; j<cfg.power[i].buttons.size(); j++){
                        if (cfg.power[i].buttons[j].uuid == uuid){
                            cfg.power[i].buttons.remove(j)
                        }
                    }
                }
                break
            }
        }
        courtroom.cfg = cfg as JSON
        courtroom.save(flush: true)
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 添加新电源控制
     */
    def addPowerNew() {
        def courtroom = Courtroom.get(params.long("id"))
        [courtroom: courtroom]
    }

    /**
     * 添加新电源控制  执行
     */
    def addSavePowerNew() {
        def courtroom = Courtroom.get(params.long("id"))
        def cfg = ctrlService.getCfg(courtroom)
        if (!cfg.powerNew){
            cfg.powerNew = []
        }
        cfg.powerNew.add([
                uuid : UUIDGenerator.nextUUID(),
                //名称
                name : params.name,
                //可见状态
                visible: params.visible,
                buttons       : []
        ])
        courtroom.cfg = cfg as JSON
        courtroomService.save(courtroom)
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 更新新电源控制
     */
    def editPowerNew(){
        def courtroom = Courtroom.get(params.long("id"))
        def uuid = params.get("uuid") as String
        def name="",visible=""  //名称--可见状态
        for(int i=0; i<JSON.parse(courtroom.cfg).powerNew.size(); i++){
            def powerNew = JSON.parse(courtroom.cfg).powerNew[i]
            if (powerNew.uuid==uuid){
                name = powerNew.name
                visible = powerNew.visible
                break
            }
        }
        [roomId:courtroom.id, uuid:uuid, name:name, visible:visible]
    }

    /**
     * 更新输出控制  执行
     */
    def editSavePowerNew(){
        def courtroom = Courtroom.get(params.long("id"))
        def uuid = params.get("uuid") as String
        //名称
        def name = params.get("name") as String
        //可见状态
        def visible = params.get("visible") as String
        if (courtroom && uuid && name && visible){
            //法庭配置
            def cfg = ctrlService.getCfg(courtroom)
            for (int i=0; i<cfg.powerNew.size(); i++){
                if (cfg.powerNew[i].uuid == uuid){
                    //执行修改
                    cfg.powerNew[i].name = name
                    cfg.powerNew[i].visible = visible
                    break
                }
            }
            courtroom.cfg = cfg as JSON
            courtroomService.save(courtroom)
            render Resp.toJson(RespType.SUCCESS)
        }else{
            render Resp.toJson(RespType.FAIL)
        }
    }

    /**
     * 新电源控制按钮列表
     */
    def powerNewBtns () {
        def courtroom = Courtroom.get(params.long("id"))
        def uuid = params.get("uuid") as String
        def button = null
        if (courtroom?.cfg) {
            def cfg = JSON.parse(courtroom.cfg)
            for (int i=0; i<cfg.powerNew.size(); i++){
                if (cfg.powerNew[i].uuid == uuid){
                    button = cfg.powerNew[i].buttons
                }
            }
        }
        [courtroom: courtroom,uuid: uuid, button: button]
    }

    /**
     * 前往添加新电源控制页面
     */
    def addPowerNewBtns () {
        //法庭
        def courtroom = Courtroom.get(params.long("id"))
        //新电源控制的一个的uuid
        def uuid = params.get("uuid") as String
        [courtroom: courtroom,uuid: uuid]
    }

    /**
     * 修改新电源控制按钮
     */
    def editPowerNewBtns () {
        //法庭
        def courtroom = Courtroom.get(params.long("id"))
        //新电源控制的一个的uuid
        def powerNewUid = params.get("powerNewUid") as String
        //此按钮的uuid
        def uuid = params.get("uuid") as String
        def name = "", codeDown = ""  //名称--可见状态--控制指令
        for (int i = 0; i < JSON.parse(courtroom.cfg).powerNew.size(); i++) {
            def powerNew = JSON.parse(courtroom.cfg).powerNew[i]
            if (powerNew.uuid == powerNewUid) {
                def buttons = powerNew.buttons
                for (int j=0; j<buttons.size();j++){
                    if (buttons[j].uuid == uuid) {
                        name = buttons[j].name
                        codeDown = buttons[j].codeDown
                    }
                }
                break
            }
        }
        [courtroom: courtroom,powerNewUid: powerNewUid, uuid: uuid, name: name, codeDown: codeDown]
    }

    /**
     * 添加新的新电源控制按钮
     */
    def addSavePowerNewBtn(){
        //法庭
        def courtroom = Courtroom.get(params.long("id"))
        //新电源控制的一个的uuid
        def uuid = params.get("uuid") as String
        //按钮名称
        def name = params.get("name") as String
        //控制指令
        def codeDown = params.get("codeDown") as String
        if (!(uuid && name && codeDown)){
            render Resp.toJson(RespType.FAIL)
            return
        }
        def cfg = ctrlService.getCfg(courtroom)
        for (int i=0; i<cfg.powerNew.size(); i++){
            if (cfg.powerNew[i].uuid == uuid){
                if (!cfg.powerNew[i].buttons){
                    cfg.powerNew[i].buttons = []
                }
                cfg.powerNew[i].buttons.add([
                        uuid         : UUIDGenerator.nextUUID(),
                        //名称
                        name         : params.name,
                        //可见状态
                        codeDown     : params.codeDown
                ])
            }
        }
        courtroom.cfg = cfg as JSON
        courtroom.save(flush: true)
        render Resp.toJson(RespType.SUCCESS)
    }

    def editSavePowerNewBtn(){
        //法庭
        def courtroom = Courtroom.get(params.long("id"))
        //新电源控制的一个的uuid
        def powerNewUid = params.get("powerNewUid") as String
        //此按钮的uuid
        def uuid = params.get("uuid") as String
        //按钮名称
        def name = params.get("name") as String
        //控制指令
        def codeDown = params.get("codeDown") as String
        if (!(courtroom && powerNewUid && uuid && name && codeDown)){
            render Resp.toJson(RespType.FAIL)
            return
        }
        def cfg = ctrlService.getCfg(courtroom)
        for (int i = 0; i < cfg.powerNew.size(); i++) {
            if (cfg.powerNew[i].uuid == powerNewUid) {
                def buttons = cfg.powerNew[i].buttons
                for (int j=0;j<buttons.size();j++){
                    if (buttons[j].uuid == uuid){
                        buttons[j].name = name
                        buttons[j].codeDown = codeDown
                    }
                }
                break
            }
        }
        courtroom.cfg = cfg as JSON
        courtroom.save(flush: true)
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 删除部分输出控制  执行
     */
    def delPowerNew(){
        def uuidsStr = params.get("uuids") as String
        if (!uuidsStr) {
            render Resp.toJson(RespType.DATA_NOT_EXIST)
            return
        }
        def courtroom = Courtroom.get(params.long("id"))
        if (uuidsStr && courtroom){
            //uuidStr数组
            def uuidsArr = uuidsStr.split(",")
            //法庭配置
            def cfg = ctrlService.getCfg(courtroom)
            ctrlService.delPowerNew(uuidsArr, cfg)
            courtroom.cfg = cfg as JSON
            courtroomService.save(courtroom)
            render Resp.toJson(RespType.SUCCESS)
        }else{
            render Resp.toJson(RespType.FAIL)
        }
    }

    /**
     * 删除部分新电源控制按钮
     */
    def delPowerNewBtns() {
        def courtroom = Courtroom.get(params.long("id"))
        def powerNewUid = params.get("powerNewUid") as String
        def uuidsStr = params.get("uuids") as String
        if (!(courtroom && powerNewUid && uuidsStr)) {
            render Resp.toJson(RespType.FAIL)
            return
        }
        //uuidStr数组
        def uuidsArr = uuidsStr.split(",")
        //法庭配置
        def cfg = ctrlService.getCfg(courtroom)
        //遍历powerNew
        for (int i=0; i<cfg.powerNew.size(); i++){
            //找到对应的powerNew
            if (cfg.powerNew[i].uuid == powerNewUid){
                //遍历uuids
                for (String uuid:uuidsArr){
                    //遍历这个powerNew对应的buttons
                    for (int j=0; j<cfg.powerNew[i].buttons.size(); j++){
                        if (cfg.powerNew[i].buttons[j].uuid == uuid){
                            cfg.powerNew[i].buttons.remove(j)
                        }
                    }
                }
                break
            }
        }
        courtroom.cfg = cfg as JSON
        courtroom.save(flush: true)
        render Resp.toJson(RespType.SUCCESS)
    }
    
    /**
     * 添加摄像头控制信息
     */
    def addCamera() {
        def courtroom = Courtroom.get(params.long("id"))
        [courtroom: courtroom]
    }

    /**
     * 添加摄像头控制信息  执行
     */
    def addSaveCamera() {
        def courtroom = Courtroom.get(params.long("id"))
        def cfg = ctrlService.getCfg(courtroom)
        cfg.camera.position.add([
                uuid : UUIDGenerator.nextUUID(),
                //名称
                name : params.name,
                //控制指令
                codeDown: params.codeDown,
                //可见状态
                visible: params.visible
        ])
        courtroom.cfg = cfg as JSON
        courtroomService.save(courtroom)
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 更新摄像头控制信息
     */
    def editCamera(){
        def courtroom = Courtroom.get(params.long("id"))
        def uuid = params.get("uuid") as String
        def name="",codeDown="",visible=""  //名称--可见状态
        for(int i=0; i<JSON.parse(courtroom.cfg).camera.position.size(); i++){
            def cp = JSON.parse(courtroom.cfg).camera.position[i]
            if (cp.uuid==uuid){
                name = cp.name
                codeDown = cp.codeDown
                visible = cp.visible
                break
            }
        }
        [roomId:courtroom.id, uuid:uuid, name:name, codeDown:codeDown, visible:visible]
    }

    /**
     * 更新摄像头控制信息  执行
     */
    def editSaveCamera(){
        def courtroom = Courtroom.get(params.long("id"))
        def uuid = params.get("uuid") as String
        //名称
        def name = params.get("name") as String
        //控制指令
        def codeDown = params.get("codeDown") as String
        //可见状态
        def visible = params.get("visible") as String
        if (courtroom && uuid && codeDown && name && visible){
            //法庭配置
            def cfg = ctrlService.getCfg(courtroom)
            for (int i=0; i<cfg.camera.position.size(); i++){
                if (cfg.camera.position[i].uuid == uuid){
                    //执行修改
                    cfg.camera.position[i].name = name
                    cfg.camera.position[i].codeDown = codeDown
                    cfg.camera.position[i].visible = visible
                    break
                }
            }
            courtroom.cfg = cfg as JSON
            courtroomService.save(courtroom)
            render Resp.toJson(RespType.SUCCESS)
        }else{
            render Resp.toJson(RespType.FAIL)
        }
    }

    /**
     * 删除部分摄像头控制信息  执行
     */
    def delCameras(){
        def uuidsStr = params.get("uuids") as String
        if (!uuidsStr) {
            render Resp.toJson(RespType.DATA_NOT_EXIST)
            return
        }
        def courtroom = Courtroom.get(params.long("id"))
        if (uuidsStr && courtroom){
            //uuidStr数组
            def uuidsArr = uuidsStr.split(",")
            //法庭配置
            def cfg = ctrlService.getCfg(courtroom)
            ctrlService.delCameras(uuidsArr, cfg)
            courtroom.cfg = cfg as JSON
            courtroomService.save(courtroom)
            render Resp.toJson(RespType.SUCCESS)
        }else{
            render Resp.toJson(RespType.FAIL)
        }
    }

    /**
     * 摄像头控制指令集合  展示
     */
    def buttons(){
        def courtroom = Courtroom.get(params.long("id"))
        def camera = null   //摄像机控制
        if (courtroom?.cfg) {
            def cfg = JSON.parse(courtroom.cfg)
            camera = cfg.camera
        }
        [courtroom: courtroom, camera: camera]
    }

    /**
     * 添加摄像头控制指令集合
     */
    def addButtons(){
        def courtroom = Courtroom.get(params.long("id"))
        [courtroom: courtroom]
    }

    /**
     * 添加摄像头控制指令集合  执行
     */
    def addSaveButtons() {
        def courtroom = Courtroom.get(params.long("id"))
        def cfg = ctrlService.getCfg(courtroom)
        cfg.camera.buttons.add([
                uuid : UUIDGenerator.nextUUID(),
                //名称
                name : params.name,
                //按下指令
                codeDown: params.codeDown,
                //放开指令
                codeUp: params.codeUp,
                //可见状态
                visible: params.visible,
                //图标
                img: params.img
        ])
        courtroom.cfg = cfg as JSON
        courtroomService.save(courtroom)
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 更新摄像头控制指令集合
     */
    def editButtons(){
        def courtroom = Courtroom.get(params.long("id"))
        def uuid = params.get("uuid") as String
        def name="",codeDown="",codeUp="",visible="",img=""  //名称--按下指令--放开指令--可见状态--图标
        for(int i=0; i<JSON.parse(courtroom.cfg).camera.buttons.size(); i++){
            def cb = JSON.parse(courtroom.cfg).camera.buttons[i]
            if (cb.uuid==uuid){
                name = cb.name
                codeDown = cb.codeDown
                codeUp = cb.codeUp
                visible = cb.visible
                img = cb.img
                break
            }
        }
        [roomId:courtroom.id, uuid:uuid, name:name, codeDown:codeDown, codeUp: codeUp, visible:visible, img: img]
    }

    /**
     * 修改摄像头控制指令集合  执行
     */
    def editSaveButtons(){
        def courtroom = Courtroom.get(params.long("id"))
        def uuid = params.get("uuid") as String
        //名称
        def name = params.get("name") as String
        //按下指令
        def codeDown = params.get("codeDown") as String
        //放开指令
        def codeUp = params.get("codeUp") as String
        //可见状态
        def visible = params.get("visible") as String
        //图标
        def img = params.get("img") as String
        if (courtroom && uuid && codeDown && codeUp && name && visible && img){
            //法庭配置
            def cfg = ctrlService.getCfg(courtroom)
            for (int i=0; i<cfg.camera.buttons.size(); i++){
                if (cfg.camera.buttons[i].uuid == uuid){
                    //执行修改
                    cfg.camera.buttons[i].name = name
                    cfg.camera.buttons[i].codeDown = codeDown
                    cfg.camera.buttons[i].codeUp = codeUp
                    cfg.camera.buttons[i].visible = visible
                    cfg.camera.buttons[i].img = img
                    break
                }
            }
            courtroom.cfg = cfg as JSON
            courtroomService.save(courtroom)
            render Resp.toJson(RespType.SUCCESS)
        }else{
            render Resp.toJson(RespType.FAIL)
        }
    }

    /**
     * 删除部分摄像头控制信息  执行
     */
    def delButtons(){
        def uuidsStr = params.get("uuids") as String
        if (!uuidsStr) {
            render Resp.toJson(RespType.DATA_NOT_EXIST)
            return
        }
        def courtroom = Courtroom.get(params.long("id"))
        if (uuidsStr && courtroom){
            //uuidStr数组
            def uuidsArr = uuidsStr.split(",")
            //法庭配置
            def cfg = ctrlService.getCfg(courtroom)
            ctrlService.delButtons(uuidsArr, cfg)
            courtroom.cfg = cfg as JSON
            courtroomService.save(courtroom)
            render Resp.toJson(RespType.SUCCESS)
        }else{
            render Resp.toJson(RespType.FAIL)
        }
    }

    /**
     * 摄像头预置位信息  展示
     */
    def presets(){
        def courtroom = Courtroom.get(params.long("id"))
        def camera = null   //摄像机控制
        if (courtroom?.cfg) {
            def cfg = JSON.parse(courtroom.cfg)
            camera = cfg.camera
        }
        [courtroom: courtroom, camera: camera]
    }

    /**
     * 添加摄像头预置位信息
     */
    def addPresets(){
        def courtroom = Courtroom.get(params.long("id"))
        [courtroom: courtroom]
    }

    /**
     * 添加摄像头预置位信息  执行
     */
    def addSavePresets() {
        def courtroom = Courtroom.get(params.long("id"))
        def cfg = ctrlService.getCfg(courtroom)
        cfg.camera.presets.add([
                uuid : UUIDGenerator.nextUUID(),
                //名称
                name : params.name,
                //保存指令
                save: params.save,
                //调用指令
                call: params.call
        ])
        courtroom.cfg = cfg as JSON
        courtroomService.save(courtroom)
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 更新摄像头预置位信息
     */
    def editPresets(){
        def courtroom = Courtroom.get(params.long("id"))
        def uuid = params.get("uuid") as String
        def name="",save="",call=""  //名称--保存指令--调用指令
        for(int i=0; i<JSON.parse(courtroom.cfg).camera.presets.size(); i++){
            def cb = JSON.parse(courtroom.cfg).camera.presets[i]
            if (cb.uuid==uuid){
                name = cb.name
                save = cb.save
                call = cb.call
                break
            }
        }
        [roomId: courtroom.id, uuid: uuid, name: name, save: save, call: call]
    }

    /**
     * 修改摄像头控制指令集合  执行
     */
    def editSavePresets(){
        def courtroom = Courtroom.get(params.long("id"))
        def uuid = params.get("uuid") as String
        //预置位名称
        def name = params.get("name") as String
        //保存指令
        def save = params.get("save") as String
        //调用指令
        def call = params.get("call") as String
        if (courtroom && name && uuid && save && call){
            //法庭配置
            def cfg = ctrlService.getCfg(courtroom)
            for (int i=0; i<cfg.camera.presets.size(); i++){
                if (cfg.camera.presets[i].uuid == uuid){
                    //执行修改
                    cfg.camera.presets[i].name = name
                    cfg.camera.presets[i].save = save
                    cfg.camera.presets[i].call = call
                    break
                }
            }
            courtroom.cfg = cfg as JSON
            courtroomService.save(courtroom)
            render Resp.toJson(RespType.SUCCESS)
        }else{
            render Resp.toJson(RespType.SUCCESS)
        }
    }

    /**
     * 删除部分摄像头控制信息  执行
     */
    def delPresets(){
        def uuidsStr = params.get("uuids") as String
        if (!uuidsStr) {
            render Resp.toJson(RespType.DATA_NOT_EXIST)
            return
        }
        def courtroom = Courtroom.get(params.long("id"))
        if (uuidsStr && courtroom){
            //uuidStr数组
            def uuidsArr = uuidsStr.split(",")
            //法庭配置
            def cfg = ctrlService.getCfg(courtroom)
            ctrlService.delPresets(uuidsArr, cfg)
            courtroom.cfg = cfg as JSON
            courtroomService.save(courtroom)
            render Resp.toJson(RespType.SUCCESS)
        }else{
            render Resp.toJson(RespType.FAIL)
        }
    }
}
