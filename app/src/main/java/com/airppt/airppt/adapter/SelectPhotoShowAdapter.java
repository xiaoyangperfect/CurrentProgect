package com.airppt.airppt.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.airppt.airppt.R;
import com.airppt.airppt.listener.RecyclerViewItemClickListener;
import com.airppt.airppt.util.ImageOptUtil;

import java.util.ArrayList;

/**
 * Created by user on 2015/4/8.
 */
public class SelectPhotoShowAdapter extends RecyclerView.Adapter<SelectPhotoShowAdapter.ViewHolder>{

    private ArrayList<String> mList;
    private RecyclerViewItemClickListener listener;

    public SelectPhotoShowAdapter(ArrayList list){
        this.mList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_photo_show, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageOptUtil.imageLoader.displayImage("file://" + mList.get(position), holder.imageView, ImageOptUtil.image_display_options);
    }

    public void setOnItemClickListener(RecyclerViewItemClickListener itemClickListener){
        this.listener = itemClickListener;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView imageView;
        public ViewHolder(View view){
            super(view);
            imageView = (ImageView) view.findViewById(R.id.select_photo_show_imageview);
            View item = view;
            item.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(itemView, getPosition());
        }
    }
}
