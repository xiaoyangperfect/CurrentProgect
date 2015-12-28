package com.airppt.airppt.model;

import com.airppt.airppt.util.MD5Util;
import com.airppt.airppt.util.Util;
import com.google.gson.Gson;
import com.airppt.airppt.entry.ImageViewState;
import com.airppt.airppt.entry.JsPage;
import com.airppt.airppt.entry.NamePoolEntry;
import com.airppt.airppt.entry.WorksEntry;
import com.airppt.airppt.util.FileUtil;
import com.airppt.airppt.util.TempEditUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

/**
 * Created by user on 2015/7/27.
 */
public class WorkV2 {
    private int MAX_PAGE_NUM = 20;

    public NamePoolV2 pool;
    private String workPath;

    //html完整路径
    public String htmlPath;
    //h5datajs路径
    public String dataJsPath;

    //html名称
    public String htmlFileName;
    //js名称
    public String dataJsFileName;


    private String orgPath;
    public ArrayList<HashMap> orgPages;

    public ArrayList<Page> pages;
    public JsPage jsPage;

    public boolean isInitSuccess = true;

    //创建

    /**
     * 创建初始化调用
     * @param path workpath
     * @param orgPath
     * @param orgH5data
     * @param miKey
     * @param worksEntry
     */
    public WorkV2(String path, String orgPath, String orgH5data, Long miKey, WorksEntry worksEntry, Gson gson, String baseUrl) {
        pool = new NamePoolV2(miKey);
        htmlFileName = pool.getUseableNameParam().getName() + ".html";
        dataJsFileName = pool.getUseableNameParam().getName() + ".js";
        if (!init(path, orgPath, orgH5data, baseUrl))
            return;
//        String h5DataJson = TempEditUtil.getJsonFromFile(orgPath + "/" + orgH5data);
        String h5Name = null;
        for (int i = 0; i < worksEntry.getFileList().size(); i++) {
            if (worksEntry.getFileList().get(i).getTag() != null && worksEntry.getFileList().get(i).getTag().endsWith("datajs")) {
                h5Name = worksEntry.getFileList().get(i).getPath();
                File file = new File(workPath + "/" + h5Name);
                file.renameTo(new File(dataJsPath));
            } else if (worksEntry.getFileList().get(i).getPath().endsWith(".html")) {
                String htmlName = worksEntry.getFileList().get(i).getPath();
                File file = new File(workPath + "/" + htmlName);
                file.renameTo(new File(htmlPath));
            }
        }

        String htmlJson = TempEditUtil.getJsonFromFile(workPath + "/" + htmlFileName);
        htmlJson = htmlJson.replaceAll(h5Name, dataJsFileName);
        FileUtil.writeData(htmlPath, htmlJson);
        jsPage = TempEditUtil.getPageInfo(dataJsPath);
        ArrayList<HashMap> hpages = initPageNum(jsPage.pageInfo, baseUrl, true);
        initPage(false, hpages);
        if (pages != null && pages.size() > 0) {
            jsPage.pageInfo = gson.toJson(getPagesInfo());
            String dataJs = jsPage.pre + jsPage.pageInfo + jsPage.next;
            FileUtil.writeData(dataJsPath, dataJs);
        } else {
            isInitSuccess = false;
        }
    }

