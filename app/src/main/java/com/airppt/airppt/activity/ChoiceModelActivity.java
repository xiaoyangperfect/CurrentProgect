package com.airppt.airppt.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.airppt.airppt.R;
import com.airppt.airppt.adapter.ChoicedModleAdapter;
import com.airppt.airppt.entry.DefaultConfig;
import com.airppt.airppt.entry.Page;
import com.airppt.airppt.listener.RecyclerViewItemClickListener;
import com.airppt.airppt.util.FileUtil;
import com.airppt.airppt.util.HttpUtil;
import com.airppt.airppt.util.ImageOptUtil;
import com.airppt.airppt.util.ViewFactory;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by user on 2015/4/9.
 */
public class ChoiceModelActivity extends BaseActivity {

    public final int CHANGE_PHOTO_INDEX = 1;

    private RecyclerView imageShowView;
    private ArrayList<String> paths;
    private ArrayList<View> views;
    private ChoicedModleAdapter adapter;
    private Gson gson;
    private ProgressBar progressBar;
    private View indexView;
    private String SD_PATH;
    private int index;
    private ViewFactory factory;
    private DefaultConfig config;
    private String jsFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choice_modle_layout);
        initView();
        initDate();
    }

    private void initView(){
        imageShowView = (RecyclerView) findViewById(R.id.choice_modle_recyclerview);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayout.HORIZONTAL);
        imageShowView.setLayoutManager(manager);
        progressBar = (ProgressBar) findViewById(R.id.choice_modle_progressbar);
    }

    private void initDate(){
        paths = getIntent().getStringArrayListExtra("paths");
        factory = new ViewFactory(ChoiceModelActivity.this, ChoiceModelActivity.this);
        gson = new Gson();
//        InputStream inputStream = getResources().openRawResource(R.raw.t1_config);
//        config = TemplateParser.parser(gson, inputStream);
        applyTemp(config);
        SD_PATH = FileUtil.createFileDir(FileUtil.SD_PATH);
    }

    private void getView(){
        imageShowView.setAdapter(adapter);
        adapter.setOnItemClickListener(new RecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                indexView = view;
                index = position;
                changePhoto(paths.get(position));
            }
        });
    }

    private void applyTemp(DefaultConfig config) {
        AsyncTask<DefaultConfig, Integer, ArrayList<View>> task = new AsyncTask<DefaultConfig, Integer, ArrayList<View>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected ArrayList<View> doInBackground(DefaultConfig... params) {
                views = factory.getViewsWithTemp(gson, params[0], paths);
                return views;
            }

            @Override
            protected void onPostExecute(ArrayList<View> views) {
                super.onPostExecute(views);
                adapter = new ChoicedModleAdapter(views);
                getView();
                progressBar.setVisibility(View.GONE);
            }
        };
        task.execute(config);
    }

    public void previewEffect(View view) {
        FileUtil.saveJson(factory.getJsonContent());
        HttpUtil.upLoadFilesToService(handler, "http://appt.hooyes.com/api/CreatePPT", SD_PATH);
    }

    public void changeModle(View view) {

    }

    public void changePhoto(String path) {
        Intent intent = new Intent(this, PhotoCropActivity.class);
        intent.putExtra("path", path);
        startActivityForResult(intent, CHANGE_PHOTO_INDEX);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CHANGE_PHOTO_INDEX && resultCode == RESULT_OK) {
            ImageView view = (ImageView) indexView.findViewById(R.id.base_view_bg);
            String name = data.getStringExtra("name");
            String path = FileUtil.SD_PATH + "/" + name;
            view.setImageBitmap(ImageOptUtil.getBitMapByPath(path));
            paths.remove(index);
            paths.add(index, path);
            Page page = config.getPages().get(index);
            page.setJsonContent(page.getJsonContent().replace(page.getElements().get(0).getImage(), name));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(SD_PATH != null) {
            File file = new File(SD_PATH);
            FileUtil.deleteDirectory(file);
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HttpUtil.HTTP_UP_LOAD_SUCCESS:
                    Log.e("http", msg.obj.toString());
                    break;
                case HttpUtil.HTTP_UP_LOAD_FAIL:
                    Log.e("http", "error");
                    break;
            }
        }
    };
}