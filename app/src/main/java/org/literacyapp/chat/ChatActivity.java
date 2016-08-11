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

import org.literacyapp.chat.dao.TextMessageDao;
import org.literacyapp.chat.model.TextMessage;
import org.literacyapp.chat.util.DeviceInfoHelper;

import java.util.Calendar;
import java.util.List;

public class ChatActivity extends Activity {

    private TextMessageDao textMessageDao;

    private List<TextMessage> textMessages;

    private ArrayAdapter arrayAdapter;

    private ListView mListPreviousMessages;

    private EditText mTextMessage;

    private ImageButton mButtonSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(getClass().getName(), "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat);

        textMessageDao = ((ChatApplication) getApplication()).getDaoSession().getTextMessageDao();

        mListPreviousMessages = (ListView) findViewById(R.id.listPreviousMessages);
        mTextMessage = (EditText) findViewById(R.id.textMessage);
        mButtonSend = (ImageButton) findViewById(R.id.buttonSend);

    }

    @Override
    protected void onStart() {
        Log.i(getClass().getName(), "onStart");
        super.onStart();

        // Load messages sent within the last 24 hours
        Calendar calendar24HoursAgo = Calendar.getInstance();
        calendar24HoursAgo.add(Calendar.HOUR_OF_DAY, -24);
        textMessages = textMessageDao.queryBuilder()
                .where(TextMessageDao.Properties.TimeSent.gt(calendar24HoursAgo.getTimeInMillis()))
                .list();
        Log.i(getClass().getName(), "textMessages.size(): " + textMessages.size());

        arrayAdapter = new MessageListArrayAdapter(getApplicationContext(), textMessages);
        mListPreviousMessages.setAdapter(arrayAdapter);

        mTextMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(editable.toString().trim().length() > 0){
                    mButtonSend.setColorFilter(Color.rgb(0,150,136));
                } else {
                    mButtonSend.setColorFilter(Color.rgb(0,0,0));
                }

            }
        });

        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(getClass().getName(), "mButtonSend onClick");

                String text = mTextMessage.getText().toString();
                Log.i(getClass().getName(), "text: " + text);

                // Check if EditText is empty
                if(!TextUtils.isEmpty(text)){
                    TextMessage textMessage = new TextMessage();
                    textMessage.setDeviceId(DeviceInfoHelper.getDeviceId(getApplicationContext()));
                    textMessage.setTimeSent(Calendar.getInstance());
                    textMessage.setText(text);

                    // Store in database
                    textMessageDao.insert(textMessage);

                    // Add to UI
                    addToMessageListAndRefresh(textMessage);

                    // Reset input field
                    mTextMessage.setText("");

                } else {
                    mButtonSend.setVisibility(View.GONE);
                }
                mButtonSend.setVisibility(View.VISIBLE);

            }
        });
    }

    private void addToMessageListAndRefresh(TextMessage textMessage) {
        textMessages.add(textMessage);
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
