package org.literacy.wifip2p;

import android.net.wifi.p2p.WifiP2pDevice;

import org.literacy.wifip2p.model.WiFiP2pService;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by oscarmakala on 23/07/2016.
 */
public class ChatHandler {

    private static volatile ChatHandler Instance;

    public ConcurrentHashMap<String, WiFiP2pService> wiFiP2pServices = new ConcurrentHashMap<>();

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


    /**
     * still have to check if truely instancename will be unique.
     * as its a check of existing in arraylist
     *
     * @param service
     */
    public void addDevice(WiFiP2pService service) {
        if (!wiFiP2pServices.contains(service.getAddress())) {
            wiFiP2pServices.put(service.getAddress(), service);
        }
    }


    public void setDevicePort(WifiP2pDevice device, String listenport) {
        if (wiFiP2pServices.contains(device.deviceAddress)) {
            wiFiP2pServices.get(device.deviceAddress).setPort(Integer.parseInt(listenport));
        }
    }
}
