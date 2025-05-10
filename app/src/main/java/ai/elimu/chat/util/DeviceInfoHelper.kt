package ai.elimu.chat.util

import android.content.Context
import android.provider.Settings

object DeviceInfoHelper {
    @JvmStatic
    fun getDeviceId(context: Context): String? {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID)
    }
}
