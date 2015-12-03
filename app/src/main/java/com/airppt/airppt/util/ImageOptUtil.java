package com.airppt.airppt.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;

import com.airppt.airppt.R;
import com.airppt.airppt.entry.Aibum;
import com.airppt.airppt.entry.PhotoInfo;
import com.airppt.airppt.view.CircleBitmapDisplayer;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by user on 2015/4/7.
 */
public class ImageOptUtil {

    private static final String[] STORE_IMAGES = {
            MediaStore.Images.Media.DISPLAY_NAME, // name
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.LONGITUDE, // 经度
            MediaStore.Images.Media._ID, // id
            MediaStore.Images.Media.BUCKET_ID, // dir id 目录
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME // dir name 目录名字

    };

    private static int[] colorBg = new int[]{R.drawable.grey_bg,
            R.drawable.light_blue,
            R.drawable.light_oringe_bg,
            R.drawable.light_pink_bg,
            R.drawable.purple_bg};

    /**
     * 图片加载设置
     */
    public static final DisplayImageOptions image_display_options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.mipmap.error_img)
            .showImageForEmptyUri(R.mipmap.error_img)
            .showImageOnFail(R.mipmap.error_img)
//            .displayer(new RoundedBitmapDisplayer(20))
            .cacheInMemory(true)
            .cacheOnDisc(true)
            .build();

    public static DisplayImageOptions getImage_display_options(int round) {
        int i = new Random().nextInt(5);
        DisplayImageOptions image_display_options = new DisplayImageOptions.Builder()
                .showImageOnLoading(colorBg[i])
                .showImageForEmptyUri(R.mipmap.error_img)
                .showImageOnFail(R.mipmap.error_img)
                .displayer(new RoundedBitmapDisplayer(round))
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .build();
        return image_display_options;
    }

    public static DisplayImageOptions getImage_display_options(int loading, int error, int fail, int round, boolean isMemory, boolean isCacheOnDisc) {
        DisplayImageOptions image_display_options = new DisplayImageOptions.Builder()
                .showImageOnLoading(loading)
                .showImageForEmptyUri(error)
                .showImageOnFail(fail)
                .displayer(new RoundedBitmapDisplayer(round))
                .cacheInMemory(isMemory)
                .cacheOnDisc(isCacheOnDisc)
                .build();
        return image_display_options;
    }

    /**
     * 图片加载设置
     */
    public static final DisplayImageOptions image_display_options_no_cache = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.mipmap.error_img)
            .showImageForEmptyUri(R.mipmap.error_img)
            .showImageOnFail(R.mipmap.error_img)
//            .displayer(new RoundedBitmapDisplayer(20))
            .cacheInMemory(false)
            .cacheOnDisc(false)
            .build();

    public static final DisplayImageOptions image_display_options_circle_no_cache = new DisplayImageOptions.Builder()
            .cacheInMemory(false)
            .cacheOnDisk(false)
            .displayer(new CircleBitmapDisplayer())
            .build();

    /**
     * 获取图片加载器
     * @return
     */
    public static ImageLoader imageLoader = ImageLoader.getInstance();

    /**
     * 初始化图片加载工具类
     * @param context
     */
    public static void initImageLoader(Context context){
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 1)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .discCacheSize(50 * 1024 * 1024)
                .discCacheFileCount(100)
//                .writeDebugLogs() // Remove for release app
                .build();
        ImageLoader.getInstance().init(config);
    }


    /**
     * 获取相册信息
     * @param context
     * @return
     */
    public static ArrayList<Aibum> getAibums(Context context){
        ArrayList<Aibum> aibums = new ArrayList<Aibum>();
        HashMap<String, Aibum> hashMap = new HashMap<>();
        Cursor cursor = MediaStore.Images.Media.query(context.getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, STORE_IMAGES);
        Aibum aibum = null;
        cursor.moveToFirst();
        while (cursor.moveToNext()){
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
            String id = cursor.getString(3);
            String dir_id = cursor.getString(4);
            String dir = cursor.getString(5);
            if (!path.contains("airppt") && !path.contains("cache")) {
                if(!hashMap.containsKey(dir_id)) {
                    aibum = new Aibum();
                    aibum.setName(dir);
                    aibum.setBitmap(Integer.parseInt(id));
                    aibum.setCount("1");
                    aibum.getPhotoList().add(new PhotoInfo(Integer.valueOf(id), path, 0));
                    hashMap.put(dir_id, aibum);
                    aibum.setPath(path);
                } else {
                    aibum = hashMap.get(dir_id);
                    aibum.setCount(String.valueOf(Integer.parseInt(aibum.getCount()) + 1));
                    aibum.getPhotoList().add(new PhotoInfo(Integer.valueOf(id), path, Integer.parseInt(aibum.getCount()) - 1));
                    aibum.setPath(path);
                }
            }
        }
        cursor.close();
        Iterable<String> it = hashMap.keySet();
        for (String key : it) {
            aibums.add(hashMap.get(key));
        }

        return aibums;
    }

    /**
     *
     * @param path 图片路径
     * @return 根据路径获取到的压缩后的bitmap
     */
    public static Bitmap getBitMapByPath(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        int w = options.outWidth;
        int h = options.outHeight;
        float width = DPIUtil.screen_width;
        float high = DPIUtil.screen_height;
        if(w > width || h > high) {
            int dw = (int) (w/width);
            int dh = (int) (h/high);
            options.inSampleSize = dw>dh?dw:dh;
//            options.inSampleSize = 1;
        } else {
            options.inSampleSize = 1;
        }
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }

    /**
     *
     * @param path 图片路径
     * @param num 压缩倍数
     * @return 根据路径获取到的压缩后的bitmap
     */
    public static Bitmap getBitMapByPath(String path, int num) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = num;
        return BitmapFactory.decodeFile(path, options);
    }

}
