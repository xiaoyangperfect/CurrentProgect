package com.airppt.airppt;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;

import com.gc.materialdesign.widgets.SnackBar;
import com.airppt.airppt.activity.BaseActivity;
import com.airppt.airppt.activity.fragment.FragmentAccount;
import com.airppt.airppt.activity.fragment.FragmentMain;
import com.airppt.airppt.activity.fragment.FragmentSearch;
import com.airppt.airppt.util.SharedPreferenceUtil;
import com.airppt.airppt.util.Util;


public class MainActivity extends BaseActivity {

    private static final String FIRSTTAB = "homepage";
    private static final String TWOTAB = "search";
    private static final String THREETAB = "account";

    private FragmentTabHost mTabHost;
    private View indicator = null;
    private ImageView modImg;
    private ImageView modImg2, modImg7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((MyApplication) getApplication()).popAllActivityExceptOne(MainActivity.class);

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        indicator = getIndicatorView(R.layout.tab_main);
        mTabHost.addTab(mTabHost.newTabSpec(FIRSTTAB).setIndicator(indicator), FragmentMain.class, null);

        indicator = getIndicatorView(R.layout.tab_search);
        mTabHost.addTab(mTabHost.newTabSpec(TWOTAB).setIndicator(indicator), FragmentSearch.class, null);

        indicator = getIndicatorView(R.layout.tab_account);
        mTabHost.addTab(mTabHost.newTabSpec(THREETAB).setIndicator(indicator), FragmentAccount.class, null);

        modImg = (ImageView) findViewById(R.id.mainpage_mod1);
        modImg2 = (ImageView) findViewById(R.id.account_mod);
        modImg7 = (ImageView) findViewById(R.id.account_mod7);

        modImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modImg.setVisibility(View.GONE);
                Util.imageRecycle(modImg);
                SharedPreferenceUtil.getSharedEditor(MainActivity.this).putString(
                        SharedPreferenceUtil.MOD1, getString(R.string.app_version)
                ).commit();
            }
        });

        modImg2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modImg2.setVisibility(View.GONE);
                Util.imageRecycle(modImg2);
                SharedPreferenceUtil.getSharedEditor(MainActivity.this).putString(
                        SharedPreferenceUtil.MOD2, getString(R.string.app_version)
                ).commit();
            }
        });
        modImg7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modImg7.setVisibility(View.GONE);
                Util.imageRecycle(modImg7);
                SharedPreferenceUtil.getSharedEditor(MainActivity.this).putString(
                        SharedPreferenceUtil.MOD7, getString(R.string.app_version)
                ).commit();
            }
        });

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if (tabId.equals(THREETAB)) {
                    if (!Util.isStringNotEmpty(SharedPreferenceUtil.getAccountSharedPreference(MainActivity.this).getString(SharedPreferenceUtil.USERID, ""))) {
                        Util.toLogin(MainActivity.this);
                        mTabHost.setCurrentTab(0);
                    }
                }
            }
        });
    }

    private View getIndicatorView(int layoutId) {
        View view = getLayoutInflater().inflate(layoutId, null);
        return view;
    }


    public void showMod1() {
        modImg.setBackgroundResource(R.mipmap.mod_1);
        modImg.setVisibility(View.VISIBLE);
    }

    public void showMod2() {
        modImg2.setBackgroundResource(R.mipmap.mod_2);
        modImg2.setVisibility(View.VISIBLE);
    }

    public void showMod7() {
        modImg7.setBackgroundResource(R.mipmap.mod_7);
        modImg7.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTabHost = null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            new SnackBar(this, getString(R.string.sure_to_exist), getString(R.string.exist), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MyApplication) getApplication()).finished();
                }
            }).show();
        }
        return super.onKeyDown(keyCode, event);
    }
}
