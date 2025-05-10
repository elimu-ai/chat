package ai.elimu.chat.util

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log

object VersionHelper {
    /**
     * @return Application's version code from the `PackageManager`.
     */
    fun getAppVersionCode(context: Context): Int {
        Log.i(VersionHelper::class.java.getName(), "getAppVersionCode")

        try {
            val packageInfo =
                context.packageManager.getPackageInfo(context.packageName, 0)
            return packageInfo.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            throw RuntimeException("Could not get package name: $e")
        }
    }
}
