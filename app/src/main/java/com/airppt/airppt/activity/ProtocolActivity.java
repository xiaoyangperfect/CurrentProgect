package com.airppt.airppt.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.airppt.airppt.R;
import com.airppt.airppt.util.CustomWebViewClient;

/**
 * Created by user on 2015/10/29.
 */
public class ProtocolActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protocol);

        WebView webView = (WebView) findViewById(R.id.protocol_webview);
        webView.setWebViewClient(new CustomWebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
        webView.loadUrl("http://www.airppt.cn/terms/terms.html");
    }


    public void finishProtocol(View view) {
        ProtocolActivity.this.finish();
    }
}
