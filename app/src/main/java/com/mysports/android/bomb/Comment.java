package com.mysports.android.bomb;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
//评论
public class Comment extends BmobObject {
    private String content; //评论内容

    private User user; //发布评论的人

    private Post post; //评论的帖子

    private String toWho;

    public String getToWho() {
        return toWho;
    }

    public void setToWho(String toWho) {
        this.toWho = toWho;
    }

    public String getContent() {
        return content;
    }

    public Post getPost() {
        return post;
    }

    public User getUser() {
        return user;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

