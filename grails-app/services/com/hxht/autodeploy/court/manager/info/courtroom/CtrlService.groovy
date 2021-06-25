package com.hxht.autodeploy.court.manager.info.courtroom

import com.hxht.techcrt.court.Courtroom
import grails.gorm.transactions.Transactional
import groovy.json.JsonSlurper

/**
 * 法庭中控服务 by arctic in 2019.11.14
 */
@Transactional
class CtrlService {

    /**
     * 获取法庭配置
     * @param courtroom 法庭
     * @return 法庭配置Map对象
     */
    def getCfg(Courtroom courtroom) {
        if (!courtroom) {
            log.info("获取法庭配置信息出错，法庭对象为null CtrlService.getCfg")
            throw new RuntimeException()
        }
        def cfg = [
                encode     : [],//法庭编码器
                ycEncode   : [],//法庭远程编码器
                decode     : [],//法庭解码器
                videoMatrix: [],//VIDEO矩阵
                vgaMatrix  : [],//VGA矩阵
                outMatrix  : [],//输出控制
                soundMatrix: [],//音量控制
                total      : [],//综合控制
                power      : [],//强电控制
                irctrl     : [],//红外控制
                //这个数据属于这个法庭--->也就是一个房间    按钮组基本不变，摄像头预位置基本不变    摄像头位置可变可编辑，获取的就是这个东西。。。。
                camera     : [//摄像头控制
                      buttons : [],//摄像头按钮组
                      presets : [],//摄像头预置位
                      position: [] //摄像头位置
                ]
        ]
        if (courtroom.cfg) {
            cfg = new JsonSlurper().parseText(courtroom.cfg) as HashMap
        }
        cfg
    }

    /**
     * 编码器删除  执行
     * @param uuidsArr  uuid字符串数组
     * @param cfg  法庭配置
     */
    def delEncodes(String[] uuidsArr, def cfg){
        for (String uuid:uuidsArr){
            for (int i=0; i<cfg.encode.size(); i++){
                if (cfg.encode[i].uuid == uuid){
                    cfg.encode.remove(i)
                }
            }
        }
    }

    /**
     * 编码器删除  执行
     * @param uuidsArr  uuid字符串数组
     * @param cfg  法庭配置
     */
    def delDecodes(String[] uuidsArr, def cfg){
        for (String uuid:uuidsArr){
            for (int i=0; i<cfg.decode.size(); i++){
                if (cfg.decode[i].uuid == uuid){
                    cfg.decode.remove(i)
                }
            }
        }
    }

    /**
     * VIDEO矩阵删除  执行
     * @param uuidsArr  uuid字符串数组
     * @param cfg  法庭配置
     */
    def delVideos(String[] uuidsArr, def cfg){
        for (String uuid:uuidsArr){
            for (int i=0; i<cfg.videoMatrix.size(); i++){
                if (cfg.videoMatrix[i].uuid == uuid){
                    cfg.videoMatrix.remove(i)
                }
            }
        }
    }

    /**
     * VGA矩阵删除  执行
     * @param uuidsArr  uuid字符串数组
     * @param cfg  法庭配置
     */
    def delVgas(String[] uuidsArr, def cfg){
        for (String uuid:uuidsArr){
            for (int i=0; i<cfg.vgaMatrix.size(); i++){
                if (cfg.vgaMatrix[i].uuid == uuid){
                    cfg.vgaMatrix.remove(i)
                }
            }
        }
    }

    /**
     * 输出控制删除  执行
     * @param uuidsArr  uuid字符串数组
     * @param cfg  法庭配置
     */
    def delOuts(String[] uuidsArr, def cfg){
        for (String uuid:uuidsArr){
            for (int i=0; i<cfg.outMatrix.size(); i++){
                if (cfg.outMatrix[i].uuid == uuid){
                    cfg.outMatrix.remove(i)
                }
            }
        }
    }

