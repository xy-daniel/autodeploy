package com.hxht.autodeploy.utils

import org.apache.commons.codec.binary.Hex
import sun.misc.BASE64Encoder

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class MD5Utils {
    /**
     * 获取MD5加密
     *
     * @param str 需要加密的字符串
     * @return String字符串 加密后的字符串
     */
    static String code(String str) {
        try {
            MessageDigest digest = MessageDigest.getInstance("md5")
            byte[] bs = digest.digest(str.getBytes())
            StringBuilder hexString = new StringBuilder()
            for (byte b : bs) {
                int temp = b & 255
                if (temp < 16) {
                    hexString.append("0").append(Integer.toHexString(temp))
                } else {
                    hexString.append(Integer.toHexString(temp))
                }
            }
            return hexString.toString()
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        return ""
    }

    /**
     * 返回32长度的md5加密
     */
    static String encryption(String str) {
        String re_md5 = new String()
        try {
            MessageDigest md = MessageDigest.getInstance("MD5")
            md.update(str.getBytes())
            def b = md.digest()

            int i

            StringBuffer buf = new StringBuffer("")
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset]
                if (i < 0)
                    i += 256
                if (i < 16)
                    buf.append("0")
                buf.append(Integer.toHexString(i))
            }

            re_md5 = buf.toString()

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace()
        }
        return re_md5.toUpperCase()
    }

    static String getMD5(String str, boolean isUpper, Integer bit) {
        String md5 = null
        try {
            // 创建加密对象
            MessageDigest md = MessageDigest.getInstance("md5")
            if (bit == 64) {
                BASE64Encoder bw = new BASE64Encoder()
                md5 = bw.encode(md.digest(str.getBytes("utf-8")))
            } else {
                // 计算MD5函数
                md.update(str.getBytes())
                def b = md.digest()
                int i
                StringBuilder sb = new StringBuilder()
                for (byte aB : b) {
                    i = aB
                    if (i < 0) {
                        i += 256
                    }
                    if (i < 16) {
                        sb.append("0")
                    }
                    sb.append(Integer.toHexString(i))
                }
                md5 = sb.toString()
                if (bit == 16) {
                    //截取32位md5为16位
                    md5 = md5.substring(8, 24)
                    if (isUpper) {
                        md5 = md5.toUpperCase()
                    }
                    return md5
                }
            }
            //转换成大写
            if (isUpper) {
                md5 = md5.toUpperCase()
            }
        } catch (Exception e) {
            e.printStackTrace()
        }
        return md5

    }

    /**
     * 获取一个文件的md5值(可处理大文件)
     * @return md5 value
     */
    static String getFileMD5(File file) {
        FileInputStream fileInputStream = null
        try {
            MessageDigest MD5 = MessageDigest.getInstance("MD5")
            fileInputStream = new FileInputStream(file)
            byte[] buffer = new byte[8192]
            int length
            while ((length = fileInputStream.read(buffer)) != -1) {
                MD5.update(buffer, 0, length)
            }
            return new String(Hex.encodeHex(MD5.digest()))
        } catch (Exception e) {
            e.printStackTrace()
            return null
        } finally {
            try {
                if (fileInputStream != null){
                    fileInputStream.close()
                }
            } catch (IOException e) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 自带方法获取文件MD5值
     * @param file
     * @return
     */
   /* static String getEasyFileMD5(File file){
        return DigestUtils.md5Hex(new FileInputStream(file))
    }*/

    static void main(String[] args) {

        String code = "（2020）粤0303民初2558号"
        String str = getMD5(code, false, 32)
        // String code="kjftkjft_2E7ADA251587822954649{\"startDate\":\"2020-04-20\",\"endDate\":\"2020-04-24\",\"deptName\":\"\",\"trialCourtName\":null}"
        System.out.print(str + "----------")
    }

    //对接庭显用
    private static MessageDigest md5
    static {
        try {
            md5 = MessageDigest.getInstance("MD5")
        } catch (Exception e) {
            throw new RuntimeException(e)
        }
    }

    static String getMd5ForDns(String string) {
        try {
            byte[] bs = md5.digest(string.getBytes("UTF-8"))
            StringBuilder sb = new StringBuilder(40)
            for (byte x : bs) {
                if ((x & 0xff) >> 4 == 0) {
                    sb.append("0").append(Integer.toHexString(x & 0xff))
                } else {
                    sb.append(Integer.toHexString(x & 0xff))
                }
            }
            return sb.toString()
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e)
        }
    }
}
