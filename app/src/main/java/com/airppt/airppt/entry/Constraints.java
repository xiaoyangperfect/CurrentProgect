package com.airppt.airppt.entry;

/**
 * Created by yang on 2015/4/21.
 */
public class Constraints {
    private float WidthByScreenWidth;
    private float HeightByWidth;
    private float TopPaddingByScreenHeight;
    private float LeftPaddingByScreenWidth;

    public float getWidthByScreenWidth() {
        return WidthByScreenWidth;
    }

    public void setWidthByScreenWidth(float widthByScreenWidth) {
        WidthByScreenWidth = widthByScreenWidth;
    }

    public float getHeightByWidth() {
        return HeightByWidth;
    }

    public void setHeightByWidth(float heightByWidth) {
        HeightByWidth = heightByWidth;
    }

    public float getTopPaddingByScreenHeight() {
        return TopPaddingByScreenHeight;
    }

    public void setTopPaddingByScreenHeight(float topPaddingByScreenHeight) {
        TopPaddingByScreenHeight = topPaddingByScreenHeight;
    }

    public float getLeftPaddingByScreenWidth() {
        return LeftPaddingByScreenWidth;
    }

    public void setLeftPaddingByScreenWidth(float leftPaddingByScreenWidth) {
        LeftPaddingByScreenWidth = leftPaddingByScreenWidth;
    }
}
