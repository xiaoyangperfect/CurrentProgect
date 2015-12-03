package com.airppt.airppt.util;

import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by yang on 2015/6/4.
 */
public class CustomWebViewClient extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }
}
