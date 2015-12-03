package com.airppt.airppt.entry;

import java.io.Serializable;

/**
 * Created by yang on 2015/6/2.
 */
public class TempFile implements Serializable {
    private String Tag;
    private String Path;
    private String PageID;

    public String getPageID() {
        return PageID;
    }

    public void setPageID(String pageID) {
        PageID = pageID;
    }

    public String getTag() {
        return Tag;
    }

    public void setTag(String tag) {
        Tag = tag;
    }

    public String getPath() {
        return Path;
    }

    public void setPath(String path) {
        Path = path;
    }
}
