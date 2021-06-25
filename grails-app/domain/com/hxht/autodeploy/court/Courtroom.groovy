package com.hxht.autodeploy.court

/**
 * 2021.04.19(23) >>> 增加远程提讯开关,修改远程提讯策略 daniel
 */
class Courtroom {

    //唯一编号
    String uid
    //法庭名称
    String name
    //直播服务地址
    String liveIp
    //直播服务端口
    String livePort
    //庭审主机地址
    String deviceIp
    //设备通信类型
    String deviceType
    //设备通信端口
    String devicePort
    //存储服务地址
    String storeIp
    //法庭配置信息
    String cfg
    //送远程地址，双方远程只用这一个
    String rtsp
    //三方远程送远程地址
    String rtsp1
    //判断此法庭是否可以被远程调用(深圳中院专用  是：1 否：0)
    Integer remote
    //标识此法庭是否能被远程提讯
    Integer isCalled
    //远程提讯占位符
    String distance
    //法庭状态，用来指房间是否正常。删除。损坏、远程被占用
    Integer status
    //排序
    Integer sequence
    //判断法庭是否为公开庭
    Integer open
    //同步罗湖法庭用的Id
    String sycLuoHuId
    //标识罗湖法庭传过来的 不为空则为传过来的值
    String luohuFlag
    //数据状态
    Integer active
    //创建时间
    Date dateCreated
    //修改时间
    Date lastUpdated

    static hasMany = [devices: CourtroomDevice]

    @Override
    String toString() {
        return this.name
    }
    static constraints = {
        uid maxSize: 64
        name maxSize: 250
        liveIp nullable: true, maxSize: 250
        livePort nullable: true, maxSize: 8
        deviceIp nullable: true, maxSize: 250
        deviceType nullable: true, maxSize: 5
        devicePort nullable: true, maxSize: 6
        storeIp nullable: true, maxSize: 250
        cfg nullable: true
        status nullable: true
        remote nullable: true
        sequence nullable: true
        sycLuoHuId nullable: true, maxSize: 50
        luohuFlag nullable: true, maxSize: 10
        open nullable: true, maxSize: 10
        rtsp nullable: true
        rtsp1 nullable: true
        isCalled nullable: true
        distance nullable: true, maxSize: 1000
    }
    static mapping = {
        uid comment: "唯一编号", index: true
        name comment: "法庭名称"
        liveIp comment: "直播服务地址"
        livePort comment: "直播服务端口"
        deviceIp comment: "庭审设备通信地址"
        deviceType comment: "庭审设备通信类型"
        devicePort comment: "庭审设备通信端口"
        storeIp comment: "存储服务地址"
        cfg sqlType: "text", comment: "配置信息"
        status comment: "法庭的状态 0：停止使用 1：正常使用 3：非正常状态 4: 远程庭审占用"
        remote comment: "判断此法庭是否可以被远程调用 0 否 1 是"
        open comment: "判断此法庭是否是公开庭 0 否 1 是"
        sequence comment: "排序"
        active comment: "数据状态"
        dateCreated comment: "创建时间"
        lastUpdated comment: "修改时间"
        sycLuoHuId comment: "同步罗湖法庭用的id"
        luohuFlag comment: "标识罗湖法庭传过来的 不为空则为传过来的值"
        rtsp comment: "送远程地址1"
        rtsp1 comment: "送远程地址2"
        isCalled comment: "标识此法庭是否能被远程提讯", defaultValue: 1
        distance comment: "远程提讯占位符", defultValue: ""
        comment "法庭表，保存所有法庭信息。"
    }
}
