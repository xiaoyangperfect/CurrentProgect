package com.airppt.airppt.activity.fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gc.materialdesign.views.ProgressBarIndeterminate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.airppt.airppt.R;
import com.airppt.airppt.activity.ShareActivity;
import com.airppt.airppt.activity.TempEditV3Activity;
import com.airppt.airppt.adapter.GridViewAdapter;
import com.airppt.airppt.entry.FeedBack;
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
import com.airppt.airppt.util.HttpConfig;
import com.airppt.airppt.util.RequestParam;
import com.airppt.airppt.util.ScreenShortCutUtil;
import com.airppt.airppt.util.SharedPreferenceUtil;
import com.airppt.airppt.util.Util;
import com.airppt.airppt.view.CircleDialogProgressBar;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by yang on 2015/6/24.
 */
public class FragmentSearch extends Fragment {

    private View mainView;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView mRecyclerView;
    private ProgressBarIndeterminate progressBar_bottom;
    private CircleDialogProgressBar circleBar;
    private EditText editText;
    private Button deletBtn;
    private View image;

    private ArrayList<WorksEntry> mList;
    private GridViewAdapter adapter;
    private String url;
    private String urlFeedBack;
    private AsyncHttpClient client;
    private Gson gson;
    //瀑布流中展示图片的宽度
    private float imgWidth;
    //recyclerview 样式
    private StaggeredGridLayoutManager layoutManager;
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

