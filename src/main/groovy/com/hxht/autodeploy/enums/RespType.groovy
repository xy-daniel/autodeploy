package com.hxht.autodeploy.enums

enum RespType {
    SUCCESS(0, "调用成功"),
    FAIL(1, "请求失败,请稍后再试"),
    PARAM_VALID_FAIL(110, "参数校验失败"),
    BUSINESS_VALID_FAIL(120, "参数不合法"),
    DATA_NOT_ALLOWED(121, "参数未审核通过"),

    DATA_NOT_EXIST(410, "数据不存在"),
    DATA_ALREADY_EXIST(411, "数据已存在"),
    PSW_NOT_SAME(412, "两次输入密码不相同"),
    PSW_OLD_PSW(413, "旧密码错误"),
    DEVICE_EXCEPTION(414, "语音播报程序异常"),

    NO_AUTHORIZED(300, "没有权限"),
    ACCOUNT_EXPIRED(301, "证书无效"),
    USERNAME_PASSWORD_ERROR(310, "用户名或密码错误"),
    SESSION_OUTTIME(320, "请先登录"),


    METHOD_NOT_SUPPORT(405, "请求方法不允许"),

    INNER_ERROR(500, "系统内部错误"),

    //对接中恒用的庭审公告和助手的返回代码
    GROGRAMME_ERROE(255, "程序异常"),
    //是否存在并案
    COMBINEDPLAN(211, "排期存在并案"),

    //5开头远程提讯代码
    COURT_OCCUPIED(501, "法庭被占用"),
    RTSP_NOT_ALLOW(502, "法庭送远程地址不完整"),
    LIVE_IP_NOT_FOUND(503, "请设置直播地址"),
    DEVICE_IP_NOT_FOUND(504, "请设置庭审设备地址"),
    NOT_ALLOW_USE_SELF(505, "不允许同一法庭相互远程调用"),
    LOCAL_COURT_NOT_USE(506, "法庭已停用或不正常状态"),
    DEVICE_TYPE_NOT_ALLOW(507, "T系列设备不支持主动发起三方提讯"),
    SERVICE_COURT_NOT_EXIST(508, "服务端法庭不存在"),
    COURTROOM_NOT_EXSIT(509, "法庭不存在"),
    NOT_DECODE(510, "无空闲解码器"),
    START_FAIL(511, "开始远程失败"),
    STOP_FAIL(521, "断开远程失败"),
    START_COURT_STATUS_ERROR(512, "连接失败，失败原因远程法庭状态异常"),
    STOP_COURT_STATUS_ERROR(522, "断开失败，失败原因远程法庭状态异常"),
    DECODE_ERROR(513, "连接失败，失败原因添加解码器异常"),
    CLEAR_DECODE_ERROR(523, "断开失败，失败原因清除解码器异常"),
    DEVICE_NOT_SUPPORT(514, "主机不支持,请升级更高版本"),
    SERVICE_DEVICE_NOT_SUPPORT(515, "主机不支持,请升级更高版本"),

    /**
     * 参数为空
     */
    PARAMETER_NULL(401, "必填参数为空！")

    RespType(int code, String msg) {
        this.setCode(code)
        this.setMsg(msg)
    }

    /**枚举值*/
    private int code
    /**描述*/
    private String msg

    int getCode() {
        return code
    }

    void setCode(int code) {
        this.code = code
    }

    String getMsg() {
        return msg
    }

    void setMsg(String msg) {
        this.msg = msg
    }
}
