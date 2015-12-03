package com.airppt.airppt.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airppt.airppt.R;
import com.airppt.airppt.util.AnimationUtil;
import com.airppt.airppt.util.AsyncHttpResponseCallBack;
import com.airppt.airppt.util.FileUtil;
import com.airppt.airppt.util.HttpConfig;
import com.airppt.airppt.util.ImageOptUtil;
import com.airppt.airppt.util.RequestParam;
import com.airppt.airppt.util.SharedPreferenceUtil;
import com.airppt.airppt.util.Util;
import com.airppt.airppt.view.CircleDialogProgressBar;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.io.File;

/**
 * Created by user on 2015/8/10.
 */
public class PersonalInfoActivity extends BaseActivity {

    private static final int SHOT_CHOSE = 10;
    private static final int SHOT_CUT = 11;
    private static final String IMAGE_UNSPECIFIED = "image/*";

    private static final int NONE = 0;
    private static final int PHOTO_GRAPH = 1;// 拍照
    private static final int PHOTO_ZOOM = 2; // 缩放
    private static final int PHOTO_CROP = 3;// 结果

    private ImageView head, qr;
    private TextView name, textSize;
    private EditText sign, nickNameInput;
    private String userId;
    private LinearLayout nickNameInputLay;

    private boolean isChanged;
    private String nickName;
    private Animation show, hide;

    private AsyncHttpClient httpClient;
    private CircleDialogProgressBar circleBar;

    private LocalBroadcastManager localBroadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        head = (ImageView) findViewById(R.id.personal_head_img);
        qr = (ImageView) findViewById(R.id.personal_qr_img);
        name = (TextView) findViewById(R.id.personal_nickname);
        sign = (EditText) findViewById(R.id.personal_sign_edit);
        textSize = (TextView) findViewById(R.id.personal_text_size);
        nickNameInputLay = (LinearLayout) findViewById(R.id.personal_info_nickname_input_lay);
        nickNameInput = (EditText) findViewById(R.id.personal_nickname_input);
        circleBar = CircleDialogProgressBar.createCircleDialogProgressBar(this);

        isChanged = false;
        //初始化网络工具
        httpClient = new AsyncHttpClient();
        httpClient.setTimeout(30000);

        userId = SharedPreferenceUtil.getAccountSharedPreference(PersonalInfoActivity.this).getString(SharedPreferenceUtil.UNIONID, "");

        ImageOptUtil.imageLoader.displayImage(SharedPreferenceUtil.getAccountSharedPreference(this).getString(SharedPreferenceUtil.HEADURL, ""),
                head, ImageOptUtil.image_display_options_circle_no_cache);
//        ImageOptUtil.imageLoader.displayImage(SharedPreferenceUtil.getAccountSharedPreference(this).getString(SharedPreferenceUtil.QRCODE, ""),
//                qr, ImageOptUtil.image_display_options_no_cache);

        name.setText(SharedPreferenceUtil.getAccountSharedPreference(this).getString(SharedPreferenceUtil.USERNAME, ""));
        sign.setText(SharedPreferenceUtil.getAccountSharedPreference(this).getString(SharedPreferenceUtil.SIGN, ""));

