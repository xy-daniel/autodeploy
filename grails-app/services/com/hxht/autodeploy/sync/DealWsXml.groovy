package com.hxht.autodeploy.sync

import grails.gorm.transactions.Transactional
import grails.web.context.ServletContextHolder
import org.dom4j.Document
import org.dom4j.DocumentException
import org.dom4j.Element
import org.dom4j.io.SAXReader

@Transactional
class DealWsXml {

    /**
     * 根据法院分级代码获取法院IP地址
     * @param courtId  法院分级代码
     * @return  法院IP地址
     */
    static String getCourtIpHuigu(String courtId) {
        Document doc
        try {
            InputStream inputStream = ServletContextHolder.getServletContext().getResourceAsStream("/court_list/shanxi.xml")
            SAXReader sr = new SAXReader()
            InputStreamReader reader = new InputStreamReader(inputStream)
            BufferedReader bufferedReader = new BufferedReader(reader)
            doc = sr.read(bufferedReader)
            Element root = doc.getRootElement()
            Element courtinfoE = (Element) root.selectSingleNode("//court[@courtid='" + courtId + "']")
            if (null != courtinfoE) {
                return courtinfoE.attributeValue("ip")
            }
        } catch (DocumentException | IOException e) {
            println e
        }
        return null
    }

    /**
     * 处理当事人信息
     *
     * @param element
     * @return
     */
    @SuppressWarnings("unchecked")
    static String dealCaseDSRInfo(Element element) {
        String dsrstr = "";
        if (element == null) {
            return dsrstr;
        }
        List<Element> dsrli = element.elements();
        for (Element dsrE : dsrli) {
            if ("DSR".equalsIgnoreCase(dsrE.getName())) {
                if (!"".equals(dsrstr)) {
                    dsrstr += ";";
                }
                dsrstr += dsrE.getTextTrim();
            } else if ("SSDW".equalsIgnoreCase(dsrE.getName())) {
                dsrstr += ":" + dsrE.getTextTrim();
            }
        }
        return dsrstr;
    }

    static String dealMysqlCaseParty(String dsrstr, String dscFlag) {
        String[] DSCInfos = dsrstr.split(";");
        String dscName = "";
        for (String dsr : DSCInfos) {
            String[] dsc = dsr.split(":");
            if ("1".equals(dscFlag)) {
                if (dsc[1].startsWith("上诉") || dsc[1].startsWith("原告")) {
                    if (!"".equals(dscName)) {
                        dscName += ",";
                    }
                    dscName += dsc[0];
                }
            } else if ("2".equals(dscFlag)) {
                if (dsc[1].startsWith("被上诉") || dsc[1].startsWith("被告")) {
                    if (!"".equals(dscName)) {
                        dscName += ",";
                    }
                    dscName += dsc[0];
                }
            }
        }
        return dscName;
    }

    /**
     * TODO 得到法院Ip
     *
     * @param courtId
     * @return
     * @throws Exception
     */
    static String getCourtIpRongji(String courtId) {
        Document doc
        try {
            InputStream inputStream = ServletContextHolder.getServletContext().getResourceAsStream("/court_list/fujian.xml")
            SAXReader sr = new SAXReader()
            InputStreamReader reader = new InputStreamReader(inputStream)
            BufferedReader bufferedReader = new BufferedReader(reader)
            doc = sr.read(bufferedReader)
            Element root = doc.getRootElement()
            Element courtinfoE = (Element) root.selectSingleNode("//courtinfo[@courtid='" + courtId + "']")
            if (null != courtinfoE) {
                return courtinfoE.attributeValue("courtip")
            }
        } catch (DocumentException | IOException e) {
            println(e.getMessage())
        }
        return null
    }
}
