package com.airppt.airppt.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.airppt.airppt.R;
import com.airppt.airppt.entry.PhotoFilter;
import com.airppt.airppt.listener.RecyclerViewItemClickListener;

import java.util.ArrayList;

import jp.co.cyberagent.android.gpuimage.GPUImage;

/**
 * Created by user on 2015/8/24.
 */
public class PhotoFilterAdapter extends RecyclerView.Adapter<PhotoFilterAdapter.ViewHolder> {

    private ArrayList<PhotoFilter> mList;
    private Bitmap bitmap;
    private GPUImage mGPUImage;
    private RecyclerViewItemClickListener onClickListener;

    public PhotoFilterAdapter(ArrayList<PhotoFilter> filters, Bitmap bitmap, GPUImage image) {
        this.mList = filters;
        this.bitmap = bitmap;
        this.mGPUImage = image;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photofilter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position == 0) {
            holder.img.setImageBitmap(bitmap);
            holder.tv.setText("原图");
        } else {
            holder.tv.setText(mList.get(position).getName());
            mGPUImage.setFilter(mList.get(position).getFilter());
            holder.img.setImageBitmap(mGPUImage.getBitmapWithFilterApplied(bitmap));
        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setOnClickListener(RecyclerViewItemClickListener listener) {
        this.onClickListener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView img;
        TextView tv;

        public ViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.item_photofilter_img);
            tv = (TextView) itemView.findViewById(R.id.item_photofilter_tv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onClickListener.onItemClick(v, getPosition());
        }
    }
}
