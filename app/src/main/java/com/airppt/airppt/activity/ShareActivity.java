package com.airppt.airppt.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.airppt.airppt.R;
import com.airppt.airppt.db.DBAdapter;
import com.airppt.airppt.db.ShareDBEntry;
import com.airppt.airppt.util.BitmapCache;
import com.airppt.airppt.util.Util;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;

/**
 * Created by user on 2015/8/4.
 */
public class ShareActivity extends BaseActivity {

    private String url;
    private String imgUrl;
    private String orgTitle;
    private String orgContent;
    private String workId;

    private EditText title, content;
    private ImageView coverImg;

    private RequestQueue mQueue;
    private ImageLoader imageLoader;
    //是否音乐分享模式
    private boolean inMusicModle;

    private DBAdapter dbAdapter;
    private ShareDBEntry shareDBEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        initDate();
        initView();
        getView();
    }

    private void initDate() {
        url = getIntent().getStringExtra("shareUrl");
        imgUrl = getIntent().getStringExtra("imgUrl");
        workId = getIntent().getStringExtra("workId");
        dbAdapter = new DBAdapter(this);
        Log.e("workid", workId);
        shareDBEntry = dbAdapter.getShareContent(workId);
        if (shareDBEntry != null) {
//            orgTitle = shareDBEntry.getTitle();
            orgContent = shareDBEntry.getContent();
            String isMusicInuse = shareDBEntry.getIsMusicInuse();
            String isMusicModle = shareDBEntry.getIsMusicModle();
            if (isMusicInuse.equals("1") && isMusicModle.equals("1")) {
                inMusicModle = true;
            } else {
                inMusicModle = false;
            }
        } else {
            orgContent = "";
        }
        orgTitle = getIntent().getStringExtra("title");

        if (url == null || imgUrl == null)
            this.finish();

        ShareSDK.initSDK(this);
        mQueue = Volley.newRequestQueue(this);
        imageLoader = new ImageLoader(mQueue, new BitmapCache());
    }

    private void initView() {
        title = (EditText) findViewById(R.id.share_title);
        content = (EditText) findViewById(R.id.share_content);
        coverImg = (ImageView) findViewById(R.id.share_cover);
        if (inMusicModle) {
            title.setText(shareDBEntry.getMusicTitle());
            content.setText(shareDBEntry.getMusicAuthor());
        } else {
            if (Util.isStringNotEmpty(orgTitle)) {
                title.setText(orgTitle);
            }
            if (Util.isStringNotEmpty(orgContent)) {
                content.setText(orgContent);
            }
        }

    }

    private void getView() {

        imageLoader.get(imgUrl,
                ImageLoader.getImageListener(coverImg,
                        R.mipmap.empty_photo, R.mipmap.error_img), 100, 100);
    }

    private Platform.ShareParams getShareParam() {
        Platform.ShareParams params = new Platform.ShareParams();
        params.setTitle(title.getText().toString());
        params.setText(content.getText().toString());
        if (inMusicModle) {
            params.setShareType(Platform.SHARE_MUSIC);
        } else {
            params.setShareType(Platform.SHARE_WEBPAGE);
        }
        params.setImageUrl(imgUrl);
        params.setUrl(url);
        return params;
    }

    private boolean canToShare() {
        if (!Util.isStringNotEmpty(title.getText().toString()) ||
                !Util.isStringNotEmpty(content.getText().toString())) {
            Toast.makeText(ShareActivity.this, getResources().getString(R.string.input_share_content), Toast.LENGTH_LONG).show();
            return false;
        }
        ShareDBEntry entry = new ShareDBEntry();
        entry.setId(workId);
        if (inMusicModle) {
            entry.setIsMusicInuse("1");
            entry.setIsMusicModle("1");
            entry.setMusicTitle(title.getText().toString());
            entry.setMusicAuthor(content.getText().toString());
            dbAdapter.updateShareMusic(entry);
        } else {
            entry.setTitle(title.getText().toString());
            entry.setContent(content.getText().toString());
            dbAdapter.updateShareText(entry);
        }


        return true;
    }

    public void shareWechat(View view) {
        if (canToShare()) {
            Platform platform = ShareSDK.getPlatform("Wechat");
            platform.share(getShareParam());
            ShareActivity.this.finish();
        }

    }

    public void shareComment(View view) {
        if (canToShare()) {
            Platform platform = ShareSDK.getPlatform("WechatMoments");
            Platform.ShareParams params = new Platform.ShareParams();
            params.setTitle(title.getText().toString() + "\n" + content.getText().toString());
            params.setText(content.getText().toString());
            if (inMusicModle) {
                params.setShareType(Platform.SHARE_MUSIC);
            } else {
                params.setShareType(Platform.SHARE_WEBPAGE);
            }
            params.setImageUrl(imgUrl);
            params.setUrl(url);
            platform.share(params);
            ShareActivity.this.finish();
        }

    }

    public void shareQQ(View view) {
        if (canToShare()) {
            Platform platform = ShareSDK.getPlatform(QZone.NAME);
            QZone.ShareParams sp = new QZone.ShareParams();
            sp.setTitle(title.getText().toString());
            sp.setTitleUrl(url);
            sp.setText(content.getText().toString());
            sp.setImageUrl(imgUrl);
            sp.setSite("AirPPT");
            sp.setSiteUrl(url);
            platform.share(sp);
            ShareActivity.this.finish();
        }
    }

    public void shareWeibo(View view) {
        if (canToShare()) {
            Platform.ShareParams sp = new Platform.ShareParams();
            sp.setTitle(title.getText().toString());
            sp.setText(content.getText().toString() + url);
            sp.setImageUrl(imgUrl);
            sp.setUrl(url);

            Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
            weibo.setPlatformActionListener(new PlatformActionListener() {
                @Override
                public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                    Toast.makeText(ShareActivity.this, getResources().getString(R.string.share_success), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onError(Platform platform, int i, Throwable throwable) {
                    Toast.makeText(ShareActivity.this, getResources().getString(R.string.share_success), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCancel(Platform platform, int i) {
                    Toast.makeText(ShareActivity.this, getResources().getString(R.string.share_success), Toast.LENGTH_LONG).show();
                }
            }); // 设置分享事件回调
// 执行图文分享
            weibo.share(sp);
            ShareActivity.this.finish();
        }

    }

    public void shareFinish(View view) {
        ShareActivity.this.finish();
    }


    @Override
    protected void onDestroy() {
        Util.imageRecycle(coverImg);
        setContentView(R.layout.null_view);
        super.onDestroy();
    }
}
