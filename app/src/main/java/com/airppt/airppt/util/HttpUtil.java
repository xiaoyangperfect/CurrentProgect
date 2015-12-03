package com.airppt.airppt.util;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.IOException;

/**
 * Created by user on 2015/4/30.
 */
public class HttpUtil {

    public static final int HTTP_UP_LOAD_SUCCESS = 101;
    public static final int HTTP_UP_LOAD_FAIL = 102;

    public static void upLoadFilesToService(final Handler handler, final String url, final String sd_path) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpResponse response = upLoadFiles(url, sd_path);
                Message message = new Message();
                if (response != null) {
                    try {
                        String json = Util.inputStream2String(response.getEntity().getContent());
                        Log.e("json", json);
                        message.obj = json;
                        message.what = HTTP_UP_LOAD_SUCCESS;
                        handler.handleMessage(message);
                        return;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                message.what = HTTP_UP_LOAD_FAIL;
                handler.handleMessage(message);
            }
        }).start();
    }

    public static HttpResponse upLoadFiles(String url, String sd_path) {
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);
        MultipartEntityBuilder builder = initBuilder(sd_path);
        Log.e("builder", builder + "");
        if (builder != null) {
            HttpEntity entity = builder.build();
            post.setEntity(entity);
            try {
                HttpResponse response = client.execute(post);
                Log.e("code", response.getStatusLine().getStatusCode() + "");
                if (response.getStatusLine().getStatusCode() == 200) {
                    return response;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static MultipartEntityBuilder initBuilder(String sd_path) {
        Log.e("sd_path", sd_path);
        File file = new File(sd_path);
        Log.e("file", file.isDirectory() + "");
        if (file.exists()) {
            File[] files = file.listFiles();
            StringBuffer buffer = new StringBuffer();
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            for (File file1:files) {
                builder.addBinaryBody(file1.getName(), file1);
            }
            builder.addTextBody("userId", "xiaoy");
            builder.addTextBody("templateId", "0");
            builder.addTextBody("worksId", "0");
            return builder;
        } else {
            return null;
        }
    }
}
