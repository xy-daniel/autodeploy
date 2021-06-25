package com.hxht.autodeploy.sync.rongji;

import com.hxht.autodeploy.service.sync.huigu.entity.Case;
import com.hxht.autodeploy.sync.DealWsXml;
import com.hxht.autodeploy.sync.rongji.pojo.RJRemotePlan;
import com.hxht.autodeploy.utils.UUIDGenerator;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class AnalysisXml {
    private static Logger log = LoggerFactory.getLogger("wsAppender");

    @SuppressWarnings("unchecked")
    static List<Element> analysisXml(String result, String elementName) {
        List<Element> ajxxE = new ArrayList<>();
        if (null == result || "".equals(result)) {
            return ajxxE;
        }
        try {
            result = result.replace("<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"?>", "");
            Document document = DocumentHelper.parseText(result);
            Element root = document.getRootElement();
            ajxxE = root.elements(elementName);
        } catch (Exception e) {
            log.error("XML parsing error:" + e.getMessage());
        }
        return ajxxE;
    }

    static Case dealCase(Element element) {
        Case courtCase = new Case();
        if (element != null) {
            if (ObjectUtils.isEmpty(element.elementTextTrim("AJBS"))) {
                log.info("AJBS为空,本条记录跳过.");
                return null;
            }
            courtCase.setUid(UUIDGenerator.nextUUID());
            courtCase.setInterfaceId(element.elementTextTrim("AJBS"));
            courtCase.setCaseno(element.elementTextTrim("AH"));//案号
            courtCase.setSummary(element.elementTextTrim("AJLX"));//案件类型
            courtCase.setCasename(element.elementTextTrim("AJBT"));//案件名称
            String str = element.elementTextTrim("LARQ");
            Date date = null;
            try {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(str);
            } catch (ParseException e) {
                log.error("parse date error!" + str);
            }
            courtCase.setCasedate(date);
            String partyInfo = DealWsXml.dealCaseDSRInfo(element.element("DSRLIST"));
            courtCase.setAccuse(DealWsXml.dealMysqlCaseParty(partyInfo, "1"));
            courtCase.setAccused(DealWsXml.dealMysqlCaseParty(partyInfo, "2"));
            courtCase.setFlag(1);
        }
        return courtCase;
    }

    static RJRemotePlan dealPlan(Element element) {
        RJRemotePlan plan = new RJRemotePlan();
        if (element != null) {
            Element ft = element.element("FTSYJL");
            try {
                plan.setUid(UUIDGenerator.nextUUID());
                plan.setInterfaceplanId(ft.elementTextTrim("FTSYJLID"));
                plan.setJudgeName(element.elementTextTrim("SPZ"));
                plan.setJudgeCode(element.elementTextTrim("SPZBS"));
                plan.setSecretaryName(element.elementTextTrim("SJY"));
                plan.setSecretaryCode(element.elementTextTrim("SPZBS"));
                String gkkt = element.elementTextTrim("GKKT");
                if ("1".equals(gkkt)) {
                    plan.setAllowplay((byte) 1);
                } else if ("2".equals(gkkt)) {
                    plan.setAllowplay((byte) 0);
                }
                plan.setStatus(0);
                String startDate = ft.elementTextTrim("KSSJ");
                plan.setStartDate(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(startDate));
                String endDate = ft.elementTextTrim("JSSJ");
                plan.setEndDate(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(endDate));
            } catch (Exception e) {
                log.error("解析plan element 出错,message:" + e.getMessage());
            }
        }
        return plan;
    }
}
