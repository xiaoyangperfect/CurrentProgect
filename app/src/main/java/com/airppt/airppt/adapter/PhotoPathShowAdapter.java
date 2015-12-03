package com.airppt.airppt.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.airppt.airppt.R;
import com.airppt.airppt.entry.Aibum;
import com.airppt.airppt.listener.RecyclerViewItemClickListener;
import com.airppt.airppt.util.ImageOptUtil;

import java.util.ArrayList;

/**
 * Created by user on 2015/4/8.
 */
public class PhotoPathShowAdapter extends RecyclerView.Adapter<PhotoPathShowAdapter.ViewHolder> {

    private ArrayList<Aibum> aibums;
    private RecyclerViewItemClickListener listener;

    public PhotoPathShowAdapter(ArrayList<Aibum> list){
        this.aibums = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_path_show, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageOptUtil.imageLoader.displayImage("file://" + aibums.get(position).getPath(), holder.showImageView, ImageOptUtil.image_display_options);
        holder.pathName.setText("");
        holder.photoNum.setText(aibums.get(position).getName() + " (共" + aibums.get(position).getPhotoList().size() + "张)");
    }

    @Override
    public int getItemCount() {
        return aibums.size();
    }

    public void setOnItemClickListener(RecyclerViewItemClickListener itemClickListener){
        this.listener = itemClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView showImageView;
        public TextView pathName;
        public TextView photoNum;
        private View itemView;

        public ViewHolder(View view){
            super(view);
            showImageView = (ImageView) view.findViewById(R.id.item_photo_path_show_showimageview);
            pathName = (TextView) view.findViewById(R.id.item_photo_path_show_pathname);
            photoNum = (TextView) view.findViewById(R.id.item_photo_path_show_photonumber);
            itemView = view;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(itemView, getPosition());
        }
    }
}
