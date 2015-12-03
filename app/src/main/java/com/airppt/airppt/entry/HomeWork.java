package com.airppt.airppt.entry;

/**
 * Created by yang on 2015/7/3.
 */
public class HomeWork {
    private HotWorkData HotWorkTagList;
    private TempDataEntry Works;
    private String Host;
    private String SearchWord;
    private String Flag;

    public String getSearchWord() {
        return SearchWord;
    }

    public void setSearchWord(String searchWord) {
        SearchWord = searchWord;
    }

    public String getFlag() {
        return Flag;
    }

    public void setFlag(String flag) {
        Flag = flag;
    }

    public String getHost() {
        return Host;
    }

    public void setHost(String host) {
        Host = host;
    }

    public HotWorkData getHotWorkTagList() {
        return HotWorkTagList;
    }

    public void setHotWorkTagList(HotWorkData hotWorkTagList) {
        HotWorkTagList = hotWorkTagList;
    }

    public TempDataEntry getWorks() {
        return Works;
    }

    public void setWorks(TempDataEntry works) {
        Works = works;
    }
}
