package com.airppt.airppt.entry;

import java.io.Serializable;

/**
 * Created by yang on 2015/6/2.
 */
public class WorkStyle implements Serializable {
    private String ShowStyle;
    private float WidthByHeight;

    public String getShowStyle() {
        return ShowStyle;
    }

    public void setShowStyle(String showStyle) {
        ShowStyle = showStyle;
    }

    public float getWidthByHeight() {
        return WidthByHeight;
    }

    public void setWidthByHeight(float widthByHeight) {
        WidthByHeight = widthByHeight;
    }
}
