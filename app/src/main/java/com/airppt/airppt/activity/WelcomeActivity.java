package com.airppt.airppt.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;
import android.widget.ImageView;

import com.airppt.airppt.MainActivity;
import com.airppt.airppt.R;
import com.airppt.airppt.util.SharedPreferenceUtil;
import com.airppt.airppt.util.Util;

/**
 * Created by yang on 2015/6/18.
 */
public class WelcomeActivity extends BaseActivity {

    private ImageView loadImg;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Util.showStatusBar(WelcomeActivity.this);
            intentToMain();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Util.hideStatusBar(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        loadImg = (ImageView) findViewById(R.id.loading_page);
        loadImg.setBackgroundResource(R.mipmap.loading_page);

        //初始化Toast展示次数
        boolean isFirstLoad = SharedPreferenceUtil.getSharedPreference(this).getBoolean(SharedPreferenceUtil.IS_FIRST_LOAD, true);
        if (isFirstLoad) {
            SharedPreferenceUtil.getSharedEditor(this).putBoolean(SharedPreferenceUtil.IS_FIRST_LOAD, false).commit();
            SharedPreferenceUtil.getSharedEditor(this).putInt(SharedPreferenceUtil.CREATE_TOAST_SHOW_TAG, 10).commit();
        }

        handler.sendEmptyMessageDelayed(0, 2000);
    }


    private void intentToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    protected void onDestroy() {
        Util.imageRecycle(loadImg);
        setContentView(R.layout.null_view);
        super.onDestroy();
    }
}
