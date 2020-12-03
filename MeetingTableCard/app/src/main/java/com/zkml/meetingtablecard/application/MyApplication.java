package com.zkml.meetingtablecard.application;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.DisplayMetrics;

import com.zkml.meetingtablecard.constant.Constant;
import com.zkml.meetingtablecard.constant.IPConfig;
import com.zkml.meetingtablecard.utils.CrashCaughtException;
import com.zkml.meetingtablecard.utils.LogUtil;
import com.zkml.meetingtablecard.utils.PackageUtil;
import com.zkml.meetingtablecard.utils.ThreadPoolUtil;
import com.zkml.meetingtablecard.utils.httputils.HttpConnUtils;
import com.zkml.meetingtablecard.utils.httputils.cache.CacheMode;

/**
 * @author: Administrator
 * data : 2019/12/26
 * description：MyApplication
 */
public class MyApplication extends Application {

    public static final String USER_INFO_NAME = "user_info_meeting_table";
    public static int screenWidth = 0;
    public static int screenHeight = 0;

    private static MyApplication mAppApplication;
    private static Context context;
    public static boolean LOG_SWITCH = true;//日志开关 false 关闭 true 打开
    private static boolean flag = false;// false:测试网。true：公网
    private static boolean isProject = true;// false 不是智慧后勤， true：是智慧后勤

    public String sessionId;//登陆sesssionId

    //获取全局Context
    public static Context getContext() {
        return context;
    }

    public static String getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName
                    (), 0);
            return packInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取Application
     */
    public static MyApplication getApp() {
        return mAppApplication;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i("zzz1", "onCreate: ");
        mAppApplication = this;
        context = getApplicationContext();
        IPConfig.setFlag(flag);
        initHttps();
        getScreenSize();
        initException();
        initThreadPool();
        initPackageName(context);//得到app包名
    }

    /**
     * 初始化线程池
     */
    private void initThreadPool() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ThreadPoolUtil.getInstance().createSingleThreadExecutor();
            }
        }).start();
    }

    //初始化异常类
    private void initException() {
        CrashCaughtException mUncaughtException = CrashCaughtException.getInstance();
        mUncaughtException.init();
    }

    /**
     * 获取屏幕尺寸
     */
    private void getScreenSize() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenHeight = dm.heightPixels;
        screenWidth = dm.widthPixels;
    }

    private void initHttps() {
        //必须调用初始化
        HttpConnUtils.init(this);
        //以下都不是必须的，根据需要自行选择
        HttpConnUtils.getInstance()//
//                .debug("OkHttpUtils")                                              //是否打开调试
                .setConnectTimeout(HttpConnUtils.DEFAULT_MILLISECONDS)               //全局的连接超时时间
                .setReadTimeOut(HttpConnUtils.DEFAULT_MILLISECONDS)                  //全局的读取超时时间
                .setWriteTimeOut(HttpConnUtils.DEFAULT_MILLISECONDS)                 //全局的写入超时时间
                .setCacheMode(CacheMode.IF_NONE_CACHE_REQUEST);
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
//        NetStateReceiver.unRegisterNetworkStateReceiver(this);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private void initPackageName(Context context) {
        Constant.APP_PACKAGENAME = PackageUtil.getPackageName(context);
        LogUtil.iJsonFormat("APP_PACKAGENAME", Constant.APP_PACKAGENAME);
    }


}