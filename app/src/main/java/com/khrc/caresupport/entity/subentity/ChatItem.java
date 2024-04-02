package com.khrc.caresupport.entity.subentity;

import java.util.Date;

public class ChatItem {

    public static final int TYPE_COMPLAINT = 0;
    public static final int TYPE_RESPONSE = 1;

    private String message;
    private int type;
    private String date;

    public ChatItem(String message, int type, String date) {
        this.message = message;
        this.type = type;
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
