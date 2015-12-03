package com.airppt.airppt.util;

/**
 * Created by yang on 2015/6/2.
 */
public class HttpConfig {

    private String getHomelist;
    private String getWorks;

//    public static String BASE_URL_1 = "http://airppt.whs.hooyes.net:5566/";
    public static String BASE_URL_1 = "http://www.airppt.cn/";
    public static String BASE_URL_STORE = "http://www.airppt.cn/";
    public static String BASE_URL = "http://a.airppt.cn/";
    public static String CHANGE_MUSIC = "http://www.airppt.cn/air/index.html?d=android&isValid=1.0.0";

    //区分登录与不登录
    private static String GETTEMPLATES = BASE_URL_1 + "AirPPT/GetWorks";
    private static String GETTEMPLATES_NOLOGIN = BASE_URL_1 + "NoLogin/GetWorks";
    private static String GETHOMELIST = BASE_URL_1 + "AirPPT/GetHomeList";
    private static String GETHOMELIST_NOLOGIN = BASE_URL_1 + "NoLogin/GetHomeList";


    public static String GETKEY = BASE_URL_1 + "AirPPT/GetKey";
    public static String CREATEUNDONEWORK = BASE_URL_1 + "AirPPT/CreateUndoneWork";
    public static String CREATEWORK = BASE_URL_1 + "AirPPT/CreateWorks";
    public static String FEEDBACK = BASE_URL_1 + "NoLogin/FeedBack";
    public static String GetHotWorkTagList = BASE_URL_1 + "AirPPT/GetHotWorkTagList";
    public static String UPLOADWORKIMGS = BASE_URL_1 + "AirPPT/UploadWorkImgs";
    public static String USERLOGIN = BASE_URL_1 + "NoLogin/UserLogin";
    public static String UPDATEWORKSDETIAL = BASE_URL_1 + "AirPPT/UpdateWorksDetial";
    public static String DELETEUNDONEWORKS = BASE_URL_1 + "AirPPT/DeleteUndoneWorks";
    public static String CLOSEWORK = BASE_URL_1 + "AirPPT/CloseWork";
    public static String SETUSERSIGNORQRCODE = BASE_URL_1 + "AirPPT/SetUserSignOrQRCode";

    public static String getGetHomelist(String userId) {
        if (Util.isStringNotEmpty(userId)) {
            return GETHOMELIST;
        } else {
            return GETHOMELIST_NOLOGIN;
        }
    }

    public void setGetHomelist(String getHomelist) {
        this.getHomelist = getHomelist;
    }

    public static String getGetWorks(String userId) {
        if (Util.isStringNotEmpty(userId)) {
            return GETTEMPLATES;
        } else {
            return GETTEMPLATES_NOLOGIN;
        }
    }

    public void setGetWorks(String getWorks) {
        this.getWorks = getWorks;
    }
}
