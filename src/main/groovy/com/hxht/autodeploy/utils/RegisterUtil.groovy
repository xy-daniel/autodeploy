package com.hxht.autodeploy.utils


import com.hxht.license.LicenseCheckModel
import com.hxht.license.LicenseVerify
import com.hxht.license.LicenseVerifyParam
import com.hxht.license.LinuxServerInfos

class RegisterUtil {

    static registerInstall(String subject, String publicAlias, String storePass, String licensePath, String publicKeysStorePath){
        Properties props=System.getProperties() //系统属性
        def javaHome = props.getProperty("java.home")//获取jdk安装目录
        System.out.println("Java的安装路径："+ javaHome)
        def abstractServerInfos  = new LinuxServerInfos()
        def info = abstractServerInfos.getServerInfos()
        def macAddress = info.macAddress
        def cpuSerial = info.cpuSerial
        def mainBoardSerial = info.mainBoardSerial
        println "macAddress = ${macAddress}"
        println "cpuSerial = ${cpuSerial}"
        println "mainBoardSerial = ${mainBoardSerial}"

        def infoLicense = new LicenseCheckModel()
        infoLicense.macAddress = macAddress
        infoLicense.cpuSerial = cpuSerial
        infoLicense.mainBoardSerial = mainBoardSerial
        def param = new LicenseVerifyParam()
        
        param.setSubject(subject)
        param.setPublicAlias(publicAlias)
        param.setStorePass(storePass)
        param.setLicensePath(licensePath)
        param.setPublicKeysStorePath(publicKeysStorePath)
        def licenseVerify = new LicenseVerify()
        //安装证书
        licenseVerify.install(param)
    }

}
