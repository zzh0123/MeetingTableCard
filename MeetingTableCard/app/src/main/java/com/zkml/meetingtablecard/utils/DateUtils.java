package com.zkml.meetingtablecard.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.zkml.meetingtablecard.R;
import com.zkml.meetingtablecard.utils.cache.StringUtils;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {
    private static final String TAG = "DateUtils";

    //将Date类型转化为适当的String类型
    @SuppressLint("SimpleDateFormat")
    public static String getStringFromDate(Date date) {
        SimpleDateFormat year    = new SimpleDateFormat("yyyy");
        Date curDate = new Date(System.currentTimeMillis());
        String yyyy    = year.format(curDate);
        SimpleDateFormat sdf;
        if (year.format(date).contains(yyyy)) {
            sdf = new SimpleDateFormat("MM月dd日 HH:mm");
        } else {
            sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        }
        return sdf.format(date);
    }

    /**
     * @param time    date 类型的时间
     * @param format  需要格式化的格式
     * @param 。return string日期
     *                根据格式样式格式化日期时间
     */
    public static String getHourMiniteStringFromDate(Date time, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(time);
    }

    /**
     * 判断当前时间是否比传入的时间小1小时
     */
    public static boolean ableDeleteOrder(String time) {
        Date date        = getDateFromString2(time);
        long timeUse;
        long currentTime = System.currentTimeMillis();
        if (date == null) {
            date = new Date(currentTime);
        }
        timeUse = date.getTime();
        return (timeUse - currentTime) >= 3600000;
    }

    public static Date getDateFromString(String strDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date date = sdf.parse(strDate); //将字符型转换成日期型
            Log.e("lyyo", "date: " + date);
            return date;
        } catch (Exception e) {
            Log.e("lyyo", "e: " + e);
            e.printStackTrace();
        }
        return null;   //返回毫秒数
    }

    //
    public static String getStringFromString(String strDate) {
        String returnStr = "";
        SimpleDateFormat sdf       = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date date = sdf.parse(strDate); //将字符型转换成日期型
            Log.e("lyyo", "date: " + date);
            returnStr = sdf.format(date);
        } catch (Exception e) {
            Log.e("lyyo", "e: " + e);
            e.printStackTrace();
        }
        return returnStr;
    }

    public static String getStringFromString1(String strDate) {
        String returnStr = "";
        SimpleDateFormat sdf       = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf.parse(strDate); //将字符型转换成日期型
            Log.e("lyyo", "date: " + date);
            returnStr = sdf.format(date);
        } catch (Exception e) {
            Log.e("lyyo", "e: " + e);
            e.printStackTrace();
        }
        return returnStr;
    }
    public static String getStringFromString2(String strDate) {
        String returnStr = "";
        String tempStr = "";
        SimpleDateFormat sdf       = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf.parse(strDate); //将字符型转换成日期型
            Log.e("lyyo", "date: " + date);
            tempStr = sdf.format(date);
            returnStr = tempStr.replaceAll("-","/");
        } catch (Exception e) {
            Log.e("lyyo", "e: " + e);
            e.printStackTrace();
        }
        return returnStr;
    }

    /**
     * 格式化分钟，
     *
     * @param startTime 开始时间 endTime 结束时间
     * @return 返回多少天多少小时等
     * @author zlx
     */
    public static String formatMinute(String startTime, String endTime) {

        long mills = getInterval(startTime, endTime);

        int minute = (int) (mills / (60 * 1000));

        String content = null;
        if (minute < 60) {
            content = minute + "分钟";
        } else if (minute >= 60 && minute < 24 * 60) {
            int h = minute / 60;
            int m = minute % 60;
            if (m == 0) {
                content = h + "小时";
            } else {
                content = h + "小时" + m + "分钟";
            }

        } else {
            int d = minute / (24 * 60);
            minute = minute % (24 * 60);
            if (minute == 0) {
                return d + "天";
            }
            int h = minute / 60;
            int m = minute % 60;
            if (h == 0) {
                return d + "天";
            }
            if (m == 0) {
                return d + "天" + h + "小时";
            }
            content = d + "天" + h + "小时" + m + "分钟";
        }
        return content;
    }

    //
    public static Date getDateFromString2(String strDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date date = sdf.parse(strDate); //将字符型转换成日期型
            Log.i("lyyo", "date: " + date);
            return date;
        } catch (Exception e) {
            Log.e("lyyo", "e: " + e);
        }
        return null;   //返回毫秒数
    }

    /**
     * 根据开始时间与时差计算结束时间
     *
     * @param starTime 开始时间
     * @return 返回时间差
     */
    public static String getTimeByStartTime(Context context, String starTime, String times) {
        String timeString = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date parse = dateFormat.parse(starTime);

            long diffStartTime = parse.getTime();
            long diffTimes = 0;
            if (!StringUtils.isStrEmpty(times)) {
                int day = times.indexOf(context.getString(R.string.daystr));
                int xiaoshi = times.indexOf(context.getString(R.string.xiaoshi));
                if (day != -1) {
                    String substringDay = times.substring(0, day);
                    long   hourStr      = 0;
                    if (xiaoshi != -1 && xiaoshi >= day + 1) {
                        String substringXiaoShi = times.substring(day + 1, xiaoshi) + "";
                        hourStr = (long) (Double.parseDouble(substringXiaoShi) * (60 * 60 * 1000));
                    }
                    long dayStr = (long) (Double.parseDouble(substringDay) * (24 * 60 * 60 * 1000));
                    diffTimes = dayStr + hourStr;
                } else if (xiaoshi != -1){
                    long   hourStr          = 0;
                    String substringXiaoShi = times.substring(0, xiaoshi);
                    hourStr = (long) (Double.parseDouble(substringXiaoShi) * (60 * 60 * 1000));
                    diffTimes =  hourStr;
                }
            }
            timeString = DateUtils.transferLongToDate("yyyy-MM-dd HH:mm:ss", diffStartTime + diffTimes);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return timeString;
    }


    public static String getCurrentTime(String format) {
        Date date        = new Date();
        SimpleDateFormat sdf         = new SimpleDateFormat(format, Locale.getDefault());
        String currentTime = sdf.format(date);
        return currentTime;
    }

    public static String getCurrentTime() {
        return getCurrentTime("yyyy-MM-dd  HH:mm:ss");
    }
    public static String getCurrentTime1() {
        return getCurrentTime("yyyy-MM-dd HH:mm");
    }
    public static String formatTime(String time) {
        Date d  = null;
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            d = sd.parse(time);
            long   rightTime = (long) d.getTime();
            String newtime   = sd.format(rightTime);
            return newtime;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static long fromDateStringToLong(String inVal) { //此方法计算时间秒
        Date date        = null;   //定义时间类型
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = inputFormat.parse(inVal); //将字符型转换成日期型
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (date == null) {
            return 0;
        }
        return date.getTime();   //返回毫秒数
    }

    //获取时间间隔相差的秒数
    public static long getInterval(String starttime, String endtime) {
        Log.e("gac", "starttime:" + starttime + " endtime:" + endtime);
        long start = formatDateStringToLong(starttime);
        long end = formatDateStringToLong(endtime);
        return end - start;
    }

    public static long formatDateStringToLong(String strTime) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar.getTimeInMillis();
    }

    public static String getCurrentDate() {
        return getCurrentTime("yyyy-MM-dd");
    }

    public static String getFormatFromLong1(String strDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TimeZone utc = TimeZone.getTimeZone("UTC+8");
        sdf.setTimeZone(utc);
        if (!TextUtils.isEmpty(strDate) && !"null".equals(strDate)) {
            Date date = new Date(Long.parseLong(strDate));
            return sdf.format(date);
        }
        return "";
    }

    public static String getFormatFromLong2(String strDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            TimeZone utc = TimeZone.getTimeZone("UTC+8");
            sdf.setTimeZone(utc);
            if (!TextUtils.isEmpty(strDate) && !"null".equals(strDate)) {
                Date date = new Date(Long.parseLong(strDate));
                return sdf.format(date);
            }
        } catch (Exception e) {
            Log.e(TAG, "getFormatFromLong2: ", e);
        }
        return "";
    }

    @SuppressLint("SimpleDateFormat")
    public static String getFormatFromLong(String strDate) {
        if (!TextUtils.isEmpty(strDate) && !"null".equals(strDate)) {
            SimpleDateFormat sdf      = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
            Date date     = getDateFromString2(strDate);
            String nowyear  = getCurrentTime("yyyy");
            String showdate = sdf.format(date);
            if (showdate.contains(nowyear)) {
                sdf = new SimpleDateFormat("MM月dd日 EEE HH:mm");
            } else {
                sdf = new SimpleDateFormat("yyyy年MM月dd日 EEE HH:mm");
            }
            return sdf.format(date);
        }
        return "";
    }
    @SuppressLint("SimpleDateFormat")
    public static String getFormatFromLong7(String strDate) {
        if (!TextUtils.isEmpty(strDate) && !"null".equals(strDate)) {
            SimpleDateFormat sdf  = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
            Date date = getDateFromString2(strDate);
            sdf =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.format(date);
        }
        return "";
    }
    @SuppressLint("SimpleDateFormat")
    public static String getFormatFromLong3(String strDate) {
        if (!TextUtils.isEmpty(strDate) && !"null".equals(strDate)) {
            SimpleDateFormat sdf      = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
            Date date     = getDateFromString2(strDate);
            String nowyear  = getCurrentTime("yyyy");
            String showdate = sdf.format(date);
            if (showdate.contains(nowyear)) {
                sdf = new SimpleDateFormat("MM月dd日 HH:mm");
            } else {
                sdf = new SimpleDateFormat("MM月dd日 HH:mm");
//                sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
            }
            return sdf.format(date);
        }
        return "";
    }

    @SuppressLint("SimpleDateFormat")
    public static String getFormatFromLong4(String strDate) {
        try {
            if (!TextUtils.isEmpty(strDate) && !"null".equals(strDate)) {
                SimpleDateFormat sdf      = new SimpleDateFormat("yyyy年MM月dd日");
                Date date     = getDateFromString3(strDate);
                String nowyear  = getCurrentTime("yyyy");
                String showdate = sdf.format(date);
                if (showdate.contains(nowyear)) {
                    sdf = new SimpleDateFormat("MM月dd日");
                } else {
                    sdf = new SimpleDateFormat("yyyy年MM月dd日");
                }
                return sdf.format(date);
            }
            return "";
        } catch (Exception e) {
            Log.e(TAG, "getFormatFromLong4: ", e);
        }
        return "";
    }

    @SuppressLint("SimpleDateFormat")
    public static String getFormatFromLong5(String strDate) {
        if (!TextUtils.isEmpty(strDate) && !"null".equals(strDate)) {
            SimpleDateFormat sdf  = new SimpleDateFormat("yyyy年MM月");
            Date date = getDateFromString2(strDate);
            return sdf.format(date);
        }
        return "";
    }
    @SuppressLint("SimpleDateFormat")
    public static String getFormatFromLong6(String strDate) {
        try {
            if (!TextUtils.isEmpty(strDate) && !"null".equals(strDate)) {
                SimpleDateFormat sdf      = new SimpleDateFormat("yyyy年MM月dd日");
                Date date     = getDateFromString3(strDate);
                String nowyear  = getCurrentTime("yyyy");
                String showdate = sdf.format(date);
                sdf = new SimpleDateFormat("yyyy年MM月dd日");
                return sdf.format(date);
            }
            return "";
        } catch (Exception e) {
            Log.e(TAG, "getFormatFromLong4: ", e);
        }
        return "";
    }
    @SuppressLint("SimpleDateFormat")
    public static String getFormatMonthAndDay(String strDate) {
        if (!TextUtils.isEmpty(strDate) && !"null".equals(strDate)) {
            SimpleDateFormat sdf  = new SimpleDateFormat("MM月-dd日");
            Date date = getDateFromString2(strDate);
            return sdf.format(date);
        }
        return "";
    }

    @SuppressLint("SimpleDateFormat")
    public static String getFormatMonthAndDay2(String strDate) {
        if (!TextUtils.isEmpty(strDate) && !"null".equals(strDate)) {
            SimpleDateFormat sdf  = new SimpleDateFormat("MM月dd日");
            Date date = getDateFromString2(strDate);
            return sdf.format(date);
        }
        return "";
    }

    @SuppressLint("SimpleDateFormat")
    public static String getFormatTime(String strDate) {
        if (!TextUtils.isEmpty(strDate) && !"null".equals(strDate)) {
            SimpleDateFormat sdf  = new SimpleDateFormat("EEE HH:mm");
            Date date = getDateFromString2(strDate);
            return sdf.format(date);
        }
        return "";
    }

    @SuppressLint("SimpleDateFormat")
    public static String getFormatTimeNoWeek(String strDate) {
        if (!TextUtils.isEmpty(strDate) && !"null".equals(strDate)) {
            SimpleDateFormat sdf  = new SimpleDateFormat("HH:mm");
            Date date = getDateFromString2(strDate);
            return sdf.format(date);
        }
        return "";
    }

    @SuppressLint("SimpleDateFormat")
    public static String getTimeFormat(String format, Date date) {
        try {
            SimpleDateFormat f = new SimpleDateFormat(format);
            return f.format(date);
        } catch (Exception e) {
            Log.e(TAG, "getTimeFormat: ", e);
        }
        return null;
    }

    public static String formatLongToTimeStr(Long l) {
        int    minute     = 0;
        int    second     = 0;
        String str_minute = "00";
        String str_second = "00";
        second = l.intValue();
        if (second > 60) {
            minute = second / 60;         //取整
            second = second % 60;         //取余
        }

        if (minute >= 0 && minute < 10) {
            str_minute = "0" + minute;
        } else {
            str_minute = minute + "";
        }
        if (second >= 0 && second < 10) {
            str_second = "0" + second;
        } else {
            str_second = second + "";
        }

        String strtime = str_minute + " : " + str_second;
        return strtime;

    }

    public static String formatLongToTimeStr2(Long l) {
        int    hour       = 0;
        int    minute     = 0;
        int    second     = 0;
        String str_hour   = "00";
        String str_minute = "00";
        String str_second = "00";
        second = l.intValue();
        if (second > 60) {
            minute = second / 60;         //取整
            second = second % 60;         //取余
        }
        if (minute > 60) {
            hour = minute / 60;
            minute = minute % 60;
        }
        if (hour >= 0 && hour < 10) {
            str_hour = "0" + hour;
        } else {
            str_hour = minute + "";
        }
        if (minute >= 0 && minute < 10) {
            str_minute = "0" + minute;
        } else {
            str_minute = minute + "";
        }
        if (second >= 0 && second < 10) {
            str_second = "0" + second;
        } else {
            str_second = second + "";
        }

        return str_hour + " : " + str_minute + " : " + str_second;

    }

    @SuppressLint("SimpleDateFormat")
    public static Date getDateFromString3(String strDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf.parse(strDate); //将字符型转换成日期型
            Log.e("lyyo", "date: " + date);
            return date;
        } catch (Exception e) {
            Log.e("lyyo", "e: " + e);
            e.printStackTrace();
        }
        return null;   //返回毫秒数
    }

    @SuppressLint("SimpleDateFormat")
    public static Date getDateFromString5(String strDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date date = sdf.parse(strDate); //将字符型转换成日期型
            Log.e("lyyo", "date: " + date);
            return date;
        } catch (Exception e) {
            Log.e("lyyo", "e: " + e);
            e.printStackTrace();
        }
        return null;   //返回毫秒数
    }

    @SuppressLint("SimpleDateFormat")
    public static Date getDateFromString4(String strDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        try {
            Date date = sdf.parse(strDate); //将字符型转换成日期型
            Log.e("lyyo", "date: " + date);
            return date;
        } catch (Exception e) {
            Log.e("lyyo", "e: " + e);
            e.printStackTrace();
        }
        return null;   //返回毫秒数
    }

    //判断两个时间的先后顺序，同时两个时间都不能早于当前时间
    @SuppressLint("SimpleDateFormat")
    public static boolean afterToday2(String firstdate, String seconddate) {
        boolean          flag       = false;
        SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date firstDate  = mFormatter.parse(firstdate);
            Date secondDate = mFormatter.parse(seconddate);
            if (firstDate.after(secondDate)) {
                flag = false;
            } else {
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    //判断两个时间的先后顺序
    @SuppressLint("SimpleDateFormat")
    public static boolean afterTodayTwoTime(String firstdate, String seconddate) {
        boolean          flag       = false;
        SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date firstDate  = mFormatter.parse(firstdate);
            Date secondDate = mFormatter.parse(seconddate);
            if (firstDate.after(secondDate)) {
                flag = false;
            } else {
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }
    //判断两个时间的先后顺序
    @SuppressLint("SimpleDateFormat")
    public static boolean afterTodayTwoTime1(String firstdate, String seconddate) {
        boolean          flag       = false;
        SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date firstDate  = mFormatter.parse(firstdate);
            Date secondDate = mFormatter.parse(seconddate);
            if (firstDate.after(secondDate)||firstDate.equals(secondDate)) {
                flag = false;
            } else {
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }
    /**
     * 两个时间之间相差距离多少天
     *
     * @param str1 时间参数 1：
     * @param str2 时间参数 2：
     * @return 相差天数
     */
    @SuppressLint("SimpleDateFormat")
    public static long getDistanceDays(String str1, String str2) {
        SimpleDateFormat df   = new SimpleDateFormat("yyyy-MM-dd");
        Date one;
        Date two;
        long             days = 0;
        try {
            one = df.parse(str1);
            two = df.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff;
            if (time1 < time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            days = diff / (1000 * 60 * 60 * 24);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return days;
    }

    /**
     * 计算时间差
     *
     * @param starTime 开始时间
     * @param endTime  结束时间
     * @return 返回时间差
     */
    public static String getTimeDifference(Context context, String starTime, String endTime) {
        String timeString = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date parse  = dateFormat.parse(starTime);
            Date parse1 = dateFormat.parse(endTime);

            long diff = parse1.getTime() - parse.getTime();

            long day = diff / (24 * 60 * 60 * 1000);
            long hour = (diff / (60 * 60 * 1000) - day * 24);


            //            timeString = day + "天" + hour + "小时";xiaoshi
            if (hour == 0) {
                timeString = day + context.getString(R.string.daystr);
            } else {
                timeString = day + context.getString(R.string.daystr) + hour + context.getString(R.string.xiaoshi);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeString;
    }

    /**
     * 计算时间差
     *
     * @param starTime 开始时间
     * @param endTime  结束时间
     * @return 返回时间差
     */
    public static String getTimeDifference3(Context context, String starTime, String endTime) {
        String timeString = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date parse   = dateFormat.parse(starTime);
            Date parse1  = dateFormat.parse(endTime);
            double diff    = parse1.getTime() - parse.getTime();
            double allHour = diff / (60 * 60 * 1000);
            if (allHour >= 24) {
                timeString = BaiduAddress.getDecimels(allHour / 24) + "\n" + context.getString(R.string.daystr);
            } else {
                timeString = BaiduAddress.getDecimels(allHour) + "\n" + context.getString(R.string.xiaoshi);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeString;
    }

    /**
     * 计算时间差
     *
     * @param starTime 开始时间
     * @param endTime  结束时间
     * @return 返回时间差
     */
    public static String getTimeDifference4(Context context, String starTime, String endTime) {
        String timeString = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date parse   = dateFormat.parse(starTime);
            Date parse1  = dateFormat.parse(endTime);
            double diff    = parse1.getTime() - parse.getTime();
            double allHour = diff / (60 * 60 * 1000);
            if (allHour >= 24) {
                timeString = BaiduAddress.getDecimels(allHour / 24) + context.getString(R.string.daystr);
            } else {
                timeString = BaiduAddress.getDecimels(allHour) + context.getString(R.string.xiaoshi);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeString;
    }

    /**
     * 计算时间差
     *
     * @param starTime 开始时间
     * @param endTime  结束时间
     * @return 返回时间差
     */
    public static String getTimeDifference2(Context context, String starTime, String endTime) {
        String timeString = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date parse  = dateFormat.parse(starTime);
            Date parse1 = dateFormat.parse(endTime);

            long diff = parse1.getTime() - parse.getTime();

            long day = diff / (24 * 60 * 60 * 1000);
            long hour = (diff / (60 * 60 * 1000) - day * 24);
            long minute = (diff / (60 * 1000) - day * 24 * 60 - hour * 60);
            long second = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - minute * 60);

            //            timeString = day + "天" + hour + "小时";xiaoshi
        /*    if (minute == 0) {
                timeString = second + context.getString(R.string.second);
            } else if (hour == 0) {
                timeString = minute + context.getString(R.string.minute)
                        + second + context.getString(R.string.second);
            } else if (day == 0) {
                timeString = hour + context.getString(R.string.xiaoshi)
                        + minute + context.getString(R.string.minute)
                        + second + context.getString(R.string.second);
            } else {
                timeString = day + context.getString(R.string.daystr)
                        + hour + context.getString(R.string.xiaoshi)
                        + minute + context.getString(R.string.minute)
                        + second + context.getString(R.string.second);
            }*/

            if (day == 0) {
                if (hour == 0) {
                    if (minute == 0) {
                        //分钟为0，直接显示天数和小时
                        if (second == 0) {
                            timeString = 0 + context.getString(R.string.daystr);
                        } else {
                            //秒数不为0
                            timeString = second + context.getString(R.string.second);
                        }
                    } else {
                        //分钟不为0
                        if (second == 0) {
                            timeString = minute + context.getString(R.string.minute);
                        } else {
                            //秒数不为0
                            timeString = minute + context.getString(R.string.minute)
                                    + second + context.getString(R.string.second);
                        }
                    }
                } else {
                    //小时不为0
                    if (minute == 0) {
                        //分钟为0，直接显示天数和小时
                        timeString = hour + context.getString(R.string.xiaoshi);
                    } else {
                        //分钟不为0
                        if (second == 0) {
                            timeString = hour + context.getString(R.string.xiaoshi)
                                    + minute + context.getString(R.string.minute);
                        } else {
                            //秒数不为0
                            timeString = hour + context.getString(R.string.xiaoshi)
                                    + minute + context.getString(R.string.minute)
                                    + second + context.getString(R.string.second);
                        }
                    }
                }
            } else {
                //天数不为0
                if (hour == 0) {
                    if (minute == 0) {
                        //分钟为0，直接显示天数和小时
                        if (second == 0) {
                            timeString = day + context.getString(R.string.daystr);
                        } else {
                            //秒数不为0
                            timeString = day + context.getString(R.string.daystr)+ second + context.getString(R.string.second);
                        }
                    } else {
                        //分钟不为0
                        if (second == 0) {
                            timeString = day + context.getString(R.string.daystr)
                                    + minute + context.getString(R.string.minute);
                        } else {
                            //秒数不为0
                            timeString = day + context.getString(R.string.daystr)
                                    + minute + context.getString(R.string.minute)
                                    + second + context.getString(R.string.second);
                        }
                    }
                } else {
                    //小时不为0
                    if (minute == 0) {
                        //分钟为0，直接显示天数和小时
                        timeString = day + context.getString(R.string.daystr)
                                + hour + context.getString(R.string.xiaoshi);
                    } else {
                        //分钟不为0
                        if (second == 0) {
                            timeString = day + context.getString(R.string.daystr)
                                    + hour + context.getString(R.string.xiaoshi)
                                    + minute + context.getString(R.string.minute);
                        } else {
                            //秒数不为0
                            timeString = day + context.getString(R.string.daystr)
                                    + hour + context.getString(R.string.xiaoshi)
                                    + minute + context.getString(R.string.minute)
                                    + second + context.getString(R.string.second);
                        }
                    }
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeString;
    }

    /**
     * 计算时间差
     *
     * @param starTime 开始时间
     * @param endTime  结束时间
     * @return 返回小时数
     */
    public static String getHourDifference(Context context, String starTime, String endTime) {
        String timeString = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        try {
            Date parse  = dateFormat.parse(starTime);
            Date parse1 = dateFormat.parse(endTime);

            double diff = parse1.getTime() - parse.getTime();

            double hour = (diff / (60 * 60 * 1000));
            timeString = new DecimalFormat("0.0").format(hour);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeString;
    }


    //判断是不是x分钟之后
    public static boolean afterNow(Date date2, int delayMinutes, Activity mContext) {
        SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar calendar   = Calendar.getInstance();
        Date date1      = calendar.getTime();
        String tr         = mFormatter.format(date1.getTime() + 1000 * 60 * delayMinutes);
        try {
            Date d = mFormatter.parse(tr);
            if (d.after(date2)) {
                //x分钟之内
                return false;
            }

        } catch (ParseException e) {
            e.printStackTrace();
            return true;
        }
        //x分钟之后
        return true;
    }

    // 格式化时间
    public static String formatDurationForHMS(long duration) {
        // 360000 360
        long second = duration / 1000;
        long minute = second / 60;
        long hours = minute / 60;
        second = second % 60;
        minute = minute % 60;

        return (hours < 10 ? "0" + hours : hours) + ":"
                + (minute < 10 ? "0" + minute : minute) + ":"
                + (second < 10 ? "0" + second : second);

    }

    public static String transferLongToDate(String dateFormat, Long millSec) {
        SimpleDateFormat sdf  = new SimpleDateFormat(dateFormat);
        Date date = new Date(millSec);
        return sdf.format(date);
    }



    /**
     * 时长 ：秒
     */
    public static String transferHourAndMinChinese(Context context, int duration) {
        String str;
        int    day = 0;
        int    hour = 0;
        int    min = 0;
        if (duration > 3600) {
            hour = (int) Math.floor(duration / 3600);
            min = (int) Math.floor((duration - hour * 3600) / 60);

        } else if (duration > 60) {
            min = (int) Math.floor(duration / 60);
        }
        if (hour == 0) {
            if (min == 0) {
                str = 1 + context.getString(R.string.minute);
            } else {
                str = min + context.getString(R.string.minute);
            }
        } else {
            if (hour >= 24) {
                day = (int) Math.floor(hour / 24);
                hour = (int) Math.floor(hour - day * 24);
                if (hour == 0) {
                    str = day + context.getString(R.string.daystr) + min + context.getString(R.string.minute);
                } else {
                    str = day + context.getString(R.string.daystr) + hour + context.getString(R.string.xiaoshi) + min + context.getString(R.string.minute);
                }

            } else {
                str = hour + context.getString(R.string.xiaoshi) + min + context.getString(R.string.minute);
            }
        }
        return str;
    }

    public static String getTodayDate() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date        = sDateFormat.format(new Date());
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        Date date1 = null;
//        try {
//            date1 = sdf.parse(date);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        return date;
    }

    /**
     * 获得指定日期的前一天
     *
     * @param specifiedDay
     * @return
     * @throws Exception
     */
    public static String getSpecifiedDayBefore(String specifiedDay) {
        Calendar c    = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat("yy-MM-dd").parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day - 1);

        String dayBefore = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
        return dayBefore;
    }

    /**
     * 获取过去或者未来 任意天内的日期数组     * @param intervals      intervals天内     * @return    &nbs
     * /**
     * 获取过去或者未来 任意天内的日期数组
     *
     * @param intervals intervals天内
     * @return 日期数组
     */
    public static ArrayList<String> getFetureDayList(int intervals) {
        //  ArrayList<String> pastDaysList = new ArrayList<>();
        ArrayList<String> fetureDaysList = new ArrayList<>();
        for (int i = 0; i < intervals; i++) {
            // pastDaysList.add(getPastDate(i));
            fetureDaysList.add(getFetureDate(i));
        }
        return fetureDaysList;
    }

    /**
     * 获取过去第几天的日期
     *
     * @param past
     * @return
     */
    public static String getPastDate(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
        Date today  = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String result = format.format(today);
        return result;
    }
    /**
     * 获取过去第几天的日期
     *
     * @param past
     * @return
     */
    public static String getPastDate1(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
        Date today  = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
        String result = format.format(today);
        return result;
    }
    /**
     * 获取未来 第 past 天的日期
     *
     * @param past
     * @return
     */
    public static String getFetureDate(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + past);
        Date today  = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String result = format.format(today);
        return result;
    }

    /**
     * 日期转星期
     *
     * @param datetime
     * @return
     */
    public static String dateToWeek(String datetime) {
        SimpleDateFormat f        = new SimpleDateFormat("yyyy-MM-dd");
        String[]         weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal      = Calendar.getInstance(); // 获得一个日历
        Date datet    = null;
        try {
            datet = f.parse(datetime);
            cal.setTime(datet);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1; // 指示一个星期中的某天。
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    /**
     * 日期转星期
     *
     * @param datetime
     * @return
     */
    public static String dateToWeek1(String datetime) {
        SimpleDateFormat f        = new SimpleDateFormat("yyyy-MM-dd");
        String[]         weekDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar cal      = Calendar.getInstance(); // 获得一个日历
        Date datet    = null;
        try {
            datet = f.parse(datetime);
            cal.setTime(datet);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1; // 指示一个星期中的某天。
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    public static String addtime(String timeStr, String addnumber) {
        String str = null;
        try {
            DateFormat df   = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = df.parse(timeStr);
            //时间累计
            Calendar gc = new GregorianCalendar();
            gc.setTime(date);
            gc.add(GregorianCalendar.MINUTE, Integer.parseInt(addnumber));
            str = df.format(gc.getTime());
        } catch (Exception e) {
        }
        return str;
    }


    public static String addSeconds(String date, int seconds) throws Exception {
        int              min      = seconds / 60;
        SimpleDateFormat sdf      = new SimpleDateFormat("yyyy-MM-dd hh:mm");//格式化
        Date inDate   = sdf.parse(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(inDate);
        calendar.add(Calendar.MINUTE, min);

        Date outDate = calendar.getTime();
        String outTime = dateToStrLong(outDate);
        return outTime;
    }

    /**
     * * 将长时间格式时间转换为字符串 yyyy-MM-dd HH:mm
     * * @param dateDate
     * * @return
     */
    public static String dateToStrLong(Date dateDate) {
        SimpleDateFormat formatter  = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dateString = formatter.format(dateDate);
        return dateString;
    }

    //将long 转化为需要类型的String
    public static String longToStr(Long time , String formatStr) {
        SimpleDateFormat formatter = new SimpleDateFormat(formatStr);
        return formatter.format(time);
    }

    /**
     * 日期字符串转日期
     * @param strDate 时间字符串
     * @param format 时间格式
     * @return date
     */
    public static Date getDateFromString(String strDate, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            Date date = sdf.parse(strDate); //将字符型转换成日期型
            return date;
        } catch (Exception e) {
        }
        return null;   //返回毫秒数
    }
    //获取当前时间毫秒数
    public static String getCurrentTimeMillis(){
        //获取当前的毫秒值
        long time = System.currentTimeMillis();
        //将毫秒值转换为String类型数据
        return String.valueOf(time);
    }
    public static String getStringFromLongTime(Long longTime) {
        String returnStr = "";
        SimpleDateFormat sdf       = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = new Date(longTime);
            returnStr = sdf.format(date);
        } catch (Exception e) {
            Log.e("lyyo", "e: " + e);
            e.printStackTrace();
        }
        return returnStr;
    }


    /**
     * 根据开始时间和结束时间计算时间差值
     * @param context
     * @param starTime
     * @param endTime
     * @return
     */
    public static String getTimeDifferenceByStartAndEndTime(Context context, String starTime, String endTime) {
        String timeString = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        try {
            Date parse  = dateFormat.parse(starTime);
            Date parse1 = dateFormat.parse(endTime);

            long diff = parse1.getTime() - parse.getTime();

            long day = diff / (24 * 60 * 60 * 1000);
            long hour = (diff / (60 * 60 * 1000) - day * 24);
            long minute = (diff / (60 * 1000) - day * 24 * 60 - hour * 60);
            if (day == 0) {
                if (hour == 0) {
                    if (minute == 0) {
                        //分钟为0，直接显示天数和小时
                        timeString = 0 + context.getString(R.string.minute);
                    } else {
                        //分钟不为0
                        timeString = minute + context.getString(R.string.minute);
                    }
                } else {
                    //小时不为0
                    if (minute == 0) {
                        //分钟为0，直接显示天数和小时
                        timeString = hour + context.getString(R.string.xiaoshi);
                    } else {
                        //分钟不为0
                        timeString = hour + context.getString(R.string.xiaoshi)
                                + minute + context.getString(R.string.minute);
                    }
                }
            } else {
                //天数不为0
                if (hour == 0) {
                    if (minute == 0) {
                        //分钟为0，直接显示天数和小时
                        timeString = day + context.getString(R.string.daystr);
                    } else {
                        //分钟不为0
                        timeString = day + context.getString(R.string.daystr)
                                + minute + context.getString(R.string.minute);
                    }
                } else {
                    //小时不为0
                    if (minute == 0) {
                        //分钟为0，直接显示天数和小时
                        timeString = day + context.getString(R.string.daystr)
                                + hour + context.getString(R.string.xiaoshi);
                    } else {
                        //分钟不为0
                        timeString = day + context.getString(R.string.daystr)
                                + hour + context.getString(R.string.xiaoshi)
                                + minute + context.getString(R.string.minute);
                    }
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeString;
    }

    public static String getMonthDay(String dateTime) {
        String monthDay = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = format.parse(dateTime);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            monthDay = "" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return monthDay;
    }

    public static int getDayofWeek(String dateTime) {

        Calendar cal = Calendar.getInstance();
        if (dateTime.equals("")) {
            cal.setTime(new Date(System.currentTimeMillis()));
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date;
            try {
                date = sdf.parse(dateTime);
            } catch (ParseException e) {
                date = null;
                e.printStackTrace();
            }
            if (date != null) {
                cal.setTime(new Date(date.getTime()));
            }
        }
        return cal.get(Calendar.DAY_OF_WEEK);
    }


    public static String getWeek(String dateTime) {
        String week = "";
        switch (getDayofWeek(dateTime)) {
            case 1:
                week = "周日";
                break;
            case 2:
                week = "周一";
                break;
            case 3:
                week = "周二";
                break;
            case 4:
                week = "周三";
                break;
            case 5:
                week = "周四";
                break;
            case 6:
                week = "周五";
                break;
            case 7:
                week = "周六";
                break;
        }
        return week;
    }
}
