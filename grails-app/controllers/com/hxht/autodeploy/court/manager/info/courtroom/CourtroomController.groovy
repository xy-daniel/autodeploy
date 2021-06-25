package com.hxht.autodeploy.court.manager.info.courtroom

import com.hxht.techcrt.DataStatus
import com.hxht.techcrt.Resp
import com.hxht.techcrt.court.Courtroom
import com.hxht.techcrt.court.PlanInfo
import com.hxht.techcrt.enums.RespType
import com.hxht.techcrt.utils.UUIDGenerator
import grails.converters.JSON
import grails.events.EventPublisher

/**
 * 法庭相关功能
 * 2021.04.19 >>> 增加远程提讯开关 daniel
 * 2021.04.23 >>> 添加或编辑法庭时去掉rtsp两端的空格 daniel
 */
class CourtroomController implements EventPublisher{
    CourtroomService courtroomService

    def list() {
        if (request.method == "POST") {
            def draw = params.int("draw") ?: 1// 记录操作的次数 每次加1
            def start = params.int("start") ?: 0// 起始
            def length = params.int("length") ?: 20// 每页显示的size
            def search = params.get("search[value]") as String//搜索内容
            def model = courtroomService.list(draw, start, length, search)
            render model as JSON
        }else{
            ["isAllow": grailsApplication.config.getProperty('tc.remote.report.is-allow')]
        }
    }

    /**
     * 添加新法庭
     */
    def add() {}

