package com.airppt.airppt.model;

import com.airppt.airppt.util.MD5Util;

import java.util.ArrayList;

/**
 * Created by user on 2015/7/11.
 */
public class NamePool {
    private NamePoolParam[] namePool;
    public NamePool(Long mikey) {
        namePool = new NamePoolParam[1024];
        for (int i = 0 ; i < 1024; i++) {
            String name = MD5Util.MD5(String.valueOf(mikey + i + 1));
            namePool[i] = new NamePoolParam(name, false, 0);
        }
    }

    public int findNameIndex(String name) {
        for (int i = 0; i < 1024; i++) {
            if (namePool[i].getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 设置指定名字对应文件现在的上传状态
     * @param name 文件名
     * @param state 3种状态:0无上传，1上传中，2已上传
     */
    public void setUpLoadState(String name, int state) {
        for (NamePoolParam param : namePool) {
            if (param.getName().equals(name)) {
                param.setIsUpload(state);
            }
        }
    }

    /**
     * 获取指定文件的上传状态
     * @param name
     * @param count
     * @return
     */
    public boolean getUpdateState(String name, int count) {
        for (int i = 0; i < count; i++) {
            if (namePool[i].getName().equals(name)) {
                if (namePool[i].getIsUpload() == 2) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 再页面删除时候相关联的被使用的名字的各种状态重新初始化
     * @param name 要初始化的名字
     */
    public void removeNameUseState(String name) {
        for (NamePoolParam param : namePool) {
            if (param.getName().equals(name)) {
                param.setIsUpload(0);
                param.setIsUsed(false);
            }
        }
    }

    /**
     * 在名字池中获取指定数目未使用的名字
     * @param pageSize 作品页面数目，作为限定条件出现，1024-pageSize的数目是名字池中可用数目
     * @param needNum 需要文件名字的数目
     * @return 返回可用nameparam的列表
     */
    public ArrayList<NamePoolParam> getUseableNames(int pageSize, int needNum) {
        ArrayList<NamePoolParam> names = new ArrayList<>();
        for (int i = 2; i < 1024 - pageSize; i++) {
            if (!namePool[i].isUsed()) {
                names.add(namePool[i]);
                namePool[i].setIsUsed(true);
                if (names.size() >= needNum)
                    return names;
            }
        }
        return null;
    }

    /**
     * 在名字池中取一个未使用过的名字
     * @param pageSize 作品页面数目，作为限定条件出现，1024-pageSize的数目是名字池中可用数目
     * @return 可用的名字类
     */
    public NamePoolParam getUseableName(int pageSize) {
        for (int i = 2; i < 1024 - pageSize; i++) {
            if (!namePool[i].isUsed()) {
                namePool[i].setIsUsed(true);
                namePool[i].setIsUpload(0);
                return namePool[i];
            }
        }
        return null;
    }

    public NamePoolParam[] getNamePool() {
        return namePool;
    }
}
