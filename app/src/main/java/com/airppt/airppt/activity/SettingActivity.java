package com.airppt.airppt.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.airppt.airppt.R;
import com.airppt.airppt.util.FileUtil;
import com.airppt.airppt.util.SharedPreferenceUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

/**
 * Created by user on 2015/8/10.
 */
public class SettingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
    }

    public void toPersonalInfo(View view) {
        Intent intent = new Intent(this, PersonalInfoActivity.class);
        startActivity(intent);
    }

    public void cleanCache(View view) {
        ImageLoader.getInstance().clearDiscCache();
        ImageLoader.getInstance().clearMemoryCache();
        if (FileUtil.deleteDirectory(new File(FileUtil.SD_PATH))) {
            Toast.makeText(this, R.string.clean_cache_success, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.clean_cache_false, Toast.LENGTH_SHORT).show();
        }

    }

    public void toLogin(View view) {
        SharedPreferenceUtil.getAccountSharedEditor(this).clear().commit();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        SettingActivity.this.finish();
    }

    public void toAboutUs(View view) {
        Intent intent = new Intent(this, AboutUsActivity.class);
        startActivity(intent);
    }

    public void toFadeback(View view) {
//        FeedbackAgent agent = new FeedbackAgent(SettingActivity.this);
//        agent.startFeedbackActivity();
        Intent intent = new Intent(this, CustomUserFadebackActivity.class);
        startActivity(intent);
    }

    public void settingFinish(View view) {
        this.finish();
    }
}
