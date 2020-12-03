package com.zkml.meetingtablecard.utils.httputils.callback;

import okhttp3.Response;

/**
 * ================================================
 * 版    本：1.0
 * 创建日期：2016/1/12
 * 描    述：返回字符串类型的数据
 * 修订历史：
 * ================================================
 */
public abstract class StringCallback extends AbsCallback<String> {

    @Override
    public String parseNetworkResponse(Response response) throws Exception {
        return response.body().string();
    }
}
