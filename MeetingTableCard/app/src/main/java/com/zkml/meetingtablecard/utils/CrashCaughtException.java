package com.zkml.meetingtablecard.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.zkml.meetingtablecard.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CrashCaughtException implements UncaughtExceptionHandler {
    private final static String TAG      = "UncaughtException";
    private static final int    TIME_OUT = 50000;
    private static CrashCaughtException     mUncaughtException;
    private UncaughtExceptionHandler mDefaultHandler;
    private Context context;
    @SuppressLint("SimpleDateFormat")
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    // 用来存储设备信息和异常信息
    private Map<String, String> infos     = new HashMap<String, String>();

    private CrashCaughtException() {
    }

    /**
     * 同步方法，以免单例多线程环境下出现异常
     *
     * @return
     */
    public synchronized static CrashCaughtException getInstance() {
        if (mUncaughtException == null) {
            mUncaughtException = new CrashCaughtException();
        }
        return mUncaughtException;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * 初始化，把当前对象设置成UncaughtExceptionHandler处理器
     */
    public void init() {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(mUncaughtException);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        // 处理异常,我们还可以把异常信息写入文件，以供后来分析。
        // saveCrashInfo2File(ex);

        if (!handleException(ex) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(600);
            } catch (InterruptedException e) {
                Log.e(TAG, "error : ", e);
            }

            Intent intent = null;
            //先判断是公务还是租赁用户
            ActivityManager.getInstance().exit();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
        // System.exit(1);
    }

    /**
     * 自定义错误处理，收集错误信息，发送错误报告等操作均在此完成
     *
     * @param ex
     * @return true：如果处理了该异常信息；否则返回 false
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        // 使用 Toast 来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(context, R.string.appexception, Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();

        // 收集设备参数信息
        collectDeviceInfo(context);
        // 保存日志文件
        saveCrashInfo2File(ex);
        return true;
    }

    /**
     * 收集设备参数信息
     *
     * @param ctx
     */
    public void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);

            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (NameNotFoundException e) {
            Log.e(TAG, "an error occured when collect package info", e);
        }

        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
                Log.d(TAG, field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                Log.e(TAG, "an error occured when collect crash info", e);
            }
        }
    }


    /**
     * 保存错误信息到文件中 *
     *
     * @param ex
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    @SuppressLint("SdCardPath")
    private String saveCrashInfo2File(Throwable ex) {
        StringBuffer sb        = new StringBuffer();
        long         timestamp = System.currentTimeMillis();
        String time      = formatter.format(new Date());
        sb.append("\n" + time);
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key   = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }

        Writer writer      = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();

        String result = writer.toString();
        sb.append(result);
        try {
            String fileName = "exception.log";
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                String path = "/sdcard/careasycrash/";
                File dir  = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(path + fileName, true);
                fos.write(sb.toString().getBytes());
                fos.close();
            }

            return fileName;
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file...", e);
        }
        return null;
    }
}