package com.airppt.airppt.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;

import com.airppt.airppt.R;
import com.airppt.airppt.util.DPIUtil;
import com.airppt.airppt.util.SharedPreferenceUtil;

/**
 * Created by user on 2015/4/7.
 */
public class PhotoPathShowActivity extends BaseActivity {

//    private RecyclerView recyclerView;
//    private ArrayList<Aibum> aibums;
//    private PhotoPathShowAdapter adapter;

    private static final int NONE = 0;
    private static final int PHOTO_GRAPH = 1;// 拍照
    private static final int PHOTO_ZOOM = 2; // 缩放
    private static final int PHOTO_RESOULT = 3;// 结果
    private static final String IMAGE_UNSPECIFIED = "image/*";
    private String imagePath;
    private float width;
    private float hight;
//    private File file;
//    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_path_show_layout);

        DPIUtil.getScreenMetrics(this);
        width = getIntent().getFloatExtra("width", DPIUtil.screen_width);
        hight = getIntent().getFloatExtra("hight", DPIUtil.screen_height);

        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
        startActivityForResult(intent, PHOTO_ZOOM);

//        recyclerView = (RecyclerView) findViewById(R.id.photo_path_show_recyclerview);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        aibums = ImageOptUtil.getAibums(this);
//        adapter = new PhotoPathShowAdapter(aibums);
//        recyclerView.setAdapter(adapter);
//
//        adapter.setOnItemClickListener(new RecyclerViewItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                Intent intent = new Intent(PhotoPathShowActivity.this, ChoicePhotoActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("ainum", aibums.get(position));
//                intent.putExtras(bundle);
//                startActivity(intent);
//                PhotoPathShowActivity.this.finish();
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == NONE){
            SharedPreferenceUtil.getSharedEditor(PhotoPathShowActivity.this).putString(SharedPreferenceUtil.CHANGE_IMAGE_NAME, "").commit();
            PhotoPathShowActivity.this.finish();
            return;
        }
        if (data == null) {
            SharedPreferenceUtil.getSharedEditor(PhotoPathShowActivity.this).putString(SharedPreferenceUtil.CHANGE_IMAGE_NAME, "").commit();
            PhotoPathShowActivity.this.finish();
            return;
        }
        if (requestCode == PHOTO_ZOOM) {
            Log.e("uri", data.getData() + "");
            ContentResolver resolver = getContentResolver();
            Uri uri = data.getData();
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = managedQuery(uri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            Intent intent = new Intent(PhotoPathShowActivity.this, PhotoCropActivity.class);
            intent.putExtra("orgPath", SharedPreferenceUtil.getSharedPreference(this).getString(SharedPreferenceUtil.WORK_PATH, ""));
            intent.putExtra("path", path);
            intent.putExtra("width", width);
            intent.putExtra("hight", hight);
            startActivity(intent);
            PhotoPathShowActivity.this.finish();
//            startPhotoZoom(data.getData());
        }
//        // 处理结果
//        if (requestCode == PHOTO_RESOULT) {
//            Bundle extras = data.getExtras();
//            if (extras != null) {
//                Bitmap photo = extras.getParcelable("data");
//                saveImage(photo);
//            }
//
//        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");//调用Android系统自带的一个图片剪裁页面,
        intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
        intent.putExtra("crop", "true");//进行修剪
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", hight/width);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", (int) width);
        intent.putExtra("outputY", (int) hight);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PHOTO_RESOULT);
    }

//    public void saveImage(Bitmap bitmap) {
//        imagePath = SharedPreferenceUtil.getSharedPreference(PhotoPathShowActivity.this).getString(SharedPreferenceUtil.CHANGE_IMAGE_NAME, "");
//        if (Util.isStringNotEmpty(imagePath)) {
//            FileUtil.saveImage(bitmap,
//                    SharedPreferenceUtil.getSharedPreference(this).getString(SharedPreferenceUtil.WORK_PATH, "") + "/" + imagePath);
//        } else {
//            SharedPreferenceUtil.getSharedEditor(PhotoPathShowActivity.this).putString(SharedPreferenceUtil.CHANGE_IMAGE_NAME, "").commit();
//        }
//        bitmap.recycle();
//        PhotoPathShowActivity.this.finish();
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            SharedPreferenceUtil.getSharedEditor(PhotoPathShowActivity.this).putString(SharedPreferenceUtil.CHANGE_IMAGE_NAME, "").commit();
            PhotoPathShowActivity.this.finish();
        }
        return false;
    }
}
