package org.literacy.wifip2p;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.util.Log;

import org.literacy.wifip2p.broadcast.WiFiDirectBroadcastReceiver;
import org.literacy.wifip2p.model.WiFiP2pService;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by oscarmakala on 22/07/2016.
 * Helper for wifi service discovery
 */
public class WsdHelper {

    private static final String AVAILABLE = "available";
    private static final String TAG = WsdHelper.class.getSimpleName();
    private final Context mContext;
    private final WifiP2pManager mWsdManager;
    private final WifiP2pManager.Channel mChannel;

    public static final String SERVICE_INSTANCE = "literacyappchat";
    public static final String SERVICE_REG_TYPE = "_literacy._tcp";
    private WifiP2pManager.ActionListener mActionListener;
    private WifiP2pManager.DnsSdServiceResponseListener mResolveListener;
    private WifiP2pManager.DnsSdTxtRecordListener mDiscoveryListener;
    private WifiP2pDnsSdServiceRequest mServiceRequest;
    private WiFiDirectBroadcastReceiver mReceiver;

    public WsdHelper(Context context) {
        mContext = context;
        mWsdManager = (WifiP2pManager) mContext.getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mWsdManager.initialize(mContext, mContext.getMainLooper(), null);
    }

    /**
     * If you're providing a local service, you need to register it for service discovery.
     * Once your local service is registered,
     * the framework automatically responds to service discovery requests from peers.
     *
     * @param localPort
     */
    public void registerService(int localPort) {
        Map<String, String> record = new HashMap<>();
        record.put(AVAILABLE, "visible");
        record.put("listenport", String.valueOf(localPort));

        WifiP2pDnsSdServiceInfo serviceInfo = WifiP2pDnsSdServiceInfo.newInstance(SERVICE_INSTANCE, SERVICE_REG_TYPE, record);
        mWsdManager.addLocalService(mChannel, serviceInfo, mActionListener);


    }

    public void initializeWsd() {
        initializeResolveListener();
        initializeDiscoveryListener();
        initializeActionListener();


    }


    private void initializeResolveListener() {
        mResolveListener = new WifiP2pManager.DnsSdServiceResponseListener() {
            @Override
            public void onDnsSdServiceAvailable(String instanceName, String registrationType, WifiP2pDevice wifiP2pDevice) {
                Log.d(TAG, "a service has been discoverd");
                if (SERVICE_INSTANCE.equalsIgnoreCase(instanceName)) {
                    WiFiP2pService service = new WiFiP2pService(instanceName, registrationType, wifiP2pDevice);
                    ChatHandler.getInstance().addDevice(service);
                    Log.d(TAG, "new device found " + instanceName);
                    atemptConnect(service);
                } else {
                    Log.d(TAG, "unknown service just ignore");
                }
            }
        };
    }

    private void atemptConnect(WiFiP2pService service) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = service.getDevice().deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        if (mServiceRequest != null) {
            mWsdManager.removeServiceRequest(mChannel, mServiceRequest, mActionListener);
            mWsdManager.connect(mChannel, config, mActionListener);
        }
    }

    /**
     * Register listenrs for DNS-SD services.These are callbacks invoked
     * by the system when a service is actually discovered.
     */
    private void initializeDiscoveryListener() {
        mDiscoveryListener = new WifiP2pManager.DnsSdTxtRecordListener() {
            @Override
            public void onDnsSdTxtRecordAvailable(String fullDomain, Map<String, String> record, WifiP2pDevice device) {
                Log.d(TAG, device.deviceName + " is " + record.get(AVAILABLE) + " on port: " + record.get("listenport"));
                ChatHandler.getInstance().setDevicePort(device, record.get("listenport"));
            }
        };
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

    public void discoverServices(Activity activity, IntentFilter intentFilter) {
        mWsdManager.setDnsSdResponseListeners(mChannel, mResolveListener, mDiscoveryListener);

        mServiceRequest = WifiP2pDnsSdServiceRequest.newInstance();

        mWsdManager.addServiceRequest(mChannel, mServiceRequest, mActionListener);
        mWsdManager.discoverServices(mChannel, mActionListener);

        //registers broadcast
        mReceiver = new WiFiDirectBroadcastReceiver(mWsdManager, mChannel, activity);
        mContext.registerReceiver(mReceiver, intentFilter);

    }

    public void stopDiscovery() {
        if (mWsdManager != null && mChannel != null) {
            mWsdManager.removeGroup(mChannel, mActionListener);
        }
        if (mReceiver != null) {
            mContext.unregisterReceiver(mReceiver);
        }

    }
}
