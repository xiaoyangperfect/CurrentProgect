package com.airppt.airppt.model;

/**
 * Created by user on 2015/7/27.
 */
public class NameParam {
    private String name;
    private boolean isUpload;
    public NameParam(String name, boolean isUpload) {
        this.name = name;
        this.isUpload = isUpload;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getIsUpload() {
        return isUpload;
    }

    public void setIsUpload(boolean isUpload) {
        this.isUpload = isUpload;
    }
}
