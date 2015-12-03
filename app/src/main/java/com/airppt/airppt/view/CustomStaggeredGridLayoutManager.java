package com.airppt.airppt.view;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

/**
 * Created by user on 2015/8/28.
 */
public class CustomStaggeredGridLayoutManager extends StaggeredGridLayoutManager {

    private GridLayoutManager.SpanSizeLookup mSpanSizeLookup;


    /**
     * Creates a StaggeredGridLayoutManager with given parameters.
     *
     * @param spanCount   If orientation is vertical, spanCount is number of columns. If
     *                    orientation is horizontal, spanCount is number of rows.
     * @param orientation {@link #VERTICAL} or {@link #HORIZONTAL}
     */
    public CustomStaggeredGridLayoutManager(int spanCount, int orientation) {
        super(spanCount, orientation);
    }

    @Override
    public void onDetachedFromWindow(RecyclerView view, RecyclerView.Recycler recycler) {
        super.onDetachedFromWindow(view, recycler);
    }

    /**
     * 设置某个位置的item的跨列程度，这里和GridLayoutManager有点不一样，
     * 如果你设置某个位置的item的span>1了，那么这个item会占据所有列
     *
     * @param spanSizeLookup instance to be used to query number of spans
     *                       occupied by each item
     */
    public void setSpanSizeLookup(GridLayoutManager.SpanSizeLookup spanSizeLookup) {
        mSpanSizeLookup = spanSizeLookup;
    }

    /**
     * Returns the current used by the GridLayoutManager.
     *
     * @return The current used by the GridLayoutManager.
     */
    public GridLayoutManager.SpanSizeLookup getSpanSizeLookup() {
        return mSpanSizeLookup;
    }

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        //Log.d(TAG, "item count = " + getItemCount());
        for (int i = 0; i < getItemCount(); i++) {

            if (mSpanSizeLookup.getSpanSize(i) > 1) {
                //Log.d(TAG, "lookup > 1 = " + i);
                try {
                    //fix 动态添加时报IndexOutOfBoundsException
                    View view = recycler.getViewForPosition(i);
                    if (view != null) {
                        /**
                         *占用所有的列
                         * @see https://plus.google.com/+EtienneLawlor/posts/c5T7fu9ujqi
                         */
                        StaggeredGridLayoutManager.LayoutParams lp = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
                        lp.setFullSpan(true);
                    }
                    // recycler.recycleView(view);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        super.onMeasure(recycler, state, widthSpec, heightSpec);
    }
}
