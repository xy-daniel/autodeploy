package com.hxht.autodeploy.sync.DongRuan

import com.hxht.techcrt.Dict
import com.hxht.techcrt.sync.rongji.pojo.Export
import com.hxht.techcrt.utils.ExceptionUtil
import grails.gorm.transactions.Transactional
import org.apache.cxf.endpoint.Client
import org.apache.cxf.frontend.ClientProxy
import org.apache.cxf.interceptor.LoggingInInterceptor
import org.apache.cxf.interceptor.LoggingOutInterceptor
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean
import org.apache.cxf.transport.http.HTTPConduit
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy

@Transactional
class DongRuanUtilService {

    private static DongRuanUtilService ts = new DongRuanUtilService()
    private static String HXHT_KEY = "hxht"

    DongRuanUtilService() {
    }

    /**
     * 单例模式禁止NEW
     *
     * @return
     */
    static DongRuanUtilService newInstance() {
        return ts
    }

    def callPlan(String url, String operation) {
        log.info("运行callPlan 获取排期专用方法。参数:url:`{}`", url)
        JaxWsProxyFactoryBean clientFactoryBean = new JaxWsProxyFactoryBean()
        clientFactoryBean.setServiceClass(Export.class)
        clientFactoryBean.setAddress(url)
        clientFactoryBean.getOutInterceptors().add(new LoggingOutInterceptor())
        clientFactoryBean.getInInterceptors().add(new LoggingInInterceptor())
        Export export = (Export) clientFactoryBean.create()
        try {
            //设置客户端的配置信息，超时等.
            Client proxy = ClientProxy.getClient(export)
            HTTPConduit conduit = (HTTPConduit) proxy.getConduit()
            HTTPClientPolicy policy = new HTTPClientPolicy()
            policy.setConnectionTimeout(600000) //连接超时时间
            policy.setReceiveTimeout(600000)//请求超时时间.

            conduit.setClient(policy)
            String result = export.getKtxxList(Dict.findByCode("CURRENT_COURT").ext3, HXHT_KEY)
            log.info("福建榕基接口获取ws返回结果:" + result)
            return result
        } catch (Exception e) {
            log.error("${operation}福建榕基接口请求出错，错误信息：${e.message} -----[${ExceptionUtil.getStackTrace(e)}")
        }
        return null
    }

}
