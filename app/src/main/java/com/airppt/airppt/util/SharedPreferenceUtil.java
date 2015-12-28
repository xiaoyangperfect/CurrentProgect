package com.airppt.airppt.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by yang on 2015/6/9.
 */
public class SharedPreferenceUtil {
    private static String APP = "airppt";
    private static String APP_ACCOUNT = "app_account";

    public static String IS_FIRST_LOAD = "isFirstLoad";

    public static String HOST = "host";
    public static String SEARCH_WORD = "searchWord";
    public static String APP_TOKEN = "appToken";
    public static String USERID = "userId";
    public static String USERNAME = "userName";
    public static String HEADURL = "headUrl";
    public static String QRCODE = "QRCode";
    public static String SIGN = "sign";
    public static String UNIONID = "UnionId";


    public static String MOD1 = "mod1";
    public static String MOD2 = "mod2";
    public static String MOD3 = "mod3";
    public static String MOD4 = "mod4";
    public static String MOD5 = "mod5";
    public static String MOD6 = "mod6";
    public static String MOD7 = "mod7";

    //当前替换image的名字
    public static String OPT_IMAGE_NAME = "optImgName";
    public static String WORK_PATH = "workPath";
    public static String CHANGE_IMAGE_NAME = "changeImageName";

    public static String CREATE_TOAST_SHOW_TAG = "createShowTag";

    public static SharedPreferences.Editor getSharedEditor(Context context) {
        return context.getSharedPreferences(APP, 0).edit();
    }
    public static SharedPreferences getSharedPreference(Context context) {
        return context.getSharedPreferences(APP, 0);
    }

    public static SharedPreferences.Editor getAccountSharedEditor(Context context) {
        return context.getSharedPreferences(APP_ACCOUNT, 0).edit();
    }
    public static SharedPreferences getAccountSharedPreference(Context context) {
        return context.getSharedPreferences(APP_ACCOUNT, 0);
    }
}
