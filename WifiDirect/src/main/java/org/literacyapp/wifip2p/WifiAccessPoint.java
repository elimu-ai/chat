package org.literacyapp.wifip2p;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Handler;
import android.util.Log;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION;

/**
 * Created by oscarmakala on 22/07/2016.
 * Helper for wifi service discovery
 */
public class WifiAccessPoint implements
        WifiP2pManager.ConnectionInfoListener,
        WifiP2pManager.GroupInfoListener,
        WifiP2pManager.PeerListListener {

    private static final String AVAILABLE = "available";
    private static final String TAG = WifiAccessPoint.class.getSimpleName();
    private static final String SEPERATOR = "|";
    private final Context mContext;
    private final WifiP2pManager mWsdManager;
    private final WifiP2pManager.Channel mChannel;

    public static final String SERVICE_REG_TYPE = "_wdm_p2p._tcp";
    private WifiP2pManager.ActionListener mActionListener;
    private BroadcastReceiver mReceiver;

    private String mNetworkName = "";
    private String mPassphrase = "";
    private String mInetAddress = "";
    private IntentFilter intentFilter;


    public WifiAccessPoint(Context context) {
        mContext = context;
        mWsdManager = (WifiP2pManager) mContext.getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mWsdManager.initialize(mContext, mContext.getMainLooper(), null);
        mReceiver = new AccessPointReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        initialize();
    }

    /**
     * If you're providing a local service, you need to register it for service discovery.
     * Once your local service is registered,
     * the framework automatically responds to service discovery requests from peers.
     */
    public void startLocalService(String instanceName) {
        Map<String, String> record = new HashMap<>();
        record.put(AVAILABLE, "visible");
        WifiP2pDnsSdServiceInfo serviceInfo = WifiP2pDnsSdServiceInfo.newInstance(instanceName, SERVICE_REG_TYPE, record);
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.LOGGER, TAG, "Add local service :" + instanceName);
        mWsdManager.addLocalService(mChannel, serviceInfo, mActionListener);
    }

    public void initialize() {
        initializeActionListener();
    }

    private void initializeActionListener() {
        mActionListener = new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Added service discovery request");
            }

            @Override
            public void onFailure(int code) {
                if (code == WifiP2pManager.P2P_UNSUPPORTED) {
                    Log.d(TAG, "P2P isn't supported on this device");
                }
            }
        };
    }

    public void start() {
        mContext.registerReceiver(mReceiver, intentFilter);
        mWsdManager.createGroup(mChannel, mActionListener);
    }

    public void stop() {
        if (mReceiver != null) {
            try {
                mContext.unregisterReceiver(mReceiver);
                mReceiver = null;
            } catch (Exception e) {
                Log.e(TAG, "stoping receiver", e);
            }
        }
        stopLocalServices();
        removeGroup();
    }

    private void removeGroup() {
        if (mWsdManager != null) {
            mWsdManager.removeGroup(mChannel, mActionListener);
        }
    }

    private void stopLocalServices() {
        mNetworkName = "";
        mPassphrase = "";
        if (mWsdManager != null && mChannel != null) {
            mWsdManager.clearLocalServices(mChannel, mActionListener);
        }
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
        if (wifiP2pInfo != null && wifiP2pInfo.isGroupOwner) {
            mInetAddress = wifiP2pInfo.groupOwnerAddress.getHostAddress();
            mWsdManager.requestGroupInfo(mChannel, WifiAccessPoint.this);
        } else {
            Log.d(TAG, "we are client: group owner address is :" + wifiP2pInfo.groupOwnerAddress);
        }
    }

    @Override
    public void onGroupInfoAvailable(WifiP2pGroup wifiP2pGroup) {
        try {
            if (wifiP2pGroup != null) {
                Collection<WifiP2pDevice> wifiP2pDeviceList = wifiP2pGroup.getClientList();
                int num = 0;
                for (WifiP2pDevice wifiP2pDevice : wifiP2pDeviceList) {
                    num++;
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.LOGGER, TAG, "client:" + num + ":" + wifiP2pDevice.deviceName + " " + wifiP2pDevice.deviceAddress);
                }
                if ((mNetworkName.equals(wifiP2pGroup.getNetworkName())) && (mPassphrase.equals(wifiP2pGroup.getPassphrase()))) {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.LOGGER, TAG, "Already have local service for " + mNetworkName + " ," + mPassphrase);
                    Log.d(TAG, "Already have local service for " + mNetworkName + " ," + mPassphrase);
                } else {
                    mNetworkName = wifiP2pGroup.getNetworkName();
                    mPassphrase = wifiP2pGroup.getPassphrase();
                    startLocalService("NI:" + mNetworkName + ":" + mPassphrase + ":" + mInetAddress);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
        ChatHandler.getInstance().peerList.clear();
        ChatHandler.getInstance().peerList.addAll(wifiP2pDeviceList.getDeviceList());
        int numm = 0;
        for (WifiP2pDevice peer : ChatHandler.getInstance().peerList) {
            numm++;
            Log.d(TAG, numm + ": " + peer.deviceName + " " + peer.deviceAddress);
        }
        if (numm > 0) {
            startClientServiceDiscovery();
        } else {
            startPeerDiscovery();
        }
    }

    private void startPeerDiscovery() {
        mWsdManager.discoverPeers(mChannel, mActionListener);
    }

    private void startClientServiceDiscovery() {
        WifiP2pDnsSdServiceRequest wifiP2pDnsSdServiceRequest = WifiP2pDnsSdServiceRequest.newInstance(SERVICE_REG_TYPE);
        final Handler handler = new Handler();
        mWsdManager.addServiceRequest(mChannel, wifiP2pDnsSdServiceRequest, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                handler.postDelayed(new Runnable() {
                    //There are supposedly a possible race-condition bug with the service discovery
                    // thus to avoid it, we are delaying the service discovery start here
                    public void run() {
                        mWsdManager.discoverServices(mChannel, new WifiP2pManager.ActionListener() {
                            public void onSuccess() {
                                Log.d(TAG, "Started client service discovery");
                            }

                            public void onFailure(int reason) {
                            }
                        });
                    }
                }, 1000);
            }

            @Override
            public void onFailure(int i) {

            }
        });
    }


    private class AccessPointReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
                int extraWifiState = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                if (extraWifiState == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                    // startLocalService();
                } else {
                    //stopLocalService();
                    //Todo: Add the state monitoring in higher level, stop & re-start all when happening
                }
            } else if (WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
                if (networkInfo.isConnected()) {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.LOGGER, TAG, "We are connected, will check info now");
                    mWsdManager.requestConnectionInfo(mChannel, WifiAccessPoint.this);
                } else {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.LOGGER, TAG, "We are DIS-connected");
                }
            }
        }
    }
}
