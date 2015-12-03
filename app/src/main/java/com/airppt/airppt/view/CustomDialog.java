package com.airppt.airppt.view;

import android.app.AlertDialog;
import android.content.Context;

/**
 * Created by user on 2015/8/21.
 */
public class CustomDialog extends AlertDialog {
    public CustomDialog(Context context) {
        super(context);
    }

    public CustomDialog(Context context, int theme) {
        super(context, theme);
    }

    protected CustomDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
}
