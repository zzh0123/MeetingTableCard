package com.zkml.meetingtablecard.utils.asynctask;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.zkml.meetingtablecard.R;
import com.zkml.meetingtablecard.activity.LoginActivity;
import com.zkml.meetingtablecard.application.MyApplication;
import com.zkml.meetingtablecard.constant.Constant;
import com.zkml.meetingtablecard.utils.ActivityUtils;
import com.zkml.meetingtablecard.utils.LogUtil;
import com.zkml.meetingtablecard.utils.NetworkDetector;
import com.zkml.meetingtablecard.utils.httputils.HttpConnUtils;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by lenovo on 2016/3/25.
 * //
 */
public class HttpPostAsyncTask extends AsyncTask<Object, Integer, Map<String, Object>> {

    protected static final String TAG = "careasydriver";
    private Dialog gifDialog;
    private AnimationDrawable anim;
    private View view;
    private Map<String, String> reqMap = null;
    private PostFormCompleteListener postFormCompleteListener;
    private int showDialog = 0;///0：显示dialog；1：显示摆钟view
    private int showContextdialog = 1;//0:显示Context的对话框，1：显示Activity的对话框--默认
    private boolean is_service = false;
    private WeakReference<Context> weakReference;
    private boolean net_flag;
    private String mReqUrl;
    private Dialog dialog;
    private int dialogRes = R.layout.car_easy_dialog_loading;
    public static final int DIALOG_TYPE_USUAL = 100;

    public HttpPostAsyncTask(Context context, View view) {
        // this.context = context;
        this.view = view;
        weakReference = new WeakReference<>(context);
    }

    public void setShowDialog(int showDialog) {
        this.showDialog = showDialog;
    }

    /**
     * 传入网络请求的dialog资源文件
     * @param dialogRes
     */
    public void setDialogRes(int dialogRes) {
        this.dialogRes = dialogRes;
    }

    public void showContextDialog(int showContextdialog) {
        this.showContextdialog = showContextdialog;
    }

    public void setService(boolean is_service) {
        this.is_service = is_service;
    }

