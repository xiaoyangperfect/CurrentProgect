package com.airppt.airppt.activity.fragment;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airppt.airppt.activity.TempEditV4Activity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.airppt.airppt.R;
import com.airppt.airppt.activity.SettingActivity;
import com.airppt.airppt.activity.ShareActivity;
import com.airppt.airppt.entry.FeedBack;
import com.airppt.airppt.entry.TempEntry;
import com.airppt.airppt.entry.TempIdEntry;
import com.airppt.airppt.entry.WorksEntry;
import com.airppt.airppt.util.AnimationUtil;
import com.airppt.airppt.util.AsyncHttpResponseCallBack;
import com.airppt.airppt.util.CustomWebViewClient;
import com.airppt.airppt.util.DPIUtil;
import com.airppt.airppt.util.FastBlur;
import com.airppt.airppt.util.FileUtil;
import com.airppt.airppt.util.HttpConfig;
import com.airppt.airppt.util.JavascriptBridge;
import com.airppt.airppt.util.MD5Util;
import com.airppt.airppt.util.RequestParam;
import com.airppt.airppt.util.ScreenShortCutUtil;
import com.airppt.airppt.util.SharedPreferenceUtil;
import com.airppt.airppt.util.Util;
import com.airppt.airppt.view.CircleDialogProgressBar;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;


/**
 * Created by user on 2015/7/9.
 */
public class FragmentAccount extends Fragment {
    private View mainView;
    private WebView webView;
    private CircleDialogProgressBar circleBar;
    private Button setBtn;

    private JavascriptBridge bridge;
    private String url;
    private Gson gson;
    private AsyncHttpClient httpClient;
    private String userId;
    //瀑布流中展示图片的宽度
    private float windowWidth;
    private TempIdEntry entry;
    private boolean isFinished;
    private WorksEntry worksEntry;

    private LocalBroadcastManager localBroadcastManager;
    private BroadcastReceiver broadcastReceiver;
    //    private MaterialDialog materialDialog, materialDialog2;
    private boolean isFirstLoad;
    private String deletId;
    private Dialog dialog, dialog2;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
//                    if (SharedPreferenceUtil.getSharedPreference(getActivity()).getString(
//                            SharedPreferenceUtil.MOD2, "0"
//                    ).equals("0")) {
//                        ((MainActivity) getActivity()).showMod2();
//                    } else {
                        if (entry == null) {

                        } else {
                            if (entry.getData().isFinished()) {
                                isFinished = true;
                                showPopWindow(entry);
                            } else {
                                File file = new File(FileUtil.WORKS_PATH + "/" + entry.getData().getWork_id());
                                isFinished = false;
                                if (file.exists()) {
                                    getWorkEntry(entry);
                                } else {
                                    deletId = entry.getData().getWork_id();
                                    dialog2.show();

                                }
                            }
                        }
//                    }
                    break;
                case 1:
                    TempEntry tempEntry = (TempEntry) msg.obj;
                    if (tempEntry.getData().getWorks().size() > 0) {
                        WorksEntry worksEntry = tempEntry.getData().getWorks().get(0);
                        toTempEditActivity(worksEntry);
                    }

                    break;
                case 2:
                    circleBar.show();
                    break;
                case 3:
                    if (circleBar.isShowing()) {
                        circleBar.dismiss();
                    }
                    break;
                case 4:
                    showReceiverWindow();
                    break;
                case 5:
                    dialog.show();
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mainView == null) {
            mainView = inflater.inflate(R.layout.fragment_account, null);
            initView();
            initData();
            showView();
            initShowWindow();
        }
