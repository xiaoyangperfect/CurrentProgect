package com.airppt.airppt.activity.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airppt.airppt.activity.TempEditV4Activity;
import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.airppt.airppt.MainActivity;
import com.airppt.airppt.R;
import com.airppt.airppt.activity.ShareActivity;
import com.airppt.airppt.adapter.CustemViewPagerAdapter;
import com.airppt.airppt.adapter.GridViewAdapter;
import com.airppt.airppt.entry.FeedBack;
import com.airppt.airppt.entry.HomeDataEntry;
import com.airppt.airppt.entry.HotWorkData;
import com.airppt.airppt.entry.Queue;
import com.airppt.airppt.entry.TempEntry;
import com.airppt.airppt.entry.WorksEntry;
import com.airppt.airppt.listener.RecyclerViewItemClickListener;
import com.airppt.airppt.util.AnimationUtil;
import com.airppt.airppt.util.AsyncHttpResponseCallBack;
import com.airppt.airppt.util.CustomWebViewClient;
import com.airppt.airppt.util.DPIUtil;
import com.airppt.airppt.util.FastBlur;
import com.airppt.airppt.util.FileUtil;
import com.airppt.airppt.util.HttpConfig;
import com.airppt.airppt.util.ImageOptUtil;
import com.airppt.airppt.util.RequestParam;
import com.airppt.airppt.util.ScreenShortCutUtil;
import com.airppt.airppt.util.SharedPreferenceUtil;
import com.airppt.airppt.util.TempEditUtil;
import com.airppt.airppt.util.Util;
import com.airppt.airppt.view.CircleDialogProgressBar;
import com.airppt.airppt.view.CustomRecyclerView;
import com.airppt.airppt.view.CustomStaggeredGridLayoutManager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

/**
 * Created by yang on 2015/5/29.
 */
public class FragmentMain extends Fragment {
    private View layView;
    private CustomRecyclerView mRecyclerView;
    private SwipeRefreshLayout refreshLayout;
    private ContentLoadingProgressBar progressBar_bottom;
    private CircleDialogProgressBar circleBar;
    private ViewPager viewPager;
    private View headView;
    private LinearLayout pointLayout;

    private ArrayList<WorksEntry> mList;
    private GridViewAdapter adapter;
    private String url;
    private String urlFeedBack;
    private AsyncHttpClient client;
    private Gson gson;
    //瀑布流中展示图片的宽度
    private float imgWidth;
    //recyclerview 样式
    private CustomStaggeredGridLayoutManager layoutManager;
    //recyclerview endless need params
    private int[] firstVisiableItemPositions;
    private int previousTotal = 0;
    private boolean loading = true;
    private int firstVisibleItem, visibleItemCount, totalItemCount;
    //模板分类
    private String templateTag;
    private TempEntry tempEntry;
    private WorksEntry worksEntry;
    private HotWorkData hotWork;
    private HomeDataEntry homeData;

