package org.literacyapp.chat.main;

import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.util.Log;

import org.literacy.wifip2p.ChatConnection;
import org.literacy.wifip2p.NotificationCenter;
import org.literacy.wifip2p.WifiAccessPoint;
import org.literacy.wifip2p.WifiConnection;
import org.literacy.wifip2p.WifiServiceSearcher;
import org.literacyapp.chat.R;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends AppCompatActivity
        implements
        MainFragment.ChatInputDelegate, NotificationCenter.NotificationCenterDelegate {

    private static final String TAG = MainActivity.class.getSimpleName();

    //will use this later to make a two pane layout of list of devices, and chat for tablet
    private boolean mTwoPane;
    private int connectionType;
    private WifiAccessPoint mWifiAccessPoint;

    private MainFragment fragment;


    private String mInetAddress;
    private WifiConnection mWifiConnection;
    private WifiServiceSearcher mWifiServiceSearcher;
    private ChatConnection mChatConnection;
    private static final int SERVICE_PORT_INSTANCE = 4323;
    private static final int CLIENT_PORT_INSTANCE = 4323;

    private Handler mUpdateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String chatLine = msg.getData().getString("msg");
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.LOGGER, TAG, chatLine);
            //addChatLine(chatLine);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mWifiAccessPoint = new WifiAccessPoint(this);
        mWifiAccessPoint.start();


        mWifiServiceSearcher = new WifiServiceSearcher(this);
        mWifiServiceSearcher.start();

        mChatConnection = new ChatConnection(mUpdateHandler, SERVICE_PORT_INSTANCE);
        if (null == savedInstanceState) {
            fragment = MainFragment.newInstance(connectionType);
            initFragment(fragment);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWifiAccessPoint != null) {
            mWifiAccessPoint.start();
        }
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.WIFICON_SERVERADDRESS);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.WIFI_CONNECTIONSTATE);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.ACCESS_POINT_INSTANCE_INFO);


    }

    @Override
    protected void onPause() {
        if (mWifiAccessPoint != null) {
            mWifiAccessPoint.stop();
        }
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.WIFICON_SERVERADDRESS);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.WIFI_CONNECTIONSTATE);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.ACCESS_POINT_INSTANCE_INFO);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mWifiConnection != null) {
            mWifiConnection.stop();
            mWifiConnection = null;
        }
        if (mWifiAccessPoint != null) {
            mWifiAccessPoint.stop();
            mWifiAccessPoint = null;
        }
        if (mWifiServiceSearcher != null) {
            mWifiServiceSearcher.stop();
            mWifiServiceSearcher = null;
        }
    }

    private void initFragment(Fragment fragment) {
        // Add the ChatesFragment to the layout
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.contentFrame, fragment);
        transaction.commit();
    }


    @Override
    public void onSendMessage(String message) {
        if (message != null) {
            if (!message.isEmpty()) {
                mChatConnection.sendMessage(message);
            }
        }
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.ACCESS_POINT_INSTANCE_INFO) {
            String instanceName = (String) args[0];
            String[] instanceNameSplit = instanceName.split(":");
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.LOGGER, TAG, "found SSID:" + instanceNameSplit[1] + ", pwd:" + instanceNameSplit[2] + "IP:" + instanceNameSplit[3]);
            if (mWifiConnection == null) {
                if (mWifiAccessPoint != null) {
                    mWifiAccessPoint.stop();
                    mWifiAccessPoint = null;
                }
                if (mWifiServiceSearcher != null) {
                    mWifiServiceSearcher.stop();
                    mWifiServiceSearcher = null;
                }

                final String networkSSID = instanceNameSplit[1];
                final String networkPassword = instanceNameSplit[2];
                final String networkIPAddress = instanceNameSplit[3];
                mWifiConnection = new WifiConnection(MainActivity.this, networkSSID, networkPassword, networkIPAddress);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.LOGGER, TAG, "found access  point");
            }
        } else if (id == NotificationCenter.WIFICON_SERVERADDRESS) {
            WifiInfo wifiInfo = (WifiInfo) args[0];
            Log.d(TAG, "ip address: " + wifiInfo.getIpAddress());
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.LOGGER, TAG, "IP:" + Formatter.formatIpAddress(wifiInfo.getIpAddress()));
            if (mWifiConnection != null) {//mClientSocket == null &&
                String ipAddress = mWifiConnection.getIpAddress();
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.LOGGER, TAG, "Starting client socket connection to :" + ipAddress);
//                mClientSocket = new ClientSocketConnection(mUpdateHandler, ipAddress, CLIENT_PORT_INSTANCE);
//                mClientSocket.start();
                mChatConnection.connectToServer(ipAddress, CLIENT_PORT_INSTANCE);
            }
        } else if (id == NotificationCenter.WIFI_CONNECTIONSTATE) {
            int status = (int) args[0];
            String mConnectionStatus = "";
            switch (status) {
                case WifiConnection.ConectionStateNONE:
                    mConnectionStatus = "NONE";
                    break;
                case WifiConnection.ConectionStatePreConnecting:
                    mConnectionStatus = "PreConnecting";
                    break;
                case WifiConnection.ConectionStateConnecting:
                    mConnectionStatus = "Connecting";
                    break;
                case WifiConnection.ConectionStateConnected:
                    mConnectionStatus = "Connected";
                    break;
                case WifiConnection.ConectionStateDisconnected: {
                    mConnectionStatus = "Disconnected";
                    if (mWifiConnection != null) {
                        mWifiConnection.stop();
                        mWifiConnection = null;
                        // should stop etc.
                        mWifiConnection = null;
                    }
                    // make sure services are re-started
                    if (mWifiAccessPoint != null) {
                        mWifiAccessPoint.stop();
                        mWifiAccessPoint = null;
                    }
                    mWifiAccessPoint = new WifiAccessPoint(this);
                    mWifiAccessPoint.start();

                    if (mWifiServiceSearcher != null) {
                        mWifiServiceSearcher.stop();
                        mWifiServiceSearcher = null;
                    }

                    mWifiServiceSearcher = new WifiServiceSearcher(this);
                    mWifiServiceSearcher.start();
                    break;
                }
            }
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.LOGGER, TAG, "Connection status:" + mConnectionStatus);
        }
    }

}
