package com.airppt.airppt.activity;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airppt.airppt.R;
import com.airppt.airppt.adapter.SortAdapter;
import com.airppt.airppt.adapter.TempEditPopAdapter;
import com.airppt.airppt.db.DBAdapter;
import com.airppt.airppt.db.ShareDBEntry;
import com.airppt.airppt.entry.DataJsAuthor;
import com.airppt.airppt.entry.DataJsMusic;
import com.airppt.airppt.entry.DataJsWork;
import com.airppt.airppt.entry.EditCallBackEntry;
import com.airppt.airppt.entry.ImageViewState;
import com.airppt.airppt.entry.JsConfig;
import com.airppt.airppt.entry.MiKeyEntry;
import com.airppt.airppt.entry.ShareUrlEntry;
import com.airppt.airppt.entry.ShortCutDataEntry;
import com.airppt.airppt.entry.TempEntry;
import com.airppt.airppt.entry.TempFile;
import com.airppt.airppt.entry.TextContent;
import com.airppt.airppt.entry.WorkConfig;
import com.airppt.airppt.entry.WorksEntry;
import com.airppt.airppt.listener.RecyclerViewItemClickListener;
import com.airppt.airppt.listener.RecyclerViewItemDrapListener;
import com.airppt.airppt.listener.RecyclerViewItemOnTuchListener;
import com.airppt.airppt.model.NameParam;
import com.airppt.airppt.model.WorkV2;
import com.airppt.airppt.util.AnimationUtil;
import com.airppt.airppt.util.AsyncHttpResponseCallBack;
import com.airppt.airppt.util.CustomWebViewClient;
import com.airppt.airppt.util.DPIUtil;
import com.airppt.airppt.util.FileUtil;
import com.airppt.airppt.util.HttpConfig;
import com.airppt.airppt.util.ImageOptUtil;
import com.airppt.airppt.util.JavascriptBridge;
import com.airppt.airppt.util.MD5Util;
import com.airppt.airppt.util.RequestParam;
import com.airppt.airppt.util.SharedPreferenceUtil;
import com.airppt.airppt.util.Util;
import com.airppt.airppt.util.ZipUtil;
import com.airppt.airppt.view.CircleDialogProgressBar;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


/**
 * Created by user on 2015/7/10.
 */
public class TempEditV4Activity extends BaseActivity {

    private RelativeLayout inputLay;
    private EditText inputEdittext;
    private TextView inputTextNum;
    private WebView webView;
    private Button editFinish, setBtn;
    private CircleDialogProgressBar circleBar;
    private Button sortBtn, music_btn;
    private LinearLayout container;
    private RecyclerView sortRecyclerView;
    private ImageView modImg;

    private AsyncHttpClient httpClient;
    private Gson gson;
    private String userId;



    //模板信息实体
    private WorksEntry tempEntry;
    private WorksEntry workEntry;
    private String workPath, tempPath;
    //datajs 中的页面数据列表,sortList中map顺序决定页面排序
    private ArrayList<HashMap> sortList;
    private Long miKey;
    //this param reprent wether those file needed to show html download finished.
    private int fileNum;
    private String orgH5dataFileName;
    private InputMethodManager inputMethodManager;
    private JavascriptBridge bridge;
    private WorkV2 work;
    //是否在reload的标志位,在reload前一定要把该值设置成true;
    private boolean reload;
    //the most importent params, they must be inited when the activity start
    private String urlLoad;
    private EditCallBackEntry editCallBackEntry;
    private LayoutInflater inflater;
    private String uploadUrl;

    private static final int EDITINDEX_0 = 0;
    private static final int EDITINDEX_1 = 1;
    private static final int EDITINDEX_2 = 2;

    private static final int NONE = 0;
    private static final int PHOTO_GRAPH = 1;// 拍照
    private static final int PHOTO_ZOOM = 2; // 缩放
    private static final int PHOTO_CROP = 3;// 结果
    private static final int PHOTO_CHOSE = 4;

    private static final int SHOT_CHOSE = 10;
    private static final int SHOT_CUT = 11;
    private static final String IMAGE_UNSPECIFIED = "image/*";

    private DataJsMusic dataJsMusic;
    private DataJsWork datajsWork;
    private DataJsAuthor dataJsAuthor;
    //上传的config内容
    private WorkConfig config;

    private Animation webScaleSmall, webScaleBig;

    //启动意图来源为main/search还是Account，
    // main/search页面为true该情况下isFinished肯定为false；
    // account页面为false，isFinished的值需要根据作品是进行编辑还是进行传作判断，因为account页面有可能存在未完成创作的作品
//    private boolean isCreate;
    //这个标志位才是真正代表创作还是编辑
//    private boolean isFinished;

    private static final int CREATE = 0;
    private static final int RE_CREATE = 1;
    private static final int MODIFY = 2;
    //这个标志位决定着该作品，是创建还是继续未完成的创建还是修改
    //0:创建 1：继续未完成的创建 2：修改作品
    private int createMod;

    //文件云端存储目录
    private String storeUrl;
    //    private MaterialDialog materialDialog;
    private Dialog dialog, dialogExist;

    private boolean isChangeCover;
    //作品是否已经改动过，决定着用户点击返回键时的操作
    private boolean isElementChange;
//    //上传完成后是否跳进分享页面
//    private boolean isToShare;
//    //现在是否正在进行finishactivity操作，对弹出设置页面的确定按钮的后续操作有影响。
//    private boolean isToFinish;
    private static final int TO_SET = 0;
    private static final int TO_SAVE = 1;
    private static final int TO_SHARE = 2;
    private int intentMod;

    private DBAdapter dbAdapter;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(TempEditV4Activity.this, "未完成作品过多,创建失败！", Toast.LENGTH_LONG).show();
                    TempEditV4Activity.this.finish();
                    break;
                case 1:
                    initWorkPath();
                    downloadFilesByCreate();
                    break;
                case 2:
                    fileNum--;
                    Log.e("file nume", fileNum + "");
                    if (fileNum == 0) {
                        if ((createMod == CREATE) && Util.isStringNotEmpty(orgH5dataFileName)) {
                            initWork(0);
                        } else if (!(createMod == CREATE) && (checkNecessaryFile(tempPath, tempEntry, true) &&
                                checkNecessaryFile(workPath, workEntry, false))){
                            initWork(3);
                        } else {
                            Toast.makeText(TempEditV4Activity.this, "初始化失败!", Toast.LENGTH_LONG).show();
                            FileUtil.deleteDirectory(new File(workPath));
                            FileUtil.deleteDirectory(new File(tempPath));
                            handler.sendEmptyMessage(9);
                            return;
                        }
                    }
                    break;
                case 3:
                    try {
                        container.getChildAt(pageIndex).setEnabled(false);
                        container.getChildAt(webViewIndex).setEnabled(true);
                        pageIndex = webViewIndex;
                    } catch (Exception e) {

                    }

                    break;
                case 4:
                    if (editCallBackEntry.getData().getType().equals("img")) {
                        Intent intent = new Intent(Intent.ACTION_PICK, null);
                        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
                        startActivityForResult(intent, PHOTO_ZOOM);
                    } else {
                        editText();
                    }
                    break;
                case 5:
                    circleBar.show();
                    int position = (int) msg.obj;
                    if (work.addPage(tempImgPathList.get(position).getPageID(), tempImgPathList.get(position).getPath(), gson)) {
                        ImageView imageView = new ImageView(TempEditV4Activity.this);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(10, 10);
                        layoutParams.setMargins(10, 0, 10, 0);
                        imageView.setLayoutParams(layoutParams);
                        imageView.setEnabled(false);
                        imageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.point_background));
                        container.addView(imageView);
                        container.getChildAt(container.getChildCount()-2).setEnabled(false);
                        container.getChildAt(container.getChildCount()-1).setEnabled(true);
                        changeElement(webViewIndex);
                        pageIndex = webViewIndex;
                    } else {
                        Toast.makeText(TempEditV4Activity.this, R.string.add_page_fale, Toast.LENGTH_LONG).show();
                    }
                    break;

                case 6:
                    if (pageIndex >= shortCutViews.size())
                        pageIndex = shortCutViews.size() - 1;
                    shortCutViews.get(pageIndex).setState(false);
                    sortAdapter.notifyItemChanged(pageIndex);
                    shortCutViews.get(webViewIndex).setState(true);
                    sortAdapter.notifyItemChanged(webViewIndex);
                    sortRecyclerView.scrollToPosition(webViewIndex);
                    container.getChildAt(pageIndex).setEnabled(false);
                    container.getChildAt(webViewIndex).setEnabled(true);
                    pageIndex = webViewIndex;
                    break;
                case 7:
                    dialog.show();
                    ButtonFlat flat = dialog.getButtonAccept();
                    flat.setText(getString(R.string.delete));
                    dialog.setButtonAccept(flat);
                    break;
                case 8:
                    workPreview();
                    break;
                case 9:
