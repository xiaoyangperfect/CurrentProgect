package com.airppt.airppt.util;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.airppt.airppt.entry.DefaultConfig;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

/**
 * Created by yang on 2015/4/22.
 */
public class TemplateParser {

    public static DefaultConfig parser(Gson gson, InputStream inputStream) {
        String json = "";
        DefaultConfig config = null;
        try {

            InputStreamReader inReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader reader = new BufferedReader(inReader);
            String str = "";
            while ((str = reader.readLine()) != null) {
                json = json + str;
            }

            Type type = new TypeToken<DefaultConfig>(){}.getType();
            config = gson.fromJson(json, type);
        } catch (Exception ex) {
            Log.e("Parser", "parser error");
        }
        return config;
    }
}
