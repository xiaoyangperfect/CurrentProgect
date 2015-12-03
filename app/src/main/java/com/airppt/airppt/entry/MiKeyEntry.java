package com.airppt.airppt.entry;

/**
 * Created by yang on 2015/6/13.
 */
public class MiKeyEntry {
    private Long Data;
    private boolean isSuccess;
    private String message;
    private int errorCode;

    public Long getData() {
        return Data;
    }

    public void setData(Long data) {
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
