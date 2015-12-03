package com.airppt.airppt.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.flurry.android.FlurryAgent;
import com.airppt.airppt.MyApplication;

/**
 * Created by user on 2015/3/27.
 */
public class BaseActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MyApplication)getApplication()).pushActivity(this);
    }

    @Override
    protected void onDestroy() {
        ((MyApplication)getApplication()).popActivity(this);
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }
}
