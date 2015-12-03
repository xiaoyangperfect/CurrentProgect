package com.airppt.airppt.entry;

/**
 * Created by yang on 2015/4/21.
 */
public class Content {

    private String Name;
    private String Value;
    private int MaxCount;
    private String Font;
    private int FontSize;
    private String FontColor;
    private Constraints Constraints;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }

    public int getMaxCount() {
        return MaxCount;
    }

    public void setMaxCount(int maxCount) {
        MaxCount = maxCount;
    }

    public String getFont() {
        return Font;
    }

    public void setFont(String font) {
        Font = font;
    }

    public int getFontSize() {
        return FontSize;
    }

    public void setFontSize(int fontSize) {
        FontSize = fontSize;
    }

    public String getFontColor() {
        return FontColor;
    }

    public void setFontColor(String fontColor) {
        FontColor = fontColor;
    }

    public com.airppt.airppt.entry.Constraints getConstraints() {
        return Constraints;
    }

    public void setConstraints(com.airppt.airppt.entry.Constraints constraints) {
        Constraints = constraints;
    }
}
