package com.airppt.airppt.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.airppt.airppt.R;
import com.airppt.airppt.entry.ImageViewState;
import com.airppt.airppt.listener.RecyclerViewItemClickListener;
import com.airppt.airppt.listener.RecyclerViewItemDrapListener;
import com.airppt.airppt.listener.RecyclerViewItemOnTuchListener;
import com.airppt.airppt.util.DPIUtil;
import com.airppt.airppt.util.ImageOptUtil;
import com.airppt.airppt.util.Util;

import java.util.ArrayList;

/**
 * Created by yang on 2015/6/30.
 */
public class SortAdapter extends RecyclerView.Adapter<SortAdapter.ViewHolder> {
    private RecyclerViewItemDrapListener drapListener;
    private RecyclerViewItemClickListener clickListener;
    private RecyclerViewItemOnTuchListener onTuchListener;

    private ArrayList<ImageViewState> mList;
    private int hight;
    private int width;

    public SortAdapter(Activity ac, ArrayList list, float wh) {
        this.mList = list;
        hight = DPIUtil.getScreenMetrics(ac).getHigh() / 6;
        int cut = 0;
        if (wh > 1)
            cut = 80;
        width = Math.round(hight * wh) - cut;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sort_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ViewGroup.LayoutParams params = holder.img.getLayoutParams();
        params.height = hight;
        params.width = width;
        holder.img.setLayoutParams(params);
//        Log.e("image", mList.get(position).getPath());
//        ImageOptUtil.imageLoader.displayImage(mList.get(position).getPath(), holder.img, ImageOptUtil.image_display_options_no_cache);
        Bitmap bitmap = ImageOptUtil.getBitMapByPath(mList.get(position).getPath(), 6);
        holder.img.setImageBitmap(bitmap);
        if (mList.get(position).isState()) {
            holder.layout.setBackgroundResource(android.R.color.holo_purple);
        } else {
            holder.layout.setBackgroundResource(android.R.color.background_light);
        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setOnTuchListener(RecyclerViewItemOnTuchListener listener) {
        this.onTuchListener = listener;
    }

    public void setDrapListener(RecyclerViewItemDrapListener listener) {
        this.drapListener = listener;
    }

    public void setClickListener(RecyclerViewItemClickListener listener) {
        this.clickListener = listener;
    }

    public void setList(ArrayList list) {
        this.mList = list;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener, View.OnDragListener, View.OnClickListener {

        private RelativeLayout layout;
        private ImageView img;

        public ViewHolder(View itemView) {
            super(itemView);

            layout = (RelativeLayout) itemView.findViewById(R.id.item_sort_lay);
            img = (ImageView) itemView.findViewById(R.id.item_sort_img);

            itemView.setOnClickListener(this);
            itemView.setOnTouchListener(this);
            itemView.setOnDragListener(this);
        }

        @Override
        public boolean onDrag(View v, DragEvent event) {
            return drapListener.onDrap(v, event, getPosition());
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return onTuchListener.onTuch(v, event, getPosition());
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(v, getPosition());
        }
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        Util.imageRecycle(holder.img);
        super.onViewRecycled(holder);
    }
}
