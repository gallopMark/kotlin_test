package com.haoyuinfo.library.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * 时间转换
 *
 * @author vendor
 */
object TimeUtil {
    /**
     * 时间格式：yyyy-MM-dd
     */
    val DATE_FORMAT = "yyyy年MM月dd日"

    val DATE_SLASH = "yyyy/MM/dd"

    val DATE_Y_M = "yyyy年MM月"

    val DATE_H_R = "yyyy-MM-dd"
    /**
     * 时间格式：yyyy-MM-dd HH:mm:ss
     */
    val TIME_FORMAT = "yyyy-MM-dd HH:mm:ss"

    /**
     * 时间格式：yyyy-MM-dd HH:mm
     */
    val TIME_FORMAT2 = "yyyy-MM-dd HH:mm"

    /**
     * 在之前
     */
    val TIME_BEFORE = 1

    /**
     * 在中间
     */
    val TIME_ING = 2

    /**
     * 在之后
     */
    val TIME_AFTER = 3

    /**
     * 异常
     */
    val TIME_ERROR = -1

    /**
     * string型时间转换
     *
     * @param timeFormat 时间格式
     * @param timestamp  时间
     * @return 刚刚  x分钟  小时前  ...
     */
    fun convertTime(timeFormat: String, timestamp: String): String? {
        val sdf = SimpleDateFormat(timeFormat, Locale.getDefault())
        try {
            return converTime(sdf.parse(timestamp).time)
        } catch (e: IllegalArgumentException) {
            return null
        } catch (e: ParseException) {
        }

        return timestamp
    }

    /**
     * string型时间转换
     *
     * @param timeFormat 时间格式
     * @param timestamp  时间
     * @return 刚刚  x分钟  小时前  ...
     */
    fun convertTime(timeFormat: String, timestamp: Long): String {
        val sdf = SimpleDateFormat(timeFormat, Locale.getDefault())
        try {
            val date = Date()
            date.time = timestamp
            return sdf.format(date)
        } catch (e: IllegalArgumentException) {
            return ""
        }

    }

