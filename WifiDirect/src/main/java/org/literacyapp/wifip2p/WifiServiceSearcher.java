package org.literacyapp.wifip2p;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION;

/**
 * Created by oscarmakala on 28/07/2016.
 */
public class WifiServiceSearcher implements WifiP2pManager.ChannelListener {
    private static final String TAG = WifiServiceSearcher.class.getSimpleName();
    private final Context mContext;
    private final WifiP2pManager.Channel mChannel;
    private WifiP2pManager mWifiP2pManager;
    private LocalBroadcastManager mBroadcast;
    private ServiceSearchReceiver mReceiver;
    private IntentFilter filter = new IntentFilter();
    private WifiP2pManager.PeerListListener mPeerListener;
    private WifiP2pManager.DnsSdServiceResponseListener mServiceListener;
    private WifiP2pManager.ActionListener mActionListener;

    enum ServiceState {
        NONE,
        DiscoverPeer,
        DiscoverService
    }

    ServiceState myServiceState = ServiceState.NONE;


    public WifiServiceSearcher(Context context) {
        mContext = context;
        mWifiP2pManager = (WifiP2pManager) mContext.getSystemService(mContext.WIFI_P2P_SERVICE);
        mChannel = mWifiP2pManager.initialize(mContext, mContext.getMainLooper(), this);
        initialize();
    }

    @Override
    public void onChannelDisconnected() {

    }

    public void stop() {
        if (mReceiver != null) {
            mContext.unregisterReceiver(mReceiver);
        }
        stopDiscovery();
        stopPeerDiscovery();
    }

    private void stopDiscovery() {
        mWifiP2pManager.clearServiceRequests(mChannel, mActionListener);
    }

    private void stopPeerDiscovery() {
        mWifiP2pManager.stopPeerDiscovery(mChannel, mActionListener);
    }

    public void start() {
        mReceiver = new ServiceSearchReceiver();

        filter.addAction(WIFI_P2P_STATE_CHANGED_ACTION);
        filter.addAction(WIFI_P2P_CONNECTION_CHANGED_ACTION);
        filter.addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION);
        filter.addAction(WIFI_P2P_PEERS_CHANGED_ACTION);
        mContext.registerReceiver(mReceiver, filter);

        mWifiP2pManager.setDnsSdResponseListeners(mChannel, mServiceListener, null);
        startPeerDiscovery();

    }

    private void startPeerDiscovery() {
        mWifiP2pManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                myServiceState = ServiceState.DiscoverPeer;
            }

            @Override
            public void onFailure(int i) {

            }
        });
    }

    public void initialize() {
        initializePeerListener();
        initializeResponseListener();
        initializeActionListener();
    }

    private void initializeActionListener() {
        mActionListener = new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int code) {
                if (code == WifiP2pManager.P2P_UNSUPPORTED) {

                }
            }
        };
    }

    private void initializeResponseListener() {
        mServiceListener = new WifiP2pManager.DnsSdServiceResponseListener() {
            @Override
            public void onDnsSdServiceAvailable(String instanceName, String serviceType, WifiP2pDevice wifiP2pDevice) {
                Log.d(TAG, "initializeResponseListener: service type " + serviceType);
                if (serviceType.startsWith(WifiAccessPoint.SERVICE_REG_TYPE)) {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.ACCESS_POINT_INSTANCE_INFO, instanceName);
                }
                startPeerDiscovery();
            }
        };
    }

    private void initializePeerListener() {
        mPeerListener = new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
                int numm = 0;
                for (WifiP2pDevice peer : wifiP2pDeviceList.getDeviceList()) {
                    numm++;
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.LOGGER, TAG, numm + ": " + peer.deviceName + " " + peer.deviceAddress);
                    Log.d(TAG, numm + ": " + peer.deviceName + " " + peer.deviceAddress);
                }
                if (numm > 0) {
                    startServiceDiscovery();
                } else {
                    startPeerDiscovery();
                }
            }
        };
    }

    private void startServiceDiscovery() {
        WifiP2pDnsSdServiceRequest request = WifiP2pDnsSdServiceRequest.newInstance(WifiAccessPoint.SERVICE_REG_TYPE);
        final Handler handler = new Handler();
        mWifiP2pManager.addServiceRequest(mChannel, request, new WifiP2pManager.ActionListener() {
            public void onSuccess() {
                handler.postDelayed(new Runnable() {
                    //There are supposedly a possible race-condition bug with the service discovery
                    // thus to avoid it, we are delaying the service discovery start here
                    public void run() {
                        mWifiP2pManager.discoverServices(mChannel, new WifiP2pManager.ActionListener() {
                            public void onSuccess() {
                                myServiceState = ServiceState.DiscoverService;
                            }

                            public void onFailure(int reason) {
                            }
                        });
                    }
                }, 1000);
            }

            public void onFailure(int reason) {
            }
        });
    }

    private class ServiceSearchReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                if (myServiceState != ServiceState.DiscoverService) {
                    mWifiP2pManager.requestPeers(mChannel, mPeerListener);
                }
            } else if (WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION.equals(action)) {
                int state = intent.getIntExtra(WifiP2pManager.EXTRA_DISCOVERY_STATE, WifiP2pManager.WIFI_P2P_DISCOVERY_STOPPED);
                if (state == WifiP2pManager.WIFI_P2P_DISCOVERY_STOPPED) {
                    startPeerDiscovery();
                }
            } else if (WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
                if (networkInfo.isConnected()) {
                    Log.d(TAG, "Connected");
                    startPeerDiscovery();
                } else {
                    Log.d(TAG, "Dis-Connected");
                    startPeerDiscovery();
                }
            }
        }
    }
}
