package com.airppt.airppt.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.view.View;

/**
 * Created by yang on 2015/6/17.
 */
public class ScreenShortCutUtil {
    public static Bitmap getShortScreen(Activity activity) throws Exception{
        try {
            //View是你需要截图的View
            View view = activity.getWindow().getDecorView();
            view.setDrawingCacheEnabled(true);
            view.buildDrawingCache();
            Bitmap b1 = view.getDrawingCache();
            if (b1 == null)
                return null;
            //获取状态栏高度
            Rect frame = new Rect();
            activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            int statusBarHeight = frame.top;
//            int width = activity.getWindowManager().getDefaultDisplay().getWidth();
            int width = b1.getWidth();
//            int height = activity.getWindowManager().getDefaultDisplay().getHeight();//去掉标题栏
            int height = b1.getHeight();//去掉标题栏
            //Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);
            Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height - statusBarHeight - 1);
            view.destroyDrawingCache();
            b1.recycle();
            return b;
        } catch (Exception ex) {
         throw new Exception();
        }
    }
}
