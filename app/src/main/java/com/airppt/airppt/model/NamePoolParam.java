package com.airppt.airppt.model;

/**
 * Created by user on 2015/7/11.
 */
public class NamePoolParam {
    private String name;
    private boolean isUsed;
    //3种状态0无上传，1上传中，2已上传
    private int isUpload;

    public NamePoolParam(String name, boolean isUsed, int isUpload) {
        this.name = name;
        this.isUsed = isUsed;
        this.isUpload = isUpload;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setIsUsed(boolean isUsed) {
        this.isUsed = isUsed;
    }

    public int getIsUpload() {
        return isUpload;
    }

    public void setIsUpload(int isUpload) {
        this.isUpload = isUpload;
    }
}