    /**
     * 保存新添加的法庭
     */
    def addSave() {
        def courtroom = new Courtroom(params)
        courtroom.rtsp = courtroom.rtsp.trim()
        courtroom.rtsp1 = courtroom.rtsp1.trim()
        courtroom.uid = UUIDGenerator.nextUUID()
        courtroom.active = DataStatus.SHOW
        courtroom.cfg = "{\"encode\":[{\"uuid\":\"953e32acd9774f73957a192d2f3b5eec\",\"name\":\"合成图像\",\"encodeip\":\"127.0.0.1\",\"number\":\"1\",\"record\":\"1\",\"order\":\"1\"},{\"uuid\":\"401e784045ae4663a252787f9ffd2f1e\",\"name\":\"激励图像\",\"encodeip\":\"127.0.0.1\",\"number\":\"2\",\"record\":\"1\",\"order\":\"2\"}],\"vgaMatrix\":[{\"uuid\":\"47591177393e4de998367bdcbb46cf08\",\"name\":\"实物展台\",\"codeDown\":\"E05.\",\"visible\":\"1\"},{\"uuid\":\"9956ca4c5b15491d86e75b3682582a0b\",\"name\":\"公诉证据\",\"codeDown\":\"E06.\",\"visible\":\"1\"},{\"uuid\":\"f4d7dd881b644720b5d6393427854cd5\",\"name\":\"辩护证据\",\"codeDown\":\"E07.\",\"visible\":\"1\"},{\"uuid\":\"ac2b8b2d2ffa48e2aa7a08c60b8e9c20\",\"name\":\"展台/电脑\",\"codeDown\":\"E08.\",\"visible\":\"1\"},{\"uuid\":\"4d9e7969aa1a4d5aad7865916e78b614\",\"name\":\"DVD视频\",\"codeDown\":\"E04.\",\"visible\":\"1\"},{\"uuid\":\"53cbba89f0854b6cae3f08275ed1fbd9\",\"name\":\"视频终端\",\"codeDown\":\"E03.\",\"visible\":\"1\"}],\"total\":[{\"uuid\":\"d4e5696c5cde4de5b284141a8abcb879\",\"name\":\"一键开庭\",\"codeDown\":\"SYSTEM-ON.\",\"sendStatus\":\"1\",\"sendPriority\":\"1\",\"visible\":\"0\",\"sendTime\":\"0001/01/01 00:00\"},{\"uuid\":\"beb6654fe2cc4caab37ce6ab5547ae12\",\"name\":\"一键闭庭\",\"codeDown\":\"SYSTEM-OFF.\",\"sendStatus\":\"3\",\"sendPriority\":\"1\",\"visible\":\"1\"},{\"uuid\":\"368d3ac04acf4cfe9e4a408ff7af455c\",\"name\":\"语音激励开\",\"codeDown\":\"JL_ON.\",\"sendStatus\":null,\"sendPriority\":null,\"visible\":\"1\"},{\"uuid\":\"64a09a5e5fb54d3bb93c12f1a4ba729f\",\"name\":\"语音激励关\",\"codeDown\":\"JL_OFF.\",\"sendStatus\":null,\"sendPriority\":null,\"visible\":\"1\"},{\"uuid\":\"332aea8814054af79f61fe15b8cf6999\",\"name\":\"远程嫌疑人\",\"codeDown\":\"YCXYR.\",\"sendStatus\":\"5\",\"sendPriority\":\"1\",\"visible\":\"1\"},{\"uuid\":\"00d54afde6b34e4a8f5721739695de4a\",\"name\":\"远程证人\",\"codeDown\":\"YCZR.\",\"sendStatus\":\"5\",\"sendPriority\":\"2\",\"visible\":\"1\"}],\"outMatrix\":[{\"uuid\":\"7f1002b5c4f3457589d8c5a61d3be12e\",\"name\":\"所有显示\",\"codeDown\":\"TV0-\",\"visible\":\"1\"},{\"uuid\":\"8c89fa3da7f94616ad4ad91516558e4b\",\"name\":\"电视左\",\"codeDown\":\"TV1-\",\"visible\":\"1\"},{\"uuid\":\"798d038f3504484aa9498eb3d5747570\",\"name\":\"电视右\",\"codeDown\":\"TV2-\",\"visible\":\"1\"},{\"uuid\":\"1d4dcaa86ccc459a9145320c3eab25f5\",\"name\":\"法官左\",\"codeDown\":\"TV3-\",\"visible\":\"1\"},{\"uuid\":\"93f9ead3e5624f12a53d7ead702278ea\",\"name\":\"法官右\",\"codeDown\":\"TV4-\",\"visible\":\"1\"},{\"uuid\":\"b74783fd5d124d8b931f63b04f13f55d\",\"name\":\"公诉\",\"codeDown\":\"TV5-\",\"visible\":\"1\"},{\"uuid\":\"ac47c461776744b68fcb979728f80a0c\",\"name\":\"辩护\",\"codeDown\":\"TV6-\",\"visible\":\"1\"},{\"uuid\":\"6c7e144aa9954a35bfeea3633641148c\",\"name\":\"终端激励画面\",\"codeDown\":\"TV7-\",\"visible\":\"0\"},{\"uuid\":\"1a6d405f04bc489f805a4ccc243001ed\",\"name\":\"终端证据画面\",\"codeDown\":\"TV8-\",\"visible\":\"0\"}],\"irctrl\":[{\"uuid\":\"369a6eaad2e14fc1a1f3d354a2836fce\",\"name\":\"电视1\",\"visible\":\"1\",\"buttons\":[{\"uuid\":\"a2433c17be9846ea987bca8e18103cd4\",\"name\":\"开/关\",\"codeDown\":\"TV1ON.\"}]},{\"uuid\":\"8f79673e208a4eb9abd9f69345cab5aa\",\"name\":\"电视2\",\"visible\":\"1\",\"buttons\":[{\"uuid\":\"6eea6522beda415e864b52f0970ed081\",\"name\":\"开/关\",\"codeDown\":\"TV2ON.\"}]},{\"uuid\":\"560fe172605f4c80a3185ac1203f7953\",\"name\":\"电视3\",\"visible\":\"0\",\"buttons\":[{\"uuid\":\"b25730185b164c21b3fdf7c04cc466d8\",\"name\":\"开/关\",\"codeDown\":\"TV3ON.\"}]},{\"uuid\":\"c9337cc302bf4e539f0cb68afb26ae6b\",\"name\":\"电视4\",\"visible\":\"0\",\"buttons\":[{\"uuid\":\"e55e073ac4b5472bad748e01a0478cd1\",\"name\":\"开/关\",\"codeDown\":\"TV4ON.\"}]},{\"uuid\":\"e2b0addbbf6c421fb40a7f072911567f\",\"name\":\"DVD\",\"visible\":\"1\",\"buttons\":[{\"uuid\":\"5b69be6bfd014a8eb68321b73ee936fe\",\"name\":\"开仓\",\"codeDown\":\"DVDOPEN.\"},{\"uuid\":\"576b123edf1e4135ab66c2018dacf2e6\",\"name\":\"播放\",\"codeDown\":\"DVDPLAY.\"},{\"uuid\":\"22c9c0924d69495b8ca517cd76a52a6c\",\"name\":\"暂停\",\"codeDown\":\"DVDPAUSE.\"},{\"uuid\":\"aa0d97bf3a2949db8319954103b28dfa\",\"name\":\"上一段\",\"codeDown\":\"DVDPRE.\"},{\"uuid\":\"52f8fc2d7958428ca8bc517015dfca3e\",\"name\":\"下一段\",\"codeDown\":\"DVDNEXT.\"},{\"uuid\":\"d08f135f27024e59a9457efa70080100\",\"name\":\"快进\",\"codeDown\":\"DVDFAST.\"},{\"uuid\":\"c029961bd3b7416ea106f073b6532b29\",\"name\":\"快退\",\"codeDown\":\"DVDBACK.\"},{\"uuid\":\"e6c4d0fe12294304b8b6460f847a11ca\",\"name\":\"声道\",\"codeDown\":\"DVDVOL.\"},{\"uuid\":\"f2bc36732c9d4befb71744a108951f8d\",\"name\":\"音量+\",\"codeDown\":\"DVDVOLUP.\"},{\"uuid\":\"93f8b403f80b47fe9d8f6a312ee9e6b6\",\"name\":\"音量-\",\"codeDown\":\"DVDVOLDW.\"},{\"uuid\":\"7664847da6184c1e895846cde5008e03\",\"name\":\"电源\",\"codeDown\":\"DVDON.\"},{\"uuid\":\"507479f41fe646628b70e37c2e2a005f\",\"name\":\"关仓\",\"codeDown\":\"DVDCLOSE.\"},{\"uuid\":\"38ed3cc696ae40369bc0c0bd694fb34e\",\"name\":\"停止\",\"codeDown\":\"DVDSTOP.\"},{\"uuid\":\"efcf862a74eb4779a4032843e571acfb\",\"name\":\"返回\",\"codeDown\":\"DVDRETURN.\"}]}],\"power\":[{\"uuid\":\"77788eb59312415ababfe34af7ca9683\",\"name\":\"所有设备\",\"visible\":\"1\",\"buttons\":[{\"uuid\":\"df70e19a57a44e5d9e86a0b27fd0c47a\",\"name\":\"全开\",\"codeDown\":\"PWON.\"},{\"uuid\":\"772f02cecb78442e81d931ffcf8f55e9\",\"name\":\"全关\",\"codeDown\":\"PWOFF.\"}]},{\"uuid\":\"85c28958c4924880a86966530c7b5e94\",\"name\":\"所有电视\",\"visible\":\"1\",\"buttons\":[{\"uuid\":\"dd81db9fdc56402fbbff71c1dd8a3eff\",\"name\":\"全开\",\"codeDown\":\"TVON.\"},{\"uuid\":\"96bf95d054a94701966dd2ac128e6c41\",\"name\":\"全关\",\"codeDown\":\"TVOFF.\"}]}],\"decode\":[],\"camera\":{\"buttons\":[{\"uuid\":\"1e6b5a9153c34ffe85db3423197eb3fd\",\"name\":\"向上\",\"codeDown\":\"CAM-UP.\",\"codeUp\":\"CAM-STOP.\",\"visible\":\"1\"},{\"uuid\":\"b6d9084300134e918637c1d18951e431\",\"name\":\"向左\",\"codeDown\":\"CAM-LEFT.\",\"codeUp\":\"CAM-STOP.\",\"visible\":\"1\"},{\"uuid\":\"6dcaca5937e74f5baa6b2e1d4feb5e95\",\"name\":\"向右\",\"codeDown\":\"CAM-RIGHT.\",\"codeUp\":\"CAM-STOP.\",\"visible\":\"1\"},{\"uuid\":\"32abea21d98c407eb44247b932e52982\",\"name\":\"光圈+\",\"codeDown\":\"CAM-APERTURE-UP.\",\"codeUp\":\"CAM-STOP.\",\"visible\":\"1\"},{\"uuid\":\"28c5adbe1c0144b78fa77c93db08cf78\",\"name\":\"光圈-\",\"codeDown\":\"CAM-APERTURE-DOWN.\",\"codeUp\":\"CAM-STOP.\",\"visible\":\"1\"},{\"uuid\":\"d26c27f4aae04fdbb3bd7068cbb0e84d\",\"name\":\"焦距+\",\"codeDown\":\"CAM-ZOOM-DOWN.\",\"codeUp\":\"CAM-STOP.\",\"visible\":\"1\"},{\"uuid\":\"1122860e92ee469db86be211fbcabfb6\",\"name\":\"焦距-\",\"codeDown\":\"CAM-ZOOM-UP.\",\"codeUp\":\"CAM-STOP.\",\"visible\":\"1\"},{\"uuid\":\"b719466ab90145e2b4fc8b2747ac5387\",\"name\":\"变倍+\",\"codeDown\":\"CAM-NEAR.\",\"codeUp\":\"CAM-STOP.\",\"visible\":\"1\"},{\"uuid\":\"a768e1915fde49c98da964782d441ba1\",\"name\":\"变倍-\",\"codeDown\":\"CAM-FAR.\",\"codeUp\":\"CAM-STOP.\",\"visible\":\"1\"},{\"uuid\":\"8e46d476f0aa48fbbffa6ea16c127731\",\"name\":\"123\",\"codeDown\":\"123\",\"codeUp\":\"123\",\"visible\":\"1\",\"img\":\"123\"}],\"presets\":[{\"uuid\":\"3861a1a7110741c5afd853f515531389\",\"name\":\"预置位1\",\"save\":\"CAM-SAVE1.\",\"call\":\"CAM-CALL1.\"},{\"uuid\":\"5a8a4570b85c479eb566f3764144a0d1\",\"name\":\"预置位2\",\"save\":\"CAM-SAVE2.\",\"call\":\"CAM-CALL2.\"},{\"uuid\":\"2845be7985774e3ca8cac328a5dc18f3\",\"name\":\"预置位3\",\"save\":\"CAM-SAVE3.\",\"call\":\"CAM-CALL3.\"}],\"position\":[{\"uuid\":\"b6359f9968fe436bbd96f82a3cb10729\",\"name\":\"法官\",\"codeDown\":\"CAM1-\",\"visible\":\"1\"},{\"uuid\":\"a873fb7bdfa845f7a2383e25b60da569\",\"name\":\"公诉\",\"codeDown\":\"CAM2-\",\"visible\":\"1\"},{\"uuid\":\"70b6370c7df74585a26e7cc4ac8ea806\",\"name\":\"辩护\",\"codeDown\":\"CAM3-\",\"visible\":\"1\"},{\"uuid\":\"57b9e422aad84226b39ac807b05ebf1e\",\"name\":\"嫌疑人\",\"codeDown\":\"CAM4-\",\"visible\":\"1\"},{\"uuid\":\"089a7fc7b7a34c55adaebf3432fc9208\",\"name\":\"证人\",\"codeDown\":\"CAM5-\",\"visible\":\"1\"},{\"uuid\":\"4d360483cda64f9c9117a6cc73098562\",\"name\":\"全景\",\"codeDown\":\"CAM6-\",\"visible\":\"1\"}]},\"videoMatrix\":[{\"uuid\":\"5683fbedcd39472aafbbe55365049ceb\",\"name\":\"合成图像\",\"codeDown\":\"HCHM.\",\"visible\":\"1\"},{\"uuid\":\"59bbb131e0fb433c8ef5009380f1e98d\",\"name\":\"激励图像\",\"codeDown\":\"JLHM.\",\"visible\":\"1\"},{\"uuid\":\"7c45610aa50a44b3ad02ffdf8971f4c8\",\"name\":\"法官图像\",\"codeDown\":\"CAM1.\",\"visible\":\"1\"},{\"uuid\":\"356a311d9caf458b816e1a3c963d63f6\",\"name\":\"公诉图像\",\"codeDown\":\"CAM2.\",\"visible\":\"1\"},{\"uuid\":\"0fc0676fab634e229205a01749cd60b3\",\"name\":\"辩护图像\",\"codeDown\":\"CAM3.\",\"visible\":\"1\"},{\"uuid\":\"3b2a9e4ebdac4c61a2a952cb997cb7a9\",\"name\":\"嫌疑人图像\",\"codeDown\":\"CAM4.\",\"visible\":\"1\"},{\"uuid\":\"f1e6e1dfa37a468aae210dae06ddd937\",\"name\":\"证人图像\",\"codeDown\":\"CAM5.\",\"visible\":\"1\"},{\"uuid\":\"716127c62fb242538e41c3f55854c021\",\"name\":\"全景图像\",\"codeDown\":\"CAM6.\",\"visible\":\"1\"},{\"uuid\":\"4e76e4b3ee0d4711b5c1e8d23b68db1b\",\"name\":\"远程图像1\",\"codeDown\":\"DECODE1.\",\"visible\":\"1\"},{\"uuid\":\"2ab6067b1d0b4fa6bdc317c3e57959d9\",\"name\":\"远程图像2\",\"codeDown\":\"DECODE2.\",\"visible\":\"1\"}],\"soundMatrix\":[{\"uuid\":\"05faccb1756d40a39e8d69d131738f22\",\"name\":\"音量全升\",\"codeDown\":\"ALLMICUP.\",\"group\":\"1_2\",\"visible\":\"1\"},{\"uuid\":\"47393f2b214c4887b78e17b4b4bb1aa7\",\"name\":\"音量全降\",\"codeDown\":\"ALLMICDOWN.\",\"group\":\"1_3\",\"visible\":\"1\"},{\"uuid\":\"c12e73a9503d4b6284f3d4012904e2b6\",\"name\":\"音量全开\",\"codeDown\":\"ALLMICNOMUTE.\",\"group\":\"1_4\",\"visible\":\"1\"},{\"uuid\":\"1dd873b6f0694097a39560e612619401\",\"name\":\"麦克1关\",\"codeDown\":\"MIC1MUTE.\",\"group\":\"2_1\",\"visible\":\"1\"},{\"uuid\":\"7e1317e6e9c741f6a5b3a1899d294317\",\"name\":\"麦克1升\",\"codeDown\":\"MIC1UP.\",\"group\":\"2_2\",\"visible\":\"1\"},{\"uuid\":\"b52c480d6b594430aee38b634031e8dc\",\"name\":\"麦克1降\",\"codeDown\":\"MIC1DOWN.\",\"group\":\"2_3\",\"visible\":\"1\"},{\"uuid\":\"d1c7998d6ddb4219ab457a3c045f2aa8\",\"name\":\"麦克1开\",\"codeDown\":\"MIC1NOMUTE.\",\"group\":\"2_4\",\"visible\":\"1\"},{\"uuid\":\"5f4a9c84b79e497c806eba39a77f732a\",\"name\":\"麦克2关\",\"codeDown\":\"MIC2MUTE.\",\"group\":\"3_1\",\"visible\":\"1\"},{\"uuid\":\"ea3e8181151c4a5baeaa5cd99a090668\",\"name\":\"麦克2升\",\"codeDown\":\"MIC2UP.\",\"group\":\"3_2\",\"visible\":\"1\"},{\"uuid\":\"58223e7309c94a53908608a17dec9cdd\",\"name\":\"麦克2降\",\"codeDown\":\"MIC2DOWN.\",\"group\":\"3_3\",\"visible\":\"1\"},{\"uuid\":\"6173bed5592449dea37495c4ecfd4e6f\",\"name\":\"麦克2开\",\"codeDown\":\"MIC1NOMUTE.\",\"group\":\"3_4\",\"visible\":\"1\"},{\"uuid\":\"ade316c8fb034f1c8d51e3929fddf61a\",\"name\":\"1231\",\"codeDown\":\"123\",\"group\":\"123\",\"visible\":\"1\"}]}"
        courtroomService.save(courtroom)
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 修改法庭
     */
    def edit() {
        def courtroom = Courtroom.get(params.long("id"))
        [courtroom: courtroom]
    }

    /**
     * 保存修改的法庭
     */
    def editSave() {
        def courtroom = Courtroom.get(params.long("id"))
        //法庭名称
        courtroom.name = params.get("name") as String
        //直播服务地址
        courtroom.liveIp = params.get("liveIp") as String
        //直播服务端口
        courtroom.livePort = params.get("livePort") as String
        //设备通讯地址
        courtroom.deviceIp = params.get("deviceIp") as String
        //设备通信类型
        courtroom.deviceType = params.get("deviceType") as String
        //设备通信端口
        courtroom.devicePort = params.get("devicePort") as String
        //存储地址
        courtroom.storeIp = params.get("storeIp") as String
        //送远程地址1----双方远程只用这一个
        courtroom.rtsp = (params.get("rtsp") as String).trim()
        //三方远程送远程地址
        courtroom.rtsp1 = (params.get("rtsp1") as String).trim()
        //法庭状态
        courtroom.status = params.int("status")
        //法庭状态
        courtroom.isCalled = params.int("isCalled")
        //排序
        courtroom.sequence = params.int("sequence")
        //数据状态
        courtroom.active = DataStatus.SHOW
        //修改时间
        courtroom.lastUpdated = new Date()
        courtroom.save(flush: true)
        if (courtroom.hasErrors()) {
            log.info("CourtroomController.editSave courtroom [${courtroom.errors}]")
            render Resp.toJson(RespType.FAIL)
        }
        //核查系统对接
        this.notify("deviceIpToVc", courtroom.name, courtroom.deviceIp)
        render Resp.toJson(RespType.SUCCESS)
    }

    /**
     * 多法庭删除
     */
    def del() {
        def roomIdsStr = params.get("roomIds") as String
        if (!roomIdsStr) {
            render Resp.toJson(RespType.DATA_NOT_EXIST)
            return
        }
        def roomIdsArr = roomIdsStr.split(",")
        def num = 0
        for (String roomId : roomIdsArr) {
            //根据主键获取法庭信息
            def courtroom = Courtroom.get(roomId as long)
            //根据这个法庭获取排期信息
            def count = PlanInfo.countByCourtroom(courtroom)
            if (count > 0) {
                //如果存在子类型不进行删除
                num++
            } else {
                //如果不存在子类型则进行删除
                courtroom.delete(flush: true)
            }
        }
        if (num > 0) {
            //如果存在部分类型无法删除则给与提示
            render Resp.toJson(RespType.FAIL)
        } else {
            //所选案件类型完全删除
            render Resp.toJson(RespType.SUCCESS)
        }
    }
}
