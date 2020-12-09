package com.zkml.meetingtablecard.utils;

import android.text.TextUtils;

/**
 * Created by ccMagic on 2018/5/10.
 * Copyright ：
 * Version ：
 * Reference ：
 * Description ：
 */
public class MyTextUtil {

    /**
     * 判断字符，不为空且不能等于“null”
     * @return  本身或“”
     * */
    public static String transEmptyOrNullToEmpty(String str) {
        if (TextUtils.isEmpty(str) || "null".equals(str)||"[]".equals(str)||"<null>".equals(str)) {
            str = "";
        }
        return str;
    }
    /**
     * 判断字符，不为空且不能等于“null”
     * @return str 本身或“--”
     * */
    public static String transEmptyToPlaceholder(String str) {
        if (TextUtils.isEmpty(str) || "null".equals(str)||"[]".equals(str)) {
            str = "--";
        }
        return str;
    }
    /**
     * 空字符串的时候转换成0
     * @return str 本身或“--”
     * */
    public static String transEmptyToZero(String str) {
        if (TextUtils.isEmpty(str) || "null".equals(str)||"[]".equals(str)) {
            str = "0";
        }
        return str;
    }
}
