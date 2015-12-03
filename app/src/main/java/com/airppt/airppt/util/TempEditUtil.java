package com.airppt.airppt.util;

import com.google.gson.Gson;
import com.airppt.airppt.entry.JsPage;
import com.airppt.airppt.model.Page;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

/**
 * Created by yang on 2015/6/6.
 * 对于模板操作，暂时定为，先下载模板到FileUtil.TEMP_MODLE_PATH这个路径下
 * 然后复制模板到FileUtil.OPT_MODEL_PATH下进行操作
 */
public class TempEditUtil {

    /***************************** 实现机制 ************************************/

    /**
     * 从H5的data文件中读取string
     * @param path h5data.js的数据列表路径
     * @return 读取到的string
     */
    public static String getJsonFromFile(String path) {

        File file = new File(path);
        if (file.exists()) {
            try {
                InputStream inputStream = new FileInputStream(file);
                int length = inputStream.available();
                byte [] buffer = new byte[length];
                inputStream.read(buffer);
                String json = EncodingUtils.getString(buffer, "UTF-8");
                inputStream.close();
                return json;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    /**
     * 从H5data中读取字符串
     * @param path h5data.js的数据列表路径
     * @return 页面列表
     */
    public static ArrayList<HashMap> getDataFromH5data(String path) {
//        String json = getJsonFromFile(path);
        JsPage jsPage = getPageInfo(path);

        if (jsPage != null) {
//            int firstIndex = json.indexOf("[");
//            int lastIndex = json.indexOf("]");
//            json = json.substring(firstIndex, lastIndex + 1);
            String json = jsPage.pageInfo;
            try {
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
                        map.put(key, value);
                    }
                    list.add(map);
                }
                return list;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    /**
     * 获取datajs内容，分为三部分，以page为界限
     * @param path
     * @return
     */
    public static JsPage getPageInfo(String path) {
        String json = getJsonFromFile(path);
        if (json != null) {
            String first = "";
            int firstIndex = json.indexOf("h5data = [");
            if (firstIndex == -1) {
                firstIndex = json.indexOf("var h5data=[");
                first = "var h5data=[";
            } else {
                first = "h5data = [";
            }
            int lastIndex = json.lastIndexOf("]");
            if (firstIndex != -1 && lastIndex != -1) {
                JsPage jsPage = new JsPage();
                firstIndex = firstIndex + first.length() -1;
//                lastIndex = lastIndex + "}]".length() - 1;
                jsPage.pageInfo = json.substring(firstIndex, lastIndex + 1);
                jsPage.pre = json.substring(0, firstIndex);
                jsPage.next = json.substring(lastIndex + 1);
                return jsPage;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public static ArrayList<HashMap> getDataFromH5data(JsPage jsPage) {

        if (jsPage != null) {
            String json = jsPage.pageInfo;
            try {
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
                        map.put(key, value);
                    }
                    list.add(map);
                }
                return list;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    /**
     * 根据新的页面数据列表生成新的H5data.js的内容
     * @param path 保存编辑后数据的h5data.js的路径
     * @param gson Gson
     * @param lastList 新的页面数据列表
     * @return 要写入h5data.js的内容
     */
    public static String createNewH5DataContent(String path, Gson gson, ArrayList lastList) {
        String json = getJsonFromFile(path);
        int index = json.indexOf("[");
        int lastIndex = json.indexOf("]");
        String newData = json.substring(0,index) + gson.toJson(lastList) + json.substring(lastIndex+1);
        return newData;
    }

    /****************** 外部调用模块 **********************?

    /**
     * 以模板为原型添加新的页面
     * @param pathOrg
     * @param pathNew
     * @param index
     * @return
     */
    public static ArrayList addPage(String pathOrg, String pathNew, int index) {
        ArrayList<HashMap> listOrg = getDataFromH5data(pathOrg);
        ArrayList<HashMap> listNew = getDataFromH5data(pathNew);
        if (listOrg != null && listNew != null) {
            listNew.add(listOrg.get(index));
            return listNew;
        }
        return null;
    }

    /**
     * 以模板为原型添加新的页面
     * @param pathOrg
     * @param pathNew
     * @param pageId
     * @return
     */
    public static ArrayList<HashMap> addPage(String oldDirPath, String newDirPath, String pathOrg, String pathNew, String pageId, String shortCutImageName) {
//        ArrayList<HashMap> listOrg = getDataFromH5data(pathOrg);
//        ArrayList<HashMap> listNew = getDataFromH5data(pathNew);
//        if (listOrg != null && listNew != null) {
//            for (HashMap map:listOrg) {
//                if (map.get("id").equals(pageId)) {
//                    //复制文件
//                    //修改页面id
//                    Set<String> set = map.keySet();
//                    for (String hashKey:set) {
//                        String name = (String) map.get(hashKey);
//                        if (name.endsWith(".jpg") || name.endsWith(".JPG") ||
//                                name.endsWith(".png")||name.endsWith(".PNG")) {
//                            String imgName = randomName(newDirPath, name);
//                            FileUtil.copyFile(oldDirPath + "/" + name, newDirPath + "/" + imgName);
//                            map.put(hashKey, imgName);
//                        }
//                        hashKey = null;
//                    }
//                    map.remove(pageId);
////                    map.put("id", randomPageId(listNew, pageId));
//                    FileUtil.copyFile(oldDirPath + "/" + shortCutImageName, newDirPath + "/" + map.get("id") + ".jpg");
//                    listNew.add(map);
//                    return listNew;
//                }
//            }
//        }
        return null;
    }

    /**
     * 删除指定位置的页面
     * @param path 用户操作的h5data.js的路径
     * @param index 删除页面在列表中的位置
     * @return 删除页面对应的缩略图的key
     */
    public static String deletPage(String dirPath, String path, int index, Gson gson) {
        ArrayList list = getDataFromH5data(path);
        if (list != null && list.size() > index) {
            HashMap hashMap = (HashMap) list.get(index);
            list.remove(index);
            Set<String> set = hashMap.keySet();
            for (String hashKey:set) {
                String name = (String) hashMap.get(hashKey);
                if (name.endsWith(".jpg") || name.endsWith(".JPG") ||
                        name.endsWith(".png")||name.endsWith(".PNG")) {
                    FileUtil.deleteFile(dirPath + "/" + name);
                }
            }
            if (!reWriteDataToFile(path, list, gson))
                return null;
            String id = (String) hashMap.get("id");
            FileUtil.deleteFile(dirPath + "/" + id);
            return id;
        }
        return null;
    }

    /**
     * 修改用户自己的h5data.js
     * @param pathNew
     * @param list
     * @param gson
     * @return
     */
    public static boolean reWriteDataToFile(String pathNew, ArrayList list, Gson gson) {
        if (list != null) {
            String newData = createNewH5DataContent(pathNew, gson, list);
            return FileUtil.writeData(pathNew, newData);
        }
        return false;
    }

    public static boolean updateText(String text)  {

        return false;
    }

    public static String randomName(String path, String name) {
        int rValue = new Random().nextInt(100);
        String newName = rValue + name;
        File dir = new File(path);
        for (File file:dir.listFiles()) {
            if (file.getName().equals(newName)) {
                return randomName(path, name);
            }
        }
        return newName;
    }


    public static String randomPageId(ArrayList<Page> list, String id) {
        int rValue = new Random().nextInt(100);
        String newName = rValue + id;
        for (Page page:list) {
            if (page.getPage().containsValue(newName))
                return randomPageId(list, id);
        }
        return newName;
    }

}
