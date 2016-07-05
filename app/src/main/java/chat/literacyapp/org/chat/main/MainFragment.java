package chat.literacyapp.org.chat.main;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import chat.literacyapp.org.chat.R;
import chat.literacyapp.org.chat.session.ChatSessionActivity;
import chat.literacyapp.org.chat.ui.widget.DividerItemDecoration;

/**
 * Created by oscarmakala on 05/07/2016.
 */
public class MainFragment extends Fragment implements MainContract.View {
    private static final int REQUEST_ENABLE_BT = 5;
    private MainPresenter mActionsListener;
    private BTDevicesAdapter mBTDevicesAdapter;
    private BluetoothAdapter mBlueToothAdapter;
    private View mProgressBar;

    public static MainFragment newInstance() {
        return new MainFragment();
    }


    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(BluetoothDevice device, BTDevicesAdapter.BTDevicesAdapterViewHolder vh);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mListAdapter = new NotesAdapter(new ArrayList<Note>(0), mItemListener);
        mActionsListener = new MainPresenter(this);


        //verify that bluetooth is supported on the device, and ensure it is enabled.
        mBlueToothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBlueToothAdapter == null) {
            //device does not support bluetooth
            Toast.makeText(getActivity(), "Bluetooth is not available", Toast.LENGTH_LONG).show();
            getActivity().finish();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        //if bluetooth is not no, request that it be enabled.
        if (!mBlueToothAdapter.isEnabled()) {
            Intent enableBlueToothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBlueToothIntent, REQUEST_ENABLE_BT);
        } else {
            showNearbyDevices();
        }

    }

    private void showNearbyDevices() {
        setProgressIndicator(true);
        mBlueToothAdapter.startDiscovery();
    }


    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mBroadCastReceiver);
    }


    @Override
    public void onResume() {
        super.onResume();
        //Register broadcast receiver to listen for nearby bluetooth devices
        getActivity().registerReceiver(mBroadCastReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (resultCode) {
            case REQUEST_ENABLE_BT: {
                //When the request to enable bluetooth retunrs
                showNearbyDevices();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        //get a reference to the RecylerView, and attach this adapter to it
        RecyclerView mRecyclerView = (RecyclerView) root.findViewById(R.id.devices_list);

        mProgressBar = root.findViewById(R.id.progress_bar);
        //set the layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        View emptyView = root.findViewById(R.id.empty_view);

        mBTDevicesAdapter = new BTDevicesAdapter(emptyView, new BTDevicesAdapter.BTDevicesAdapterOnClickHandler() {
            @Override
            public void onClick(int position, BTDevicesAdapter.BTDevicesAdapterViewHolder vh) {
                // Cancel discovery because it's costly and we're about to connect
                mBlueToothAdapter.cancelDiscovery();
                BluetoothDevice device = vh.getItemByPosition(position);
                ((Callback) getActivity()).onItemSelected(device, vh);
            }
        });
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        //add line between line items
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setAdapter(mBTDevicesAdapter);
        return root;
    }

    private boolean mFoundDevice = false;
    private BroadcastReceiver mBroadCastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                if (mFoundDevice == false) {
                    mFoundDevice = true;
                    setProgressIndicator(false);
                }
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mBTDevicesAdapter.add(device);
            }
        }
    };

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
