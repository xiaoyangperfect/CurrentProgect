package com.airppt.airppt.listener;

import android.view.DragEvent;
import android.view.View;

/**
 * Created by yang on 2015/6/30.
 */
public interface RecyclerViewItemDrapListener {
    boolean onDrap(View v, DragEvent event, int position);
}
