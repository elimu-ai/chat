package org.literacyapp.wifip2p;

import android.net.wifi.p2p.WifiP2pDevice;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oscarmakala on 23/07/2016.
 */
public class ChatHandler {

    private static volatile ChatHandler Instance;

    public List<WifiP2pDevice> peerList = new ArrayList<>();

    public static ChatHandler getInstance() {
        ChatHandler localInstance = Instance;
        if (localInstance == null) {
            synchronized (ChatHandler.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new ChatHandler();
                }
            }
        }
        return localInstance;
    }



}
