package com.airppt.airppt.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.airppt.airppt.R;
import com.airppt.airppt.listener.RecyclerViewItemClickListener;

import java.util.ArrayList;

/**
 * Created by yang on 2015/4/22.
 */
public class ChoicedModleAdapter extends RecyclerView.Adapter<ChoicedModleAdapter.ViewHolder> {

    private ArrayList<View> views;
    private RecyclerViewItemClickListener listener;

    public ChoicedModleAdapter(ArrayList<View> list) {
        this.views = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_choice_modle, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.layout.removeAllViews();
        holder.layout.addView(views.get(position));
    }

    @Override
    public int getItemCount() {
        return views.size();
    }

    public void setOnItemClickListener(RecyclerViewItemClickListener itemClickListener){
        this.listener = itemClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public RelativeLayout layout;
        private View view;
        public ViewHolder(View itemView) {
            super(itemView);
            layout = (RelativeLayout) itemView.findViewById(R.id.item_choice_modle_layout);
            view = itemView;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(view, getPosition());
        }
    }
}