//                    FileUtil.deleteDirectory(new File(workPath));
//                    FileUtil.deleteDirectory(new File(tempPath));
                    TempEditV4Activity.this.finish();
                    break;
                case 10:
                    showSetView();
                    break;
                case 11:
                    if (circleBar.isShowing())
                        circleBar.dismiss();
                    break;
                case 12:
                    prepareData4uploadWork();
                    updateWorkDetial();
                    break;
                case 13:
                    if (intentMod == TO_SET) {
                        if (circleBar.isShowing())
                            circleBar.dismiss();
                    } else {
                        try {
                            ZipUtil.zipFolder(FileUtil.UPLOAD_PATH + "/" + miKey, FileUtil.UPLOAD_PATH + "/" + miKey + ".zip");
                            String url = HttpConfig.CREATEWORK + "?worksId=" + miKey;
                            RequestParams createParams = RequestParam.getRequestParams(TempEditV4Activity.this);
                            createParams.put("worksId", miKey);
                            createParams.put("file", new File(FileUtil.UPLOAD_PATH + "/" + miKey + ".zip"));
                            httpClient.post(url, createParams, new AsyncHttpResponseCallBack() {
                                @Override
                                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                                    Log.e("up result", new String(bytes));
                                    work.saveNamePoolFile(gson);
                                    Type type = new TypeToken<ShareUrlEntry>() {
                                    }.getType();
                                    ShareUrlEntry urlEntry = gson.fromJson(new String(bytes), type);
                                    handler.sendEmptyMessage(11);
                                    FileUtil.deleteDirectory(new File(workPath));
                                    FileUtil.deleteDirectory(new File(tempPath));
                                    //删除上传用的临时文件夹
                                    FileUtil.deleteDirectory(new File(FileUtil.UPLOAD_PATH));
                                    if (intentMod == TO_SHARE) {
                                        Intent intent = new Intent(TempEditV4Activity.this, ShareActivity.class);
                                        intent.putExtra("shareUrl", urlEntry.getData());
                                        intent.putExtra("imgUrl", storeUrl + "shareimg_" + MD5Util.MD5(miKey + "") + ".jpg");
                                        intent.putExtra("title", title);
                                        intent.putExtra("workId", miKey + "");
                                        startActivity(intent);
                                        TempEditV4Activity.this.finish();
                                    } else {
                                        TempEditV4Activity.this.finish();
                                    }
                                }

                                @Override
                                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                                    Log.e("upload error", i + new String(headers + "") + " " + "error" + new String(bytes + "") + " " + throwable.getMessage());
                                    Toast.makeText(TempEditV4Activity.this, "上传文件失败，请重试！", Toast.LENGTH_LONG).show();
                                    if (circleBar.isShowing())
                                        circleBar.dismiss();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (circleBar.isShowing())
                                circleBar.dismiss();
                        }
                    }

                    break;
                case 14:
                    changeElement(webViewIndex);
                    break;
                case 15:
                    boolean isModify = webViewIndex == 0 ? false : work.pages.get(webViewIndex - 1).isModify();
                    Log.e("TempEdit", (webViewIndex - 1) + " : " + isModify + "");
                    if (isModify && !(createMod == MODIFY)) {
                        upLoadImage(webViewIndex - 1);
                    }
                    break;
                case 16:
                    TempEditV4Activity.this.finish();
                    break;
                case 17:
                    getShortCut(webViewIndex);
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tempeditv4);

        initView();
        initCustemData();
        initIntentData();
    }

    private void initView() {
        inputLay = (RelativeLayout) findViewById(R.id.activity_tempedit_input_lay);
        inputEdittext = (EditText) findViewById(R.id.activity_tempedit_input);
        inputTextNum = (TextView) findViewById(R.id.activity_tempedit_input_limit);
        setBtn = (Button) findViewById(R.id.activity_tempedit_setting);
        webView = (WebView) findViewById(R.id.activity_tempedit_webview);
        editFinish = (Button) findViewById(R.id.activity_tempedit_finish);
        sortBtn = (Button) findViewById(R.id.activity_tempedit_exchange);
        container = (LinearLayout) findViewById(R.id.webview_index_container);
        sortRecyclerView = (RecyclerView) findViewById(R.id.avtivity_tempedit_sortLay);
        music_btn = (Button) findViewById(R.id.activity_tempedit_music);
        LinearLayoutManager managers = new LinearLayoutManager(this);
        managers.setOrientation(LinearLayoutManager.HORIZONTAL);
        sortRecyclerView.setLayoutManager(managers);
        //loading
        circleBar = CircleDialogProgressBar.createCircleDialogProgressBar(this);
        circleBar.show();

        //页面删除提示框初始化
        dialog = new Dialog(this, getString(R.string.prompt), getString(R.string.sure_to_delete_page));
        dialog.addCancelButton(getString(R.string.cancel));

        dialogExist = new Dialog(this, getString(R.string.exist_work), getString(R.string.exist_prompt));

        modImg = (ImageView) findViewById(R.id.tempedit_mod3);
        String modVersion = SharedPreferenceUtil.getSharedPreference(this).getString(
                SharedPreferenceUtil.MOD3, "0"
        );
        if (!modVersion.equals("0")) {
            modImg.setVisibility(View.GONE);
        } else {
            modImg.setBackgroundResource(R.mipmap.mod_3);
            modImg.setTag("mod_3");
            modImg.setVisibility(View.VISIBLE);
        }
    }

    private void initCustemData() {
        //用户ID
        userId = SharedPreferenceUtil.getAccountSharedPreference(TempEditV4Activity.this).getString(SharedPreferenceUtil.USERID, "");
        //初始化屏幕分辨率工具
        DPIUtil.getScreenMetrics(this);
        //初始化网络工具
        httpClient = new AsyncHttpClient();
        httpClient.setTimeout(30000);
        gson = new Gson();
        //初始化输入法工具
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //布局
        inflater = LayoutInflater.from(this);
        //上传作品URL
        uploadUrl = HttpConfig.UPLOADWORKIMGS;
        config = new WorkConfig();
        webScaleSmall = AnimationUtil.initScale10To8Animation();
        webScaleSmall.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                sortBtn.setClickable(false);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ViewGroup.LayoutParams params = webView.getLayoutParams();
                params.width = (int) (webviewWidth * 0.85);
                params.height = (int) (webViewHight * 0.85);
                webView.setLayoutParams(params);
                sortBtn.setClickable(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
//        webScaleBig = AnimationUtil.initScale8To10Animation();
        webScaleBig = AnimationUtil.initAlphaShowAnimation();
        webScaleBig.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                ViewGroup.LayoutParams params = webView.getLayoutParams();
                params.width = webviewWidth;
                params.height = webViewHight;
                webView.setLayoutParams(params);
                sortBtn.setClickable(false);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                sortBtn.setClickable(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        storeUrl = SharedPreferenceUtil.getSharedPreference(TempEditV4Activity.this).getString(
                SharedPreferenceUtil.HOST, HttpConfig.BASE_URL
        );
        //数据库初始化
        dbAdapter = new DBAdapter(this);
    }

    /**
     * 根据传过来的值进行初始化
     */
    private void initIntentData() {
        String optFrom = getIntent().getStringExtra("from");
        if (optFrom.equals("create")) {
            createMod = CREATE;
            tempEntry = (WorksEntry) getIntent().getSerializableExtra("entry");
            if (tempEntry == null) {
                TempEditV4Activity.this.finish();
                Toast.makeText(this, "不好意思，我把数据弄错了，重新来一遍吧--！", Toast.LENGTH_LONG).show();
            }
            Log.e("entry", gson.toJson(tempEntry));
            getMikey();
        } else {
//            isChangeCover = true;
            workEntry = (WorksEntry) getIntent().getSerializableExtra("entry");
            String isLock = getIntent().getStringExtra("isLock");
            if(getIntent().getBooleanExtra("isFinished", false)) {
                createMod = MODIFY;
            } else {
                createMod = RE_CREATE;
            }
            if (isLock.equals("1")) {
                isPassWordInUse = true;
            } else {
                isPassWordInUse = false;
            }
            Log.e("entry", gson.toJson(workEntry));
            getTempEntry();
        }

    }

    private void setListener() {
        dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circleBar.show();
                removePage(webViewIndex);
            }
        });

        dialogExist.addCancelButton(getString(R.string.give_up), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (createMod == MODIFY) {
                    FileUtil.deleteDirectory(new File(workPath));
                    TempEditV4Activity.this.finish();
                } else {
                    deletUndoWork(miKey + "");
                }
            }
        });

        dialogExist.setOnAcceptButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogExist.dismiss();
                if (datajsWork == null || !isChangeCover) {
                    handler.sendEmptyMessage(10);
                    return;
                }
                intentMod = TO_SAVE;
                handler.sendEmptyMessage(12);
            }
        });

        inputEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                inputTextNum.setText(s.length() + "/100");
                if (s.length() > 100) {
                    inputEdittext.setTextColor(Color.RED);
                } else {
                    inputEdittext.setTextColor(Color.WHITE);
                }
                if (s.length() == 0) {
                    editFinish.setText(R.string.cancel);
                } else {
                    editFinish.setText(R.string.finish);
                }
            }
        });
        editTextFocusListener();

        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentMod = TO_SET;
                showSetView();
            }
        });

        modImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch ((String) modImg.getTag()) {
                    case "mod_3":
                        modImg.setBackgroundResource(R.mipmap.mod_4);
                        modImg.setTag("mod_4");
                        break;
                    case "mod_4":
                        modImg.setBackgroundResource(R.mipmap.mod_5);
                        modImg.setTag("mod_5");
                        break;
                    case "mod_5":
                        modImg.setBackgroundResource(R.mipmap.mod_6);
                        modImg.setTag("mod_6");
                        break;
                    case "mod_6":
                        modImg.setVisibility(View.GONE);
                        SharedPreferenceUtil.getSharedEditor(TempEditV4Activity.this).putString(
                                SharedPreferenceUtil.MOD3, getString(R.string.app_version)
                        ).commit();
                        Util.imageRecycle(modImg);
                        break;
                }
            }
        });
    }




    private void getMikey() {
        String miKeyUrl = HttpConfig.CREATEUNDONEWORK;
        RequestParams params = RequestParam.getRequestParams(TempEditV4Activity.this);
        params.put("templateId", tempEntry.getWorksId());
        httpClient.post(miKeyUrl, params, new AsyncHttpResponseCallBack(TempEditV4Activity.this) {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                super.onSuccess(i, headers, bytes);
                Log.e("miKey", new String(bytes));
                try {
                    Type type = new TypeToken<MiKeyEntry>() {
                    }.getType();
                    MiKeyEntry miKeyEntry = gson.fromJson(new String(bytes), type);
                    miKey = miKeyEntry.getData();
                    handler.sendEmptyMessage(1);
                } catch (Exception ex) {
                    handler.sendEmptyMessage(0);
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                handler.sendEmptyMessage(0);
                Toast.makeText(TempEditV4Activity.this, "getMikey error", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getTempEntry() {
        String tempUrl = HttpConfig.getGetWorks(userId);
        RequestParams params = RequestParam.getRequestParams(TempEditV4Activity.this);
        params.put("pageCount", 1);
        String query = "{ \"WorksId\":" + workEntry.getTemplateId() + "}";
        params.put("query", query);
        httpClient.post(tempUrl, params, new AsyncHttpResponseCallBack(TempEditV4Activity.this) {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                super.onSuccess(i, headers, bytes);
                Log.e("tempWork", new String(bytes));
                Type type = new TypeToken<TempEntry>() {
                }.getType();
                TempEntry entry = gson.fromJson(new String(bytes), type);
                if (entry != null && entry.getData().getWorks().size() > 0) {
                    tempEntry = entry.getData().getWorks().get(0);
                } else {
                    Toast.makeText(TempEditV4Activity.this, "getTempEntry error", Toast.LENGTH_LONG).show();
                    TempEditV4Activity.this.finish();
                }
                initPath();

                boolean checkLocalFileNum = checkLocalFile();
                boolean checkTempFile = checkNecessaryFile(tempPath, tempEntry, true);

                if (checkLocalFileNum && checkTempFile) {
                    File namepoolFile = new File(workPath + "/" + FileUtil.NAMEPOOL);
                    if (namepoolFile.exists()) {
                        initWork(1);
                    } else {
                        initWork(2);
                    }
                } else {
                    if (createMod == MODIFY) {
                        downloadFileByEdit();
                    } else {
                        Toast.makeText(TempEditV4Activity.this, "作品部分文件缺失，请放弃该作品!", Toast.LENGTH_LONG).show();
                        TempEditV4Activity.this.finish();
                    }
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }

    //初始化路径
    private void initPath() {
        tempPath = FileUtil.TEMP_PATH + "/" + tempEntry.getWorksId();
        workPath = FileUtil.WORKS_PATH + "/" + workEntry.getWorksId();
        SharedPreferenceUtil.getSharedEditor(this).putString(SharedPreferenceUtil.WORK_PATH, workPath).commit();
    }

    private void initWorkPath() {
        tempPath = FileUtil.TEMP_PATH + "/" + tempEntry.getWorksId();
        workPath = FileUtil.WORKS_PATH + "/" + miKey;
        SharedPreferenceUtil.getSharedEditor(this).putString(SharedPreferenceUtil.WORK_PATH, workPath).commit();
    }

    //检查本地文件是否存在
    private boolean checkLocalFile() {
        File file = new File(tempPath);
        if (!file.exists()) {
            return false;
        }

        File file2 = new File(workPath);
        if (!file2.exists()) {
            return false;
        }

        return true;
    }

    /**
     * 初始化work 数据模型
     *
     * @param way 0：创建时调用；1：编辑下且namepool本地文件存在；2：创建未完成下且namepool不存在；3：编辑且无namepool不存在
     */
    private void initWork(int way) {
        switch (way) {
            case 0:
                work = new WorkV2(workPath, tempPath, orgH5dataFileName, miKey, tempEntry, gson, storeUrl);
                break;
            case 1:
                miKey = Long.valueOf(workEntry.getWorksId());
                work = new WorkV2(miKey, workPath, tempPath, orgH5dataFileName, gson, storeUrl);
                break;
            case 2:
                miKey = Long.valueOf(workEntry.getWorksId());
                work = new WorkV2(workPath, tempPath, orgH5dataFileName, Long.valueOf(workEntry.getWorksId()), (createMod == MODIFY), storeUrl);
                break;
            case 3:
                miKey = Long.valueOf(workEntry.getWorksId());
                work = new WorkV2(miKey, workPath, orgH5dataFileName, gson, storeUrl, tempPath);
                break;
        }

        if (!work.isInitSuccess || work.getPageSize() < 1) {
            Log.e("init work", "false");
            Toast.makeText(this, "初始化失败!", Toast.LENGTH_LONG).show();
            handler.sendEmptyMessage(9);
            return;
        }

        initAddPagePopWindow();
        initSort();
        initPreview();
        initEditSet();
        setListener();
        prepareViewDate();
        initJsConfig();
    }


    //创建模式下，下载文件
    private void downloadFilesByCreate() {
        fileNum = 2 + tempEntry.getThumbnailImages().size();
        //判断模板文件是否存在，存在就复制到工作控件，不存在就下载模板
        if (new File(tempPath).exists()) {
            //此处复制
            //复制原模板所有文件文件同时要修改缩略图名称为以pageid命名的
            if (FileUtil.copyDir(this, tempPath, workPath)) {
                for (TempFile tempFile : tempEntry.getFileList()) {
                    if (tempFile.getTag() != null && tempFile.getTag().equals("datajs"))
                        orgH5dataFileName = tempFile.getPath();
                }
                initWorkThumbnailImages();
                fileNum = 1;
                handler.sendEmptyMessage(2);
            } else {
                Log.e("copy file", "error");
                handler.sendEmptyMessage(0);
            }
        } else {
            //创建模板和工作的文件夹
            FileUtil.createFileDir(tempPath);
            FileUtil.createFileDir(workPath);
            for (int i = 0; i < tempEntry.getFileList().size(); i++) {
                if (tempEntry.getFileList().get(i).getTag() != null
                        && tempEntry.getFileList().get(i).getTag().equals("datajs")) {
                    orgH5dataFileName = tempEntry.getFileList().get(i).getPath();
                    String downloadUrl = storeUrl + tempEntry.getFileList().get(i).getPath();
                    String pathTempFile = tempPath + "/" + tempEntry.getFileList().get(i).getPath();
                    String pathWorkFile = workPath + "/" + tempEntry.getFileList().get(i).getPath();
                    downloadFileCreateMod(downloadUrl, pathTempFile, pathWorkFile, false);
                } else if (tempEntry.getFileList().get(i).getPath().endsWith(".html")) {
                    String downloadUrl = storeUrl + tempEntry.getFileList().get(i).getPath();
                    String pathTempFile = tempPath + "/" + tempEntry.getFileList().get(i).getPath();
                    String pathWorkFile = workPath + "/" + tempEntry.getFileList().get(i).getPath();
                    downloadFileCreateMod(downloadUrl, pathTempFile, pathWorkFile, false);
                }
            }

            for (int i = 0; i < tempEntry.getThumbnailImages().size(); i++) {
                String downloadUrl = storeUrl + tempEntry.getThumbnailImages().get(i).getPath();
                String pathTempFile = tempPath + "/" + tempEntry.getThumbnailImages().get(i).getPath();
                String pathWorkFile = workPath + "/" + tempEntry.getThumbnailImages().get(i).getPageID() + ".jpg";
                downloadFileCreateMod(downloadUrl, pathTempFile, pathWorkFile, true);
            }
        }

    }

    //单个文件下载

    private void downloadFileCreateMod(String downloadUrl, final String pathTempFile, final String pathWorkFile, final boolean isThum) {

        httpClient.get(downloadUrl, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {

                File temp = FileUtil.createFile(pathTempFile);
                File work = FileUtil.createFile(pathWorkFile);
                try {
                    FileOutputStream outputStream = new FileOutputStream(temp);
                    outputStream.write(bytes);
                    outputStream.flush();
                    outputStream.close();
                    outputStream = new FileOutputStream(work);
                    outputStream.write(bytes);
                    outputStream.flush();
                    outputStream.close();
                } catch (Exception e) {
                    Log.e("write file", "error");
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(2);
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(TempEditV4Activity.this, "下载模板失败!", Toast.LENGTH_LONG).show();
                if (isThum) {
                    handler.sendEmptyMessage(9);
                } else {
                    handler.sendEmptyMessage(2);
                }
            }
        });
    }

    private void initWorkThumbnailImages() {
        File renameFile;
        for (TempFile tempFile : tempEntry.getThumbnailImages()) {
            renameFile = new File(workPath + "/" + tempFile.getPath());
            if (renameFile.exists()) {
                renameFile.renameTo(new File(workPath + "/" + tempFile.getPageID() + ".jpg"));
            }
        }
    }

    private void downloadFileByEdit() {
        fileNum = 2 + tempEntry.getThumbnailImages().size()
                + 2 + workEntry.getThumbnailImages().size();

        FileUtil.deleteDirectory(new File(tempPath));
        FileUtil.deleteDirectory(new File(workPath));
        //创建模板和工作的文件夹
        FileUtil.createFileDir(tempPath);
        FileUtil.createFileDir(workPath);
        String downLoadUrl;
        for (TempFile tempFile : tempEntry.getFileList()) {
            if ((tempFile.getTag() != null && tempFile.getTag().equals("datajs")) ||
                    tempFile.getPath().contains(".html")) {
                downLoadUrl = storeUrl + tempFile.getPath();
                final String filePath;
                if (tempFile.getPath().contains("html")) {
                    filePath = tempPath + "/" + tempFile.getPath().substring(0, tempFile.getPath().indexOf(".")) + ".html";
                    orgH5dataFileName = tempFile.getPath();
                } else {
                    filePath = tempPath + "/" + tempFile.getPath().substring(0, tempFile.getPath().indexOf(".")) + ".js";
                }
                httpClient.get(downLoadUrl, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {
                        File file = FileUtil.createFile(filePath);
                        try {
                            FileOutputStream outputStream = new FileOutputStream(file);
                            outputStream.write(bytes);
                            outputStream.flush();
                            outputStream.close();
                        } catch (Exception e) {
                            Toast.makeText(TempEditV4Activity.this, "temp file write error", Toast.LENGTH_LONG).show();
                        }
                        handler.sendEmptyMessage(2);
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        Toast.makeText(TempEditV4Activity.this, "download temp file error in edit", Toast.LENGTH_LONG).show();
                        handler.sendEmptyMessage(2);
                    }
                });
            }
        }

        for (TempFile thumFile : tempEntry.getThumbnailImages()) {
            downLoadUrl = storeUrl + thumFile.getPath();
            final String filePath = tempPath + "/" + thumFile.getPath().substring(0, thumFile.getPath().indexOf(".")) + ".jpg";
            httpClient.get(downLoadUrl, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    File file = FileUtil.createFile(filePath);
                    try {
                        FileOutputStream outputStream = new FileOutputStream(file);
                        outputStream.write(bytes);
                        outputStream.flush();
                        outputStream.close();
                    } catch (Exception e) {
                        Toast.makeText(TempEditV4Activity.this, "temp thum write thing error in edit", Toast.LENGTH_LONG).show();
                    }
                    handler.sendEmptyMessage(2);
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    Toast.makeText(TempEditV4Activity.this, "temp thum download error in edit", Toast.LENGTH_LONG).show();
                    handler.sendEmptyMessage(2);
                }
            });

        }

        for (TempFile tempFile : workEntry.getFileList()) {
            if (tempFile.getTag() != null && (tempFile.getTag().equals("datajs") ||
                    tempFile.getPath().endsWith(".html"))) {
                downLoadUrl = storeUrl + tempFile.getPath();
                final String filePath = workPath + "/" + tempFile.getPath();
                httpClient.get(downLoadUrl, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {
                        File file = FileUtil.createFile(filePath);
                        try {
                            FileOutputStream outputStream = new FileOutputStream(file);
                            outputStream.write(bytes);
                            outputStream.flush();
                            outputStream.close();

                        } catch (Exception e) {
                            handler.sendEmptyMessage(0);
                            Toast.makeText(TempEditV4Activity.this, "work file write error in edit", Toast.LENGTH_LONG).show();
                        }
                        handler.sendEmptyMessage(2);
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        Toast.makeText(TempEditV4Activity.this, "work file download error in edit", Toast.LENGTH_LONG).show();
                        handler.sendEmptyMessage(2);
                    }
                });
            }
        }

        for (TempFile thumFile : workEntry.getThumbnailImages()) {
            downLoadUrl = storeUrl + thumFile.getPath();
            final String filePath = workPath + "/" + thumFile.getPageID() + ".jpg";
            httpClient.get(downLoadUrl, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    File file = FileUtil.createFile(filePath);
                    try {
                        FileOutputStream outputStream = new FileOutputStream(file);
                        outputStream.write(bytes);
                        outputStream.flush();
                        outputStream.close();
                    } catch (Exception e) {
                        Toast.makeText(TempEditV4Activity.this, "work thum write error in edit", Toast.LENGTH_LONG).show();
                    }
                    handler.sendEmptyMessage(2);
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    Toast.makeText(TempEditV4Activity.this, "work thum download error in edit", Toast.LENGTH_LONG).show();
                    handler.sendEmptyMessage(2);
                }
            });
        }
    }


    private void prepareViewDate() {
        initPointContainer();
        //&123
        urlLoad = "file:///" + work.htmlPath + "?edit=" + EDITINDEX_1 + "&os=android";
        webView.setWebViewClient(new CustomWebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (circleBar.isShowing())
                    circleBar.dismiss();
            }
        });
//        webView.setWebChromeClient(new WebChromeClient());
        WebSettings settings = webView.getSettings();
        webView.setDrawingCacheEnabled(true);
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
//        settings.setAppCacheEnabled(false);
        bridge = new JavascriptBridge(webView);
        bridge.addJavaMethod("editor", new JavascriptBridge.Function() {

            @Override
            public Object execute(JSONObject params) {
                Log.e("editor", params.toString());
                parserEditCallBack(params.toString());
                Message message = new Message();
                message.obj = params.toString();
                message.what = 4;
                handler.sendMessage(message);
                return true;
            }
        });
        bridge.addJavaMethod("editor_snap_page", new JavascriptBridge.Function() {
            @Override
            public Object execute(JSONObject params) {
                Log.e("editor_snap_page", params.toString() + "  params params");
                parserWebViewIndex(params.toString());
                nextPage(webViewIndex);
                return true;
            }
        });

        bridge.addJavaMethod("editor_add_page", new JavascriptBridge.Function() {
            @Override
            public Object execute(JSONObject params) {
                Log.e("editor_add_page", params.toString() + "  params params");

                window.showAtLocation(webView, Gravity.CENTER, 0, 0);
                return true;
            }
        });

        bridge.addJavaMethod("editor_preview_page", new JavascriptBridge.Function() {
            @Override
            public Object execute(JSONObject params) {
                Log.e("editor_preview_page", params.toString() + "  params params");
//                handler.sendEmptyMessage(9);
                intentMod = TO_SET;
                if (datajsWork == null || !isChangeCover) {
                    handler.sendEmptyMessage(10);
                    return false;
                }
                reCreateNewDataJsContent();
                handler.sendEmptyMessage(8);
                return true;
            }
        });
        bridge.addJavaMethod("editor_current_page", new JavascriptBridge.Function() {
            @Override
            public Object execute(JSONObject params) {
                Log.e("editor_current_page", params.toString() + "  params params");
                parserWebViewIndex(params.toString());
                if (isSorting) {
                    handler.sendEmptyMessage(6);
                } else {
                    handler.sendEmptyMessage(3);
                }
                handler.sendEmptyMessage(15);
                return true;
            }
        });
        bridge.addJavaMethod("editor_remove_page", new JavascriptBridge.Function() {
            @Override
            public Object execute(JSONObject params) {
                Log.e("editor_remove_page", params.toString() + "  params params");
                parserWebViewIndex(params.toString());
                handler.sendEmptyMessage(7);
                return true;
            }
        });
        bridge.addJavaMethod("editor_share_page", new JavascriptBridge.Function() {
            @Override
            public Object execute(JSONObject params) {
                Log.e("editor_share_page", params.toString() + "  params params");
                intentMod = TO_SHARE;
                if (datajsWork == null || !isChangeCover) {
                    handler.sendEmptyMessage(10);
                    return false;
                }
                handler.sendEmptyMessage(12);
                return true;
            }
        });
        if (!isExist)
            webView.loadUrl(urlLoad + "&hash=" + System.currentTimeMillis());
    }

    private void changeElement(int index) {
//        Toast.makeText(TempEditV3Activity.this, "开始调用reloadPage " + webViewIndex, Toast.LENGTH_LONG).show();
        Bundle params = new Bundle();
        params.putString("page", index + "");
        params.putString("datajsfile", work.dataJsFileName);
        bridge.require("reloadPage", params, new JavascriptBridge.Callback() {

            @Override
            public void onComplate(JSONObject response, String cmd, Bundle params) {
                Log.e("reloadPage", response.toString());
            }
        });
        isElementChange = true;
        if (circleBar.isShowing())
            circleBar.dismiss();
    }

    private void changeCurrentPage(int index) {
        Bundle params = new Bundle();
        params.putString("page", index + "");
        params.putString("datajsfile", work.dataJsFileName);
        bridge.require("seekPage", params, new JavascriptBridge.Callback() {

            @Override
            public void onComplate(JSONObject response, String cmd, Bundle params) {
                Log.e("seekPage", response.toString());
            }
        });
    }

    private void initPointContainer() {
        container.removeAllViews();
        ImageView imageView;
        for (int i = 0; i < work.getPageSize(); i++) {
            imageView = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10, 10);
            params.setMargins(10, 0, 10, 0);
            imageView.setLayoutParams(params);
            imageView.setEnabled(false);
            imageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.point_background));
            container.addView(imageView);
        }
        if (work.getPageSize() > 0)
            container.getChildAt(0).setEnabled(true);
    }


    private SortAdapter sortAdapter;
    private ArrayList<ImageViewState> shortCutViews;
    private View mDrapView;
    private ImageViewState imageViewState;
    private GestureDetector mGestureDetector;
    private int preIndex;
    private float shortCutViewHeight;

    private void initSort() {
        shortCutViewHeight = DPIUtil.screen_height / 6 + DPIUtil.px2dip(this, 10);
        ViewGroup.LayoutParams params = sortRecyclerView.getLayoutParams();
        params.height = (int) shortCutViewHeight;
        sortRecyclerView.setLayoutParams(params);
        shortCutViews = new ArrayList<>();
        shortCutViews = work.getShortCuts(webViewIndex);
        sortAdapter = new SortAdapter(TempEditV4Activity.this, shortCutViews, tempEntry.getWorkStyle().getWidthByHeight());
        sortRecyclerView.setAdapter(sortAdapter);
        sortAdapter.setDrapListener(new RecyclerViewItemDrapListener() {
            @Override
            public boolean onDrap(View v, DragEvent event, int position) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        // Do nothing
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        v.setAlpha(0.5F);
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        v.setAlpha(1F);
                        break;
                    case DragEvent.ACTION_DROP:

                        if (work.sortPage(gson, preIndex, position)) {
//                            sortAdapter.notifyItemChanged(webViewIndex);
//                        progressBar.setVisibility(View.VISIBLE);
                            shortCutViews.remove(imageViewState);
                            imageViewState.setState(true);
                            shortCutViews.add(position, imageViewState);
                            sortAdapter.notifyItemMoved(preIndex, position);
//                            sortAdapter.notifyItemChanged(position);
                            container.getChildAt(pageIndex).setEnabled(false);
                            container.getChildAt(position).setEnabled(true);
                            webViewIndex = position;
                            handler.sendEmptyMessage(14);
                            pageIndex = position;
                        }

//                        urlLoad = "file:///" + work.htmlPath + "?edit=" + EDITINDEX_2 + "&os=android";
//                        reload = true;
//                        webView.reload();
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        v.setAlpha(1F);
                    default:
                        break;
                }
                return true;
            }
        });

        sortAdapter.setOnTuchListener(new RecyclerViewItemOnTuchListener() {
            @Override
            public boolean onTuch(View v, MotionEvent event, int position) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    imageViewState = shortCutViews.get(position);
                    shortCutViews.get(webViewIndex).setState(false);
                    sortAdapter.notifyItemChanged(webViewIndex);
                    shortCutViews.get(position).setState(true);
                    sortAdapter.notifyItemChanged(position);
                    container.getChildAt(pageIndex).setEnabled(false);
                    container.getChildAt(position).setEnabled(true);
                    pageIndex = position;
                }
                mDrapView = v;
                webViewIndex = position;
                preIndex = position;
//                changeElement(position);
                changeCurrentPage(position);
                if (mGestureDetector.onTouchEvent(event))
                    return true;

                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:

                        break;
                    case MotionEvent.ACTION_MOVE:

                        break;
                }

                return false;
            }
        });

        sortAdapter.setClickListener(new RecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.e("sortAdapter", position + "");
//                Bundle params = new Bundle();
//                params.putString("page", position + "");
//                params.putString("datajsfile", work.dataJsFileName);
//                bridge.require("reloadPage", params, new JavascriptBridge.Callback() {
//                    @Override
//                    public void onComplate(JSONObject response, String cmd, Bundle params) {
//                        Log.e("reloadPage", response.toString());
//                    }
//                });
//                imageViewState = shortCutViews.get(position);
//                mDrapView = view;
//                shortCutViews.get(webViewIndex).setState(false);
//                sortAdapter.notifyItemChanged(webViewIndex);
//                shortCutViews.get(position).setState(true);
//                sortAdapter.notifyItemChanged(position);
//                webViewIndex = position;
//                preIndex = position;
////                changeElement(position);
//                changeCurrentPage(position);
            }
        });

        mGestureDetector = new GestureDetector(this, new DrapGestureListener());
    }

    private boolean isSorting;

    public void sortViewOpt(View view) {
        if (isSorting) {
            sortFinished();
        } else {
            sortView();
        }
    }

    private int webviewWidth;
    private int webViewHight;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void sortView() {
//        circleBar.show();
        ViewGroup.LayoutParams layoutParams = webView.getLayoutParams();
        webviewWidth = webView.getWidth();
        webViewHight = webView.getHeight();
        container.setVisibility(View.GONE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            webView.startAnimation(webScaleSmall);

        if (webViewIndex >= work.getPageSize())
            webViewIndex = work.getPageSize() - 1;
        sortBtn.setBackground(getResources().getDrawable(R.drawable.edit_sort_larger));
        webView.clearCache(true);
//
//        urlLoad = "file:///" + work.htmlPath + "?edit=" + EDITINDEX_2 + "&os=android&p=" + webViewIndex;
        isSorting = true;
        Bundle params = new Bundle();
        params.putString("page", webViewIndex + "");
        params.putString("edit", 2 + "");
        bridge.require("switchMode", params, new JavascriptBridge.Callback() {

            @Override
            public void onComplate(JSONObject response, String cmd, Bundle params) {
                Log.e("switchMode", response.toString());
            }
        });
//        reload = true;
//        webView.reload();
//        webView.loadUrl(urlLoad);
        sortRecyclerView.setVisibility(View.VISIBLE);
        shortCutViews.clear();
        shortCutViews = work.getShortCuts(webViewIndex);
        sortAdapter.setList(shortCutViews);
        sortAdapter.notifyDataSetChanged();
        sortRecyclerView.scrollToPosition(webViewIndex);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void sortFinished() {
//        circleBar.show();
        container.setVisibility(View.VISIBLE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            webView.startAnimation(webScaleBig);
        sortBtn.setBackground(getResources().getDrawable(R.drawable.edit_sort));
//        urlLoad = "file:///" + work.htmlPath + "?edit=" + EDITINDEX_1 + "&os=android&p=" + webViewIndex;
        isSorting = false;
        sortRecyclerView.setVisibility(View.GONE);
//        reload = true;
//        webView.reload();
//        handler.sendEmptyMessage(3);
        Bundle params = new Bundle();
        params.putString("page", webViewIndex + "");
        params.putString("edit", 1 + "");
        bridge.require("switchMode", params, new JavascriptBridge.Callback() {

            @Override
            public void onComplate(JSONObject response, String cmd, Bundle params) {
                Log.e("switchMode", response.toString());
            }
        });
    }


    //截图的页面位置
    int webViewIndex = 0;
    //当前页码
    int pageIndex = 0;

    private void parserWebViewIndex(String json) {
        Type type = new TypeToken<ShortCutDataEntry>() {
        }.getType();
        ShortCutDataEntry entry = gson.fromJson(json, type);
        webViewIndex = entry.getData().getPageIndex();
        if (webViewIndex >= work.getPageSize()) {
            sortBtn.setEnabled(false);
//            sortBtn.setBackgroundResource(R.mipmap.editor_tailoring);
        } else {
            sortBtn.setEnabled(true);
//            sortBtn.setBackgroundResource(R.drawable.edit_sort);
        }
    }

    /**
     * 解析html操作返回的数据
     *
     * @param json
     */
    private void parserEditCallBack(String json) {
        Type type = new TypeToken<EditCallBackEntry>() {
        }.getType();
        editCallBackEntry = gson.fromJson(json, type);
    }

    private void nextPage(int index) {
//        handler.sendEmptyMessage(3);
//        if (index != work.getPageSize() && work.pages.get(index).isModify()) {
        handler.sendEmptyMessageDelayed(17, 300);
//        getShortCut(index);
//        }

    }

    private void getShortCut(int index) {
        String shortName = work.setShortCut(index);
        Bitmap bitmap = webView.getDrawingCache();
        if (tempEntry.getWorkStyle().getWidthByHeight() > 1)
            bitmap = rotateBitmap(270, bitmap);
        File file = new File(workPath + "/" + shortName);
        if (file.exists())
            file.delete();
        try {
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, outputStream);
            outputStream.flush();
            outputStream.close();
            bitmap.recycle();
            webView.destroyDrawingCache();
        } catch (Exception e) {
            Log.e("shortcut", e.getMessage());
        }
    }


    public void editTextFocusListener() {
        inputLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputEdittext.setText("");
                inputLay.setVisibility(View.GONE);
                container.setVisibility(View.VISIBLE);
                inputMethodManager.hideSoftInputFromWindow(inputEdittext.getWindowToken(), 0);
            }
        });
    }

    //显示输入框
    private void editText() {
        container.setVisibility(View.GONE);
        inputLay.setVisibility(View.VISIBLE);
        inputEdittext.requestFocus();
        inputEdittext.setText(editCallBackEntry.getData().getValue());
        inputEdittext.setSelection(editCallBackEntry.getData().getValue().length());
        inputMethodManager.showSoftInput(inputEdittext, InputMethodManager.SHOW_FORCED);
    }

    //清除编辑内容
    public void editTextClear(View view) {
        inputEdittext.setText("");
    }

    //编辑完成时调用
    public void editTextFinish(View view) {
        String edit = inputEdittext.getText().toString();
        inputEdittext.setText("");
        if (edit.length() != 0) {
            if (work.updateH5dataJs(gson, webViewIndex, editCallBackEntry.getData().getKey(), edit)) {
                work.pages.get(webViewIndex).setIsModify(true);
                inputLay.setVisibility(View.GONE);
                inputMethodManager.hideSoftInputFromWindow(inputEdittext.getWindowToken(), 0);
                webView.clearCache(true);
                handler.sendEmptyMessage(14);
            }
        } else {
            inputLay.setVisibility(View.GONE);
        }
        container.setVisibility(View.VISIBLE);

//        urlLoad = "file:///" + work.htmlPath + "?edit=" + EDITINDEX_1 + "&os=android&p=" + webViewIndex;
//        reload = true;
//        webView.reload();
    }

    private void removePage(int index) {
//        progressBar.setVisibility(View.VISIBLE);
        if (work.removePage(gson, index)) {
//            Toast.makeText(TempEditV3Activity.this, "remove->write js success", Toast.LENGTH_LONG).show();
            container.removeViewAt(webViewIndex);
//            urlLoad = "file:///" + work.htmlPath + "?edit=" + EDITINDEX_2 + "&os=android";
//            webView.reload();
//            shortCutViews.clear();
//            shortCutViews = work.getShortCuts(webViewIndex);
//            sortAdapter.notifyDataSetChanged();
            shortCutViews.remove(index);
            sortAdapter.notifyItemRemoved(index);
            if (pageIndex >= shortCutViews.size())
                pageIndex = shortCutViews.size() - 1;
            if (webViewIndex >= shortCutViews.size())
                webViewIndex = shortCutViews.size()-1;
            shortCutViews.get(webViewIndex).setState(true);
            sortAdapter.notifyItemChanged(webViewIndex);
            container.getChildAt(pageIndex).setEnabled(false);
            container.getChildAt(webViewIndex).setEnabled(true);
            pageIndex = webViewIndex;
            handler.sendEmptyMessage(14);
        } else {
            if (circleBar.isShowing())
                circleBar.dismiss();
            Toast.makeText(TempEditV4Activity.this, "remove->write js error", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * **********切换音乐*************
     */
    private String musicId = "0";
    private String singer = "";
    private String musicName = "";
    private String coverImageURL = "";

    public void changeMusic(View view) {
        Intent intent = new Intent(this, ChangeMusicActivity.class);
        startActivityForResult(intent, 100);
    }


    private PopupWindow window;

    private ArrayList<TempFile> tempImgPathList;

    private void initAddPagePopWindow() {
        View view = LayoutInflater.from(this).inflate(R.layout.tempedit_pop, null);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.tempedit_pop_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);

        window = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, 500);
        window.setBackgroundDrawable(new BitmapDrawable());
        window.setOutsideTouchable(true);
        window.setFocusable(true);
        tempImgPathList = new ArrayList<>();
        a:
        for (HashMap<String, String> map : work.orgPages) {
            String id = map.get("id");
            for (TempFile tempFile : tempEntry.getThumbnailImages()) {
                if (tempFile.getPageID().equals(id)) {
                    tempImgPathList.add(tempFile);
                    continue a;
                }
            }
        }
//        tempImgPathList = tempEntry.getThumbnailImages();
        int width = DPIUtil.getScreenMetrics(TempEditV4Activity.this).getWidth() / 3;
        int hight = Math.round(width / tempEntry.getWorkStyle().getWidthByHeight());
        TempEditPopAdapter adapter = new TempEditPopAdapter(tempImgPathList, tempPath, width, hight);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new RecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Message message = new Message();
                message.obj = position;
                message.what = 5;
                handler.sendMessage(message);
                window.dismiss();
            }
        });
    }

    boolean isNameParamExist = false;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 100:
                    musicId = data.getStringExtra("musicId");
                    singer = data.getStringExtra("singer");
                    musicName = data.getStringExtra("musicName");
                    coverImageURL = data.getStringExtra("coverImageURL");
                    isMusicInUse = true;
                    music_btn.setBackgroundResource(R.mipmap.editor_musiced);
                    dataJsMusic = null;
                    dataJsMusic = new DataJsMusic(singer, coverImageURL, musicId, musicName);
                    break;

                case PHOTO_ZOOM:
