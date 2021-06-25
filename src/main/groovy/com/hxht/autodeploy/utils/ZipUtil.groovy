package com.hxht.autodeploy.utils

import javax.servlet.http.HttpServletResponse
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

class ZipUtil {

    /**
     * 打包
     * @param srcFiles  打包的文件
     * @param zipFile   打包后的文件
     */
    static void zipFiles(File[] srcFiles, File zipFile) {
        // 判断压缩后的文件存在不，不存在则创建
        if (!zipFile.exists()) {
            try {
                zipFile.createNewFile()
            } catch (IOException e) {
                e.printStackTrace()
            }
        }
        // 创建 FileOutputStream 对象
        FileOutputStream fileOutputStream
        // 创建 ZipOutputStream
        ZipOutputStream zipOutputStream
        // 创建 FileInputStream 对象
        FileInputStream fileInputStream = null
        try {
            // 实例化 FileOutputStream 对象
            fileOutputStream = new FileOutputStream(zipFile)
            // 实例化 ZipOutputStream 对象
            zipOutputStream = new ZipOutputStream(fileOutputStream)
            // 创建 ZipEntry 对象
            ZipEntry zipEntry
            // 遍历源文件数组
            for (int i = 0; i < srcFiles.length; i++) {
                // 将源文件数组中的当前文件读入 FileInputStream 流中
                fileInputStream = new FileInputStream(srcFiles[i])
                // 实例化 ZipEntry 对象，源文件数组中的当前文件
                zipEntry = new ZipEntry(srcFiles[i].getName())
                zipOutputStream.putNextEntry(zipEntry)
                // 该变量记录每次真正读的字节个数
                int len
                // 定义每次读取的字节数组
                byte[] buffer = new byte[1024]
                while ((len = fileInputStream.read(buffer)) > 0) {
                    zipOutputStream.write(buffer, 0, len)
                }
            }
            zipOutputStream.closeEntry()
            zipOutputStream.close()
            fileInputStream.close()
            fileOutputStream.close()
        } catch (IOException e) {
            e.printStackTrace()
        }
    }

    static void download(HttpServletResponse response, File file, String filePath){
        InputStream fis = new BufferedInputStream(new FileInputStream(filePath))
        byte[] buffer = new byte[fis.available()]
        fis.read(buffer)
        fis.close()
        OutputStream toClient = new BufferedOutputStream(response.getOutputStream())
        response.reset()
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(file.getName(),"UTF-8"))
        response.addHeader("Content-Length", "" + file.length())
        response.setContentType("application/octet-stream")
        toClient.write(buffer)
        toClient.flush()
        toClient.close()
    }

    /**
     * zip解压缩
     */
    static void decompression(String inputFile,String destDirPath){
        File srcFile = new File(inputFile)//获取当前压缩文件
        // 判断源文件是否存在
        if (!srcFile.exists()) {
            throw new Exception(srcFile.getPath() + "所指文件不存在")
        }
        ZipFile zipFile = new ZipFile(srcFile)//创建压缩文件对象
        //开始解压
        Enumeration<?> entries = zipFile.entries()
        while (entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entries.nextElement()
            // 如果是文件夹，就创建个文件夹
            if (entry.isDirectory()) {
                String dirPath = destDirPath + File.separator + entry.getName()
                srcFile.mkdirs()
            } else {
                // 如果是文件，就先创建一个文件，然后用io流把内容copy过去
                File targetFile = new File(destDirPath + File.separator + entry.getName())
                // 保证这个文件的父文件夹必须要存在
                if (!targetFile.getParentFile().exists()) {
                    targetFile.getParentFile().mkdirs()
                }
                targetFile.createNewFile()
                // 将压缩文件内容写入到这个文件中
                InputStream is = zipFile.getInputStream(entry)
                FileOutputStream fos = new FileOutputStream(targetFile)
                int len
                byte[] buf = new byte[1024]
                while ((len = is.read(buf)) != -1) {
                    fos.write(buf, 0, len)
                }
                // 关流顺序，先打开的后关闭
                fos.close()
                is.close()
            }
        }
    }
}