//        refreshView();
        return mainView;
    }

    @Override
    public void onResume() {
        if (isFirstLoad) {
            isFirstLoad = false;
        } else {
            refreshView();
        }

        super.onResume();
    }


    private void initView() {
        webView = (WebView) mainView.findViewById(R.id.account_webView);
        setBtn = (Button) mainView.findViewById(R.id.activity_tempedit_setting);
        webView.setWebViewClient(new CustomWebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (circleBar.isShowing()) {
                    circleBar.dismiss();
                }
                super.onPageFinished(view, url);
            }
        });
        WebSettings settings = webView.getSettings();
        webView.setDrawingCacheEnabled(true);
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        bridge = new JavascriptBridge(webView);
        bridge.addJavaMethod("personal_work_detail", new JavascriptBridge.Function() {
            @Override
            public Object execute(JSONObject params) {
                Log.e("personal_work_detail", params.toString() + "  params params");
                Type type = new TypeToken<TempIdEntry>() {
                }.getType();
                entry = gson.fromJson(params.toString(), type);
                if (!entry.getData().getWork_id().equals("0")) {
                    Message msg = new Message();
                    msg.what = 0;
                    handler.sendMessage(msg);
                }
                return true;
            }
        });

        bridge.addJavaMethod("stoploading", new JavascriptBridge.Function() {

            @Override
            public Object execute(JSONObject params) {
                Log.e("stoploading", params.toString());
                return null;
            }
        });

        bridge.addJavaMethod("personal_work_delete", new JavascriptBridge.Function() {

            @Override
            public Object execute(JSONObject params) {
                Log.e("personal_work_delete", params.toString());
                try {
                    JSONObject jsonObject = new JSONObject(params.toString());
                    JSONObject object = jsonObject.getJSONObject("data");
                    deletId = object.getString("work_id");
                    handler.sendEmptyMessage(5);
//                    materialDialog2.setTitle("提示")
//                            .setMessage("确定要删除此页？")
//                            .setPositiveButton("删了", new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    handler.sendEmptyMessage(2);
//                                    materialDialog2.dismiss();
//                                    deletUndoWork(deletId);
//                                }
//                            })
//                            .setNegativeButton("算了", new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    materialDialog2.dismiss();
//                                }
//                            });
//                    materialDialog2.show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });

        bridge.addJavaMethod("collection_work_detail", new JavascriptBridge.Function() {

            @Override
            public Object execute(JSONObject params) {
                Log.e("collection_work_detail", params.toString());

                try {
                    JSONObject jsonObject = new JSONObject(params.toString());
                    JSONObject object = jsonObject.getJSONObject("data");
                    JSONObject ob = object.getJSONObject("data");
                    Type type = new TypeToken<WorksEntry>() {
                    }.getType();
                    worksEntry = gson.fromJson(ob.toString(), type);
                    handler.sendEmptyMessage(4);
                } catch (Exception e) {

                }

                return null;
            }
        });

        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toSetting();
            }
        });


    }

    private void initData() {
        userId = SharedPreferenceUtil.getAccountSharedPreference(getActivity()).getString(SharedPreferenceUtil.USERID, "");
        url = "http://www.airppt.cn/air/edit.html?userId=" + userId + "&d=android&accessToken=" + SharedPreferenceUtil.getAccountSharedPreference(getActivity()).getString(SharedPreferenceUtil.APP_TOKEN, "");
//        url = "http://www.ddiaos.com/ppt/edit.html?userId=" + userId + "&d=android&accessToken=" + SharedPreferenceUtil.getAccountSharedPreference(getActivity()).getString(SharedPreferenceUtil.APP_TOKEN, "");
        gson = new Gson();
        isFirstLoad = true;
        httpClient = new AsyncHttpClient();
        DPIUtil.getScreenMetrics(getActivity());
        windowWidth = DPIUtil.screen_width - 2 * DPIUtil.dip2px(getActivity(), 50);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                webView.reload();
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("action_personalinfo");
        localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
        circleBar = CircleDialogProgressBar.createCircleDialogProgressBar(getActivity());
        if (Util.isStringNotEmpty(SharedPreferenceUtil.getAccountSharedPreference(getActivity()).getString(SharedPreferenceUtil.USERID, ""))) {
            circleBar.show();
//            if (SharedPreferenceUtil.getSharedPreference(getActivity()).getString(
//                    SharedPreferenceUtil.MOD7, "0"
//            ).equals("0")) {
//                ((MainActivity) getActivity()).showMod7();
//            }
        }
//        materialDialog = new MaterialDialog(getActivity());
//        materialDialog2 = new MaterialDialog(getActivity());
//        dialog = new Dialog(getActivity(), getString(R.string.prompt), getString(R.string.sure_to_delete));
//        dialog.addCancelButton(getString(R.string.cancel));
//
//        dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                handler.sendEmptyMessage(2);
//                dialog.dismiss();
//                deletUndoWork(deletId);
//            }
//        });

        dialog  = new AlertDialog.Builder(getActivity(), android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)
                .setTitle(R.string.prompt)
                .setMessage(R.string.sure_to_delete)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.sendEmptyMessage(2);
                        dialog.dismiss();
                        deletUndoWork(deletId);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
        dialog2  = new AlertDialog.Builder(getActivity(), android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)
                .setTitle(R.string.prompt)
                .setMessage(R.string.file_mis_sure_to_delete)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.sendEmptyMessage(2);
                        dialog.dismiss();
                        deletUndoWork(deletId);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
    }

    private void showView() {
        webView.loadUrl(url);
    }

    private void getWorkEntry(TempIdEntry tempIdEntry) {
        String tempUrl = HttpConfig.getGetWorks(userId);
        RequestParams params = RequestParam.getRequestParams(getActivity());
        params.put("pageCount", 1);
        String query = "{ \"WorksId\":" + tempIdEntry.getData().getWork_id() + "}";
        params.put("query", query);
        httpClient.post(tempUrl, params, new AsyncHttpResponseCallBack() {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {
                        super.onSuccess(i, headers, bytes);
                        Log.e("tempWork", new String(bytes));
                        Type type = new TypeToken<TempEntry>() {
                        }.getType();
                        TempEntry tempEntry = gson.fromJson(new String(bytes), type);
                        if (tempEntry.getData().getWorks().size() > 0) {
                            WorksEntry worksEntry = tempEntry.getData().getWorks().get(0);
                            toTempEditActivity(worksEntry);
                            popupWindow.dismiss();
                        }
//                        Message msg = new Message();
//                        msg.obj = tempEntry;
//                        msg.what = 1;
//                        handler.sendMessage(msg);
                        if (circleBar.isShowing()) {
                            circleBar.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        if (circleBar.isShowing()) {
                            circleBar.dismiss();
                        }
                    }
                }

        );
    }

    private void toTempEditActivity(WorksEntry worksEntry) {
        Intent intent = new Intent(getActivity(), TempEditV4Activity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("entry", worksEntry);
        intent.putExtras(bundle);
        intent.putExtra("from", "edit");
        intent.putExtra("isLock", entry.getData().getHavePassword());
        intent.putExtra("isFinished", isFinished);
        startActivity(intent);
        bundle.clear();
    }

    private void refreshView() {
        Bundle params = new Bundle();
        params.putString("asdfasdf", "123123");
        bridge.require("createHtml", null, new JavascriptBridge.Callback() {

            @Override
            public void onComplate(JSONObject response, String cmd, Bundle params) {
                Log.e("createHtml", response.toString());
            }
        });
    }

    private void removeWork(String id) {
        Bundle params = new Bundle();
        params.putString("id", id);
        bridge.require("workRemove", params, new JavascriptBridge.Callback() {

            @Override
            public void onComplate(JSONObject response, String cmd, Bundle params) {
                Log.e("workRemove", response.toString());
            }
        });
    }

    /******
     * 弹出框
     *******/
    private WebView mWebView;
    private TextView popTitle, popAuthor;
    private Button popLeft, popEdit, popRight, passBtn;
    private View popView;
    private PopupWindow popupWindow;
    private RelativeLayout popContainer;
    private Animation animation;
    private float screenWByH;

    private void initShowWindow() {

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        popView = inflater.inflate(R.layout.main_popupwindow_layout, null);
        mWebView = (WebView) popView.findViewById(R.id.main_pop_webview);
        popTitle = (TextView) popView.findViewById(R.id.main_pop_title);
        popAuthor = (TextView) popView.findViewById(R.id.main_pop_author);
        popLeft = (Button) popView.findViewById(R.id.main_pop_hotpoint);
        popEdit = (Button) popView.findViewById(R.id.main_pop_toedit);
        popRight = (Button) popView.findViewById(R.id.main_pop_share);
        popContainer = (RelativeLayout) popView.findViewById(R.id.main_pop_container);

        animation = AnimationUtil.initAlphaShowAnimation();
        screenWByH = DPIUtil.getWidthByHight();
        popAuthor.setVisibility(View.GONE);


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

    }

    /**
     * 显示窗口，重新加载url，重新定义窗口大小，设置背景透明度
     */
    private void showPopWindow(TempIdEntry entry) {
        mWebView.clearCache(true);
        popLeft.setBackgroundResource(R.drawable.to_share_btn);
        ViewGroup.LayoutParams lparams = popLeft.getLayoutParams();
        int base = DPIUtil.dip2px(getActivity(), 20);
        lparams.height = base;
        lparams.width = Math.round(base * 43 / 33);
        popLeft.setLayoutParams(lparams);
        popEdit.setBackgroundResource(R.drawable.account_edit_btn);
        ViewGroup.LayoutParams eparams = popEdit.getLayoutParams();
        eparams.height = DPIUtil.dip2px(getActivity(), 30);
        eparams.width = DPIUtil.dip2px(getActivity(), 30);
        popEdit.setLayoutParams(eparams);

        ViewGroup.LayoutParams rparams = popRight.getLayoutParams();
        rparams.height = base;
        rparams.width = Math.round(base * 27 / 33);
        popRight.setLayoutParams(rparams);

        initPopListener();

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            FastBlur.blur(getActivity(), ScreenShortCutUtil.getShortScreen(getActivity()), popView);
        }
        mWebView.loadUrl(entry.getData().getPreview_url());
        popTitle.setText(entry.getData().getTitle());
        if (entry.getData().getHavePassword().equals("1")) {
            popRight.setBackgroundResource(R.mipmap.account_locked);
        } else {
            popRight.setBackgroundResource(R.mipmap.account_lock);
        }

        int baseHight = 0;
        baseHight = Math.round(windowWidth / screenWByH);
        ViewGroup.LayoutParams params = popContainer.getLayoutParams();
        params.width = Math.round(windowWidth);
        params.height = baseHight;
        popContainer.setLayoutParams(params);
        ViewGroup.LayoutParams layoutParams = mWebView.getLayoutParams();
        layoutParams.width = Math.round(windowWidth);
        layoutParams.height = baseHight;
        mWebView.setLayoutParams(layoutParams);
        popupWindow.showAtLocation(mainView, Gravity.CENTER, 0, 0);
    }

    private void showReceiverWindow() {
        mWebView.clearCache(true);
        initRecPopListener();

        try {
            FastBlur.blur(getActivity(), ScreenShortCutUtil.getShortScreen(getActivity()), popView);
        } catch (Exception ex) {

        }
        popEdit.setBackgroundResource(R.drawable.to_edit_btn);
        popLeft.setBackgroundResource(R.mipmap.loved);
        popRight.setBackgroundResource(R.drawable.to_share_btn);
        ViewGroup.LayoutParams lparams = popRight.getLayoutParams();
        int base = DPIUtil.dip2px(getActivity(), 20);
        lparams.height = base;
        lparams.width = Math.round(base * 43 / 33);
        popRight.setLayoutParams(lparams);
        int baseHight = 0;
        baseHight = Math.round(windowWidth / screenWByH);
        ViewGroup.LayoutParams params = popContainer.getLayoutParams();
        params.width = Math.round(windowWidth);
        params.height = baseHight;
        popContainer.setLayoutParams(params);
        ViewGroup.LayoutParams layoutParams = mWebView.getLayoutParams();
        layoutParams.width = Math.round(windowWidth);
        layoutParams.height = baseHight;
        mWebView.setLayoutParams(layoutParams);
        mWebView.loadUrl(worksEntry.getShowPath());
        popupWindow.showAtLocation(mainView, Gravity.CENTER, 0, 0);
    }

    /**
     * popupwindow监听事件注册
     */
    private void initPopListener() {
        popEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                handler.sendEmptyMessage(2);
                getWorkEntry(entry);
            }
        });
        popView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mWebView.loadUrl(HttpConfig.BASE_URL);
            }
        });

        popLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ShareActivity.class);
                intent.putExtra("shareUrl", entry.getData().getPreview_url());
                String imgurl = SharedPreferenceUtil.getSharedPreference(getActivity()).getString(
                        SharedPreferenceUtil.HOST, HttpConfig.BASE_URL
                ) + "shareimg_" + MD5Util.MD5(entry.getData().getWork_id()) + ".jpg";
                intent.putExtra("imgUrl", imgurl);
                intent.putExtra("title", entry.getData().getTitle());
                intent.putExtra("workId", entry.getData().getWork_id());
                startActivity(intent);
            }
        });
    }

    /**
     * popupwindow监听事件注册
     */
    private void initRecPopListener() {
        popEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TempEditV4Activity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("entry", worksEntry);
                intent.putExtras(bundle);
                intent.putExtra("from", "create");
                startActivity(intent);
                bundle.clear();
                popupWindow.dismiss();
            }
        });
        popView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        popLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userFeedBack();
            }
        });

        popRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });
    }

    public void deletUndoWork(final String id) {
        FileUtil.deleteDirectory(new File(FileUtil.WORKS_PATH + "/" + id));
        RequestParams params = RequestParam.getRequestParams(getActivity());
        params.put("worksId", id);
        Log.e("params", params.toString());
        httpClient.post(HttpConfig.CLOSEWORK, params, new AsyncHttpResponseCallBack() {
            @Override
            public void onSuccess(int var1, Header[] var2, byte[] var3) {
                super.onSuccess(var1, var2, var3);
                Log.e("closedowork", new String(var3));
                handler.sendEmptyMessage(3);
                removeWork(id);
            }

            @Override
            public void onFailure(int var1, Header[] var2, byte[] var3, Throwable var4) {
                super.onFailure(var1, var2, var3, var4);
            }
        });
    }

    private void userFeedBack() {
        popupWindow.dismiss();
        circleBar.show();
        String urlFeedBack = HttpConfig.FEEDBACK;
        RequestParams params = new RequestParams();
        FeedBack feedBack = new FeedBack();

        feedBack.setTag("0");
        feedBack.setFeedBackUserId(userId);
        feedBack.setWorksId(worksEntry.getWorksId());
        String feed = gson.toJson(feedBack);
        params.put("feedBackLog", feed);

        httpClient.post(urlFeedBack, params, new AsyncHttpResponseCallBack() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                Log.e("feedback", new String(bytes));
                refreshView();
                if (circleBar.isShowing()) {
                    circleBar.dismiss();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }

    public void toSetting() {
        Intent intent = new Intent(getActivity(), SettingActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ViewGroup group = (ViewGroup) mainView.getParent();
        if (group != null) {
            group.removeView(mainView);
        }
    }

    @Override
    public void onDestroy() {
        try {
            localBroadcastManager.unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {

        }
        super.onDestroy();
    }
}
