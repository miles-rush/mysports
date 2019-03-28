package com.mysports.android.bomb;

import java.util.Date;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class Community extends BmobObject {
    private String name;

    private String text; //动态文本内容

    //private Date date; //动态日期

    private BmobFile pic; //配图

    private Integer clickNum;

    private Integer accountID;

    public Integer getAccountID() {
        return accountID;
    }

    public void setAccountID(Integer accountID) {
        this.accountID = accountID;
    }

    public Integer getClickNum() {
        return clickNum;
    }

    public void setClickNum(Integer clickNum) {
        this.clickNum = clickNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BmobFile getPic() {
        return pic;
    }

    public void setPic(BmobFile pic) {
        this.pic = pic;
    }



    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
