package com.zkml.meetingtablecard.utils.httputils.callbackimpl;


import com.zkml.meetingtablecard.utils.httputils.callback.AbsCallback;
import com.zkml.meetingtablecard.utils.httputils.request.BaseRequest;

/**
 * ================================================
 * 版    本：1.0
 * 创建日期：2016/4/8
 * 修订历史：该类主要用于在所有请求之前添加公共的请求头或请求参数，例如登录授权的 token,使用的设备信息等
 * ================================================
 */
public abstract class CommonCallback<T> extends AbsCallback<T> {
    @Override
    public void onBefore(BaseRequest request) {
        super.onBefore(request);
        //如果账户已经登录，就添加 token 等等
        request.headers("CallBackHeaderKey1", "CallBackHeaderValue1")//
                .headers("CallBackHeaderKey2", "CallBackHeaderValue2")//
                .params("CallBackParamsKey1", "CallBackParamsValue1")//
                .params("CallBackParamsKey2", "CallBackParamsValue1")//
                .params("token", "3215sdf13ad1f65asd4f3ads1f");
    }
}