    private ArrayList<HashMap> initPageNum(String json, String base, boolean isLimit) {
        if (json != null) {
            try {
                HashMap<Integer, String> pageType = new HashMap<>();
                JSONArray jsonArray = new JSONArray(json);
                ArrayList<HashMap> list = new ArrayList<>();
                HashMap<String, String> map;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    Iterator<String> iterator = object.keys();
                    map = new HashMap<>();
                    while (iterator.hasNext()) {
                        String key = iterator.next().toString();
                        String value = object.getString(key);
                        if(key.contains("img")) {
                            value = base + object.getString(key);
                        }
                        map.put(key, value);
                    }
                    if (!pageType.containsValue(map.get("tid"))) {
                        pageType.put( i, map.get("tid"));
                    }
                    list.add(map);
                }

                if (!isLimit || list.size() <= MAX_PAGE_NUM) {
                    return list;
                } else {
                    int typeNum = pageType.size();
                    int currentNum = MAX_PAGE_NUM - typeNum;
                    ArrayList<HashMap> mList = new ArrayList<>();
                    for (int i = 0; i < MAX_PAGE_NUM; i++) {
                        if (pageType.containsKey(i)) {
                            mList.add(list.get(i));
                        } else {
                            if (currentNum > 0) {
                                mList.add(list.get(i));
                                currentNum--;
                            }
                        }
                    }
                    return mList;
                }

            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    //创建模式下,有本地文件，无namepool本地文件
    public WorkV2(String path, String orgPath, String orgH5data, Long miKey, boolean finished, String baseUrl) {
        pool = new NamePoolV2(miKey, path, finished, null);
        htmlFileName = pool.htmlName + ".html";
        dataJsFileName = pool.jsName + ".js";
        init(path, orgPath, orgH5data, baseUrl);
        jsPage = TempEditUtil.getPageInfo(dataJsPath);
        ArrayList<HashMap> hpages = TempEditUtil.getDataFromH5data(jsPage);
        initPage(false, hpages);
    }

    //有本地文件，有namepool本地文件
    public WorkV2(Long miKey, String path, String orgPath, String orgH5data, Gson gson, String baseUrl) {
        pool = new NamePoolV2(miKey, path, gson);
        htmlFileName = pool.htmlName + ".html";
        dataJsFileName = pool.jsName + ".js";
        init(path, orgPath, orgH5data, baseUrl);
        jsPage = TempEditUtil.getPageInfo(dataJsPath);
        ArrayList<HashMap> hpages = TempEditUtil.getDataFromH5data(jsPage);
        initPage(false, hpages);
    }

    /**
     * 编辑模式且无本地文件
     * @param miKey
     * @param path
     * @param orgH5data
     * @param gson
     * @param baseUrl
     */
    public WorkV2(Long miKey, String path, String orgH5data, Gson gson, String baseUrl, String orgPath) {
        htmlFileName = MD5Util.MD5(String.valueOf(miKey + 1)) + ".html";
        dataJsFileName = MD5Util.MD5(String.valueOf(miKey + 2)) + ".js";
        init(path, orgPath, orgH5data, baseUrl);
        jsPage = TempEditUtil.getPageInfo(dataJsPath);
        ArrayList<HashMap> hpages = initPageNum(jsPage.pageInfo, baseUrl, false);
        initPage(false, hpages);
        if (pages != null) {
            pool = new NamePoolV2(miKey, path, true, pages);
            pool.getUseableNameParam().getName();
            pool.getUseableNameParam().getName();
            jsPage.pageInfo = gson.toJson(getPagesInfo());
            String dataJs = jsPage.pre + jsPage.pageInfo + jsPage.next;
            FileUtil.writeData(dataJsPath, dataJs);
        } else {
            isInitSuccess = false;
        }
    }


    private boolean init(String path, String orgPath, String orgH5data, String baseUrl) {
        this.workPath = path;
        this.orgPath = orgPath;
        htmlPath = path + "/" + htmlFileName;
        dataJsPath = path + "/" + dataJsFileName;
//        jsPage = TempEditUtil.getPageInfo(dataJsPath);
        pages = new ArrayList<>();
//        orgPages = TempEditUtil.getDataFromH5data(orgPath + "/" + orgH5data);
        JsPage jsPage1 = TempEditUtil.getPageInfo(orgPath + "/" + orgH5data);
        if (jsPage1 != null && Util.isStringNotEmpty(jsPage1.pageInfo)) {
            orgPages = initPageNum(jsPage1.pageInfo, baseUrl, true);
            return true;
        } else {
            isInitSuccess = false;
            return false;
        }
    }

    private void initPage(boolean isModify, ArrayList<HashMap> list) {
        try {
//            ArrayList<HashMap> list = TempEditUtil.getDataFromH5data(dataJsPath);
            if (list != null || list.size() != 0) {
                Page page;
                for (HashMap map : list) {
                    page = new Page(map);
                    page.setShortCut();
                    page.setIsModify(isModify);
                    pages.add(page);
                }
            } else {
                isInitSuccess = false;
            }
        } catch (Exception e) {
            isInitSuccess = false;
        }
    }

    /**
     * 获取页面信息列表
     * @return map中存储页面信息
     */
    public ArrayList<HashMap<String, String>> getPagesInfo() {
        ArrayList<HashMap<String, String>> maps = new ArrayList<>();
        for (Page page : pages) {
            maps.add(page.getPage());
        }
        return maps;
    }

    /**
     * 获取截图列表
     * @param index
     * @return
     */
    public ArrayList<ImageViewState> getShortCuts(int index) {
        ArrayList<ImageViewState> imageViewStates = new ArrayList<>();
        ImageViewState state;
        for (int i = 0; i < pages.size(); i++) {
            state = new ImageViewState();
//            state.setPath("file://" + workPath + "/" + pages.get(i).getShortCut());
            state.setPath(workPath + "/" + pages.get(i).getShortCut());
            if (i == index) {
                state.setState(true);
            } else {
                state.setState(false);
            }
            imageViewStates.add(state);
        }
        return imageViewStates;
    }

    /**
     * 获取所有截图map
     * @return
     */
    public HashMap<String, String> getShortCutMap() {
        HashMap map = new HashMap();
        for (Page page : pages) {
            map.put(page.getPage().get("id"), page.getShortCut());
        }
        return map;
    }

    public ArrayList<String> getShortCuts() {
        ArrayList<String> list = new ArrayList<>();
        for (Page page : pages) {
            list.add(page.getShortCut());
        }
        return list;
    }

    /**
     * 设置page的截图信息
     * @param index
     */
    public String setShortCut(int index) {
        return pages.get(index).setShortCut();
    }

    /**
     * 获取当前的页面
     * @param index
     * @return
     */
    public Page getCurrentPage(int index) {
        return pages.get(index);
    }

    /**
     * 添加新的页面
     * @return
     */
    public boolean addPage(String pageId, String shortCutImageName, Gson gson) {
        for (HashMap map : orgPages) {
            if (map.containsValue(pageId)) {
                //复制文件
                //修改页面id
//                Set<String> set = map.keySet();
//                for (String hashKey:set) {
//                    if (hashKey.startsWith("img")) {
//                        String name = (String) map.get(hashKey);
//                        String imgName = pool.getUseableNameParam().getName() + name.substring(name.indexOf("."));
//                        FileUtil.copyFile(orgPath + "/" + name, workPath + "/" + imgName);
//                        map.put(hashKey, imgName);
//                    }
//                }
                HashMap map1 = new HashMap();
                map1.putAll(map);
                map1.put("id", UUID.randomUUID().toString());
//                map.put("id", TempEditUtil.randomPageId(pages, pageId));
                FileUtil.copyFile(orgPath + "/" + shortCutImageName, workPath + "/" + map1.get("id") + ".jpg");
                Page page = new Page(map1);
                page.setShortCut();
                pages.add(page);
            }
        }

        return TempEditUtil.reWriteDataToFile(dataJsPath, getPagesInfo(), gson);
    }

    public boolean removePage(Gson gson, int index) {
        if (pages.size() <= 2) {
            return false;
        }

        Page page = pages.get(index);
        HashMap<String, String> map = page.getPage();
        String img;
        for (String key : map.keySet()) {
            if (key.startsWith("img")) {
                img = map.get(key);
                pool.setUsedNameUnuse(img.substring(0, img.indexOf(".")));
                FileUtil.deleteFile(workPath + "/" + img);
            }
        }
        FileUtil.deleteFile(workPath + "/" + page.getShortCut());
//        page.setShortCut("");
        pages.remove(index);

        return TempEditUtil.reWriteDataToFile(dataJsPath, getPagesInfo(), gson);
    }

    public boolean updateH5dataJs(Gson gson, int index,String key, String value) {
        pages.get(index).getPage().put(key, value);
        return TempEditUtil.reWriteDataToFile(dataJsPath, getPagesInfo(), gson);
    }

    public int getPageSize() {
        return pages.size();
    }

    public boolean sortPage(Gson gson, int preIndex, int index) {
        Page page = pages.get(preIndex);
        pages.remove(preIndex);
        pages.add(index, page);
        return TempEditUtil.reWriteDataToFile(dataJsPath, getPagesInfo(), gson);
    }

    /**
     * 设置指定名字对应文件现在的上传状态
     * @param name 文件名
     * @param state
     */
    public void setFileUploadState(String name, boolean state) {
        pool.setNameParamUploadState(name, state);
    }

    public void saveNamePoolFile(Gson gson) {
        NamePoolEntry entry = new NamePoolEntry();
        entry.setUnUsedName(pool.getUnUsedNameParams());
        entry.setUsedName(pool.getUsedNameParams());
        String json = gson.toJson(entry);
        FileUtil.writeData(workPath + "/namepool.txt", json);
    }

    /**
     *
     */
    public void getUseableNameParam() {
        pool.getUsedNameParams();
    }
}
