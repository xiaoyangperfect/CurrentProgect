package com.airppt.airppt.view;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;

import com.airppt.airppt.adapter.GridViewAdapter;
import com.airppt.airppt.listener.RecyclerViewItemClickListener;

/**
 * 自定义Recyclerview用于瀑布流可添加头部
 * Created by user on 2015/8/28.
 */
public class CustomRecyclerView extends RecyclerView {

    // protected final String TAG = getClass().getSimpleName();
    private View mHeaderView = null;

    private View mFooterView = null;

    public CustomRecyclerView(Context context) {
        super(context);
    }

    public CustomRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
        CustomStaggeredGridLayoutManager manager = (CustomStaggeredGridLayoutManager) layout;
        manager.setSpanSizeLookup(new StaggeredGridSpanSizeLookup(manager.getSpanCount()));
    }


    private class StaggeredGridSpanSizeLookup extends GridLayoutManager.SpanSizeLookup{
        private int mSpanSize = 1;

        public StaggeredGridSpanSizeLookup(int spanSize) {
            this.mSpanSize = spanSize;
        }

        @Override
        public int getSpanSize(int position) {
            GridViewAdapter adapter = (GridViewAdapter) getAdapter();
            if (adapter.getItemViewType(position) == GridViewAdapter.VIEW_TYPES.HEADER ||
                    adapter.getItemViewType(position) == GridViewAdapter.VIEW_TYPES.FOOTER) {
                return mSpanSize;
            }
            return 1;
        }
    }

    /**
     * Set the header view of the adapter.
     */
    public void addHeaderView(View headerView) {
        mHeaderView = headerView;
    }

    /**
     * @return recycle的头部视图
     */
    public View getHeaderView() {
        return mHeaderView;
    }

    /**
     * 设置底部的视图
     */
    public void addFooterView(View footerView) {
        mFooterView = footerView;
    }

    /**
     * 得到底部的视图
     */
    public View getFooterView() {
        return mFooterView;
    }

    /**
     * 需要在设置头、低、监听器之后再调用setAdapter(Adapter adapter)来设置适配器
     */
    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        if (adapter instanceof GridViewAdapter) {
            ((GridViewAdapter) adapter).listener = (RecyclerViewItemClickListener) mOnItemClickListener;
//            ((GridViewAdapter) adapter).mOnItemLongClickListener = mOnItemLongClickListener;
            ((GridViewAdapter) adapter).customHeaderView = mHeaderView;
            ((GridViewAdapter) adapter).customFooterView = mFooterView;
        }
    }

    /**
     * 平滑滚动到某个位置
     *
     * @param isAbsolute position是否是绝对的，如果是绝对的，那么header的位置就是0
     *                   如果是相对的，那么position就是相对内容的list的位置
     */
    public void smoothScrollToPosition(int position, boolean isAbsolute) {
        if (!isAbsolute && mHeaderView != null) {
            position++;
        }
        smoothScrollToPosition(position);
    }

    /**
     * 设置item的点击事件
     */
    private static AdapterView.OnItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    /**
     * 设置item的长按事件
     */
    public static AdapterView.OnItemLongClickListener mOnItemLongClickListener = null;

    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener listener) {
        mOnItemLongClickListener = listener;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        return super.onInterceptTouchEvent(e);
    }
}
