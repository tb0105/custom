package com.rzq.custom.base.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static String datelongToString(long datalong) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //设置格式
        return format.format(datalong);
    }

    public static long dateStringTolong(String datastring) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //设置格式
        //设置要读取的时间字符串格式
        Date date = null;
        try {
            date = format.parse(datastring);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //转换为Date类
        return date.getTime();
    }
}
