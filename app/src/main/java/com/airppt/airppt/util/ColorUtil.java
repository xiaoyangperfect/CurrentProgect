package com.airppt.airppt.util;


/**
 * Created by yang on 2015/4/22.
 */
public class ColorUtil {

    public static RGB getRGBFromTemp(String rgb) {
        if(rgb == null || rgb.equals("")) {
            return null;
        }
        int start = rgb.indexOf("(") + 1;
        int end = rgb.indexOf(")");
        if(start != 0 && end != -1 && start < rgb.length() && end < rgb.length() && start < end) {
            String str = rgb.substring(start, end);
        }
        String str = rgb.substring(start , end);
        try {
            String[] rgbs = str.split(",");
            RGB rgb1 = new RGB();
            rgb1.r = Integer.parseInt(rgbs[0]);
            rgb1.g = Integer.parseInt(rgbs[1]);
            rgb1.b = Integer.parseInt(rgbs[2]);
            rgb1.a = (int) Double.parseDouble(rgbs[3])*100;
            return rgb1;
        }catch (Exception ex) {
            return null;
        }
    }


   static class RGB{
        int r;
        int g;
        int b;
        int a;
    }
}