    fun getSlashDate(time: Long): String? {
        var timeStr: String? = null
        try {
            val date = Date(time)
            timeStr = SimpleDateFormat(DATE_SLASH, Locale.getDefault()).format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return timeStr
    }

    /**
     * int型时间转换
     *
     * @param timestamp 时间
     * @return 刚刚  x分钟  一天内  ...
     */
    fun converTime(timestamp: Long): String {
        val timeStr: String

        val interval = (System.currentTimeMillis() - timestamp) / 1000
        if (interval <= 60) { //1分钟内 服务端的时间 可能和本地的有区别 所以小于0的 对于这个情况全部都显示刚刚
            timeStr = "刚刚"
        } else if (interval < 60 * 60) { // 1小时内
            timeStr = (if (interval / 60 == 0L) 1 else interval / 60).toString() + "分钟前"
        } else if (interval < 24 * 60 * 60) { // 一天内
            timeStr = (if (interval / 60 * 60 == 0L) 1 else interval / (60 * 60)).toString() + "小时前"
        } else if (interval < 30 * 24 * 60 * 60) { // 天前
            timeStr = (if (interval / 24 * 60 * 60 == 0L) 1 else interval / (24 * 60 * 60)).toString() + "天前"
        } else if (interval < 12 * 30 * 24 * 60 * 60) { // 月前
            timeStr = (if (interval / 30 * 24 * 60 * 60 == 0L) 1 else interval / (30 * 24 * 60 * 60)).toString() + "个月前"
        } else if (interval < 12 * 30 * 24 * 60 * 60) { // 年前
            timeStr = (if (interval / 12 * 30 * 24 * 60 * 60 == 0L) 1 else interval / (12 * 30 * 24 * 60 * 60)).toString() + "年前"
        } else {
            val date = Date()
            date.time = timestamp
            timeStr = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(date)
        }
        return timeStr
    }

    /**
     * int型时间转换 比较距离结束
     *
     * @param timestamp 时间
     * @return 刚刚  x分钟  一天后  ...
     */
    fun convertEndTime(timestamp: Long): String {
        val timeStr: String

        val interval = (timestamp - System.currentTimeMillis()) / 1000
        if (interval <= 60) { //1分钟内 服务端的时间 可能和本地的有区别 所以小于0的 对于这个情况全部都显示刚刚
            timeStr = "1分钟"
        } else if (interval < 60 * 60) { // 1小时内
            timeStr = (if (interval / 60 == 0L) 1 else interval / 60).toString() + "分钟"
        } else if (interval < 24 * 60 * 60) { // 一天内
            timeStr = (if (interval / 60 * 60 == 0L) 1 else interval / (60 * 60)).toString() + "小时"
        } else if (interval < 30 * 24 * 60 * 60) { // 天前
            timeStr = (if (interval / 24 * 60 * 60 == 0L) 1 else interval / (24 * 60 * 60)).toString() + "天"
        } else if (interval < 12 * 30 * 24 * 60 * 60) { // 月前
            timeStr = (if (interval / 30 * 24 * 60 * 60 == 0L) 1 else interval / (30 * 24 * 60 * 60)).toString() + "个月"
        } else if (interval < 12 * 30 * 24 * 60 * 60) { // 年前
            timeStr = (if (interval / 12 * 30 * 24 * 60 * 60 == 0L) 1 else interval / (12 * 30 * 24 * 60 * 60)).toString() + "年"
        } else {
            timeStr = getDateFormat(timestamp)
        }
        return timeStr
    }

    fun getDateFormat(timestamp: Long): String {
        var timeStr: String
        try {
            val date = Date()
            date.time = timestamp
            timeStr = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(date)
        } catch (e: Exception) {
            e.printStackTrace()
            timeStr = "时间格式未知"
        }

        return timeStr
    }

    fun getDateYM(timestamp: Long): String {
        var timeStr: String
        try {
            val date = Date()
            date.time = timestamp
            timeStr = SimpleDateFormat(DATE_Y_M, Locale.getDefault()).format(date)
        } catch (e: Exception) {
            e.printStackTrace()
            timeStr = "时间格式未知"
        }

        return timeStr
    }

    /**
     * 将long型时间转为固定格式的时间字符串
     *
     * @param longTime 时间
     * @return [TimeUtil.TIME_FORMAT]
     */
    fun convertToTime(longTime: Long): String {
        return convertToTime(TIME_FORMAT, longTime)
    }

    /**
     * 将long型时间转为固定格式的时间字符串
     *
     * @param timeformat 时间格式
     * @param longTime   时间
     * @return timeformat
     */
    fun convertToTime(timeformat: String, longTime: Long): String {
        val date = Date(longTime)
        return convertToTime(timeformat, date)
    }

    /**
     * 将Date型时间转为固定格式的时间字符串
     *
     * @param timeformat 时间格式
     * @param date       时间
     * @return timeformat
     */
    fun convertToTime(timeformat: String, date: Date): String {
        val sdf = SimpleDateFormat(timeformat, Locale.getDefault())
        return sdf.format(date)
    }

    /**
     * 将Calendar型时间转为固定格式的时间字符串
     *
     * @param timeformat 时间格式
     * @param calendar   时间
     * @return timeformat
     */
    fun convertToTime(timeformat: String, calendar: Calendar): String {
        val sdf = SimpleDateFormat(timeformat, Locale.getDefault())
        return sdf.format(calendar.time)
    }

    /**
     * 将long型时间转为固定格式的日期字符串
     *
     * @param longTime 时间
     * @return [TimeUtil.DATE_FORMAT]
     */
    fun convertToDate(longTime: Long): String {
        return convertToTime(DATE_FORMAT, longTime)
    }

    /**
     * 将String类型时间转为long类型时间
     *
     * @param timeFormat 解析格式
     * @param timestamp  yyyy-MM-dd HH:mm:ss
     * @return 时间
     */
    fun covertToLong(timeFormat: String, timestamp: String): Long {
        val sdf = SimpleDateFormat(timeFormat, Locale.getDefault())
        try {
            val date = sdf.parse(timestamp)
            return date.time
        } catch (e: ParseException) {
            return -1
        }

    }

    /**
     * long型时间转换
     *
     * @param longTime 长整型时间
     * @return 2013年7月3日 18:05(星期三)
     */
    fun convertDayOfWeek(longTime: Long): String {
        val format = "%d年%d月%d日 %s:%s(%s)"

        val c = Calendar.getInstance() // 日历实例
        c.time = Date(longTime)

        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val date = c.get(Calendar.DATE)
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val h = if (hour > 9) hour.toString() else "0$hour"
        val minute = c.get(Calendar.MINUTE)
        val m = if (minute > 9) minute.toString() else "0$minute"
        return String.format(Locale.getDefault(), format, year, month + 1, date, h, m, converToWeek(c.get(Calendar.DAY_OF_WEEK)))
    }

    /**
     * 转换数字的星期为字符串的
     *
     * @param w
     * @return 星期x
     */
    private fun converToWeek(w: Int): String? {
        var week: String? = null

        when (w) {
            1 -> week = "星期日"
            2 -> week = "星期一"
            3 -> week = "星期二"
            4 -> week = "星期三"
            5 -> week = "星期四"
            6 -> week = "星期五"
            7 -> week = "星期六"
        }

        return week
    }

    /**
     * 计算时间是否在区间内
     *
     * @param time  time
     * @param time1 time
     * @param time2 time
     * @return [TimeUtil.TIME_BEFORE][TimeUtil.TIME_ING][TimeUtil.TIME_AFTER]
     */
    fun betweenTime(time: Long, time1: Long, time2: Long): Int {
        var time1 = time1
        var time2 = time2
        if (time1 > time2) {  //时间1大
            val testTime = time1
            time1 = time2
            time2 = testTime
        }

        //已经过去
        return if (time1 > time) {
            TIME_BEFORE
        } else if (time2 < time) {
            TIME_AFTER
        } else {
            TIME_ING
        }
    }

    fun computeTimeDifference(time: String): String? {
        try {
            val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = df.parse(time)
            return computeTimeDifference(date.time)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return null
    }

    fun computeTimeDifference(time: Long): String {
        val l = time - System.currentTimeMillis()
        if (l <= 0) {
            return "" + 0 + "天" + 0 + "时" + 0 + "分"
        }

        val day = l / (24 * 60 * 60 * 1000)
        val hour = l / (60 * 60 * 1000) - day * 24
        val min = l / (60 * 1000) - day * 24 * 60 - hour * 60
        //        long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        return "" + day + "天" + hour + "时" + min + "分"
    }

    fun timeFormat(time: Long): String {
        val l = time - System.currentTimeMillis()
        if (l <= 0) {
            return "" + 0 + "天" + 0 + "小时"
        }

        val diffHour = l / (1000 * 60 * 60)
        val day = l / (1000 * 60 * 60 * 24)
        return if (diffHour < 24) {
            // 显示为小时
            "" + diffHour + "小时"
        } else {
            // 显示天
            "" + day + "天"
        }
    }

    fun getDate(time: Long): String {
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = Date(time)
            return sdf.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""
    }

    fun getTime(time: Long): String {
        try {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            val date = Date(time)
            return sdf.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""
    }

    /**
     * 得到几天后的时间
     *
     * @param d
     * @param day
     * @return
     */
    fun getDateAfter(d: Date, day: Int): Long {
        val now = Calendar.getInstance()
        now.time = d
        now.set(Calendar.DATE, now.get(Calendar.DATE) + day)
        return now.timeInMillis
    }

    fun getDateHR(timestamp: Long): String {
        var timeStr: String
        try {
            val date = Date()
            date.time = timestamp
            timeStr = SimpleDateFormat(DATE_H_R, Locale.getDefault()).format(date)
        } catch (e: Exception) {
            e.printStackTrace()
            timeStr = "时间格式未知"
        }

        return timeStr
    }

    fun getSurplusDay(createTime: Long): Int {
        var createTime = createTime
        createTime += (1000 * 60 * 60 * 24 * 60).toLong()
        val l = createTime - System.currentTimeMillis()
        return if (l <= 0) {
            0
        } else (l / (1000 * 60 * 60 * 24)).toInt()
    }

    fun getCCTotalDay(createTime: Long): Int {
        var createTime = createTime
        createTime += getDateAfter(Date(), 60)
        return (createTime / (1000 * 60 * 60 * 24)).toInt()
    }

    fun differentDays(date1: Date, date2: Date): Int {
        val date = Date()
        val cal1 = Calendar.getInstance()
        cal1.time = date1

        val cal2 = Calendar.getInstance()
        cal2.time = date2
        val day1 = cal1.get(Calendar.DAY_OF_YEAR)
        val day2 = cal2.get(Calendar.DAY_OF_YEAR)

        val year1 = cal1.get(Calendar.YEAR)
        val year2 = cal2.get(Calendar.YEAR)
        if (year1 != year2)
        //同一年
        {
            var timeDistance = 0
            for (i in year1 until year2) {
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0)
                //闰年
                {
                    timeDistance += 366
                } else
                //不是闰年
                {
                    timeDistance += 365
                }
            }

            return timeDistance + (day2 - day1)
        } else
        //不同年
        {
            return day2 - day1
        }
    }

    fun convertDayOfMinute(startTime: Long, endTime: Long): String {
        val format1 = "%d年%d月%d日 %s:%s"
        val format2 = "%d月%d日 %s:%s"
        val c1 = Calendar.getInstance() // 日历实例
        c1.time = Date(startTime)
        val c2 = Calendar.getInstance() // 日历实例
        c2.time = Date(endTime)
        val year1 = c1.get(Calendar.YEAR)
        val month1 = c1.get(Calendar.MONTH)
        val date1 = c1.get(Calendar.DATE)
        val hour1 = c1.get(Calendar.HOUR_OF_DAY)
        val h1 = if (hour1 > 9) hour1.toString() else "0$hour1"
        val minute1 = c1.get(Calendar.MINUTE)
        val m1 = if (minute1 > 9) minute1.toString() else "0$minute1"
        val year2 = c2.get(Calendar.YEAR)
        val month2 = c2.get(Calendar.MONTH)
        val date2 = c2.get(Calendar.DATE)
        val hour2 = c1.get(Calendar.HOUR_OF_DAY)
        val h2 = if (hour1 > 9) hour2.toString() else "0$hour2"
        val minute2 = c1.get(Calendar.MINUTE)
        val m2 = if (minute1 > 9) minute2.toString() else "0$minute2"
        return if (year1 == year2) {
            (String.format(Locale.getDefault(), format1, year1, month1 + 1, date1, h1, m1) + "至"
                    + String.format(Locale.getDefault(), format2, month2 + 1, date2, h2, m2))
        } else String.format(Locale.getDefault(), format1, year1, month1 + 1, date1, h1, m1) + "至" + String.format(Locale.getDefault(), format1, year2, month2 + 1, date2, h2, m2)
    }

    fun convertTimeOfDay(startTime: Long, endTime: Long): String {
        val format1 = "%d/%d/%d"
        val format2 = "%d/%d %s:%s"
        val c1 = Calendar.getInstance() // 日历实例
        c1.time = Date(startTime)
        val c2 = Calendar.getInstance() // 日历实例
        c2.time = Date(endTime)
        val year1 = c1.get(Calendar.YEAR)
        val month1 = c1.get(Calendar.MONTH)
        val date1 = c1.get(Calendar.DATE)
        val hour1 = c1.get(Calendar.HOUR_OF_DAY)
        val h1 = if (hour1 > 9) hour1.toString() else "0$hour1"
        val minute1 = c1.get(Calendar.MINUTE)
        val m1 = if (minute1 > 9) minute1.toString() else "0$minute1"
        val year2 = c2.get(Calendar.YEAR)
        val month2 = c2.get(Calendar.MONTH)
        val date2 = c2.get(Calendar.DATE)
        val hour2 = c1.get(Calendar.HOUR_OF_DAY)
        val h2 = if (hour1 > 9) hour2.toString() else "0$hour2"
        val minute2 = c1.get(Calendar.MINUTE)
        val m2 = if (minute1 > 9) minute2.toString() else "0$minute2"
        return if (year1 == year2) {
            (String.format(Locale.getDefault(), format1, year1, month1 + 1, date1, h1, m1) + "-"
                    + String.format(Locale.getDefault(), format2, month2 + 1, date2, h2, m2))
        } else String.format(Locale.getDefault(), format1, year1, month1 + 1, date1, h1, m1) + "至" + String.format(Locale.getDefault(), format1, year2, month2 + 1, date2, h2, m2)
    }

    fun convertDayOfMinute(time: Long): String {
        val format = "%d年%d月%d日 %s:%s"
        val c = Calendar.getInstance() // 日历实例
        c.time = Date(time)
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val date = c.get(Calendar.DATE)
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val h = if (hour > 9) hour.toString() else "0$hour"
        val minute = c.get(Calendar.MINUTE)
        val m1 = if (minute > 9) minute.toString() else "0$minute"
        return String.format(Locale.getDefault(), format, year, month + 1, date, h, m1)
    }

    fun getYear(longTime: Long): Int {
        val c = Calendar.getInstance() // 日历实例
        c.time = Date(longTime)
        return c.get(Calendar.YEAR)
    }

    /*
    * 将时间转换为时间戳
    */
    fun dateToLong(stamp: String, dateFormat: String): Long {
        try {
            val simpleDateFormat = SimpleDateFormat(dateFormat, Locale.getDefault())
            val date = simpleDateFormat.parse(stamp)
            return date.time
        } catch (e: ParseException) {
            e.printStackTrace()
            return System.currentTimeMillis()
        }

    }

    fun dateDiff(startTime: Long, endTime: Long): String {
        val actionText = StringBuilder()
        val nd = (1000 * 24 * 60 * 60).toLong()// 一天的毫秒数
        val nh = (1000 * 60 * 60).toLong()// 一小时的毫秒数
        val nm = (1000 * 60).toLong()// 一分钟的毫秒数
        val diff: Long
        val day: Long
        val hour: Long
        val min: Long
        // 获得两个时间的毫秒时间差异
        diff = endTime - startTime
        day = diff / nd// 计算差多少天
        hour = diff % nd / nh + day * 24// 计算差多少小时
        min = diff % nd % nh / nm + day * 24 * 60// 计算差多少分钟
        // 输出结果

        println("hour=$hour,min=$min")
        actionText.append("<font color='#181818'>"
                + "离活动结束还剩：" + " " + "</font>")
        actionText.append("<font color='#ff9900'>"
                + day + "</font>")
        actionText.append("<font color='#181818'>"
                + "天" + " " + "</font>")
        actionText.append("<font color='#ff9900'>"
                + (hour - day * 24) + "</font>")
        actionText.append("<font color='#181818'>"
                + "时" + " " + "</font>")
        actionText.append("<font color='#ff9900'>"
                + (min - day * 24 * 60) + "</font>")
        actionText.append("<font color='#181818'>"
                + "分" + " " + "</font>")

        return actionText.toString()
    }

    fun computeTimeDiff(minutes: Long): String {
        var timeStr = ""
        if (minutes <= 0) { //1分钟内 服务端的时间 可能和本地的有区别 所以小于0的 对于这个情况全部都显示刚刚
            timeStr = "1分钟"
        } else if (minutes < 60) { // 1小时内
            timeStr = minutes.toString() + "分钟"
        } else if (minutes < 24 * 60) { // 一天内
            timeStr = (if (minutes / 60 == 0L) 1 else minutes / 60).toString() + "小时"
        } else if (minutes < 30 * 24 * 60) { // 天前
            val day = minutes / 60 / 24
            val hour = (minutes - day * 24 * 60) / 60
            val min = minutes - day * 24 * 60 - hour * 60
            return "" + day + "天" + hour + "时" + min + "分"
        } else if (minutes < 12 * 30 * 24 * 60) { // 月前
            timeStr = (if (minutes / 30 * 24 * 60 == 0L) 1 else minutes / (30 * 24 * 60)).toString() + "个月"
        } else if (minutes < 12 * 30 * 24 * 60) { // 年前
            timeStr = (if (minutes / 12 * 30 * 24 * 60 == 0L) 1 else minutes / (12 * 30 * 24 * 60)).toString() + "年"
        }
        return timeStr
    }

    fun dateDiff(minutes: Long): String {
        val timeStr: String
        val actionText = StringBuilder()
        if (minutes <= 0) { //1分钟内 服务端的时间 可能和本地的有区别 所以小于0的 对于这个情况全部都显示刚刚
            timeStr = (minutes * 60).toString() + "秒"
        } else if (minutes > 0 && minutes < 60) { // 1小时内
            timeStr = minutes.toString() + "分钟"
        } else if (minutes > 60 && minutes < 24 * 60) { // 天前
            timeStr = (if (minutes / 60 == 0L) 1 else minutes / 60).toString() + "小时"
        } else if (minutes > 24 * 60 && minutes < 30 * 24 * 60) { // 月前
            val day = minutes / 60 / 24
            val hour = (minutes - day * 24 * 60) / 60
            val min = minutes - day * 24 * 60 - hour * 60
            // 输出结果
            actionText.append("<font color='#ff9900'>"
                    + day + "</font>")
            actionText.append("<font color='#181818'>"
                    + "天" + " " + "</font>")
            actionText.append("<font color='#ff9900'>"
                    + hour + "</font>")
            actionText.append("<font color='#181818'>"
                    + "时" + " " + "</font>")
            actionText.append("<font color='#ff9900'>"
                    + min + "</font>")
            actionText.append("<font color='#181818'>"
                    + "分" + " " + "</font>")
            return actionText.toString()
        } else if (minutes > 30 * 24 * 60 && minutes < 12 * 30 * 24 * 60) { // 年前
            timeStr = (if (minutes / 30 * 24 * 60 == 0L) 1 else minutes / (30 * 24 * 60)).toString() + "个月"
        } else { // 年前
            timeStr = (if (minutes / 12 * 30 * 24 * 60 == 0L) 1 else minutes / (12 * 30 * 24 * 60)).toString() + "年"
        }
        return timeStr
    }
}