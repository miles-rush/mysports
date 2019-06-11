package com.mysports.android.bomb;

import cn.bmob.v3.BmobObject;

public class LikesRelation extends BmobObject {
    private User master;
    private User guest;

    public User getGuest() {
        return guest;
    }

    public User getMaster() {
        return master;
    }

    public void setGuest(User guest) {
        this.guest = guest;
    }

    public void setMaster(User master) {
        this.master = master;
    }
}

