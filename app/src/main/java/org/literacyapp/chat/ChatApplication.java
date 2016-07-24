package org.literacyapp.chat;

import android.app.Application;
import android.util.Log;

import org.greenrobot.greendao.database.Database;
import org.literacyapp.chat.dao.DaoMaster;
import org.literacyapp.chat.dao.DaoSession;

public class ChatApplication extends Application {

    private DaoSession daoSession;

    @Override
    public void onCreate() {
        Log.i(getClass().getName(), "onCreate");
        super.onCreate();

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "chat-db");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
