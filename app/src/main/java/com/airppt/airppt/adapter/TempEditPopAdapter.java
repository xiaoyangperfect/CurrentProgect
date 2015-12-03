package com.airppt.airppt.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.airppt.airppt.R;
import com.airppt.airppt.entry.TempFile;
import com.airppt.airppt.listener.RecyclerViewItemClickListener;
import com.airppt.airppt.util.ImageOptUtil;

import java.util.ArrayList;

/**
 * Created by yang on 2015/6/11.
 */
public class TempEditPopAdapter extends RecyclerView.Adapter<TempEditPopAdapter.ViewHolder> {
    private RecyclerViewItemClickListener listener;
    private ArrayList<TempFile> mList;
    private String path;
    private int itemWidth;
    private int itemHight;

    public TempEditPopAdapter(ArrayList list, String url, int width, int hight) {
        this.mList = list;
        this.path = url;
        this.itemWidth = width;
        this.itemHight = hight;
    }

    public void setOnItemClickListener(RecyclerViewItemClickListener itemClickListener){
        this.listener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tempedit_pop_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ViewGroup.LayoutParams params = holder.imageView.getLayoutParams();
        params.width = itemWidth;
        params.height = itemHight;
        holder.imageView.setLayoutParams(params);
//        Log.e("bitmapss", holder.imageView.getLayoutParams().width + " " + holder.imageView.getLayoutParams().height);
//        holder.imageView.setBackgroundResource(R.mipmap.ic_empty);
//        File file = new File(path + "/" + mList.get(position).getPath());
        //这里可以做优化
        Bitmap bitmap = ImageOptUtil.getBitMapByPath(path + "/" + mList.get(position).getPath(), 3);
        holder.imageView.setImageBitmap(bitmap);
        ImageOptUtil.imageLoader.displayImage("file://" + path + "/" + mList.get(position).getPath(), holder.imageView, ImageOptUtil.image_display_options);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView imageView;
        public ViewHolder(View view){
            super(view);
            imageView = (ImageView) view.findViewById(R.id.tempedit_pop_item_img);
            View item = view;
            item.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(itemView, getPosition());
        }
    }
}
