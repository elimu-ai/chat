package ai.elimu.chat.util;

import android.content.Context;
import android.provider.Settings;

public class DeviceInfoHelper {

    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
