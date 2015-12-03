package com.airppt.airppt.model;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.airppt.airppt.entry.NamePoolEntry;
import com.airppt.airppt.util.MD5Util;
import com.airppt.airppt.util.TempEditUtil;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by user on 2015/7/27.
 */
public class NamePoolV2 {
    private ArrayList<NameParam> unUsedNameParams;
    private ArrayList<NameParam> usedNameParams;

    public String htmlName;
    public String jsName;

    public NamePoolV2(Long mikey) {
        unUsedNameParams = new ArrayList<>();
        usedNameParams = new ArrayList<>();
        String name;
        htmlName = MD5Util.MD5(String.valueOf(mikey + 1));
        jsName = MD5Util.MD5(String.valueOf(mikey + 2));
        for (int i = 0; i < 1024; i++) {
            name = MD5Util.MD5(String.valueOf(mikey + i + 1));
            unUsedNameParams.add(new NameParam(name, false));
        }
    }

    public NamePoolV2(Long miKey, String path, Gson gson) {
        unUsedNameParams = new ArrayList<>();
        usedNameParams = new ArrayList<>();
        htmlName = MD5Util.MD5(String.valueOf(miKey + 1));
        jsName = MD5Util.MD5(String.valueOf(miKey + 2));
        String json = TempEditUtil.getJsonFromFile(path + "/namepool.txt");
        Type type = new TypeToken<NamePoolEntry>(){}.getType();
        NamePoolEntry entry = gson.fromJson(json, type);
        unUsedNameParams.addAll(entry.getUnUsedName());
        usedNameParams.addAll(entry.getUsedName());
        setNameParamUploadState(htmlName, false);
        setNameParamUploadState(jsName, false);
    }

    /**
     * 创建未完成再次创建模式
     * @param miKey
     * @param path
     * @param isFinished
     */
    public NamePoolV2(Long miKey, String path, boolean isFinished, ArrayList<Page> pages) {
        unUsedNameParams = new ArrayList<>();
        usedNameParams = new ArrayList<>();
        htmlName = MD5Util.MD5(String.valueOf(miKey + 1));
        jsName = MD5Util.MD5(String.valueOf(miKey + 2));
        String name;
        for (int i = 0; i < 1024; i++) {
            name = MD5Util.MD5(String.valueOf(miKey + i + 1));
            unUsedNameParams.add(new NameParam(name, false));
        }
        if (pages == null) {
            File dir = new File(path);
            Log.e("file size", path + ":" + dir.listFiles().length + "");
            a: for (File file:dir.listFiles()) {
                String fileName = file.getName();
                fileName = fileName.substring(0, fileName.indexOf("."));
                for (NameParam param:unUsedNameParams) {
                    if (param.getName().equals(fileName)) {
                        unUsedNameParams.remove(param);
                        param.setIsUpload(isFinished);
                        usedNameParams.add(param);
                        continue a;
                    }
                }
            }
        } else {
            for (Page page:pages) {
                HashMap<String, String> map = page.getPage();
                Set<String> set = map.keySet();
                b: for (String key:set) {
                    String value = map.get(key);
                    if (value.contains(".jpg")) {
                        for (NameParam param:unUsedNameParams) {
                            if (value.contains(param.getName())) {
                                unUsedNameParams.remove(param);
                                param.setIsUpload(isFinished);
                                usedNameParams.add(param);
                                continue b;
                            }
                        }
                    }
                }
            }
        }

        setNameParamUploadState(htmlName, false);
        setNameParamUploadState(jsName, false);
    }

    public NamePoolV2(Long miKey, String path) {
        unUsedNameParams = new ArrayList<>();
        usedNameParams = new ArrayList<>();
        htmlName = MD5Util.MD5(String.valueOf(miKey + 1));
        jsName = MD5Util.MD5(String.valueOf(miKey + 2));
        String name;
        for (int i = 0; i < 1024; i++) {
            name = MD5Util.MD5(String.valueOf(miKey + i + 1));
            unUsedNameParams.add(new NameParam(name, false));
        }
        File dir = new File(path);
        Log.e("file size", path + ":" + dir.listFiles().length + "");
        for (File file:dir.listFiles()) {
            String fileName = file.getName();
            fileName = fileName.substring(0, fileName.indexOf("."));
            for (NameParam param:unUsedNameParams) {
                if (param.getName().equals(fileName)) {
                    unUsedNameParams.remove(param);
                    param.setIsUpload(true);
                    usedNameParams.add(param);
                    break;
                }
            }
        }
        setNameParamUploadState(htmlName, false);
        setNameParamUploadState(jsName, false);
    }

    /**
     * 获取可使用的名字
     * @return
     */
    public NameParam getUseableNameParam() {
//        int i = 0;
        NameParam param = getNameParams();
        param.setIsUpload(false);
        usedNameParams.add(param);
        return param;
    }

    /**
     * 获取一个未使用的nameparam
     * @return
     */
    private NameParam getNameParams() {
        NameParam param = unUsedNameParams.get(0);
        unUsedNameParams.remove(0);
        if (!usedNameParams.contains(param)) {
            return param;
        } else {
            return getNameParams();
        }
    }

    /**
     * 返回图片预备的图片
     * @return
     */
    public NameParam getPrepNameParam() {
        NameParam param = unUsedNameParams.get(0);
        return param;
    }

    /**
     * 页面删除时候相关联的被使用的名字的各种状态重新初始化
     * @param name 要初始化的名字
     */
    public boolean setUsedNameUnuse(String name) {
        for (int i = 0; i < usedNameParams.size(); i++) {
            NameParam param = usedNameParams.get(i);
            if (param.getName().equals(name)) {
                param.setIsUpload(false);
                unUsedNameParams.add(0, param);
                usedNameParams.remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * 设置指定名字对应文件现在的上传状态
     * @param name 文件名
     * @param isUpload
     */
    public boolean setNameParamUploadState(String name, boolean isUpload) {
        for (int i = 0; i < usedNameParams.size(); i++) {
            if (usedNameParams.get(i).getName().equals(name)) {
                usedNameParams.get(i).setIsUpload(isUpload);
                return true;
            }
        }
        return false;
    }

    public boolean getFileUploadState(String  name) {
        for (NameParam param:usedNameParams) {
            if (param.getName().equals(name)) {
                return param.getIsUpload();
            }
        }
        return true;
    }

    public ArrayList<NameParam> getUsedNameParams() {
        return usedNameParams;
    }

    public ArrayList<NameParam> getUnUsedNameParams() {
        return unUsedNameParams;
    }

    public String getThumpName(int index) {
        try {
            return unUsedNameParams.get(unUsedNameParams.size() - 1 - index).getName();
        } catch (Exception e) {
            return null;
        }
    }
}