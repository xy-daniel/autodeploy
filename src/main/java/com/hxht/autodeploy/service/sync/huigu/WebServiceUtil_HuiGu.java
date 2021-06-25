package com.hxht.autodeploy.service.sync.huigu;

import com.hxht.autodeploy.service.sync.huigu.pojo.SynchroPQInfo;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.encoding.XMLType;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;

/**
 * 交大慧谷接口ws服务初始化(格式化日志) updated by daniel in 2021.04.26
 */
public class WebServiceUtil_HuiGu {
    private static final WebServiceUtil_HuiGu ts = new WebServiceUtil_HuiGu();
    private static final Logger log = LoggerFactory.getLogger("wsAppender");

    private WebServiceUtil_HuiGu() {
    }

    /**
     * 单例模式禁止new
     */
    public static WebServiceUtil_HuiGu newinstance() {
        return ts;
    }

    /**
     * 获得WS客户端
     */
    public String callws(String endPoint, String operation, String param) {
        log.info("[WebServiceUtil_HuiGu.callws] calls接口参数:endPoint={},方法名:{},参数:{}", endPoint, operation, param);
        Service service = new Service();
        String result = "";
        try {
            Call call = (Call) service.createCall();
            call.setTargetEndpointAddress(new URL(endPoint));
            call.setOperationName(operation); // 设定调用函数名称
            // 设定HEADER验证 
            SOAPHeaderElement sh = new SOAPHeaderElement("http://service.ws.cmp.withub.net.cn//authentication", "auth:authentication");
            sh.addChildElement("auth:username").addTextNode("kjft_user");
            sh.addChildElement("auth:password").addTextNode("kjft_pw");
            call.setOperationName(new QName("http://pqWebService.webService.ajgl.withub.net.cn/", operation));
            call.addHeader(sh);
            call.setReturnType(XMLType.XSD_STRING);
            call.setTimeout(1000 * 60); // 设定超时时间为10秒
            call.setEncodingStyle("UTF-8");
            // 参数类型设定
            Object[] obj;
            if (null == param) {
                obj = new Object[]{};
            } else {
                if (Objects.equals(operation, "synchroPQInfo")) {
                    call.addParameter(new QName("arg0"), XMLType.SOAP_STRING, ParameterMode.IN);
                } else
                    call.addParameter(new QName("xml"), XMLType.XSD_STRING, ParameterMode.IN);
                obj = new Object[]{param};
                log.info("obj 参数:{}", Arrays.toString(obj));
            }
            result = (String) call.invoke(obj);
            // 获取SOAP
            MessageContext msgContext = call.getMessageContext();
            // 请求消息
            Message reqMsg = msgContext.getRequestMessage();
            log.info(reqMsg.getSOAPPartAsString());
        } catch (Exception e) {
            log.error("[WebServiceUtil_HuiGu.callws] {}导入（交大慧谷）数据失败:{}", operation, e.getMessage());
        }
        return result;
    }

    /**
     * 初始化排期接口
     */
    public String callPlan(String url, String operation, String param) {
        log.info("[WebServiceUtil_HuiGu.callPlan] 运行callPlan 获取排期专用方法.");
        JaxWsProxyFactoryBean clientFactoryBean = new JaxWsProxyFactoryBean();
        clientFactoryBean.setServiceClass(PqWebService.class);
        clientFactoryBean.setAddress(url);
        clientFactoryBean.getOutInterceptors().add(new AddSoapHeader());
        clientFactoryBean.getOutInterceptors().add(new CDataWriterInterceptor());
        clientFactoryBean.getOutInterceptors().add(new LoggingOutInterceptor());
        clientFactoryBean.getInInterceptors().add(new LoggingInInterceptor());
        PqWebService pqWebService = (PqWebService) clientFactoryBean.create();
        try {
            Client proxy = ClientProxy.getClient(pqWebService);
            HTTPConduit conduit = (HTTPConduit) proxy.getConduit();
            HTTPClientPolicy policy = new HTTPClientPolicy();
            policy.setConnectionTimeout(600000);
            policy.setReceiveTimeout(600000);
            conduit.setClient(policy);
            SynchroPQInfo synchroPQInfo = new SynchroPQInfo();
            synchroPQInfo.setArg0(param);
            log.info("[WebServiceUtil_HuiGu.callPlan] 慧谷获取排期ws的参数为:" + synchroPQInfo.getArg0());
            return pqWebService.synchroPQInfo(synchroPQInfo.getArg0());
        } catch (Exception e) {
            log.error("[WebServiceUtil_HuiGu.callPlan] {}请求出错，错误信息:{}", operation, e.getMessage());
        }
        return null;
    }
}
