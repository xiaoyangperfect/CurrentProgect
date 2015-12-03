package com.airppt.airppt.db;

/**
 * Created by user on 2015/9/22.
 */
public class ShareDBEntry {
    private String id = "";
    private String title = "";
    private String content = "";
    private String isMusicModle = "";
    private String isMusicInuse = "";
    private String musicTitle = "";
    private String musicAuthor = "";

    public String getMusicTitle() {
        return musicTitle;
    }

    public void setMusicTitle(String musicTitle) {
        this.musicTitle = musicTitle;
    }

    public String getMusicAuthor() {
        return musicAuthor;
    }

    public void setMusicAuthor(String musicAuthor) {
        this.musicAuthor = musicAuthor;
    }

    public String getIsMusicModle() {
        return isMusicModle;
    }

    public void setIsMusicModle(String isMusicModle) {
        this.isMusicModle = isMusicModle;
    }

    public String getIsMusicInuse() {
        return isMusicInuse;
    }

    public void setIsMusicInuse(String isMusicInuse) {
        this.isMusicInuse = isMusicInuse;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
