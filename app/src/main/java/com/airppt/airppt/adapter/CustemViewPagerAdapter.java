package com.airppt.airppt.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by yang on 2015/6/23.
 */
public class CustemViewPagerAdapter extends PagerAdapter {

    private ArrayList<View> mList;

    public CustemViewPagerAdapter(ArrayList list) {
        this.mList = list;
    }

    @Override
    public int getCount() {
//        return mList.size();
        if (mList.size() == 0) {
            return 0;
        } else {
            return Integer.MAX_VALUE;
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        try{
            container.addView(mList.get(position % mList.size()));
        } catch (Exception e) {

        }
        return mList.get(position % mList.size());
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        container.removeView(mList.get(position));
        container.removeView(mList.get(position % mList.size()));
    }
}
