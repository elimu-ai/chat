package ai.elimu.chat


import ai.elimu.chat.di.ServiceLocator
import ai.elimu.chat.util.Constants
import ai.elimu.chat.util.VersionHelper
import android.app.Application
import android.preference.PreferenceManager
import android.util.Log
import androidx.core.content.edit

//@HiltAndroidApp
class ChatApplication : Application() {


    override fun onCreate() {
        Log.i(javaClass.getName(), "onCreate")
        super.onCreate()
        ServiceLocator.initialize(this)

        // Check if the application's versionCode was upgraded
        val sharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
        var oldVersionCode = sharedPreferences.getInt(Constants.PREF_APP_VERSION_CODE, 0)
        val newVersionCode = VersionHelper.getAppVersionCode(applicationContext)
        if (oldVersionCode == 0) {
            sharedPreferences.edit(commit = true) { putInt(Constants.PREF_APP_VERSION_CODE, newVersionCode) }
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
            sharedPreferences.edit(commit = true) { putInt(Constants.PREF_APP_VERSION_CODE, newVersionCode) }
        }
    }
}
