package ai.elimu.chat

import ai.elimu.chat.dao.DaoMaster
import ai.elimu.chat.dao.DaoMaster.DevOpenHelper
import ai.elimu.chat.dao.DaoSession
import ai.elimu.chat.util.VersionHelper
import android.app.Application
import android.preference.PreferenceManager
import android.util.Log
import androidx.core.content.edit

class ChatApplication : Application() {
    var daoSession: DaoSession? = null
        private set

    override fun onCreate() {
        Log.i(javaClass.getName(), "onCreate")
        super.onCreate()

        val helper = DevOpenHelper(this, "chat-db")
        val db = helper.writableDb
        daoSession = DaoMaster(db).newSession()

        // Check if the application's versionCode was upgraded
        val sharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
        var oldVersionCode = sharedPreferences.getInt(PREF_APP_VERSION_CODE, 0)
        val newVersionCode = VersionHelper.getAppVersionCode(applicationContext)
        if (oldVersionCode == 0) {
            sharedPreferences.edit(commit = true) { putInt(PREF_APP_VERSION_CODE, newVersionCode) }
            oldVersionCode = newVersionCode
        }
        if (oldVersionCode < newVersionCode) {
            Log.i(
                javaClass.getName(),
                "Upgrading application from version $oldVersionCode to $newVersionCode"
            )
            //            if (newVersionCode == ???) {
//                // Put relevant tasks required for upgrading here
//            }
            sharedPreferences.edit(commit = true) { putInt(PREF_APP_VERSION_CODE, newVersionCode) }
        }
    }

    companion object {
        const val PREF_APP_VERSION_CODE: String = "pref_app_version_code"
    }
}
