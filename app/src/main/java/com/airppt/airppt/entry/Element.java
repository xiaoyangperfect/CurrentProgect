package com.airppt.airppt.entry;

/**
 * Created by yang on 2015/4/21.
 */
public class Element {
    private Content Content;
    private int Id;
    private int PageId;
    private String Image;
    private String BackgroundColor;
    private String Type;
    private Constraints Constraints;

    public com.airppt.airppt.entry.Content getContent() {
        return Content;
    }

    public void setContent(com.airppt.airppt.entry.Content content) {
        Content = content;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getPageId() {
        return PageId;
    }

    public void setPageId(int pageId) {
        PageId = pageId;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getBackgroundColor() {
        return BackgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        BackgroundColor = backgroundColor;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public com.airppt.airppt.entry.Constraints getConstraints() {
        return Constraints;
    }

    public void setConstraints(com.airppt.airppt.entry.Constraints constraints) {
        Constraints = constraints;
    }
}
