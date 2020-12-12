package com.java.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class CommonUtils {
    /**
     * 字符串首字母大写
     *
     * @param str
     * @return
     */
    public static String upperCaseFirst(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
    /**
     * 字符串首字母小写
     *
     * @param str
     * @return
     */
    public static String lowerCaseFirst(String str) {
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    /**
     * 产生[start,end)之间的随机整数
     * @param start
     * @param end
     * @return
     */
    public static int getRandom(int start, int end) {
        Random random = new Random();
        return random.nextInt(end - start + 1) + start;
    }

    /**
     * 日期转字符串
     * @param date
     * @param pattern
     * @return
     */
    public static String format(Date date,String pattern)  {
        if(pattern == null){
            pattern = "yyyy-MM-dd HH:mm:ss";
        }
        if(date == null){
            date = new Date();
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);



    }

    /**
     * 字符串转日期
     * @param date
     * @param pattern
     * @return
     * @throws ParseException
     */
    public static Date parse(String date,String pattern) throws ParseException {
        if(pattern == null){
            pattern = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf2 = new SimpleDateFormat(pattern);
        return sdf2.parse(date);
    }

}
