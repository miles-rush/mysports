package com.mysports.android.util;

import com.mysports.android.bomb.Notifier;
import com.mysports.android.bomb.User;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

//消息通知器
public class NotiferUtil {
    static private void notifer(String objectID, String master, int type) {
        Notifier notifier = new Notifier();
        notifier.setObjectID(objectID);
        User m = new User(); //发送给他
        m.setObjectId(master);
        User g = new User(); //发送者
        g.setObjectId(BmobUser.getCurrentUser(User.class).getObjectId());

        notifier.setMy(g);
        notifier.setInteractive(m);
        notifier.setType(type);

        notifier.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {

                }else {

                }
            }
        });
    }

    static public void notiferRecordGood(String objectID, String master) {
        notifer(objectID, master, 1);
    }

    static public void notiferPostGood(String objectID, String master) {
        notifer(objectID, master, 2);
    }

    static public void notiferGetLike(String objectID, String master) {
        notifer(objectID, master, 3);
    }

    static public void notiferPostComment(String objectID, String master) {
        notifer(objectID, master, 4);
    }

    static public void notiferCommentComment(String objectID, String master) {
        notifer(objectID, master, 5);
    }

    static public void notiferMessage(String objectID, String master) {
        notifer(objectID, master, 6);
    }
}
