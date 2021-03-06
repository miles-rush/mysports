package com.mysports.android.map;

import com.amap.api.location.AMapLocation;

import java.util.ArrayList;
import java.util.List;

public class PathRecord {
    private AMapLocation mStartPoint; //开始点
    private AMapLocation mEndPoint; //结束点
    private List<AMapLocation> mPathLinePoints = new ArrayList<AMapLocation>(); //路径坐标集合
    private String mDistance; //路径长度
    private String mDuration; //耗费时间
    private String mAveragespeed; //平均速度
    private String mDate; //记录时间
    private int mId = 0;

    public PathRecord() {

    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public AMapLocation getStartpoint() {
        return mStartPoint;
    }

    public void setStartpoint(AMapLocation startpoint) {
        this.mStartPoint = startpoint;
    }

    public AMapLocation getEndpoint() {
        return mEndPoint;
    }

    public void setEndpoint(AMapLocation endpoint) {
        this.mEndPoint = endpoint;
    }

    public List<AMapLocation> getPathline() {
        return mPathLinePoints;
    }

    public void setPathline(List<AMapLocation> pathline) {
        this.mPathLinePoints = pathline;
    }

    public String getDistance() {
        return mDistance;
    }

    public void setDistance(String distance) {
        this.mDistance = distance;
    }

    public String getDuration() {
        return mDuration;
    }

    public void setDuration(String duration) {
        this.mDuration = duration;
    }

    public String getAveragespeed() {
        return mAveragespeed;
    }

    public void setAveragespeed(String averagespeed) {
        this.mAveragespeed = averagespeed;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        this.mDate = date;
    }

    public void addpoint(AMapLocation point) {
        mPathLinePoints.add(point);
    }

    @Override
    public String toString() {
        StringBuilder record = new StringBuilder();
        record.append("存储:" + getPathline().size() + ", ");
        record.append("运动里程:" + getDistance() + "m, ");
        record.append("运动时间:" + getDuration() + "s");
        return record.toString();
    }
}
