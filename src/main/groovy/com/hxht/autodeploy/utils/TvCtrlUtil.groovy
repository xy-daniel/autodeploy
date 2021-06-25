package com.hxht.autodeploy.utils

import cn.hutool.core.util.HexUtil

/**
 *
 * TCP指令控制电视画面切换 by arctic in 2019.12.26
 */
class CtrlCommandUtil {

    /**
     * 庭审主机发送命令
     * 参数：
     *      设备ip
     *      设备端口
     *      控制指令
     * 无返回值
     */
    static void ctrlCommand(String host, Integer port, String instruction){
        Socket client = new Socket(host, port)
        client.setSoTimeout(300)
        OutputStream output = client.getOutputStream()
        output.write(instruction.getBytes("UTF-8"))
        output.close()
        client.close()
    }

    /**
     * power电源命令tcp主机发送命令
     * 参数：
     *      设备ip
     *      设备端口
     *      控制指令（发送16进制指令）
     * 无返回值
     */
    static void ctrlCommandPowerNew(String host, Integer port, String instruction){
        Socket client = new Socket(host, port)
        client.setSoTimeout(300)
        OutputStream output = client.getOutputStream()
        output.write(HexUtil.decodeHex(instruction))//将十六进制字符串解码为byte[]
        output.close()
        client.close()
    }

}
