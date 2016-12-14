package org.literacyapp.chat;

import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import org.literacyapp.chat.receiver.StudentUpdateReceiver;
import org.literacyapp.chat.util.DeviceInfoHelper;

import java.util.Calendar;
import java.util.List;

public class ChatActivity extends Activity {

    public static final int PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 0;

    private MessageDao messageDao;

    private List<Message> messages;

    private ArrayAdapter arrayAdapter;

    private ListView mListPreviousMessages;

    private EditText messageText;

    private ImageButton mButtonSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(getClass().getName(), "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat);

        messageDao = ((ChatApplication) getApplication()).getDaoSession().getMessageDao();

        mListPreviousMessages = (ListView) findViewById(R.id.listPreviousMessages);
        messageText = (EditText) findViewById(R.id.message);
        mButtonSend = (ImageButton) findViewById(R.id.buttonSend);
    }

    @Override
    protected void onStart() {
        Log.i(getClass().getName(), "onStart");
        super.onStart();

        // Ask for permissions
        int permissionCheckWriteExternalStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheckWriteExternalStorage != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
            return;
        }

        // Load messages sent within the last 24 hours
        Calendar calendar24HoursAgo = Calendar.getInstance();
        calendar24HoursAgo.add(Calendar.HOUR_OF_DAY, -24);
        messages = messageDao.queryBuilder()
                .where(MessageDao.Properties.TimeSent.gt(calendar24HoursAgo.getTimeInMillis()))
                .list();
        Log.i(getClass().getName(), "messages.size(): " + messages.size());

        arrayAdapter = new MessageListArrayAdapter(getApplicationContext(), messages);
        mListPreviousMessages.setAdapter(arrayAdapter);

        messageText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.i(getClass().getName(), "afterTextChanged");

                Log.i(getClass().getName(), "editable: " + editable);

                if (!TextUtils.isEmpty(editable)) {
                    if (!mButtonSend.isEnabled()) {
                        mButtonSend.setEnabled(true);
                        mButtonSend.setImageDrawable(getDrawable(R.drawable.ic_send_white_24dp));

//                        // Animate button to indicate that it can be pressed
//                        final long duration = 300;
//
//                        final ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(mButtonSend, View.SCALE_X, 1f, 1.2f, 1f);
//                        scaleXAnimator.setDuration(duration);
//                        scaleXAnimator.setRepeatCount(1);
//
//                        final ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(mButtonSend, View.SCALE_Y, 1f, 1.2f, 1f);
//                        scaleYAnimator.setDuration(duration);
//                        scaleYAnimator.setRepeatCount(1);
//
//                        scaleXAnimator.start();
//                        scaleYAnimator.start();
//
//                        final AnimatorSet animatorSet = new AnimatorSet();
//                        animatorSet.play(scaleXAnimator).with(scaleYAnimator);
//                        animatorSet.start();
                    }
                } else {
                    mButtonSend.setEnabled(false);
                    mButtonSend.setImageDrawable(getDrawable(R.drawable.ic_send_grey_24dp));
                }
            }
        });

        // Default to grey button
        mButtonSend.setEnabled(false);
        mButtonSend.setImageDrawable(getDrawable(R.drawable.ic_send_grey_24dp));

        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(getClass().getName(), "mButtonSend onClick");

                String text = messageText.getText().toString();
                Log.i(getClass().getName(), "text: " + text);

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                // Store in database
                Message message = new Message();
                message.setDeviceId(DeviceInfoHelper.getDeviceId(getApplicationContext()));
                String studentId = sharedPreferences.getString(StudentUpdateReceiver.PREF_STUDENT_ID, null);
                if (!TextUtils.isEmpty(studentId)) {
                    message.setStudentId(studentId);
                }
                String studentAvatar = sharedPreferences.getString(StudentUpdateReceiver.PREF_STUDENT_AVATAR, null);
                if (!TextUtils.isEmpty(studentAvatar)) {
                    message.setStudentAvatar(studentAvatar);
                }
                message.setTimeSent(Calendar.getInstance());
                message.setText(text);
                messageDao.insert(message);

                // Add to UI
                addToMessageListAndRefresh(message);

                // Reset input field
                messageText.setText("");
            }
        });
    }

    private void addToMessageListAndRefresh(Message message) {
        Log.i(getClass().getName(), "addToMessageListAndRefresh");
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
