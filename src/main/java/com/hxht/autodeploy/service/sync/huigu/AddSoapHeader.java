package com.hxht.autodeploy.service.sync.huigu;

import org.apache.cxf.binding.soap.SoapHeader;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.headers.Header;
import org.apache.cxf.helpers.DOMUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.List;

/**
 * @author Nano on 2018/8/22.
 * @version 1.0
 * @since 1.0
 */
public class AddSoapHeader extends AbstractSoapInterceptor {
    public AddSoapHeader() {
        super(Phase.WRITE);
    }


    @Override
    public void handleMessage(SoapMessage soapMessage) throws Fault {
        Document doc= DOMUtils.createDocument();
        // 根节点
        Element rootEle=doc.createElementNS("http://service.ws.cmp.withub.net.cn//authentication", "auth:authentication");
        // 用户ID
        Element userEle = doc.createElement("auth:username");
        userEle.setTextContent("kjft_user");
        rootEle.appendChild(userEle);
        // 密码
        Element passEle = doc.createElement("auth:password");
        passEle.setTextContent("kjft_pw");
        rootEle.appendChild(passEle);
        // 添加到头
        List<Header> headers = soapMessage.getHeaders();
        QName qname=new QName("");
        SoapHeader head=new SoapHeader(qname, rootEle);
        headers.add(head);
    }
}
