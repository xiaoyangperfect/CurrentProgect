package com.airppt.airppt.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.VideoView;

import com.airppt.airppt.MainActivity;
import com.airppt.airppt.R;
import com.airppt.airppt.util.Util;

/**
 * Created by user on 2015/9/14.
 */
public class UserGuideActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userguide);
        Util.hideStatusBar(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

//        VideoView videoView = (VideoView) findViewById(R.id.userguide_videoview);
//        String uri = "android.resource://" + getPackageName() + "/" + R.raw.welcome;
//        videoView.setVideoURI(Uri.parse(uri));
//        videoView.start();
//        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
////                SharedPreferenceUtil.getSharedEditor(UserGuideActivity.this).putString(
////                        SharedPreferenceUtil.IS_FIRST_LOAD, getString(R.string.app_version)
////                ).commit();
//                Util.showStatusBar(UserGuideActivity.this);
//                Intent intent = new Intent(UserGuideActivity.this, MainActivity.class);
//                startActivity(intent);
//                UserGuideActivity.this.finish();
//            }
//        });
    }

}
