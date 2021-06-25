package com.hxht.autodeploy.webService;

public class TT {

    public static void main(String[] args) {
        ZgjkRecord service = new ZgjkRecord();
        ZgjkRecordPortType port = service.getZgjkRecordHttpPort();
        String body = port.getCode("1");
        System.out.println(body);
    }
}
