package ai.elimu.chat;

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

//        ContentProvider.initializeDb(this);
//        List<Letter> letters = ContentProvider.getAvailableLetters();
//        Log.i(getClass().getName(), "letters: " + letters);

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

                boolean showMessageFromAkili = Math.random() > 0.5;
                if (showMessageFromAkili) {
                    mButtonSend.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Simulate message from AI tutor

                            // Akili
                            Message message = new Message();
                            message.setStudentId("00000000aaaaaaaa_1");
                            message.setTimeSent(Calendar.getInstance());
                            message.setText(getRandomEmoji());
                            addToMessageListAndRefresh(message);
                        }
                    }, 2000 + (int) (Math.random() * 8000));
                }

                boolean showMessageFromPenguin = Math.random() > 0.5;
                if (showMessageFromPenguin) {
                    mButtonSend.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Penguin
                            Message messagePenguin = new Message();
                            messagePenguin.setStudentId("00000000aaaaaaaa_2");
                            messagePenguin.setTimeSent(Calendar.getInstance());
                            messagePenguin.setText(getRandomEmoji());
                            addToMessageListAndRefresh(messagePenguin);
                        }
                    }, 2000 + (int) (Math.random() * 8000));
                }
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

    private String getRandomEmoji() {
        int[] unicodes = new int[] {
                // Emoticons
                0x1F601,
                0x1F602,
                0x1F603,
                0x1F604,
                0x1F605,
                0x1F606,
                0x1F609,
                0x1F60A,
                0x1F60B,
                0x1F60C,
                0x1F60D,
                0x1F60F,
                0x1F612,
                0x1F613,
                0x1F614,
                0x1F616,
                0x1F618,
                0x1F61A,
                0x1F61C,
                0x1F61D,
                0x1F61E,
                0x1F620,
                0x1F621,
                0x1F622,
                0x1F623,
                0x1F624,
                0x1F625,
                0x1F628,
                0x1F629,
                0x1F62A,
                0x1F62B,
                0x1F62D,
                0x1F630,
                0x1F631,
                0x1F632,
                0x1F633,
                0x1F635,
                0x1F637,
                0x1F638,
                0x1F639,
                0x1F63A,
                0x1F63B,
                0x1F63C,
                0x1F63D,
                0x1F63E,
                0x1F63F,
                0x1F640,
                0x1F645,
                0x1F646,
                0x1F647,
                0x1F648,
                0x1F649,
                0x1F64A,
                0x1F64B,
                0x1F64C,
                0x1F64D,
                0x1F64E,
                0x1F64F,

                // Uncategorized
                0x1F40C,
                0x1F40D,
                0x1F40E,
                0x1F411,
                0x1F412,
                0x1F414,
                0x1F418,
                0x1F419,
                0x1F41A,
                0x1F41B,
                0x1F41C,
                0x1F41D,
                0x1F41E,
                0x1F41F,
                0x1F420,
                0x1F421,
                0x1F422,
                0x1F423,
                0x1F424,
                0x1F425,
                0x1F426,
                0x1F427,
                0x1F428,
        };
        int randomIndex = (int) (Math.random() * unicodes.length);
        int unicode = unicodes[randomIndex];
        Log.d(getClass().getName(), "unicode: " + unicode);
        String emoji = getEmijoByUnicode(unicode);
        Log.i(getClass().getName(), "emoji: " + emoji);
        return emoji;
    }

    /**
     * See http://apps.timwhitlock.info/emoji/tables/unicode
     * @param unicode Example: "U+1F601" --> "0x1F601"
     * @return
     */
    private String getEmijoByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }
}
