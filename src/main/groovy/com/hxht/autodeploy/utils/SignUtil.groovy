package com.hxht.autodeploy.utils

import cn.hutool.core.date.DateUtil

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * 生成验证标识
 * 验证规则
 *      1、source+当前日期+"ropIn"。其中source为source节点字符串，当前日期字符串格式为"yyyy-MM-dd","ropIn"为固定字符串
 *      2、md5上一步生成的字符串
 */
class SignUtil {

    /**
     * 生成签名标识符
     */
    static String getSign(def source, def ropIn){
        def dataStr = source + DateUtil.format(new Date(), "yyyy-MM-dd") + ropIn
        MessageDigest messageDigest = null
        try{
            messageDigest = MessageDigest.getInstance("MD5")
            messageDigest.reset()
            messageDigest.update(dataStr.getBytes("UTF-8"))
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace()
        } catch (UnsupportedEncodingException e){
            e.printStackTrace()
        }
        byte[] byteArray = messageDigest.digest()
        StringBuffer md5StrBuff = new StringBuffer()
        for (int i = 0; i < byteArray.length; i++){
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]))
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]))
        }
        return md5StrBuff.toString()
    }

}
