package org.literacyapp.wifip2p;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * Created by oscarmakala on 27/07/2016.
 */
public class WifiConnection {
    private final Context mContext;
    private final String mIpAddress;
    private WifiConnectionBroadcastReceiver mReceiver;
    private WifiManager mWifiManager;
    private WifiConfiguration mWifiConfig;
    private int networkId;
    static final public int ConectionStateNONE = 0;
    static final public int ConectionStatePreConnecting = 1;
    static final public int ConectionStateConnecting = 2;
    static final public int ConectionStateConnected = 3;
    static final public int ConectionStateDisconnected = 4;
    private int mConnectionState = ConectionStateNONE;
    private boolean hadConnection;
    private IntentFilter intentFilter = new IntentFilter();

    public WifiConnection(Context context, String networkName, String passPhrase, String ipAddress) {
        mContext = context;
        mIpAddress = ipAddress;

        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        mReceiver = new WifiConnectionBroadcastReceiver();
        mContext.registerReceiver(mReceiver, intentFilter);


        mWifiManager = (WifiManager) mContext.getSystemService(mContext.WIFI_SERVICE);

        mWifiConfig = new WifiConfiguration();
        mWifiConfig.SSID = String.format("\"%s\"", networkName);
        mWifiConfig.preSharedKey = String.format("\"%s\"", passPhrase);

        networkId = mWifiManager.addNetwork(mWifiConfig);
        mWifiManager.enableNetwork(networkId, false);
        mWifiManager.reconnect();
    }


    public String getIpAddress() {
        return mIpAddress;
    }

    public int getNetworkId() {
        return networkId;
    }

    public void setNetworkId(int networkId) {
        this.networkId = networkId;
    }


    public void stop() {
        if (mReceiver != null) {
            mContext.unregisterReceiver(mReceiver);
        }
        if (mWifiManager != null) {
            mWifiManager.disconnect();
        }

    }

    private class WifiConnectionBroadcastReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info != null && info.isConnected()) {
                    hadConnection = true;
                    mConnectionState = ConectionStateConnected;
                } else if (info != null && info.isConnectedOrConnecting()) {
                    mConnectionState = ConectionStateConnecting;
                } else {
                    if (hadConnection) {
                        mConnectionState = ConectionStateDisconnected;
                    } else {
                        mConnectionState = ConectionStatePreConnecting;
                    }
                }
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.WIFI_CONNECTIONSTATE, mConnectionState);
                WifiInfo wiffo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
                if (wiffo != null) {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.WIFICON_SERVERADDRESS, wiffo);
                }
            }
        }
    }
}
