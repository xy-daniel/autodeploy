package com.hxht.autodeploy.mem

import com.hxht.techcrt.court.mem.VideoToMp3
import grails.gorm.transactions.Transactional

@Transactional("mem")
class VideoToMp3Service {

    def findByTrialInfoId(Long trialId) {
        return VideoToMp3.findByTrialInfo(trialId)
    }

    /**
     * 保存转换音频失败的错误记录
     */
    def saveVideoToMp3(boolean isSuccess, Long videoInfo, Integer linkNumber, Long trialInfo) {
        def videoToMp3 = new VideoToMp3()
        videoToMp3.isSuccess = isSuccess
        videoToMp3.videoInfo = videoInfo
        videoToMp3.linkNumber = linkNumber
        videoToMp3.trialInfo = trialInfo
        videoToMp3.dateCreated = new Date()
        videoToMp3.save(flush: true)
        if (videoToMp3.hasErrors()){
            log.error("[videoToMp3.save]保存视频转码信息时失败.\n" +
                    "错误信息：${videoToMp3.errors}")
        }
    }
}
