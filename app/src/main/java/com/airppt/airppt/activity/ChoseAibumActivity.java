package com.airppt.airppt.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.airppt.airppt.R;
import com.airppt.airppt.adapter.PhotoPathShowAdapter;
import com.airppt.airppt.entry.Aibum;
import com.airppt.airppt.listener.RecyclerViewItemClickListener;
import com.airppt.airppt.util.ImageOptUtil;

import java.util.ArrayList;

/**
 * Created by user on 2015/9/25.
 */
public class ChoseAibumActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private ArrayList<Aibum> aibums;
    private PhotoPathShowAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_path_show_layout);

        Button button = (Button) findViewById(R.id.photo_path_finish);
        recyclerView = (RecyclerView) findViewById(R.id.photo_path_show_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        aibums = ImageOptUtil.getAibums(this);
        adapter = new PhotoPathShowAdapter(aibums);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new RecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("ainum", aibums.get(position));
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                ChoseAibumActivity.this.finish();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChoseAibumActivity.this.finish();
            }
        });
    }

}
