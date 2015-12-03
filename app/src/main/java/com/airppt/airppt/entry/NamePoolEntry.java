package com.airppt.airppt.entry;

import com.airppt.airppt.model.NameParam;

import java.util.ArrayList;

/**
 * Created by user on 2015/7/27.
 */
public class NamePoolEntry {
    private ArrayList<NameParam> unUsedName;
    private ArrayList<NameParam> usedName;

    public ArrayList<NameParam> getUnUsedName() {
        return unUsedName;
    }

    public void setUnUsedName(ArrayList<NameParam> unUsedName) {
        this.unUsedName = unUsedName;
    }

    public ArrayList<NameParam> getUsedName() {
        return usedName;
    }

    public void setUsedName(ArrayList<NameParam> usedName) {
        this.usedName = usedName;
    }
}
