package com.hxht.autodeploy.service.huigu

import com.hxht.techcrt.Dict
import com.hxht.techcrt.service.sync.huigu.entity.Dept
import com.hxht.techcrt.service.sync.huigu.entity.Users
import com.hxht.techcrt.utils.UUIDGenerator
import grails.core.GrailsApplication
import grails.gorm.transactions.Transactional
import org.dom4j.Document
import org.dom4j.DocumentHelper
import org.dom4j.Element
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment
import org.springframework.util.StringUtils

import java.text.SimpleDateFormat

@Transactional
class AnalysisXml {
    GrailsApplication grailsApplication
    Environment environment

    private static Logger log = LoggerFactory.getLogger(AnalysisXml.class)
    private static Logger wsLog = LoggerFactory.getLogger("wsAppender")

    static List<Element> analysisXml(String result, String elementName) {
        List<Element> ajxxE = new ArrayList<>()
        if (null == result || "" == result) {
            return ajxxE
        }

        return ajxxE
    }

    static String makeWsParam() {
        log.info("开始构建同步基础ws接口参数。")
        Document parDoc = DocumentHelper.createDocument()
        Element rooE = parDoc.addElement("COURTINFOLIST")
        Element caseinfoE = rooE.addElement("CASEINFO")
        Element fyxx = caseinfoE.addElement("FYXX")
        fyxx.addElement("FYDM").addText(Dict.findByCode("CURRENT_COURT").ext3)
        log.info("同步基础ws数据接口参数XML构建如下：{}", parDoc.asXML())
        return parDoc.asXML()
    }


    static String makePlanWsParam() {
        log.info("开始构建同步规定时间段排期数据接口参数。")
        Document parDoc = DocumentHelper.createDocument()
        Element rooE = parDoc.addElement("COURTINFOLIST")
        Element caseinfoE = rooE.addElement("CASEINFO")

        Element fyxx = caseinfoE.addElement("FYXX")
        fyxx.addElement("FYDM").addText(Dict.findByCode("CURRENT_COURT").ext3)

        // 开庭信息设定
        Element ktxxE = caseinfoE.addElement("KTXX")
        ktxxE.addElement("FYDM").addText(Dict.findByCode("CURRENT_COURT").ext3)
        Calendar cal = Calendar.getInstance()
        cal.add(Calendar.DATE, 15)//日期增加7天
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")
        String today = sdf.format(cal.getTime())//增加七天后的时间
//        environment.getProperty("syncData.huigu.quarzt.loop")
        cal.add(Calendar.DATE, -30)//增加后的日期日期回滚14天
        ktxxE.addElement("KSKTRQ").addText(sdf.format(cal.getTime()))
        ktxxE.addElement("JSKTRQ").addText(today)
        ktxxE.addElement("FTBM").addText("")
        // 案件信息设定
        Element caseE = caseinfoE.addElement("CASE")
        caseE.addElement("NDH").addText("")
        caseE.addElement("AJBH").addText("")
        caseE.addElement("BZZH").addText("")

        log.info("同步规定时间段排期数据接口参数XML构建如下：{}", parDoc.getRootElement().asXML())
        return parDoc.getRootElement().asXML()
    }

    /**
     * RETURNSTATUS 返回值状态
     * RETURNMSG 返回信息
     * DEPLIST xml部门列表
     * DEP xml每一个部门信息
     * @param deptXmlString
     * @return
     */
    static List<Dept> resolveDept(String deptXmlString) {
        if (StringUtils.isEmpty(deptXmlString)){
            return null
        }
        try {
            //将CDATA数据转换成xml数据
            Document resDoc = DocumentHelper.parseText(deptXmlString)
            //获取根节点
            Element rootE = resDoc.getRootElement()
            //返回值状态
            String returnstatus = rootE.elementText("RETURNSTATUS")
            //返回信息
            String returnmsg = rootE.elementText("RETURNMSG")
            //判断返回值状态是否为0
            if ("0" == returnstatus) {
                log.error("部门同步失败,错误信息：{}", returnmsg)
                return null
            }
            List<Dept> deptList = new ArrayList<>()
            Element deplistE = rootE.element("DEPLIST")
            //depL:从xml解析出来的部门DEP元素列表
            List<Element> depL = deplistE.elements("DEP")
            //depE:每一个部门信息
            for (Element depE : depL) {
                Dept dept = new Dept()
                // CODE设置为Dept的uid
                dept.setUid(depE.elementText("CODE"))
                // NAME设置为Dept的deptname
                dept.setDeptname(depE.elementText("NAME"))
                // PARENTID设置为Dept的pid
                dept.setPid("" == depE.elementText("PARENTID") ? null : depE.elementText("PARENTID"))
                //等同于Pid
                dept.setInterfacePid("" == depE.elementText("PARENTID") ? null : depE.elementText("PARENTID"))
                // STATUS设置为Dept的flag----1启用，0停用
                dept.setFlag(Integer.parseInt(depE.elementText("STATUS")))
                //同时将CODE设置为Dept的interfaceId
                dept.setInterfaceId(depE.elementText("CODE"))
                deptList.add(dept)
            }
            return deptList
        } catch (Exception e) {
            log.error("处理慧谷部门数据出错，错误信息：{}", e.getMessage())
            return null
        }
    }

    static List<Users> resolveUsers(String deptXmlString) {
        if (StringUtils.isEmpty(deptXmlString))
            return null
        try {
            Document resDoc = DocumentHelper.parseText(deptXmlString)
            Element rootE = resDoc.getRootElement()
            String returnstatus = rootE.elementText("RETURNSTATUS")
            String returnmsg = rootE.elementText("RETURNMSG")
            if ("0" == returnstatus) {
                log.error("用户同步失败，错误信息：{}", returnmsg)
                return null
            }
            List<Users> usersList = new ArrayList<>()
            Element userListE = rootE.element("USERLIST")
            List<Element> userL = userListE.elements("USER")
            for (Element userE : userL) {
                Users user = new Users()
                user.setUid(UUIDGenerator.nextUUID())
                user.setInterfaceId(userE.elementText("ID"))
                user.setUserid(userE.elementText("LOGINNAME"))
                user.setDeptId(userE.elementText("DEPCODE"))
                user.setUsername(userE.elementText("NAME"))
                user.setFlag(Integer.parseInt(userE.elementText("STATUS")))
                user.setIsinput((byte) 1)
                usersList.add(user)
            }
            return usersList
        } catch (Exception e) {
            log.error("处理慧谷人员数据出错，错误信息：{}", e.getMessage())
            return null
        }
    }

    static String escapeXml(String xmlStr) {
        return xmlStr.replace("&","\$")
    }
}
