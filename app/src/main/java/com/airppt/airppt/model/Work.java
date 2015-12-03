package com.airppt.airppt.model;

import com.google.gson.Gson;
import com.airppt.airppt.entry.ImageViewState;
import com.airppt.airppt.entry.WorksEntry;
import com.airppt.airppt.util.FileUtil;
import com.airppt.airppt.util.TempEditUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

/**
 * Created by user on 2015/7/10.
 */
public class Work {

    private WorksEntry worksEntry;
    private String workPath;

    //html路径
    public String htmlPath;
    //h5datajs路径
    public String dataJsPath;


    private String orgPath;
    private ArrayList<HashMap> orgPages;

    private ArrayList<Page> pages;
    public NamePool pool;

    /**
     *
     * @param entry
     * @param path 工作目录
     * @param miKey
     * @param orgH5data 原始dataJs路径
     * @param thumbnailMap 缩略图的map
     */
    public Work(WorksEntry entry, String path, Long miKey, HashMap thumbnailMap, String orgPath, String orgH5data) {
        pool = new NamePool(miKey);
        this.workPath = path;
        this.worksEntry = entry;
        this.orgPath = orgPath;
        htmlPath = path + "/" + pool.getNamePool()[0].getName() + ".html";
        dataJsPath = path + "/" + pool.getNamePool()[1].getName() + ".js";
        pages = new ArrayList<>();

        orgPages = TempEditUtil.getDataFromH5data(orgH5data);
        String h5DataJson = TempEditUtil.getJsonFromFile(orgH5data);

        String h5Name = null;
        for (int i = 0; i < worksEntry.getFileList().size(); i++) {
            if (worksEntry.getFileList().get(i).getTag() != null && worksEntry.getFileList().get(i).getTag().endsWith("datajs")) {
                h5Name = worksEntry.getFileList().get(i).getPath();
                pool.getNamePool()[1].setIsUsed(true);
                pool.getNamePool()[1].setIsUpload(0);
                File file = new File(workPath + "/" + h5Name);
                file.renameTo(new File(dataJsPath));
            } else if (worksEntry.getFileList().get(i).getPath().endsWith(".html")) {
                String htmlName = worksEntry.getFileList().get(i).getPath();
                pool.getNamePool()[0].setIsUsed(true);
                pool.getNamePool()[0].setIsUpload(0);
                File file = new File(workPath + "/" + htmlName);
                file.renameTo(new File(htmlPath));
            } else {
                String oldName = worksEntry.getFileList().get(i).getPath();
                String name = pool.getUseableName(orgPages.size()).getName() + oldName.substring(oldName.indexOf("."));
                h5DataJson = h5DataJson.replaceAll(oldName, name);
                File file = new File(workPath + "/" + oldName);
                file.renameTo(new File(workPath + "/" + name));
            }
        }
        FileUtil.writeData(dataJsPath, h5DataJson);
        String htmlJson = TempEditUtil.getJsonFromFile(workPath + "/" + pool.getNamePool()[0].getName() + ".html");
        htmlJson = htmlJson.replaceAll(h5Name, pool.getNamePool()[1].getName() + ".js");
        FileUtil.writeData(htmlPath, htmlJson);

        ArrayList<HashMap> list = TempEditUtil.getDataFromH5data(dataJsPath);
        Page page;
        for (HashMap map : list) {
            page = new Page(map);
            if (thumbnailMap.containsKey(map.get("id"))) {
                page.setShortCut();
            }
            pages.add(page);
        }
    }

    /**
     * 获取页面信息列表
     * @return map中存储页面信息
     */
    public ArrayList<HashMap> getPagesInfo() {
        ArrayList<HashMap> maps = new ArrayList<>();
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
            state.setPath("file://" + workPath + "/" + pages.get(i).getShortCut());
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
                Set<String> set = map.keySet();
                for (String hashKey:set) {
                    if (hashKey.startsWith("img")) {
                        String name = (String) map.get(hashKey);
                        String imgName = pool.getUseableName(pages.size()).getName() + name.substring(name.indexOf("."));
                        FileUtil.copyFile(orgPath + "/" + name, workPath + "/" + imgName);
                        map.put(hashKey, imgName);
                    }
                }
                map.put("id", UUID.randomUUID().toString());
//                map.put("id", TempEditUtil.randomPageId(pages, pageId));
                FileUtil.copyFile(orgPath + "/" + shortCutImageName, workPath + "/" + map.get("id") + ".jpg");
                Page page = new Page(map);
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
                pool.removeNameUseState(img.substring(0, map.get(key).indexOf(".")));
                FileUtil.deleteFile(workPath + "/" + img);
            }
        }
        FileUtil.deleteFile(workPath + "/" + page.getShortCut());
        page.setShortCut("");
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
     * @param state 3种状态:0无上传，1上传中，2已上传
     */
    public void setFileUploadState(String name, int state) {
        pool.setUpLoadState(name, state);
    }
}