    //    private ImageLoader imageLoader;
    private int LID;
    private InputMethodManager inputMethodManager;
    private String searchText;
    private Animation anim;

    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mainView == null) {
            mainView = inflater.inflate(R.layout.fragment_search, null);
            initView();
            initData();
            setListener();
            initPopupWindow();
            String search = SharedPreferenceUtil.getSharedPreference(getActivity()).getString(
                    SharedPreferenceUtil.SEARCH_WORD, ""
            );
//            if (Util.isStringNotEmpty(search)) {
            getListData(search);
//            } else {
//                circleBar.dismiss();
//            }

        }
        return mainView;
    }

    private void initView() {
        refreshLayout = (SwipeRefreshLayout) mainView.findViewById(R.id.search_refresh);
        mRecyclerView = (RecyclerView) mainView.findViewById(R.id.search_recycler);
        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        progressBar_bottom = (ProgressBarIndeterminate) mainView.findViewById(R.id.search_progressBar);
        editText = (EditText) mainView.findViewById(R.id.search_edit);
        deletBtn = (Button) mainView.findViewById(R.id.search_delet);
        image = mainView.findViewById(R.id.search_img);
        circleBar = CircleDialogProgressBar.createCircleDialogProgressBar(getActivity());
        circleBar.show();
    }

    private void initData() {
        anim = AnimationUtil.initScaleShowAnimation();
        userId = SharedPreferenceUtil.getAccountSharedPreference(getActivity()).getString(SharedPreferenceUtil.USERID, "");
        templateTag = "";
        LID = -1;
        gson = new Gson();
        client = new AsyncHttpClient();
        url = HttpConfig.getGetWorks(userId);
        DPIUtil.getScreenMetrics(getActivity());
        float windowWidth = DPIUtil.screen_width;
        float paddingWidth = DPIUtil.dip2px(getActivity(), 5);
        imgWidth = windowWidth / 2 - paddingWidth;

        mList = new ArrayList<>();
        adapter = new GridViewAdapter(getActivity(), mList, imgWidth);
        mRecyclerView.setAdapter(adapter);
        searchText = "";

        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.theme_accent));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                firstVisibleItem = 0;
                visibleItemCount = 0;
                totalItemCount = 0;
                previousTotal = 0;
                LID = -1;
                mList.clear();
                getListData(searchText);
            }
        });
        firstVisiableItemPositions = new int[2];
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    private void getListData(String text) {
//        if (!Util.isStringNotEmpty(text)) {
//            if (circleBar.isShowing()) {
//                circleBar.dismiss();
//            }
//            return;
//        }
        RequestParams params = RequestParam.getRequestParams(getActivity());
        String workId = "";
        String lastId = "";
        String lastData = "";
        if (mList.size() != 0 && tempEntry != null) {
            workId = tempEntry.getData().getLastRank();
            lastId = tempEntry.getData().getLastWorkId();
            lastData = tempEntry.getData().getLastDate();
        }

        Log.e("work", workId);
        Queue queue = new Queue();
        queue.setWorksId("");
        queue.setLastRank(workId);
        queue.setLastWorkId(lastId);
        queue.setText(text);
        queue.setUserId("");
        queue.setUserId("-1");
        queue.setTemplateId("");
        queue.setTemplateTag("");
        queue.setHotPoint(-1);
        queue.setHotTagId(LID);
        queue.setDateNow("");
        queue.setLastDate(lastData);
        queue.setTemplateFlag(-1);
        queue.setStatus("inuse");
        String query = gson.toJson(queue);
        params.put("query", query);
        params.put("pageCount", 20);
        Log.e("param", params.toString());
        client.post(url, params, new AsyncHttpResponseCallBack(getActivity()) {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                super.onSuccess(i, headers, bytes);
                Log.e("json", new String(bytes));
                searchText = "";
                Type type = new TypeToken<TempEntry>() {
                }.getType();
                tempEntry = new TempEntry();
                try {
                    tempEntry = gson.fromJson(new String(bytes), type);
                } catch (Exception ex) {
                    Log.e("error", ex.getMessage());
                }

                mList.addAll(tempEntry.getData().getWorks());
//                adapter.setmList(mList);
                if (mList.size() > tempEntry.getData().getWorks().size()) {
                    adapter.notifyItemRangeInserted(mList.size() - tempEntry.getData().getWorks().size() + 1, tempEntry.getData().getWorks().size());
                } else {
                    adapter.notifyDataSetChanged();
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
                showPopWindow(position);
                worksEntry = mList.get(position);
                clickItemIndex = position;
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
                    getListData(searchText);
                    // Do something
                    loading = true;
                }
            }
        });

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    circleBar.show();
                    searchText = editText.getText().toString();
                    image.setVisibility(View.GONE);
                    deletBtn.setBackground(getResources().getDrawable(R.mipmap.bar_search_search_icon));
                    mList.clear();
                    getListData(searchText);
                    editText.setText("");
                    inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                }
                return false;
            }
        });

        deletBtn.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                image.setVisibility(View.GONE);
                deletBtn.setBackground(getResources().getDrawable(R.mipmap.bar_search_search_icon));
                editText.setText("");
                inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            }
        });

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    image.startAnimation(anim);
                    image.setVisibility(View.VISIBLE);
                    deletBtn.setBackground(getResources().getDrawable(R.mipmap.bar_search_delet));
                } else {
                    image.setVisibility(View.GONE);
                    deletBtn.setBackground(getResources().getDrawable(R.mipmap.bar_search_search_icon));
                }
            }
        });

        editText.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                image.setVisibility(View.VISIBLE);
                image.startAnimation(anim);
                deletBtn.setBackground(getResources().getDrawable(R.mipmap.bar_search_delet));
            }
        });
    }


    /******
     * 弹出框
     *******/
    private WebView mWebView;
    private TextView popTitle, popAuthor;
    private Button popHotPoint, popEdit, popShare;
    private View popView;
    private PopupWindow popupWindow;
    private RelativeLayout popContainer;
    private Animation animation;
    private float screenWByH;

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

        mWebView.setWebViewClient(new CustomWebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mWebView.startAnimation(animation);
                super.onPageFinished(view, url);
            }
        });
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
//        mWebView.getSettings().setSupportZoom(true);
//        // 设置出现缩放工具
//        mWebView.getSettings().setBuiltInZoomControls(true);
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
     *
     * @param position 点击列表位置
     */
    private void showPopWindow(int position) {
        try {
            FastBlur.blur(getActivity(), ScreenShortCutUtil.getShortScreen(getActivity()), popView);
        } catch (Exception ex) {

        }
        float baseWidth = imgWidth * 2 - DPIUtil.dip2px(getActivity(), 80);
        int baseHight = 0;
//        if (mList.get(position).getWorkStyle().getShowStyle().equals("Horizontal")) {
//            baseHight = Math.round(baseWidth * screenWByH);
//        } else {
        baseHight = Math.round(baseWidth / screenWByH);
//        }
        if (mList.get(position).getIsDianZan() == 0) {
            popHotPoint.setBackgroundResource(R.mipmap.love);
        } else {
            popHotPoint.setBackgroundResource(R.mipmap.loved);
        }
        ViewGroup.LayoutParams params = popContainer.getLayoutParams();
        params.width = Math.round(baseWidth);
        params.height = baseHight;
        popContainer.setLayoutParams(params);
        mWebView.loadUrl(mList.get(position).getShowPath());
        popTitle.setText(mList.get(position).getTitle());
        popAuthor.setText(mList.get(position).getAuthor().getNickname());
        popupWindow.showAtLocation(mainView, Gravity.CENTER, 0, 0);
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
                    Intent intent = new Intent(getActivity(), TempEditV3Activity.class);
                    intent.putExtra("imgPath", "");
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
                mWebView.reload();
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
            mList.get(clickItemIndex).setFeedBackCount(num < 0 ? 0 : num);
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
        super.onDestroyView();
        ViewGroup group = (ViewGroup) mainView.getParent();
        if (group != null) {
            group.removeView(mainView);
        }
    }
}
