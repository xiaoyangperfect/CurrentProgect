package com.airppt.airppt.entry;

/**
 * Created by user on 2015/7/30.
 */
public class UserInfo {
    private int UserId;
    private String UnionId;
    private String Nickname;
    private int Sex;
    private String Headimgurl;
    private String Country;
    private String OpenId;
    private String Sign;
    private String QRCode;
    private int Platform;
    private int Level;
    private int WorksTotal;
    private int DianzanTotal;
    private String CreateDate;

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }

    public String getUnionId() {
        return UnionId;
    }

    public void setUnionId(String unionId) {
        UnionId = unionId;
    }

    public String getNickname() {
        return Nickname;
    }

    public void setNickname(String nickname) {
        Nickname = nickname;
    }

    public int getSex() {
        return Sex;
    }

    public void setSex(int sex) {
        Sex = sex;
    }

    public String getHeadimgurl() {
        return Headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        Headimgurl = headimgurl;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getOpenId() {
        return OpenId;
    }

    public void setOpenId(String openId) {
        OpenId = openId;
    }

    public String getSign() {
        return Sign;
    }

    public void setSign(String sign) {
        Sign = sign;
    }

    public String getQRCode() {
        return QRCode;
    }

    public void setQRCode(String QRCode) {
        this.QRCode = QRCode;
    }

    public int getPlatform() {
        return Platform;
    }

    public void setPlatform(int platform) {
        Platform = platform;
    }

    public int getLevel() {
        return Level;
    }

    public void setLevel(int level) {
        Level = level;
    }

    public int getWorksTotal() {
        return WorksTotal;
    }

    public void setWorksTotal(int worksTotal) {
        WorksTotal = worksTotal;
    }

    public int getDianzanTotal() {
        return DianzanTotal;
    }

    public void setDianzanTotal(int dianzanTotal) {
        DianzanTotal = dianzanTotal;
    }

    public String getCreateDate() {
        return CreateDate;
    }

    public void setCreateDate(String createDate) {
        CreateDate = createDate;
    }
}
