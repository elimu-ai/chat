package chat.literacyapp.org.chat.bluetooth;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import java.util.ArrayList;

import chat.literacyapp.org.chat.R;
import chat.literacyapp.org.chat.main.BTDevicesAdapter;
import chat.literacyapp.org.chat.ui.widget.DividerItemDecoration;
import chat.literacyapp.org.chat.utils.BluetoothHelper;

/**
 * Created by oscarmakala on 13/07/2016.
 * Dialog fragment that shows the list of chat servers to connect to.
 */

public class ChatServerListFragment extends DialogFragment {


    private Dialog dialog;
    private ChatServerListDelegate chatServerListDelegate;

    public interface ChatServerListDelegate {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(BluetoothDevice device, BTDevicesAdapter.BTDevicesAdapterViewHolder vh, Dialog dialog);
    }

    private BTDevicesAdapter mBluetoohListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mBluetoohListAdapter.add(device);
            }
        }
    };



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            chatServerListDelegate = (ChatServerListDelegate) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBluetoothAdapter = BluetoothHelper.getBluetoothAdapter(getActivity());


        if (Build.VERSION.SDK_INT >= 23) {

        } else {
            mBluetoothAdapter.startDiscovery();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dailog_chat_servers_list, container, false);

        RecyclerView mRecyclerView = (RecyclerView) v.findViewById(R.id.devices_list);
        View emptyView = v.findViewById(R.id.empty_view);


        mBluetoohListAdapter = new BTDevicesAdapter(emptyView, new BTDevicesAdapter.BTDevicesAdapterOnClickHandler() {
            @Override
            public void onClick(int position, BTDevicesAdapter.BTDevicesAdapterViewHolder vh) {
                // Cancel discovery because it's costly and we're about to connect
                if (Build.VERSION.SDK_INT >= 23) {
                    //mBluetoothAdapter.getBluetoothLeScanner().startScan(mLeScanCallback);
                } else {
                    mBluetoothAdapter.cancelDiscovery();
                }
                BluetoothDevice device = vh.getItemByPosition(position);
                chatServerListDelegate.onItemSelected(device, vh, dialog);
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(RecyclerView.VERTICAL);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mBluetoohListAdapter);
        return v;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }


    @Override
    public void onStart() {
        super.onStart();
        dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getActivity().registerReceiver(mReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }
    }


    public static ChatServerListFragment newInstance() {
        return new ChatServerListFragment();
    }
}
