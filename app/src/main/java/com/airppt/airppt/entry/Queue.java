package com.airppt.airppt.entry;

/**
 * Created by yang on 2015/6/16.
 */
public class Queue {
    private String WorksId;
    private String LastRank;
    private String LastWorkId;
    private String Text;
    private String UserId;
    private String TemplateId;
    private String TemplateTag;
    private int HotPoint;
    private int TemplateFlag;
    private String Status;
    private int HotTagId;
    private String LastDate;
    private String DateNow;

    public int getHotTagId() {
        return HotTagId;
    }

    public void setHotTagId(int hotTagId) {
        HotTagId = hotTagId;
    }

    public String getLastDate() {
        return LastDate;
    }

    public void setLastDate(String lastDate) {
        LastDate = lastDate;
    }

    public String getDateNow() {
        return DateNow;
    }

    public void setDateNow(String dateNow) {
        DateNow = dateNow;
    }

    public String getLastWorkId() {
        return LastWorkId;
    }

    public void setLastWorkId(String lastWorkId) {
        LastWorkId = lastWorkId;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getWorksId() {
        return WorksId;
    }

    public void setWorksId(String worksId) {
        WorksId = worksId;
    }

    public String getLastRank() {
        return LastRank;
    }

    public void setLastRank(String lastRank) {
        LastRank = lastRank;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }


    public String getTemplateId() {
        return TemplateId;
    }

    public void setTemplateId(String templateId) {
        TemplateId = templateId;
    }

    public String getTemplateTag() {
        return TemplateTag;
    }

    public void setTemplateTag(String templateTag) {
        TemplateTag = templateTag;
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


}
