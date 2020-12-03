package com.zkml.meetingtablecard.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.NumberPicker;


import androidx.annotation.NonNull;

import com.zkml.meetingtablecard.constant.Constant;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class DateFormatManager {

    @SuppressLint("SimpleDateFormat")
    public static String getFormatDate(String strDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date date = sdf.parse(strDate);
            String formatDate = sdf.format(date);
            return formatDate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @SuppressLint("SimpleDateFormat")
    public static String getFormatFromDate(String strDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date date = sdf.parse(strDate);
            String formatDate = sdf.format(date);
            return formatDate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getCurrentTime(String format) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        String currentTime = sdf.format(date);
        return currentTime;
    }

    public static String getCurrentTime() {
        return getCurrentTime("yyyy-MM-dd  HH:mm:ss");
    }

    @SuppressLint("SimpleDateFormat")
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


    public static Date getDateFromString2(String strDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
    public static String getFormatFromLong(String strDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        TimeZone utc = TimeZone.getTimeZone("UTC+8");
        sdf.setTimeZone(utc);
        if (!TextUtils.isEmpty(strDate) && !"null".equals(strDate)) {
            Date date = new Date(Long.parseLong(strDate));
            return sdf.format(date);
        }
        return "";
    }


    @SuppressLint("SimpleDateFormat")
    public static String getFormatFromLong(long strDate, @NonNull String form) {
        SimpleDateFormat sdf = new SimpleDateFormat(form);
        TimeZone utc = TimeZone.getTimeZone("UTC+8");
        sdf.setTimeZone(utc);
        Date date = new Date();
        date.setTime(strDate);
        return sdf.format(date);
    }

    public static String formateDecimalNumber(double number) {
        int digits = 2;
        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMaximumFractionDigits(digits);
        return format.format(number);
    }

    public static String formateDecimalNumber2(double number) {
        DecimalFormat df = new DecimalFormat(".00");
        return df.format(number);
    }

    @SuppressLint("SimpleDateFormat")
    public static String getStringFromDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {

            String formatDate = sdf.format(date);
            return formatDate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 得到timePicker里面的android.widget.NumberPicker组件 （有两个android.widget.NumberPicker组件--hour，minute）
     *
     * @param viewGroup
     * @return
     */
    public static List<NumberPicker> findNumberPicker(ViewGroup viewGroup) {
        List<NumberPicker> npList = new ArrayList<NumberPicker>();
        View child = null;

        if (null != viewGroup) {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                child = viewGroup.getChildAt(i);
                if (child instanceof NumberPicker) {
                    npList.add((NumberPicker) child);
                } else if (child instanceof LinearLayout) {
                    List<NumberPicker> result = findNumberPicker((ViewGroup) child);
                    if (result.size() > 0) {
                        return result;
                    }
                }
            }
        }

        return npList;
    }

    /**
     * 查找timePicker里面的android.widget.NumberPicker组件 ，并对其进行时间间隔设置
     *
     * @param viewGroup TimePicker timePicker
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void setNumberPickerTextSize(ViewGroup viewGroup) {
        List<NumberPicker> npList = findNumberPicker(viewGroup);
        if (null != npList) {
            for (NumberPicker mMinuteSpinner : npList) {
//            	System.out.println("mMinuteSpinner.toString()="+mMinuteSpinner.toString());
                if (mMinuteSpinner.toString().contains("id/minute")) {//对分钟进行间隔设置
                    //android.widget.NumberPicker{42af7a60 VFED.... ......I. 0,0-0,0 #1020499 android:id/minute}
                    mMinuteSpinner.setMinValue(0);
                    mMinuteSpinner.setMaxValue(Constant.minuts.length - 1);
                    mMinuteSpinner.setDisplayedValues(Constant.minuts);  //分钟显示数组
                }
                //对小时进行间隔设置 使用 if(mMinuteSpinner.toString().contains("id/hour")){}即可
                //android.widget.NumberPicker{42af7a60 VFED.... ......I. 0,0-0,0 #1020499 android:id/hour}
            }
        }
    }

}

