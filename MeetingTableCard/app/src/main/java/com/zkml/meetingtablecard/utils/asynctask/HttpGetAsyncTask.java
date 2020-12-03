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
import com.zkml.meetingtablecard.utils.ActivityUtils;
import com.zkml.meetingtablecard.utils.LogUtil;
import com.zkml.meetingtablecard.utils.NetworkDetector;
import com.zkml.meetingtablecard.utils.httputils.HttpConnUtils;

import java.lang.ref.WeakReference;
import java.util.Map;


public class HttpGetAsyncTask extends AsyncTask<Object, Integer, Map<String, Object>> {
    private ReqGetCompleteListener reqGetCompleteListener;
    private int showDialog = 0;///0：显示dialog；1：显示摆钟view
    private WeakReference<Context> weakReference;
    private Map<String, String> reqMap = null;
    private View view;
    private boolean net_flag;
    private Dialog gifDialog;
    private AnimationDrawable anim;
    private Dialog dialog;
    private int dialogRes = R.layout.car_easy_dialog_loading;
    public static final int DIALOG_TYPE_USUAL = 100;

    public HttpGetAsyncTask(Context context, View view) {
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

    @Override
    protected void onPreExecute() {
        //判断网络连接是否可用
        net_flag = NetworkDetector.isNetworkConnected(weakReference.get());
        if (!net_flag) {
            //网络连接不可用
            return;
        } else {
            // 网络连接可用
            Log.e("gac", "showDialog:" + showDialog);
            if (showDialog == 0) {
                //显示dialog
                View layout = View.inflate(weakReference.get(), dialogRes, null);
                dialog = ActivityUtils.showDialogByViewForRes((Activity) weakReference.get(), layout);
            } else if (showDialog == 1) {
                //显示摆钟view或者progressbar
                //显示新建HttpPostAsyncTask对象时传入的View;
                view.setVisibility(View.VISIBLE);
            } else if (showDialog == DIALOG_TYPE_USUAL) {//数值为100是为了区分，可能传入的的值为2
                View layout = View.inflate(weakReference.get(), dialogRes, null);
                dialog = ActivityUtils.showDialogByViewForRes((Activity) weakReference.get(), layout);
            }
        }
    }

    @Override
    protected Map<String, Object> doInBackground(Object... params) {
        String req_url = (String) params[0];
        if (params.length >= 2) {
            reqMap = (Map<String, String>) params[1];
        }
        Map<String, Object> resultMap = null;
        if (net_flag) {
            resultMap = HttpConnUtils.commGetReqToServer(req_url, reqMap, weakReference.get());
        }
        return resultMap;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected void onPostExecute(Map<String, Object> result) {
        if (showDialog == 0) {
            //不显示dialog
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

        if (null == result) {//未报错，网络请求延时
            //未报错，网络请求延时
            String net_exception_tip = ActivityUtils.getStringXmlValue(weakReference.get(), R.string.net_exception_tip);
            ActivityUtils.toast(weakReference.get(), net_exception_tip);
        } else {
            if (null != result.get("exception")) {
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
            } else if ("true".equals(result.get("no_auth"))) {
                String app_name_tip = ActivityUtils.getStringXmlValue(weakReference.get(), R.string.app_name_tip);
                String net_exception_tip = ActivityUtils.getStringXmlValue(weakReference.get(), R.string.no_auth_tip);
                String sure = ActivityUtils.getStringXmlValue(weakReference.get(), R.string.dialog_ok);
                ActivityUtils.toast(weakReference.get(), net_exception_tip);
            } else if ("true".equals(result.get("reLogin"))) {
                String session_timeout_error = ActivityUtils.getStringXmlValue(weakReference.get(), R.string.session_timeout_error);
                ActivityUtils.toast((Activity) weakReference.get(), session_timeout_error);
                Intent intent = new Intent(weakReference.get(), LoginActivity.class);
                weakReference.get().startActivity(intent);
            } else if ("true".equals(result.get("sessionagain"))) {//账号在别处登录了
                String session_timeout_error = ActivityUtils.getStringXmlValue(weakReference.get(), R.string.session_again_error);
                ActivityUtils.toast((Activity) weakReference.get(), session_timeout_error);
                Intent intent = new Intent(weakReference.get(), LoginActivity.class);
                weakReference.get().startActivity(intent);
            } else if (result.get("randomCode") != null && result.size() == 1) {
                ActivityUtils.toast(weakReference.get(), weakReference.get().getString(R.string.systemerror));
            } else {
                reqGetCompleteListener.reqGetComplete(result);
            }
        }
    }

    public void setGetCompleteListener(ReqGetCompleteListener reqGetCompleteListener) {
        this.reqGetCompleteListener = reqGetCompleteListener;
    }

    public interface ReqGetCompleteListener {
        public void reqGetComplete(Map<String, Object> resultMap);

    }

}
