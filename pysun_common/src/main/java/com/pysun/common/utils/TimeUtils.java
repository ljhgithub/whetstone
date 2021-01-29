package com.pysun.common.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {

    public static String getDateTimeString(long milliseconds, String format) {
        Date date = new Date(milliseconds);
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.getDefault());
        return formatter.format(date);
    }

    public static Long getDateTimeByString(String timeStr, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.getDefault());
        try {
            return formatter.parse(timeStr).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1L;
    }

    /**
     * 通过时间秒毫秒数判断两个时间的间隔
     * date2>date1 返回正数
     * @param date1
     * @param date2
     * @return
     */
    public static int differentDaysByMillisecond(long date1,long date2)
    {
        int days = (int) ((date2 - date1) / (1000*3600*24));
        return days;
    }

    public static String getWeek(int calendarWeek ) {

        String weekString;
        switch (calendarWeek) {
            case Calendar.SUNDAY:
                weekString = "日";
                break;
            case Calendar.MONDAY:
                weekString = "一";
                break;
            case Calendar.TUESDAY:
                weekString = "二";
                break;
            case Calendar.WEDNESDAY:
                weekString = "三";
                break;
            case Calendar.THURSDAY:
                weekString = "四";
                break;
            case Calendar.FRIDAY:
                weekString = "五";
                break;
            default:
                weekString = "六";
                break;

        }

        return weekString;
    }

    public static String timetoDate(long time, String format) {
        if (TextUtils.isEmpty(format)) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(time));
    }
}
