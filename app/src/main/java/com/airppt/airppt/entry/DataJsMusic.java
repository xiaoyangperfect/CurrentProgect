package com.airppt.airppt.entry;

/**
 * Created by user on 2015/7/16.
 */
public class DataJsMusic {
    private String singer;
    private String coverImageURL;
    private String musicId;
    private String musicName;

    public DataJsMusic(String si, String co, String mi, String mn) {
        this.singer = si;
        this.coverImageURL = co;
        this.musicId = mi;
        this.musicName = mn;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getCoverImageURL() {
        return coverImageURL;
    }

    public void setCoverImageURL(String coverImageURL) {
        this.coverImageURL = coverImageURL;
    }

    public String getMusicId() {
        return musicId;
    }

    public void setMusicId(String musicId) {
        this.musicId = musicId;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }
}
