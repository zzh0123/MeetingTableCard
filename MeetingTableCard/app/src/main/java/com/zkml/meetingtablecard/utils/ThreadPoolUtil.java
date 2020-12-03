package com.zkml.meetingtablecard.utils;

import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by ccMagic on 2018/7/23.
 * Copyright ：
 * Version ：
 * Reference ：
 * Description ：线程池获取工具类
 * !!!!!!!需要使用的线程池记得先到Application里进行初始化操作
 *
 * 一个任务通过 execute(Runnable)方法被添加到线程池，任务就是一个 Runnable类型的对象，任务的执行方法就是 Runnable类型对象的run()方法。
 * <p>
 * 当一个任务通过execute(Runnable)方法欲添加到线程池时：
 * <p>
 * 如果此时线程池中的数量小于corePoolSize，即使线程池中的线程都处于空闲状态，也要创建新的线程来处理被添加的任务。
 * 如果此时线程池中的数量等于 corePoolSize，但是缓冲队列 workQueue未满，那么任务被放入缓冲队列。
 * 如果此时线程池中的数量大于corePoolSize，缓冲队列workQueue满，并且线程池中的数量小于maximumPoolSize，建新的线程来处理被添加的任务。
 * 如果此时线程池中的数量大于corePoolSize，缓冲队列workQueue满，并且线程池中的数量等于maximumPoolSize，那么通过 handler所指定的策略来处理此任务。
 * <p>
 * 也就是：处理任务的优先级为：
 * 核心线程corePoolSize、任务队列workQueue、最大线程maximumPoolSize，如果三者都满了，使用handler处理被拒绝的任务。
 */
public class ThreadPoolUtil {

    private static final String TAG = "ThreadPoolUtil";
    public static ExecutorService mSingleExecutorService;
    public static ExecutorService mCachedThreadPool;
    public static ExecutorService mFixedThreadPool;
    public static ScheduledExecutorService mScheduledExecutorService;
    public static ScheduledExecutorService mSingleScheduledThreadPool;
    private static ThreadPoolUtil           mThreadPoolUtil;
    //线程池正在创建，线程池创建比较耗时
    private boolean mSingleThreadExecutorCreating = false;
    private boolean mNewCachedThreadPoolCreating = false;
    private boolean mFixedThreadPoolCreating = false;
    private boolean mScheduledThreadPoolCreating = false;
    private boolean mSingleScheduledThreadPoolCreating = false;

    private ThreadPoolUtil() {
    }

    public static ThreadPoolUtil getInstance() {
        if (mThreadPoolUtil == null) {
            mThreadPoolUtil = new ThreadPoolUtil();
        }
        return mThreadPoolUtil;
    }

    /**
     * 获取一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。
     * 线程池为无限大，当执行第二个任务时第一个任务已经完成，会复用执行第一个任务的线程，而不用每次新建线程
     */
    public void createCachedThreadPool() {
        if (!mNewCachedThreadPoolCreating) {
            mNewCachedThreadPoolCreating = true;
            if (mCachedThreadPool == null) {
                mCachedThreadPool = Executors.newCachedThreadPool();
                try {
                    mCachedThreadPool.awaitTermination(20, TimeUnit.SECONDS);
                } catch (Exception e) {
                    Log.e(TAG, "getSingleThreadExecutor: ", e);
                }
            }
        }
    }

    /**
     * 获取一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待。
     * 定长线程池的大小最好根据系统资源进行设置。如Runtime.getRuntime().availableProcessors()。
     */
    public void getFixedThreadPool() {
        if (!mFixedThreadPoolCreating) {
            mFixedThreadPoolCreating = true;
            if (mFixedThreadPool == null) {
                mFixedThreadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
                try {
                    mFixedThreadPool.awaitTermination(20, TimeUnit.SECONDS);
                } catch (Exception e) {
                    Log.e(TAG, "getSingleThreadExecutor: ", e);
                }
            }
        }
    }

    /**
     * 创建一个定长线程池，支持定时及周期性任务执行
     */
    public void getScheduledThreadPool() {
        if (!mScheduledThreadPoolCreating) {
            mScheduledThreadPoolCreating = true;
            if (mScheduledExecutorService == null) {
                mScheduledExecutorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
                try {
                    mScheduledExecutorService.awaitTermination(20, TimeUnit.SECONDS);
                } catch (Exception e) {
                    Log.e(TAG, "getSingleThreadExecutor: ", e);
                }
            }
        }
    }
    /**
     * 创建一个定长线程池，支持定时及周期性任务执行
     */
    public void getSingleScheduledThreadPool() {
        if (!mSingleScheduledThreadPoolCreating) {
            mSingleScheduledThreadPoolCreating = true;
            if (mSingleScheduledThreadPool == null) {
                mSingleScheduledThreadPool = Executors.newScheduledThreadPool(1);
                try {
                    mSingleScheduledThreadPool.awaitTermination(20, TimeUnit.SECONDS);
                } catch (Exception e) {
                    Log.e(TAG, "getSingleThreadExecutor: ", e);
                }
            }
        }
    }
    /**
     * 单例线程
     * 获取一个单线程化的线程池，
     * 它只会用唯一的工作线程来执行任务，
     * 保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行,
     * 相当于顺序执行各个任务。
     */
    public void createSingleThreadExecutor() {
        Log.i(TAG, "createSingleThreadExecutor mSingleThreadExecutorCreating: " + mSingleThreadExecutorCreating);
        if (!mSingleThreadExecutorCreating) {
            mSingleThreadExecutorCreating = true;
            if (mSingleExecutorService == null) {
                mSingleExecutorService = Executors.newSingleThreadExecutor();
                try {
                    mSingleExecutorService.awaitTermination(20, TimeUnit.SECONDS);
                } catch (Exception e) {
                    Log.e(TAG, "getSingleThreadExecutor: ", e);
                }
            }
        }
    }
}
