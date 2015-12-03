package com.airppt.airppt.entry;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by yang on 2015/6/2.
 */
public class WorksEntry implements Serializable {
    private String WorksId;
    private int UserId;
    private String TemplateId;
    private String Title;
    private Author Author;
    private String ShowPath;
    private ArrayList<TempFile> FileList;
    private ArrayList<TempFile> ThumbnailImages;
    private ArrayList<TextContent> TextContent;
    private WorkStyle WorkStyle;
    private String TemplateTag;
    private String MusicId;
    private int HotPoint;
    private int TemplateFlag;
    private String Status;
    private String Memo;
    private String Tstamp;
    private String RootTemplateId;
    private int IsDianZan;
    private int FeedBackCount;
    private String WXShareImg;

    public String getWXShareImg() {
        return WXShareImg;
    }

    public void setWXShareImg(String WXShareImg) {
        this.WXShareImg = WXShareImg;
    }

    public int getFeedBackCount() {
        return FeedBackCount;
    }

    public void setFeedBackCount(int feedBackCount) {
        FeedBackCount = feedBackCount;
    }

    public int getIsDianZan() {
        return IsDianZan;
    }

    public void setIsDianZan(int isDianZan) {
        IsDianZan = isDianZan;
    }

    public String getRootTemplateId() {
        return RootTemplateId;
    }

    public void setRootTemplateId(String rootTemplateId) {
        RootTemplateId = rootTemplateId;
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

    public int getHotPoint() {
        return HotPoint;
    }

    public void setHotPoint(int hotPoint) {
        HotPoint = hotPoint;
    }

    public int getTemplateFlag() {
        return TemplateFlag;
    }

    public void setTemplateFlag(int templateFlag) {
        TemplateFlag = templateFlag;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getMemo() {
        return Memo;
    }

    public void setMemo(String memo) {
        Memo = memo;
    }

    public String getTstamp() {
        return Tstamp;
    }

    public void setTstamp(String tstamp) {
        Tstamp = tstamp;
    }

    public ArrayList<TextContent> getTextContent() {
        return TextContent;
    }

    public void setTextContent(ArrayList<TextContent> textContent) {
        TextContent = textContent;
    }

    public ArrayList<TempFile> getThumbnailImages() {
        return ThumbnailImages;
    }

    public void setThumbnailImages(ArrayList<TempFile> thumbnailImages) {
        ThumbnailImages = thumbnailImages;
    }

    public String getWorksId() {
        return WorksId;
    }

    public void setWorksId(String worksId) {
        WorksId = worksId;
    }

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }

    public String getTemplateId() {
        return TemplateId;
    }

    public void setTemplateId(String templateId) {
        TemplateId = templateId;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public com.airppt.airppt.entry.Author getAuthor() {
        return Author;
    }

    public void setAuthor(com.airppt.airppt.entry.Author author) {
        Author = author;
    }

    public String getShowPath() {
        return ShowPath;
    }

    public void setShowPath(String showPath) {
        ShowPath = showPath;
    }

    public ArrayList<TempFile> getFileList() {
        return FileList;
    }

    public void setFileList(ArrayList<TempFile> fileList) {
        FileList = fileList;
    }
}
