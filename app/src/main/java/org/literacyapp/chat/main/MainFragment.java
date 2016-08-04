package org.literacyapp.chat.main;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.literacyapp.wifip2p.NotificationCenter;
import org.literacyapp.chat.R;

/**
 * Created by oscarmakala on 05/07/2016.
 */
public class MainFragment extends Fragment implements
        MainContract.View,
        NotificationCenter.NotificationCenterDelegate {


    private RecyclerView mChatList;
    private ChatAdapter chatAdapter;
    private EditText mChatMessage;
    private TextView mTextView;

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (NotificationCenter.LOGGER == id) {
            final String who = (String) args[0];
            final String line = (String) args[1];
            //get errors from connection thread.must update on main u
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTextView.append(who + ":" + line + "\n");
                }
            });

        }
    }


    public interface ChatInputDelegate {
        public void onSendMessage(String message);
    }


    public static MainFragment newInstance(int connectionType) {
        Bundle bundle = new Bundle();
        bundle.putInt("connectionType", connectionType);
        MainFragment fragment = new MainFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onResume() {
        super.onResume();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.LOGGER);
    }


    @Override
    public void onPause() {
        super.onPause();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.LOGGER);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        mTextView = (TextView) root.findViewById(R.id.infoText);
//        mChatList = (RecyclerView) root.findViewById(R.id.chatMessageView);
//        chatAdapter = new ChatAdapter();
//        mChatList.setLayoutManager(new LinearLayoutManager(getActivity()));
//        // use this setting to improve performance if you know that changes
//        // in content do not change the layout size of the RecyclerView
//        mChatList.setHasFixedSize(true);
//        mChatList.setAdapter(chatAdapter);

        mChatMessage = (EditText) root.findViewById(R.id.chatInputView);
        root.findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ChatInputDelegate) getActivity()).onSendMessage(mChatMessage.getText().toString());
                mChatMessage.setText("");
            }
        });
        return root;
    }

    private boolean mFoundDevice = false;

    @Override
    public void setProgressIndicator(boolean active) {


    }


    @Override
    public void showChatUi(BluetoothDevice device) {

    }
}
