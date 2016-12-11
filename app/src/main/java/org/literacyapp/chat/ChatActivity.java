package org.literacyapp.chat;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import org.literacyapp.chat.dao.MessageDao;
import org.literacyapp.chat.model.Message;
import org.literacyapp.chat.util.DeviceInfoHelper;

import java.util.Calendar;
import java.util.List;

public class ChatActivity extends Activity {

    private MessageDao messageDao;

    private List<Message> messages;

    private ArrayAdapter arrayAdapter;

    private ListView mListPreviousMessages;

    private EditText message;

    private ImageButton mButtonSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(getClass().getName(), "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat);

        messageDao = ((ChatApplication) getApplication()).getDaoSession().getMessageDao();

        mListPreviousMessages = (ListView) findViewById(R.id.listPreviousMessages);
        message = (EditText) findViewById(R.id.message);
        mButtonSend = (ImageButton) findViewById(R.id.buttonSend);

    }

    @Override
    protected void onStart() {
        Log.i(getClass().getName(), "onStart");
        super.onStart();

        // Load messages sent within the last 24 hours
        Calendar calendar24HoursAgo = Calendar.getInstance();
        calendar24HoursAgo.add(Calendar.HOUR_OF_DAY, -24);
        messages = messageDao.queryBuilder()
                .where(MessageDao.Properties.TimeSent.gt(calendar24HoursAgo.getTimeInMillis()))
                .list();
        Log.i(getClass().getName(), "messages.size(): " + messages.size());

        arrayAdapter = new MessageListArrayAdapter(getApplicationContext(), messages);
        mListPreviousMessages.setAdapter(arrayAdapter);

        message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(!TextUtils.isEmpty(editable)){
                    mButtonSend.setColorFilter(Color.rgb(0,150,136));
                } else {
                    mButtonSend.setColorFilter(Color.rgb(158,158,158));
                }

            }
        });

        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(getClass().getName(), "mButtonSend onClick");

                String text = message.getText().toString();
                Log.i(getClass().getName(), "text: " + text);

                // Check if EditText is empty
                if(!TextUtils.isEmpty(text)){
                    Message message = new Message();
                    message.setDeviceId(DeviceInfoHelper.getDeviceId(getApplicationContext()));
                    message.setTimeSent(Calendar.getInstance());
                    message.setText(text);

                    // Store in database
                    messageDao.insert(message);

                    // Add to UI
                    addToMessageListAndRefresh(message);

                    // Reset input field
                    ChatActivity.this.message.setText("");

                } else {
                    mButtonSend.setVisibility(View.GONE);
                }
                mButtonSend.setVisibility(View.VISIBLE);

            }
        });
    }

    private void addToMessageListAndRefresh(Message message) {
        messages.add(message);
        refreshMessageList();
    }

    private void refreshMessageList() {
        Log.i(getClass().getName(), "refreshMessageList");

        arrayAdapter.notifyDataSetChanged();
        mListPreviousMessages.smoothScrollToPosition(mListPreviousMessages.getCount());
        // Fix problem with scrolling when keyboard is present
        mListPreviousMessages.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i(getClass().getName(), "mListPreviousMessages.postDelayed");
                mListPreviousMessages.setSelection(mListPreviousMessages.getCount());
            }
        }, 100);
    }
}
