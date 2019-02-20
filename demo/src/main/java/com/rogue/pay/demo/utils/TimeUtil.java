package com.rogue.pay.demo.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author weigaosheng
 * @description
 * @CalssName TimeUtil
 * @date 2019/2/20
 * @params
 * @return
 */
public class TimeUtil {
    public static final String DEFAULT_TIME_FORMAT_DB = "yyyyMMddHHmmss";

    private static final long ONE_MINUTE = 60000L;
    private static final long ONE_HOUR = 3600000L;
    private static final long ONE_DAY = 86400000L;
    private static final long ONE_WEEK = 604800000L;

    private static final String ONE_SECOND_AGO = "秒前";
    private static final String ONE_MINUTE_AGO = "分钟前";
    private static final String ONE_HOUR_AGO = "小时前";
    private static final String ONE_DAY_AGO = "天前";
    private static final String ONE_MONTH_AGO = "月前";
    private static final String ONE_YEAR_AGO = "年前";




    /**
     * 将时间串 yyyyMMddHHmmss转换成yyyy-MM-dd HH:mm:ss
     * @param dateStr
     * @return
     */
    public static String formatDateByStr(String dateStr){
        if(dateStr.length() != 14){
            return null;
        }
        return dateStr.substring(0,4)+"-"+dateStr.substring(4,6)+"-" +dateStr.substring(6,8)+" "
                +dateStr.substring(8,10)+":"+dateStr.substring(10,12)+":"+dateStr.substring(12,14);
    }

    /**
     * 将毫秒值时间转换成yyyyMMddHHmmss字符串形式时间
     * @param mimsd
     * @return
     */
    public static String getStrFromMillis(String mimsd){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return dateFormat.format(Long.valueOf(mimsd));
    }

    /**
     * 将时间串 yyyyMMddHHmmss转换成yyyy-MM-dd
     * @param dateStr
     * @return
     */
    public static String formatDateByStrNew(String dateStr){
        if(dateStr.length() != 14){
            return null;
        }
        return dateStr.substring(0,4)+"-"+dateStr.substring(4,6)+"-" +dateStr.substring(6,8);
    }

    /**
     * 将时间串 yyyyMMddHHmmss转换成HH:mm:ss
     * @param dateStr
     * @return
     */
    public static String formatTimeByStr(String dateStr){
        if(dateStr.length() != 14){
            return null;
        }
        return dateStr.substring(8,10)+":"+dateStr.substring(10,12)+":"+dateStr.substring(12,14);
    }

