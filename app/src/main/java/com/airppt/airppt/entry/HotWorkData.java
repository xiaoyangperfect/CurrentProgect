package com.airppt.airppt.entry;

import java.util.ArrayList;

/**
 * Created by yang on 2015/7/3.
 */
public class HotWorkData {
    private BaseResult BaseResult;
    private ArrayList<HotWorkTag> HotTags;

    public com.airppt.airppt.entry.BaseResult getBaseResult() {
        return BaseResult;
    }

    public void setBaseResult(com.airppt.airppt.entry.BaseResult baseResult) {
        BaseResult = baseResult;
    }

    public ArrayList<HotWorkTag> getHotTags() {
        return HotTags;
    }

    public void setHotTags(ArrayList<HotWorkTag> hotTags) {
        HotTags = hotTags;
    }
}
