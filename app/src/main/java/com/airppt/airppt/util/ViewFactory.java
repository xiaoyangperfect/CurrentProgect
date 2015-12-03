package com.airppt.airppt.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.airppt.airppt.R;
import com.airppt.airppt.entry.DefaultConfig;
import com.airppt.airppt.entry.Element;
import com.airppt.airppt.entry.Page;
import com.airppt.airppt.entry.WH;

import java.util.ArrayList;

/**
 * Created by yang on 2015/4/21.
 * 这里是同步创建view，后期可更改为异步创建view
 */
public class ViewFactory {

    private static double scaleView4ScreenWidth = 0.8;
    private static double scaleView4ScreenHigh = 0.6;

    private Context mContext;
    private Activity mActivity;

    private DefaultConfig defaultConfig;

    public ViewFactory(Context context, Activity activity) {
        this.mContext = context;
        this.mActivity = activity;
    }

    /**
     *
     * @param gson gson
     * @param config 模板描述json
     * @param paths 选取图片路径列表
     * @return 根据模板生成view列表
     */
    public ArrayList<View> getViewsWithTemp(Gson gson, DefaultConfig config, ArrayList<String> paths) {
        ArrayList<View> views = new ArrayList<>();
        ArrayList<Page> pages = null;
        String[] des = null;
        int index = 0;

//        DefaultConfig config = TemplateParser.parser(gson, inputStream);
        String descrip = config.getDescription();

        if(descrip != null && !descrip.equals("")) {
            des = descrip.split(",");
        } else {
            des = new String[]{"0"};
        }

        if(config != null && config.getPages() != null) {
            pages = config.getPages();
        }
        if(pages !=null && pages.size() > 0) {
            defaultConfig = config;
            views = new ArrayList<>();
            for (int i = 0; i < paths.size(); i++) {
                if(i >= pages.size()) {
                    int pageIndex = 0;
                    try {
                        pageIndex = Integer.parseInt(des[index]);
                    } catch (Exception ex) {
                        pageIndex = 0;
                    }
                    pages.add(pages.get(pageIndex));
                    index = ++index >= des.length?0:index;
                }
                String key = pages.get(i).getElements().get(0).getImage();
                pages.get(i).getJsonContent().replace(key, paths.get(i));
                View childView = createView(pages.get(i), paths.get(i));
                views.add(childView);
            }
            defaultConfig.setPages(pages);
        }
        return views;
    }

    /**
     * @param page  页面实体
     * @param path  页面背景路径
     * @return view 页面view
     */
    public View createView(Page page, String path) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.base_view, null);
        ImageView img = (ImageView) view.findViewById(R.id.base_view_bg);
        //compute view's width and high
        WH wh = getViewWH();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(wh.getWidth(), wh.getHigh());
        view.setLayoutParams(params);
//        ImageOptUtil.imageLoader.displayImage("file://" + path, img, ImageOptUtil.image_display_options);
        img.setImageBitmap(ImageOptUtil.getBitMapByPath(path));
        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.base_view_parent);

        if(page.getElements().size() > 1) {
            for(int i = 1; i < page.getElements().size(); i++) {
                View childView = createElementView(page.getElements().get(i), wh);
                layout.addView(childView);
            }
        }
        return view;
    }

    /**
     *
     * @param element   控件元素实体
     * @param wh    屏幕宽高实体
     * @return 控件view
     */
    private View createElementView(Element element, WH wh) {
        View childView = LayoutInflater.from(mContext).inflate(R.layout.base_element_view, null);

        //compute view's width and high
        float width = wh.getWidth() * element.getConstraints().getWidthByScreenWidth();
        float high = width * element.getConstraints().getHeightByWidth();
        //set view's param
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) width, (int) high);
        //set view's margin
        params.setMargins((int) (wh.getWidth() * element.getConstraints().getLeftPaddingByScreenWidth()),
                (int) (wh.getHigh() * element.getConstraints().getTopPaddingByScreenHeight()), 0, 0);
        childView.setLayoutParams(params);

        ViewHolder viewHolder = new ViewHolder();
        viewHolder.imageView = (ImageView) childView.findViewById(R.id.base_element_view_image);
        viewHolder.textView = (TextView) childView.findViewById(R.id.base_element_view_textview);

        if(element.getImage() != null && !element.getImage().equals("")){

        } else if (element.getBackgroundColor() != null && !element.getBackgroundColor().equals("")) {
            ColorUtil.RGB rgb = ColorUtil.getRGBFromTemp(element.getBackgroundColor());
            if(rgb != null) {
                viewHolder.imageView.setBackgroundColor(Color.rgb(rgb.r, rgb.g, rgb.b));
                viewHolder.imageView.setAlpha(rgb.a);
            }
        } else {
            viewHolder.imageView.setVisibility(View.GONE);
        }

        if(element.getType().equals("content")) {
            viewHolder.textView.setText(element.getContent().getValue());
            if(element.getContent().getFontSize() != 0)
                viewHolder.textView.setTextSize(element.getContent().getFontSize());
            if(element.getContent().getMaxCount() != 0)
                viewHolder.textView.setMaxEms(element.getContent().getMaxCount());
            ColorUtil.RGB rgb = ColorUtil.getRGBFromTemp(element.getContent().getFontColor());
            if(rgb != null) {
                viewHolder.textView.setTextColor(Color.rgb(rgb.r, rgb.g, rgb.b));
                viewHolder.textView.setAlpha(rgb.a);
            }
        } else {
            viewHolder.textView.setVisibility(View.GONE);
        }

        return childView;
    }

    static class ViewHolder {
        public ImageView imageView;
        public TextView textView;
    }

    /**
     *
     * @return  获取view宽高
     */
    public WH getViewWH() {
        WH wh = new WH();
        DisplayMetrics metrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        wh.setWidth((int)(metrics.widthPixels* scaleView4ScreenWidth));
        wh.setHigh((int) (metrics.widthPixels* scaleView4ScreenWidth * 486 / 320));
        return wh;
    }

    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }


    public String getJsonContent() {
        String des = "";
        if(defaultConfig != null) {
            for(int i = 0; i < defaultConfig.getPages().size(); i++) {
                des = des + "," + defaultConfig.getPages().get(i).getJsonContent();
            }
            des = des.substring(1);
            return defaultConfig.getJsonContent().replace("{1}", des);
        }
        return null;
    }
}
