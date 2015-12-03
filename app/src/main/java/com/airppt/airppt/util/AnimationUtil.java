package com.airppt.airppt.util;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

/**
 * Created by yang on 2015/6/8.
 */
public class AnimationUtil {

    public static Animation initTransitionHideAnim() {
        Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                -1.0f);
        animation.setDuration(300);
        return animation;
    }

    public static Animation initTransitionShowAnim() {
        Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF,
                0.0f);
        animation.setDuration(300);
        return animation;
    }

    public static Animation initScaleHideAnimation() {
        Animation animation = new ScaleAnimation(Animation.RELATIVE_TO_SELF,
                1.0f, Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF,
                0.3f);
        animation.setDuration(300);
        return animation;
    }

    public static Animation initScaleShowAnimation() {
        Animation animation = new ScaleAnimation(Animation.RELATIVE_TO_SELF,
                1.0f, Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.7f);
        animation.setDuration(1000);
        return animation;
    }

    public static Animation initScale10To8Animation() {
        Animation animation = new ScaleAnimation(Animation.RELATIVE_TO_SELF,
                0.85f, Animation.RELATIVE_TO_SELF, 0.85f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0f);
        animation.setDuration(700);
//        animation.setFillEnabled(true);
//        animation.setFillAfter(true);
        return animation;
    }

    public static Animation initScale8To10Animation() {
        Animation animation = new ScaleAnimation(Animation.RELATIVE_TO_SELF,
                1.2f, Animation.RELATIVE_TO_SELF, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0f);
        animation.setDuration(1000);
//        animation.setFillAfter(true);
        return animation;
    }

    public static Animation initScale1To12Animation() {
        Animation animation = new ScaleAnimation(Animation.RELATIVE_TO_SELF,
                1.2f, Animation.RELATIVE_TO_SELF, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0f);
        animation.setDuration(700);
        animation.setFillAfter(true);
        return animation;
    }

    public static Animation initAlphaShowAnimation() {
        Animation animation = new AlphaAnimation(0f, 1.0f);
        animation.setDuration(700);
        animation.setFillAfter(true);
        return animation;
    }

    public static Animation initAlphaHideAnimation() {
        Animation animation = new AlphaAnimation(0.7f, 0f);
        animation.setDuration(300);
        animation.setFillAfter(true);
        return animation;
    }
}
