package com.airppt.airppt.entry;

/**
 * Created by user on 2015/7/16.
 */
public class DataJsAuthor {
    private String QRImageURL;
    private String name;
    private String desc;
    private String ImageURL;

    public DataJsAuthor(String qr, String na, String de, String im) {
        this.QRImageURL = qr;
        this.name = na;
        this.desc = de;
        this.ImageURL = im;
    }

    public String getQRImageURL() {
        return QRImageURL;
    }

    public void setQRImageURL(String QRImageURL) {
        this.QRImageURL = QRImageURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImageURL() {
        return ImageURL;
    }

    public void setImageURL(String imageURL) {
        ImageURL = imageURL;
    }
}