    @Override
    protected void onPreExecute() {
        net_flag = NetworkDetector.isNetworkConnected(weakReference.get());
        if (net_flag) {
            Log.e("gac", "showDialog:" + showDialog);
            if (showDialog == 0) {//显示dialog
                View layout = View.inflate(weakReference.get(), dialogRes, null);
                dialog = ActivityUtils.showDialogByViewForRes((Activity) weakReference.get(), layout);
            } else if (showDialog == 1) {//显示摆钟view或者progressbar
                view.setVisibility(View.VISIBLE);
            } else if (showDialog == DIALOG_TYPE_USUAL) {//数值为100是为了区分，可能传入的的值为2
                View layout = View.inflate(weakReference.get(), dialogRes, null);
                dialog = ActivityUtils.showDialogByViewForRes((Activity) weakReference.get(), layout);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Map<String, Object> doInBackground(Object... params) {
        mReqUrl = (String) params[0];
        if (params.length >= 2) {
            reqMap = (Map<String, String>) params[1];
        }
        Map<String, Object> resultMap = null;
        Log.i(TAG, "doInBackground net_flag: " + net_flag);
        if (net_flag) {
            if (params.length >= 3) {
                String json = (String) params[2];
                resultMap = HttpConnUtils.commPostJsonReqToServer(mReqUrl, reqMap, weakReference.get(), json);
            } else {
                resultMap = HttpConnUtils.commPostReqToServer(mReqUrl, reqMap, weakReference.get());
            }
        }
        return resultMap;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected void onPostExecute(Map<String, Object> result) {
        if (Constant.LOGINURL.equals(mReqUrl)) {
            //防止登录操作网络异常时的未登录成功，islogin还是true，引起用户还能点击其他操作
            Map<String, String> addMap = new HashMap<>();
            addMap.put("islogin", "false");
            ActivityUtils.addSharedPreferencesData(weakReference.get(), addMap, MyApplication.USER_INFO_NAME);
        }
        if (showDialog == 0) {//不显示dialog
            //ActivityUtils.cancelDiaolg();
            if (dialog != null) {
                dialog.dismiss();
            }
        } else if (showDialog == 1) {//不显示摆钟view或者progressbar
            view.setVisibility(View.GONE);
        } else if (showDialog == DIALOG_TYPE_USUAL) {
            if (dialog != null) {
                dialog.dismiss();
            }
        }
        if (null == result) {   //未报错，网络请求延时
            if (!is_service) {
                String app_name_tip = ActivityUtils.getStringXmlValue(weakReference.get(), R.string.app_name_tip);
                String net_exception_tip = ActivityUtils.getStringXmlValue(weakReference.get(), R.string.net_exception_tip);
                String sure = ActivityUtils.getStringXmlValue(weakReference.get(), R.string.dialog_ok);
                ActivityUtils.toast((Activity) weakReference.get(), net_exception_tip);
                //BaseActivity by = new BaseActivity();
                //by.sessionOut(weakReference.get());
            }
        } else {
            if (null != result.get("exception")) {//检查各种异常
                if (!is_service) {
                    String app_name_tip = ActivityUtils.getStringXmlValue(weakReference.get(), R.string.app_name_tip);
                    String sure = ActivityUtils.getStringXmlValue(weakReference.get(), R.string.dialog_ok);
                    String exception = result.get("exception").toString();
                    int flag = HttpConnUtils.handException(exception);//0系统异常,1服务器连接失败
                    String msg = "";
                    if (flag == 0) {
                        msg = weakReference.get().getString(R.string.systembusy);
                    } else if (flag == 1) {
                        msg = weakReference.get().getString(R.string.systemconnectfailed);
                    } else if (flag == 2) {
                        msg = weakReference.get().getString(R.string.urlfailed);
                    } else if (flag == 3) {
                        msg = weakReference.get().getString(R.string.systemconnectfailed);
                    } else if (flag == 4) {
                        msg = weakReference.get().getString(R.string.systemerror);
                    } else if (flag == 5) {
                        LogUtil.iJsonFormat("zkml", "alert: " + msg);
                        return;
                    }
                    ActivityUtils.toast(weakReference.get(), msg);
                }
            } else if ("true".equals(result.get("no_auth"))) {//没有权限
                if (!is_service) {
                    String app_name_tip = ActivityUtils.getStringXmlValue(weakReference.get(), R.string.app_name_tip);
                    String net_exception_tip = ActivityUtils.getStringXmlValue(weakReference.get(), R.string.no_auth_tip);
                    String sure = ActivityUtils.getStringXmlValue(weakReference.get(), R.string.dialog_ok);
                    ActivityUtils.toast(weakReference.get(), net_exception_tip);
                }
            } else if ("true".equals(result.get("reLogin"))) {//回话失效
                if (!is_service) {
                    String session_timeout_error = ActivityUtils.getStringXmlValue(weakReference.get(), R.string.session_timeout_error);
                    ActivityUtils.toast((Activity) weakReference.get(), session_timeout_error);
                    Intent intent = new Intent(weakReference.get(), LoginActivity.class);
                    weakReference.get().startActivity(intent);
                }
                return;
            } else if ("true".equals(result.get("sessionagain"))) {//账号在别处登录了
                if (!is_service) {
                    String session_timeout_error = ActivityUtils.getStringXmlValue(weakReference.get(), R.string.session_again_error);
                    ActivityUtils.toast((Activity) weakReference.get(), session_timeout_error);
                    Intent intent = new Intent(weakReference.get(), LoginActivity.class);
                    weakReference.get().startActivity(intent);
                }
                return;
            } else if (result.get("randomCode") != null && result.size() == 1) {
                if (!is_service) {
                    ActivityUtils.toast(weakReference.get(), weakReference.get().getString(R.string.systemerror));
                }
            } else {
                postFormCompleteListener.postFormComplete(result, reqMap);
            }
        }
    }

    public void setPostCompleteListener(PostFormCompleteListener postFormCompleteListener) {
        this.postFormCompleteListener = postFormCompleteListener;
    }

    public interface PostFormCompleteListener {
        void postFormComplete(Map<String, Object> resultMap, Map<String, String> reqMap);
    }
}