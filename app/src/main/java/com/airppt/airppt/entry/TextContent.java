package com.airppt.airppt.entry;

import java.io.Serializable;

/**
 * Created by yang on 2015/6/15.
 */
public class TextContent implements Serializable {
    private String Content;

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }
}