    private CustemViewPagerAdapter custemViewPagerAdapter;
    private ArrayList<View> mViewList;
    private int preIndex;
//    private ImageLoader imageLoader;
    private int LID;
    private String userId;
    private String storeUrl;
    private File backUpFile;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                    break;
                case 1:
                    getViewpager();
                    custemViewPagerAdapter.notifyDataSetChanged();
                    adapter.notifyDataSetChanged();
                    if (circleBar.isShowing()) {
                        circleBar.dismiss();
                    }
                    startViewPager();
                    break;
            }

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(layView == null) {
            initView(inflater);
            initData();
            getDataView();
//            ImageOptUtil.getAibums(getActivity());
        }
        return layView;
    }
    private void initView(LayoutInflater inflater) {
        layView = inflater.inflate(R.layout.fragment_main, null);
        mRecyclerView = (CustomRecyclerView) layView.findViewById(R.id.main_recycler_view);
//        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager = new CustomStaggeredGridLayoutManager(2, CustomStaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        refreshLayout = (SwipeRefreshLayout) layView.findViewById(R.id.swipe_container);
        progressBar_bottom = (ContentLoadingProgressBar) layView.findViewById(R.id.main_progressBar_bottom);
        headView = inflater.inflate(R.layout.recycler_headview, null);
        viewPager = (ViewPager) headView.findViewById(R.id.activity_main_custemviewpager);
        pointLayout = (LinearLayout) headView.findViewById(R.id.mainpager_pointcontainer);
        circleBar = CircleDialogProgressBar.createCircleDialogProgressBar(getActivity());
        circleBar.show();

    }

    private void initData() {
        userId = SharedPreferenceUtil.getAccountSharedPreference(getActivity()).getString(SharedPreferenceUtil.USERID, "");
        templateTag = "";
        LID = 1;
        gson = new Gson();
        client = new AsyncHttpClient();
        url = HttpConfig.getGetWorks(userId);
        DPIUtil.getScreenMetrics(getActivity());
        float windowWidth = DPIUtil.screen_width;
        float paddingWidth = DPIUtil.dip2px(getActivity(), 5);
        imgWidth = windowWidth / 2 - paddingWidth;

        mList = new ArrayList<>();
        mViewList = new ArrayList<>();
        backUpFile = new File(FileUtil.TEXT_BACKUP);
        if (backUpFile.exists()) {
            String json = TempEditUtil.getJsonFromFile(FileUtil.TEXT_BACKUP);
            if (json != null) {
                try {
                    Type type = new TypeToken<HomeDataEntry>(){}
                            .getType();
                    homeData = gson.fromJson(json, type);
                    hotWork = homeData.getData().getHotWorkTagList();
                    mList.addAll(homeData.getData().getWorks().getWorks());
                    getViewpager();
                } catch (Exception e) {

                }

            }
        }

        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.theme_accent));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                firstVisibleItem = 0;
                visibleItemCount = 0;
                totalItemCount = 0;
                previousTotal = 0;
                LID = 1;
                mList.clear();
                getListData();
            }
        });
        firstVisiableItemPositions = new int[2];

    }

    private void getDataView() {
        float imgWidths = DPIUtil.screen_width;
        float imgHeight = imgWidths * 284 / 716;
        ViewGroup.LayoutParams params = viewPager.getLayoutParams();
        params.height = Math.round(imgHeight);
        params.width = Math.round(imgWidths);
        viewPager.setLayoutParams(params);
        custemViewPagerAdapter = new CustemViewPagerAdapter(mViewList);
        viewPager.setAdapter(custemViewPagerAdapter);

        mRecyclerView.addHeaderView(headView);
        adapter = new GridViewAdapter(getActivity(), mList, imgWidth);
        mRecyclerView.setAdapter(adapter);
        setListener();
        initPopupWindow();
        getHomeData();
    }

    private void getHomeData() {
        RequestParams params = RequestParam.getRequestParams(getActivity());
        params.put("pageCount", 20);
        client.post(HttpConfig.getGetHomelist(userId), params, new AsyncHttpResponseCallBack(getActivity()) {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                super.onSuccess(i, headers, bytes);
                try {
                    Log.e("hot", new String(bytes));
                    Type type = new TypeToken<HomeDataEntry>(){}
                            .getType();
                    homeData = gson.fromJson(new String(bytes), type);
                    storeUrl = homeData.getData().getHost();
                    SharedPreferenceUtil.getSharedEditor(getActivity()).putString(
                            SharedPreferenceUtil.HOST, storeUrl
                    ).commit();
                    SharedPreferenceUtil.getSharedEditor(getActivity()).putString(
                            SharedPreferenceUtil.SEARCH_WORD, homeData.getData().getSearchWord()
                    ).commit();
                    FileUtil.writeData(FileUtil.TEXT_BACKUP, new String(bytes));
                    hotWork = homeData.getData().getHotWorkTagList();
                    mList.clear();
                    mList.addAll(homeData.getData().getWorks().getWorks());
                    handler.sendEmptyMessage(1);
                } catch (Exception e) {

                }

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }

    private void getViewpager() {
        if (hotWork.getHotTags().size() < 3)
            return;

        mViewList.clear();
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View v1 = inflater.inflate(R.layout.item_homepage_showview_01, null);
        ImageView img1 = (ImageView) v1.findViewById(R.id.mainpage_img_01);
        ImageOptUtil.imageLoader.displayImage(hotWork.getHotTags().get(0).getCoverUrl(),
                img1, ImageOptUtil.image_display_options);
        mViewList.add(v1);
        v1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circleBar.show();
                firstVisibleItem = 0;
                visibleItemCount = 0;
                totalItemCount = 0;
                previousTotal = 0;
                mList.clear();
                LID = hotWork.getHotTags().get(0).getLID();
                Map<String, String> map = new HashMap<String, String>();
                map.put("Hot_lid", LID + "");
                FlurryAgent.logEvent("HotListShow", map);
                getListData();
            }
        });
        View v2 = inflater.inflate(R.layout.item_homepage_showview_02, null);
        ImageView img2 = (ImageView) v2.findViewById(R.id.mainpage_img_02);
        ImageOptUtil.imageLoader.displayImage(hotWork.getHotTags().get(1).getCoverUrl(),
                img2, ImageOptUtil.image_display_options);
        mViewList.add(v2);
        v2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circleBar.show();
                firstVisibleItem = 0;
                visibleItemCount = 0;
                totalItemCount = 0;
                previousTotal = 0;
                mList.clear();
                LID = hotWork.getHotTags().get(1).getLID();
                Map<String, String> map = new HashMap<String, String>();
                map.put("Hot_lid", LID + "");
                FlurryAgent.logEvent("HotListShow", map);
                getListData();
            }
        });
        View v3 = inflater.inflate(R.layout.item_homepage_showview_03, null);
        ImageView img3 = (ImageView) v3.findViewById(R.id.mainpage_img_03);
        ImageOptUtil.imageLoader.displayImage(hotWork.getHotTags().get(2).getCoverUrl(),
                img3, ImageOptUtil.image_display_options);
        mViewList.add(v3);
        v3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circleBar.show();
                firstVisibleItem = 0;
                visibleItemCount = 0;
                totalItemCount = 0;
                previousTotal = 0;
                mList.clear();
                LID = hotWork.getHotTags().get(2).getLID();
                Map<String, String> map = new HashMap<String, String>();
                map.put("Hot_lid", LID + "");
                FlurryAgent.logEvent("HotListShow", map);
                getListData();
            }
        });

        pointLayout.removeAllViews();
        for (int i = 0; i < mViewList.size(); i++) {
            imageView = new ImageView(getActivity());
            LinearLayout.LayoutParams paramsP = new LinearLayout.LayoutParams(10, 10);
            paramsP.setMargins(10, 0, 10, 0);
            imageView.setLayoutParams(paramsP);
            imageView.setEnabled(false);
            imageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.point_background));
            pointLayout.addView(imageView);
        }
        pointLayout.getChildAt(0).setEnabled(true);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pointLayout.getChildAt(preIndex % 3).setEnabled(false);
                pointLayout.getChildAt(position % 3).setEnabled(true);
                preIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void getListView() {
        adapter.notifyDataSetChanged();
        if (circleBar.isShowing()) {
            circleBar.dismiss();
        }
        refreshLayout.setRefreshing(false);
        progressBar_bottom.setVisibility(View.GONE);
    }

    private void getListData() {
        RequestParams params = RequestParam.getRequestParams(getActivity());
        String workId = "";
        String lastId = "";
        String lastData = "";
        if (mList.size() != 0 && tempEntry != null) {
            workId = tempEntry.getData().getLastRank();
            lastId = tempEntry.getData().getLastWorkId();
            lastData = tempEntry.getData().getLastDate();
        } else {
            if(mList.size() != 0 && homeData != null) {
                workId = homeData.getData().getWorks().getLastRank();
                lastId = homeData.getData().getWorks().getLastWorkId();
                lastData = homeData.getData().getWorks().getLastDate();
            }
        }

        Log.e("work", workId);
        Queue queue = new Queue();
        queue.setWorksId("");
        queue.setLastRank(workId);
        queue.setLastWorkId(lastId);
        queue.setLastDate(lastData);
        queue.setText("");
        queue.setUserId("");
        queue.setUserId("-1");
        queue.setTemplateId("");
        queue.setTemplateTag("");
        queue.setHotPoint(-1);
        queue.setHotTagId(LID);
        queue.setDateNow("");
        queue.setTemplateFlag(-1);
        queue.setStatus("");
        String query = gson.toJson(queue);
        params.put("query", query);
        params.put("pageCount", 10);
        Log.e("param", params.toString());
        client.post(url, params, new AsyncHttpResponseCallBack(getActivity()) {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                super.onSuccess(i, headers, bytes);
                Log.e("json", new String(bytes));
                Type type = new TypeToken<TempEntry>() {
                }.getType();
                tempEntry = new TempEntry();
                try {
                    tempEntry = gson.fromJson(new String(bytes), type);
                    mList.addAll(tempEntry.getData().getWorks());
//                adapter.setmList(mList);
                    if (mList.size() > tempEntry.getData().getWorks().size()) {
                        adapter.notifyItemRangeInserted(mList.size()-tempEntry.getData().getWorks().size() + 1, tempEntry.getData().getWorks().size());
                    } else {
                        adapter.notifyDataSetChanged();
                    }

//
                } catch (Exception ex) {
                }


                if (circleBar.isShowing()) {
                    circleBar.dismiss();
                }
                refreshLayout.setRefreshing(false);
                progressBar_bottom.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (circleBar.isShowing()) {
                    circleBar.dismiss();
                }
                refreshLayout.setRefreshing(false);
                progressBar_bottom.setVisibility(View.GONE);
            }
        });
    }

    private int clickItemIndex;
    private void setListener() {
        adapter.setOnItemClickListener(new RecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                if (SharedPreferenceUtil.getSharedPreference(getActivity()).getString(
//                        SharedPreferenceUtil.MOD1, "0"
//                ).equals("0")) {
//                    ((MainActivity)getActivity()).showMod1();
//                } else {
//
//                }
                ((MainActivity) getActivity()).showCreateToast();
                showPopWindow(position-1);
                worksEntry = mList.get(position-1);
                clickItemIndex = position-1;
            }
        });

        //上拉加载
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        break;
                }
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                firstVisibleItem = layoutManager.findFirstVisibleItemPositions(firstVisiableItemPositions)[0];
                visibleItemCount = mRecyclerView.getChildCount();
                totalItemCount = layoutManager.getItemCount();
                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem)) {
                    // End has been reached
                    progressBar_bottom.setVisibility(View.VISIBLE);
                    getListData();
                    // Do something
                    loading = true;
                }
            }
        });
    }

    private void startViewPager() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        };

        Timer timer = new Timer();
        timer.schedule(task, 5000, 5000);
    }

