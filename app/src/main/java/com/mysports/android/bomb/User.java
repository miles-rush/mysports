package com.mysports.android.bomb;


import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;

public class User extends BmobUser {
    private Integer accountID;

    public Integer getAccountID() {
        return accountID;
    }

    public void setAccountID(Integer accountID) {
        this.accountID = accountID;
    }
}
