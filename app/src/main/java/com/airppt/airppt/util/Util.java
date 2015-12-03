package com.airppt.airppt.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.WindowManager;
import android.widget.ImageView;

import com.airppt.airppt.activity.LoginActivity;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by user on 2015/4/24.
 */
public class Util {

    public static boolean isStringNotEmpty(String str) {
        if(str != null && !str.equals(""))
            return true;
        return false;
    }

    public static String inputStream2String(InputStream inputStream) {
        StringBuilder str = new StringBuilder();
        try {
            int len = -1;
            byte[] buff = new byte[1024];
            while ((len = inputStream.read(buff)) != -1) {
                str.append(new String(buff, 0, len));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str.toString();
    }

    /**
     * 回收用过的imageview
     * @param imageView
     */
    public static void imageRecycle(ImageView imageView) {
        if (imageView == null)
            return;
        Drawable drawable = imageView.getDrawable();
        if (drawable != null && drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
    }

    public static void hideStatusBar(Activity activity) {
        WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        activity.getWindow().setAttributes(attrs);
    }

    public static void showStatusBar(Activity activity) {
        WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        activity.getWindow().setAttributes(attrs);
    }

    //跳转到登录页面
    public static void toLogin(Activity activity) {
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
    }
}
