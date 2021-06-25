package com.hxht.autodeploy.utils


import java.nio.channels.FileChannel

class VideoReadUtil {
    /**
     * 视频大小
     *
     * @param source
     * @return
     */
    static String ReadVideoSize(File source) {
        FileChannel fc = null
        String size
        try {
            FileInputStream fis = new FileInputStream(source)
            fc = fis.getChannel()
            size = fc.size().toString()
        } catch (FileNotFoundException e) {
            e.printStackTrace()
        } catch (IOException e) {
            e.printStackTrace()
        } finally {
            if (null != fc) {
                try {
                    fc.close()
                } catch (IOException e) {
                    e.printStackTrace()
                }
            }
        }
        return size
    }
}