//    /********** 实现列表随机选中的item中的图片每隔一定时间更换一次 ***********/
    ImageView imageView;
//    private void changeItemBgImage() {
//        int index = firstVisibleItem + new Random().nextInt(5);
//        View view = layoutManager.getChildAt(index);
//        imageView = (ImageView) view.findViewById(R.id.item_base_view_img);
//        String url = HttpConfig.BASE_URL_STORE + mList.get(index).getThumbnailImages().get(1).getPath();
//        ImageRequest imageRequest = new ImageRequest(url,
//                new Response.Listener<Bitmap>() {
//                    @Override
//                    public void onResponse(Bitmap response) {
//                        imageView.setImageBitmap(response);
//                    }
//                }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                imageView.setImageResource(R.mipmap.ic_error);
//            }
//        });
////        mQueue.add(imageRequest);
////        mQueue.start();
//    }
//
//    Timer timer = new Timer();
//
//    TimerTask changeImageTask = new TimerTask() {
//        @Override
//        public void run() {
//            handler.sendEmptyMessage(0);
//        }
//    };
//
//    Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            changeItemBgImage();
//        }
//    };


    /******弹出框*******/
    private WebView mWebView;
    private TextView popTitle, popAuthor;
    private Button popHotPoint, popEdit, popShare;
    private View popView;
    private PopupWindow popupWindow;
    private RelativeLayout popContainer;
    private Animation animation;
    private float screenWByH;
    float baseWidth;

    /**
     * 初始化窗口的基本信息，声明所需基本变量，避免重复创建
     */
    private void initPopupWindow() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        popView = inflater.inflate(R.layout.main_popupwindow_layout, null);
        mWebView = (WebView) popView.findViewById(R.id.main_pop_webview);
        popTitle = (TextView) popView.findViewById(R.id.main_pop_title);
        popAuthor = (TextView) popView.findViewById(R.id.main_pop_author);
        popHotPoint = (Button) popView.findViewById(R.id.main_pop_hotpoint);
        popEdit = (Button) popView.findViewById(R.id.main_pop_toedit);
        popShare = (Button) popView.findViewById(R.id.main_pop_share);
        popContainer = (RelativeLayout) popView.findViewById(R.id.main_pop_container);

        animation = AnimationUtil.initAlphaShowAnimation();
        screenWByH = DPIUtil.getWidthByHight();

        baseWidth = imgWidth * 2 - DPIUtil.dip2px(getActivity(), 80);

        mWebView.setWebViewClient(new CustomWebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
//        mWebView.setWebChromeClient(new WebChromeClient());
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        //扩大比例的缩放
        mWebView.getSettings().setUseWideViewPort(true);
        //自适应屏幕
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWebView.getSettings().setLoadWithOverviewMode(true);

        popupWindow = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, false);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setAnimationStyle(R.style.popupwindowStyle);

        initPopListener();
    }

    /**
     * 显示窗口，重新加载url，重新定义窗口大小，设置背景透明度
     * @param position 点击列表位置
     */
    private void showPopWindow(int position) {
        try {
            Bitmap screenBitmap = ScreenShortCutUtil.getShortScreen(getActivity());
            if (screenBitmap != null) {
                FastBlur.blur(getActivity(), screenBitmap, popView);
                screenBitmap.recycle();
            }

        } catch (Exception ex) {

        }

        int baseHight = 0;
        baseHight = Math.round(baseWidth / screenWByH);
        if (mList.get(position).getIsDianZan() == 0) {
            popHotPoint.setBackgroundResource(R.mipmap.love);
        } else {
            popHotPoint.setBackgroundResource(R.mipmap.loved);
        }
        ViewGroup.LayoutParams params = popContainer.getLayoutParams();
        params.width = Math.round(baseWidth);
        params.height = baseHight;
        popContainer.setLayoutParams(params);
        ViewGroup.LayoutParams layoutParams = mWebView.getLayoutParams();
        layoutParams.width = Math.round(baseWidth);
        layoutParams.height = baseHight;
        mWebView.setLayoutParams(layoutParams);
//        mWebView.setVisibility(View.GONE);

        mWebView.loadUrl(mList.get(position).getShowPath());

        popTitle.setText(mList.get(position).getTitle());
        popAuthor.setText(mList.get(position).getAuthor().getNickname());
        popupWindow.showAtLocation(layView, Gravity.CENTER, 0, 0);
    }

    /**
     * popupwindow监听事件注册
     */
    private void initPopListener() {
        popEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Util.isStringNotEmpty(userId)) {
                    Util.toLogin(getActivity());
                } else {
                    Intent intent = new Intent(getActivity(), TempEditV4Activity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("entry", worksEntry);
                    intent.putExtras(bundle);
                    intent.putExtra("from", "create");
                    startActivity(intent);
                    bundle.clear();
                    popupWindow.dismiss();
                }

            }
        });
        popView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        popHotPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Util.isStringNotEmpty(userId)) {
                    Util.toLogin(getActivity());
                } else {
                    userFeedBack(worksEntry.getWorksId());
                }

            }
        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mWebView.reload();
            }
        });

        popShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Util.isStringNotEmpty(userId)) {
                    Util.toLogin(getActivity());
                } else {
                    Intent intent = new Intent(getActivity(), ShareActivity.class);
                    intent.putExtra("shareUrl", worksEntry.getShowPath());
                    intent.putExtra("imgUrl", SharedPreferenceUtil.getSharedPreference(getActivity()).getString(
                            SharedPreferenceUtil.HOST, HttpConfig.BASE_URL
                    ) + worksEntry.getWXShareImg());
                    intent.putExtra("title", worksEntry.getTitle());
                    intent.putExtra("workId", worksEntry.getWorksId());
                    startActivity(intent);
                    popupWindow.dismiss();
                }

            }
        });
    }


    private void userFeedBack(String workId) {
        urlFeedBack = HttpConfig.FEEDBACK;
        RequestParams params = new RequestParams();
        FeedBack feedBack = new FeedBack();
        if (worksEntry.getIsDianZan() == 0) {
            feedBack.setTag("1");
            popHotPoint.setBackgroundResource(R.mipmap.loved);
            mList.get(clickItemIndex).setIsDianZan(1);
            mList.get(clickItemIndex).setFeedBackCount(mList.get(clickItemIndex).getFeedBackCount() + 1);
            adapter.notifyDataSetChanged();
        } else {
            feedBack.setTag("0");
            popHotPoint.setBackgroundResource(R.mipmap.love);
            mList.get(clickItemIndex).setIsDianZan(0);
            int num = mList.get(clickItemIndex).getFeedBackCount() - 1;
            mList.get(clickItemIndex).setFeedBackCount(num < 0? 0 : num);
            adapter.notifyDataSetChanged();
        }
        feedBack.setFeedBackUserId(userId);
        feedBack.setWorksId(workId);
        String feed = gson.toJson(feedBack);
        params.put("feedBackLog", feed);

        client.post(urlFeedBack, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                Log.e("feedback", new String(bytes));
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroy();
        ViewGroup group = (ViewGroup) layView.getParent();
        if(group != null) {
            group.removeView(layView);
        }
    }
}
