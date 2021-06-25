package com.hxht.autodeploy

/**
 * 系统操作日志工具类 create in 2019.10.21 -> update in 2020.11.25
 * @auther Arctic
 */
class LogSystemUtil {

    //日志等级ERROR > WARN > INFO > DEBUG
    public static final String ERROR = "1"
    public static final String WARN  = "2"
    public static final String INFO  = "3"
    public static final String DEBUG = "4"

    /**
     * 自动以系统操作日志
     * @param level 从上面的等级中选择一个
     * @param message  日志信息
     */
    static void log(String level, String message){
        new LogSystem(
                level: level,
                message: message
        ).save(flush:true)
    }
}
