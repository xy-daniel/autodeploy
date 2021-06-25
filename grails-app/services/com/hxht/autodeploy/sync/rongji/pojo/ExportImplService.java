package com.hxht.autodeploy.sync.rongji.pojo;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class was generated by Apache CXF 3.2.6
 * 2019-01-22T11:31:23.385+08:00
 * Generated source version: 3.2.6
 *
 */
@WebServiceClient(name = "ExportImplService",
                  wsdlLocation = "file:/C:/Users/ADMINI~1/AppData/Local/Temp/tempdir2897188809891880105.tmp/ktxx_1.wsdl",
                  targetNamespace = "http://service.sfgl.biz.gxjh.court.rjsoft.com/")
public class ExportImplService extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://service.sfgl.biz.gxjh.court.rjsoft.com/", "ExportImplService");
    public final static QName ExportImplPort = new QName("http://service.sfgl.biz.gxjh.court.rjsoft.com/", "ExportImplPort");
    static {
        URL url = null;
        try {
            url = new URL("file:/C:/Users/ADMINI~1/AppData/Local/Temp/tempdir2897188809891880105.tmp/ktxx_1.wsdl");
        } catch (MalformedURLException e) {
            java.util.logging.Logger.getLogger(ExportImplService.class.getName())
                .log(java.util.logging.Level.INFO,
                     "Can not initialize the default wsdl from {0}", "file:/C:/Users/ADMINI~1/AppData/Local/Temp/tempdir2897188809891880105.tmp/ktxx_1.wsdl");
        }
        WSDL_LOCATION = url;
    }

    public ExportImplService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public ExportImplService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public ExportImplService() {
        super(WSDL_LOCATION, SERVICE);
    }

    public ExportImplService(WebServiceFeature ... features) {
        super(WSDL_LOCATION, SERVICE, features);
    }

    public ExportImplService(URL wsdlLocation, WebServiceFeature ... features) {
        super(wsdlLocation, SERVICE, features);
    }

    public ExportImplService(URL wsdlLocation, QName serviceName, WebServiceFeature ... features) {
        super(wsdlLocation, serviceName, features);
    }




    /**
     *
     * @return
     *     returns Export
     */
    @WebEndpoint(name = "ExportImplPort")
    public Export getExportImplPort() {
        return super.getPort(ExportImplPort, Export.class);
    }

    /**
     *
     * @param features
     *     A list of {@link WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns Export
     */
    @WebEndpoint(name = "ExportImplPort")
    public Export getExportImplPort(WebServiceFeature... features) {
        return super.getPort(ExportImplPort, Export.class, features);
    }

}