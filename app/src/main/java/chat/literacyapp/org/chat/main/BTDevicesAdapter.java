package chat.literacyapp.org.chat.main;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import chat.literacyapp.org.chat.R;

/**
 * Created by oscarmakala on 05/07/2016.
 */
public class BTDevicesAdapter extends RecyclerView.Adapter<BTDevicesAdapter.BTDevicesAdapterViewHolder> {
    private final View mEmptyView;
    private final BTDevicesAdapterOnClickHandler mClickHandler;
    List<BluetoothDevice> mItems = new ArrayList<>();

    public BTDevicesAdapter(View emptyView, BTDevicesAdapterOnClickHandler clickHandler) {
        mEmptyView = emptyView;
        mClickHandler = clickHandler;
    }

    @Override
    public BTDevicesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_device, parent, false);
        view.setFocusable(true);
        return new BTDevicesAdapterViewHolder((TextView) view);
    }

    @Override
    public void onBindViewHolder(BTDevicesAdapterViewHolder holder, int position) {
        BluetoothDevice device = mItems.get(position);
        holder.mTextView.setText(device.getName());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void add(BluetoothDevice device) {
        if (!mItems.contains(device)) {
            mItems.add(device);
            notifyDataSetChanged();
        }
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }


    public static interface BTDevicesAdapterOnClickHandler {
        void onClick(int postition, BTDevicesAdapterViewHolder vh);
    }


    public class BTDevicesAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        private final TextView mTextView;


        public BTDevicesAdapterViewHolder(TextView itemView) {
            super(itemView);
            mTextView = itemView;
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(adapterPosition, this);
        }

        public BluetoothDevice getItemByPosition(int position) {
            return mItems.get(position);
        }
    }


}
