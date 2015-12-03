package com.airppt.airppt.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.airppt.airppt.MainActivity;
import com.airppt.airppt.R;
import com.airppt.airppt.entry.UserInfoData;
import com.airppt.airppt.util.HttpConfig;
import com.airppt.airppt.util.SharedPreferenceUtil;
import com.airppt.airppt.view.CircleDialogProgressBar;
import com.airppt.airppt.wxapi.WXConfig;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.apache.http.Header;

import java.lang.reflect.Type;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;

/**
 * Created by yang on 2015/6/18.
 */
public class LoginActivity extends BaseActivity {
    private IWXAPI api;
    private CircleDialogProgressBar circleBar;

    private String accessToken;
    private String openId;
    private AsyncHttpClient httpClient;
    private Gson gson;

    public static boolean isShowing = false;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    SharedPreferenceUtil.getAccountSharedEditor(LoginActivity.this).putString(SharedPreferenceUtil.APP_TOKEN, accessToken).commit();
                    RequestParams requestParams = new RequestParams();
                    requestParams.put("accessToken", accessToken);
                    requestParams.put("openId", openId);
                    requestParams.put("loginType", "weibo");
                    httpClient.post(HttpConfig.USERLOGIN, requestParams, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int i, Header[] headers, byte[] bytes) {
                            Log.e("userInfo", new String(bytes));
                            Type type = new TypeToken<UserInfoData>() {
                            }
                                    .getType();
                            UserInfoData data = gson.fromJson(new String(bytes), type);
                            SharedPreferenceUtil.getAccountSharedEditor(LoginActivity.this).putString(SharedPreferenceUtil.USERID, data.getData().getUserId() + "").commit();
                            SharedPreferenceUtil.getAccountSharedEditor(LoginActivity.this).putString(SharedPreferenceUtil.USERNAME, data.getData().getNickname()).commit();
                            SharedPreferenceUtil.getAccountSharedEditor(LoginActivity.this).putString(SharedPreferenceUtil.HEADURL, data.getData().getHeadimgurl()).commit();
                            SharedPreferenceUtil.getAccountSharedEditor(LoginActivity.this).putString(SharedPreferenceUtil.QRCODE, data.getData().getQRCode()).commit();
                            SharedPreferenceUtil.getAccountSharedEditor(LoginActivity.this).putString(SharedPreferenceUtil.SIGN, data.getData().getSign()).commit();
                            SharedPreferenceUtil.getAccountSharedEditor(LoginActivity.this).putString(SharedPreferenceUtil.UNIONID, data.getData().getUnionId()).commit();

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            Toast.makeText(LoginActivity.this, "登陆成功！", Toast.LENGTH_LONG).show();
                            LoginActivity.this.finish();
                        }

                        @Override
                        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                            Toast.makeText(LoginActivity.this, "登陆失败！", Toast.LENGTH_LONG).show();
                        }
                    });

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        ((MyApplication)getApplication()).popAllActivityExceptOne(LoginActivity.class);
        TextView tv = (TextView) findViewById(R.id.login_protocol_text);
        tv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        circleBar = CircleDialogProgressBar.createCircleDialogProgressBar(this);
        httpClient = new AsyncHttpClient();
        gson = new Gson();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isShowing) {
            if (circleBar.isShowing())
                circleBar.dismiss();
        }
    }

    public void loadByWeChat(View view) {
        circleBar.show();
        api = WXAPIFactory.createWXAPI(this, WXConfig.APP_ID, false);

        if(!api.isWXAppInstalled()) {
            Toast.makeText(this, getString(R.string.cannot_find_wechat), Toast.LENGTH_LONG).show();
        }
        api.registerApp(WXConfig.APP_ID);
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = System.currentTimeMillis() + "";
        api.sendReq(req);
    }

    public void skip(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        SharedPreferenceUtil.getAccountSharedEditor(LoginActivity.this).putString(SharedPreferenceUtil.USERID,  5 + "").commit();
        startActivity(intent);
        this.finish();
    }

    public void loadByWeibo(View view) {
        circleBar.show();
        ShareSDK.initSDK(LoginActivity.this);
        Platform weibo = ShareSDK.getPlatform(LoginActivity.this, SinaWeibo.NAME);
//        weibo.SSOSetting(false);
        weibo.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                Platform weibo = ShareSDK.getPlatform(LoginActivity.this, SinaWeibo.NAME);
                accessToken = weibo.getDb().getToken();
                openId = weibo.getDb().getUserId();
                handler.sendEmptyMessage(1);
                Log.e("weibo", "ok" + accessToken);
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                Log.e("weibo", "ok");
                if (circleBar.isShowing())
                    circleBar.dismiss();
            }

            @Override
            public void onCancel(Platform platform, int i) {
                Log.e("weibo", "ok");
                if (circleBar.isShowing())
                    circleBar.dismiss();
            }
        });

        weibo.authorize();
    }

    public void loginFinish(View view) {
        LoginActivity.this.finish();
    }

    public void toProtocolActivity(View view) {
        Intent intent = new Intent(this, ProtocolActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setContentView(R.layout.null_view);
    }
}
