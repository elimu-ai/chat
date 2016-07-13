package chat.literacyapp.org.chat.main;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import chat.literacyapp.org.chat.R;
import chat.literacyapp.org.chat.bluetooth.ChatServerListFragment;
import chat.literacyapp.org.chat.bluetooth.Connection;
import chat.literacyapp.org.chat.ui.widget.DividerItemDecoration;

import static chat.literacyapp.org.chat.bluetooth.Connection.*;

/**
 * Created by oscarmakala on 05/07/2016.
 */
public class MainFragment extends Fragment implements MainContract.View {
    private static final int REQUEST_ENABLE_BT = 5;
    private MainPresenter mActionsListener;
    private BTDevicesAdapter mBTDevicesAdapter;
    private BluetoothAdapter mBlueToothAdapter;
    private View mProgressBar;
    // 0 = server, 1 = client
    private int mConnectionType;
    private Connection mConnection;

    public static MainFragment newInstance(int connectionType) {
        Bundle bundle = new Bundle();
        bundle.putInt("connectionType", connectionType);
        MainFragment fragment = new MainFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    private OnMaxConnectionsReachedDelegate onMaxConnectionsReachedDelegate = new OnMaxConnectionsReachedDelegate() {
        @Override
        public void OnMaxConnectionsReached() {

        }
    };

    private OnConnectionServiceReadyDelegate onConnectionServiceReadyDelegate = new OnConnectionServiceReadyDelegate() {
        @Override
        public void OnConnectionServiceReady() {
            if (mConnectionType == 0) {
                mConnection.startServer(1, onMaxConnectionsReachedDelegate);
            } else {
                //show list of available chat rooms.
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("images_dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);
                boolean showDelete = true;
                // Create and show the dialog.
                ChatServerListFragment newFragment = ChatServerListFragment.newInstance();
                newFragment.show(ft, "images_dialog");
            }
        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mConnectionType = getArguments().getInt("connectionType");
        }
        mActionsListener = new MainPresenter(this);
        mConnection = new Connection(getActivity(), onConnectionServiceReadyDelegate);

    }


    @Override
    public void onDestroy() {
        if (mConnection != null) {
            mConnection.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onPause() {
        super.onPause();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);

        return root;
    }

    private boolean mFoundDevice = false;

    @Override
    public void setProgressIndicator(boolean active) {
        if (active) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public void showChatUi(BluetoothDevice device) {

    }
}