    /**
     * 得到指定日期相差几月的日期字符串:yyyymmdd（正数+，负数-）
     */
    public static String getMouth(String data, int num) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date dt = sdf.parse(data);
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(dt);
        rightNow.add(Calendar.MONTH, num);
        Date dt1 = rightNow.getTime();
        data = sdf.format(dt1);
        return data;
    }

    /**
     * 得到当前日期字符串:yyyymmdd
     */
    public static String getCurrentDateString() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;
        int date = calendar.get(Calendar.DATE);
        return "" + calendar.get(Calendar.YEAR)
                + (month < 10 ? "0" + month : "" + month)
                + (date < 10 ? "0" + date : "" + date);
    }
    /**
     * 更具传进来的格式返回当前时间字符串
     */
    public static String getCurrentDateString(String formart) {
        if(formart == null){
            return getCurrentDateString();
        }
        SimpleDateFormat format = new SimpleDateFormat(formart);
        Date date = new Date();
        return format.format(date);
    }
    /**
     * 取得若干天前/后的系统日期   取得若干天前/后的系统日期
     */
    public static String getDifferentDate(int days) {
        return getDifferentTime(24 * days);
    }

    /**
     * 取得指定小时单位间隔后的系统时间
     */
    public static String getDifferentTime(int hour) {
//        GregorianCalendar calendar = (GregorianCalendar) Calendar.getInstance();
//        calendar.add(Calendar.HOUR, hour);
//        calendar.add(Calendar.MINUTE,minutes);
//        SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_TIME_FORMAT_DB);
//        return formatter.format(calendar.getTime());
        return getDifferentTimeByMinutes(60 * hour);
    }

    /**
     * 取得指定分钟单位间隔后的系统时间
     */

    public static String getDifferentTimeByMinutes(int minutes) {
        GregorianCalendar calendar = (GregorianCalendar) Calendar.getInstance();
        calendar.add(Calendar.MINUTE,minutes);
        SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_TIME_FORMAT_DB);
        return formatter.format(calendar.getTime());
    }
    /**
     * 得到指定日期相差几月的日期字符串:yyyymmdd（正数+，负数-）
     */
    public static String getDay(String data, int num) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date dt = sdf.parse(data);
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(dt);
        rightNow.add(Calendar.DAY_OF_MONTH, num);
        Date dt1 = rightNow.getTime();
        data = sdf.format(dt1);
        return data;
    }

    /**
     * 时间处理yyyy-mm-dd转yyyymmmdd
     *
     * @Author lianyt
     * @Date Create in  2018/1/17 21:09
     * @Modifined by
     **/
    public static String formDate(String data) throws ParseException {
        String time = "";
        if ("".equals(data)) {
            time = TimeUtil.getCurrentDateString();
        } else {
            String[] date = data.split("-");
            for (int i = 0; i < date.length; i++) {
                time = time + date[i];
            }
        }
        return time;
    }

    /**
     * 获取nextDay
     * @param date
     * @return
     */
    public static Date getNextDate(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);
        date = calendar.getTime();
        return date;
    }
    /**
     *时间串转成时间 YYYY-MM-DD HH:MM:ss
     */
    public static Date getDate(String dateStr){
        String format = "yyyy-MM-dd hh:mm:ss";
        Date date  = null;
        DateFormat dateFormat = new SimpleDateFormat(format);
        try {
            date = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 时间转成字符串
     * @param date
     * @return
     */
    public static String getDateStr(Date date){
        String time = "";
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        time=formatter.format(date);
        return time;
    }
    /**
     * 二月份天数
     */
    public static int getTwoDay(String yearTime){
        int twoDay = 0;
        yearTime = yearTime.substring(0,4);
        int year = Integer.valueOf(yearTime);
        if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
            twoDay = 29;
        } else {
            twoDay = 28;
        }
        return twoDay;
    }


    /**
     *时间串转成时间 YYYY-MM-DD
     */
    public static Date getDateLikeYMD(String dateStr){
        String format = "yyyy-MM-dd";
        Date date  = null;
        DateFormat dateFormat = new SimpleDateFormat(format);
        try {
            date = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * @return a
     * @Author weigaosheng
     * @Description 判断是多少时间以前，参数格式：date类型2018-12-29 14:35:35
     * @Date 16:19 2018/12/2
     * @Param
     **/
    public static String formatBefore(Date date) {
        long delta = new Date().getTime() - date.getTime();
        if (delta < 1L * ONE_MINUTE) {
            long seconds = toSeconds(delta);
            return (seconds <= 0 ? 1 : seconds) + ONE_SECOND_AGO;
        }
        if (delta < 45L * ONE_MINUTE) {
            long minutes = toMinutes(delta);
            return (minutes <= 0 ? 1 : minutes) + ONE_MINUTE_AGO;
        }
        if (delta < 24L * ONE_HOUR) {
            long hours = toHours(delta);
            return (hours <= 0 ? 1 : hours) + ONE_HOUR_AGO;
        }
        if (delta < 48L * ONE_HOUR) {
            return "昨天";
        }
        if (delta < 30L * ONE_DAY) {
            long days = toDays(delta);
            return (days <= 0 ? 1 : days) + ONE_DAY_AGO;
        }
        if (delta < 12L * 4L * ONE_WEEK) {
            long months = toMonths(delta);
            return (months <= 0 ? 1 : months) + ONE_MONTH_AGO;
        } else {
            long years = toYears(delta);
            return (years <= 0 ? 1 : years) + ONE_YEAR_AGO;
        }
    }

    public static String formatBefore(String  dateStr) {
        return formatBefore(getDate(formatDateByStr(dateStr)));
    }
    private static long toSeconds(long date) {
        return date / 1000L;
    }

    private static long toMinutes(long date) {
        return toSeconds(date) / 60L;
    }

    private static long toHours(long date) {
        return toMinutes(date) / 60L;
    }

    private static long toDays(long date) {
        return toHours(date) / 24L;
    }

    private static long toMonths(long date) {
        return toDays(date) / 30L;
    }

    private static long toYears(long date) {
        return toMonths(date) / 365L;
    }
}
