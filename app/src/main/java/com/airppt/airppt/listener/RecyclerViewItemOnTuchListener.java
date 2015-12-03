package com.airppt.airppt.listener;

import android.view.MotionEvent;
import android.view.View;

/**
 * Created by yang on 2015/6/30.
 */
public interface RecyclerViewItemOnTuchListener {
    boolean onTuch(View v, MotionEvent event, int position);
}
