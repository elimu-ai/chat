package ai.elimu.chat;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.greenrobot.greendao.database.Database;
import ai.elimu.chat.dao.DaoMaster;
import ai.elimu.chat.dao.DaoSession;
import ai.elimu.chat.util.VersionHelper;

public class ChatApplication extends Application {

    public static final String PREF_APP_VERSION_CODE = "pref_app_version_code";

    private DaoSession daoSession;

    @Override
    public void onCreate() {
        Log.i(getClass().getName(), "onCreate");
        super.onCreate();

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "chat-db");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();

        // Check if the application's versionCode was upgraded
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int oldVersionCode = sharedPreferences.getInt(PREF_APP_VERSION_CODE, 0);
        int newVersionCode = VersionHelper.getAppVersionCode(getApplicationContext());
        if (oldVersionCode == 0) {
            sharedPreferences.edit().putInt(PREF_APP_VERSION_CODE, newVersionCode).commit();
            oldVersionCode = newVersionCode;
        }
        if (oldVersionCode < newVersionCode) {
            Log.i(getClass().getName(), "Upgrading application from version " + oldVersionCode + " to " + newVersionCode);
//            if (newVersionCode == ???) {
//                // Put relevant tasks required for upgrading here
//            }
            sharedPreferences.edit().putInt(PREF_APP_VERSION_CODE, newVersionCode).commit();
        }
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
