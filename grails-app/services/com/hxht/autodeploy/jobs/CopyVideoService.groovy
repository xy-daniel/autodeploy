package com.hxht.autodeploy.jobs

import cn.hutool.core.date.DateUtil
import com.hxht.techcrt.CopyVideoLog
import com.hxht.techcrt.DataStatus
import com.hxht.techcrt.MountDisk
import com.hxht.techcrt.court.PlanInfo
import com.hxht.techcrt.court.TrialInfo
import com.hxht.techcrt.court.VideoInfo
import com.hxht.techcrt.utils.MD5Utils
import com.hxht.techcrt.utils.UUIDGenerator
import com.hxht.techcrt.utils.VideoReadUtil
import grails.core.GrailsApplication
import grails.events.EventPublisher
import grails.gorm.transactions.Transactional
import org.apache.commons.io.FileUtils
import ws.schild.jave.MultimediaObject
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * 2021.04.06 >>> 视频转移到挂载磁盘定时器修改 daniel
 */
@Transactional
class CopyVideoService implements EventPublisher{
    GrailsApplication grailsApplication
    def linkNum = 1 //尝试连接转移次数
    def numMount = 1 //挂载磁盘个数

    /**
     * 视频转移
     * @param video 需要转移的视频记录
     * @param num 开始几点
     * @param mountDisks 磁盘
     */
    void videoInfoTransfer(VideoInfo video, Integer num, List<MountDisk> mountDisks) {
        try {
            if (!video.fileName || video.fileName?.contains("movies")) {
                log.info("[CopyVideoService.videoInfoTransfer] 视频记录file_name不存在或视频记录file_name包含movies，说明已经转移成功,跳过此条记录.")
                return
            }
            def path = grailsApplication.config.getProperty('tc.trial.note.path')
            log.info("[CopyVideoService.videoInfoTransfer] 需要拷贝的文件根目录:${path}")
            def file = new File("${path}/${video.fileName}")
            if (!file.exists()) {
                log.info("[CopyVideoService.videoInfoTransfer] 需要拷贝的视频文件不存在跳过此记录,路径:${file.getPath()}")
                return
            }
            log.info("[CopyVideoService.videoInfoTransfer] 视频文件存在开始进行视频转移.")
            for (def mount : mountDisks) {
                if (!video.size) {
                    log.info("[CopyVideoService.videoInfoTransfer] 视频记录文件大小不存在，开始计算视频文件大小并保存.")
                    //视频大小不存在则进行计算得出大小
                    def videoSize = VideoReadUtil.ReadVideoSize(file)
                    log.info("[CopyVideoService.videoInfoTransfer] 视频文件大小计算成功，视频文件大小:${videoSize}.")
                    video.size = videoSize
                    video.save(flush: true)
                    if (video.hasErrors()) {
                        log.error("[CopyVideoService.videoInfoTransfer] 视频文件保存出错,视频主键:{$video.id}")
                    }
                }
                //计算拷贝到的空间大小并比较
                def fileSize = new File(mount.urlMount).getUsableSpace()
                if (fileSize - 1024 * 1024 * 1024 > Integer.parseInt(video.size)) {
                    //存储空间大于视频大小则进行传输(由于存在误差所以留1G空间)
                    log.info("[CopyVideoService.videoInfoTransfer] 查询到合适的视频转移空间开始视频转移.")
                    def sfile = new File("${mount.urlMount}/${video.fileName}") //定义要转移的路径位置
                    if (!sfile.getParentFile().exists()) {
                        sfile.getParentFile().mkdirs()
                        sfile.getParentFile().setReadable(true)
                        sfile.getParentFile().setWritable(true)
                    }
                    FileUtils.copyFile(file, sfile)
                    log.info("[CopyVideoService.videoInfoTransfer] 视频路径为:${file.getPath()}的视频文件转移成功.")
                    def fileMd5 = MD5Utils.getFileMD5(file)
                    def sfileMd5 = MD5Utils.getFileMD5(sfile)
                    if (fileMd5 && sfileMd5 && fileMd5 == sfileMd5) {
                        //成功后删除本地文件 并将filename的路径改成挂载路径
                        log.info("[CopyVideoService.videoInfoTransfer] 视频转移成功开始删除本地文件.")
                        def delResult = FileUtils.deleteQuietly(file)
                        if (!delResult) {//未能成功删除
                            log.error("[CopyVideoService.videoInfoTransfer] 删除本地文件出错 videoInfo.id=${video.id}")
                        } else {
                            log.info("[CopyVideoService.videoInfoTransfer] 删除本地文件成功.")
                        }
                        def urlNmae = mount.urlMount.substring(mount.urlMount.lastIndexOf("/") + 1)
                        video.fileName = "${urlNmae}/${video.fileName}"
                        video.save(flush: true)
                        if (video.hasErrors()) {
                            log.error("[CopyVideoService.videoInfoTransfer] 修改保存视频挂载路径到${urlNmae}下出错,videoInfo.id=${video.id}")
                        } else {
                            log.info("[CopyVideoService.videoInfoTransfer] 修改视频路径成功,开始添加或修改转移日志.")
                        }
                        saveCopyVideoLog(true, video, num)
                        def mountFile = new File(mount.urlMount)
                        def totalSpace = mountFile.getTotalSpace()
                        def usableSpace = mountFile.getUsableSpace()
                        mount.totalSpace = totalSpace
                        mount.usableSpace = usableSpace
                        mount.freeSpace = totalSpace - usableSpace
                        mount.save(flush: true)
                        if (mount.hasErrors()) {
                            log.error("[CopyVideoService.videoInfoTransfer] 修改挂载的磁盘空间大小出错,mountDisk.id=${mount.id}")
                        } else {
                            log.info("[CopyVideoService.videoInfoTransfer] 修改挂载的磁盘空间大小成功.")
                        }
                        //深圳点播平台推送系统视频点播数据
                        this.notify("pushSvVideoUrlData", video.id)
                        return
                    } else {
                        //重新尝试提交 尝试三次则放弃提交
                        if (linkNum <= 3 && num <= 10) {
                            saveCopyVideoLog(false, video, num)
                            num++
                            linkNum++
                            log.info("[CopyVideoService.videoInfoTransfer] 视频转移失败开始重试.")
                            videoInfoTransfer(video, num, mountDisks)
                        } else {
                            log.error("[CopyVideoService.videoInfoTransfer] 视频转移次数过多，放弃此条数据.")
                            linkNum = 1
                        }
                        return
                    }
                } else {
                    log.info("[CopyVideoService.videoInfoTransfer] 磁盘剩余空间不足以容纳此视频文件，使用下一块磁盘.")
                    numMount++
                }
            }
            if (mountDisks.size() == numMount - 1) {
                log.info("[CopyVideoService.videoInfoTransfer] 定时转移视频时所有配置挂载磁盘都已占满，请挂载新磁盘或清空部分磁盘空间!")
            }
        } catch (e) {
            log.error("[CopyVideoService.videoInfoTransfer.catch] 定时任务转移视频文件到挂载分区出现异常,异常信息:\n${e.message}")
            //重新尝试提交 尝试三次则放弃提交
            if (linkNum <= 3 && num <= 10) {
                saveCopyVideoLog(false, video, num)
                num++
                linkNum++
                log.error("[CopyVideoService.videoInfoTransfer.catch] 视频转移失败开始重试.")
                videoInfoTransfer(video, num, mountDisks)
            } else {
                log.error("[CopyVideoService.videoInfoTransfer.catch] 视频转移次数过多，放弃此条数据.")
                linkNum = 1
            }
        }
    }

