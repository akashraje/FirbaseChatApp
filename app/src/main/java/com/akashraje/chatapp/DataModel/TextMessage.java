package com.akashraje.chatapp.DataModel;

/**
 * Created by akashraje on 02/07/17.
 */

public class TextMessage {
    private String text;
    private String userName;
    private long sentTime;


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getSentTime() {
        return sentTime;
    }

    public void setSentTime(long sentTime) {
        this.sentTime = sentTime;
    }
}
