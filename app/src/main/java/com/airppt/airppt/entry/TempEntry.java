package com.airppt.airppt.entry;

/**
 * Created by yang on 2015/6/2.
 */
public class TempEntry {
    private TempDataEntry Data;
    private boolean isSuccess;
    private String message;
    private int errorCode;

    public TempDataEntry getData() {
        return Data;
    }

    public void setData(TempDataEntry data) {
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
