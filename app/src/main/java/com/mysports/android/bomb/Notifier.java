package com.mysports.android.bomb;

import cn.bmob.v3.BmobObject;

//消息通知器
public class Notifier extends BmobObject {
    private int type; //消息种类
    /*
    1.运动记录点赞--跳转到运动记录
    2.动态点赞--跳转到动态
    3.收到关注--不跳转
    4.动态收到评论--跳转到动态
    5.评论收到回复--跳转到动态
    6.收到私信--进入私信
     */

    private User Interactive; //交互对象

    private User my;

    private String objectID;


    public int getType() {
        return type;
    }

    public String getObjectID() {
        return objectID;
    }

    public void setInteractive(User interactive) {
        Interactive = interactive;
    }

    public void setMy(User my) {
        this.my = my;
    }

    public User getInteractive() {
        return Interactive;
    }

    public User getMy() {
        return my;
    }


    public void setObjectID(String objectID) {
        this.objectID = objectID;
    }


    public void setType(int type) {
        this.type = type;
    }


}
