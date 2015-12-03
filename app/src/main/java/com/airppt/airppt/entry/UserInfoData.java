package com.airppt.airppt.entry;

/**
 * Created by user on 2015/7/30.
 */
public class UserInfoData {
    private UserInfo Data;
    private boolean isSuccess;
    private String message;
    private int errorCode;

    public UserInfo getData() {
        return Data;
    }

    public void setData(UserInfo data) {
        Data = data;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
