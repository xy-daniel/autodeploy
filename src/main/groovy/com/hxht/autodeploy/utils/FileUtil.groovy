package com.hxht.autodeploy.utils

import sun.misc.BASE64Decoder
import sun.misc.BASE64Encoder

import javax.servlet.http.HttpServletResponse

class FileUtil {
    /**
     * 将文件转成base64 字符串
     *
     * @param
     * @return *
     * @throws Exception
     */
    static String encodeBase64File(String path) throws Exception {
        File file = new File(path)
        FileInputStream inputFile = new FileInputStream(file)
        byte[] buffer = new byte[(int) file.length()]
        inputFile.read(buffer)
        inputFile.close()
        return new BASE64Encoder().encode(buffer)
    }

    /**
     * 将base64字符解码保存文件
     *
     * @param base64Code
     * @param targetPath
     * @throws Exception
     */

    static void decoderBase64File(String base64Code, String targetPath)
            throws Exception {
        byte[] buffer = new BASE64Decoder().decodeBuffer(base64Code)
        FileOutputStream out = new FileOutputStream(targetPath)
        out.write(buffer)
        out.close()

    }

    /**
     * 将base64字符保存文本文件
     *
     * @param base64Code
     * @param targetPath
     * @throws Exception
     */

    static void toFile(String base64Code, String targetPath)
            throws Exception {
        byte[] buffer = base64Code.getBytes()
        FileOutputStream out = new FileOutputStream(targetPath)
        out.write(buffer)
        out.close()
    }

    /**
     * 根据地址下载文件
     */
    static void download(HttpServletResponse response, String path){
        File file = new File(path)
        ZipUtil.download(response, file, path)
    }
}
