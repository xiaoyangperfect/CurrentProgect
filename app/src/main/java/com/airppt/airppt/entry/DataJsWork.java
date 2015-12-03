package com.airppt.airppt.entry;

/**
 * Created by user on 2015/7/16.
 */
public class DataJsWork {
    private boolean requirePassword;
    private String workId;
    private boolean canComment;
    private String workName;
    private String workImageURL;
    public DataJsWork(boolean re, String wi, boolean com, String wn, String wiu) {
        this.requirePassword = re;
        this.workId = wi;
        this.canComment = com;
        this.workName = wn;
        this.workImageURL = wiu;
    }

    public boolean isRequirePassword() {
        return requirePassword;
    }

    public void setRequirePassword(boolean requirePassword) {
        this.requirePassword = requirePassword;
    }

    public String getWorkId() {
        return workId;
    }

    public void setWorkId(String workId) {
        this.workId = workId;
    }

    public boolean isCanComment() {
        return canComment;
    }

    public void setCanComment(boolean canComment) {
        this.canComment = canComment;
    }

    public String getWorkName() {
        return workName;
    }

    public void setWorkName(String workName) {
        this.workName = workName;
    }

    public String getWorkImageURL() {
        return workImageURL;
    }

    public void setWorkImageURL(String workImageURL) {
        this.workImageURL = workImageURL;
    }
}
