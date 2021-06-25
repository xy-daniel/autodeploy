package com.hxht.autodeploy.utils

import sun.misc.BASE64Decoder

/**
 * base64 图片工具类
 */
class Base64ImgUtl {
    /**
     * 对字节数组字符串进行Base64解码并生成文件保存到磁盘
     * @param base64str base64码
     * @param savePath 图片路径
     * @return img file
     */
    static File generateImage(String base64Str, String path) {
        if (!base64Str) {
            return null
        }
        def decoder = new BASE64Decoder()

        def file = new File(path)
        if (!file.getAbsoluteFile().getParentFile().exists()) {
            file.getParentFile().mkdirs()
        }
        def out = null
        try {
            def b = decoder.decodeBuffer(base64Str)//Base64解码
            for (def i = 0; i < b.length; ++i) {
                //调整异常数据
                if (b[i] < 0) {
                    b[i] += 256
                }
            }
            out = new FileOutputStream(file)
            out.write(b)
            return file
        } catch (Exception e) {
            e.printStackTrace()
        }finally{
            out.flush()
            out.close()
        }
        return null
    }

}