        Log.e("info",
                SharedPreferenceUtil.getAccountSharedPreference(this).getString(SharedPreferenceUtil.HEADURL, "") + "\n" +
                        SharedPreferenceUtil.getAccountSharedPreference(this).getString(SharedPreferenceUtil.USERNAME, "") + "\n" +
                        SharedPreferenceUtil.getAccountSharedPreference(this).getString(SharedPreferenceUtil.SIGN, ""));
        sign.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int num = s.length();
                textSize.setText((250 - num) + "");
                if (250 <= num) {
                    Toast.makeText(PersonalInfoActivity.this, R.string.input_out_bande, Toast.LENGTH_SHORT).show();
                }
            }
        });

        show = AnimationUtil.initAlphaShowAnimation();
        hide = AnimationUtil.initAlphaHideAnimation();



        localBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    public void setHead(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
        startActivityForResult(intent, PHOTO_ZOOM);
    }

    public void personalFinish(View view) {
        this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PHOTO_ZOOM:
                    ContentResolver resolver = getContentResolver();
                    Uri uri = data.getData();
                    String[] proj = {MediaStore.Images.Media.DATA};
                    Cursor cursor = managedQuery(uri, proj, null, null, null);
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    String path = cursor.getString(column_index);
                    Intent intent = new Intent(PersonalInfoActivity.this, PhotoCropActivity.class);
                    intent.putExtra("orgPath", FileUtil.WORKS_PATH);
                    intent.putExtra("path", path);
                    intent.putExtra("width", "300");
                    intent.putExtra("hight", "300");
                    intent.putExtra("name", userId + ".jpg");
                    startActivityForResult(intent, PHOTO_CROP);
                    break;
                case PHOTO_CROP:
                    ImageOptUtil.imageLoader.displayImage("file://" + FileUtil.WORKS_PATH + "/" + userId + ".jpg",
                            head, ImageOptUtil.image_display_options_circle_no_cache);
                    isChanged = true;
                    break;
            }
        }
    }

    public void onNickName(View view) {
        if (nickNameInputLay.getVisibility() == View.VISIBLE) {
            nickNameInputLay.startAnimation(hide);
            nickNameInputLay.setVisibility(View.GONE);
            nickName = nickNameInput.getText().toString();
            if (Util.isStringNotEmpty(nickName)) {
                name.setText(nickName);
            }
        } else {
            nickNameInputLay.startAnimation(show);
            nickNameInputLay.setVisibility(View.VISIBLE);
        }
    }

    public void onSaved(View view) {
        circleBar.show();
        if (isChanged || Util.isStringNotEmpty(nickNameInput.getText().toString()) || Util.isStringNotEmpty(sign.getText().toString())) {
            try {
                RequestParams params = RequestParam.getRequestParams(PersonalInfoActivity.this);
                File file = new File(FileUtil.WORKS_PATH + "/" + userId + ".jpg");
                if (isChanged && file.exists()) {
                   params.put("profile_picture", file);
                }
                params.put("unionId", SharedPreferenceUtil.getAccountSharedPreference(PersonalInfoActivity.this).getString(SharedPreferenceUtil.UNIONID, ""));
                if (Util.isStringNotEmpty(nickNameInput.getText().toString())) {
                    params.put("nickName", nickNameInput.getText().toString());
                } else {
                    params.put("nickName", "");
                }
                if (Util.isStringNotEmpty(sign.getText().toString())) {
                    params.put("sign", sign.getText().toString());
                } else {
                    params.put("sign", "");
                }
                httpClient.post(HttpConfig.SETUSERSIGNORQRCODE, params, new AsyncHttpResponseCallBack(PersonalInfoActivity.this) {
                    @Override
                    public void onSuccess(int var1, Header[] var2, byte[] var3) {
                        super.onSuccess(var1, var2, var3);
                        if (isChanged) {
                            SharedPreferenceUtil.getAccountSharedEditor(PersonalInfoActivity.this)
                                    .putString(SharedPreferenceUtil.HEADURL,
                                            SharedPreferenceUtil.getSharedPreference(PersonalInfoActivity.this).getString(
                                                    SharedPreferenceUtil.HOST, HttpConfig.BASE_URL_1
                                            )+ userId + ".jpg").commit();
                        }
                        if (Util.isStringNotEmpty(nickNameInput.getText().toString())) {
                            SharedPreferenceUtil.getAccountSharedEditor(PersonalInfoActivity.this)
                                    .putString(SharedPreferenceUtil.USERNAME, nickNameInput.getText().toString()).commit();
                        }
                        if (Util.isStringNotEmpty(sign.getText().toString())) {
                            SharedPreferenceUtil.getAccountSharedEditor(PersonalInfoActivity.this)
                                    .putString(SharedPreferenceUtil.SIGN, sign.getText().toString()).commit();
                        }
                        Toast.makeText(PersonalInfoActivity.this, R.string.save_success, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent();
                        intent.setAction("action_personalinfo");
                        localBroadcastManager.sendBroadcast(intent);
                        if (circleBar.isShowing())
                            circleBar.dismiss();
                    }

                    @Override
                    public void onFailure(int var1, Header[] var2, byte[] var3, Throwable var4) {
                        super.onFailure(var1, var2, var3, var4);
                        Toast.makeText(PersonalInfoActivity.this, R.string.save_fale, Toast.LENGTH_LONG).show();
                        if (circleBar.isShowing())
                            circleBar.dismiss();
                    }
                });
            } catch (Exception e) {
                Toast.makeText(PersonalInfoActivity.this, R.string.save_fale, Toast.LENGTH_LONG).show();
            }


        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        FileUtil.deleteFile(FileUtil.WORKS_PATH + "/" + userId + ".jpg");
    }
}