    /**
     * 保存视频转移日志
     * @param isSuccess 是否转移成功
     * @param video 需要转移的视频记录
     * @param num 次数
     */
    void saveCopyVideoLog(boolean isSuccess, VideoInfo video, Integer num) {
        def copyVideoLog = CopyVideoLog.findByVideoInfo(video)
        if (!copyVideoLog) {
            log.info("[CopyVideoService.saveCopyVideoLog] 根据videoInfo未查找到视频转移日志,新建日志.")
            copyVideoLog = new CopyVideoLog()
        }
        copyVideoLog.linkNumber = num
        copyVideoLog.isSuccess = isSuccess
        copyVideoLog.videoInfo = video
        copyVideoLog.save(flush: true)
        if (copyVideoLog.hasErrors()) {
            log.error("[CopyVideoService.saveCopyVideoLog] 保存拷贝视频日志记录出错,错误信息：${copyVideoLog.errors}")
        } else {
            log.info("[CopyVideoService.saveCopyVideoLog] 保存视频转移日志成功.")
        }
    }

    /**
     * 文件转移
     * @param trial 相关庭次
     * @param note 文件路径
     * @param mountDisks 磁盘列表
     * @param pdf 1:paf  2:word
     */
    void trialInfoTransfer(TrialInfo trial, String note, List<MountDisk> mountDisks, Integer pdf) {
        try {
            if (!note || note?.contains("movies")) {
                log.info("[CopyVideoService.trialInfoTransfer] 笔录文件不存在或笔录文件路径中包含movies，说明已经转移成功,跳过此条记录.")
                return
            }
            def path = grailsApplication.config.getProperty('tc.trial.note.path')
            def file = new File("${path}/${note}")
            log.info("[CopyVideoService.trialInfoTransfer] 需要拷贝的文件根目录:${path}")
            if (!file.exists()) {
                log.info("[CopyVideoService.trialInfoTransfer] 需要拷贝的路径为${file.getPath()}的笔录文件不存在,查询是否已经转移成功.")
                /*boolean isExists = false
                if (pdf == 1) {
                    //现在在转移pdf,修改note
                    if (!trial.noteWord.startsWith("movies")) {
                        log.info("[CopyVideoService.trialInfoTransfer] pdf笔录对应的word笔录尚未成功转移,禁止同步操作.")
                    } else {
                        def pdfFile = new File("${path}/${trial.noteWord.substring(0, trial.noteWord.indexOf('/'))}/${trial.note}")
                        if (pdfFile.exists()) {
                            trial.note = "${trial.noteWord.substring(0, trial.noteWord.indexOf('/'))}/${trial.note}"
                            trial.save(flush: true)
                            isExists = true
                            log.info("[CopyVideoService.trialInfoTransfer] 需要拷贝的pdf笔录文件已经转移成功.")
                        }
                    }
                }
                if (pdf == 2) {
//                    str
                    //现在在转移word,修改noteWord
                    if (!trial.note.startsWith("movies")) {
                        log.info("[CopyVideoService.trialInfoTransfer] word笔录对应的pdf笔录尚未成功转移,禁止同步操作.")
                    } else {
                        def wordFile = new File("${path}/${trial.note.substring(0, trial.note.indexOf('/'))}/${trial.noteWord}")
                        if (wordFile.exists()) {
                            trial.noteWord = "${trial.note.substring(0, trial.note.indexOf('/'))}/${trial.noteWord}"
                            trial.save(flush: true)
                            isExists = true
                            log.info("[CopyVideoService.trialInfoTransfer] 需要拷贝的word笔录文件已经转移成功.")
                        }
                    }
                }
                if (!isExists) {
                    log.info("[CopyVideoService.trialInfoTransfer] 需要拷贝的笔录文件确实不存在跳过此记录.")
                }*/
                return
            }
            for (def mount : mountDisks) {
                def fileSize = new File(mount.urlMount).getUsableSpace()
                if (fileSize - 1024 * 1024 * 1024 > file.length()) { //存储空间大于视频大小则进行传输(由于存在误差所以留1G空间)
                    log.info("[CopyVideoService.trialInfoTransfer] 查询到合适的文件转移空间开始文件转移.")
                    def sfile = new File("${mount.urlMount}/${note}") //定义要转移的路径位置
                    if (!sfile.getParentFile().exists()) {
                        sfile.getParentFile().mkdirs()
                        sfile.getParentFile().setReadable(true)
                        sfile.getParentFile().setWritable(true)
                    }
                    FileUtils.copyFile(file, sfile)
                    log.info("[CopyVideoService.trialInfoTransfer] 文件路径为:${file.getPath()}的笔录文件转移成功.")
                    def fileMd5 = MD5Utils.getFileMD5(file)
                    def sfileMd5 = MD5Utils.getFileMD5(sfile)
                    if (fileMd5 && sfileMd5 && fileMd5 == sfileMd5) {
                        //成功后删除本地文件 并将filename的路径改成挂载路径
                        log.info("[CopyVideoService.trialInfoTransfer] 笔录文件转移成功开始删除本地文件.")
                        def delResult = FileUtils.deleteQuietly(file)
                        if (!delResult) {
                            log.error("[CopyVideoService.trialInfoTransfer] 删除本地文件出错 trialInfo.id=${trial.id}")
                        } else {
                            log.info("[CopyVideoService.trialInfoTransfer] 删除本地文件成功.")
                        }
                        def urlNmae = mount.urlMount.substring(mount.urlMount.lastIndexOf("/") + 1)
                        if (pdf == 1) {
                            trial.note = "${urlNmae}/${note}"
                            trial.save(flush: true)
                            if (trial.hasErrors()) {
                                log.error("[CopyVideoService.trialInfoTransfer] 修改保存pdf笔录挂载路径到${urlNmae}下出错,trial.id=${trial.id}")
                            } else {
                                log.info("[CopyVideoService.trialInfoTransfer] 修改pdf笔录路径成功.")
                            }
                        } else if (pdf == 2) {
                            trial.noteWord = "${urlNmae}/${note}"
                            trial.save(flush: true)
                            if (trial.hasErrors()) {
                                log.error("[CopyVideoService.trialInfoTransfer] 修改保存word笔录挂载路径到${urlNmae}下出错,trial.id=${trial.id}")
                            } else {
                                log.info("[CopyVideoService.trialInfoTransfer] 修改word笔录路径成功.")
                            }
                        }
                        def mountFile = new File(mount.urlMount)
                        def totalSpace = mountFile.getTotalSpace()
                        def usableSpace = mountFile.getUsableSpace()
                        mount.totalSpace = totalSpace
                        mount.usableSpace = usableSpace
                        mount.freeSpace = totalSpace - usableSpace
                        mount.save(flush: true)
                        if (mount.hasErrors()) {
                            log.error("[CopyVideoService.trialInfoTransfer] 修改挂载的磁盘空间大小出错,mountDisk.id=${mount.id}")
                        } else {
                            log.info("[CopyVideoService.trialInfoTransfer] 修改挂载的磁盘空间大小成功.")
                        }
                        return
                    } else {
                        //重新尝试提交 尝试三次则放弃提交
                        if (linkNum <= 3) {
                            linkNum++
                            log.info("[CopyVideoService.trialInfoTransfer] 笔录转移失败开始重试.")
                            trialInfoTransfer(trial, note, mountDisks, pdf)
                        } else {
                            log.error("[CopyVideoService.trialInfoTransfer] 视频转移次数过多，放弃此条数据.")
                            linkNum = 1
                        }
                        return
                    }
                } else {
                    log.info("[CopyVideoService.trialInfoTransfer] 磁盘剩余空间不足以容纳此笔录文件，使用下一块磁盘.")
                    numMount++
                }
            }
            if (mountDisks.size() == numMount - 1) {
                log.info("[CopyVideoService.trialInfoTransfer] 定时转移笔录文件时所有配置挂载磁盘都已占满，请挂载新磁盘或清空部分磁盘空间!")
            }
        } catch (e) {
            log.error("[CopyVideoService.trialInfoTransfer.catch] 定时任务转移笔录文件到挂载分区出现异常,异常信息:\n${e.message}")
            //重新尝试提交 尝试三次则放弃提交
            if (linkNum <= 3) {
                linkNum++
                log.error("[CopyVideoService.trialInfoTransfer.catch] 视频转移失败开始重试.")
                trialInfoTransfer(trial, note, mountDisks, pdf)
            } else {
                log.error("[CopyVideoService.trialInfoTransfer.catch] 视频转移次数过多，放弃此条数据.")
                linkNum = 1
            }
        }
    }

