package com.hxht.autodeploy.async

import com.hxht.techcrt.court.VideoInfo
import grails.core.GrailsApplication
import grails.gorm.transactions.Transactional
import ws.schild.jave.MultimediaObject
import ws.schild.jave.ScreenExtractor

@Transactional
class ScreenShotService {

    GrailsApplication grailsApplication
    /**
     * 异步执行视频截图功能
     * @param video
     * @param trial
     * @return
     */
    def screenShot(Long videoId) {
        try{
            def video = VideoInfo.get(videoId)
            def trial = video.trialInfo
            def uploadUrl = grailsApplication.config.getProperty('tc.trial.images.path')
            def file = new File(  uploadUrl + "/"  + trial.id + "/" + video.channelNum + video.channelName +
                    video.startRecTime?.format("HH:mm").toString().replace(":","") + ".jpg")
            if (file.exists()){
                return
            }
            def url = "http://"+ trial.courtroom.storeIp +":8200/"+video.fileName
            URL source = new URL(url)
            MultimediaObject multimediaObject = new MultimediaObject(source)
            //图片的宽度
            def width = 180
            //图片的高度
            def height = 70
            //截取图片时的秒数
            def seconds = 2
            //图片的质量（数值越大质量越差）
            def quality = 1
            def instance = new ScreenExtractor()
            instance.render(multimediaObject, width, height, seconds, file, quality)
            return '执行视频截图完毕'
        } catch (Exception e) {
            log.error("异步执行视频截图出错，错误信息为: ${e.getMessage()}")
        }
    }
}
