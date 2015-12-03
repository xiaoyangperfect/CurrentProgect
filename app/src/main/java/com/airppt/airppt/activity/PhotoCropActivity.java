package com.airppt.airppt.activity;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.airppt.airppt.R;
import com.airppt.airppt.adapter.PhotoFilterAdapter;
import com.airppt.airppt.entry.PhotoFilter;
import com.airppt.airppt.listener.RecyclerViewItemClickListener;
import com.airppt.airppt.util.DPIUtil;
import com.airppt.airppt.util.ImageOptUtil;
import com.airppt.airppt.util.ImageUtil;
import com.airppt.airppt.util.SharedPreferenceUtil;
import com.airppt.airppt.view.CropImageView;

import java.util.ArrayList;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageColorInvertFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageContrastFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageEmbossFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGammaFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGrayscaleFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageHueFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageMonochromeFilter;
import jp.co.cyberagent.android.gpuimage.GPUImagePosterizeFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageRGBFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSaturationFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSepiaFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSharpenFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSobelEdgeDetection;
import jp.co.cyberagent.android.gpuimage.GPUImageVignetteFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageWhiteBalanceFilter;

/**
 * Created by user on 2015/4/27.
 */
public class PhotoCropActivity extends BaseActivity {

    private CropImageView cropImageView;
    private Bitmap bitmap;
    private int width;
    private int hight;
    private String name;
    private String orgPath;
    private GPUImage mGPUImage;
    private RecyclerView recyclerView;
    private ArrayList<PhotoFilter> filters;
    private Bitmap filterBitmap;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_crop);
        Log.e("CropImage", "PhotoCropActivity");
        DPIUtil.getScreenMetrics(PhotoCropActivity.this);
        path = getIntent().getStringExtra("path");
        String w = getIntent().getStringExtra("width");
        String h = getIntent().getStringExtra("hight");
        orgPath = getIntent().getStringExtra("orgPath");
        name = getIntent().getStringExtra("name");

        mGPUImage = new GPUImage(this);
        filters = new ArrayList<>();
        initPhotoFilter();

        float wh = 0;
        if (w.contains("%")) {
            w = w.substring(0, w.indexOf("%"));
            w = Float.parseFloat(w) > 100 ? 100 + "" : w;
            width = (int) (DPIUtil.screen_width * Float.parseFloat(w) / 100);
        } else if (Float.parseFloat(w) <= 1) {
            width = (int) (DPIUtil.screen_width * Float.parseFloat(w));
        } else {
            width = (int) Float.parseFloat(w);
            width = DPIUtil.dip2px(this, width);
        }

        if (h.contains("%")) {
            h = h.substring(0, h.indexOf("%"));
            h = Float.parseFloat(h) > 100 ? 100 + "" : h;
            hight = (int) (DPIUtil.screen_height * Float.parseFloat(h) / 100);
        } else if (Float.parseFloat(h) <= 1) {
            hight = (int) (DPIUtil.screen_height * Float.parseFloat(h));
        } else {
            hight = (int) Float.parseFloat(h);
            hight = DPIUtil.dip2px(this, hight);
        }

        wh = (float) width / (float) hight;
        if (wh > DPIUtil.getWidthByHight()) {
            width = (int) (DPIUtil.screen_width * 0.7);
            hight = (int) (DPIUtil.screen_width * 0.7 / wh);
        } else {
            hight = (int) (DPIUtil.screen_height * 0.7);
            width = (int) (DPIUtil.screen_height * 0.7 * wh);
        }

        width = DPIUtil.px2dip(this, width);
        hight = DPIUtil.px2dip(this, hight);

        if (name.contains("shareimg_")) {
            width = Integer.parseInt(getIntent().getStringExtra("width"));
            hight = Integer.parseInt(getIntent().getStringExtra("hight"));
        }

        cropImageView = (CropImageView) findViewById(R.id.crop_image_view);
        recyclerView = (RecyclerView) findViewById(R.id.photocrop_recyclerview);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(manager);
        bitmap = ImageOptUtil.getBitMapByPath(path);
        Log.e("CropImage", "PhotoCropActivity set img");
        setCropImageView(bitmap, true);

        PhotoFilterAdapter adapter = new PhotoFilterAdapter(filters, ImageOptUtil.getBitMapByPath(path, 15), mGPUImage);
        recyclerView.setAdapter(adapter);
        adapter.setOnClickListener(new RecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position == 0) {
                    if (filterBitmap != null) {
                        filterBitmap.recycle();
                    }
//                    bitmap = ImageOptUtil.getBitMapByPath(path);
                    setCropImageView(bitmap, false);
                } else {
                    if (filterBitmap != null) {
                        filterBitmap.recycle();
                    }
                    mGPUImage.setFilter(filters.get(position).getFilter());
                    filterBitmap = mGPUImage.getBitmapWithFilterApplied(bitmap);
                    setCropImageView(filterBitmap, false);
                }

            }
        });
        Log.e("CropImage", "PhotoCropActivity is OK");
    }

    /**
     * @param bitmap  要设置的图片
     * @param isFirst 是否需要重绘图片大小
     */
    private void setCropImageView(Bitmap bitmap, boolean isFirst) {

        Drawable drawable = new BitmapDrawable(bitmap);
        cropImageView.setDrawable(drawable, width, hight, isFirst);
        drawable = null;
    }

    public void saveImage(View view) {
//        if (FileUtil.saveImage(cropImageView.getCropImage(),
////        if (FileUtil.saveImage(cropImageView.getCurrentImage(),
//                orgPath + "/" + name)) {
        if (cropImageView.getCropImage(orgPath + "/" + name)) {
            setResult(RESULT_OK);
            finish();
        }

    }

    private void initPhotoFilter() {
        filters.add(null);
        filters.add(new PhotoFilter(new GPUImageContrastFilter(2.0f), "对比度"));
        filters.add(new PhotoFilter(new GPUImageGammaFilter(2.0f), "伽马值"));
        filters.add(new PhotoFilter(new GPUImageColorInvertFilter(), "负片"));
        filters.add(new PhotoFilter(new GPUImageHueFilter(90.0f), "色调"));
        filters.add(new PhotoFilter(new GPUImageGrayscaleFilter(), "灰阶"));
        filters.add(new PhotoFilter(new GPUImageSepiaFilter(), "棕褐色"));
        GPUImageSharpenFilter sharpness = new GPUImageSharpenFilter();
        sharpness.setSharpness(2.0f);
        filters.add(new PhotoFilter(sharpness, "加重"));
        filters.add(new PhotoFilter(new GPUImageSobelEdgeDetection(), "Sobel边缘检测"));
        filters.add(new PhotoFilter(new GPUImageEmbossFilter(), "浮雕"));
        filters.add(new PhotoFilter(new GPUImagePosterizeFilter(), "分色"));
        filters.add(new PhotoFilter(new GPUImageSaturationFilter(1.0f), "饱和度"));
//        filters.add(new PhotoFilter(new GPUImageExposureFilter(0.0f), "EXPOSURE"));
//        filters.add(new PhotoFilter(new GPUImageHighlightShadowFilter(0.0f, 1.0f), "HIGHLIGHT_SHADOW"));
        filters.add(new PhotoFilter(new GPUImageMonochromeFilter(1.0f, new float[]{0.6f, 0.45f, 0.3f, 1.0f}), "黑白色"));
//        filters.add(new PhotoFilter(new GPUImageOpacityFilter(1.0f), "OPACITY"));
        filters.add(new PhotoFilter(new GPUImageRGBFilter(1.0f, 1.0f, 1.0f), "RGB"));
        filters.add(new PhotoFilter(new GPUImageWhiteBalanceFilter(5000.0f, 0.0f), "白平衡"));
        PointF centerPoint = new PointF();
        centerPoint.x = 0.5f;
        centerPoint.y = 0.5f;
        filters.add(new PhotoFilter(new GPUImageVignetteFilter(centerPoint, new float[]{0.0f, 0.0f, 0.0f}, 0.3f, 0.75f), "LOMO"));
    }

    /**
     * 翻转图片
     *
     * @param view
     */
    public void rotateImage(View view) {

        // 背景图片翻转
        bitmap = ImageUtil.postRotateBitamp(bitmap, -90);
        if (filterBitmap != null) {
            try {
                filterBitmap = ImageUtil.postRotateBitamp(filterBitmap, -90);
                //前面图片翻转 首先获取当前图片
                setCropImageView(filterBitmap, true);
            } catch (Exception e) {
                Log.e("bitmap_error", "find error " + e.getLocalizedMessage());
            }

        } else {
            setCropImageView(bitmap, true);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            SharedPreferenceUtil.getSharedEditor(PhotoCropActivity.this).putString(SharedPreferenceUtil.CHANGE_IMAGE_NAME, "").commit();
            PhotoCropActivity.this.finish();
        }
        return false;
    }

    public void cropFinished(View view) {
        PhotoCropActivity.this.finish();
    }

    @Override
    protected void onDestroy() {
        if (bitmap != null) {
            bitmap.recycle();
        }
        if (filterBitmap != null) {
            filterBitmap.recycle();
        }
        cropImageView = null;
        setContentView(R.layout.null_view);
        super.onDestroy();
    }
}