    /**
     * 删除所有排期空文件夹
     */
    void deletePlanFolder() {
        try {
            def path = grailsApplication.config.getProperty('tc.trial.note.path')
            log.info("[CopyVideoService.deletePlanFolder] 需要删除的排期空文件夹根目录:${path}")
            def file = new File(path)
            def filelist = file.listFiles()
            if (filelist == null || filelist.length == 0) {
                log.info("[CopyVideoService.deletePlanFolder] 根目录下没有文件夹或文件,结束.")
                return
            }
            log.info("[CopyVideoService.deletePlanFolder] 查询到文件夹或文件的总数为${filelist.size()},开始遍历.")
            // 存在文件 遍历 判断
            for (File f : filelist) {
                log.info("[CopyVideoService.deletePlanFolder] 文件或文件夹的真实路径:${f.getAbsolutePath()}.")
                // 判断是否为 文件夹
                if (!f.isDirectory()) {
                    log.info("[CopyVideoService.deletePlanFolder] 此路径为文件,跳过此条数据.")
                    continue
                }
                def fName = f.getAbsolutePath().substring(f.getAbsolutePath().lastIndexOf("/") + 1)
                if (!fName.isNumber()) {
                    log.info("[CopyVideoService.deletePlanFolder] 此文件夹不可能是排期文件夹,跳过此条数据.")
                    continue
                }
                def planInfo = PlanInfo.get(fName as Long)
                if (!planInfo) {
                    log.info("[CopyVideoService.deletePlanFolder] 根据此文件夹名称未找到对应排期,跳过此条数据.")
                    continue
                }
                def dateFileList = f.listFiles()
                if (dateFileList == null || dateFileList.length == 0) {
                    FileUtils.deleteQuietly(f)
                    log.info("[CopyVideoService.deletePlanFolder] 此文件夹中不存在文件夹或文件,删除此文件夹成功.")
                    continue
                }
                def success = false
                log.info("[CopyVideoService.deletePlanFolder] 此文件夹中含有文件夹或文件，开始判断子文件夹或文件.")
                for (File dateFile : dateFileList) {
                    log.info("[CopyVideoService.deletePlanFolder] 子文件夹或子文件路径:${dateFile.getAbsolutePath()}")
                    if (dateFile.isDirectory()) {
                        log.info("[CopyVideoService.deletePlanFolder] 此路径下为文件夹.")
                        String str = dateFile.getAbsolutePath().substring(dateFile.getAbsolutePath().lastIndexOf("/") + 1)
                        String regex = "[0-9]{4}-[0-9]{2}-[0-9]{2}"
                        Pattern pattern = Pattern.compile(regex)
                        Matcher m = pattern.matcher(str)
                        boolean dateFlag = m.matches()
                        if (dateFlag) {
                            log.info("[CopyVideoService.deletePlanFolder] 文件夹下日期格式的文件夹,再次进行判断文件夹下是否还有视频文件.")
                            def videoFileList = dateFile.listFiles()
                            if (videoFileList == null || videoFileList.length == 0) {
                                log.info("[CopyVideoService.deletePlanFolder] 文件夹下日期格式的文件夹为空")
                                success = false
                            } else {
                                log.info("[CopyVideoService.deletePlanFolder] 文件夹下日期格式的文件夹含有文件夹或文件，")
                                for (File videoFile : videoFileList) {
                                    String videoName = videoFile.getName()
                                    if (videoName.endsWith("mp4")) {
                                        log.info("[CopyVideoService.deletePlanFolder] 文件夹下路径(${videoFile.getAbsolutePath()})存在mp4文件,不进行删除.")
                                        success = true
                                    }
                                }
                            }
                        }
                    }
                    if (dateFile.isFile()) {
                        log.info("[CopyVideoService.deletePlanFolder] 此路径下为文件.")
                        String filename = dateFile.getName()
                        if (filename.endsWith("jpg") || filename.endsWith("jepg") || filename.endsWith("jpeg") ||
                                filename.endsWith("doc") || filename.endsWith("docx") || filename.endsWith("pdf")) {
                            log.info("[CopyVideoService.deletePlanFolder] 文件为jpg、jepg、jpeg、doc、docx、pdf等格式的文件，不进行删除.")
                            success = true
                        }
                    }
                }
                if (!success) {
                    FileUtils.deleteQuietly(f)
                    log.info("[CopyVideoService.deletePlanFolder] 经过判断,允许删除文件夹(${f.getAbsolutePath()}),删除成功.")
                }
            }
        } catch (e) {
            log.error("[CopyVideoJob.deletePlanFolder] 删除空文件夹出错,错误信息:\n${e.message}")
        }
    }

