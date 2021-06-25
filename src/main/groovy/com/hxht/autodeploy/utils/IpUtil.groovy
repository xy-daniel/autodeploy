package com.hxht.autodeploy.utils

import com.hxht.techcrt.court.Courtroom

import javax.servlet.http.HttpServletRequest

/**
 * Created by Voyager on 2017/1/16.
 * 2021.03.26 >>> 添加获取服务器IP工具 daniel
 */
class IpUtil {

    static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for")
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP")
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP")
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP")
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR")
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr()
        }
        return ip
    }

    static boolean ping(String host){
        try {
            return InetAddress.getByName(host).isReachable(100)
        } catch (e) {
            println "ping主机[${host}]时出现异常,异常信息：\n${e.getMessage()}"
            return false
        }
    }

    /**
     * 获取内网IP
     * @return
     */
    static String getLocalIp() {
        String localIp = null
        try {
            Enumeration netInterfaces = NetworkInterface.getNetworkInterfaces()
            InetAddress ip = null
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) netInterfaces.nextElement()
                Enumeration address = ni.getInetAddresses()
                while (address.hasMoreElements()) {
                    ip = (InetAddress) address.nextElement()
                    if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {// 内网IP
                        localIp = ip.getHostAddress()
                        break
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace()
        }
        return localIp
    }

    /**
     * 获取服务器IP
     */
    static String getServiceIp () {
        String ip = ""
        List<Courtroom> courtroomList = Courtroom.findAll()
        for (int i = 0; i < courtroomList.size(); i++) {
            if (courtroomList.get(i).liveIp) {
                ip = courtroomList.get(i).liveIp
                break
            }
        }
        return ip
    }
}
