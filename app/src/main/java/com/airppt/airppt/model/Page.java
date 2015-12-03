package com.airppt.airppt.model;

import java.util.HashMap;

/**
 * Created by user on 2015/7/10.
 */
public class Page {
    private HashMap<String, String> page;
    private String shortCut;
    //是否有改动
    private boolean isModify;

    public boolean isModify() {
        return isModify;
    }

    public void setIsModify(boolean isModify) {
        this.isModify = isModify;
    }

    public Page(HashMap<String, String> map) {
        this.page = map;
    }

    public HashMap<String, String> getPage() {
        return page;
    }

    public void setPage(HashMap<String, String> page) {
        this.page = page;
    }

    public String getShortCut() {
        return shortCut;
    }

    public void setShortCut(String shortCut) {
        this.shortCut = shortCut;
    }

    public void updateDataJsContent(String key, String value) {
        page.put(key, value);
    }


    public String setShortCut() {
        return this.shortCut = page.get("id") + ".jpg";
    }
}
