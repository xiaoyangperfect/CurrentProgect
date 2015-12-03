package com.airppt.airppt.util;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.RequestParams;

/**
 * Created by user on 2015/8/3.
 */
public class RequestParam {
    public static RequestParams getRequestParams(Context context) {
        RequestParams params = new RequestParams();
        Log.e("userId", SharedPreferenceUtil.getAccountSharedPreference(context).getString(SharedPreferenceUtil.USERID, ""));
        params.put("userId", SharedPreferenceUtil.getAccountSharedPreference(context).getString(SharedPreferenceUtil.USERID, ""));
        params.put("accessToken", SharedPreferenceUtil.getAccountSharedPreference(context).getString(SharedPreferenceUtil.APP_TOKEN, ""));
        return params;
    }
}
