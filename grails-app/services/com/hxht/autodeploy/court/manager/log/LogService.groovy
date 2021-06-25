package com.hxht.autodeploy.court.manager.log

import com.hxht.techcrt.LogLogin
import com.hxht.techcrt.LogSystem
import grails.gorm.transactions.Transactional

/**
 * LogSystem Precess Class By Arctic in 2019/10/19
 */
@Transactional
class LogService {
    /**
     * 获取每页的日志列表
     * @param draw  记录操作的次数 每次加1
     * @param start  起始编号
     * @param length  查询长度
     * @param search  查询关键词
     * @return  List<LogSystem>
     */
    def logSystemList(int draw, int start, int length, String search) {
        def model = [:]
        model.put("draw", draw)
        def count = LogSystem.createCriteria().count() {
            if (search) {
                or {
                    like("message", "%${search}%")
                    like("level", "%${search}%")
                }
            }
        }
        def dataList = LogSystem.createCriteria().list {
            and {
                setMaxResults(length)
                setFirstResult(start)
            }
            or {
                like("message", "%${search}%")
                like("level", "%${search}%")
            }
            order("level","asc")
            order("dateCreated", "desc")
        } as List<LogSystem>
            def modelDataList = []
        for (def logSystem : dataList) {
            def data = [:]
            data.put("id", logSystem.id)
            data.put("level", logSystem.level)
            data.put("message", logSystem.message.length()>74?logSystem.message.substring(0,74):logSystem.message)
            data.put("dateCreated", logSystem.dateCreated?.format('yyyy/MM/dd HH:mm'))
            modelDataList.add(data)
        }
        model.put("recordsTotal", count)//数据总条数
        model.put("recordsFiltered", count)//显示的条数
        model.put("data", modelDataList)
        model
    }

    /**
     * 获取每页的登录日志列表
     * @param draw  记录操作的次数 每次加1
     * @param start  起始编号
     * @param length  查询长度
     * @param search  查询关键词
     * @return  List<LogLogin>
     */
    def logLoginList(int draw, int start, int length, String search) {
        def model = [:]
        model.put("draw", draw)
        //根据信息查询条数
        def count = LogLogin.createCriteria().count() {
            if (search) {
                or {
                    like("message", "%${search}%")
                }
            }
        }
        def dataList = LogLogin.createCriteria().list {
            and {
                setMaxResults(length)
                setFirstResult(start)
            }
            or {
                like("message", "%${search}%")
            }
            order("dateCreated", "desc")
        } as List<LogLogin>
        def modelDataList = []
        for (def logLogin : dataList) {
            def data = [:]
            data.put("id", logLogin.id)
//            data.put("userName", User.findAllById(logLogin.userId).size()==1?User.findAllById(logLogin.userId).get(0).getRealName():"用户不存在")
            data.put("message", logLogin.message.length()>74?logLogin.message.substring(0,74):logLogin.message)
            data.put("dateCreated", logLogin.dateCreated?.format('yyyy/MM/dd HH:mm'))
            modelDataList.add(data)
        }
        model.put("recordsTotal", count)//数据总条数
        model.put("recordsFiltered", count)//显示的条数
        model.put("data", modelDataList)
        model
    }
}
