package com.hxht.autodeploy.sync.DongRuan

import cn.hutool.core.date.DateUtil
import com.hxht.techcrt.court.*
import com.hxht.techcrt.court.manager.SystemController
import com.hxht.techcrt.sync.util.Axis2ClientUtilsService
import com.hxht.techcrt.sync.util.DataUtil
import com.hxht.techcrt.webService.ZgjkRecord
import org.dom4j.DocumentHelper
import org.dom4j.Element

class DongRuanService {

    DongRuanPlanService dongRuanPlanService
    Axis2ClientUtilsService axis2ClientUtilsService

    /**
     * 获取案由
     * @return
     */
    def getAyData() {
        try {
            //1:法院数据2: 案由数据3: 案件类别审判程序数据4：其他字典信息数据
            def result = new ZgjkRecord().getZgjkRecordHttpPort().getCode("2")
            log.info("同步东软数据，拉取案由：" + result)
            def elementList = DataUtil.xmlSingel(result)
            for (def element : elementList) {
                try {
                    dongRuanPlanService.saveAnyou(element)
                } catch (e) {
                    e.printStackTrace()
                    log.error("同步东软数据错误[getAyData]：${e.getMessage()}")
                }

            }
        } catch (e) {
            e.printStackTrace()
            log.error(e.getMessage())
        }
    }

    /**
     * 获取案件类别审判程序数据
     * @return
     */
    def getAjlbData() {
        try {
            //1:法院数据2: 案由数据3: 案件类别审判程序数据4：其他字典信息数据
            def result = new ZgjkRecord().getZgjkRecordHttpPort().getCode("3")
            log.info("同步东软数据，拉取案件类别审判程序数据：" + result)
            def elementList = DataUtil.xmlSingel(result)
            for (def element : elementList) {
                try {
                    dongRuanPlanService.saveAjlb(element)
                } catch (e) {
                    e.printStackTrace()
                    log.error("同步东软数据错误[getAyData],保存案件类别出错：${e.getMessage()}")
                }

            }
        } catch (e) {
            e.printStackTrace()
            log.error(e.getMessage())
        }
    }

    /**
     * 获取保存部门
     */
    def getDeptData() {
        try {
            // 1:法院数据2: 案由数据3: 案件类别审判程序数据4：其他字典信息数据
            def fydm = SystemController.currentCourt.ext3
            def result = new ZgjkRecord().getZgjkRecordHttpPort().getJcxx(fydm, "1")
            log.info("同步东软数据，拉取获取部门数据：" + result)
            def elementList = DataUtil.xmlSingel(result)//1:法院数据2: 案由数据3: 案件类别审判程序数据4：其他字典信息数据
            for (def element : elementList) {
                try {
                    def uid = element.elementText("bmxh") // 处理数据 部门编号
                    def name = element.elementText("bmqc")
                    dongRuanPlanService.saveDepartment(uid, name)
                } catch (e) {
                    e.printStackTrace()
                    log.error("同步东软数据错误[getDeptData],保存部门出错：${e.getMessage()}")
                }

            }
        } catch (e) {
            e.printStackTrace()
            log.error(e.getMessage())
        }

    }
    /**
     * 获取法庭数据
     */
    def getCourtRoom() {
        try {
            // 1:法院数据2: 案由数据3: 案件类别审判程序数据4：其他字典信息数据
            def fydm = SystemController.currentCourt.ext3
            def result = new ZgjkRecord().getZgjkRecordHttpPort().getJcxx(fydm, "2")
            log.info("同步东软数据，拉取法庭数据数据：" + result)
            def elementList = DataUtil.xmlSingel(result)
            for (def element : elementList) {// 把数据写入到文件 同时把数据插入到数据库中,插入法庭编号，法庭名称
                try {
                    //表字段截取前三位判断是否存在相同的uid
                    def uid = element.elementText("ftxh")
                    log.info("当前法庭的uid(ftxh)：" + uid)
                    if (uid) {
                        // 法庭编号--拼接成32位uuid
                        def uidLength = uid.getBytes().length
                        if (uidLength == 1) {
                            uid = uid.substring(0, 1) + "zz68c2e562441088f2a494a081109fb"
                        } else if (uidLength == 2) {
                            uid = uid.substring(0, 2) + "z68c2e562441088f2a494a081109fb"
                        } else if (uidLength == 3) {
                            uid = uid.substring(0, 3) + "68c2e562441088f2a494a081109fb"
                        }
                    }

                    def name = element.elementText("ftmc")
                    dongRuanPlanService.saveCourtroom(uid, name)
                } catch (e) {
                    e.printStackTrace()
                    log.error("同步东软数据错误[getCourtRoom],法庭入库失败：${e.getMessage()}")
                }

            }
        } catch (e) {
            e.printStackTrace()
            log.error(e.getMessage())
        }

    }

