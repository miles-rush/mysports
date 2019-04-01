package com.mysports.android.bomb;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class PostImage extends BmobObject {
    private BmobFile pic;

    private Post post;

    public Post getPost() {
        return post;
    }

    public BmobFile getPic() {
        return pic;
    }

    public void setPic(BmobFile pic) {
        this.pic = pic;
    }

    public void setPost(Post post) {
        this.post = post;
    }


}
