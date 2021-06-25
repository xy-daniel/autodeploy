package com.hxht.autodeploy.sync.tongdahai;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

/**
 * This class was generated by Apache CXF 3.2.6
 * 2018-09-19T10:50:40.839+08:00
 * Generated source version: 3.2.6
 *
 */
@WebService(targetNamespace = "http://szft.tdh/", name = "SzftWebService")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface SzftWebService {

    @WebMethod(operationName = "GetFt")
    @WebResult(name = "ResultXML", targetNamespace = "http://szft.tdh/", partName = "ResultXML")
    String getFt(
            @WebParam(partName = "fydm", name = "fydm")
                    String fydm,
            @WebParam(partName = "token", name = "token")
                    String token,
            @WebParam(partName = "xml", name = "xml")
                    String xml
    );

    @WebMethod(operationName = "GetPlKtDel")
    @WebResult(name = "ResultXML", targetNamespace = "http://szft.tdh/", partName = "ResultXML")
    String getPlKtDel(
            @WebParam(partName = "fydm", name = "fydm")
                    String fydm,
            @WebParam(partName = "token", name = "token")
                    String token,
            @WebParam(partName = "xml", name = "xml")
                    String xml
    );

    @WebMethod(operationName = "GetRy")
    @WebResult(name = "ResultXML", targetNamespace = "http://szft.tdh/", partName = "ResultXML")
    String getRy(
            @WebParam(partName = "fydm", name = "fydm")
                    String fydm,
            @WebParam(partName = "token", name = "token")
                    String token,
            @WebParam(partName = "xml", name = "xml")
                    String xml
    );

    @WebMethod(operationName = "GetPlKt")
    @WebResult(name = "ResultXML", targetNamespace = "http://szft.tdh/", partName = "ResultXML")
    String getPlKt(
            @WebParam(partName = "fydm", name = "fydm")
                    String fydm,
            @WebParam(partName = "token", name = "token")
                    String token,
            @WebParam(partName = "xml", name = "xml")
                    String xml
    );

    @WebMethod(operationName = "GetPlAj")
    @WebResult(name = "ResultXML", targetNamespace = "http://szft.tdh/", partName = "ResultXML")
    String getPlAj(
            @WebParam(partName = "fydm", name = "fydm")
                    String fydm,
            @WebParam(partName = "token", name = "token")
                    String token,
            @WebParam(partName = "xml", name = "xml")
                    String xml
    );

    @WebMethod(operationName = "GetDsr")
    @WebResult(name = "ResultXML", targetNamespace = "http://szft.tdh/", partName = "ResultXML")
    String getDsr(
            @WebParam(partName = "fydm", name = "fydm")
                    String fydm,
            @WebParam(partName = "token", name = "token")
                    String token,
            @WebParam(partName = "xml", name = "xml")
                    String xml
    );

    @WebMethod(operationName = "SendKtxx")
    @WebResult(name = "Result", targetNamespace = "http://szft.tdh/", partName = "Result")
    String sendKtxx(
            @WebParam(partName = "fydm", name = "fydm")
                    String fydm,
            @WebParam(partName = "token", name = "token")
                    String token,
            @WebParam(partName = "xml", name = "xml")
                    String xml
    );

    @WebMethod(operationName = "GetZzjg")
    @WebResult(name = "ResultXML", targetNamespace = "http://szft.tdh/", partName = "ResultXML")
    String getZzjg(
            @WebParam(partName = "fydm", name = "fydm")
                    String fydm,
            @WebParam(partName = "token", name = "token")
                    String token,
            @WebParam(partName = "xml", name = "xml")
                    String xml
    );
}
