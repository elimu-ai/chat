package org.literacyapp.chat.main;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import org.literacy.wifip2p.ChatConnection;
import org.literacy.wifip2p.ChatHandler;
import org.literacy.wifip2p.ClientSocketConnection;
import org.literacy.wifip2p.ConnectionManager;
import org.literacy.wifip2p.GroupOwnerSocketConnection;
import org.literacy.wifip2p.WsdHelper;
import org.literacy.wifip2p.model.WiFiP2pService;
import org.literacyapp.chat.Constants;
import org.literacyapp.chat.R;

import java.io.IOException;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends AppCompatActivity implements MainFragment.ChatInputDelegate, WifiP2pManager.ConnectionInfoListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    //will use this later to make a two pane layout of list of devices, and chat for tablet
    private boolean mTwoPane;
    private int connectionType;
    private WsdHelper mWsdHelper;
    private final IntentFilter intentFilter = new IntentFilter();

    public static final int MESSAGE_READ = 0x400 + 1;
    public static final int MY_HANDLE = 0x400 + 2;
    private MainFragment fragment;
    private Handler mUpdateHandler;
    private ChatConnection mConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat);

        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mUpdateHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String chatLine = msg.getData().getString("msg");
                //addChatLine(chatLine);
            }
        };

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        mConnection = new ChatConnection(mUpdateHandler);
        mWsdHelper = new WsdHelper(this);
        mWsdHelper.initializeWsd();

        if (null == savedInstanceState) {
            fragment = MainFragment.newInstance(connectionType);
            initFragment(fragment);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWsdHelper != null) {
            mWsdHelper.registerService(mConnection.getLocalPort());
            mWsdHelper.discoverServices(this, intentFilter);
        }

    }

    @Override
    protected void onPause() {
        if (mWsdHelper != null) {
            mWsdHelper.stopDiscovery();
        }
        super.onPause();
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
                mConnection.sendMessage(message);
            }
        }
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo p2pInfo) {
        if (p2pInfo.isGroupOwner) {
            Log.d(TAG, "Connected as group owner");
        }

        mConnection.connectToServer(p2pInfo.groupOwnerAddress, 4545);


    }
}
