package com.airppt.airppt.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;

import com.airppt.airppt.R;

/**
 * Created by user on 2015/8/28.
 */
public class CircleDialogProgressBar extends Dialog {
    public CircleDialogProgressBar(Context context) {
        super(context);
    }

    public CircleDialogProgressBar(Context context, int theme) {
        super(context, theme);
    }

    protected CircleDialogProgressBar(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progressbar_circle);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getWindow().setWindowAnimations(R.style.popupwindowStyle);
    }

    public static CircleDialogProgressBar createCircleDialogProgressBar(Context context) {
        return new CircleDialogProgressBar(context, R.style.loading_dialog);
    }
}
