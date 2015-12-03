package com.airppt.airppt.entry;

import java.util.ArrayList;

/**
 * Created by yang on 2015/4/21.
 */
public class Page {
    private ArrayList<Element> Elements;
    private int Id;
    private String JsonContent;

    public ArrayList<Element> getElements() {
        return Elements;
    }

    public void setElements(ArrayList<Element> elements) {
        Elements = elements;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getJsonContent() {
        return JsonContent;
    }

    public void setJsonContent(String jsonContent) {
        JsonContent = jsonContent;
    }

}
