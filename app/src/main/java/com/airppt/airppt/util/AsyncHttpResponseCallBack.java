package com.airppt.airppt.util;

import android.content.Context;

import org.apache.http.Header;

/**
 * Created by user on 2015/8/3.
 */
public class AsyncHttpResponseCallBack extends AsyncHttpResponseHandler {

    public AsyncHttpResponseCallBack() {
        super();
    }

    public AsyncHttpResponseCallBack(Context context) {
        super(context);
    }

    @Override
    public void onSuccess(int var1, Header[] var2, byte[] var3) {
        super.onSuccess(var1, var2, var3);
    }

    @Override
    public void onFailure(int var1, Header[] var2, byte[] var3, Throwable var4) {

    }
}
