package org.literacyapp.wifip2p;

import android.content.Context;

/**
 * Created by oscarmakala on 26/07/2016.
 */
public class WifiHelper {

    private static volatile WifiHelper Instance;
    private final Context mContext;

    public static WifiHelper getInstance(Context context) {
        WifiHelper localInstance = Instance;
        if (localInstance == null) {
            synchronized (WifiHelper.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new WifiHelper(context);
                }
            }
        }
        return localInstance;
    }

    public WifiHelper(Context context) {
        mContext = context;
    }


}
