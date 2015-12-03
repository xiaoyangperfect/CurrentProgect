package com.airppt.airppt.entry;

/**
 * Created by user on 2015/7/24.
 */
public class TempIdTextEntry {
    private String work_id;
    private boolean isFinished;
    private String preview_url;
    private String title;
    private String shareImageUrl;
    private String HavePassword;

    public String getHavePassword() {
        return HavePassword;
    }

    public void setHavePassword(String havePassword) {
        HavePassword = havePassword;
    }

    public String getPreview_url() {
        return preview_url;
    }

    public void setPreview_url(String preview_url) {
        this.preview_url = preview_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShareImageUrl() {
        return shareImageUrl;
    }

    public void setShareImageUrl(String shareImageUrl) {
        this.shareImageUrl = shareImageUrl;
    }

    public String getWork_id() {
        return work_id;
    }

    public void setWork_id(String work_id) {
        this.work_id = work_id;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setIsFinished(boolean isFinished) {
        this.isFinished = isFinished;
    }
}
