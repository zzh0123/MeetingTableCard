package com.zkml.meetingtablecard.utils.httputils.callbackimpl;

import android.app.Activity;
import android.app.ProgressDialog;
import android.view.Window;

import androidx.annotation.Nullable;

import com.zkml.meetingtablecard.utils.httputils.request.BaseRequest;

import okhttp3.Call;
import okhttp3.Response;

/**
 * ================================================
 * 版    本：1.0
 * 创建日期：2016/4/8
 * 修订历史：
 * ================================================
 */
public abstract class StringDialogCallback extends EncryptCallback<String> {

    private ProgressDialog dialog;

    public StringDialogCallback(Activity activity) {
        dialog = new ProgressDialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("请求网络中...");
    }

    @Override
    public String parseNetworkResponse(Response response) throws Exception {
        return response.body().string();
    }

    @Override
    public void onBefore(BaseRequest request) {
        super.onBefore(request);
        //网络请求前显示对话框
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    @Override
    public void onAfter(boolean isFromCache, @Nullable String s, Call call, Response response, @Nullable Exception e) {
        super.onAfter(isFromCache, s, call, response, e);
        //网络请求结束后关闭对话框
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
