package com.airppt.airppt.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.airppt.airppt.MyApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Random;

/**
 * Created by user on 2015/4/27.
 */
public class FileUtil {
    //SD卡路径
    public static String SD_PATH;
    public static String JS_FILE_PATH;
    //模板文件夹
    public static String TEMP_PATH = getSdPath() + "/airppt/airppt_temps";
    //用户作品文件夹
    public static String WORKS_PATH = getSdPath() + "/airppt/airppt_works/work";
    //上传路径
    public static String UPLOAD_PATH = getSdPath() + "/airppt/upload";

    public static String BACKUP_PATH = getSdPath() + "/airppt/backup";

    public static String TEXT_BACKUP = getSdPath() + "/airppt/textcache.txt";
    public static String NAMEPOOL = "namepool.txt";

    static {
        init();
    }

    public static void init() {
        SD_PATH = getSdPath() + "/airppt";
        JS_FILE_PATH = SD_PATH + "/data.js";
    }

    public static String getSdPath() {
        if (android.os.Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().getPath();
        } else {
            return MyApplication.getInstance().getApplicationContext().getFilesDir().getAbsolutePath();
        }
    }

    /**
     * 文件是否存在
     *
     * @param path 文件路径
     * @return true存在；false不存在
     */
    public static boolean isFileExist(String path) {
        File file = new File(path);
        return file.exists();
    }

    /**
     * 删除单个文件
     *
     * @param filePath 文件路径
     * @return 删除与否
     */
    public static boolean deleteFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                return file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String newImageName() {
        return new Random().nextInt(100) + ".jpg";
    }

    /**
     * 删除文件夹及文件夹下的图片，建议每次app退出的时候使用
     *
     * @param dir 文件夹目录
     */
    public static boolean deleteDirectory(File dir) {
        try {
            if (!dir.exists())
                return true;
            if (dir.isDirectory()) {
                File[] listFiles = dir.listFiles();
                for (int i = 0; i < listFiles.length; i++) {
                    deleteDirectory(listFiles[i]);
                }
            }
            return dir.delete();
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * 创建文件目录
     *
     * @param path 目录
     * @return 创建是否成功
     */
    public static String createFileDir(String path) {
        File file = new File(path);
        if (!file.exists() && !file.isDirectory()) {
            if (file.mkdir())
                return path;
        }
        if (file.exists()) {
            return path;
        } else {
            return null;
        }

    }

    /**
     * 创建新的文件目录
     *
     * @param path 目录
     * @return 创建是否成功
     */
    public static String createNewFileDir(String path) {
        File file = new File(path);

        if (file.exists()) {
            deleteDirectory(file);
        }
        if (file.mkdir())
            return path;
        return null;
    }

    /**
     * 创建文件
     *
     * @param filePath 文件路径
     * @return 是否创建成功
     */
    public static File createFile(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                if (!file.createNewFile())
                    return null;
            }
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将图片保存为指定路径指定名称
     *
     * @param bitmap 图片
     * @param path   全路径
     */
    public static boolean saveImage(Bitmap bitmap, String path) {
        try {
            File file = new File(path);
            if (createFile(path) != null) {
                FileOutputStream outputStream = new FileOutputStream(path);
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)) {
                    outputStream.flush();
                    outputStream.close();
                    outputStream = null;
                    return true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static void saveJson(String json) {
        try {
            File file = createFile(JS_FILE_PATH);
            if (file != null) {
                RandomAccessFile raf = new RandomAccessFile(file, "rwd");
                raf.seek(file.length());
                raf.write(json.getBytes());
                raf.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 写数据入H5Data.js
     *
     * @param json 要写的数据
     * @return 是否写成功
     */
    public static boolean writeData(String path, String json) {
        File file = new File(path);
        try {
            if (!file.exists())
                createFile(path);
            FileWriter writer = new FileWriter(file);
            writer.write("");
            writer.write(json);
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 复制文件
     *
     * @param oldPath
     * @param newPath
     */
    public static boolean copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            createFile(newPath);
            InputStream inStream = new FileInputStream(oldPath); //读入原文件
            FileOutputStream fs = new FileOutputStream(newPath);
            byte[] buffer = new byte[1444];
            int length;
            while ((byteread = inStream.read(buffer)) != -1) {
                bytesum += byteread; //字节数 文件大小
                System.out.println(bytesum);
                fs.write(buffer, 0, byteread);
            }
            inStream.close();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("error", "copy file error" + e.getMessage());
            return false;
        }
    }

    public static String htmlFileName;
    /**
     * 复制目录下所有文件
     *
     * @param oldPath
     * @param newPath
     * @return
     */
    public static boolean copyDir(Context context, String oldPath, String newPath) {
        File oldDir = new File(oldPath);
        for (int i = 0; i < oldDir.listFiles().length; i++) {
            String name = oldDir.listFiles()[i].getName();
            if (!copyFile((oldPath + "/" + name), (newPath + "/" + name)))
                return false;
        }
        return true;
    }

    /**
     * 查找指定目录下的html文件名
     * @param path 目录
     * @return 文件名
     */
    public static String getHtmlFileName(String path) {
        File fileDir = new File(path);
        for (File file:fileDir.listFiles()) {
            String name = file.getName();
            if (name.endsWith(".html")) {
                return name;
            }
        }
        return null;
    }

}
