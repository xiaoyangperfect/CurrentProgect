package com.airppt.airppt.entry;

import java.util.ArrayList;

/**
 * Created by yang on 2015/6/2.
 */
public class TempDataEntry {

//    private String BaseResult;
    private int Total;
    private String LastRank;
    private String LastDate;
    private String LastWorkId;

    public String getLastDate() {
        return LastDate;
    }

    public void setLastDate(String lastDate) {
        LastDate = lastDate;
    }

    public String getLastWorkId() {
        return LastWorkId;
    }

    public void setLastWorkId(String lastWorkId) {
        LastWorkId = lastWorkId;
    }

    private ArrayList<WorksEntry> Works;

//    public String getBaseResult() {
//        return BaseResult;
//    }
//
//    public void setBaseResult(String baseResult) {
//        BaseResult = baseResult;
//    }


    public String getLastRank() {
        return LastRank;
    }

    public void setLastRank(String lastRank) {
        LastRank = lastRank;
    }

    public int getTotal() {
        return Total;
    }

    public void setTotal(int total) {
        Total = total;
    }

    public ArrayList<WorksEntry> getWorks() {
        return Works;
    }

    public void setWorks(ArrayList<WorksEntry> works) {
        Works = works;
    }
}
