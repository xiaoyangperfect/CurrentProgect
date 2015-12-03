package com.airppt.airppt.entry;

import java.io.Serializable;

/**
 * Created by user on 2015/4/7.
 */
public class PhotoInfo implements Serializable {
    public PhotoInfo(int id,String path, int index) {
        photoID = id;
        select = false;
        this.path=path;
    }
    public int getPhotoID() {
        return photoID;
    }

    public void setPhotoID(int photoID) {
        this.photoID = photoID;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    private int  photoID;
    private boolean select;
    private String path;
    private int index;
}
