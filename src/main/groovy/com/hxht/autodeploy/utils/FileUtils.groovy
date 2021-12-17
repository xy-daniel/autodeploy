package com.hxht.autodeploy.utils

import javax.servlet.http.HttpServletResponse

class FileUtils {

    /**
     * 根据地址下载文件
     */
    static void download(HttpServletResponse response, String filePath) {
        File file = new File(filePath)
        InputStream fis = new BufferedInputStream(new FileInputStream(filePath))
        byte[] buffer = new byte[fis.available()]
        fis.read(buffer)
        fis.close()
        OutputStream toClient = new BufferedOutputStream(response.getOutputStream())
        response.reset()
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(file.getName(), "UTF-8"))
        response.addHeader("Content-Length", "" + file.length())
        response.setContentType("application/octet-stream")
        toClient.write(buffer)
        toClient.flush()
        toClient.close()
    }
}
