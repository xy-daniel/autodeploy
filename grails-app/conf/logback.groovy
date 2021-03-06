import grails.util.BuildSettings
import grails.util.Environment
import org.springframework.boot.logging.logback.ColorConverter
import org.springframework.boot.logging.logback.WhitespaceThrowableProxyConverter
import ch.qos.logback.classic.filter.LevelFilter
import ch.qos.logback.core.spi.FilterReply

import java.nio.charset.Charset

conversionRule 'clr', ColorConverter
conversionRule 'wex', WhitespaceThrowableProxyConverter

// See http://logback.qos.ch/manual/groovy.html for details on configuration
appender('STDOUT', ConsoleAppender) {
    filter(LevelFilter) {
        level = WARN
        onMatch = FilterReply.DENY
        onMismatch = FilterReply.ACCEPT
    }
    encoder(PatternLayoutEncoder) {
        charset = Charset.forName('UTF-8')

        pattern =
                '%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} ' + // Date
                        '%clr(%5p) ' + // Log level
                        '%clr(---){faint} %clr([%15.15t]){faint} ' + // Thread
                        '%clr(%-40.40logger:%line{39}){cyan} %clr(:){faint} ' + // Logger
                        '%m%n%wex' // Message
    }
}
//SizeAndTimeBasedFNATP is deprecated. Use SizeAndTimeBasedRollingPolicy instead
def targetDir = BuildSettings.TARGET_DIR
if (Environment.isDevelopmentMode() && targetDir != null) {
    appender("FULL_STACKTRACE", FileAppender) {
        file = "${targetDir}/auto/stacktrace.log"
        append = true
        encoder(PatternLayoutEncoder) {
            pattern = "%level %logger - %msg%n"
        }
    }
    logger("StackTrace", ERROR, ['FULL_STACKTRACE'], false)
}

//定义当前目录
def HOME_DIR = "/usr/local/auto_logs"

//ERROR级别的日志
appender("ERROR", RollingFileAppender) {
    //过滤器，只记录ERROR级别的日志
    filter(LevelFilter) {
        level = ERROR
        onMatch = FilterReply.ACCEPT
        onMismatch = FilterReply.DENY
    }
    //PatternLayoutEncoder对输出日志信息进行格式化
    encoder(PatternLayoutEncoder) {
        //默认为pattern = "%level %logger - %msg%n"
        //%d表示日期，%thread表示线程名，%level日志级别，%file具体的文件，%line记录日志位置，%msg日志消息，%n换行符
        pattern =
                '%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} ' + // Date
                        '%clr(%5p) ' + // Log level
                        '%clr(---){faint} %clr([%15.15t]){faint} ' + // Thread
                        '%clr(%-40.40logger:%line{39}){cyan} %clr(:){faint} ' + // Logger
                        '%m%n%wex' // Message
    }
    //指定日志生成格式
    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = "${HOME_DIR}/logs/auto/%d{yyyy-MM-dd}_ERROR_%i.log"
        maxHistory = 15 //日志最长保留30天
        timeBasedFileNamingAndTriggeringPolicy(SizeAndTimeBasedFNATP) {
            maxFileSize = "50MB"
        }
    }

    append = true
}
//logger("console",ERROR,['ERROR'],false)

//INFO级别的日志
appender("INFO", RollingFileAppender) {
    //过滤器，只记录INFO级别的日志
    filter(LevelFilter) {
        level = INFO
        onMatch = FilterReply.ACCEPT
        onMismatch = FilterReply.DENY
    }
    //PatternLayoutEncoder对输出日志信息进行格式化
    encoder(PatternLayoutEncoder) {
        //默认为pattern = "%level %logger - %msg%n"
        //%d表示日期，%thread表示线程名，%level日志级别，%file具体的文件，%line记录日志位置，%msg日志消息，%n换行符
        pattern =
                '%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} ' + // Date
                        '%clr(%5p) ' + // Log level
                        '%clr(---){faint} %clr([%15.15t]){faint} ' + // Thread
                        '%clr(%-40.40logger:%line{39}){cyan} %clr(:){faint} ' + // Logger
                        '%m%n%wex' // Message
    }
    //指定日志生成格式，文件名以日期命名，生成每日日志文件，如果超出大小则另起文件存放
    //%d{yyyy-MM-dd}-日期，%i-用于记录每日日志个数
    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = "${HOME_DIR}/logs/auto/%d{yyyy-MM-dd}_INFO_%i.log"
        maxHistory = 30 //日志最长保留30天
        timeBasedFileNamingAndTriggeringPolicy(SizeAndTimeBasedFNATP) {
            maxFileSize = "10MB" //单个日志文件最大为10MB
        }
    }

    append = true
}

//DEBUG级别的日志
appender("DEBUG", RollingFileAppender) {
    //过滤器，只记录DEBUG级别的日志
    filter(LevelFilter) {
        level = DEBUG
        onMatch = FilterReply.ACCEPT
        onMismatch = FilterReply.DENY
    }
    //PatternLayoutEncoder对输出日志信息进行格式化
    encoder(PatternLayoutEncoder) {
        //默认为pattern = "%level %logger - %msg%n"
        //%d表示日期，%thread表示线程名，%level日志级别，%file具体的文件，%line记录日志位置，%msg日志消息，%n换行符
        pattern =
                '%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} ' + // Date
                        '%clr(%5p) ' + // Log level
                        '%clr(---){faint} %clr([%15.15t]){faint} ' + // Thread
                        '%clr(%-40.40logger:%line{39}){cyan} %clr(:){faint} ' + // Logger
                        '%m%n%wex' // Message
    }
    //指定日志生成格式，文件名以日期命名，生成每日日志文件，如果超出大小则另起文件存放
    //%d{yyyy-MM-dd}-日期，%i-用于记录每日日志个数
    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = "${HOME_DIR}/logs/auto/%d{yyyy-MM-dd}_DEBUG_%i.log"
        maxHistory = 30 //日志最长保留30天
        timeBasedFileNamingAndTriggeringPolicy(SizeAndTimeBasedFNATP) {
            maxFileSize = "10MB" //单个日志文件最大为10MB
        }
    }

    append = true
}
//为false表示只打印到本log的appender中，不打印父类
//logger("console",INFO,['INFO'],false)

//将指定级别的日志输出到日志文件中
//日志级别从高到地低 OFF 、 FATAL 、 ERROR 、 WARN 、 INFO 、 DEBUG 、 TRACE 、 ALL
root(INFO, ['STDOUT', 'INFO', 'ERROR', 'DEBUG'])
