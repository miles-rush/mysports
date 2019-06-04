package com.mysports.android.bomb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class Post extends BmobObject {
    private String content;

    private User author;

    private Integer pageView;

    private String picUrl; //图片地址


    private List<String> pics; //本地用
    public Post() {
        content = null;
        author = null;
        pageView = null;
        pics = new ArrayList<String>();
    }



    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public List<String> getPics() {
        return pics;
    }

    public void setPics(List<String> pics) {
        this.pics = pics;
    }

    public Integer getPageView() {
        return pageView;
    }

    public String getContent() {
        return content;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public void setPageView(Integer pageView) {
        this.pageView = pageView;
    }
}
