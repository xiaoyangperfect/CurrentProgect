package com.airppt.airppt.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import com.airppt.airppt.util.ImageOptUtil;
import com.airppt.airppt.view.PhotoGridItem;

import java.util.ArrayList;

/**
 * Created by user on 2015/4/8.
 */
public class AllPhotoShowAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> mList;
    private String path;
    private float wh;

    public AllPhotoShowAdapter(Context context, String path, ArrayList list){
        this.mContext = context;
        this.mList = list;
        this.path = path;
    }

    public AllPhotoShowAdapter(Context context, String path, ArrayList list, float wh){
        this.mContext = context;
        this.mList = list;
        this.path = path;
        this.wh = wh;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PhotoGridItem item;
        if(convertView == null) {
            item = new PhotoGridItem(mContext, wh);
            item.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                    AbsListView.LayoutParams.MATCH_PARENT));
        } else {
            item = (PhotoGridItem) convertView;
        }
//        if(mList.get(position).isSelect()){
//            item.setChecked(true);
//        } else {
//            item.setChecked(false);
//        }
        ImageOptUtil.imageLoader.displayImage("file://" + path + "/" + mList.get(position), item.getImageView(), ImageOptUtil.image_display_options_no_cache);
        return item;
    }

}
