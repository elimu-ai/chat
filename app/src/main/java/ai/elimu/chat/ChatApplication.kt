package ai.elimu.chat

import ai.elimu.chat.dao.DaoMaster
import ai.elimu.chat.dao.DaoMaster.DevOpenHelper
import ai.elimu.chat.dao.DaoSession
import ai.elimu.chat.util.VersionHelper
import android.app.Application
import android.preference.PreferenceManager
import android.util.Log

class ChatApplication : Application() {
    var daoSession: DaoSession? = null
        private set

    override fun onCreate() {
        Log.i(javaClass.getName(), "onCreate")
        super.onCreate()

        val helper = DevOpenHelper(this, "chat-db")
        val db = helper.getWritableDb()
        daoSession = DaoMaster(db).newSession()

        // Check if the application's versionCode was upgraded
        val sharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
        var oldVersionCode = sharedPreferences.getInt(PREF_APP_VERSION_CODE, 0)
        val newVersionCode = VersionHelper.getAppVersionCode(getApplicationContext())
        if (oldVersionCode == 0) {
            sharedPreferences.edit().putInt(PREF_APP_VERSION_CODE, newVersionCode).commit()
            oldVersionCode = newVersionCode
        }
        if (oldVersionCode < newVersionCode) {
            Log.i(
                javaClass.getName(),
                "Upgrading application from version " + oldVersionCode + " to " + newVersionCode
            )
            //            if (newVersionCode == ???) {
//                // Put relevant tasks required for upgrading here
//            }
            sharedPreferences.edit().putInt(PREF_APP_VERSION_CODE, newVersionCode).commit()
        }
    }

    companion object {
        const val PREF_APP_VERSION_CODE: String = "pref_app_version_code"
    }
}
