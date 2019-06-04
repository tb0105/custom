package com.rzq.custom.base.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils {


    public static long getOndate() {
        return System.currentTimeMillis();//当前时间毫秒数
    }

    public static long getOldOneDaydate() {
        return System.currentTimeMillis() - 24 * 60 * 60 * 1000;//昨天的这一时间的毫秒数
    }

    public static long getTodayStartdate() {
        long current = System.currentTimeMillis();//当前时间毫秒数
        return current / (1000 * 3600 * 24) * (1000 * 3600 * 24) - TimeZone.getDefault().getRawOffset();//今天零点零分零秒的毫秒数
    }

    public static long getTodayOverdate() {
        long current = System.currentTimeMillis();//当前时间毫秒数
        long zero = current / (1000 * 3600 * 24) * (1000 * 3600 * 24) - TimeZone.getDefault().getRawOffset();//今天零点零分零秒的毫秒数
        return zero + 24 * 60 * 60 * 1000 - 1;//今天23点59分59秒的毫秒数
    }

    public static Date stringToDate(String strTime, String formatType)
            throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        date = formatter.parse(strTime);
        return date;
    }

    public static long StringtoLong(String requestdt, String formatType) {
        Date date = null; // String类型转成date类型
        try {
            date = stringToDate(requestdt, formatType);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date == null) {
            return 0;
        } else {
            long currentTime = dateToLong(date); // date类型转成long类型
            return currentTime;
        }
    }

    public static long dateToLong(Date date) {
        return date.getTime();
    }

    public static String getdayTime(Date updatedt) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(updatedt);
        calendar.get(Calendar.YEAR);
        int mM = calendar.get(Calendar.MONTH);
        int dD = calendar.get(Calendar.DATE);
        calendar.get(Calendar.HOUR);
        int hH = calendar.get(Calendar.HOUR_OF_DAY);
        int mm = calendar.get(Calendar.MINUTE);
        calendar.get(Calendar.SECOND);
        calendar.get(Calendar.DAY_OF_WEEK);
        return mM + "-" + dD + " " + hH + ":" + mm;
    }

    public static String getyardayTime(Date updatedt) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(updatedt);
        int yY = calendar.get(Calendar.YEAR);
        int mM = calendar.get(Calendar.MONTH);
        int dD = calendar.get(Calendar.DATE);
        calendar.get(Calendar.HOUR);
        int hH = calendar.get(Calendar.HOUR_OF_DAY);
        int mm = calendar.get(Calendar.MINUTE);
        calendar.get(Calendar.SECOND);
        calendar.get(Calendar.DAY_OF_WEEK);
        return yY + "-" + mM + "-" + dD;
    }

    public static String getyardayHmTime(Date updatedt) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(updatedt);
        int yY = calendar.get(Calendar.YEAR);
        int mM = calendar.get(Calendar.MONTH);
        int dD = calendar.get(Calendar.DATE);
        calendar.get(Calendar.HOUR);
        int hH = calendar.get(Calendar.HOUR_OF_DAY);
        int mm = calendar.get(Calendar.MINUTE);
        calendar.get(Calendar.SECOND);
        calendar.get(Calendar.DAY_OF_WEEK);
        return yY + "-" + mM + "-" + dD + " " + hH + ":" + mm;
    }

    public static String getyarmmTime(Date sendtime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sendtime);
        int yY = calendar.get(Calendar.YEAR);
        int mM = calendar.get(Calendar.MONTH);
        int dD = calendar.get(Calendar.DATE);
        calendar.get(Calendar.HOUR);
        int hH = calendar.get(Calendar.HOUR_OF_DAY);
        int mm = calendar.get(Calendar.MINUTE);
        int sS = calendar.get(Calendar.SECOND);
        calendar.get(Calendar.DAY_OF_WEEK);
        return yY + "" + mM + "" + dD + "" + hH + "" + mm + ""+sS;
    }
}