    /**
     * 获取人员数据
     */
    def getUserData() {
        try {
            // 1:法院数据2: 案由数据3: 案件类别审判程序数据4：其他字典信息数据
            def fydm = SystemController.currentCourt.ext3
            def result = new ZgjkRecord().getZgjkRecordHttpPort().getJcxx(fydm, "3")
            log.info("同步东软数据，拉取人员数据：" + result)
            def elementList = DataUtil.xmlSingel(result)
            for (def element : elementList) {
                try {
                    dongRuanPlanService.saveEmployeeAndUsr(element)
                } catch (e) {
                    e.printStackTrace()
                    log.error("同步东软数据错误[getUserData],获取人员数据失败：${e.getMessage()}")
                }

            }
        } catch (e) {
            e.printStackTrace()
        }

    }
    /**
     *
     * 预定案件排期信息
     */
    def getCaseAndPlan() {
        try {
            //获取所有的法庭
            def courtRoomList = Courtroom.withNewSession {
                Courtroom.findAll()
            }
            for (def room : courtRoomList) {
                log.info("同步东软数据，案件排期数据-正在处理法庭为:" + room.name)  //根据法庭，循环查询每个法庭的案件
                try {
                    def date = DateUtil.beginOfDay(new Date()) //获取今日的0点
                    def start = DateUtil.format(date, "yyyy-MM-dd")
                    def end = DateUtil.format(DateUtil.offsetDay(date, 7), "yyyy-MM-dd")
                    def uidCourt = room.getUid().substring(0, 3) //其余法庭的uid就是对应的法庭编号
                    log.info("同步东软数据，案件排期数据-截取的字符串：" + uidCourt)
                    def uid = uidCourt.replaceAll("[^\\d.]", "")  //去除所有非数字的字符
                    log.info("同步东软数据，案件排期数据-截取后的字串：" + uid)
                    def fydm = SystemController.currentCourt.ext3
                    def result = axis2ClientUtilsService.getAhAndKtxx(start, end, fydm, uid)
//                    def result = "<?xml version=\"1.0\" encoding=\"GBK\"?>\n<root opercode=\"getAhAndKtxx\" startDate=\"2020-12-09\" endDate=\"2020-12-16\" fydm=\"J30\" ftdm=\"533\"><ajdata><record><ajbh>23000536242315358</ajbh><jbfybh>J30</jbfybh><ajlbbh>6</ajlbbh><ah>（2020）粤03行终1460号</ah><aybh>812</aybh><cbfg>王成明</cbfg><hytcy>杨宝强,伍建卿</hytcy><cbbmbh>17</cbbmbh><sjybh>J305319067879</sjybh><cbrbh>J300909170811</cbrbh></record><record><ajbh>23023128415119965</ajbh><jbfybh>J30</jbfybh><ajlbbh>6</ajlbbh><ah>（2020）粤03行初198号</ah><aybh>873</aybh><cbfg>杨宝强</cbfg><hytcy>苏祥华(陪),孙少芳(陪)</hytcy><cbbmbh>17</cbbmbh><sjybh>J304836091228</sjybh><cbrbh>J300909170742</cbrbh></record><record><ajbh>23023131391259422</ajbh><jbfybh>J30</jbfybh><ajlbbh>6</ajlbbh><ah>（2020）粤03行初199号</ah><aybh>873</aybh><cbfg>杨宝强</cbfg><hytcy>苏祥华(陪),孙少芳(陪)</hytcy><cbbmbh>17</cbbmbh><sjybh>J304836091228</sjybh><cbrbh>J300909170742</cbrbh></record><record><ajbh>23023131404481710</ajbh><jbfybh>J30</jbfybh><ajlbbh>6</ajlbbh><ah>（2020）粤03行初200号</ah><aybh>873</aybh><cbfg>杨宝强</cbfg><hytcy>苏祥华(陪),孙少芳(陪)</hytcy><cbbmbh>17</cbbmbh><sjybh>J304836091228</sjybh><cbrbh>J300909170742</cbrbh></record><record><ajbh>23039389475547041</ajbh><jbfybh>J30</jbfybh><ajlbbh>6</ajlbbh><ah>（2020）粤03行终1592号</ah><aybh>5003</aybh><cbfg>陈锦辉</cbfg><hytcy>杨宝强</hytcy><cbbmbh>17</cbbmbh><sjybh>J305653386676</sjybh><cbrbh>J3020011610621</cbrbh></record><record><ajbh>23043045699977391</ajbh><jbfybh>J30</jbfybh><ajlbbh>6</ajlbbh><ah>（2020）粤03行终1600号</ah><aybh>5003</aybh><cbfg>陈锦辉</cbfg><hytcy>杨宝强</hytcy><cbbmbh>17</cbbmbh><sjybh>J305653386676</sjybh><cbrbh>J3020011610621</cbrbh></record><record><ajbh>23051699753220236</ajbh><jbfybh>J30</jbfybh><ajlbbh>6</ajlbbh><ah>（2020）粤03行终1625号</ah><aybh>5002</aybh><cbfg>陈锦辉</cbfg><hytcy>杨宝强</hytcy><cbbmbh>17</cbbmbh><sjybh>J305653386676</sjybh><cbrbh>J3020011610621</cbrbh></record><record><ajbh>23051699760644799</ajbh><jbfybh>J30</jbfybh><ajlbbh>6</ajlbbh><ah>（2020）粤03行终1626号</ah><aybh>5002</aybh><cbfg>陈锦辉</cbfg><hytcy>杨宝强</hytcy><cbbmbh>17</cbbmbh><sjybh>J305653386676</sjybh><cbrbh>J3020011610621</cbrbh></record><record><ajbh>23055143488633562</ajbh><jbfybh>J30</jbfybh><ajlbbh>6</ajlbbh><ah>（2020）粤03行终1632号</ah><aybh>5001</aybh><cbfg>陈锦辉</cbfg><hytcy>杨宝强</hytcy><cbbmbh>17</cbbmbh><sjybh>J305653386676</sjybh><cbrbh>J3020011610621</cbrbh></record><record><ajbh>23055147974363942</ajbh><jbfybh>J30</jbfybh><ajlbbh>6</ajlbbh><ah>（2020）粤03行终1633号</ah><aybh>812</aybh><cbfg>陈锦辉</cbfg><hytcy>杨宝强</hytcy><cbbmbh>17</cbbmbh><sjybh>J305653386676</sjybh><cbrbh>J3020011610621</cbrbh></record><record><ajbh>23905658065254540</ajbh><jbfybh>J30</jbfybh><ajlbbh>6</ajlbbh><ah>（2020）粤03行初109号</ah><aybh>5003</aybh><cbfg>王成明</cbfg><hytcy>杨宝强,伍建卿</hytcy><cbbmbh>17</cbbmbh><sjybh>J305319067879</sjybh><cbrbh>J300909170811</cbrbh></record><record><ajbh>23905666953285394</ajbh><jbfybh>J30</jbfybh><ajlbbh>6</ajlbbh><ah>（2020）粤03行初110号</ah><aybh>5003</aybh><cbfg>王成明</cbfg><hytcy>杨宝强,伍建卿</hytcy><cbbmbh>17</cbbmbh><sjybh>J305319067879</sjybh><cbrbh>J300909170811</cbrbh></record><record><ajbh>23996144846940182</ajbh><jbfybh>J30</jbfybh><ajlbbh>6</ajlbbh><ah>（2020）粤03行终1429号</ah><aybh>812</aybh><cbfg>王成明</cbfg><hytcy>杨宝强,伍建卿</hytcy><cbbmbh>17</cbbmbh><sjybh>J305319067879</sjybh><cbrbh>J300909170811</cbrbh></record></ajdata><dsrdata><record><ajbh>23000536242315358</ajbh><dsrbh>1</dsrbh><dsrmc ssdwmc=\"上诉人\" dsrlxmc=\"\">宏源精密五金（深圳）有限公司</dsrmc></record><record><ajbh>23000536242315358</ajbh><dsrbh>2</dsrbh><dsrmc ssdwmc=\"被上诉人\" dsrlxmc=\"\">深圳市生态环境局宝安管理局</dsrmc></record><record><ajbh>23000536242315358</ajbh><dsrbh>3</dsrbh><dsrmc ssdwmc=\"被上诉人\" dsrlxmc=\"\">深圳市生态环境局</dsrmc></record><record><ajbh>23023128415119965</ajbh><dsrbh>1</dsrbh><dsrmc ssdwmc=\"原告\" dsrlxmc=\"\">邓春儒</dsrmc></record><record><ajbh>23023128415119965</ajbh><dsrbh>2</dsrbh><dsrmc ssdwmc=\"被告\" dsrlxmc=\"\">深圳市人民政府</dsrmc></record><record><ajbh>23023131391259422</ajbh><dsrbh>1</dsrbh><dsrmc ssdwmc=\"原告\" dsrlxmc=\"\">吴清水</dsrmc></record><record><ajbh>23023131391259422</ajbh><dsrbh>2</dsrbh><dsrmc ssdwmc=\"被告\" dsrlxmc=\"\">深圳市人民政府</dsrmc></record><record><ajbh>23023131404481710</ajbh><dsrbh>1</dsrbh><dsrmc ssdwmc=\"原告\" dsrlxmc=\"\">吴益先</dsrmc></record><record><ajbh>23023131404481710</ajbh><dsrbh>2</dsrbh><dsrmc ssdwmc=\"被告\" dsrlxmc=\"\">深圳市人民政府</dsrmc></record><record><ajbh>23039389475547041</ajbh><dsrbh>1</dsrbh><dsrmc ssdwmc=\"上诉人\" dsrlxmc=\"\">杨新良</dsrmc></record><record><ajbh>23039389475547041</ajbh><dsrbh>2</dsrbh><dsrmc ssdwmc=\"被上诉人\" dsrlxmc=\"\">深圳市福田区人力资源局</dsrmc></record><record><ajbh>23043045699977391</ajbh><dsrbh>1</dsrbh><dsrmc ssdwmc=\"上诉人\" dsrlxmc=\"\">陈益群</dsrmc></record><record><ajbh>23043045699977391</ajbh><dsrbh>2</dsrbh><dsrmc ssdwmc=\"被上诉人\" dsrlxmc=\"\">深圳市人力资源和社会保障局</dsrmc></record><record><ajbh>23043045699977391</ajbh><dsrbh>3</dsrbh><dsrmc ssdwmc=\"被上诉人\" dsrlxmc=\"\">深圳市人民政府</dsrmc></record><record><ajbh>23043045699977391</ajbh><dsrbh>4</dsrbh><dsrmc ssdwmc=\"被上诉人\" dsrlxmc=\"\">大连森成物流有限公司深圳分公司</dsrmc></record><record><ajbh>23051699753220236</ajbh><dsrbh>1</dsrbh><dsrmc ssdwmc=\"被上诉人\" dsrlxmc=\"\">舒小林</dsrmc></record><record><ajbh>23051699753220236</ajbh><dsrbh>2</dsrbh><dsrmc ssdwmc=\"被上诉人\" dsrlxmc=\"\">吴志勇</dsrmc></record><record><ajbh>23051699753220236</ajbh><dsrbh>3</dsrbh><dsrmc ssdwmc=\"上诉人\" dsrlxmc=\"\">深圳市罗湖区城市更新和土地整备局</dsrmc></record><record><ajbh>23051699760644799</ajbh><dsrbh>1</dsrbh><dsrmc ssdwmc=\"被上诉人\" dsrlxmc=\"\">舒小林</dsrmc></record><record><ajbh>23051699760644799</ajbh><dsrbh>2</dsrbh><dsrmc ssdwmc=\"被上诉人\" dsrlxmc=\"\">吴志勇</dsrmc></record><record><ajbh>23051699760644799</ajbh><dsrbh>3</dsrbh><dsrmc ssdwmc=\"上诉人\" dsrlxmc=\"\">深圳市罗湖区清水河街道办事处</dsrmc></record><record><ajbh>23055143488633562</ajbh><dsrbh>1</dsrbh><dsrmc ssdwmc=\"上诉人\" dsrlxmc=\"\">彭登</dsrmc></record><record><ajbh>23055143488633562</ajbh><dsrbh>2</dsrbh><dsrmc ssdwmc=\"被上诉人\" dsrlxmc=\"\">深圳市龙华区科技创新局</dsrmc></record><record><ajbh>23055147974363942</ajbh><dsrbh>1</dsrbh><dsrmc ssdwmc=\"上诉人\" dsrlxmc=\"\">深圳市卓越兴隆物业管理有限公司</dsrmc></record><record><ajbh>23055147974363942</ajbh><dsrbh>2</dsrbh><dsrmc ssdwmc=\"被上诉人\" dsrlxmc=\"\">深圳市宝安区土地规划监察局</dsrmc></record><record><ajbh>23905658065254540</ajbh><dsrbh>1</dsrbh><dsrmc ssdwmc=\"原告\" dsrlxmc=\"\">叶秀霞</dsrmc></record><record><ajbh>23905658065254540</ajbh><dsrbh>2</dsrbh><dsrmc ssdwmc=\"原告\" dsrlxmc=\"\">廖玉华</dsrmc></record><record><ajbh>23905658065254540</ajbh><dsrbh>3</dsrbh><dsrmc ssdwmc=\"原告\" dsrlxmc=\"\">廖文钊</dsrmc></record><record><ajbh>23905658065254540</ajbh><dsrbh>4</dsrbh><dsrmc ssdwmc=\"被告\" dsrlxmc=\"\">深圳市宝安区人民政府</dsrmc></record><record><ajbh>23905658065254540</ajbh><dsrbh>6</dsrbh><dsrmc ssdwmc=\"第三人\" dsrlxmc=\"\">深圳市黎光聚英股份合作公司</dsrmc></record><record><ajbh>23905658065254540</ajbh><dsrbh>7</dsrbh><dsrmc ssdwmc=\"被告\" dsrlxmc=\"\">深圳市宝安区石岩街道办事处</dsrmc></record><record><ajbh>23905658065254540</ajbh><dsrbh>8</dsrbh><dsrmc ssdwmc=\"被告\" dsrlxmc=\"\">深圳市宝安区城市更新和土地整备局</dsrmc></record><record><ajbh>23905666953285394</ajbh><dsrbh>1</dsrbh><dsrmc ssdwmc=\"原告\" dsrlxmc=\"\">叶文映</dsrmc></record><record><ajbh>23905666953285394</ajbh><dsrbh>2</dsrbh><dsrmc ssdwmc=\"被告\" dsrlxmc=\"\">深圳市宝安区人民政府</dsrmc></record><record><ajbh>23905666953285394</ajbh><dsrbh>4</dsrbh><dsrmc ssdwmc=\"第三人\" dsrlxmc=\"\">深圳市黎光聚英股份合作公司</dsrmc></record><record><ajbh>23905666953285394</ajbh><dsrbh>5</dsrbh><dsrmc ssdwmc=\"被告\" dsrlxmc=\"\">深圳市宝安区石岩街道办事处</dsrmc></record><record><ajbh>23905666953285394</ajbh><dsrbh>6</dsrbh><dsrmc ssdwmc=\"被告\" dsrlxmc=\"\">深圳市宝安区城市更新和土地整备局</dsrmc></record><record><ajbh>23996144846940182</ajbh><dsrbh>1</dsrbh><dsrmc ssdwmc=\"上诉人\" dsrlxmc=\"\">李伟清</dsrmc></record><record><ajbh>23996144846940182</ajbh><dsrbh>2</dsrbh><dsrmc ssdwmc=\"被上诉人\" dsrlxmc=\"\">深圳市公安局交通警察支队福田大队</dsrmc></record><record><ajbh>23996144846940182</ajbh><dsrbh>3</dsrbh><dsrmc ssdwmc=\"被上诉人\" dsrlxmc=\"\">深圳市公安局交通警察局</dsrmc></record></dsrdata><hytdata><record><ajbh>23000536242315358</ajbh><rybh>J305319067879</rybh><ryxm>林艾</ryxm><ryjs>审计员</ryjs></record><record><ajbh>23000536242315358</ajbh><rybh>J300909170811</rybh><ryxm>王成明</ryxm><ryjs>承办人</ryjs></record><record><ajbh>23000536242315358</ajbh><rybh>J300909170811</rybh><ryxm>王成明</ryxm><ryjs>审判长</ryjs></record><record><ajbh>23000536242315358</ajbh><rybh>J3020011610617</rybh><ryxm>伍建卿</ryxm><ryjs>合议庭成员</ryjs></record><record><ajbh>23000536242315358</ajbh><rybh>J300909170742</rybh><ryxm>杨宝强</ryxm><ryjs>合议庭成员</ryjs></record><record><ajbh>23023128415119965</ajbh><rybh>J302010165126</rybh><ryxm>苏祥华(陪)</ryxm><ryjs>合议庭成员</ryjs></record><record><ajbh>23023128415119965</ajbh><rybh>J302010160392</rybh><ryxm>孙少芳(陪)</ryxm><ryjs>合议庭成员</ryjs></record><record><ajbh>23023128415119965</ajbh><rybh>J304836091228</rybh><ryxm>颜慧婷</ryxm><ryjs>审计员</ryjs></record><record><ajbh>23023128415119965</ajbh><rybh>J300909170742</rybh><ryxm>杨宝强</ryxm><ryjs>承办人</ryjs></record><record><ajbh>23023128415119965</ajbh><rybh>J300909170742</rybh><ryxm>杨宝强</ryxm><ryjs>审判长</ryjs></record><record><ajbh>23023131391259422</ajbh><rybh>J302010165126</rybh><ryxm>苏祥华(陪)</ryxm><ryjs>合议庭成员</ryjs></record><record><ajbh>23023131391259422</ajbh><rybh>J302010160392</rybh><ryxm>孙少芳(陪)</ryxm><ryjs>合议庭成员</ryjs></record><record><ajbh>23023131391259422</ajbh><rybh>J304836091228</rybh><ryxm>颜慧婷</ryxm><ryjs>审计员</ryjs></record><record><ajbh>23023131391259422</ajbh><rybh>J300909170742</rybh><ryxm>杨宝强</ryxm><ryjs>承办人</ryjs></record><record><ajbh>23023131391259422</ajbh><rybh>J300909170742</rybh><ryxm>杨宝强</ryxm><ryjs>审判长</ryjs></record><record><ajbh>23023131404481710</ajbh><rybh>J302010165126</rybh><ryxm>苏祥华(陪)</ryxm><ryjs>合议庭成员</ryjs></record><record><ajbh>23023131404481710</ajbh><rybh>J302010160392</rybh><ryxm>孙少芳(陪)</ryxm><ryjs>合议庭成员</ryjs></record><record><ajbh>23023131404481710</ajbh><rybh>J304836091228</rybh><ryxm>颜慧婷</ryxm><ryjs>审计员</ryjs></record><record><ajbh>23023131404481710</ajbh><rybh>J300909170742</rybh><ryxm>杨宝强</ryxm><ryjs>承办人</ryjs></record><record><ajbh>23023131404481710</ajbh><rybh>J300909170742</rybh><ryxm>杨宝强</ryxm><ryjs>审判长</ryjs></record><record><ajbh>23039389475547041</ajbh><rybh>J3020011610621</rybh><ryxm>陈锦辉</ryxm><ryjs>承办人</ryjs></record><record><ajbh>23039389475547041</ajbh><rybh>J305653386676</rybh><ryxm>李运佳</ryxm><ryjs>审计员</ryjs></record><record><ajbh>23039389475547041</ajbh><rybh>J3020010610598</rybh><ryxm>王惠奕</ryxm><ryjs>审判长</ryjs></record><record><ajbh>23039389475547041</ajbh><rybh>J300909170742</rybh><ryxm>杨宝强</ryxm><ryjs>合议庭成员</ryjs></record><record><ajbh>23043045699977391</ajbh><rybh>J3020011610621</rybh><ryxm>陈锦辉</ryxm><ryjs>承办人</ryjs></record><record><ajbh>23043045699977391</ajbh><rybh>J305653386676</rybh><ryxm>李运佳</ryxm><ryjs>审计员</ryjs></record><record><ajbh>23043045699977391</ajbh><rybh>J3020010610598</rybh><ryxm>王惠奕</ryxm><ryjs>审判长</ryjs></record><record><ajbh>23043045699977391</ajbh><rybh>J300909170742</rybh><ryxm>杨宝强</ryxm><ryjs>合议庭成员</ryjs></record><record><ajbh>23051699753220236</ajbh><rybh>J3020011610621</rybh><ryxm>陈锦辉</ryxm><ryjs>承办人</ryjs></record><record><ajbh>23051699753220236</ajbh><rybh>J305653386676</rybh><ryxm>李运佳</ryxm><ryjs>审计员</ryjs></record><record><ajbh>23051699753220236</ajbh><rybh>J3020010610598</rybh><ryxm>王惠奕</ryxm><ryjs>审判长</ryjs></record><record><ajbh>23051699753220236</ajbh><rybh>J300909170742</rybh><ryxm>杨宝强</ryxm><ryjs>合议庭成员</ryjs></record><record><ajbh>23051699760644799</ajbh><rybh>J3020011610621</rybh><ryxm>陈锦辉</ryxm><ryjs>承办人</ryjs></record><record><ajbh>23051699760644799</ajbh><rybh>J305653386676</rybh><ryxm>李运佳</ryxm><ryjs>审计员</ryjs></record><record><ajbh>23051699760644799</ajbh><rybh>J3020010610598</rybh><ryxm>王惠奕</ryxm><ryjs>审判长</ryjs></record><record><ajbh>23051699760644799</ajbh><rybh>J300909170742</rybh><ryxm>杨宝强</ryxm><ryjs>合议庭成员</ryjs></record><record><ajbh>23055143488633562</ajbh><rybh>J3020011610621</rybh><ryxm>陈锦辉</ryxm><ryjs>承办人</ryjs></record><record><ajbh>23055143488633562</ajbh><rybh>J305653386676</rybh><ryxm>李运佳</ryxm><ryjs>审计员</ryjs></record><record><ajbh>23055143488633562</ajbh><rybh>J3020010610598</rybh><ryxm>王惠奕</ryxm><ryjs>审判长</ryjs></record><record><ajbh>23055143488633562</ajbh><rybh>J300909170742</rybh><ryxm>杨宝强</ryxm><ryjs>合议庭成员</ryjs></record><record><ajbh>23055147974363942</ajbh><rybh>J3020011610621</rybh><ryxm>陈锦辉</ryxm><ryjs>承办人</ryjs></record><record><ajbh>23055147974363942</ajbh><rybh>J305653386676</rybh><ryxm>李运佳</ryxm><ryjs>审计员</ryjs></record><record><ajbh>23055147974363942</ajbh><rybh>J3020010610598</rybh><ryxm>王惠奕</ryxm><ryjs>审判长</ryjs></record><record><ajbh>23055147974363942</ajbh><rybh>J300909170742</rybh><ryxm>杨宝强</ryxm><ryjs>合议庭成员</ryjs></record><record><ajbh>23905658065254540</ajbh><rybh>J305319067879</rybh><ryxm>林艾</ryxm><ryjs>审计员</ryjs></record><record><ajbh>23905658065254540</ajbh><rybh>J300909170811</rybh><ryxm>王成明</ryxm><ryjs>承办人</ryjs></record><record><ajbh>23905658065254540</ajbh><rybh>J300909170811</rybh><ryxm>王成明</ryxm><ryjs>审判长</ryjs></record><record><ajbh>23905658065254540</ajbh><rybh>J3020011610617</rybh><ryxm>伍建卿</ryxm><ryjs>合议庭成员</ryjs></record><record><ajbh>23905658065254540</ajbh><rybh>J300909170742</rybh><ryxm>杨宝强</ryxm><ryjs>合议庭成员</ryjs></record><record><ajbh>23905666953285394</ajbh><rybh>J305319067879</rybh><ryxm>林艾</ryxm><ryjs>审计员</ryjs></record><record><ajbh>23905666953285394</ajbh><rybh>J300909170811</rybh><ryxm>王成明</ryxm><ryjs>承办人</ryjs></record><record><ajbh>23905666953285394</ajbh><rybh>J300909170811</rybh><ryxm>王成明</ryxm><ryjs>审判长</ryjs></record><record><ajbh>23905666953285394</ajbh><rybh>J3020011610617</rybh><ryxm>伍建卿</ryxm><ryjs>合议庭成员</ryjs></record><record><ajbh>23905666953285394</ajbh><rybh>J300909170742</rybh><ryxm>杨宝强</ryxm><ryjs>合议庭成员</ryjs></record><record><ajbh>23996144846940182</ajbh><rybh>J305319067879</rybh><ryxm>林艾</ryxm><ryjs>审计员</ryjs></record><record><ajbh>23996144846940182</ajbh><rybh>J300909170811</rybh><ryxm>王成明</ryxm><ryjs>承办人</ryjs></record><record><ajbh>23996144846940182</ajbh><rybh>J300909170811</rybh><ryxm>王成明</ryxm><ryjs>审判长</ryjs></record><record><ajbh>23996144846940182</ajbh><rybh>J3020011610617</rybh><ryxm>伍建卿</ryxm><ryjs>合议庭成员</ryjs></record><record><ajbh>23996144846940182</ajbh><rybh>J300909170742</rybh><ryxm>杨宝强</ryxm><ryjs>合议庭成员</ryjs></record></hytdata><ktdata><record><ajbh>23996144846940182</ajbh><ktbh>1</ktbh><ftbh>533</ftbh><ktrq>2020-12-09</ktrq><ktcs>1</ktcs><ydkssj>09:30:00</ydkssj><ydjssj>10:20:00</ydjssj><ktzt>预定成功</ktzt></record><record><ajbh>23055147974363942</ajbh><ktbh>1</ktbh><ftbh>533</ftbh><ktrq>2020-12-16</ktrq><ktcs>1</ktcs><ydkssj>10:30:00</ydkssj><ydjssj>11:30:00</ydjssj><ktzt>预定成功</ktzt></record><record><ajbh>23000536242315358</ajbh><ktbh>2</ktbh><ftbh>533</ftbh><ktrq>2020-12-09</ktrq><ktcs>1</ktcs><ydkssj>10:30:00</ydkssj><ydjssj>12:00:00</ydjssj><ktzt>预定成功</ktzt></record><record><ajbh>23023131391259422</ajbh><ktbh>3</ktbh><ftbh>533</ftbh><ktrq>2020-12-15</ktrq><ktcs>1</ktcs><ydkssj>09:30:00</ydkssj><ydjssj>12:00:00</ydjssj><ktzt>预定成功</ktzt></record><record><ajbh>23023128415119965</ajbh><ktbh>2</ktbh><ftbh>533</ftbh><ktrq>2020-12-11</ktrq><ktcs>1</ktcs><ydkssj>09:30:00</ydkssj><ydjssj>12:00:00</ydjssj><ktzt>撤消</ktzt></record><record><ajbh>23023131404481710</ajbh><ktbh>2</ktbh><ftbh>533</ftbh><ktrq>2020-12-11</ktrq><ktcs>1</ktcs><ydkssj>09:30:00</ydkssj><ydjssj>12:00:00</ydjssj><ktzt>撤消</ktzt></record><record><ajbh>23023131391259422</ajbh><ktbh>2</ktbh><ftbh>533</ftbh><ktrq>2020-12-11</ktrq><ktcs>1</ktcs><ydkssj>09:30:00</ydkssj><ydjssj>12:00:00</ydjssj><ktzt>撤消</ktzt></record><record><ajbh>23023128415119965</ajbh><ktbh>1</ktbh><ftbh>533</ftbh><ktrq>2020-12-11</ktrq><ktcs>1</ktcs><ydkssj>09:30:00</ydkssj><ydjssj>12:00:00</ydjssj><ktzt>撤消</ktzt></record><record><ajbh>23023131404481710</ajbh><ktbh>1</ktbh><ftbh>533</ftbh><ktrq>2020-12-11</ktrq><ktcs>1</ktcs><ydkssj>09:30:00</ydkssj><ydjssj>12:00:00</ydjssj><ktzt>撤消</ktzt></record><record><ajbh>23023131391259422</ajbh><ktbh>1</ktbh><ftbh>533</ftbh><ktrq>2020-12-11</ktrq><ktcs>1</ktcs><ydkssj>09:30:00</ydkssj><ydjssj>12:00:00</ydjssj><ktzt>撤消</ktzt></record><record><ajbh>23023128415119965</ajbh><ktbh>3</ktbh><ftbh>533</ftbh><ktrq>2020-12-15</ktrq><ktcs>1</ktcs><ydkssj>09:30:00</ydkssj><ydjssj>12:00:00</ydjssj><ktzt>预定成功</ktzt></record><record><ajbh>23023131404481710</ajbh><ktbh>3</ktbh><ftbh>533</ftbh><ktrq>2020-12-15</ktrq><ktcs>1</ktcs><ydkssj>09:30:00</ydkssj><ydjssj>12:00:00</ydjssj><ktzt>预定成功</ktzt></record><record><ajbh>23055143488633562</ajbh><ktbh>1</ktbh><ftbh>533</ftbh><ktrq>2020-12-16</ktrq><ktcs>1</ktcs><ydkssj>09:30:00</ydkssj><ydjssj>10:30:00</ydjssj><ktzt>预定成功</ktzt></record><record><ajbh>23051699753220236</ajbh><ktbh>1</ktbh><ftbh>533</ftbh><ktrq>2020-12-10</ktrq><ktcs>1</ktcs><ydkssj>14:30:00</ydkssj><ydjssj>15:30:00</ydjssj><ktzt>预定成功</ktzt></record><record><ajbh>23051699760644799</ajbh><ktbh>1</ktbh><ftbh>533</ftbh><ktrq>2020-12-10</ktrq><ktcs>1</ktcs><ydkssj>15:30:00</ydkssj><ydjssj>16:30:00</ydjssj><ktzt>预定成功</ktzt></record><record><ajbh>23043045699977391</ajbh><ktbh>1</ktbh><ftbh>533</ftbh><ktrq>2020-12-10</ktrq><ktcs>1</ktcs><ydkssj>09:30:00</ydkssj><ydjssj>10:30:00</ydjssj><ktzt>预定成功</ktzt></record><record><ajbh>23039389475547041</ajbh><ktbh>3</ktbh><ftbh>533</ftbh><ktrq>2020-12-10</ktrq><ktcs>1</ktcs><ydkssj>10:30:00</ydkssj><ydjssj>11:30:00</ydjssj><ktzt>预定成功</ktzt></record><record><ajbh>23039389475547041</ajbh><ktbh>2</ktbh><ftbh>533</ftbh><ktrq>2020-12-10</ktrq><ktcs>1</ktcs><ydkssj>10:30:00</ydkssj><ydjssj>11:30:00</ydjssj><ktzt>撤消</ktzt></record><record><ajbh>23905658065254540</ajbh><ktbh>1</ktbh><ftbh>533</ftbh><ktrq>2020-12-09</ktrq><ktcs>2</ktcs><ydkssj>14:30:00</ydkssj><ydjssj>17:30:00</ydjssj><ktzt>撤消</ktzt></record><record><ajbh>23905666953285394</ajbh><ktbh>1</ktbh><ftbh>533</ftbh><ktrq>2020-12-09</ktrq><ktcs>2</ktcs><ydkssj>14:30:00</ydkssj><ydjssj>17:30:00</ydjssj><ktzt>撤消</ktzt></record></ktdata></root>"
                    log.info("同步东软数据，案件排期数据-拉取东软排期：" + result)
                    /* if(!(room.name == "第一庭")){
                         continue
                     }*/
                    if (!result) {
                        continue
                    }
                    //获取所有案件数据
                    def root = DocumentHelper.parseText(result).getRootElement()
                    //案件列表
                    def ajrecordsList = root.element("ajdata").elements() as List<Element>
                    //当事人列表
                    def dsrrecordsList = root.element("dsrdata").elements() as List<Element>
                    //合议庭成员列表
                    def hytrecordsList = root.element("hytdata").elements() as List<Element>
                    //开庭信息列表
                    def ktrecordsList = root.element("ktdata").elements() as List<Element>
                    /**
                     * 注意此处，每一条数据都是一条事物，要保证排期，案件，人员编号，法庭事物一致性
                     */
                    for (def element : ajrecordsList) {
                        //根据案件编号找出， 合议庭数据，当事人信息（一条一条执行）
                        dongRuanPlanService.planMap(element, hytrecordsList, dsrrecordsList, ktrecordsList, room)
                    }
                } catch (e) {
                    e.printStackTrace()
                    log.error("同步东软数据错误[getCaseAndPlan]，获取预定案件排期失败：${e.getMessage()}")
                }
            }
        } catch (e) {
            e.printStackTrace()
        }

    }

}
