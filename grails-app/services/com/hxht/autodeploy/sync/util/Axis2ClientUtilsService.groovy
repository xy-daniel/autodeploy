package com.hxht.autodeploy.sync.util

import com.hxht.techcrt.webService.ZgjkRecord
import com.hxht.techcrt.webService.ZgjkRecordPortType

class Axis2ClientUtilsService {

    /**
     * 1.获取基础字典数据
     * typeCode(1:法院数据2: 案由数据3: 案件类别审判程序数据4：其他字典信息数据)
     */
    def getCode(String typeCode){
        ZgjkRecord service = new ZgjkRecord()
        ZgjkRecordPortType port = service.getZgjkRecordHttpPort()
        String body = port.getCode(typeCode)
        return body
    }

    /**
     * 2.基础数据
     * typeCode(1:法院数据2: 案由数据3: 案件类别审判程序数据4：其他字典信息数据)
     */
    def getJcxx(String fydm,String typeCode){

        ZgjkRecord service = new ZgjkRecord()
        ZgjkRecordPortType port = service.getZgjkRecordHttpPort()
        String body = port.getJcxx(fydm,typeCode)
        return body
    }

    /**
     * 3.获取案件排期信息
     *
     */
    def getAhAndKtxx(String startDate,String endDate,String  fydm, String ftdm)
    {
        ZgjkRecord service = new ZgjkRecord()
        ZgjkRecordPortType port = service.getZgjkRecordHttpPort()
        String body = port.getAhAndKtxx(startDate,endDate,fydm,ftdm)
        return body

    }
}
