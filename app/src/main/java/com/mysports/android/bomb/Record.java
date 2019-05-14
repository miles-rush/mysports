package com.mysports.android.bomb;

import cn.bmob.v3.BmobObject;

//存储在云数据库上的运动记录类
public class Record extends BmobObject {
    private String distance;
    private String duration;
    private String date;
    private String speed;

    private String locations;
    private String startPoint;
    private String endPoint;

    private String Text;
    private User author;
    public Record() {

    }

    public String getText() {
        return Text;
    }

    public User getAuthor() {
        return author;
    }

    public String getDate() {
        return date;
    }

    public String getDistance() {
        return distance;
    }

    public String getDuration() {
        return duration;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public String getLocations() {
        return locations;
    }

    public String getSpeed() {
        return speed;
    }

    public String getStartPoint() {
        return startPoint;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public void setLocations(String loactions) {
        this.locations = loactions;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public void setStartPoint(String startPoint) {
        this.startPoint = startPoint;
    }

    public void setText(String text) {
        Text = text;
    }

    public void setAuthor(User author) {
        this.author = author;
    }
}
