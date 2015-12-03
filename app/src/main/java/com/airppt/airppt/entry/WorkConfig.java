package com.airppt.airppt.entry;

import java.util.ArrayList;

/**
 * Created by yang on 2015/6/15.
 */
public class WorkConfig {
    private String WorksId;
    private String UserId;
    private String TemplateId;
    private String RootTemplateId;
    private String Title;
    private ArrayList<TempFile> FileList;
    private ArrayList<TempFile> ThumbnailImages;
    private ArrayList<TextContent> TextContent;
    private WorkStyle WorkStyle;
    private String TemplateTag;
    private String MusicId;
    private int TemplateFlag;
    private String Memo;

    public ArrayList<com.airppt.airppt.entry.TextContent> getTextContent() {
        return TextContent;
    }

    public void setTextContent(ArrayList<com.airppt.airppt.entry.TextContent> textContent) {
        TextContent = textContent;
    }

    public String getWorksId() {
        return WorksId;
    }

    public void setWorksId(String worksId) {
        WorksId = worksId;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getTemplateId() {
        return TemplateId;
    }

    public void setTemplateId(String templateId) {
        TemplateId = templateId;
    }

    public String getRootTemplateId() {
        return RootTemplateId;
    }

    public void setRootTemplateId(String rootTemplateId) {
        RootTemplateId = rootTemplateId;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public ArrayList<TempFile> getFileList() {
        return FileList;
    }

    public void setFileList(ArrayList<TempFile> fileList) {
        FileList = fileList;
    }

    public ArrayList<TempFile> getThumbnailImages() {
        return ThumbnailImages;
    }

    public void setThumbnailImages(ArrayList<TempFile> thumbnailImages) {
        ThumbnailImages = thumbnailImages;
    }

    public com.airppt.airppt.entry.WorkStyle getWorkStyle() {
        return WorkStyle;
    }

    public void setWorkStyle(com.airppt.airppt.entry.WorkStyle workStyle) {
        WorkStyle = workStyle;
    }

    public String getTemplateTag() {
        return TemplateTag;
    }

    public void setTemplateTag(String templateTag) {
        TemplateTag = templateTag;
    }

    public String getMusicId() {
        return MusicId;
    }

    public void setMusicId(String musicId) {
        MusicId = musicId;
    }

    public int getTemplateFlag() {
        return TemplateFlag;
    }

    public void setTemplateFlag(int templateFlag) {
        TemplateFlag = templateFlag;
    }

    public String getMemo() {
        return Memo;
    }

    public void setMemo(String memo) {
        Memo = memo;
    }
}
