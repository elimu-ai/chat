package org.literacyapp.chat;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import org.literacyapp.chat.model.TextMessage;
import org.literacyapp.chat.util.DeviceInfoHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChatActivity extends Activity {

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

        textMessages = new ArrayList<>();

        mListPreviousMessages = (ListView) findViewById(R.id.listPreviousMessages);
        mTextMessage = (EditText) findViewById(R.id.textMessage);
        mButtonSend = (ImageButton) findViewById(R.id.buttonSend);
    }

    @Override
    protected void onStart() {
        Log.i(getClass().getName(), "onStart");
        super.onStart();

        arrayAdapter = new MessageListArrayAdapter(getApplicationContext(), textMessages);
        mListPreviousMessages.setAdapter(arrayAdapter);


        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(getClass().getName(), "mButtonSend onClick");

                String text = mTextMessage.getText().toString();
                Log.i(getClass().getName(), "text: " + text);

                TextMessage textMessage = new TextMessage();
                textMessage.setDeviceId(DeviceInfoHelper.getDeviceId(getApplicationContext()));
                textMessage.setTimeSent(Calendar.getInstance());
                textMessage.setText(text);

                // Store in database
                // TODO

                mTextMessage.setText("");

                addToMessageList(textMessage);
            }
        });

        // TODO: load previous messages
    }

    private void addToMessageList(TextMessage textMessage) {
        Log.i(getClass().getName(), "addToMessageList");

        textMessages.add(textMessage);
        arrayAdapter.notifyDataSetChanged();
        mListPreviousMessages.smoothScrollToPosition(textMessages.size());
    }
}
