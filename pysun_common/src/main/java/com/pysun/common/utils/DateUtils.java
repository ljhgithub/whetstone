package com.pysun.common.utils;

import androidx.annotation.StringDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 时间工具类
 */
public class DateUtils {

    private static final int YEAR = 365 * 24 * 60 * 60;// 年
    private static final int MONTH = 30 * 24 * 60 * 60;// 月
    private static final int DAY = 24 * 60 * 60;// 天
    private static final int HOUR = 60 * 60;// 小时
    private static final int MINUTE = 60;// 分钟

    /**
     * 获取当前日期的指定格式的字符串
     *
     * @param date_format 指定的日期时间格式，若为null或""则使用指定的格式"yyyy-MM-dd HH:MM"
     */

    public static String getCurrentTime(@DATE_FORMAT String date_format) {
        SimpleDateFormat sdf = new SimpleDateFormat();
        if (date_format == null || date_format.trim().equals("")) {
            sdf.applyPattern(date_format);
        } else {
            sdf.applyPattern(date_format);
        }
        return sdf.format(new Date());
    }


    public static String getDescriptionTimeFromTimestamp(long timestamp) {

        long currentTime = System.currentTimeMillis();
        // 与现在时间相差秒数
        long timeGap = (currentTime - timestamp) / 1000;
        System.out.println("timeGap: " + timeGap);
        String timeStr;
        if (timeGap > YEAR) {
            timeStr = timeGap / YEAR + "年前";
        } else if (timeGap > MONTH) {
            timeStr = timeGap / MONTH + "个月前";
        } else if (timeGap > DAY) {// 1天以上
            timeStr = timeGap / DAY + "天前";
        } else if (timeGap > HOUR) {// 1小时-24小时
            timeStr = timeGap / HOUR + "小时前";
        } else if (timeGap > MINUTE) {// 1分钟-59分钟
            timeStr = timeGap / MINUTE + "分钟前";
        } else {// 1秒钟-59秒钟
            timeStr = "刚刚";
        }
        return timeStr;
    }

    /**
     * @param timestamp
     * @return
     */
    public static String getDescriptionTime15(long timestamp) {

        long currentTime = System.currentTimeMillis();
        // 与现在时间相差秒数
        long timeGap = (currentTime - timestamp) / 1000;
        System.out.println("timeGap: " + timeGap);
        String timeStr;
        if (timeGap > YEAR) {
            timeStr = timeGap / YEAR + "年前";
        } else if (timeGap > MONTH) {
            timeStr = timeGap / MONTH + "个月前";
        } else if (timeGap > DAY * 7) {
            timeStr = timeGap / DAY + "天前";
        } else if (timeGap > DAY * 3) {// 1天以上
            timeStr = "近期";
        } else if (timeGap > DAY) {
            timeStr = timeGap / DAY + "天前";
        } else if (timeGap > HOUR) {
            timeStr = timeGap / HOUR + "小时前";
        } else if (timeGap > MINUTE * 15) {
            timeStr = "刚刚";
        } else if (timeGap > MINUTE) {
            timeStr = timeGap / MINUTE + "分钟前";
        } else {
            timeStr = timeGap % MINUTE + "秒前";
        }
        return timeStr;
    }


    public static String long2String(long timeStamp, @DATE_FORMAT String format) {
        return date2String(long2Date(timeStamp, format), format);
    }

    public static String date2String(Date date, @DATE_FORMAT String format) {
        return new SimpleDateFormat(format, Locale.CHINA).format(date);
    }

    public static Date long2Date(long timeStamp, @DATE_FORMAT String format) {
        // 根据long类型的毫秒数生命一个date类型的时间
        Date dateOld = new Date(timeStamp);
        // 把date类型的时间转换为string
        String sDateTime = date2String(dateOld, format);
        // 把String类型转换为Date类型
        return string2Date(sDateTime, format);
    }

    public static Date string2Date(String time, @DATE_FORMAT String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = formatter.parse(time);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date;
    }

    public static long string2Long(String strTime, @DATE_FORMAT String format) {
        // String类型转成date类型
        Date date = string2Date(strTime, format);
        if (date == null) {
            return 0;
        } else {
            // date类型转成long类型
            return dateToLong(date);
        }
    }


    /**
     * date类型转换为long类型
     * date要转换的date类型的时间
     */
    public static long dateToLong(Date date) {
        return date.getTime();
    }


    @StringDef({DATE_FORMAT.DATE_DAY, DATE_FORMAT.DATE_MOTH, DATE_FORMAT.DATE_YEAR,
            DATE_FORMAT.DATE_SECONDS, DATE_FORMAT.DATE_MINUTE, DATE_FORMAT.DATE_HOUR, DATE_FORMAT.HOUR, DATE_FORMAT.DATE_MONTH_DAY_CH})
    @Target({ElementType.PARAMETER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DATE_FORMAT {
        String DATE_SECONDS = "yyyy/MM/dd HH:mm:ss";
        String DATE_MINUTE = "yyyy/MM/dd HH:mm";
        String DATE_HOUR = "yyyy/MM/dd HH";
        String DATE_DAY = "yyyy/MM/dd";
        String DATE_MOTH = "yyyy/MM";
        String DATE_YEAR = "yyyy";
        String DATE_MONTH_DAY_CH = "MM月dd日";
        String HOUR = "HH:mm";
    }

    /**
     * 根据时间获取当天的时间戳
     *
     * @param des 09:00
     * @return 时间戳
     */
    public static long getMillSecondsInDay(String des) {
        int h = 0, m = 0;
        String t[] = des.split(":");
        if (t.length > 1) {
            h = Integer.parseInt(t[0]);
            m = Integer.parseInt(t[1]);
        }
        return h * 3600 * 1000 + m * 60 * 1000;
    }

    /**
     * 当前时间距离一天结束的剩余毫秒数
     * @param currentDate
     * @return
     */
    public static Integer getRemainSecondsOneDay(Date currentDate) {
        Calendar midnight= Calendar.getInstance();

        midnight.setTime(currentDate);

        midnight.add(midnight.DAY_OF_MONTH,1);

        midnight.set(midnight.HOUR_OF_DAY,0);

        midnight.set(midnight.MINUTE,0);

        midnight.set(midnight.SECOND,0);

        midnight.set(midnight.MILLISECOND,0);

        Integer seconds=(int)((midnight.getTime().getTime()-currentDate.getTime()));

        return seconds;
    }

}
