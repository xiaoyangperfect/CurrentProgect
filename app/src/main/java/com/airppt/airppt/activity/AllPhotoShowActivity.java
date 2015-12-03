package com.airppt.airppt.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import com.airppt.airppt.R;
import com.airppt.airppt.adapter.AllPhotoShowAdapter;
import com.airppt.airppt.adapter.SelectPhotoShowAdapter;
import com.airppt.airppt.entry.Aibum;
import com.airppt.airppt.listener.RecyclerViewItemClickListener;
import com.airppt.airppt.util.DPIUtil;
import com.airppt.airppt.view.PhotoGridItem;

import java.util.ArrayList;

/**
 * Created by user on 2015/4/8.
 */
public class AllPhotoShowActivity extends BaseActivity {
    private Aibum aibum;
    private GridView allImageView;
    private AllPhotoShowAdapter adapter;
    private RecyclerView selectImgsView;
    private SelectPhotoShowAdapter selectPhotoShowAdapter;
    //选中图片路径集合
    private ArrayList<String> paths;
    private Button startBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_all_show_layout);

        initView();
        initDate();
        getView();

    }

    private void initDate(){
        DPIUtil.getScreenMetrics(this);
        aibum = (Aibum) getIntent().getSerializableExtra("ainum");
//        adapter = new AllPhotoShowAdapter(this, aibum.getPhotoList());
        paths = new ArrayList<>();
        selectPhotoShowAdapter = new SelectPhotoShowAdapter(paths);

    }

    private void initView(){
        allImageView = (GridView) findViewById(R.id.all_photo_show_gridView);
        selectImgsView = (RecyclerView) findViewById(R.id.photo_select_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        selectImgsView.setLayoutManager(linearLayoutManager);
        selectImgsView.setItemAnimator(new DefaultItemAnimator());
        startBtn = (Button) findViewById(R.id.start_create);
    }

    private void getView(){
        allImageView.setAdapter(adapter);
        selectImgsView.setAdapter(selectPhotoShowAdapter);

        allImageView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                photoClick(view, position);
            }
        });

        selectPhotoShowAdapter.setOnItemClickListener(new RecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int index = 0;
                for(int i = 0; i < aibum.getPhotoList().size(); i++) {
                    if(paths.get(position).equals(aibum.getPhotoList().get(i).getPath())){
                        index = i;
                        break;
                    }
                }

                photoClick(allImageView.getChildAt(index), index);
            }
        });

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AllPhotoShowActivity.this, ChoiceModelActivity.class);
                intent.putStringArrayListExtra("paths", paths);
                startActivity(intent);
            }
        });
    }

    /**
     * 图片点击事件处理
     * @param view  被选中的view
     * @param position  被选中的照片在整个相册的照片列表的位置
     */
    private void photoClick(View view, int position){
        if(aibum.getPhotoList().get(position).isSelect()){
            photoRemove(view, position);
        } else {
            photoSelect(view, position);
        }
    }

    /**
     * 选中图片操作
     * @param view 被选中的view
     * @param position 被选中的照片在整个相册的照片列表的位置
     */
    private void photoSelect(View view, int position){
        try {
            paths.add(aibum.getPhotoList().get(position).getPath());
            PhotoGridItem item = (PhotoGridItem) view;
            item.setChecked(true);
            selectPhotoShowAdapter.notifyItemInserted((paths.size() - 1) < 0? 0:(paths.size()-1));
            aibum.getPhotoList().get(position).setSelect(true);
        }catch (Exception ex){
            AllPhotoShowActivity.this.finish();
        }
    }

    /**
     * 图片选中状态取消操作
     * @param view  被取消选中状态的view
     * @param position  被取消选中的照片在整个相册的照片列表的位置
     */
    private void photoRemove(View view, int position) {
        try {
            int index = paths.indexOf(aibum.getPhotoList().get(position).getPath());
            paths.remove(aibum.getPhotoList().get(position).getPath());
            PhotoGridItem item = (PhotoGridItem) view;
            item.setChecked(false);
            selectPhotoShowAdapter.notifyItemRemoved(index);
            aibum.getPhotoList().get(position).setSelect(false);
        } catch (Exception ex) {
            AllPhotoShowActivity.this.finish();
        }
    }
}
