package com.airppt.airppt.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.airppt.airppt.R;
import com.airppt.airppt.adapter.SelectPhotoShowAdapter;
import com.airppt.airppt.entry.Aibum;
import com.airppt.airppt.entry.PhotoInfo;
import com.airppt.airppt.listener.RecyclerViewItemClickListener;

import java.util.ArrayList;

/**
 * Created by user on 2015/9/25.
 */
public class ChosePhotoActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private ArrayList<String> paths;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_path_show_layout);

        getView();
        getData();
    }

    private void getView() {
        TextView title = (TextView) findViewById(R.id.photo_path_title);
        title.setText(getString(R.string.chose_photo));
        recyclerView = (RecyclerView) findViewById(R.id.photo_path_show_recyclerview);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        button = (Button) findViewById(R.id.photo_path_finish);
    }

    private void getData() {
        paths = new ArrayList<>();
        Aibum aibum = (Aibum) getIntent().getSerializableExtra("ainum");
        for (PhotoInfo info:aibum.getPhotoList()) {
            paths.add(info.getPath());
        }
        SelectPhotoShowAdapter selectPhotoShowAdapter = new SelectPhotoShowAdapter(paths);
        recyclerView.setAdapter(selectPhotoShowAdapter);

        selectPhotoShowAdapter.setOnItemClickListener(new RecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent();
                intent.putExtra("imagepath", paths.get(position));
                setResult(RESULT_OK, intent);
                ChosePhotoActivity.this.finish();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChosePhotoActivity.this.finish();
            }
        });
    }
}
