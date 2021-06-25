package com.hxht.autodeploy.api

import com.hxht.techcrt.court.*
import grails.events.EventPublisher
import grails.gorm.transactions.Transactional
import grails.plugin.springwebsocket.WebSocket
import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.ftp.FTPFile
import org.apache.commons.net.ftp.FTPReply

@Transactional()
class ApiMoveVideoService implements WebSocket, EventPublisher{

    /**
     * 获取FTPClient对象
     * @param ftpHost 服务器IP
     * @param ftpPort 服务器端口号
     * @param ftpUserName 用户名
     * @param ftpPassword 密码
     * @return FTPClient
     */
    FTPClient login(String ftpHost, int ftpPort,
                                  String ftpUserName, String ftpPassword) {

        FTPClient ftp = null
        try {
            ftp = new FTPClient()
            // 连接FPT服务器,设置IP及端口
            ftp.connect(ftpHost, ftpPort)
            // 设置用户名和密码
            ftp.login(ftpUserName, ftpPassword)
            // 设置连接超时时间,5000毫秒
            ftp.setConnectTimeout(50000)
            // 设置中文编码集，防止中文乱码
            ftp.setControlEncoding("UTF-8")

            ftp.setFileType(FTPClient.BINARY_FILE_TYPE)
            //限制缓冲区大小
            ftp.setBufferSize(1024 * 1024 * 4)
            if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
                log.info("未连接到FTP，用户名或密码错误")
                ftp.disconnect()
            } else {
                log.info("FTP连接成功")
            }

        } catch (SocketException e) {
            e.printStackTrace()
            log.info("FTP的IP地址可能错误，请正确配置")
        } catch (IOException e) {
            e.printStackTrace()
            log.info("FTP的端口错误,请正确配置")
        }
        return ftp
    }


    /**
     * 关闭FTP方法
     * @param ftp
     * @return
     */
    boolean closeConnect(FTPClient ftp){
        if (ftp && ftp.isConnected()) {
            try {
                ftp.logout()
            } catch (Exception e) {
                log.error("FTP关闭失败")
            }finally{
                if (ftp.isConnected()) {
                    try {
                        ftp.disconnect()
                    } catch (IOException ioe) {
                        log.error("FTP关闭失败")
                    }
                }
            }
        }
        return false
    }

    static ftpName = []
    /**
     * 下载FTP下指定文件
     * @param ftp FTPClient对象
     * @param filePath FTP文件路径
     * @param fileName 文件名
     * @param downPath 下载保存的目录
     * @return
     */
    boolean downLoadFTP(FTPClient ftp, String filePath, String fileName,
                               String downPath) {
        // 默认失败
        boolean flag = false

        try {
            // 跳转到文件目录
            ftp.changeWorkingDirectory(filePath)
            // 获取目录下文件集合
            ftp.enterLocalPassiveMode()
            FTPFile[] files = ftp.listFiles()
            for (FTPFile file : files) {
                // 取得指定文件并下载
                if (file.getName().substring(file.getName().lastIndexOf(".")) == ".mp4"){
                    def ss= 0
                    for (def name: ftpName){
                        if (name == file.getName()){
                            ss =1
                        }
                    }
                    if (ss==0){
                        log.info("说明没有转移过 则进行转移！")
                        log.info("---->${file.getName()}文件不存在，准备执行下载")
                        def channel = file.getName().substring(0,file.getName().indexOf("_"))
                        def fileChn = fileName.substring(fileName.lastIndexOf("_") + 1,fileName.lastIndexOf("."))
                        if (channel == fileChn){
//                }
//                if (file.getName().equals(fileName)) {
                            File downFile = new File(downPath)
                            OutputStream out = new FileOutputStream(downFile)
                            // 绑定输出流下载文件,需要设置编码集，不然可能出现文件为空的情况
                            flag = ftp.retrieveFile(file.getName(), out)
                            out.flush()
                            out.close()
                            if(flag){
                                log.info("下载成功 并写到内存转移成功！")
                                ftpName.add(file.getName())
                                log.info(ftpName.toString())
                                /*try {
                                    //转存目录
                                    ftp.changeWorkingDirectory(filePath)
                                    ftp.enterLocalPassiveMode()
                                    //新文件夹不存在则创建
                                    if(!ftp.changeWorkingDirectory(filePath+"copy" + File.separator)){
                                        log.info("新文件夹不存在新建！${filePath +"copy" + File.separator}")
                                        ftp.makeDirectory(filePath + "copy" + File.separator)
                                    }
                                    //回到原有工作目录
                                    ftp.changeWorkingDirectory(filePath)
                                    log.info("回到原有工作目录！${filePath}")
                                    flag = ftp.rename(new String(file.getName().getBytes("UTF-8"),"ISO-8859-1"), filePath+"copy"+File.separator+new String(file.getName().getBytes("UTF-8"),"ISO-8859-1"))
                                    if(flag){
                                        log.info(file.getName()+"移动成功")
                                    }else{
                                        log.error(file.getName()+"移动失败")
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace()
                                    log.error("移动文件失败")
                                }*/
                            }else{
                                log.error("下载失败11")
                            }
                            break
                        }
                    }
                }
            }

        } catch (Exception e) {
            log.error("下载失败22  -----${e.message}")
        }
        return flag
    }

    def moveVideo(){
        def videos = [
                "/usr/local/movies/movies8/43dc984b3baa4bde8af54a0988bef43f/20181108/140.80.96.101_20181108_095348_1.mp4",
                "/usr/local/movies/movies8/43dc984b3baa4bde8af54a0988bef43f/20181108/140.80.96.101_20181108_095348_2.mp4",
                "/usr/local/movies/movies8/43dc984b3baa4bde8af54a0988bef43f/20181108/140.80.96.101_20181108_095348_3.mp4",
                "/usr/local/movies/movies8/43dc984b3baa4bde8af54a0988bef43f/20181108/140.80.96.101_20181108_095348_4.mp4",
                "/usr/local/movies/movies7/42ef3ab666774d0896a1d08d256f8f69/20181120/140.80.96.101_20181120_093631_3.mp4",
                "/usr/local/movies/movies7/42ef3ab666774d0896a1d08d256f8f69/20181120/140.80.96.101_20181120_100633_3.mp4",
                "/usr/local/movies/movies7/42ef3ab666774d0896a1d08d256f8f69/20181120/140.80.96.101_20181120_110640_1.mp4",
                "/usr/local/movies/movies7/42ef3ab666774d0896a1d08d256f8f69/20181120/140.80.96.101_20181120_114036_1.mp4",
                "/usr/local/movies/movies7/42ef3ab666774d0896a1d08d256f8f69/20181120/140.80.96.101_20181120_114036_2.mp4",
                "/usr/local/movies/movies7/42ef3ab666774d0896a1d08d256f8f69/20181120/140.80.96.101_20181120_114036_3.mp4",
                "/usr/local/movies/movies7/42ef3ab666774d0896a1d08d256f8f69/20181120/140.80.96.101_20181120_114036_4.mp4",
                "/usr/local/movies/movies8/32f36200608d49e8bbdcf917a6c7d181/20181206/140.80.96.101_20181206_083146_1.mp4",
                "/usr/local/movies/movies8/32f36200608d49e8bbdcf917a6c7d181/20181206/140.80.96.101_20181206_083146_4.mp4",
                "/usr/local/movies/movies8/32f36200608d49e8bbdcf917a6c7d181/20181206/140.80.96.101_20181206_090156_2.mp4",
                "/usr/local/movies/movies8/32f36200608d49e8bbdcf917a6c7d181/20181206/140.80.96.101_20181206_110202_1.mp4",
                "/usr/local/movies/movies8/32f36200608d49e8bbdcf917a6c7d181/20181206/140.80.96.101_20181206_113219_3.mp4",
                "/usr/local/movies/movies8/32f36200608d49e8bbdcf917a6c7d181/20181206/140.80.96.101_20181206_150015_2.mp4",
                "/usr/local/movies/movies8/32f36200608d49e8bbdcf917a6c7d181/20181206/140.80.96.101_20181206_163035_4.mp4",
                "/usr/local/movies/movies5/1e9aed965a6d49be96beaff2a892c4f6/20181213/140.80.96.101_20181213152223_0.mp4",
                "/usr/local/movies/movies5/1e9aed965a6d49be96beaff2a892c4f6/20181213/140.80.96.101_20181213152224_1.mp4",
                "/usr/local/movies/movies5/1e9aed965a6d49be96beaff2a892c4f6/20181213/140.80.96.101_20181213152225_3.mp4",
                "/usr/local/movies/movies5/1e9aed965a6d49be96beaff2a892c4f6/20181213/140.80.96.101_20181213152227_4.mp4",
                "/usr/local/movies/movies8/4f68044c451e42ab8b526cd5de4f463e/20190402/140.80.96.101_20190402_154719_1.mp4",
                "/usr/local/movies/movies8/4f68044c451e42ab8b526cd5de4f463e/20190402/140.80.96.101_20190402_154719_2.mp4",
                "/usr/local/movies/movies7/40c93fa171834c1a8f175e8d5541c9a3/20190409/140.80.96.101_20190409_150146_2.mp4",
                "/usr/local/movies/movies8/45a54f8ee1cb45b18ad04f771e6a5a9d/20190627/140.80.96.101_20190627_093016_1.mp4",
                "/usr/local/movies/movies8/45a54f8ee1cb45b18ad04f771e6a5a9d/20190627/140.80.96.101_20190627_100023_2.mp4",
                "/usr/local/movies/movies8/45a54f8ee1cb45b18ad04f771e6a5a9d/20190627/140.80.96.101_20190627_100024_1.mp4",
                "/usr/local/movies/movies8/45a54f8ee1cb45b18ad04f771e6a5a9d/20190627/140.80.96.101_20190627_103025_1.mp4",
                "/usr/local/movies/movies7/429a4e9b779d4b42982a83a63be4b5c4/20190828/140.80.96.101_20190828_090650_2.mp4",
                "/usr/local/movies/movies7/429a4e9b779d4b42982a83a63be4b5c4/20190828/140.80.96.101_20190828_103653_1.mp4",
                "/usr/local/movies/movies7/429a4e9b779d4b42982a83a63be4b5c4/20190828/140.80.96.101_20190828_110653_1.mp4",
                "/usr/local/movies/movies7/429a4e9b779d4b42982a83a63be4b5c4/20190828/140.80.96.101_20190828_110653_2.mp4",
                "/usr/local/movies/movies7/429a4e9b779d4b42982a83a63be4b5c4/20190828/140.80.96.101_20190828_113653_2.mp4",
                "/usr/local/movies/movies7/429a4e9b779d4b42982a83a63be4b5c4/20190828/140.80.96.101_20190828_113654_1.mp4",
                "/usr/local/movies/movies7/429a4e9b779d4b42982a83a63be4b5c4/20190828/140.80.96.101_20190828_140514_2.mp4",
                "/usr/local/movies/movies7/429a4e9b779d4b42982a83a63be4b5c4/20190828/140.80.96.101_20190828_140515_1.mp4",
                "/usr/local/movies/movies7/429a4e9b779d4b42982a83a63be4b5c4/20190828/140.80.96.101_20190828_143515_1.mp4",
                "/usr/local/movies/movies7/429a4e9b779d4b42982a83a63be4b5c4/20190828/140.80.96.101_20190828_143515_2.mp4",
                "/usr/local/movies/movies7/429a4e9b779d4b42982a83a63be4b5c4/20190828/140.80.96.101_20190828_150515_2.mp4",
                "/usr/local/movies/movies8/40c9237a4add4ceb9ab347713a3e394f/20190903/140.80.96.101_20190903_090106_1.mp4",
                "/usr/local/movies/movies8/40c9237a4add4ceb9ab347713a3e394f/20190903/140.80.96.101_20190903_090106_2.mp4",
                "/usr/local/movies/movies8/3f316e3a24994e1a840510d309c8ed81/20191119/140.80.96.101_20191119_155451_1.mp4",
                "/usr/local/movies/movies7/4381bf0989da4b50b4e64d42fa135341/20200115/140.80.96.101_20200115_100245_2.mp4",
                "/usr/local/movies/movies7/4381bf0989da4b50b4e64d42fa135341/20200115/140.80.96.101_20200115_103247_2.mp4",
                "/usr/local/movies/movies7/4381bf0989da4b50b4e64d42fa135341/20200115/140.80.96.101_20200115_140300_1.mp4",
                "/usr/local/movies/movies7/4381bf0989da4b50b4e64d42fa135341/20200115/140.80.96.101_20200115_143300_2.mp4",
                "/usr/local/movies/movies7/4381bf0989da4b50b4e64d42fa135341/20200115/140.80.96.101_20200115_150301_1.mp4",
                "/usr/local/movies/movies7/4381bf0989da4b50b4e64d42fa135341/20200115/140.80.96.101_20200115_150301_2.mp4",
                "/usr/local/movies/movies7/4381bf0989da4b50b4e64d42fa135341/20200115/140.80.96.101_20200115_154553_2.mp4",
                "/usr/local/movies/movies7/4381bf0989da4b50b4e64d42fa135341/20200115/140.80.96.101_20200115_161601_2.mp4",
                "/usr/local/movies/movies8/43dc984b3baa4bde8af54a0988bef43f/20181108/140.80.96.102_20181108_095348_1.mp4",
                "/usr/local/movies/movies8/43dc984b3baa4bde8af54a0988bef43f/20181108/140.80.96.102_20181108_095348_2.mp4",
                "/usr/local/movies/movies8/43dc984b3baa4bde8af54a0988bef43f/20181108/140.80.96.102_20181108_095348_3.mp4",
                "/usr/local/movies/movies8/43dc984b3baa4bde8af54a0988bef43f/20181108/140.80.96.102_20181108_095348_4.mp4",
                "/usr/local/movies/movies7/42ef3ab666774d0896a1d08d256f8f69/20181120/140.80.96.102_20181120_093632_1.mp4",
                "/usr/local/movies/movies7/42ef3ab666774d0896a1d08d256f8f69/20181120/140.80.96.102_20181120_100634_1.mp4",
                "/usr/local/movies/movies7/42ef3ab666774d0896a1d08d256f8f69/20181120/140.80.96.102_20181120_103634_2.mp4",
                "/usr/local/movies/movies7/42ef3ab666774d0896a1d08d256f8f69/20181120/140.80.96.102_20181120_110638_4.mp4",
                "/usr/local/movies/movies7/42ef3ab666774d0896a1d08d256f8f69/20181120/140.80.96.102_20181120_121043_4.mp4",
                "/usr/local/movies/movies7/42ef3ab666774d0896a1d08d256f8f69/20181120/140.80.96.102_20181120_121044_1.mp4",
                "/usr/local/movies/movies8/32f36200608d49e8bbdcf917a6c7d181/20181206/140.80.96.102_20181206_083147_4.mp4",
                "/usr/local/movies/movies8/32f36200608d49e8bbdcf917a6c7d181/20181206/140.80.96.102_20181206_090158_1.mp4",
                "/usr/local/movies/movies8/32f36200608d49e8bbdcf917a6c7d181/20181206/140.80.96.102_20181206_110213_3.mp4",
                "/usr/local/movies/movies8/32f36200608d49e8bbdcf917a6c7d181/20181206/140.80.96.102_20181206_113219_2.mp4",
                "/usr/local/movies/movies8/32f36200608d49e8bbdcf917a6c7d181/20181206/140.80.96.102_20181206_120220_3.mp4",
                "/usr/local/movies/movies8/32f36200608d49e8bbdcf917a6c7d181/20181206/140.80.96.102_20181206_123226_2.mp4",
                "/usr/local/movies/movies8/32f36200608d49e8bbdcf917a6c7d181/20181206/140.80.96.102_20181206_133227_1.mp4",
                "/usr/local/movies/movies8/32f36200608d49e8bbdcf917a6c7d181/20181206/140.80.96.102_20181206_140232_2.mp4",
                "/usr/local/movies/movies8/32f36200608d49e8bbdcf917a6c7d181/20181206/140.80.96.102_20181206_143232_3.mp4",
                "/usr/local/movies/movies8/32f36200608d49e8bbdcf917a6c7d181/20181206/140.80.96.102_20181206_150015_4.mp4",
                "/usr/local/movies/movies8/32f36200608d49e8bbdcf917a6c7d181/20181206/140.80.96.102_20181206_150237_3.mp4",
                "/usr/local/movies/movies8/32f36200608d49e8bbdcf917a6c7d181/20181206/140.80.96.102_20181206_153023_4.mp4",
                "/usr/local/movies/movies8/32f36200608d49e8bbdcf917a6c7d181/20181206/140.80.96.102_20181206_153243_3.mp4",
                "/usr/local/movies/movies5/1e9aed965a6d49be96beaff2a892c4f6/20181213/140.80.96.102_20181213152227_0.mp4",
                "/usr/local/movies/movies5/1e9aed965a6d49be96beaff2a892c4f6/20181213/140.80.96.102_20181213152228_1.mp4",
                "/usr/local/movies/movies5/1e9aed965a6d49be96beaff2a892c4f6/20181213/140.80.96.102_20181213152230_2.mp4",
                "/usr/local/movies/movies5/1e9aed965a6d49be96beaff2a892c4f6/20181213/140.80.96.102_20181213152231_3.mp4",
                "/usr/local/movies/movies8/4f68044c451e42ab8b526cd5de4f463e/20190402/140.80.96.102_20190402_154719_2.mp4",
                "/usr/local/movies/movies8/4f68044c451e42ab8b526cd5de4f463e/20190402/140.80.96.102_20190402_154719_4.mp4",
                "/usr/local/movies/movies7/40c93fa171834c1a8f175e8d5541c9a3/20190409/140.80.96.102_20190409_153153_4.mp4",
                "/usr/local/movies/movies8/45a54f8ee1cb45b18ad04f771e6a5a9d/20190627/140.80.96.102_20190627_100023_2.mp4",
                "/usr/local/movies/movies7/429a4e9b779d4b42982a83a63be4b5c4/20190828/140.80.96.102_20190828_093651_2.mp4",
                "/usr/local/movies/movies7/429a4e9b779d4b42982a83a63be4b5c4/20190828/140.80.96.102_20190828_093652_4.mp4",
                "/usr/local/movies/movies7/429a4e9b779d4b42982a83a63be4b5c4/20190828/140.80.96.102_20190828_100652_2.mp4",
                "/usr/local/movies/movies7/429a4e9b779d4b42982a83a63be4b5c4/20190828/140.80.96.102_20190828_110653_2.mp4",
                "/usr/local/movies/movies7/429a4e9b779d4b42982a83a63be4b5c4/20190828/140.80.96.102_20190828_133510_2.mp4",
                "/usr/local/movies/movies7/429a4e9b779d4b42982a83a63be4b5c4/20190828/140.80.96.102_20190828_140515_4.mp4",
                "/usr/local/movies/movies7/429a4e9b779d4b42982a83a63be4b5c4/20190828/140.80.96.102_20190828_143515_2.mp4",
                "/usr/local/movies/movies7/429a4e9b779d4b42982a83a63be4b5c4/20190828/140.80.96.102_20190828_143515_4.mp4",
                "/usr/local/movies/movies7/429a4e9b779d4b42982a83a63be4b5c4/20190828/140.80.96.102_20190828_153516_2.mp4",
                "/usr/local/movies/movies8/40c9237a4add4ceb9ab347713a3e394f/20190903/140.80.96.102_20190903_090106_2.mp4",
                "/usr/local/movies/movies8/40c9237a4add4ceb9ab347713a3e394f/20190903/140.80.96.102_20190903_090106_4.mp4",
                "/usr/local/movies/movies7/4381bf0989da4b50b4e64d42fa135341/20200115/140.80.96.102_20200115_110248_4.mp4",
                "/usr/local/movies/movies7/4381bf0989da4b50b4e64d42fa135341/20200115/140.80.96.102_20200115_113249_2.mp4",
                "/usr/local/movies/movies7/4381bf0989da4b50b4e64d42fa135341/20200115/140.80.96.102_20200115_133251_2.mp4",
                "/usr/local/movies/movies7/4381bf0989da4b50b4e64d42fa135341/20200115/140.80.96.102_20200115_133251_4.mp4",
                "/usr/local/movies/movies7/4381bf0989da4b50b4e64d42fa135341/20200115/140.80.96.102_20200115_143300_4.mp4",
                "/usr/local/movies/movies7/4381bf0989da4b50b4e64d42fa135341/20200115/140.80.96.102_20200115_164602_2.mp4",
                "/usr/local/movies/movies8/3252b7d9c2ae455ba6e729667922c3b8/20190326/140.80.96.106_20190326_090605_1.mp4",
                "/usr/local/movies/movies8/3252b7d9c2ae455ba6e729667922c3b8/20190326/140.80.96.106_20190326_090605_2.mp4",
                "/usr/local/movies/movies8/4f6f23dd14dc45198cca45fb8f64fe56/20190711/140.80.96.106_20190711_095801_1.mp4",
                "/usr/local/movies/movies8/4f6f23dd14dc45198cca45fb8f64fe56/20190711/140.80.96.106_20190711_095801_2.mp4",
                "/usr/local/movies/movies8/3685ba6790534e35abfbaec844ced478/20190718/140.80.96.106_20190718_093353_1.mp4",
                "/usr/local/movies/movies8/3685ba6790534e35abfbaec844ced478/20190718/140.80.96.106_20190718_093353_2.mp4",
                "/usr/local/movies/movies7/3bf23aa4051b46e5a2a02f68eb8bfd25/20190809/140.80.96.106_20190809_090046_1.mp4",
                "/usr/local/movies/movies7/3bf23aa4051b46e5a2a02f68eb8bfd25/20190809/140.80.96.106_20190809_090046_2.mp4",
                "/usr/local/movies/movies7/3bf23aa4051b46e5a2a02f68eb8bfd25/20190809/140.80.96.106_20190809_093050_1.mp4",
                "/usr/local/movies/movies7/3bf23aa4051b46e5a2a02f68eb8bfd25/20190809/140.80.96.106_20190809_093050_2.mp4",
                "/usr/local/movies/movies7/41199cc50fa8462cbde37ad32051ee6a/20200304/140.80.96.106_20200304_150329_1.mp4",
                "/usr/local/movies/movies7/41199cc50fa8462cbde37ad32051ee6a/20200304/140.80.96.106_20200304_150329_2.mp4",
                "/usr/local/movies/movies7/41199cc50fa8462cbde37ad32051ee6a/20200304/140.80.96.106_20200304_153331_2.mp4",
                "/usr/local/movies/movies7/41199cc50fa8462cbde37ad32051ee6a/20200304/140.80.96.106_20200304_160331_1.mp4",
                "/usr/local/movies/movies7/41199cc50fa8462cbde37ad32051ee6a/20200304/140.80.96.106_20200304_160331_2.mp4",
                "/usr/local/movies/movies8/3252b7d9c2ae455ba6e729667922c3b8/20190326/140.80.96.107_20190326_090605_4.mp4",
                "/usr/local/movies/movies8/4f6f23dd14dc45198cca45fb8f64fe56/20190711/140.80.96.107_20190711_095801_4.mp4",
                "/usr/local/movies/movies8/3685ba6790534e35abfbaec844ced478/20190718/140.80.96.107_20190718_093353_4.mp4",
                "/usr/local/movies/movies7/3bf23aa4051b46e5a2a02f68eb8bfd25/20190809/140.80.96.107_20190809_090046_4.mp4",
                "/usr/local/movies/movies7/3bf23aa4051b46e5a2a02f68eb8bfd25/20190809/140.80.96.107_20190809_093050_4.mp4",
                "/usr/local/movies/movies7/41199cc50fa8462cbde37ad32051ee6a/20200304/140.80.96.107_20200304_150329_4.mp4",
                "/usr/local/movies/movies7/41199cc50fa8462cbde37ad32051ee6a/20200304/140.80.96.107_20200304_153331_4.mp4",
                "/usr/local/movies/movies7/41199cc50fa8462cbde37ad32051ee6a/20200304/140.80.96.107_20200304_160331_4.mp4",
                "/usr/local/movies/movies8/393c91b3b79f46b79c8d420cdb170afc/20190628/140.80.96.111_20190628_083112_1.mp4",
                "/usr/local/movies/movies8/393c91b3b79f46b79c8d420cdb170afc/20190628/140.80.96.111_20190628_083112_2.mp4",
                "/usr/local/movies/movies8/393c91b3b79f46b79c8d420cdb170afc/20190628/140.80.96.111_20190628_090117_1.mp4",
                "/usr/local/movies/movies8/393c91b3b79f46b79c8d420cdb170afc/20190628/140.80.96.111_20190628_090117_2.mp4",
                "/usr/local/movies/movies8/393c91b3b79f46b79c8d420cdb170afc/20190628/140.80.96.111_20190628_093117_2.mp4",
                "/usr/local/movies/movies8/393c91b3b79f46b79c8d420cdb170afc/20190628/140.80.96.111_20190628_093118_1.mp4",
                "/usr/local/movies/movies7/35817ee4b1384c9bb71433cf90c81178/20190905/140.80.96.111_20190905_090110_1.mp4",
                "/usr/local/movies/movies7/35817ee4b1384c9bb71433cf90c81178/20190905/140.80.96.111_20190905_090110_2.mp4",
                "/usr/local/movies/movies7/35817ee4b1384c9bb71433cf90c81178/20190905/140.80.96.111_20190905_093116_1.mp4",
                "/usr/local/movies/movies7/35817ee4b1384c9bb71433cf90c81178/20190905/140.80.96.111_20190905_093116_2.mp4",
                "/usr/local/movies/movies8/393c91b3b79f46b79c8d420cdb170afc/20190628/140.80.96.112_20190628_083112_4.mp4",
                "/usr/local/movies/movies8/393c91b3b79f46b79c8d420cdb170afc/20190628/140.80.96.112_20190628_090117_4.mp4",
                "/usr/local/movies/movies8/393c91b3b79f46b79c8d420cdb170afc/20190628/140.80.96.112_20190628_093117_4.mp4",
                "/usr/local/movies/movies7/35817ee4b1384c9bb71433cf90c81178/20190905/140.80.96.112_20190905_090110_4.mp4",
                "/usr/local/movies/movies7/35817ee4b1384c9bb71433cf90c81178/20190905/140.80.96.112_20190905_093116_4.mp4",
                "/usr/local/movies/movies8/3c1219c614e04fc480fe853a5bf0fe15/20200312/140.80.96.112_20200312_100029_4.mp4",
                "/usr/local/movies/movies7/3d1056090df6405d92d3590ff39b40c7/20181107/140.80.96.116_20181107_085953_1.mp4",
                "/usr/local/movies/movies7/323aa6d29a9e4a48938c546dc0189950/20181224/140.80.96.116_20181224_104005_1.mp4",
                "/usr/local/movies/movies7/3e7b5656cfbe452fae2454006b5baf4b/20190522/140.80.96.116_20190522_105724_1.mp4",
                "/usr/local/movies/movies7/3e7b5656cfbe452fae2454006b5baf4b/20190522/140.80.96.116_20190522_112727_1.mp4",
                "/usr/local/movies/movies7/3e7b5656cfbe452fae2454006b5baf4b/20190522/140.80.96.116_20190522_115727_1.mp4",
                "/usr/local/movies/movies7/3e7b5656cfbe452fae2454006b5baf4b/20190522/140.80.96.116_20190522_122727_1.mp4",
                "/usr/local/movies/movies7/3e7b5656cfbe452fae2454006b5baf4b/20190522/140.80.96.116_20190522_125727_1.mp4",
                "/usr/local/movies/movies7/3e7b5656cfbe452fae2454006b5baf4b/20190522/140.80.96.116_20190522_132727_1.mp4",
                "/usr/local/movies/movies7/3e7b5656cfbe452fae2454006b5baf4b/20190522/140.80.96.116_20190522_135727_1.mp4",
                "/usr/local/movies/movies7/3e7b5656cfbe452fae2454006b5baf4b/20190522/140.80.96.116_20190522_142727_1.mp4",
                "/usr/local/movies/movies8/3db7816ba72f426e8c4ae1bd79b01d8f/20200430/140.80.96.121_20200430_090802_1.mp4",
                "/usr/local/movies/movies8/3db7816ba72f426e8c4ae1bd79b01d8f/20200430/140.80.96.121_20200430_093802_1.mp4",
                "/usr/local/movies/movies7/3d92405eb6f2494c82686dd66b035046/20190409/140.80.96.126_20190409_090239_1.mp4",
                "/usr/local/movies/movies7/3fe387bda0be48b9b92dd82196e2e2a5/20200707/140.80.96.126_20200707_100145_1.mp4",
                "/usr/local/movies/movies7/40b092406cb14c48ad51e87227f06f91/20181105/140.80.96.136_20181105_153453_1.mp4",
                "/usr/local/movies/movies7/40b092406cb14c48ad51e87227f06f91/20181105/140.80.96.136_20181105_153453_2.mp4",
                "/usr/local/movies/movies7/40b092406cb14c48ad51e87227f06f91/20181105/140.80.96.136_20181105_160456_1.mp4",
                "/usr/local/movies/movies7/40b092406cb14c48ad51e87227f06f91/20181105/140.80.96.136_20181105_160456_2.mp4",
                "/usr/local/movies/movies7/40b092406cb14c48ad51e87227f06f91/20181105/140.80.96.137_20181105_153453_4.mp4",
                "/usr/local/movies/movies7/40b092406cb14c48ad51e87227f06f91/20181105/140.80.96.137_20181105_160456_4.mp4",
                "/usr/local/movies/movies7/3d64c949d2f84ddbb92f238be95c5d00/20181119/140.80.96.141_20181119_150731_2.mp4",
                "/usr/local/movies/movies7/502ef725eab54d0cb37c43fbe6cd5a84/20200527/140.80.96.141_20200527_094409_1.mp4",
                "/usr/local/movies/movies7/502ef725eab54d0cb37c43fbe6cd5a84/20200527/140.80.96.141_20200527_094409_2.mp4",
                "/usr/local/movies/movies7/34ac4748b41f4a8cbe6d933f5cfb2afc/20200709/140.80.96.141_20200709_160248_1.mp4",
                "/usr/local/movies/movies7/34ac4748b41f4a8cbe6d933f5cfb2afc/20200709/140.80.96.141_20200709_160248_2.mp4",
                "/usr/local/movies/movies7/34ac4748b41f4a8cbe6d933f5cfb2afc/20200709/140.80.96.141_20200709_163255_1.mp4",
                "/usr/local/movies/movies8/3c61a10682f24b86843eb626924a802e/20200722/140.80.96.141_20200722_091644_1.mp4",
                "/usr/local/movies/movies7/3d64c949d2f84ddbb92f238be95c5d00/20181119/140.80.96.142_20181119_150731_4.mp4",
                "/usr/local/movies/movies7/502ef725eab54d0cb37c43fbe6cd5a84/20200527/140.80.96.142_20200527_094409_4.mp4",
                "/usr/local/movies/movies7/34ac4748b41f4a8cbe6d933f5cfb2afc/20200709/140.80.96.142_20200709_160248_4.mp4",
                "/usr/local/movies/movies7/34ac4748b41f4a8cbe6d933f5cfb2afc/20200709/140.80.96.142_20200709_163255_4.mp4",
                "/usr/local/movies/movies10/1d69530a6aeb4a6db1c0a50f7a37c4a0/20180410/140.80.96.162_20180410_095536_1.mp4",
                "/usr/local/movies/movies10/1d69530a6aeb4a6db1c0a50f7a37c4a0/20180410/140.80.96.162_20180410_095536_2.mp4",
                "/usr/local/movies/movies10/1d69530a6aeb4a6db1c0a50f7a37c4a0/20180410/140.80.96.162_20180410_095536_3.mp4",
                "/usr/local/movies/movies10/292dcca92ebd46b288402dd04e5c5e84/20180806/140.80.96.169_20180806_155512_3.mp4",
                "/usr/local/movies/movies10/292dcca92ebd46b288402dd04e5c5e84/20180806/140.80.96.169_20180806_155513_1.mp4",
                "/usr/local/movies/movies10/292dcca92ebd46b288402dd04e5c5e84/20180806/140.80.96.169_20180806_155513_2.mp4",
                "/usr/local/movies/movies10/0d314667c9b84e7c9f50e06da1120866/20180807/140.80.96.169_20180807_154357_1.mp4",
                "/usr/local/movies/movies10/0d314667c9b84e7c9f50e06da1120866/20180807/140.80.96.169_20180807_154357_2.mp4",
                "/usr/local/movies/movies10/0d314667c9b84e7c9f50e06da1120866/20180807/140.80.96.169_20180807_154357_3.mp4",
                "/usr/local/movies/movies10/319f17fc0e8f46d7a126ec5f0fa065a7/20180814/140.80.96.169_20180814_161804_1.mp4",
                "/usr/local/movies/movies10/319f17fc0e8f46d7a126ec5f0fa065a7/20180814/140.80.96.169_20180814_161804_3.mp4",
                "/usr/local/movies/movies10/319f17fc0e8f46d7a126ec5f0fa065a7/20180814/140.80.96.169_20180814_161805_2.mp4",
                "/usr/local/movies/movies6/43f951a655a247ca82db6fb93cc0237a/20160310/205.73.8.131_20160310_093144_4.mp4",
                "/usr/local/movies/movies6/45223361fb784c41affb329b682ef543/20160323/205.73.8.131_20160323_104434_4.mp4",
                "/usr/local/movies/movies6/45223361fb784c41affb329b682ef543/20160323/205.73.8.131_20160323_104435_2.mp4",
                "/usr/local/movies/movies9/418d72dbf1a943abb7506a8a9f25f904/20170117/205.73.8.131_20170117_155056_1.mp4",
                "/usr/local/movies/movies9/418d72dbf1a943abb7506a8a9f25f904/20170117/205.73.8.131_20170117_155056_2.mp4",
                "/usr/local/movies/movies9/418d72dbf1a943abb7506a8a9f25f904/20170117/205.73.8.131_20170117_155056_3.mp4",
                "/usr/local/movies/movies9/418d72dbf1a943abb7506a8a9f25f904/20170117/205.73.8.131_20170117_155056_4.mp4",
                "/usr/local/movies/movies9/42de33ab783847d2817bd8f4794fed28/20170222/205.73.8.131_20170222_085428_1.mp4",
                "/usr/local/movies/movies9/42de33ab783847d2817bd8f4794fed28/20170222/205.73.8.131_20170222_085428_3.mp4",
                "/usr/local/movies/movies9/3c49d7117fb44304bd25de226683088b/20170309/205.73.8.131_20170309_093519_3.mp4",
                "/usr/local/movies/movies9/3c49d7117fb44304bd25de226683088b/20170309/205.73.8.131_20170309_100521_3.mp4",
                "/usr/local/movies/movies9/3c49d7117fb44304bd25de226683088b/20170309/205.73.8.131_20170309_100521_4.mp4",
                "/usr/local/movies/movies9/3943ce2efacd46f4a8aaab1a7b91dbb3/20170426/205.73.8.131_20170426_100018_4.mp4",
                "/usr/local/movies/movies9/44852b2b6e8542edb55605000df4b28c/20170517/205.73.8.131_20170517_093423_4.mp4",
                "/usr/local/movies/movies9/44852b2b6e8542edb55605000df4b28c/20170517/205.73.8.131_20170517_103426_4.mp4",
                "/usr/local/movies/movies9/44852b2b6e8542edb55605000df4b28c/20170517/205.73.8.131_20170517_110427_3.mp4",
                "/usr/local/movies/movies9/44852b2b6e8542edb55605000df4b28c/20170517/205.73.8.131_20170517_111329_4.mp4",
                "/usr/local/movies/movies9/44852b2b6e8542edb55605000df4b28c/20170517/205.73.8.131_20170517_114339_1.mp4",
                "/usr/local/movies/movies9/44852b2b6e8542edb55605000df4b28c/20170517/205.73.8.131_20170517_114339_2.mp4",
                "/usr/local/movies/movies9/44852b2b6e8542edb55605000df4b28c/20170517/205.73.8.131_20170517_130447_4.mp4",
                "/usr/local/movies/movies9/44852b2b6e8542edb55605000df4b28c/20170517/205.73.8.131_20170517_133450_2.mp4",
                "/usr/local/movies/movies9/44852b2b6e8542edb55605000df4b28c/20170517/205.73.8.131_20170517_140451_2.mp4",
                "/usr/local/movies/movies9/44852b2b6e8542edb55605000df4b28c/20170517/205.73.8.131_20170517_140451_3.mp4",
                "/usr/local/movies/movies9/44852b2b6e8542edb55605000df4b28c/20170517/205.73.8.131_20170517_150453_2.mp4",
                "/usr/local/movies/movies9/44852b2b6e8542edb55605000df4b28c/20170517/205.73.8.131_20170517_153455_2.mp4",
                "/usr/local/movies/movies9/44852b2b6e8542edb55605000df4b28c/20170517/205.73.8.131_20170517_163458_2.mp4",
                "/usr/local/movies/movies9/44852b2b6e8542edb55605000df4b28c/20170517/205.73.8.131_20170517_173529_2.mp4",
                "/usr/local/movies/movies9/44852b2b6e8542edb55605000df4b28c/20170517/205.73.8.131_20170517_173529_4.mp4",
                "/usr/local/movies/movies9/392ea7755dcf47a0b492b26a02bfc53a/20170601/205.73.8.131_20170601_100121_1.mp4",
                "/usr/local/movies/movies9/392ea7755dcf47a0b492b26a02bfc53a/20170601/205.73.8.131_20170601_100121_2.mp4",
                "/usr/local/movies/movies9/392ea7755dcf47a0b492b26a02bfc53a/20170601/205.73.8.131_20170601_100121_3.mp4",
                "/usr/local/movies/movies7/4374463de2574e9ebcb56364cb7aeb6a/20171128/205.73.8.131_20171128_090603_3.mp4",
                "/usr/local/movies/movies8/357fae9d9dcb4151897b5345e7dea3f3/20171214/205.73.8.131_20171214_161959_1.mp4",
                "/usr/local/movies/movies8/357fae9d9dcb4151897b5345e7dea3f3/20171214/205.73.8.131_20171214_161959_2.mp4",
                "/usr/local/movies/movies8/357fae9d9dcb4151897b5345e7dea3f3/20171214/205.73.8.131_20171214_161959_3.mp4",
                "/usr/local/movies/movies8/357fae9d9dcb4151897b5345e7dea3f3/20171214/205.73.8.131_20171214_161959_4.mp4",
                "/usr/local/movies/movies7/42a4393beddb47099b7240318920c16a/20171215/205.73.8.131_20171215_083313_4.mp4",
                "/usr/local/movies/movies7/42a4393beddb47099b7240318920c16a/20171215/205.73.8.131_20171215_090323_1.mp4",
                "/usr/local/movies/movies7/42a4393beddb47099b7240318920c16a/20171215/205.73.8.131_20171215_093324_2.mp4",
                "/usr/local/movies/movies7/42a4393beddb47099b7240318920c16a/20171215/205.73.8.131_20171215_093324_3.mp4",
                "/usr/local/movies/movies7/42a4393beddb47099b7240318920c16a/20171215/205.73.8.131_20171215_093324_4.mp4",
                "/usr/local/movies/movies7/42a4393beddb47099b7240318920c16a/20171215/205.73.8.131_20171215_100326_1.mp4",
                "/usr/local/movies/movies7/42a4393beddb47099b7240318920c16a/20171215/205.73.8.131_20171215_100326_2.mp4",
                "/usr/local/movies/movies7/42a4393beddb47099b7240318920c16a/20171215/205.73.8.131_20171215_103328_1.mp4",
                "/usr/local/movies/movies7/42a4393beddb47099b7240318920c16a/20171215/205.73.8.131_20171215_110329_3.mp4",
                "/usr/local/movies/movies7/42a4393beddb47099b7240318920c16a/20171215/205.73.8.131_20171215_110329_4.mp4",
                "/usr/local/movies/movies7/42a4393beddb47099b7240318920c16a/20171215/205.73.8.131_20171215_113331_1.mp4",
                "/usr/local/movies/movies7/42a4393beddb47099b7240318920c16a/20171215/205.73.8.131_20171215_113331_2.mp4",
                "/usr/local/movies/movies7/42a4393beddb47099b7240318920c16a/20171215/205.73.8.131_20171215_113331_4.mp4",
                "/usr/local/movies/movies7/443bea1f2fad48ffb32f253cd8c02311/20180327/205.73.8.131_20180327_083657_2.mp4",
                "/usr/local/movies/movies7/443bea1f2fad48ffb32f253cd8c02311/20180327/205.73.8.131_20180327_094637_2.mp4",
                "/usr/local/movies/movies7/443bea1f2fad48ffb32f253cd8c02311/20180327/205.73.8.131_20180327_094637_3.mp4",
                "/usr/local/movies/movies7/443bea1f2fad48ffb32f253cd8c02311/20180327/205.73.8.131_20180327_101645_1.mp4",
                "/usr/local/movies/movies7/443bea1f2fad48ffb32f253cd8c02311/20180327/205.73.8.131_20180327_101645_2.mp4",
                "/usr/local/movies/movies7/443bea1f2fad48ffb32f253cd8c02311/20180327/205.73.8.131_20180327_101645_4.mp4",
                "/usr/local/movies/movies7/443bea1f2fad48ffb32f253cd8c02311/20180327/205.73.8.131_20180327_104646_3.mp4",
                "/usr/local/movies/movies7/443bea1f2fad48ffb32f253cd8c02311/20180327/205.73.8.131_20180327_114649_2.mp4",
                "/usr/local/movies/movies7/443bea1f2fad48ffb32f253cd8c02311/20180327/205.73.8.131_20180327_124652_2.mp4",
                "/usr/local/movies/movies7/443bea1f2fad48ffb32f253cd8c02311/20180327/205.73.8.131_20180327_124652_4.mp4",
                "/usr/local/movies/movies6/500de9ac03c748ba8cb566ee0d0ac9a3/20141210/205.73.8.132_20141210_155720_1r.mp4",
                "/usr/local/movies/movies6/500de9ac03c748ba8cb566ee0d0ac9a3/20141210/205.73.8.132_20141210_155720_2r.mp4",
                "/usr/local/movies/movies6/500de9ac03c748ba8cb566ee0d0ac9a3/20141210/205.73.8.132_20141210_155720_3r.mp4",
                "/usr/local/movies/movies6/500de9ac03c748ba8cb566ee0d0ac9a3/20141210/205.73.8.132_20141210_165601_0r.mp4",
                "/usr/local/movies/movies6/2f91b605dfa746dc9bf7722351373889/20141224/205.73.8.132_20141224_095755_2r.mp4",
                "/usr/local/movies/movies6/2f91b605dfa746dc9bf7722351373889/20141224/205.73.8.132_20141224_102756_0r.mp4",
                "/usr/local/movies/movies6/45a91c4b5e82475595e6a8da869f38ff/20150428/205.73.8.132_20150428_155340_0r.mp4",
                "/usr/local/movies/movies6/45a91c4b5e82475595e6a8da869f38ff/20150428/205.73.8.132_20150428_155340_2r.mp4",
                "/usr/local/movies/movies6/3edf92adaa034a4ebe92569847176f14/20150526/205.73.8.132_20150526_090051_1r.mp4",
                "/usr/local/movies/movies6/3edf92adaa034a4ebe92569847176f14/20150526/205.73.8.132_20150526_093102_3r.mp4",
                "/usr/local/movies/movies6/33c75761a2ca4cee938de23709aacc53/20150528/205.73.8.132_20150528_154541_0r.mp4",
                "/usr/local/movies/movies6/33c75761a2ca4cee938de23709aacc53/20150528/205.73.8.132_20150528_154541_1r.mp4",
                "/usr/local/movies/movies6/33c75761a2ca4cee938de23709aacc53/20150528/205.73.8.132_20150528_154541_2r.mp4",
                "/usr/local/movies/movies6/33c75761a2ca4cee938de23709aacc53/20150528/205.73.8.132_20150528_154541_3r.mp4",
                "/usr/local/movies/movies6/3afe5f4086e44a40929e672b825319bb/20150616/205.73.8.132_20150616_160711_2r.mp4",
                "/usr/local/movies/movies6/3fdb69ad458e43b4b4425fbc40554a3e/20151030/205.73.8.132_20151030_094856_2r.mp4",
                "/usr/local/movies/movies6/3fdb69ad458e43b4b4425fbc40554a3e/20151030/205.73.8.132_20151030_095033_0r.mp4",
                "/usr/local/movies/movies6/3fdb69ad458e43b4b4425fbc40554a3e/20151030/205.73.8.132_20151030_111900_1r.mp4",
                "/usr/local/movies/movies6/3fdb69ad458e43b4b4425fbc40554a3e/20151030/205.73.8.132_20151030_111900_2r.mp4",
                "/usr/local/movies/movies6/3fdb69ad458e43b4b4425fbc40554a3e/20151030/205.73.8.132_20151030_111900_3r.mp4",
                "/usr/local/movies/movies6/3fdb69ad458e43b4b4425fbc40554a3e/20151030/205.73.8.132_20151030_112041_0r.mp4",
                "/usr/local/movies/movies6/3fdb69ad458e43b4b4425fbc40554a3e/20151030/205.73.8.132_20151030_114901_2r.mp4",
                "/usr/local/movies/movies6/3fdb69ad458e43b4b4425fbc40554a3e/20151030/205.73.8.132_20151030_114901_3r.mp4",
                "/usr/local/movies/movies6/3fdb69ad458e43b4b4425fbc40554a3e/20151030/205.73.8.132_20151030_115041_0r.mp4",
                "/usr/local/movies/movies6/31204189f38041428e6a4f15408bbff3/20151225/205.73.8.132_20151225_150242_2r.mp4",
                "/usr/local/movies/movies6/31204189f38041428e6a4f15408bbff3/20151225/205.73.8.132_20151225_153246_2r.mp4",
                "/usr/local/movies/movies6/31204189f38041428e6a4f15408bbff3/20151225/205.73.8.132_20151225_160247_3r.mp4",
                "/usr/local/movies/movies6/31204189f38041428e6a4f15408bbff3/20151225/205.73.8.132_20151225_163248_1r.mp4",
                "/usr/local/movies/movies6/43f951a655a247ca82db6fb93cc0237a/20160310/205.73.8.132_20160310_093144_1.mp4",
                "/usr/local/movies/movies6/43f951a655a247ca82db6fb93cc0237a/20160310/205.73.8.132_20160310_093144_3.mp4",
                "/usr/local/movies/movies6/45223361fb784c41affb329b682ef543/20160323/205.73.8.132_20160323_094430_1.mp4",
                "/usr/local/movies/movies6/45223361fb784c41affb329b682ef543/20160323/205.73.8.132_20160323_101432_4.mp4",
                "/usr/local/movies/movies6/45223361fb784c41affb329b682ef543/20160323/205.73.8.132_20160323_101433_1.mp4",
                "/usr/local/movies/movies6/45223361fb784c41affb329b682ef543/20160323/205.73.8.132_20160323_104433_3.mp4",
                "/usr/local/movies/movies6/45223361fb784c41affb329b682ef543/20160323/205.73.8.132_20160323_104434_1.mp4",
                "/usr/local/movies/movies6/45223361fb784c41affb329b682ef543/20160323/205.73.8.132_20160323_104434_2.mp4",
                "/usr/local/movies/movies9/418d72dbf1a943abb7506a8a9f25f904/20170117/205.73.8.132_20170117_155056_1.mp4",
                "/usr/local/movies/movies9/418d72dbf1a943abb7506a8a9f25f904/20170117/205.73.8.132_20170117_155056_2.mp4",
                "/usr/local/movies/movies9/418d72dbf1a943abb7506a8a9f25f904/20170117/205.73.8.132_20170117_155056_3.mp4",
                "/usr/local/movies/movies9/418d72dbf1a943abb7506a8a9f25f904/20170117/205.73.8.132_20170117_155056_4.mp4",
                "/usr/local/movies/movies9/42de33ab783847d2817bd8f4794fed28/20170222/205.73.8.132_20170222_085429_1.mp4",
                "/usr/local/movies/movies9/42de33ab783847d2817bd8f4794fed28/20170222/205.73.8.132_20170222_085429_3.mp4",
                "/usr/local/movies/movies9/3c49d7117fb44304bd25de226683088b/20170309/205.73.8.132_20170309_090519_1.mp4",
                "/usr/local/movies/movies9/3c49d7117fb44304bd25de226683088b/20170309/205.73.8.132_20170309_090519_3.mp4",
                "/usr/local/movies/movies9/3c49d7117fb44304bd25de226683088b/20170309/205.73.8.132_20170309_093530_3.mp4",
                "/usr/local/movies/movies9/3c49d7117fb44304bd25de226683088b/20170309/205.73.8.132_20170309_100531_4.mp4",
                "/usr/local/movies/movies9/3943ce2efacd46f4a8aaab1a7b91dbb3/20170426/205.73.8.132_20170426_100018_2.mp4",
                "/usr/local/movies/movies9/3943ce2efacd46f4a8aaab1a7b91dbb3/20170426/205.73.8.132_20170426_100018_3.mp4",
                "/usr/local/movies/movies9/44852b2b6e8542edb55605000df4b28c/20170517/205.73.8.132_20170517_085259_3.mp4",
                "/usr/local/movies/movies9/44852b2b6e8542edb55605000df4b28c/20170517/205.73.8.132_20170517_090414_2.mp4",
                "/usr/local/movies/movies9/44852b2b6e8542edb55605000df4b28c/20170517/205.73.8.132_20170517_093423_3.mp4",
                "/usr/local/movies/movies9/44852b2b6e8542edb55605000df4b28c/20170517/205.73.8.132_20170517_103425_2.mp4",
                "/usr/local/movies/movies9/44852b2b6e8542edb55605000df4b28c/20170517/205.73.8.132_20170517_103425_3.mp4",
                "/usr/local/movies/movies9/44852b2b6e8542edb55605000df4b28c/20170517/205.73.8.132_20170517_110427_1.mp4",
                "/usr/local/movies/movies9/44852b2b6e8542edb55605000df4b28c/20170517/205.73.8.132_20170517_111329_2.mp4",
                "/usr/local/movies/movies9/44852b2b6e8542edb55605000df4b28c/20170517/205.73.8.132_20170517_111329_3.mp4",
                "/usr/local/movies/movies9/44852b2b6e8542edb55605000df4b28c/20170517/205.73.8.132_20170517_114338_1.mp4",
                "/usr/local/movies/movies9/44852b2b6e8542edb55605000df4b28c/20170517/205.73.8.132_20170517_114338_2.mp4",
                "/usr/local/movies/movies9/44852b2b6e8542edb55605000df4b28c/20170517/205.73.8.132_20170517_114338_4.mp4",
                "/usr/local/movies/movies9/44852b2b6e8542edb55605000df4b28c/20170517/205.73.8.132_20170517_130447_1.mp4",
                "/usr/local/movies/movies9/44852b2b6e8542edb55605000df4b28c/20170517/205.73.8.132_20170517_130447_4.mp4",
                "/usr/local/movies/movies9/44852b2b6e8542edb55605000df4b28c/20170517/205.73.8.132_20170517_133449_2.mp4",
                "/usr/local/movies/movies9/44852b2b6e8542edb55605000df4b28c/20170517/205.73.8.132_20170517_143444_1.mp4",
                "/usr/local/movies/movies9/44852b2b6e8542edb55605000df4b28c/20170517/205.73.8.132_20170517_143444_2.mp4",
                "/usr/local/movies/movies9/44852b2b6e8542edb55605000df4b28c/20170517/205.73.8.132_20170517_143444_3.mp4",
                "/usr/local/movies/movies9/44852b2b6e8542edb55605000df4b28c/20170517/205.73.8.132_20170517_143444_4.mp4",
                "/usr/local/movies/movies9/44852b2b6e8542edb55605000df4b28c/20170517/205.73.8.132_20170517_150452_3.mp4",
                "/usr/local/movies/movies9/44852b2b6e8542edb55605000df4b28c/20170517/205.73.8.132_20170517_150452_4.mp4",
                "/usr/local/movies/movies9/44852b2b6e8542edb55605000df4b28c/20170517/205.73.8.132_20170517_153453_3.mp4",
                "/usr/local/movies/movies9/44852b2b6e8542edb55605000df4b28c/20170517/205.73.8.132_20170517_153454_1.mp4",
                "/usr/local/movies/movies9/44852b2b6e8542edb55605000df4b28c/20170517/205.73.8.132_20170517_160455_4.mp4",
                "/usr/local/movies/movies9/44852b2b6e8542edb55605000df4b28c/20170517/205.73.8.132_20170517_173528_4.mp4",
                "/usr/local/movies/movies9/392ea7755dcf47a0b492b26a02bfc53a/20170601/205.73.8.132_20170601_100121_3.mp4",
                "/usr/local/movies/movies7/4374463de2574e9ebcb56364cb7aeb6a/20171128/205.73.8.132_20171128_090605_3.mp4",
                "/usr/local/movies/movies8/357fae9d9dcb4151897b5345e7dea3f3/20171214/205.73.8.132_20171214_161959_1.mp4",
                "/usr/local/movies/movies8/357fae9d9dcb4151897b5345e7dea3f3/20171214/205.73.8.132_20171214_161959_2.mp4",
                "/usr/local/movies/movies8/357fae9d9dcb4151897b5345e7dea3f3/20171214/205.73.8.132_20171214_161959_3.mp4",
                "/usr/local/movies/movies8/357fae9d9dcb4151897b5345e7dea3f3/20171214/205.73.8.132_20171214_161959_4.mp4",
                "/usr/local/movies/movies7/42a4393beddb47099b7240318920c16a/20171215/205.73.8.132_20171215_083315_4.mp4",
                "/usr/local/movies/movies7/42a4393beddb47099b7240318920c16a/20171215/205.73.8.132_20171215_090322_4.mp4",
                "/usr/local/movies/movies7/42a4393beddb47099b7240318920c16a/20171215/205.73.8.132_20171215_100325_2.mp4",
                "/usr/local/movies/movies7/42a4393beddb47099b7240318920c16a/20171215/205.73.8.132_20171215_103327_1.mp4",
                "/usr/local/movies/movies7/42a4393beddb47099b7240318920c16a/20171215/205.73.8.132_20171215_103327_4.mp4",
                "/usr/local/movies/movies7/42a4393beddb47099b7240318920c16a/20171215/205.73.8.132_20171215_110329_3.mp4",
                "/usr/local/movies/movies7/42a4393beddb47099b7240318920c16a/20171215/205.73.8.132_20171215_120332_4.mp4",
                "/usr/local/movies/movies7/443bea1f2fad48ffb32f253cd8c02311/20180327/205.73.8.132_20180327_083657_1.mp4",
                "/usr/local/movies/movies7/443bea1f2fad48ffb32f253cd8c02311/20180327/205.73.8.132_20180327_083657_4.mp4",
                "/usr/local/movies/movies7/443bea1f2fad48ffb32f253cd8c02311/20180327/205.73.8.132_20180327_090701_3.mp4",
                "/usr/local/movies/movies7/443bea1f2fad48ffb32f253cd8c02311/20180327/205.73.8.132_20180327_094637_4.mp4",
                "/usr/local/movies/movies7/443bea1f2fad48ffb32f253cd8c02311/20180327/205.73.8.132_20180327_104646_3.mp4",
                "/usr/local/movies/movies7/443bea1f2fad48ffb32f253cd8c02311/20180327/205.73.8.132_20180327_111647_3.mp4",
                "/usr/local/movies/movies7/443bea1f2fad48ffb32f253cd8c02311/20180327/205.73.8.132_20180327_111647_4.mp4",
                "/usr/local/movies/movies7/443bea1f2fad48ffb32f253cd8c02311/20180327/205.73.8.132_20180327_114649_3.mp4",
                "/usr/local/movies/movies7/443bea1f2fad48ffb32f253cd8c02311/20180327/205.73.8.132_20180327_121650_1.mp4",
                "/usr/local/movies/movies7/443bea1f2fad48ffb32f253cd8c02311/20180327/205.73.8.132_20180327_124651_1.mp4",
                "/usr/local/movies/movies6/500de9ac03c748ba8cb566ee0d0ac9a3/20141210/205.73.8.133_20141210_155720_0r.mp4",
                "/usr/local/movies/movies6/500de9ac03c748ba8cb566ee0d0ac9a3/20141210/205.73.8.133_20141210_155720_1r.mp4",
                "/usr/local/movies/movies6/500de9ac03c748ba8cb566ee0d0ac9a3/20141210/205.73.8.133_20141210_165602_0r.mp4",
                "/usr/local/movies/movies6/2f91b605dfa746dc9bf7722351373889/20141224/205.73.8.133_20141224_102756_0r.mp4",
                "/usr/local/movies/movies6/2f91b605dfa746dc9bf7722351373889/20141224/205.73.8.133_20141224_102756_3r.mp4",
                "/usr/local/movies/movies6/2f91b605dfa746dc9bf7722351373889/20141224/205.73.8.133_20141224_112757_0r.mp4",
                "/usr/local/movies/movies6/2f91b605dfa746dc9bf7722351373889/20141224/205.73.8.133_20141224_112757_2r.mp4",
                "/usr/local/movies/movies6/45a91c4b5e82475595e6a8da869f38ff/20150428/205.73.8.133_20150428_155340_1r.mp4",
                "/usr/local/movies/movies6/3edf92adaa034a4ebe92569847176f14/20150526/205.73.8.133_20150526_090052_0r.mp4",
                "/usr/local/movies/movies6/3edf92adaa034a4ebe92569847176f14/20150526/205.73.8.133_20150526_090052_2r.mp4",
                "/usr/local/movies/movies6/3edf92adaa034a4ebe92569847176f14/20150526/205.73.8.133_20150526_093102_1r.mp4",
                "/usr/local/movies/movies6/3edf92adaa034a4ebe92569847176f14/20150526/205.73.8.133_20150526_093102_2r.mp4",
                "/usr/local/movies/movies6/3edf92adaa034a4ebe92569847176f14/20150526/205.73.8.133_20150526_093102_3r.mp4",
                "/usr/local/movies/movies6/33c75761a2ca4cee938de23709aacc53/20150528/205.73.8.133_20150528_154359_3r.mp4",
                "/usr/local/movies/movies6/33c75761a2ca4cee938de23709aacc53/20150528/205.73.8.133_20150528_154541_0r.mp4",
                "/usr/local/movies/movies6/33c75761a2ca4cee938de23709aacc53/20150528/205.73.8.133_20150528_154541_1r.mp4",
                "/usr/local/movies/movies6/33c75761a2ca4cee938de23709aacc53/20150528/205.73.8.133_20150528_154542_2r.mp4",
                "/usr/local/movies/movies6/33c75761a2ca4cee938de23709aacc53/20150528/205.73.8.133_20150528_154542_3r.mp4",
                "/usr/local/movies/movies6/3afe5f4086e44a40929e672b825319bb/20150616/205.73.8.133_20150616_160712_2r.mp4",
                "/usr/local/movies/movies6/3fdb69ad458e43b4b4425fbc40554a3e/20151030/205.73.8.133_20151030_094856_2r.mp4",
                "/usr/local/movies/movies6/3fdb69ad458e43b4b4425fbc40554a3e/20151030/205.73.8.133_20151030_094856_3r.mp4",
                "/usr/local/movies/movies6/3fdb69ad458e43b4b4425fbc40554a3e/20151030/205.73.8.133_20151030_104859_1r.mp4",
                "/usr/local/movies/movies6/3fdb69ad458e43b4b4425fbc40554a3e/20151030/205.73.8.133_20151030_111900_1r.mp4",
                "/usr/local/movies/movies6/3fdb69ad458e43b4b4425fbc40554a3e/20151030/205.73.8.133_20151030_111900_2r.mp4",
                "/usr/local/movies/movies6/3fdb69ad458e43b4b4425fbc40554a3e/20151030/205.73.8.133_20151030_111900_3r.mp4",
                "/usr/local/movies/movies6/3fdb69ad458e43b4b4425fbc40554a3e/20151030/205.73.8.133_20151030_114901_1r.mp4",
                "/usr/local/movies/movies6/31204189f38041428e6a4f15408bbff3/20151225/205.73.8.133_20151225_153246_2r.mp4",
                "/usr/local/movies/movies6/31204189f38041428e6a4f15408bbff3/20151225/205.73.8.133_20151225_160247_1r.mp4",
                "/usr/local/movies/movies6/31204189f38041428e6a4f15408bbff3/20151225/205.73.8.133_20151225_163248_2r.mp4",
                "/usr/local/movies/movies9/368c87b3058248018901ceafa58c00b9/20170309/205.73.8.136_20170309_091557_1.mp4",
                "/usr/local/movies/movies9/368c87b3058248018901ceafa58c00b9/20170309/205.73.8.136_20170309_091557_2.mp4",
                "/usr/local/movies/movies9/368c87b3058248018901ceafa58c00b9/20170309/205.73.8.136_20170309_094600_1.mp4",
                "/usr/local/movies/movies9/368c87b3058248018901ceafa58c00b9/20170309/205.73.8.136_20170309_094600_2.mp4",
                "/usr/local/movies/movies9/368c87b3058248018901ceafa58c00b9/20170309/205.73.8.136_20170309_101602_1.mp4",
                "/usr/local/movies/movies9/368c87b3058248018901ceafa58c00b9/20170309/205.73.8.136_20170309_101602_2.mp4",
                "/usr/local/movies/movies9/44bb0c50e38e4158a3258b17fa3b18dc/20170606/205.73.8.136_20170606_110211_2.mp4",
                "/usr/local/movies/movies9/44bb0c50e38e4158a3258b17fa3b18dc/20170606/205.73.8.136_20170606_110350_1.mp4",
                "/usr/local/movies/movies9/44bb0c50e38e4158a3258b17fa3b18dc/20170606/205.73.8.136_20170606_113220_2.mp4",
                "/usr/local/movies/movies9/44bb0c50e38e4158a3258b17fa3b18dc/20170606/205.73.8.136_20170606_113350_1.mp4",
                "/usr/local/movies/movies8/4f5f2bf14322432c82de232a5b3535c1/20170726/205.73.8.136_20170726_101202_2.mp4",
                "/usr/local/movies/movies8/4100502ff9274b229c202113c3348c79/20180529/205.73.8.136_20180529_102102_2.mp4",
                "/usr/local/movies/movies8/3d22afe21f514c90984808cacc9332a2/20180704/205.73.8.136_20180704_164934_1.mp4",
                "/usr/local/movies/movies8/3d22afe21f514c90984808cacc9332a2/20180704/205.73.8.136_20180704_164934_2.mp4",
                "/usr/local/movies/movies8/368ca52f31184ef3bb8a9884f4f35bb9/20180706/205.73.8.136_20180706_090734_1.mp4",
                "/usr/local/movies/movies8/368ca52f31184ef3bb8a9884f4f35bb9/20180706/205.73.8.136_20180706_090735_2.mp4",
                "/usr/local/movies/movies8/368ca52f31184ef3bb8a9884f4f35bb9/20180706/205.73.8.136_20180706_093741_1.mp4",
                "/usr/local/movies/movies8/368ca52f31184ef3bb8a9884f4f35bb9/20180706/205.73.8.136_20180706_093741_2.mp4",
                "/usr/local/movies/movies7/3edc12ccaaec4d82baf079578031329b/20180814/205.73.8.136_20180814_153851_1.mp4",
                "/usr/local/movies/movies7/3edc12ccaaec4d82baf079578031329b/20180814/205.73.8.136_20180814_153851_2.mp4",
                "/usr/local/movies/movies8/41010853707b4b988eccc18643257662/20180913/205.73.8.136_20180913_090204_1.mp4",
                "/usr/local/movies/movies8/41010853707b4b988eccc18643257662/20180913/205.73.8.136_20180913_090204_2.mp4",
                "/usr/local/movies/movies8/41010853707b4b988eccc18643257662/20180913/205.73.8.136_20180913_093205_1.mp4",
                "/usr/local/movies/movies8/41010853707b4b988eccc18643257662/20180913/205.73.8.136_20180913_093205_2.mp4",
                "/usr/local/movies/movies6/421564dd62514936929c6651675ae683/20160408/205.73.8.137_20160408_115946_3r.mp4",
                "/usr/local/movies/movies9/368c87b3058248018901ceafa58c00b9/20170309/205.73.8.137_20170309_091557_4.mp4",
                "/usr/local/movies/movies9/368c87b3058248018901ceafa58c00b9/20170309/205.73.8.137_20170309_094600_4.mp4",
                "/usr/local/movies/movies9/368c87b3058248018901ceafa58c00b9/20170309/205.73.8.137_20170309_101602_4.mp4",
                "/usr/local/movies/movies9/44bb0c50e38e4158a3258b17fa3b18dc/20170606/205.73.8.137_20170606_113220_4.mp4",
                "/usr/local/movies/movies8/4100502ff9274b229c202113c3348c79/20180529/205.73.8.137_20180529_102102_4.mp4",
                "/usr/local/movies/movies8/3d22afe21f514c90984808cacc9332a2/20180704/205.73.8.137_20180704_164934_4.mp4",
                "/usr/local/movies/movies8/368ca52f31184ef3bb8a9884f4f35bb9/20180706/205.73.8.137_20180706_090734_4.mp4",
                "/usr/local/movies/movies8/368ca52f31184ef3bb8a9884f4f35bb9/20180706/205.73.8.137_20180706_093741_4.mp4",
                "/usr/local/movies/movies7/3edc12ccaaec4d82baf079578031329b/20180814/205.73.8.137_20180814_153851_4.mp4",
                "/usr/local/movies/movies8/41010853707b4b988eccc18643257662/20180913/205.73.8.137_20180913_090204_4.mp4",
                "/usr/local/movies/movies8/41010853707b4b988eccc18643257662/20180913/205.73.8.137_20180913_093205_4.mp4",
                "/usr/local/movies/movies6/421564dd62514936929c6651675ae683/20160408/205.73.8.138_20160408_112941_2r.mp4",
                "/usr/local/movies/movies6/421564dd62514936929c6651675ae683/20160408/205.73.8.138_20160408_115946_3r.mp4",
                "/usr/local/movies/movies9/3c05a65c81dd4dc8b56921322506d5da/20170508/205.73.8.141_20170508_150030_1.mp4",
                "/usr/local/movies/movies9/3c05a65c81dd4dc8b56921322506d5da/20170508/205.73.8.141_20170508_150030_2.mp4",
                "/usr/local/movies/movies9/3c05a65c81dd4dc8b56921322506d5da/20170508/205.73.8.141_20170508_150031_3.mp4",
                "/usr/local/movies/movies9/3c05a65c81dd4dc8b56921322506d5da/20170508/205.73.8.141_20170508_150031_4.mp4",
                "/usr/local/movies/movies9/325ae452a7d44b8db2003505715ecda9/20170519/205.73.8.141_20170519_104108_1.mp4",
                "/usr/local/movies/movies9/325ae452a7d44b8db2003505715ecda9/20170519/205.73.8.141_20170519_104108_2.mp4",
                "/usr/local/movies/movies9/325ae452a7d44b8db2003505715ecda9/20170519/205.73.8.141_20170519_104108_3.mp4",
                "/usr/local/movies/movies9/325ae452a7d44b8db2003505715ecda9/20170519/205.73.8.141_20170519_104108_4.mp4",
                "/usr/local/movies/movies9/3d2e0d9bce564c7f981baf25071c4e2d/20170526/205.73.8.141_20170526_100436_1.mp4",
                "/usr/local/movies/movies9/3d2e0d9bce564c7f981baf25071c4e2d/20170526/205.73.8.141_20170526_100436_2.mp4",
                "/usr/local/movies/movies9/3d2e0d9bce564c7f981baf25071c4e2d/20170526/205.73.8.141_20170526_100437_4.mp4",
                "/usr/local/movies/movies9/3d2e0d9bce564c7f981baf25071c4e2d/20170526/205.73.8.141_20170526_113551_2.mp4",
                "/usr/local/movies/movies9/3d2e0d9bce564c7f981baf25071c4e2d/20170526/205.73.8.141_20170526_113551_3.mp4",
                "/usr/local/movies/movies9/3d2e0d9bce564c7f981baf25071c4e2d/20170526/205.73.8.141_20170526_113551_4.mp4",
                "/usr/local/movies/movies7/3583f7948af848249f43928b4bf853ab/20171212/205.73.8.141_20171212_160009_2.mp4",
                "/usr/local/movies/movies7/3583f7948af848249f43928b4bf853ab/20171212/205.73.8.141_20171212_160009_3.mp4",
                "/usr/local/movies/movies7/452f28ce0a184c81959aa122625aa797/20180227/205.73.8.141_20180227_093215_2.mp4",
                "/usr/local/movies/movies9/3c05a65c81dd4dc8b56921322506d5da/20170508/205.73.8.142_20170508_150031_1.mp4",
                "/usr/local/movies/movies9/3c05a65c81dd4dc8b56921322506d5da/20170508/205.73.8.142_20170508_150031_2.mp4",
                "/usr/local/movies/movies9/3c05a65c81dd4dc8b56921322506d5da/20170508/205.73.8.142_20170508_150032_3.mp4",
                "/usr/local/movies/movies9/3c05a65c81dd4dc8b56921322506d5da/20170508/205.73.8.142_20170508_150032_4.mp4",
                "/usr/local/movies/movies9/325ae452a7d44b8db2003505715ecda9/20170519/205.73.8.142_20170519_104108_1.mp4",
                "/usr/local/movies/movies9/325ae452a7d44b8db2003505715ecda9/20170519/205.73.8.142_20170519_104108_2.mp4",
                "/usr/local/movies/movies9/325ae452a7d44b8db2003505715ecda9/20170519/205.73.8.142_20170519_104108_3.mp4",
                "/usr/local/movies/movies9/325ae452a7d44b8db2003505715ecda9/20170519/205.73.8.142_20170519_104108_4.mp4",
                "/usr/local/movies/movies9/3d2e0d9bce564c7f981baf25071c4e2d/20170526/205.73.8.142_20170526_100438_1.mp4",
                "/usr/local/movies/movies9/3d2e0d9bce564c7f981baf25071c4e2d/20170526/205.73.8.142_20170526_100438_2.mp4",
                "/usr/local/movies/movies9/3d2e0d9bce564c7f981baf25071c4e2d/20170526/205.73.8.142_20170526_100438_3.mp4",
                "/usr/local/movies/movies9/3d2e0d9bce564c7f981baf25071c4e2d/20170526/205.73.8.142_20170526_100438_4.mp4",
                "/usr/local/movies/movies9/3d2e0d9bce564c7f981baf25071c4e2d/20170526/205.73.8.142_20170526_113551_1.mp4",
                "/usr/local/movies/movies9/3d2e0d9bce564c7f981baf25071c4e2d/20170526/205.73.8.142_20170526_113551_2.mp4",
                "/usr/local/movies/movies9/3d2e0d9bce564c7f981baf25071c4e2d/20170526/205.73.8.142_20170526_113551_4.mp4",
                "/usr/local/movies/movies7/3583f7948af848249f43928b4bf853ab/20171212/205.73.8.142_20171212_160009_2.mp4",
                "/usr/local/movies/movies7/3583f7948af848249f43928b4bf853ab/20171212/205.73.8.142_20171212_160009_3.mp4",
                "/usr/local/movies/movies7/3583f7948af848249f43928b4bf853ab/20171212/205.73.8.142_20171212_160009_4.mp4",
                "/usr/local/movies/movies7/452f28ce0a184c81959aa122625aa797/20180227/205.73.8.142_20180227_100223_3.mp4",
                "/usr/local/movies/movies8/503870f23a3048daa5c692b8c6d00af7/20180821/205.73.8.142_20180821_153028_4.mp4",
                "/usr/local/movies/movies7/452d494a50fd4bb8a78629b5932676a7/20170627/205.73.8.147_20170627_091653_1.mp4",
                "/usr/local/movies/movies8/3bf394409cc941498c384906a2dde49a/20180314/205.73.8.147_20180314_094827_1.mp4",
                "/usr/local/movies/movies7/41a819cd92db45888b4cf1042c3f2762/20180523/205.73.8.147_20180523_150313_1.mp4",
                "/usr/local/movies/movies7/41a819cd92db45888b4cf1042c3f2762/20180523/205.73.8.147_20180523_153321_1.mp4",
                "/usr/local/movies/movies7/41a819cd92db45888b4cf1042c3f2762/20180523/205.73.8.147_20180523_160321_1.mp4",
                "/usr/local/movies/movies7/3ddce998c5c9461eb1daecf0fd27cc4c/20180719/205.73.8.147_20180719_090037_1.mp4",
                "/usr/local/movies/movies9/502089ce18b544faae7172122c943e4d/20161223/205.73.8.151_20161223_150058_1.mp4",
                "/usr/local/movies/movies9/33bd25d7f41a45878c82b28abcb89d92/20170220/205.73.8.151_20170220_094223_1.mp4",
                "/usr/local/movies/movies8/3d22d781807e4afb9f4936acf0837b0b/20171019/205.73.8.151_20171019_091652_1.mp4",
                "/usr/local/movies/movies7/30f33c2e521e4ff0aa92b5ba86e834ad/20180518/205.73.8.151_20180518_091256_1.mp4",
                "/usr/local/movies/movies7/30f33c2e521e4ff0aa92b5ba86e834ad/20180518/205.73.8.151_20180518_094305_1.mp4",
                "/usr/local/movies/movies8/41128dc101d44a40b8c7a17fc435ac00/20180626/205.73.8.151_20180626_155455_1.mp4",
                "/usr/local/movies/movies8/41128dc101d44a40b8c7a17fc435ac00/20180626/205.73.8.151_20180626_162501_1.mp4",
                "/usr/local/movies/movies9/3f3a3a04fc0b413aaf0b7dfcffa63c8b/20170224/205.73.8.156_20170224_090305_1.mp4",
                "/usr/local/movies/movies9/3f3a3a04fc0b413aaf0b7dfcffa63c8b/20170224/205.73.8.156_20170224_090308_1.mp4",
                "/usr/local/movies/movies7/507a2ddd81fe42519f449e97621eee56/20180417/205.73.8.156_20180417_090935_1.mp4",
                "/usr/local/movies/movies8/3e661d2fbb854f2d82b5e88e943b004a/20181023/205.73.8.156_20181023_145814_1.mp4",
                "/usr/local/movies/movies8/3e661d2fbb854f2d82b5e88e943b004a/20181023/205.73.8.156_20181023_152818_1.mp4",
                "/usr/local/movies/movies7/50b392c87575413aa6527209102c1665/20181030/205.73.8.156_20181030_090251_1.mp4",
                "/usr/local/movies/movies6/30f013a799944e7b8f5cb6dc54fecfb0/20160415/205.73.8.157_20160415_092038_0r.mp4",
                "/usr/local/movies/movies6/30f013a799944e7b8f5cb6dc54fecfb0/20160415/205.73.8.157_20160415_095045_0r.mp4",
                "/usr/local/movies/movies6/30f013a799944e7b8f5cb6dc54fecfb0/20160415/205.73.8.157_20160415_160912_0r.mp4",
                "/usr/local/movies/movies9/5032826855c54a1a951c9bea0aac39c4/20161229/205.73.8.157_20161229_103033_0r.mp4",
                "/usr/local/movies/movies9/41a34b9755474a728e44fab987d54502/20161227/205.73.8.161_20161227_100354_1.mp4",
                "/usr/local/movies/movies9/3dcc4cbbd4b842a2b4376710175487f5/20161228/205.73.8.161_20161228_151057_1.mp4",
                "/usr/local/movies/movies9/3d798ab75cd64c82b410efee65863e4e/20170302/205.73.8.161_20170302_101340_1.mp4",
                "/usr/local/movies/movies9/3d798ab75cd64c82b410efee65863e4e/20170302/205.73.8.161_20170302_104344_1.mp4",
                "/usr/local/movies/movies7/50bde40b802f4cc4bb04493bdea36e28/20170808/205.73.8.161_20170808_090800_1.mp4",
                "/usr/local/movies/movies7/50bde40b802f4cc4bb04493bdea36e28/20170808/205.73.8.161_20170808_093804_1.mp4",
                "/usr/local/movies/movies7/4f79f858ccaf45bca07713560f24cdad/20180205/205.73.8.161_20180205_091342_1.mp4",
                "/usr/local/movies/movies7/423888bd2ec64457a5d9c1fd2bf9a02d/20180712/205.73.8.161_20180712_093929_1.mp4",
                "/usr/local/movies/movies8/325202e4e37549af8dd4d53e109b71f3/20180911/205.73.8.161_20180911_090930_1.mp4",
                "/usr/local/movies/movies6/3b226026584843b59e821c6982f5017b/20160317/205.73.8.162_20160317_090333_0r.mp4",
                "/usr/local/movies/movies6/3b226026584843b59e821c6982f5017b/20160317/205.73.8.162_20160317_093342_0r.mp4",
                "/usr/local/movies/movies6/3b226026584843b59e821c6982f5017b/20160317/205.73.8.162_20160317_100342_0r.mp4",
                "/usr/local/movies/movies6/3b226026584843b59e821c6982f5017b/20160317/205.73.8.162_20160317_103343_0r.mp4",
                "/usr/local/movies/movies6/3b226026584843b59e821c6982f5017b/20160317/205.73.8.162_20160317_110345_0r.mp4",
                "/usr/local/movies/movies6/3b226026584843b59e821c6982f5017b/20160317/205.73.8.162_20160317_145919_0r.mp4",
                "/usr/local/movies/movies6/3b226026584843b59e821c6982f5017b/20160317/205.73.8.162_20160317_152924_0r.mp4",
                "/usr/local/movies/movies6/3b226026584843b59e821c6982f5017b/20160317/205.73.8.162_20160317_155924_0r.mp4",
                "/usr/local/movies/movies7/393380a8cebd4f02afd50be116cbb520/20170911/205.73.8.166_20170911_153738_1.mp4",
                "/usr/local/movies/movies7/393380a8cebd4f02afd50be116cbb520/20170911/205.73.8.166_20170911_153738_2.mp4",
                "/usr/local/movies/movies7/30e23ec0e520495881e3339949e08bbe/20170921/205.73.8.166_20170921_100813_2.mp4",
                "/usr/local/movies/movies7/43dc2cd162504081b041919504829d70/20171106/205.73.8.166_20171106_150951_1.mp4",
                "/usr/local/movies/movies7/43dc2cd162504081b041919504829d70/20171106/205.73.8.166_20171106_150951_2.mp4",
                "/usr/local/movies/movies7/43dc2cd162504081b041919504829d70/20171106/205.73.8.166_20171106_153952_1.mp4",
                "/usr/local/movies/movies7/43dc2cd162504081b041919504829d70/20171106/205.73.8.166_20171106_153952_2.mp4",
                "/usr/local/movies/movies7/43dc2cd162504081b041919504829d70/20171106/205.73.8.166_20171106_160953_2.mp4",
                "/usr/local/movies/movies7/43dc2cd162504081b041919504829d70/20171106/205.73.8.166_20171106_160954_1.mp4",
                "/usr/local/movies/movies8/34885caf4d834bedae32bbce67c9f6a1/20180123/205.73.8.166_20180123_094024_1.mp4",
                "/usr/local/movies/movies8/34885caf4d834bedae32bbce67c9f6a1/20180123/205.73.8.166_20180123_094024_2.mp4",
                "/usr/local/movies/movies8/37b9bf6f13a341888296f44b81e9c9dd/20180925/205.73.8.166_20180925_153735_1.mp4",
                "/usr/local/movies/movies7/393380a8cebd4f02afd50be116cbb520/20170911/205.73.8.167_20170911_153738_4.mp4",
                "/usr/local/movies/movies7/30e23ec0e520495881e3339949e08bbe/20170921/205.73.8.167_20170921_100813_4.mp4",
                "/usr/local/movies/movies7/43dc2cd162504081b041919504829d70/20171106/205.73.8.167_20171106_150951_4.mp4",
                "/usr/local/movies/movies7/43dc2cd162504081b041919504829d70/20171106/205.73.8.167_20171106_153952_4.mp4",
                "/usr/local/movies/movies7/43dc2cd162504081b041919504829d70/20171106/205.73.8.167_20171106_160953_4.mp4",
                "/usr/local/movies/movies8/34885caf4d834bedae32bbce67c9f6a1/20180123/205.73.8.167_20180123_094024_4.mp4",
                "/usr/local/movies/movies8/37b9bf6f13a341888296f44b81e9c9dd/20180925/205.73.8.167_20180925_153735_4.mp4",
                "/usr/local/movies/movies9/44a12a62e97d4ad7a143f51c4f6b7af5/20161229/205.73.8.171_20161229_154022_2.mp4",
                "/usr/local/movies/movies9/44a12a62e97d4ad7a143f51c4f6b7af5/20161229/205.73.8.171_20161229_161030_2.mp4",
                "/usr/local/movies/movies9/44a12a62e97d4ad7a143f51c4f6b7af5/20161229/205.73.8.171_20161229_164030_2.mp4",
                "/usr/local/movies/movies6/348e94a39053409db35bcff41b237539/20160128/205.73.8.172_20160128_102508_0r.mp4",
                "/usr/local/movies/movies6/348e94a39053409db35bcff41b237539/20160128/205.73.8.172_20160128_105508_0r.mp4",
                "/usr/local/movies/movies6/349fef1cdd4e450d9d09b5b1cd3d39b4/20160303/205.73.8.172_20160303_150907_0r.mp4",
                "/usr/local/movies/movies6/349fef1cdd4e450d9d09b5b1cd3d39b4/20160303/205.73.8.172_20160303_153909_0r.mp4",
                "/usr/local/movies/movies9/44a12a62e97d4ad7a143f51c4f6b7af5/20161229/205.73.8.172_20161229_154022_4.mp4",
                "/usr/local/movies/movies9/44a12a62e97d4ad7a143f51c4f6b7af5/20161229/205.73.8.172_20161229_161030_4.mp4",
                "/usr/local/movies/movies9/44a12a62e97d4ad7a143f51c4f6b7af5/20161229/205.73.8.172_20161229_164030_4.mp4",
                "/usr/local/movies/movies5/movies1_bak/0bfde9b8feba448aaf5d7e54d898465c/20200616/_140.80.96.121_20200616_130023_1.mp4",
                "/usr/local/movies/movies8/8ee74ce7406644e6b18ec01d14adcdbd/20171017/_205.73.8.131_20171017_160339_3.mp4",
                "/usr/local/movies/movies7/3edc12ccaaec4d82baf079578031329b/20180814/_205.73.8.136_20180814_153510_1.mp4",
                "/usr/local/movies/movies7/3edc12ccaaec4d82baf079578031329b/20180814/_205.73.8.136_20180814_153515_2.mp4",
                "/usr/local/movies/movies7/3edc12ccaaec4d82baf079578031329b/20180814/_205.73.8.137_20180814_153515_4.mp4",
                "/usr/local/movies/movies9/1594ee5ea6ed4105a52837128542ddfc/20161216/_205.73.8.157_20161216_155017_0r.mp4"
        ]
        def cunchuIp = ["140.80.96.161","140.80.96.162","140.80.96.163","140.80.96.164","140.80.96.165","140.80.96.166","140.80.96.167","140.80.96.168","140.80.96.169","140.80.96.170"]
        FTPClient ftpClient

        for (def video: videos){
            try {
                def videoPath = video.substring(video.indexOf("movies/")+15)
                def planId = videoPath.substring(0,videoPath.indexOf("/"))
                def ipAddress = VideoInfo.findByFileName(video.substring(18))?.trialInfo.planInfo?.courtroom?.deviceIp
                if (!ipAddress){
                    log.info(video+"-----未从数据中找到对应的庭审主机ip")
                    convertAndSend "/topic/indexVideo", "${video}-----未从数据中找到对应的庭审主机ip"
                    continue
                }
                def videoName = videoPath.substring(videoPath.lastIndexOf("/")+1)
                def lujing = video.substring(video.indexOf("movies/")+7)
                def lujingDir = lujing.substring(0,lujing.lastIndexOf("/"))
                ftpClient = this.login(ipAddress,21,"ftp","ftp")
                def planIdStr = planId + "/"
                log.info("ip 地址：${ipAddress} ftp文件夹路径:${planIdStr}")
                convertAndSend "/topic/indexVideo", "ftp文件夹路径:${planIdStr}"
                def sss = 0
                if (!ftpClient.changeWorkingDirectory(new String(planIdStr.getBytes("UTF-8"),"ISO-8859-1"))) {
                    log.error("${planIdStr+ "/"}---文件夹不存在")
                    log.info("开始遍历庭审存储！")
                    closeConnect(ftpClient)
                    ftpClient = null
                    for (def ip:cunchuIp){
                        ftpClient = login(ip,21,"ftp","ftp")
                        if (!ftpClient.changeWorkingDirectory(new String(planIdStr.getBytes("UTF-8"),"ISO-8859-1"))) {
                            log.info("存储主机${ip}未找到视频路径！")
                            closeConnect(ftpClient)
                            ftpClient = null
                            continue
                        }else{
                            log.info("存储主机ip${ip} 找到视频并进行下载！")
                            convertAndSend "/topic/indexVideo", "存储主机ip${ip} 找到视频并进行下载！"
                            sss = 1
                            break
                        }
                    }
                    if (sss == 0){
                        convertAndSend "/topic/indexVideo", "文件在庭审主机和存储主机中都未找到 ${videoPath}"
                        continue
                    }
                }
                //遍历文件名称把文件下载下来
                ftpClient.enterLocalPassiveMode()  // 设置被动模式，开通一个端口来传输数据
                String[] fs = ftpClient.listNames()
                for (String ff : fs) {
                    log.info(ff)
                    def channel = ff.substring(0,ff.indexOf("_"))
                    log.info(channel)
                    def fileChn = videoName.substring(videoName.lastIndexOf("_") + 1,videoName.lastIndexOf("."))
                    log.info(fileChn)
                    if (channel == fileChn){
                        String ftpName = new String(ff.getBytes("UTF-8"),"ISO-8859-1")
                        log.info("---->文件名称${ftpName}")
                        //排期目录
                        File planDirect = new File("/usr/local/movies/${lujingDir}")
                        if (!planDirect.exists()){
                            log.info("---->排期：${planId}文件夹不存在,准备创建文件夹")
                            planDirect.mkdirs()
                            log.info("---->排期：${planId}文件夹不存在,创建文件夹成功")
                            planDirect.setReadable(true, false)
                            planDirect.setWritable(true,false)
                            planDirect.setExecutable(true,false)
                        }else{
                            log.info("---->排期：${planId}文件夹存在,无需创建")
                        }
                        try {
                            File file = new File(video)
//                            if (!file.exists()){
                                this.downLoadFTP(ftpClient,planIdStr,videoName,video)
                                log.info("---->ftp文件：${ftpName}文件下载完成")

                                //视频信息分析
                                file.setReadable(true, false)
                                file.setWritable(true,false)
                                file.setExecutable(true,false)
                                /*def videoInfo = VideoInfo.findByFileName(video.substring(video.indexOf("movies/")+7))
                                if (videoInfo){
                                    videoInfo.fileName = videoPath
                                    videoInfo.save(flush:true)
                                    convertAndSend "/topic/indexVideo", "执行下载成功 并成功修改数据库filename${videoInfo.fileName}"
                                }else{
                                    log.error("数据库中未找到视频记录${video.substring(video.indexOf("movies/")+7)}")
                                    convertAndSend "/topic/indexVideo", "数据库中未找到视频记录${video.substring(video.indexOf("movies/")+7)}"
                                }*/
//                            }else{
//                                log.info("---->${file.getPath()}文件已存在")
//
//
//                                convertAndSend "/topic/indexVideo", "${file.getPath()}文件已存在"
//                            }
                            break
                        } catch (Exception e) {
                            log.error(e.getMessage())
                        }
                    }
                }
            }catch(Exception error){
                log.error("---->错误信息：\n${error.getMessage()}")
            }finally {
                closeConnect(ftpClient)
                ftpClient = null
            }
        }
    }
    /**
     * 实现文件的移动，这里做的是一个文件夹下的所有内容移动到新的文件，
     * 如果要做指定文件移动，加个判断判断文件名
     * 如果不需要移动，只是需要文件重命名，可以使用ftp.rename(oleName,newName)
     * @param ftp
     * @param oldPath
     * @param newPath
     * @return
     */
    boolean moveFile(FTPClient ftp,String oldPath,String newPath,fileName){
        boolean flag = false

        try {
            ftp.changeWorkingDirectory(oldPath)
            ftp.enterLocalPassiveMode()
            //获取文件数组
            FTPFile[] files = ftp.listFiles()
            //新文件夹不存在则创建
            if(!ftp.changeWorkingDirectory(newPath)){
                ftp.makeDirectory(newPath)
            }
            //回到原有工作目录
            ftp.changeWorkingDirectory(oldPath)
            for (FTPFile file : files) {

                //转存目录
                flag = ftp.rename(new String(file.getName().getBytes("UTF-8"),"ISO-8859-1"), newPath+File.separator+new String(file.getName().getBytes("UTF-8"),"ISO-8859-1"))
                if(flag){
                    log.info(file.getName()+"移动成功")
                }else{
                    log.error(file.getName()+"移动失败")
                }
            }
        } catch (Exception e) {
            e.printStackTrace()
            log.error("移动文件失败")
        }
        return flag
    }
}
