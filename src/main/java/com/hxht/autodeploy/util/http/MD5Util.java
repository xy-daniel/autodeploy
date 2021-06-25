package com.hxht.autodeploy.util.http;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
    /**
     * 获取MD5加密
     *
     * @param str 需要加密的字符串
     * @return String字符串 加密后的字符串
     */
    public static String code(String str) {
        try {
            MessageDigest digest = MessageDigest.getInstance("md5");
            byte[] bs = digest.digest(str.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : bs) {
                int temp = b & 255;
                if (temp < 16) {
                    hexString.append("0").append(Integer.toHexString(temp));
                } else {
                    hexString.append(Integer.toHexString(temp));
                }
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }
}
