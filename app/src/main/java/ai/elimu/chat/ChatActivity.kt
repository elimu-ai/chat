package ai.elimu.chat

import ai.elimu.chat.data.local.ChatMessageDao
import ai.elimu.chat.data.local.toEntity
import ai.elimu.chat.data.local.toMessage
import ai.elimu.chat.di.ServiceLocator
import ai.elimu.chat.model.Message
import ai.elimu.chat.model.MessageBuilder
import ai.elimu.chat.model.generateEmojiMessage
import ai.elimu.chat.util.Constants
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import androidx.core.app.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.util.Calendar

//@AndroidEntryPoint
class ChatActivity : ComponentActivity() {

    // @Inject
    lateinit var chatMessageDao: ChatMessageDao

    lateinit var sharedPreferences: SharedPreferences

    private var messages: MutableList<Message> = mutableListOf()

    private var arrayAdapter: ArrayAdapter<*>? = null

    private var mListPreviousMessages: ListView? = null

    private var messageText: EditText? = null

    private var mButtonSend: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(javaClass.getName(), "onCreate")
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_chat)
        chatMessageDao = ServiceLocator.provideChatMessageDao()

        sharedPreferences = ServiceLocator.provideSharedPreference()

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
        /*        val calendar24HoursAgo = Calendar.getInstance()
                calendar24HoursAgo.add(Calendar.HOUR_OF_DAY, -24)
                messages = messageDao!!.queryBuilder()
                    .where(MessageDao.Properties.TimeSent.gt(calendar24HoursAgo.getTimeInMillis()))
                    .list()
                Log.i(javaClass.getName(), "messages.size(): " + messages.size)*/

        arrayAdapter = MessageListArrayAdapter(applicationContext, messages)
        mListPreviousMessages!!.setAdapter(arrayAdapter)

        lifecycleScope.launch {
            val calendar24HoursAgo = Calendar.getInstance()
            calendar24HoursAgo.add(Calendar.HOUR_OF_DAY, -24)
            val newMessages =
                chatMessageDao.getMessages(timeStamp = calendar24HoursAgo.timeInMillis)
            val uiMessages = newMessages.map { it.toMessage() }
            messages.addAll(uiMessages)
            refreshMessageList()
        }

        messageText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                Log.i(javaClass.getName(), "afterTextChanged")

                Log.i(javaClass.getName(), "editable: $editable")

                if (!TextUtils.isEmpty(editable)) {
                    if (!mButtonSend!!.isEnabled) {
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

        mButtonSend!!.setOnClickListener {
            Log.i(javaClass.getName(), "mButtonSend onClick")

            val text = messageText!!.getText().toString()
            Log.i(javaClass.getName(), "text: $text")

            // Store in database
            val messageBuilder = MessageBuilder()
            messageBuilder.deviceId(ServiceLocator.provideDeviceId())
            val studentId =
                sharedPreferences.getString(Constants.PREF_STUDENT_ID, null)
            studentId?.let {
                messageBuilder.studentId(it)
            }

            val studentAvatar =
                sharedPreferences.getString(Constants.PREF_STUDENT_AVATAR, null)
            studentAvatar?.let {
                messageBuilder.studentAvatar(it)
            }

            messageBuilder.message(text)

            val message = messageBuilder.build()

            lifecycleScope.launch {
                chatMessageDao.insert(message.toEntity())
            }
            //  messageDao!!.insert(message)

            // Add to UI
            addToMessageListAndRefresh(message)

            // Reset input field
            messageText!!.setText("")

            val showMessageFromAkili = Math.random() > 0.5
            if (showMessageFromAkili) {
                mButtonSend!!.postDelayed({ // Simulate message from AI tutor
                    val akiliMessage = generateEmojiMessage("00000000aaaaaaaa_1")
                    addToMessageListAndRefresh(akiliMessage)
                }, (2000 + (Math.random() * 8000).toInt()).toLong())
            }

            val showMessageFromPenguin = Math.random() > 0.5
            if (showMessageFromPenguin) {
                mButtonSend!!.postDelayed({ // Penguin
                    val penguinMessage = generateEmojiMessage("00000000aaaaaaaa_2")
                    addToMessageListAndRefresh(penguinMessage)
                }, (2000 + (Math.random() * 8000).toInt()).toLong())
            }
        }
    }

    private fun addToMessageListAndRefresh(message: Message) {
        Log.i(javaClass.getName(), "addToMessageListAndRefresh")
        messages.add(message)
        refreshMessageList()
    }

    private fun refreshMessageList() {
        Log.i(javaClass.getName(), "refreshMessageList")

        arrayAdapter!!.notifyDataSetChanged()
        mListPreviousMessages!!.smoothScrollToPosition(mListPreviousMessages!!.count)
        // Fix problem with scrolling when keyboard is present
        mListPreviousMessages!!.postDelayed({
            Log.i(javaClass.getName(), "mListPreviousMessages.postDelayed")
            mListPreviousMessages!!.setSelection(mListPreviousMessages!!.count)
        }, 100)
    }
}
