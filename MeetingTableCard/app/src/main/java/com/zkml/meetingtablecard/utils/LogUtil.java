package com.zkml.meetingtablecard.utils;

import android.text.TextUtils;
import android.util.Log;

import com.zkml.meetingtablecard.application.MyApplication;


/**
 * 日志工具类
 */
public class LogUtil {

    /**
     * Log工具，类似android.util.Log。
     * tag自动产生，格式: customTagPrefix:className.methodName(L:lineNumber),
     * customTagPrefix为空时只输出：className.methodName(L:lineNumber)。
     * Author: zlx
     * Date: 13-7-24
     * Time: 下午12:23
     */
    public static String customTagPrefix = "log";
    //日志开关
    private static boolean mIsDebug        = MyApplication.LOG_SWITCH;//true:开发模式；false 上线模式
   // private static boolean mIsDebug = true;
    private LogUtil() {
    }

    private static String generateTag() {
        StackTraceElement caller          = new Throwable().getStackTrace()[2];
        String tag             = "%s.%s(L:%d)";
        String callerClazzName = caller.getClassName();
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        tag = String.format(tag, callerClazzName, caller.getMethodName(), caller.getLineNumber());
        tag = TextUtils.isEmpty(customTagPrefix) ? tag : customTagPrefix + ":" + tag;
        return tag;
    }

    public static void d(String content) {
        String tag = generateTag();
        if (mIsDebug)
            Log.d(tag, content);
    }

    public static void d(String content, Throwable tr) {
        String tag = generateTag();
        if (mIsDebug)
            Log.d(tag, content, tr);
    }

    public static void e(String tag, String content, Throwable t) {
        if (mIsDebug) {
            Log.e(tag, content, t);
        }

    }

    public static void e(String tag, String content) {
        if (mIsDebug) {
            Log.e(tag, content);
        }

    }

    public static void e(String content) {
        String tag = generateTag();
        if (mIsDebug)
            Log.e(tag, content);
    }

    public static void e(String content, Throwable tr) {
        String tag = generateTag();
        if (mIsDebug)
            Log.e(tag, content, tr);
    }

    public static void i(String content) {
        String tag = generateTag();
        if (mIsDebug)
            Log.i(tag, content);
    }

    public static void i(String content, Throwable tr) {
        String tag = generateTag();
        if (mIsDebug)
            Log.i(tag, content, tr);
    }

    public static void v(String content) {
        String tag = generateTag();
        if (mIsDebug)
            Log.v(tag, content);
    }

    public static void v(String content, Throwable tr) {
        String tag = generateTag();
        if (mIsDebug)
            Log.v(tag, content, tr);
    }

    public static void w(String content) {
        String tag = generateTag();
        if (mIsDebug)
            Log.w(tag, content);
    }

    public static void w(String content, Throwable tr) {
        String tag = generateTag();
        if (mIsDebug)
            Log.w(tag, content, tr);
    }

    public static void w(Throwable tr) {
        String tag = generateTag();
        if (mIsDebug)
            Log.w(tag, tr);
    }


    public static void wtf(String content) {
        String tag = generateTag();
        if (mIsDebug)
            Log.wtf(tag, content);
    }

    public static void wtf(String content, Throwable tr) {
        String tag = generateTag();
        if (mIsDebug)
            Log.wtf(tag, content, tr);
    }

    public static void wtf(Throwable tr) {
        String tag = generateTag();
        if (mIsDebug)
            Log.wtf(tag, tr);
    }

    /**
     * 截断输出日志
     *
     * @param msg
     */
    public static void i(String tag, String msg) {
        if (tag == null || tag.length() == 0
                || msg == null || msg.length() == 0)
            return;
        int segmentSize = 3 * 1024;
        long length = msg.length();
        if (length <= segmentSize) {// 长度小于等于限制直接打印
            if (mIsDebug)
                Log.i(tag, msg);
        } else {
            while (msg.length() > segmentSize) {// 循环分段打印日志
                String logContent = msg.substring(0, segmentSize);
                msg = msg.replace(logContent, "");
                if (mIsDebug)
                    Log.i(tag, logContent);
            }
            if (mIsDebug)
                Log.i(tag, msg);// 打印剩余日志
        }
    }

    /**
     * Json格式化输出
     *
     * @param tag
     * @param message 内容
     */
    public static void iJsonFormat(String tag, String message) {
        if (!TextUtils.isEmpty(message)) {
            if (mIsDebug)
                LogUtil.i(tag, LogUtil.format(LogUtil.convertUnicode(message)));
        }
    }

    public static String convertUnicode(String ori) {
        char         aChar;
        int          len       = ori.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len; ) {
            aChar = ori.charAt(x++);
            if (aChar == '\\') {
                aChar = ori.charAt(x++);
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = ori.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed   \\uxxxx   encoding.");
                        }
                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';
                    else if (aChar == 'n')
                        aChar = '\n';
                    else if (aChar == 'f')
                        aChar = '\f';
                    outBuffer.append(aChar);
                }
            } else
                outBuffer.append(aChar);

        }
        return outBuffer.toString();
    }

    public static String format(String jsonStr) {
        int          level         = 0;
        StringBuffer jsonForMatStr = new StringBuffer();
        for (int i = 0; i < jsonStr.length(); i++) {
            char c = jsonStr.charAt(i);
            if (level > 0 && '\n' == jsonForMatStr.charAt(jsonForMatStr.length() - 1)) {
                jsonForMatStr.append(getLevelStr(level));
            }
            switch (c) {
                case '{':
                case '[':
                    jsonForMatStr.append(c + "\n");
                    level++;
                    break;
                case ',':
                    jsonForMatStr.append(c + "\n");
                    break;
                case '}':
                case ']':
                    jsonForMatStr.append("\n");
                    level--;
                    jsonForMatStr.append(getLevelStr(level));
                    jsonForMatStr.append(c);
                    break;
                default:
                    jsonForMatStr.append(c);
                    break;
            }
        }

        return jsonForMatStr.toString();

    }

    private static String getLevelStr(int level) {
        StringBuffer levelStr = new StringBuffer();
        for (int levelI = 0; levelI < level; levelI++) {
            levelStr.append("\t");
        }
        return levelStr.toString();
    }
}

