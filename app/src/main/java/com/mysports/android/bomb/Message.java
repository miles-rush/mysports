package com.mysports.android.bomb;

import cn.bmob.v3.BmobObject;
//私信类
public class Message extends BmobObject {
    private User sender;
    private User recevier;

    private String content;



    public User getRecevier() {
        return recevier;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setRecevier(User recevier) {
        this.recevier = recevier;
    }
}