//                    Aibum aibum = (Aibum) data.getSerializableExtra("ainum");
//                    Intent intents = new Intent(TempEditV3Activity.this, ChosePhotoActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("ainum", aibum);
//                    intents.putExtras(bundle);
//                    startActivityForResult(intents, PHOTO_CHOSE);
                    try {
                        ContentResolver resolver = getContentResolver();
                        Uri uri = data.getData();
                        String[] proj = {MediaStore.Images.Media.DATA};
                        Cursor cursor = managedQuery(uri, proj, null, null, null);
                        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        cursor.moveToFirst();
                        Log.e("CropImage", "TempEdit photo zoom");
                        String path = cursor.getString(column_index);
                        Intent intent = new Intent(TempEditV4Activity.this, PhotoCropActivity.class);
                        intent.putExtra("orgPath", workPath);
                        intent.putExtra("path", path);
                        intent.putExtra("width", editCallBackEntry.getData().getWidth());
                        intent.putExtra("hight", editCallBackEntry.getData().getHeight());
                        if (editCallBackEntry.getData().getValue().contains("http")) {
                            String name = editCallBackEntry.getData().getValue().substring(storeUrl.length());
                            name = name.substring(0, name.lastIndexOf("."));
                            ArrayList<NameParam> list = new ArrayList<>();
                            list = work.pool.getUsedNameParams();
                            isNameParamExist = false;
                            for (NameParam param : list) {
                                if (param.getName().equals(name)) {
                                    isNameParamExist = true;
                                    break;
                                }
                            }
                            Log.e("CropImage", "TempEdit photo zoom2");
                            if (isNameParamExist) {
                                name = editCallBackEntry.getData().getValue().substring(storeUrl.length());
                            } else {
                                name = work.pool.getPrepNameParam().getName() + editCallBackEntry.getData().getValue().substring(editCallBackEntry.getData().getValue().lastIndexOf("."));
                            }
                            intent.putExtra("name", name);

                        } else {
                            intent.putExtra("name", editCallBackEntry.getData().getValue());
                        }

                        startActivityForResult(intent, PHOTO_CROP);
                    } catch (Exception e) {
                        Toast.makeText(TempEditV4Activity.this, "路径识别失败，请选择系统本地相册内容！", Toast.LENGTH_SHORT).show();
                    }

                    break;

                case PHOTO_CHOSE:
                    String path = data.getStringExtra("imagepath");
                    Intent intent = new Intent(TempEditV4Activity.this, PhotoCropActivity.class);
                    intent.putExtra("orgPath", workPath);
                    intent.putExtra("path", path);
                    intent.putExtra("width", editCallBackEntry.getData().getWidth());
                    intent.putExtra("hight", editCallBackEntry.getData().getHeight());
                    if (editCallBackEntry.getData().getValue().contains("http")) {
                        String name = editCallBackEntry.getData().getValue().substring(storeUrl.length());
                        name = name.substring(0, name.lastIndexOf("."));
                        ArrayList<NameParam> list = new ArrayList<>();
                        list = work.pool.getUsedNameParams();
                        isNameParamExist = false;
                        for (NameParam param : list) {
                            if (param.getName().equals(name)) {
                                isNameParamExist = true;
                                break;
                            }
                        }
                        if (isNameParamExist) {
                            name = editCallBackEntry.getData().getValue().substring(storeUrl.length());
                        } else {
                            name = work.pool.getPrepNameParam().getName() + editCallBackEntry.getData().getValue().substring(editCallBackEntry.getData().getValue().lastIndexOf("."));
                        }
                        intent.putExtra("name", name);

                    } else {
                        intent.putExtra("name", editCallBackEntry.getData().getValue());
                    }

                    startActivityForResult(intent, PHOTO_CROP);
                    break;

                case PHOTO_CROP:
                    String name = editCallBackEntry.getData().getValue();
                    if (name.contains("http") && isNameParamExist) {
                        String newName = name.substring(storeUrl.length());
                        work.setFileUploadState(newName.substring(0, newName.lastIndexOf(".")), false);
                        work.pages.get(webViewIndex).setIsModify(true);
                        work.pages.get(webViewIndex).getPage().put(editCallBackEntry.getData().getKey(), newName);
                        work.jsPage.pageInfo = gson.toJson(work.getPagesInfo());
                        String json = work.jsPage.pre + work.jsPage.pageInfo + work.jsPage.next;
                        FileUtil.writeData(work.dataJsPath, json);
                    } else if (name.contains("http") && !isNameParamExist) {
                        String newName = work.pool.getUseableNameParam().getName();
                        work.pages.get(webViewIndex).setIsModify(true);
                        work.pages.get(webViewIndex).getPage().put(editCallBackEntry.getData().getKey(), newName + editCallBackEntry.getData().getValue().substring(editCallBackEntry.getData().getValue().lastIndexOf(".")));
                        work.jsPage.pageInfo = gson.toJson(work.getPagesInfo());
                        String json = work.jsPage.pre + work.jsPage.pageInfo + work.jsPage.next;
                        FileUtil.writeData(work.dataJsPath, json);
                    } else {
                        name = name.substring(0, name.lastIndexOf("."));
                        work.setFileUploadState(name, false);
                        work.pages.get(webViewIndex).setIsModify(true);
                    }

                    webView.clearCache(true);
                    handler.sendEmptyMessage(14);
                    break;

                case SHOT_CHOSE:
                    String shot = data.getStringExtra("shotPath");
                    Intent cropIntent = new Intent(TempEditV4Activity.this, PhotoCropActivity.class);
                    cropIntent.putExtra("orgPath", workPath);
                    cropIntent.putExtra("path", workPath + "/" + shot);
                    cropIntent.putExtra("width", "256");
                    cropIntent.putExtra("hight", "256");
                    cropIntent.putExtra("name", "shareimg_" + MD5Util.MD5(miKey + "") + ".jpg");
                    startActivityForResult(cropIntent, SHOT_CUT);
                    break;

                case SHOT_CUT:
                    isChangeCover = true;
                    ImageOptUtil.imageLoader.displayImage("file://" + workPath + "/" + "shareimg_" + MD5Util.MD5(miKey + "") + ".jpg", shortImg, ImageOptUtil.image_display_options_no_cache);
                    break;

            }
        }
    }

    private boolean isExist;
    /**
     * 结束activity
     * @param view
     */
    public void finishActivity(View view) {
        if (!(createMod == MODIFY) && !isElementChange) {
            if (miKey == null || miKey == 0) {
                isExist = true;
                TempEditV4Activity.this.finish();
            } else {
                deletUndoWork(miKey + "");
            }
        } else if ((createMod == MODIFY) && !isElementChange) {
            FileUtil.deleteDirectory(new File(workPath));
            TempEditV4Activity.this.finish();
        } else {
            dialogExist.show();
            ButtonFlat flat = dialogExist.getButtonAccept();
            flat.setText(getString(R.string.save));
            dialogExist.setButtonAccept(flat);
        }

    }


    private class DrapGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            ClipData data = ClipData.newPlainText("", "");
            MyDragShadowBuilder shadowBuilder = new MyDragShadowBuilder(
                    mDrapView);
            mDrapView.startDrag(data, shadowBuilder, mDrapView, 0);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }

    private class MyDragShadowBuilder extends View.DragShadowBuilder {

        private final WeakReference<View> mView;

        public MyDragShadowBuilder(View view) {
            super(view);
            mView = new WeakReference<View>(view);
        }

        @Override
        public void onDrawShadow(Canvas canvas) {
            canvas.scale(1.5F, 1.5F);
            super.onDrawShadow(canvas);
        }

        @Override
        public void onProvideShadowMetrics(Point shadowSize,
                                           Point shadowTouchPoint) {

            final View view = mView.get();
            if (view != null) {
                shadowSize.set((int) (view.getWidth() * 1.5F),
                        (int) (view.getHeight() * 1.5F));
                shadowTouchPoint.set(shadowSize.x / 2, shadowSize.y / 2);
            } else {

            }
        }

    }


    /**
     * ***弹出框******
     */
    private WebView mWebView;
    private PopupWindow popupWindow;
    private RelativeLayout popContainer;
    private float screenWByH;
    private TextView popTitle, popAuthor;
    private Button popHotPoint, popEdit, popShare;

    private void initPreview() {

        View popView = inflater.inflate(R.layout.main_popupwindow_layout, null);
        mWebView = (WebView) popView.findViewById(R.id.main_pop_webview);
        popTitle = (TextView) popView.findViewById(R.id.main_pop_title);
        popAuthor = (TextView) popView.findViewById(R.id.main_pop_author);
        popHotPoint = (Button) popView.findViewById(R.id.main_pop_hotpoint);
        popEdit = (Button) popView.findViewById(R.id.main_pop_toedit);
        popShare = (Button) popView.findViewById(R.id.main_pop_share);
        popContainer = (RelativeLayout) popView.findViewById(R.id.main_pop_container);

        popTitle.setText(tempEntry.getTitle());
        popAuthor.setVisibility(View.GONE);
        popHotPoint.setVisibility(View.GONE);
        popEdit.setBackgroundDrawable(getResources().getDrawable(R.drawable.to_share_btn));
        popShare.setVisibility(View.GONE);

        ViewGroup.LayoutParams paramsE = popEdit.getLayoutParams();
        paramsE.width = DPIUtil.dip2px(this, 25);
        paramsE.height = DPIUtil.dip2px(this, 25);
        popEdit.setLayoutParams(paramsE);

        screenWByH = tempEntry.getWorkStyle().getWidthByHeight();
//        mWebView.setWebViewClient(new CustomWebViewClient());
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        //扩大比例的缩放
        mWebView.getSettings().setUseWideViewPort(true);
        //自适应屏幕
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWebView.getSettings().setLoadWithOverviewMode(true);

        float width = DPIUtil.screen_width - DPIUtil.dip2px(this, 120);
        float hight = width / screenWByH;
        if (screenWByH > 1)
            hight = width * screenWByH;
        ViewGroup.LayoutParams params = popContainer.getLayoutParams();
        params.width = Math.round(width);
        params.height = Math.round(hight);
        popContainer.setLayoutParams(params);

        popupWindow = new PopupWindow(popView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, false);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setAnimationStyle(R.style.popupwindowStyle);

        popEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentMod = TO_SHARE;
                prepareData4uploadWork();
                updateWorkDetial();
            }
        });
        mWebView.loadUrl("file:///" + work.htmlPath + "?edit=0&os=android");
    }

    private void workPreview() {
        popTitle.setText(title);

        mWebView.reload();
        popupWindow.showAtLocation(webView, Gravity.CENTER, 0, 0);
    }

    private PopupWindow setWindow;
    private EditText titleEdit, passWordEdit;
    private TextView music, pass;
    private ImageView shortImg, musicImg, passImg;
    private Button setCancel, setOk, musicBtn, passBtn, musicCoverBtn;

    private boolean isMusicInUse, isPassWordInUse;
    private String passWord;
    private String title;
    private View setView;
    private RelativeLayout music_cover_lay;
    //分享模式是否是音乐模式
    private boolean isMusicModle;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initEditSet() {
        setView = inflater.inflate(R.layout.pop_tempedit_set_lay, null);
        titleEdit = (EditText) setView.findViewById(R.id.edit_set_title);
        passWordEdit = (EditText) setView.findViewById(R.id.edit_set_password_text);
        music = (TextView) setView.findViewById(R.id.edit_set_music_name);
        shortImg = (ImageView) setView.findViewById(R.id.edit_set_cover);
        setCancel = (Button) setView.findViewById(R.id.edit_set_cancel);
        setOk = (Button) setView.findViewById(R.id.edit_set_ok);
        musicBtn = (Button) setView.findViewById(R.id.edit_set_music_btn);
        passBtn = (Button) setView.findViewById(R.id.edit_set_password_btn);
        musicImg = (ImageView) setView.findViewById(R.id.edit_set_music_img);
        passImg = (ImageView) setView.findViewById(R.id.edit_set_password_img);
        pass = (TextView) setView.findViewById(R.id.edit_set_nopassword_text);
        music_cover_lay = (RelativeLayout) setView.findViewById(R.id.music_cover_lay);
        musicCoverBtn = (Button) setView.findViewById(R.id.edit_set_music_cover_btn);

        setWindow = new PopupWindow(setView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, false);
        setWindow.setBackgroundDrawable(new BitmapDrawable());
        setWindow.setOutsideTouchable(true);
        setWindow.setFocusable(true);
        setWindow.setAnimationStyle(R.style.popupwindowStyle);

        if (isPassWordInUse) {
            passWordEdit.setHint("有密码");
        }

        ImageOptUtil.imageLoader.displayImage(storeUrl + "shareimg_" + MD5Util.MD5(miKey + "") + ".jpg",
                shortImg, ImageOptUtil.getImage_display_options(R.mipmap.error_img, R.mipmap.edit_cover, R.mipmap.edit_cover, 0, false, false));

        shortImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> shortImgs = work.getShortCuts();
                Intent intent = new Intent(TempEditV4Activity.this, ChoicePhotoActivity.class);
                intent.putStringArrayListExtra("shots", shortImgs);
                intent.putExtra("path", workPath);
                startActivityForResult(intent, SHOT_CHOSE);
            }
        });

        musicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMusicInUse) {
                    isMusicInUse = false;
                    setMusicState(false);
                } else {
                    isMusicInUse = true;
                    setMusicState(true);
                }
            }
        });

        passBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPassWordInUse) {
                    isPassWordInUse = false;
                    setPassWordState(false);
                } else {
                    isPassWordInUse = true;
                    setPassWordState(true);
                }
            }
        });

        setCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setWindow.dismiss();
            }
        });

        setOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Util.isStringNotEmpty(titleEdit.getText().toString())) {
                    Toast.makeText(TempEditV4Activity.this, R.string.input_title, Toast.LENGTH_LONG).show();
                    return;
                }
                if ((createMod == CREATE) && !isChangeCover) {
                    Toast.makeText(TempEditV4Activity.this, R.string.set_cover, Toast.LENGTH_LONG).show();
                    return;
                }

                title = titleEdit.getText().toString();
                datajsWork = new DataJsWork(isPassWordInUse, miKey + "", false, titleEdit.getText().toString(), storeUrl + "shareimg_" + MD5Util.MD5(miKey + "") + ".jpg");
                dataJsAuthor = new DataJsAuthor(
                        SharedPreferenceUtil.getAccountSharedPreference(TempEditV4Activity.this).getString(SharedPreferenceUtil.QRCODE, ""),
                        SharedPreferenceUtil.getAccountSharedPreference(TempEditV4Activity.this).getString(SharedPreferenceUtil.USERNAME, ""),
                        tempEntry.getTitle(),
                        SharedPreferenceUtil.getAccountSharedPreference(TempEditV4Activity.this).getString(SharedPreferenceUtil.HEADURL, ""));
                if (!isMusicInUse) {
                    dataJsMusic = new DataJsMusic("", "", "", "");
                } else {
//                    dataJsMusic = new DataJsMusic(singer, coverImageURL, musicId, musicName);
                }
                if (intentMod == TO_SET) {
                    updateWorkDetial();
                } else {
                    prepareData4uploadWork();
                    updateWorkDetial();
                }
                isElementChange = true;
                setWindow.dismiss();
            }
        });

        musicCoverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (isMusicModle) {
                   isMusicModle = false;
                   musicCoverBtn.setBackground(getResources().getDrawable(R.mipmap.edit_set_nouse));
               } else {
                   isMusicModle = true;
                   musicCoverBtn.setBackground(getResources().getDrawable(R.mipmap.edit_set_inuse));
               }
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void showSetView() {
        if (Util.isStringNotEmpty(musicName) && Util.isStringNotEmpty(musicId)) {
            musicBtn.setClickable(true);
        } else {
            musicBtn.setClickable(false);
        }
        if (!isMusicInUse) {
            setMusicState(false);
        } else {
            setMusicState(true);
            music.setText(dataJsMusic.getMusicName());
        }

        //Util.isStringNotEmpty(passWord)
        if (isPassWordInUse) {
            setPassWordState(true);
        } else {
            setPassWordState(false);
//            passWordEdit.setText(passWord);
        }

        if (Util.isStringNotEmpty(title)) {
            titleEdit.setText(title);
        }
        setWindow.showAtLocation(webView, Gravity.CENTER, 0, 0);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setMusicState(boolean isMusic) {
        if (isMusic) {
            music_btn.setBackgroundResource(R.mipmap.editor_musiced);
            music.setText(musicName);
            musicImg.setBackground(getResources().getDrawable(R.mipmap.edit_set_song));
            musicBtn.setBackground(getResources().getDrawable(R.mipmap.edit_set_inuse));
            if (isMusicModle) {
                musicCoverBtn.setBackground(getResources().getDrawable(R.mipmap.edit_set_inuse));
            } else {
                musicCoverBtn.setBackground(getResources().getDrawable(R.mipmap.edit_set_nouse));
            }
            music_cover_lay.setVisibility(View.VISIBLE);
        } else {
            music_btn.setBackgroundResource(R.mipmap.editor_music);
            music.setText(R.string.no_music);
            musicImg.setBackground(getResources().getDrawable(R.mipmap.edit_set_nosong));
            musicBtn.setBackground(getResources().getDrawable(R.mipmap.edit_set_nouse));
            music_cover_lay.setVisibility(View.GONE);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setPassWordState(boolean isPassWord) {
        if (isPassWord) {
            passImg.setBackground(getResources().getDrawable(R.mipmap.edit_set_lock));
            passWordEdit.setVisibility(View.VISIBLE);
            pass.setVisibility(View.GONE);
            passBtn.setBackground(getResources().getDrawable(R.mipmap.edit_set_inuse));
        } else {
            passImg.setBackground(getResources().getDrawable(R.mipmap.edit_set_nolock));
            passWordEdit.setVisibility(View.GONE);
            pass.setVisibility(View.VISIBLE);
            passBtn.setBackground(getResources().getDrawable(R.mipmap.edit_set_nouse));
        }
    }

    private void reCreateNewDataJsContent() {
        String dataJsConfig = "var config = { \"work\" : " + gson.toJson(datajsWork)
                + ", \"author\" : " + gson.toJson(dataJsAuthor)
                + ", \"music\" : " + gson.toJson(dataJsMusic) + "} \n var h5data = " + gson.toJson(work.getPagesInfo()) + ";";
        Log.e("dataJsConfig", dataJsConfig);
        FileUtil.writeData(work.dataJsPath, dataJsConfig);
    }

    /**
     * 把本地js里面的图片去掉http前缀，改为本地寻址
     */
    private void reCreateNewDataJsContentNoUrl() {
        ArrayList<HashMap<String, String>> hashMaps = work.getPagesInfo();
        for (int i = 0; i < hashMaps.size(); i++) {
            HashMap<String, String> hashMap = hashMaps.get(i);
            Set<String> iterator = hashMap.keySet();
            for (String key : iterator) {
                if (key.contains("img") && (hashMap.get(key).contains("http"))) {
                    String value = hashMap.get(key);
                    int index = value.lastIndexOf("/");
                    if (index != -1) {
                        value = value.substring(index + 1);
                        hashMaps.get(i).put(key, value);
                    }
                }
            }
        }
        String dataJsConfig = "var config = { \"work\" : " + gson.toJson(datajsWork)
                + ", \"author\" : " + gson.toJson(dataJsAuthor)
                + ", \"music\" : " + gson.toJson(dataJsMusic) + "} \n var h5data = " + gson.toJson(hashMaps) + ";";
        Log.e("work", dataJsConfig);
        FileUtil.writeData(FileUtil.UPLOAD_PATH + "/" + miKey + "/" + work.dataJsFileName, dataJsConfig);
    }

    private void initJsConfig() {

//        String js = TempEditUtil.getJsonFromFile(workPath + "/" + work.dataJsFileName);
        String js = work.jsPage.pre;
        if (js != null && Util.isStringNotEmpty(js)) {
            if (js.contains("var config =") && (js.contains("var h5data =") || js.contains("var h5data="))) {
                int preIndex = js.indexOf("var config =") + "var config =".length();
                int lastIndex = js.indexOf("var h5data =");
                if (lastIndex == -1) {
                    lastIndex = js.indexOf("var h5data=");
                }
                String json = js.substring(preIndex, lastIndex);
                Log.e("init js config", json);
                try {
                    Type type = new TypeToken<JsConfig>() {
                    }.getType();
                    JsConfig jsConfig = gson.fromJson(json, type);
                    if (jsConfig != null) {
                        datajsWork = jsConfig.getWork();
                        dataJsAuthor = jsConfig.getAuthor();
                        if (createMod == CREATE) {
                            dataJsMusic = new DataJsMusic("", "", "", "");;
                        } else {
                            dataJsMusic = jsConfig.getMusic();
                        }

                        if (dataJsMusic != null && Util.isStringNotEmpty(dataJsMusic.getMusicId())) {
                            isMusicInUse = true;
                            ShareDBEntry dbEntry = dbAdapter.getShareContent(miKey + "");
                            if (dbEntry != null) {
                                String musicModle = dbEntry.getIsMusicModle();
                                if ((musicModle != null) && musicModle.equals("1")) {
                                    isMusicModle = true;
                                }
                            }
                            music_btn.setBackgroundResource(R.mipmap.editor_musiced);
                        } else {
                            isMusicInUse = false;
                            music_btn.setBackgroundResource(R.mipmap.editor_music);
                        }
                        if (datajsWork != null && datajsWork.getWorkImageURL() != null && datajsWork.getWorkImageURL().contains(MD5Util.MD5(miKey + ""))) {
                            isChangeCover = true;
                        } else {
                            isChangeCover = false;
                        }
                        if (datajsWork != null && Util.isStringNotEmpty(datajsWork.getWorkName()) && !(createMod == CREATE)) {
                            title = datajsWork.getWorkName();
                        } else {
                            title = "";
                        }
                    }
                } catch (Exception e) {
                    Log.e("init js config", "error");
                }
            }
        }
    }


    /**
     * 上传页面图片
     *
     * @param index
     */
    private void upLoadImage(int index) {
        //上传结束要把work.pages.get(index).setIsModify(false);
        HashMap<String, String> map = work.getCurrentPage(index).getPage();
        Set<String> set = map.keySet();
        for (String key : set) {
            if (key.contains("img")) {
                final String name = map.get(key);
                if (!name.contains("http") && !work.pool.getFileUploadState(name.substring(0, name.lastIndexOf(".")))) {
                    try {
                        RequestParams params = RequestParam.getRequestParams(TempEditV4Activity.this);
                        params.put("worksId", miKey);
                        File file = new File(workPath + "/" + name);
                        if (file.exists()) {
                            params.put("profile_picture", file);
                            httpClient.post(uploadUrl, params, new AsyncHttpResponseCallBack(TempEditV4Activity.this) {
                                @Override
                                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                                    super.onSuccess(i, headers, bytes);
                                    Log.e("TempEdit", i + "");
                                    work.pool.setNameParamUploadState(name.substring(0, name.lastIndexOf(".")), true);
                                    Log.e("TempEdit", name + " set upload state true");
                                }

                                @Override
                                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                                    Log.e("TempEdit", name + " img upload fail");
                                    Toast.makeText(TempEditV4Activity.this, "单张图片上传失败", Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            work.pool.setNameParamUploadState(name.substring(0, name.lastIndexOf(".")), true);
                        }

                    } catch (Exception e) {

                    }

                }
            }
        }
        work.pages.get(index).setIsModify(false);
    }

    /**
     * 将mikey，标题等信息保存到数据库
     */
    private void saveWorkInfoToDB() {
        ShareDBEntry shareDBEntry = new ShareDBEntry();
        shareDBEntry.setId(miKey + "");
        shareDBEntry.setTitle(title);
        shareDBEntry.setContent("");
        if (isMusicModle) {
            shareDBEntry.setIsMusicModle("1");
        } else {
            shareDBEntry.setIsMusicModle("0");
        }

        if (isMusicInUse) {
            shareDBEntry.setIsMusicInuse("1");
        } else {
            shareDBEntry.setIsMusicInuse("0");
        }

        shareDBEntry.setMusicTitle(dataJsMusic.getMusicName());
        shareDBEntry.setMusicAuthor(dataJsMusic.getSinger());
        dbAdapter.updateShareMusic(shareDBEntry);
    }


    /**
     * 上传work前，数据准备
     */
    private void prepareData4uploadWork() {
        circleBar.show();

        ArrayList<TempFile> fileListUploadconfig = new ArrayList<>();
        ArrayList<TempFile> thumList = new ArrayList<>();

        FileUtil.createNewFileDir(FileUtil.UPLOAD_PATH + "/" + miKey);
        //这里有更好的办法改进
        HashMap map = new HashMap();
        for (int i = 0; i < work.pool.getUsedNameParams().size(); i++) {
            if (!work.pool.getUsedNameParams().get(i).getIsUpload()) {
                map.put(i + "", work.pool.getUsedNameParams().get(i).getName());
//                work.pool.getUsedNameParams().get(i).setIsUpload(true);
                Log.e("uploadwork", work.pool.getUsedNameParams().get(i).getName());
            }
        }
        File dir = new File(workPath);
        //把文件复制到上传专用的文件夹中
        for (File file : dir.listFiles()) {
            String name = file.getName();
            if (name.equals("namepool.txt"))
                continue;
            if (map.containsValue(name.substring(0, name.lastIndexOf("."))) || name.endsWith("html")) {
                FileUtil.copyFile(workPath + "/" + name, FileUtil.UPLOAD_PATH + "/" + miKey + "/" + name);
            }
        }

        TempFile tempFile;

        for (NameParam param:work.pool.getUsedNameParams()) {
            tempFile = new TempFile();
            if (work.dataJsFileName.contains(param.getName())) {
                tempFile.setTag("datajs");
                tempFile.setPath(work.dataJsFileName);
            } else if (work.htmlFileName.contains(param.getName())){
                tempFile.setTag("");
                tempFile.setPath(work.htmlFileName);
            } else {
                tempFile.setTag("");
                tempFile.setPath(param.getName() + ".jpg");
            }
            fileListUploadconfig.add(tempFile);
        }

        reCreateNewDataJsContentNoUrl();

        //缩略图处理
        for (int i = 0; i < work.pages.size(); i++) {
            String name = work.pool.getThumpName(i);
            if (name != null) {
                Log.e("thump", name);
                FileUtil.copyFile(workPath + "/" + work.pages.get(i).getShortCut(), FileUtil.UPLOAD_PATH + "/" + miKey + "/" + name + ".jpg");
                tempFile = new TempFile();
                tempFile.setPath(name + ".jpg");
                tempFile.setPageID(work.pages.get(i).getShortCut().substring(0, work.pages.get(i).getShortCut().lastIndexOf(".")));
                tempFile.setTag("");
                thumList.add(tempFile);
            }
        }

        config.setWorksId(miKey + "");
        config.setUserId(userId);
        config.setTemplateId(tempEntry.getWorksId());
        config.setTemplateFlag(tempEntry.getTemplateFlag());
        config.setTemplateTag("");
        config.setRootTemplateId(tempEntry.getRootTemplateId());
        config.setTitle(datajsWork.getWorkName());
        config.setFileList(fileListUploadconfig);
        config.setThumbnailImages(thumList);
        config.setWorkStyle(tempEntry.getWorkStyle());
        if (!isMusicInUse) {
            musicId = "0";
        }
        config.setMusicId(musicId);
        config.setMemo("test");
        ArrayList<HashMap<String, String>> pageHash = work.getPagesInfo();
        ArrayList<TextContent> textContents = new ArrayList<>();
        TextContent textContent;
        for (HashMap<String, String> hashMap : pageHash) {
            Set<String> set = hashMap.keySet();
            for (String key : set) {
                if (key.startsWith("text")) {
                    textContent = new TextContent();
                    textContent.setContent(hashMap.get(key));
                    textContents.add(textContent);
                }
            }
        }

        config.setTextContent(textContents);
        String configJson = gson.toJson(config);
        FileUtil.writeData(FileUtil.UPLOAD_PATH + "/" + miKey + "/config.txt", configJson);
    }


    /**
     * 将图片纠正到正确方向
     *
     * @param degree ： 图片被系统旋转的角度
     * @param bitmap ： 需纠正方向的图片
     * @return 纠向后的图片
     */
    public static Bitmap rotateBitmap(int degree, Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);

        Bitmap bm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
        return bm;
    }

    private void updateWorkDetial() {
        if (!circleBar.isShowing())
            circleBar.show();

        saveWorkInfoToDB();

        String url = HttpConfig.UPDATEWORKSDETIAL;
        RequestParams params = RequestParam.getRequestParams(TempEditV4Activity.this);
        String paramWork = "{WorksId:\"" + miKey +
                "\", Title:\"" + datajsWork.getWorkName() +
                "\", WXShareImg:\"" + "shareimg_" + MD5Util.MD5(miKey + "") + ".jpg" + "\"}";
        params.put("works", paramWork);
        params.put("password", passWordEdit.getText().toString());
        if (!(createMod == MODIFY)) {
            params.put("config", "");
            params.put("datajsName", "");
        } else {
            params.put("config", gson.toJson(config));
            params.put("datajsName", work.dataJsFileName);
        }

        if (isPassWordInUse) {
            params.put("isClosePassword", 0);
        } else {
            params.put("isClosePassword", 1);
        }

        params.put("viewPassword", "");
        Log.e("param", params.toString());
        try {
            params.put("profile_picture", new File(workPath + "/" + "shareimg_" + MD5Util.MD5(miKey + "") + ".jpg"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        httpClient.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                Log.e("updateWorkDetial", new String(bytes));
                handler.sendEmptyMessage(13);
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Log.e("error updateWorkDetial", new String(bytes));
                if (circleBar.isShowing())
                    circleBar.dismiss();
                Toast.makeText(TempEditV4Activity.this, "updateWorkDetail 接口失败！", Toast.LENGTH_LONG).show();
            }
        });
    }


    /**
     * 校验必要的文件是否存在
     *
     * @param path         要查询模板的路径
     * @param entry        要查询模板的实体
     * @param isCheackTemp 是否校验的是模板文件, 用于初始化orgH5dataFileName
     * @return 校验是否成功
     */
    private boolean checkNecessaryFile(String path, WorksEntry entry, boolean isCheackTemp) {
        int checkOkFileNum = 0;
        try {
            for (TempFile temp : entry.getFileList()) {
                if (temp.getTag() != null && temp.getTag().equals("datajs")) {
                    File file = new File(path + "/" + temp.getPath().substring(0, temp.getPath().indexOf(".")) + ".js");
                    if (file.exists()) {
                        ++checkOkFileNum;
                        if (isCheackTemp) {
                            orgH5dataFileName = temp.getPath().substring(0, temp.getPath().indexOf(".")) + ".js";
                        }

                        if (checkOkFileNum == 2) {
                            break;
                        }
                    }
                }
                if (temp.getPath().contains("html")) {
                    File file = new File(path + "/" + temp.getPath().substring(0, temp.getPath().indexOf(".")) + ".html");
                    if (file.exists()) {
                        ++checkOkFileNum;
                        if (checkOkFileNum == 2) {
                            break;
                        }
                    }
                }
            }
            if (checkOkFileNum == 2) {
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            return false;
        }
    }

    public void deletUndoWork(String id) {
        circleBar.show();
        Log.e("id", id);
        if (id != null) {

        }
        FileUtil.deleteDirectory(new File(workPath));
        RequestParams params = RequestParam.getRequestParams(TempEditV4Activity.this);
        params.put("worksId", id);
        Log.e("params", params.toString());
        httpClient.post(HttpConfig.CLOSEWORK, params, new AsyncHttpResponseCallBack() {
            @Override
            public void onSuccess(int var1, Header[] var2, byte[] var3) {
                super.onSuccess(var1, var2, var3);
                Log.e("closedowork", new String(var3));
                TempEditV4Activity.this.finish();
            }

            @Override
            public void onFailure(int var1, Header[] var2, byte[] var3, Throwable var4) {
                super.onFailure(var1, var2, var3, var4);
                TempEditV4Activity.this.finish();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finishActivity(null);
        }
        return false;
    }

}
