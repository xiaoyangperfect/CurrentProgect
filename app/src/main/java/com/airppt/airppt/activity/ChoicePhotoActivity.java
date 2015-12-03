package com.airppt.airppt.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.airppt.airppt.R;
import com.airppt.airppt.adapter.AllPhotoShowAdapter;
import com.airppt.airppt.util.DPIUtil;
import com.airppt.airppt.util.SharedPreferenceUtil;

import java.util.ArrayList;

/**
 * Created by yang on 2015/6/1.
 */
public class ChoicePhotoActivity extends BaseActivity {

    private String path;
    private GridView allImageView;
    private AllPhotoShowAdapter adapter;
    private ArrayList<String> shots;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choicephoto);
        initView();
        initDate();
        getView();
    }

    private void initView() {
        allImageView = (GridView) findViewById(R.id.all_photo_show_gridView);
    }

    private void initDate() {
        DPIUtil.getScreenMetrics(this);
        shots = getIntent().getStringArrayListExtra("shots");
        path = getIntent().getStringExtra("path");
        Log.e("shots", shots.toString());
        if (shots == null || path == null)
            this.finish();
        adapter = new AllPhotoShowAdapter(this, path, shots, DPIUtil.getWidthByHight());
    }

    private void getView() {
        allImageView.setAdapter(adapter);
        allImageView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("shotPath", shots.get(position));
                setResult(RESULT_OK, intent);
                ChoicePhotoActivity.this.finish();
            }
        });
    }

    private void intentToCrop(){
        Intent intent = new Intent(ChoicePhotoActivity.this, PhotoCropActivity.class);
        intent.putExtra("path", path);
        startActivity(intent);
        ChoicePhotoActivity.this.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            SharedPreferenceUtil.getSharedEditor(ChoicePhotoActivity.this).putString(SharedPreferenceUtil.CHANGE_IMAGE_NAME, "").commit();
            ChoicePhotoActivity.this.finish();
        }
        return false;
    }

    public void choicePhotoFinished(View view) {
        SharedPreferenceUtil.getSharedEditor(ChoicePhotoActivity.this).putString(SharedPreferenceUtil.CHANGE_IMAGE_NAME, "").commit();
        ChoicePhotoActivity.this.finish();
    }
}
