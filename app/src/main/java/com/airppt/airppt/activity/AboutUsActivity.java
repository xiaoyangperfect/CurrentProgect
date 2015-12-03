package com.airppt.airppt.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.airppt.airppt.R;

/**
 * Created by user on 2015/8/19.
 */
public class AboutUsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);
    }


    public void toFadeBack(View view) {
//        FeedbackAgent agent = new FeedbackAgent(AboutUsActivity.this);
//        agent.startFeedbackActivity();
        Intent intent = new Intent(this, CustomUserFadebackActivity.class);
        startActivity(intent);
    }

    public void aboutUsFinished(View view) {
        AboutUsActivity.this.finish();
    }

    public void toProtocol(View view) {
        Intent intent = new Intent(this, ProtocolActivity.class);
        startActivity(intent);
    }
}
