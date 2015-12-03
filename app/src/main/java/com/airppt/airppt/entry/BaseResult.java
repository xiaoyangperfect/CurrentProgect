package com.airppt.airppt.entry;

/**
 * Created by yang on 2015/7/3.
 */
public class BaseResult {
    private String Code;
    private String Message;

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getCode() {

        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }
}
