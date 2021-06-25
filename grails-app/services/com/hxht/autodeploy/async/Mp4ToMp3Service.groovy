package com.hxht.autodeploy.async

import cn.hutool.core.date.DateUtil
import com.hxht.techcrt.Dict
import com.hxht.techcrt.court.PlanInfo
import com.hxht.techcrt.court.TrialInfo
import com.hxht.techcrt.court.VideoInfo
import com.hxht.techcrt.mem.VideoToMp3Service
import grails.converters.JSON
import grails.core.GrailsApplication
import grails.gorm.transactions.Transactional
import org.apache.commons.io.FileUtils
import ws.schild.jave.DefaultFFMPEGLocator

@Transactional
class Mp4ToMp3Service {
    GrailsApplication grailsApplication
    VideoToMp3Service videoToMp3Service

    private static String courtCode

    /**
     * 异步执行视频mp4转成mp3
     * @return
     */
    def audioToVideo(Long videoId) {
        courtCode = Dict.findByCode("CURRENT_COURT").ext3
        //深圳中院代号 目前只有深圳中院在用
        if (courtCode == "J30") {
            log.info("开始执行视频转mp3异步接口")
            def video = VideoInfo.get(videoId)
            def trialInfo = video.trialInfo
            //是否是公开庭，是的话进行转换并合成
            if ((!trialInfo.courtroom.open) || trialInfo.courtroom.open == 1) {
                def courtRoom = trialInfo.courtroom
                def planInfo = trialInfo.planInfo
                def archives = planInfo.caseInfo.archives
                def numZhengju = 0
                def numHecheng = 0
                //num判断法庭是否包含证据画面，如果不包含则用合成图像
                try {
                    def cfg = JSON.parse(courtRoom.cfg)//获取通道信息配置
                    for (def encode : cfg.encode) {
                        if (encode.name && encode.name.contains("证据")) {
                            numZhengju = 1
                            break
                        }
                    }
                    if (numZhengju == 0) {
                        for (def encode : cfg.encode) {
                            if (encode.name && encode.name.contains("合成")) {
                                numHecheng = 1
                                break
                            }
                        }
                    }
                    log.info("证据：" + numZhengju + "合成：" + numHecheng )
                    def status = trialInfo.status
                    def trialInfoId = trialInfo.id
                    def videoToMp3 = videoToMp3Service.findByTrialInfoId(trialInfoId)
                    log.info(video.channelName)
                    if ((numZhengju == 1 && numHecheng == 0 && video.channelName?.contains("证据")) ||
                            (numZhengju == 0 && numHecheng == 1 && video.channelName?.contains("合成"))) {//证明法庭有证据画面或者合成图像
                        log.info("开始mp4转mp3，证据：" + numZhengju + "合成：" + numHecheng)
                        this.toMp3(video) //异步对接视频转mp3格式
                        log.info("异步执行音频格式转换完毕成功")
                        if (status == 3) {//闭庭的状态
                            if (videoToMp3) {//如果不为空则证明此trial存在报错的视频转码mp3，则不进行视频拼接
                                log.info("音频转mp3中出现报错则不进行视频拼接和放置光栅，trialId为：${trialInfoId}")
                            } else {
                                log.info("onComplete闭庭操作后进行视频拼接后放置光栅")
                                //异步对接视频拼接成一个mp3文件，闭庭情况下进行视频拼接并将拼接的视频放置光栅
                                this.mp3Combine(trialInfo, planInfo, archives)
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("存储服务接口 toMp3方法出错！" + e.getMessage())
                }
            }
        }
    }
    def toMp3(VideoInfo video){
        log.info("开始执行视频格式转换")
        //视频的原始路径
        def filePrefix = grailsApplication.config.getProperty('tc.trial.note.path')
        def inputSource = "${filePrefix}/${video.fileName}"
        def inputSourceFile = new File(inputSource)
        //文件不存在则说明不在此服务器
        if (!inputSourceFile.exists()){
            log.info("${inputSource}--视频路径此服务器不存在")
            return
        }
        //生成的音频文件路径
        def outputPath = inputSource.substring(0,inputSource.lastIndexOf(".")) + ".mp3"
        //-------开始调用ffmpeg程序-------/
        //获取ffmpeg执行文件路径
        def defaultFFm = new DefaultFFMPEGLocator()
        // 调用转换命令
        def strCmd = defaultFFm.getFFMPEGExecutablePath() + " -i " + inputSource + " -vn " + outputPath + " -y"
        Process p = null
        try {
            // p=Runtime.getRuntime().exec("cmd /c " + strCmd)
            p = Runtime.getRuntime().exec(strCmd)
            // 获取进程的错误流
            final InputStream is2 = p.getErrorStream()

            def br2 = new BufferedReader(new InputStreamReader(is2))
            try {
                def line2 = null
                while ((line2 = br2.readLine()) != null) {
                    if (line2 != null) {
                    }
                }
            } catch (IOException e) {
                e.printStackTrace()
            } finally {
                try {
                    is2.close()
                } catch (IOException e) {
                    e.printStackTrace()
                }
            }
            log.info("开始阻塞视频转mp3")
            // 进行阻塞
            p.waitFor()
            log.info("阻塞是否视频mp3")

        } catch (Exception e) {
            log.error("视频转mp3文件出错！"+e.getMessage())
            //将出错的视频文件写入到数据库定时器再次进行视频音频的转换
            videoToMp3Service.saveVideoToMp3(false,video.id,0,video.trialInfo.id)
        } finally {
            if (null != p) {
                // 释放资源
                p.destroy()
            }
        }
    }

    /**
     * 异步执行视频mp3拼接成一个mp3文件并放置光栅
     * @return
     */
    def mp3Combine(TrialInfo trialInfo,PlanInfo planInfo,String archives) {
        log.info("开始打印Sql日志")
        //没有证据图像时取用合成图像
        def videoInfoList
        VideoInfo.withNewSession {
            videoInfoList = VideoInfo.findAllByTrialInfoAndChannelNameLike(trialInfo, "%证据%", [sort: "startRecTime", order: "asc"])
            if (!videoInfoList){
                videoInfoList = VideoInfo.findAllByTrialInfoAndChannelNameLike(trialInfo, "%合成%", [sort: "startRecTime", order: "asc"])
            }
            if (!videoInfoList){
                log.info("未找到对应的画面返回")
                return
            }
        }
        def dateString = DateUtil.format(videoInfoList.get(0).startRecTime as Date, "yyyy-MM-dd").replaceAll("-","")
        //视频的原始路径
        def filePrefix = grailsApplication.config.getProperty('tc.trial.note.path')
        //音频文件放置的光栅路径
        def fileRaster = grailsApplication.config.getProperty('tc.trial.fileRaster.path')
        //需要放到光栅的路径
        def rasterFilename = (planInfo.id as String) + "_" + trialInfo.id + ".mp3"
        def rasterFile = "${fileRaster}/${dateString}/${rasterFilename}"
//            def rasterFile = "/user/local/movies/luyin/${rasterFilename}"
        log.info("放置的光栅路径为：${rasterFile}")

        def rasterTxt = "${fileRaster}/${dateString}/${dateString}.txt"
        log.info("放置的txt光栅路径为：${rasterTxt}")

        //生成的音频文件路径
        def inputSource1 = "${filePrefix}/${videoInfoList.get(0).fileName}"
        def outputPath = inputSource1.substring(0,inputSource1.lastIndexOf("/")+1) + (planInfo.id as String) + "_" + trialInfo.id + ".mp3"
        //光栅目录不存在则创建目录
        def file = new File(rasterFile)
        def outputDir = file.getAbsoluteFile()
        outputDir.getParentFile().mkdirs()
        outputDir.getParentFile().setWritable(true)

        //写入txt文件路径
//            def inputtxt = "/usr/local/movies/${videoInfoList.get(0).fileName}"
        def outputTxt = inputSource1.substring(0, inputSource1.lastIndexOf("/")+1) +
                (planInfo.id as String) + "_" + trialInfo.id+ ".txt"
        def outputTxtFile = new File(outputTxt)
        //如果目录不存在则返回，说明不在此服务器存储视频
        if (!outputTxtFile.getParentFile().exists()){
            log.info("排期id为[${planInfo.id}] 存储视频不在此服务器并返回！")
            return
        }
        if (!outputTxtFile.exists()){
            outputTxtFile.createNewFile()
        }

        //写入排期id庭审id等信息写入txt文件，再将txt文件放置光栅。用做外网信息同步扫描，下载mp3
        //usr/local/movies/20220202/20220202.txt
        def outputTxtMp3 = filePrefix + dateString + "/"+ dateString + ".txt"
        log.info("外网传输的txt路径："+outputTxtMp3)
        def outputTxtFileMp3 = new File(outputTxtMp3)
        //目录不存在则创建目录
        def txtFileMp3Parent = outputTxtFileMp3.getAbsoluteFile().getParentFile()
        if (!txtFileMp3Parent.exists()){
            txtFileMp3Parent.mkdirs()
            txtFileMp3Parent.setWritable(true)
        }
        if (!outputTxtFileMp3.exists()){
            outputTxtFileMp3.createNewFile()
        }
        //写入生成的txt文件
        BufferedWriter outMp3 = new BufferedWriter(new FileWriter(outputTxtFileMp3,true))
        def data = [:]
        data.put("caseno", archives.trim())
        data.put("planid", planInfo.id)
        data.put("trialid", trialInfo.id)
        data.put("endtime",DateUtil.format(trialInfo.endDate as Date, "yyyy-MM-dd HH:mm:ss"))
        data.put("status",trialInfo.status)
        def dataJson = data as JSON
        outMp3.write(dataJson.toString() + "\r\n")
        log.info(dataJson.toString())
        outMp3.flush() // 把缓存区内容压入文件
        outMp3.close() // 最后记得关闭文件

        for (int i=0; i<videoInfoList.size(); i++) {
            def video = videoInfoList.get(i)
            //原始视频的路径
            def inputSource = "${filePrefix}${video.fileName}"
            //生成的音频文件路径
            def outputPathMp3 = inputSource.substring(0, inputSource.lastIndexOf(".")) + ".mp3"
            //写入生成的txt文件
            BufferedWriter out = new BufferedWriter(new FileWriter(outputTxtFile,true))
            out.write("file " + outputPathMp3 + "\r\n") // \r\n即为换行
            out.flush() // 把缓存区内容压入文件
            out.close() // 最后记得关闭文件
           /*//旧命令为concat连接mp3 linux执行不生效 后改为写入txt文件执行生成的txt文件
           if (!outputPathMp3Total){
                outputPathMp3Total = outputPathMp3
            }else{
                outputPathMp3Total = outputPathMp3Total + "|" + outputPathMp3
            }*/
        }

        //-------开始调用ffmpeg程序-------/
        //获取ffmpeg执行文件路径
        def defaultFFm = new DefaultFFMPEGLocator()
        // 调用转换命令
        def strCmd = defaultFFm.getFFMPEGExecutablePath() + "  -f concat -safe 0 -i " + outputTxt +" -c copy " + outputPath + " -y"
        log.info(strCmd)
        Process p = null
        try {
            // p=Runtime.getRuntime().exec("cmd /c " + strCmd)
            log.info("开始执行")
            p = Runtime.getRuntime().exec(strCmd)
            // 获取进程的错误流
            final InputStream is2 = p.getErrorStream()

            def br2 = new BufferedReader(new InputStreamReader(is2))
            try {
                def line2 = null
                while ((line2 = br2.readLine()) != null) {
                    if (line2 != null) {
                    }
                }
            } catch (IOException e) {
                e.printStackTrace()
            } finally {
                try {
                    is2.close()
                } catch (IOException e) {
                    e.printStackTrace()
                }
            }
            log.info("开始阻塞视频拼接")
            // 进行阻塞
            p.waitFor()
            log.info("阻塞是否视频拼接")
            try {
                File file1 = new File(outputPath)
                file1.renameTo(new File(rasterFile))
                File file2 = new File(outputTxtMp3)
                FileUtils.copyFile(file2, new File(rasterTxt))
            }catch (Exception e) {
                e.printStackTrace()
            }

        } catch (Exception e) {
            log.error("视频转mp3文件出错！"+e.getMessage())
        } finally {
            if (null != p) {
                // 释放资源
                p.destroy()
                log.info('释放资源完毕，异步执行音频mp3拼接完毕')
            }
        }
        return '异步执行音频mp3拼接完毕'
    }
}
