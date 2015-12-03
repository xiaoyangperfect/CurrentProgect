package com.airppt.airppt.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.airppt.airppt.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.airppt.airppt.MainActivity;
import com.airppt.airppt.activity.LoginActivity;
import com.airppt.airppt.entry.UserInfoData;
import com.airppt.airppt.entry.WXEntry;
import com.airppt.airppt.util.HttpConfig;
import com.airppt.airppt.util.SharedPreferenceUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.apache.http.Header;

import java.lang.reflect.Type;

/**
 * Created by yang on 2015/6/18.
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;
    private AsyncHttpClient httpClient;
    private Gson gson;
    private boolean isAuth;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    WXEntry entry = (WXEntry) msg.obj;
                    SharedPreferenceUtil.getAccountSharedEditor(WXEntryActivity.this).putString(SharedPreferenceUtil.APP_TOKEN, entry.getAccess_token()).commit();
                    RequestParams requestParams = new RequestParams();
                    requestParams.put("accessToken", entry.getAccess_token());
                    requestParams.put("openId", entry.getOpenid());
                    requestParams.put("loginType", "weixin");
                    httpClient.post(HttpConfig.USERLOGIN, requestParams, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int i, Header[] headers, byte[] bytes) {
                            Log.e("userInfo", new String(bytes));
                            Type type = new TypeToken<UserInfoData>(){}
                                    .getType();
                            UserInfoData data = gson.fromJson(new String(bytes), type);
                            SharedPreferenceUtil.getAccountSharedEditor(WXEntryActivity.this).putString(SharedPreferenceUtil.USERID,  data.getData().getUserId() + "").commit();
                            SharedPreferenceUtil.getAccountSharedEditor(WXEntryActivity.this).putString(SharedPreferenceUtil.USERNAME, data.getData().getNickname()).commit();
                            SharedPreferenceUtil.getAccountSharedEditor(WXEntryActivity.this).putString(SharedPreferenceUtil.HEADURL, data.getData().getHeadimgurl()).commit();
                            SharedPreferenceUtil.getAccountSharedEditor(WXEntryActivity.this).putString(SharedPreferenceUtil.QRCODE, data.getData().getQRCode()).commit();
                            SharedPreferenceUtil.getAccountSharedEditor(WXEntryActivity.this).putString(SharedPreferenceUtil.SIGN, data.getData().getSign()).commit();
                            SharedPreferenceUtil.getAccountSharedEditor(WXEntryActivity.this).putString(SharedPreferenceUtil.UNIONID, data.getData().getUnionId()).commit();
                            Intent intent = new Intent(WXEntryActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            Toast.makeText(WXEntryActivity.this, getString(R.string.load_sucess), Toast.LENGTH_LONG).show();
                            WXEntryActivity.this.finish();
                        }

                        @Override
                        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                            Toast.makeText(WXEntryActivity.this, getString(R.string.load_fail), Toast.LENGTH_LONG).show();
                        }
                    });

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        switch (getIntent().getIntExtra("_wxapi_command_type", 0)) {
            case 1:
                isAuth = true;
                break;
            default:
                isAuth = false;
                break;
        }

        httpClient = new AsyncHttpClient();
        gson = new Gson();
        api = WXAPIFactory.createWXAPI(this, WXConfig.APP_ID, false);
        api.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    //这里有坑哦，发现没？
    @Override
    public void onResp(BaseResp baseResp) {
        String result = "";
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                if (!isAuth) {
                    Toast.makeText(WXEntryActivity.this, "分享成功！", Toast.LENGTH_LONG).show();
                }

                try {
                    String code = ((SendAuth.Resp) baseResp).code;
                    String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + WXConfig.APP_ID + "&secret=" + WXConfig.APP_SECRET + "&code=" + code + "&grant_type=authorization_code";

                    httpClient.get(url, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int i, Header[] headers, byte[] bytes) {
                            Log.e("wx", new String(bytes));
                            Type type = new TypeToken<WXEntry>(){}
                                    .getType();
                            WXEntry entry = gson.fromJson(new String(bytes), type);
                            Message msg = new Message();
                            msg.what = 1;
                            msg.obj = entry;
                            handler.sendMessage(msg);
                        }

                        @Override
                        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

                        }
                    });
                } catch (Exception e) {
//                    Toast.makeText(WXEntryActivity.this, "操作失败！", Toast.LENGTH_LONG).show();
                    WXEntryActivity.this.finish();
                }
                break;

            case BaseResp.ErrCode.ERR_USER_CANCEL:
                Toast.makeText(WXEntryActivity.this, "操作被取消！", Toast.LENGTH_LONG).show();
                LoginActivity.isShowing = true;
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                Toast.makeText(WXEntryActivity.this, "操作被拒绝！", Toast.LENGTH_LONG).show();
                LoginActivity.isShowing = true;
                break;

            default:
                break;

        }

        WXEntryActivity.this.finish();
    }

}