    /**
     * 红外控制删除  执行
     * @param uuidsArr  uuid字符串数组
     * @param cfg  法庭配置
     */
    def delIrctrls(String[] uuidsArr, def cfg){
        for (String uuid:uuidsArr){
            for (int i=0; i<cfg.irctrl.size(); i++){
                if (cfg.irctrl[i].uuid == uuid){
                    cfg.irctrl.remove(i)
                }
            }
        }
    }

    /**
     * 红外控制删除  执行
     * @param uuidsArr  uuid字符串数组
     * @param cfg  法庭配置
     */
    def delSounds(String[] uuidsArr, def cfg){
        for (String uuid:uuidsArr){
            for (int i=0; i<cfg.soundMatrix.size(); i++){
                if (cfg.soundMatrix[i].uuid == uuid){
                    cfg.soundMatrix.remove(i)
                }
            }
        }
    }

    /**
     * 综合控制删除  执行
     * @param uuidsArr  uuid字符串数组
     * @param cfg  法庭配置
     */
    def delTotals(String[] uuidsArr, def cfg){
        for (String uuid:uuidsArr){
            for (int i=0; i<cfg.total.size(); i++){
                if (cfg.total[i].uuid == uuid){
                    cfg.total.remove(i)
                }
            }
        }
    }

    /**
     * 强电控制删除  执行
     * @param uuidsArr  uuid字符串数组
     * @param cfg  法庭配置
     */
    def delPowers(String[] uuidsArr, def cfg){
        for (String uuid:uuidsArr){
            for (int i=0; i<cfg.powerNew.size(); i++){
                if (cfg.powerNew[i].uuid == uuid){
                    cfg.powerNew.remove(i)
                }
            }
        }
    }

    /**
     * 强电控制删除  执行
     * @param uuidsArr  uuid字符串数组
     * @param cfg  法庭配置
     */
    def delPowerNew(String[] uuidsArr, def cfg){
        for (String uuid:uuidsArr){
            for (int i=0; i<cfg.powerNew.size(); i++){
                if (cfg.powerNew[i].uuid == uuid){
                    cfg.powerNew.remove(i)
                }
            }
        }
    }

    /**
     * 摄像头控制删除  执行
     * @param uuidsArr  uuid字符串数组
     * @param cfg  法庭配置
     */
    def delCameras(String[] uuidsArr, def cfg){
        for (String uuid:uuidsArr){
            for (int i=0; i<cfg.camera.position.size(); i++){
                if (cfg.camera.position[i].uuid == uuid){
                    cfg.camera.position.remove(i)
                }
            }
        }
    }

    /**
     * 摄像头控制删除  执行
     * @param uuidsArr  uuid字符串数组
     * @param cfg  法庭配置
     */
    def delButtons(String[] uuidsArr, def cfg){
        for (String uuid:uuidsArr){
            for (int i=0; i<cfg.camera.buttons.size(); i++){
                if (cfg.camera.buttons[i].uuid == uuid){
                    cfg.camera.buttons.remove(i)
                }
            }
        }
    }

    /**
     * 摄像头控制删除  执行
     * @param uuidsArr  uuid字符串数组
     * @param cfg  法庭配置
     */
    def delPresets(String[] uuidsArr, def cfg){
        for (String uuid:uuidsArr){
            for (int i=0; i<cfg.camera.presets.size(); i++){
                if (cfg.camera.presets[i].uuid == uuid){
                    cfg.camera.presets.remove(i)
                }
            }
        }
    }

    /**
     * 远程编码器控制删除  执行
     * @param uuidsArr  uuid字符串数组
     * @param cfg  法庭配置
     */
    def delYcEncodes(String[] uuidsArr, def cfg){
        for (String uuid:uuidsArr){
            for (int i=0; i<cfg.ycEncode.size(); i++){
                if (cfg.ycEncode[i].uuid == uuid){
                    cfg.ycEncode.remove(i)
                }
            }
        }
    }

}
