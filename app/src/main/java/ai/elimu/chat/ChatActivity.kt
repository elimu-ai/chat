package ai.elimu.chat

import ai.elimu.chat.dao.MessageDao
import ai.elimu.chat.model.Message
import ai.elimu.chat.receiver.StudentUpdateReceiver
import ai.elimu.chat.util.DeviceInfoHelper
import android.app.Activity
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import java.util.Calendar

class ChatActivity : Activity() {
    private var messageDao: MessageDao? = null

    private var messages: MutableList<Message> = mutableListOf()

    private var arrayAdapter: ArrayAdapter<*>? = null

    private var mListPreviousMessages: ListView? = null

    private var messageText: EditText? = null

    private var mButtonSend: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(javaClass.getName(), "onCreate")
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_chat)

        messageDao = (getApplication() as ChatApplication).daoSession!!.getMessageDao()

        mListPreviousMessages = findViewById<View?>(R.id.listPreviousMessages) as ListView
        messageText = findViewById<View?>(R.id.message) as EditText
        mButtonSend = findViewById<View?>(R.id.buttonSend) as ImageButton
    }

    override fun onStart() {
        Log.i(javaClass.getName(), "onStart")
        super.onStart()

        //        ContentProvider.initializeDb(this);
//        List<Letter> letters = ContentProvider.getAvailableLetters();
//        Log.i(getClass().getName(), "letters: " + letters);

        // Load messages sent within the last 24 hours
        val calendar24HoursAgo = Calendar.getInstance()
        calendar24HoursAgo.add(Calendar.HOUR_OF_DAY, -24)
        messages = messageDao!!.queryBuilder()
            .where(MessageDao.Properties.TimeSent.gt(calendar24HoursAgo.getTimeInMillis()))
            .list()
        Log.i(javaClass.getName(), "messages.size(): " + messages!!.size)

        arrayAdapter = MessageListArrayAdapter(getApplicationContext(), messages)
        mListPreviousMessages!!.setAdapter(arrayAdapter)

        messageText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                Log.i(javaClass.getName(), "afterTextChanged")

                Log.i(javaClass.getName(), "editable: " + editable)

                if (!TextUtils.isEmpty(editable)) {
                    if (!mButtonSend!!.isEnabled()) {
                        mButtonSend!!.setEnabled(true)
                        mButtonSend!!.setImageDrawable(getDrawable(R.drawable.ic_send_white_24dp))

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
                    mButtonSend!!.setEnabled(false)
                    mButtonSend!!.setImageDrawable(getDrawable(R.drawable.ic_send_grey_24dp))
                }
            }
        })

        // Default to grey button
        mButtonSend!!.setEnabled(false)
        mButtonSend!!.setImageDrawable(getDrawable(R.drawable.ic_send_grey_24dp))

        mButtonSend!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                Log.i(javaClass.getName(), "mButtonSend onClick")

                val text = messageText!!.getText().toString()
                Log.i(javaClass.getName(), "text: " + text)

                val sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext())

                // Store in database
                val message = Message()
                message.setDeviceId(DeviceInfoHelper.getDeviceId(getApplicationContext()))
                val studentId =
                    sharedPreferences.getString(StudentUpdateReceiver.PREF_STUDENT_ID, null)
                if (!TextUtils.isEmpty(studentId)) {
                    message.setStudentId(studentId)
                }
                val studentAvatar =
                    sharedPreferences.getString(StudentUpdateReceiver.PREF_STUDENT_AVATAR, null)
                if (!TextUtils.isEmpty(studentAvatar)) {
                    message.setStudentAvatar(studentAvatar)
                }
                message.setTimeSent(Calendar.getInstance())
                message.setText(text)
                messageDao!!.insert(message)

                // Add to UI
                addToMessageListAndRefresh(message)

                // Reset input field
                messageText!!.setText("")

                val showMessageFromAkili = Math.random() > 0.5
                if (showMessageFromAkili) {
                    mButtonSend!!.postDelayed(object : Runnable {
                        override fun run() {
                            // Simulate message from AI tutor

                            // Akili

                            val message = Message()
                            message.setStudentId("00000000aaaaaaaa_1")
                            message.setTimeSent(Calendar.getInstance())
                            message.setText(randomEmoji)
                            addToMessageListAndRefresh(message)
                        }
                    }, (2000 + (Math.random() * 8000).toInt()).toLong())
                }

                val showMessageFromPenguin = Math.random() > 0.5
                if (showMessageFromPenguin) {
                    mButtonSend!!.postDelayed(object : Runnable {
                        override fun run() {
                            // Penguin
                            val messagePenguin = Message()
                            messagePenguin.setStudentId("00000000aaaaaaaa_2")
                            messagePenguin.setTimeSent(Calendar.getInstance())
                            messagePenguin.setText(randomEmoji)
                            addToMessageListAndRefresh(messagePenguin)
                        }
                    }, (2000 + (Math.random() * 8000).toInt()).toLong())
                }
            }
        })
    }

    private fun addToMessageListAndRefresh(message: Message) {
        Log.i(javaClass.getName(), "addToMessageListAndRefresh")
        messages.add(message)
        refreshMessageList()
    }

    private fun refreshMessageList() {
        Log.i(javaClass.getName(), "refreshMessageList")

        arrayAdapter!!.notifyDataSetChanged()
        mListPreviousMessages!!.smoothScrollToPosition(mListPreviousMessages!!.getCount())
        // Fix problem with scrolling when keyboard is present
        mListPreviousMessages!!.postDelayed(object : Runnable {
            override fun run() {
                Log.i(javaClass.getName(), "mListPreviousMessages.postDelayed")
                mListPreviousMessages!!.setSelection(mListPreviousMessages!!.getCount())
            }
        }, 100)
    }

    private val randomEmoji: String
        get() {
            val unicodes = intArrayOf(
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
                0x1F64F,  // Uncategorized

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
            )
            val randomIndex = (Math.random() * unicodes.size).toInt()
            val unicode = unicodes[randomIndex]
            Log.d(javaClass.getName(), "unicode: " + unicode)
            val emoji = getEmijoByUnicode(unicode)
            Log.i(javaClass.getName(), "emoji: " + emoji)
            return emoji
        }

    /**
     * See http://apps.timwhitlock.info/emoji/tables/unicode
     * @param unicode Example: "U+1F601" --> "0x1F601"
     * @return
     */
    private fun getEmijoByUnicode(unicode: Int): String {
        return String(Character.toChars(unicode))
    }
}
