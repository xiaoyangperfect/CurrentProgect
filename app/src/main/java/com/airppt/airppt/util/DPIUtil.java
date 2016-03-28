package com.airppt.airppt.util;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

import com.airppt.airppt.entry.WH;

/**
 * 调用屏幕宽高之前必须先调用getScreenMetrics
 */
public class DPIUtil {

	private static float mDensity = DisplayMetrics.DENSITY_DEFAULT;
	public static float screen_width;
	public static float screen_height;

	public static void setDensity(float density) {
		mDensity = density;
	}
	public static float getDensity() {
		return mDensity;
	}

	public static int dip2px(Context context, float dipValue) {
		mDensity = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (dipValue * mDensity + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		return (int) (pxValue / mDensity + 0.5f);
	}
	
	public static int px2sp(Context context, float pxValue) {  
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;  
        return (int) (pxValue / fontScale + 0.5f);  
    }
	/**
	 * 获取屏幕宽高 这是个别扭的方法; 每次看到都蛋疼
	 * @param act
	 */
	public static WH getScreenMetrics(Activity act){
		DisplayMetrics dm = new DisplayMetrics();
		act.getWindowManager().getDefaultDisplay().getMetrics(dm);
		screen_width = dm.widthPixels;
		screen_height = dm.heightPixels - dip2px(act, 24f);
		WH wh = new WH();
		wh.setWidth((int) screen_width);
		wh.setHigh((int) screen_height);
		return wh;
	}

	public static float getWidthByHight() {
		return screen_width / screen_height;
	}

}