    /**
     * 将本地/usr/local/movies下的视频文件没有入库到videoInfo  检索并进行入库。
     */
    void insertVideoInfo() {
        try {
            def path = "/usr/local/movies/"
            def file = new File(path)
            def filelist = file.listFiles()
            if (filelist == null || filelist.length == 0) {
                println("[CopyVideoService.insertVideoInfo] 根目录下没有文件夹或文件,结束.")
                return
            }
            println("[CopyVideoService.insertVideoInfo] 查询到文件夹或文件的总数为${filelist.size()},开始遍历.")
            // 存在文件 遍历 判断
            for (File f : filelist) {
                println("[CopyVideoService.insertVideoInfo] 文件或文件夹的真实路径:${f.getAbsolutePath()}.")
                // 判断是否为 文件夹
                if (!f.isDirectory()) {
                    println("[CopyVideoService.insertVideoInfo] 此路径为文件,跳过此条数据.")
                    continue
                }
                def fName = f.getAbsolutePath().substring(f.getAbsolutePath().lastIndexOf("/") + 1)
                if (!fName.isNumber()) {
                    println("[CopyVideoService.insertVideoInfo] 此文件夹不可能是排期文件夹,跳过此条数据.")
                    continue
                }
                def planInfo = PlanInfo.get(fName as Long)
                if (!planInfo) {
                    println("[CopyVideoService.insertVideoInfo] 根据此文件夹名称未找到对应排期,跳过此条数据.")
                    continue
                }
                def dateFileList = f.listFiles()
                if (dateFileList == null || dateFileList.length == 0) {
                    FileUtils.deleteQuietly(f)
                    println("[CopyVideoService.insertVideoInfo] 此文件夹中不存在文件夹或文件,删除此文件夹成功.")
                    continue
                }
               
                println("[CopyVideoService.insertVideoInfo] 此文件夹中含有文件夹或文件，开始判断子文件夹或文件.")
                for (File dateFile : dateFileList) {
                    println("[CopyVideoService.insertVideoInfo] 子文件夹或子文件路径:${dateFile.getAbsolutePath()}")
                    if (dateFile.isDirectory()) {
                        println("[CopyVideoService.insertVideoInfo] 此路径下为文件夹.")
                        String str = dateFile.getAbsolutePath().substring(dateFile.getAbsolutePath().lastIndexOf("/") + 1)
                        //判断文件夹的日期
                        if (str == "2021-05-31"){
                            def videoFileList = dateFile.listFiles()
                            if (videoFileList == null || videoFileList.length == 0) {
                                println("[CopyVideoService.insertVideoInfo] 文件夹下日期格式的文件夹为空")
                            } else {
                                println("[CopyVideoService.insertVideoInfo] 文件夹下日期格式的文件夹含有文件夹或文件，按照日期升序")
                                Arrays.sort(videoFileList, new Comparator<File>() {
                                    int compare(File f1, File f2) {
                                        long diff = f1.lastModified() - f2.lastModified();
                                        if (diff > 0)
                                            return 1
                                        else if (diff == 0)
                                            return 0
                                        else
                                            return -1//如果 if 中修改为 返回-1 同时此处修改为返回 1  排序就会是递减
                                    }

                                    boolean equals(Object obj) {
                                        return true
                                    }

                                })
                                for (int i = 0; i < videoFileList.length; i++) {
                                    def fileName = videoFileList[i].getName()
                                    if (fileName.endsWith("mp4")) {
                                        def videoFileName = "${fName}/${str}/${fileName}"
                                        if (VideoInfo.findByFileName("${videoFileName}")){
                                            println("${videoFileName}")
                                            continue
                                        }
                                        def fileNameArr = fileName.split("_")
                                        def dateStr = fileNameArr[1]+fileNameArr[2]
                                        def startDateStr = dateStr.substring(0,4)+"-"+dateStr.substring(4,6)+"-"+dateStr.substring(6,8)+" "+dateStr.substring(8,10)+":"+dateStr.substring(10,12)+":"+dateStr.substring(12,14)
                                        def startDate = DateUtil.parse(startDateStr,"yyyy-MM-dd HH:mm:ss")
                                        def endDate = new Date(videoFileList[i].lastModified())
                                        def channelNum = fileNameArr[3].substring(0,1)
                                        def channelName
                                        if (channelNum == "1"){
                                            channelName = "合成画面"
                                        }else{
                                            channelName = "证据画面"
                                        }
                                        def videoSize = VideoReadUtil.ReadVideoSize(videoFileList[i])
                                        def source = videoFileList[i]//原视频文件
                                        def length
                                        try {//尝试分析视频文件，通过视频文件获取视频时长
                                            def multObj = new MultimediaObject(source)
                                            def duration = multObj.getInfo().getDuration()
                                            length = (duration / 1000).intValue()
                                        } catch (e) {
                                            e.printStackTrace()
                                        }
                                        new VideoInfo(
                                                uid: UUIDGenerator.nextUUID(),
                                                channelNum: channelNum,    //通道号
                                                channelName: channelName,    //通道名称
                                                startRecTime: startDate,    //开始录像时间
                                                endRecTime: endDate,    //结束录像时间
                                                fileName: videoFileName,    //视频文件名称
                                                size: videoSize,//文件大小
                                                length: length,    //录像时长
                                                active: DataStatus.SHOW,    //数据状态
                                                trialInfo: TrialInfo.findByPlanInfo(planInfo) //所属庭审
                                        ).save(flush: true)
                                    }
                                }
                                
                            }
                        }else{
                            break
                        }
                        
                    }
                }
            }
        } catch (e) {
            println("[CopyVideoJob.insertVideoInfo] 删除空文件夹出错,错误信息:\n${e.message}")
        }
    }
}
