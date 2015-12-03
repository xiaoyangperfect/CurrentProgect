package com.airppt.airppt.entry;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by user on 2015/4/7.
 */
public class Aibum implements Serializable {
    private String name;   //相册名字
    private String count; //数量
    private int  bitmap;  // 相册第一张图片
    private String path; //相册第一张图片path
    private ArrayList<PhotoInfo> photoList = new ArrayList<PhotoInfo>();

    public Aibum(){
    }

    public Aibum(String name, String count, int bitmap) {
        super();
        this.name = name;
        this.count = count;
        this.bitmap = bitmap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public int getBitmap() {
        return bitmap;
    }

    public void setBitmap(int bitmap) {
        this.bitmap = bitmap;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ArrayList<PhotoInfo> getPhotoList() {
        return photoList;
    }

    public void setPhotoList(ArrayList<PhotoInfo> photoList) {
        this.photoList = photoList;
    }
}
