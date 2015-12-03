package com.airppt.airppt.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.airppt.airppt.R;
import com.airppt.airppt.entry.MusicEntry;
import com.airppt.airppt.util.CustomWebViewClient;
import com.airppt.airppt.util.HttpConfig;
import com.airppt.airppt.util.JavascriptBridge;
import com.airppt.airppt.util.SharedPreferenceUtil;
import com.airppt.airppt.view.CircleDialogProgressBar;

import org.json.JSONObject;

import java.lang.reflect.Type;

/**
 * Created by yang on 2015/7/2.
 */
public class ChangeMusicActivity extends BaseActivity{
    private WebView webView;
    private String url;
    private Handler handler;
    private Gson gson;
    private MusicEntry entry;
    private CircleDialogProgressBar circleBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changemusic);

        webView = (WebView) findViewById(R.id.changemusic_webview);
        circleBar = CircleDialogProgressBar.createCircleDialogProgressBar(this);
        circleBar.show();
        gson = new Gson();
        url = HttpConfig.CHANGE_MUSIC + "&userId=" + SharedPreferenceUtil.getAccountSharedPreference(this).getString(SharedPreferenceUtil.USERID, "")
            + "&accessToken=" + SharedPreferenceUtil.getAccountSharedPreference(this).getString(SharedPreferenceUtil.APP_TOKEN, "");
        webView.setWebViewClient(new CustomWebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (circleBar.isShowing())
                    circleBar.dismiss();
            }
        });
        WebSettings settings = webView.getSettings();
        webView.setDrawingCacheEnabled(true);
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        JavascriptBridge bridge = new JavascriptBridge(webView);
        bridge.addJavaMethod("getMusicInfo", new JavascriptBridge.Function() {

            @Override
            public Object execute(JSONObject params) {
                try {
                    Log.e("params", params.toString());
                    Type type = new TypeToken<MusicEntry>() {
                    }.getType();
                    entry = gson.fromJson(params.toString(), type);
//                   handler.sendEmptyMessage(0);
                } catch (Exception e) {

                }

                return true;
            }
        });
        webView.loadUrl(url);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                if (entry != null) {
                    Intent intent = new Intent();
                    intent.putExtra("musicId", entry.getData().getMusicId());
                    intent.putExtra("singer", entry.getData().getSinger());
                    intent.putExtra("musicName", entry.getData().getMusicName());
                    intent.putExtra("coverImageURL", entry.getData().getCoverImageURL());
                    ChangeMusicActivity.this.setResult(RESULT_OK, intent);
                }

                ChangeMusicActivity.this.finish();
            }
        };
    }

    public void changeMusciFinish(View view) {
        handler.sendEmptyMessage(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.destroy();
        handler = null;
        setContentView(R.layout.null_view);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            //do something...
            handler.sendEmptyMessage(0);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
