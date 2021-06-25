package com.hxht.autodeploy.sync.shandong

import java.sql.Connection
import java.sql.Driver
import java.sql.DriverManager

class ConnectionUtilService {
    /**
     * 山东威海法院使用sybase数据库直接读取数据
     * @param data
     * @return
     */
    synchronized Connection getInstance(String sybaseIp, String sybaseUser, String sybasePwd) throws Exception {
        //sybaseUrl=jdbc\:sybase\:Tds\:142.176.1.36\:8888/escloud?charset\=eucgb
        Connection con
        String sybaseUrl="jdbc:sybase:Tds:"+sybaseIp+":8888/escloud?charset=eucgb"
        log.info("----logs---" + sybaseUrl + "----" + sybaseUser + "-----" + sybasePwd)
        DriverManager.registerDriver((Driver) Class.forName("com.sybase.jdbc4.jdbc.SybDriver").newInstance())
        con = DriverManager.getConnection(sybaseUrl, sybaseUser, sybasePwd)
        log.info("connection:" + con + " ----")
      /*  Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
        String url = "jdbc:jtds:sybase://"+sybaseIp+":8888/escloud?charset=eucgb";// 数据库名
        Properties sysProps = System.getProperties();
        sysProps.put("user", sybaseUser); // 设置数据库访问用户名
        sysProps.put("password", sybasePwd); // 密码
        log.info("----logs---" + url + "----" + sybaseUser + "-----" + sybasePwd)
        con = DriverManager.getConnection(url, sysProps)
        log.info("connection:" + con + " ----")*/
        return con
    }
}
