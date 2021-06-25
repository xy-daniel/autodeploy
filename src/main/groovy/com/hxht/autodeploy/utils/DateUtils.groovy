package com.hxht.autodeploy.utils

import cn.hutool.core.date.DateUtil

import java.text.ParseException

/**
 * @Description: 日期工具类
 * @author: daniel
 * @date: 2021.05.14
 */
class DateUtils {
    /**
     * 获取指定月份第一天
     * @param year 指定年 2019 2018 2017..
     * @param month 指定月份 1 2 3 ..12
     * @return 第一天
     */
    static Date getMonthFirstDay(int year, int month) {
        Calendar calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year) //设置年份
        calendar.set(Calendar.MONTH, month - 1) //设置月份
        int firstDay = calendar.getActualMinimum(Calendar.DAY_OF_MONTH)  //获取某月最小天数
        calendar.set(Calendar.DAY_OF_MONTH, firstDay) //设置日历中月份的最小天数
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.getTime()
    }

    /**
     * 获取指定月份最后一天
     * @param year 指定年 2019 2018 2017..
     * @param month 指定月份 1 2 3 ..12
     * @return 最后一天
     */
    static Date getMonthLastDay(int year, int month) {
        Calendar calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year) //设置年份
        calendar.set(Calendar.MONTH, month - 1) //设置月份
        int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)  //获取某月最小天数
        calendar.set(Calendar.DAY_OF_MONTH, lastDay) //设置日历中月份的最大天数
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.getTime()
    }

    //时间字符串转换为日期格式
    static Date str2Date(String str) {
        Date date = null
        if (str) {
            try {
                //尝试以"yyyy-MM-dd HH:mm:ss"解析时间字符串
                date = DateUtil.parse(str, "yyyy-MM-dd HH:mm:ss")
            } catch (ignored) {
                date = DateUtil.parse(str, "yyyy-MM-dd")
            }
        }
        date
    }

    //月份填充为2位数
    static String handleMonth(int month) {
        month < 10 ? "0${month}" : "${month}"
    }

    static String dateToWeek(String datetime) {
        String[] weekDays = ["星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"]
        Calendar calendar = Calendar.getInstance() // 获得一个日历
        try {
            calendar.setTime(DateUtil.parse(datetime, "yyyy-MM-dd"))
        } catch (ParseException e) {
            e.printStackTrace()
        }
        int w = calendar.get(Calendar.DAY_OF_WEEK) - 1 // 指示一个星期中的某天。
        if (w < 0)
            w = 0
        return weekDays[w]
    }
}
