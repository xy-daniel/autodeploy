package com.hxht.autodeploy.triggers

import com.hxht.techcrt.CopyVideoLog
import com.hxht.techcrt.jobs.CopyVideoService
import com.hxht.techcrt.MountDisk
import com.hxht.techcrt.court.TrialInfo
import com.hxht.techcrt.court.VideoInfo

/**
 * 2021.04.06 >>> 视频转移到挂载磁盘定时器修改 daniel
 */
class CopyVideoJob {
    CopyVideoService copyVideoService

    static triggers = {
        cron cronExpression: "0 01 22 * * ? *"  //每天1：10执行一次
    }

    def execute() {
        log.info("++++++++++++++++++视频转移及删除空文件夹开始++++++++++++++++")
        def mountDiskList = this.getMountDiskList()
        if (!mountDiskList) {
            log.info("+++++++++++++++++++++未配置挂载磁盘+++++++++++++++++++++++")
            return
        }
        log.info("+++++++++++++++转移videoInfo表格中的视频文件+++++++++++++++")
        this.copyVideo(mountDiskList)
        log.info("++++++++++++++++++转移转移失败的视频文件+++++++++++++++++++")
        this.copyErrorVideo(mountDiskList)
        log.info("+++++++++++++++++++++转移pdf笔录文件++++++++++++++++++++++")
        this.copyWordPdf(mountDiskList)
        log.info("+++++++++++++++++++++转移word笔录文件+++++++++++++++++++++")
        this.copyWord(mountDiskList)
        log.info("++++++++++++++++++++删除所有排期空文件夹+++++++++++++++++++")
        copyVideoService.deletePlanFolder()
        log.info("++++++++++++++++++视频转移及删除空文件夹完毕++++++++++++++++")
    }

    /**
     * 将videoInfo中的视频转移，步骤如下：
     *    定时任务启动调用的接口
     *    向挂载磁盘拷贝文件
     *    查询出视频记录表中所有没有成功并且小于等于十次的记录和去除已经拷贝成功的日志
     * @param mountDiskList 磁盘列表
     */
    void copyVideo(List<MountDisk> mountDiskList) {
        def videoList = VideoInfo.findAllByFileNameNotLike("%movies%")
        if (videoList.size() == 0) {
            log.info("[CopyVideoJob.copyVideo] 没有找到需要转移的视频记录,结束视频转移.")
            return
        }
        log.info("[CopyVideoJob.copyVideo] 检索到${videoList.size()}个需要转移的视频记录,开始进行视频转移.")
        for (def video : videoList) {
            def num = 1
            copyVideoService.videoInfoTransfer(video, num, mountDiskList)
        }
    }

    /**
     * 将转移出错的视频再次转移,查找所有记录为失败并且尝试次数小于等于十次的视频再次提交
     * @param mountDiskList 磁盘列表
     */
    void copyErrorVideo(List<MountDisk> mountDiskList) {
        def videoLogList = CopyVideoLog.findAllByIsSuccessAndLinkNumberLessThanEquals(false, 10)
        if (videoLogList.size() == 0) {
            log.info("[CopyVideoLog.copyErrorVideo] 没有查询到转移失败的日志记录,返回.")
            return
        }
        log.info("[CopyVideoLog.copyErrorVideo] 获取视频转移时出错的视频,数量:${videoLogList.size()},开始将这些视频再次尝试转移.")
        for (def videoLog : videoLogList) {
            def linkNumber = videoLog.linkNumber
            def videoInfo = videoLog.videoInfo
            if (!videoInfo) {
                log.error("[CopyVideoLog.copyErrorVideo] 根据视频转移日志没有查询到对应的videoINfo,跳过此条记录.")
                continue
            }
            log.error("[CopyVideoLog.copyErrorVideo] 开始将videoInfo.id=${videoInfo.id}的视频文件进行转移.")
            copyVideoService.videoInfoTransfer(videoInfo, linkNumber, mountDiskList)
        }
    }

    /**
     * pdf转移接口
     * @param mountDiskList 磁盘列表
     */
    void copyWordPdf(List<MountDisk> mountDiskList) {
        def trialInfoList = TrialInfo.findAllByNoteIsNotNullAndNoteNotLike("%movies%")
        if (trialInfoList.size() == 0) {
            log.info("[CopyVideoLog.copyWordPdf] 没有需要转移的pdf文档,结束.")
            return
        }
        log.info("[CopyVideoLog.copyWordPdf] 检索到${trialInfoList.size()}个pdf文档,开始转移.")
        for (def trialInfo : trialInfoList) {
            copyVideoService.trialInfoTransfer(trialInfo, trialInfo.note, mountDiskList, 1)
        }
    }

    /**
     * 将word转移
     * @param mountDiskList 磁盘列表
     */
    void copyWord(List<MountDisk> mountDiskList) {
        def trialInfoList = TrialInfo.findAllByNoteWordIsNotNullAndNoteWordNotLike("%movies%")
        if (trialInfoList.size() == 0) {
            log.info("[CopyVideoLog.copyWord] 没有需要转移的word文档,结束.")
            return
        }
        log.info("[CopyVideoLog.copyWord] 检索到${trialInfoList.size()}个word文档,开始转移.")
        for (def trialInfo : trialInfoList) {
            copyVideoService.trialInfoTransfer(trialInfo, trialInfo.noteWord, mountDiskList, 2)
        }
    }

    /**
     * 获取磁盘列表
     * @return 磁盘列表
     */
    List<MountDisk> getMountDiskList() {
        def mountDiskList = MountDisk.findAll()
        log.info("[CopyVideoJob.getMountDiskList] 查询所有挂载磁盘,数量:${mountDiskList.size()}")
        if (!mountDiskList) {
            log.info("[CopyVideoJob.getMountDiskList] 没有查找到挂载磁盘.")
            return null
        }
        return mountDiskList
    }
}
