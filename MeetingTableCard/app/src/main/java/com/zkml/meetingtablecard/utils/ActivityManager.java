package com.zkml.meetingtablecard.utils;

import android.app.Activity;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ActivityManager {
    private static ActivityManager instance;
    private ConcurrentLinkedQueue<Activity> activityList = new ConcurrentLinkedQueue<>();

    private ActivityManager() {
    }

    /**
     * 单例模式中获取唯一的MyApplication实例
     */
    public static ActivityManager getInstance() {
        if (null == instance) {
            instance = new ActivityManager();
        }
        return instance;
    }

    /**
     * 添加Activity到容器中
     */
    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    /**
     * 遍历所有Activity并finish
     */
    public void exit() {
        for (Activity activity : activityList) {
            activity.finish();
        }
        System.exit(0);
    }

    /**
     * 结束所有的activity,但是不exit
     */
    public void finishactivity() {
        for (Activity activity : activityList) {
            activity.finish();
        }
    }
    /**
     * 结束除指定类名的Activity
     */
    public void finishExceptActivity(Class<?> cls) {
        for (Activity activity : activityList) {
            if (!activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }
    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : activityList) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityList.remove(activity);
            activity.finish();
        }
    }
}
