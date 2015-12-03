package com.airppt.airppt.entry;

import java.util.ArrayList;

/**
 * Created by yang on 2015/4/22.
 */
public class DefaultConfig {
    private int Id;
    private ArrayList<Page> Pages;
    private String Title;
    private String Description;
    private String JsonContent;

    public String getJsonContent() {
        return JsonContent;
    }

    public void setJsonContent(String jsonContent) {
        JsonContent = jsonContent;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public ArrayList<Page> getPages() {
        return Pages;
    }

    public void setPages(ArrayList<Page> pages) {
        Pages = pages;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }
}
