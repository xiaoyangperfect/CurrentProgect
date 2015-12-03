package com.airppt.airppt.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.airppt.airppt.R;
import com.airppt.airppt.entry.WorksEntry;
import com.airppt.airppt.listener.RecyclerViewItemClickListener;
import com.airppt.airppt.util.DPIUtil;
import com.airppt.airppt.util.HttpConfig;
import com.airppt.airppt.util.ImageOptUtil;
import com.airppt.airppt.util.SharedPreferenceUtil;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by yang on 2015/5/28.
 */
public class GridViewAdapter extends RecyclerView.Adapter<GridViewAdapter.ViewHolder> {

    private ArrayList<WorksEntry> mList;
    public RecyclerViewItemClickListener listener;
//    private ImageLoader imageLoader;
//    private ImageLoader.ImageListener imageListener, imageListenerHead;
    private float imgWidth;

    private Random random;
    private int headWidth;
    private Context mContent;
    public View customHeaderView = null;
    public View customFooterView = null;

    private int nowIndex;

    /**
     * view的基本类型，这里只有头/底部/普通，在子类中可以扩展
     */
    public class VIEW_TYPES {

        public static final int HEADER = 7;

        public static final int FOOTER = 8;
    }

    public GridViewAdapter (Context context, ArrayList list, float width) {
        this.mList = list;
        this.imgWidth = width;
        random = new Random();
        mContent = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPES.HEADER && customHeaderView != null) {
            return new ViewHolder(customHeaderView);
        } else if (viewType == VIEW_TYPES.FOOTER && customFooterView != null) {
            return new ViewHolder(customFooterView);
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_base_view, parent, false);
        headWidth = DPIUtil.dip2px(parent.getContext(), 43);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position == 0 && customHeaderView != null){
            nowIndex = 1;
        } else {
            holder.name.setText(mList.get(position-nowIndex).getTitle());
            int id = random.nextInt(5);
            float widbyhig = mList.get(position-nowIndex).getWorkStyle().getWidthByHeight();
            if (widbyhig == 0)
                widbyhig = 1;
            int high = Math.round(imgWidth / widbyhig);
            int width = Math.round(imgWidth);
            ViewGroup.LayoutParams params = holder.imageView.getLayoutParams();
            params.height = high;
            params.width = width;
            holder.imageView.setLayoutParams(params);
//        if (mList.get(position).getIsDianZan() == 0) {
            holder.love.setBackgroundResource(R.mipmap.love_empty);
//        } else {
//            holder.love.setBackgroundResource(R.mipmap.loved);
//        }
            holder.source.setText(mList.get(position-nowIndex).getAuthor().getNickname());
            holder.loveNum.setText(mList.get(position-nowIndex).getFeedBackCount() + "");
            try {
                ImageOptUtil.imageLoader.displayImage(
                        SharedPreferenceUtil.getSharedPreference(mContent).getString(SharedPreferenceUtil.HOST, HttpConfig.BASE_URL_STORE) + mList.get(position - nowIndex).getThumbnailImages().get(0).getPath(),
                        holder.imageView, ImageOptUtil.getImage_display_options(0));
            } catch (Exception e) {

            }


            ImageOptUtil.imageLoader.displayImage(mList.get(position-nowIndex).getAuthor().getHeadimgurl(),
                    holder.head, ImageOptUtil.getImage_display_options(50));
        }

    }

    @Override
    public int getItemCount() {
        int headerOrFooter = 0;
        if (customHeaderView != null) {
            headerOrFooter++;
        }
        return mList.size() + headerOrFooter;
    }

    public void setOnItemClickListener(RecyclerViewItemClickListener itemClickListener) {
        this.listener = itemClickListener;
    }

    public void setmList(ArrayList list) {
        this.mList = list;
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {

        public SimpleViewHolder(View itemView) {
            super(itemView);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView name, loveNum, messageNum;
        private ImageView imageView, head;
        private TextView source;
        private Button love, message;

        public ViewHolder(View itemView) {
            super(itemView);
            if (getPosition() != 0) {
                name = (TextView) itemView.findViewById(R.id.item_base_view_tv_1);
                imageView = (ImageView) itemView.findViewById(R.id.item_base_view_img);
                source = (TextView) itemView.findViewById(R.id.item_base_view_tv_2);
                love = (Button) itemView.findViewById(R.id.item_base_view_love);
                message = (Button) itemView.findViewById(R.id.item_base_view_message);
                loveNum = (TextView) itemView.findViewById(R.id.item_base_view_love_num);
                messageNum = (TextView) itemView.findViewById(R.id.item_base_view_message_num);
                head = (ImageView) itemView.findViewById(R.id.item_base_picture);

                itemView.setOnClickListener(this);
            }

        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(v, getPosition());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (customFooterView != null && position == getItemCount() - 1) {
            return VIEW_TYPES.FOOTER;
        } else if (customHeaderView != null && position == 0) {
            return VIEW_TYPES.HEADER;
        } else {
            if (customHeaderView != null) {
                return super.getItemViewType(position - 1);
            }
            return super.getItemViewType(position);
        }
    }
}
