package ai.elimu.chat.ui

import ai.elimu.chat.R
import ai.elimu.chat.model.Message
import ai.elimu.chat.util.Constants
import android.annotation.SuppressLint
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

@SuppressLint("RestrictedApi")
class ChatActivity : ComponentActivity() {

    private var messages: MutableList<Message> = mutableListOf()

    private lateinit var arrayAdapter: ArrayAdapter<*>

    private lateinit var mListPreviousMessages: ListView

    private lateinit var messageText: EditText

    private lateinit var mButtonSend: ImageButton

    lateinit var viewModel: ChatViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(javaClass.getName(), "onCreate")
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_chat)

        viewModel = ChatViewModel()

        mListPreviousMessages = findViewById<View?>(R.id.listPreviousMessages) as ListView
        messageText = findViewById<View?>(R.id.message) as EditText
        mButtonSend = findViewById<View?>(R.id.buttonSend) as ImageButton

        arrayAdapter = MessageListArrayAdapter(applicationContext, messages)
        mListPreviousMessages.setAdapter(arrayAdapter)

        viewModel.loadRecentMessages()

        lifecycleScope.launch {
            viewModel.messages.collect { newMessages ->
                messages.clear()
                messages.addAll(newMessages)
                refreshMessageList()
            }
        }

        messageText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                Log.i(javaClass.getName(), "afterTextChanged")
                setSendButtonState(TextUtils.isEmpty(editable))
            }
        })

        setSendButtonState(false)

        mButtonSend.setOnClickListener {
            Log.i(javaClass.getName(), "mButtonSend onClick")
            val message = messageText.getText().toString()
            sendMessage(message)
        }
    }

    private fun sendMessage(message: String) {
        viewModel.sendMessage(message)

        // Reset input field
        messageText.setText("")

        //showMessageFromAkili
        viewModel.maybeSimulateMessage(Constants.STUDENTID_AKILI)

        //showMessageFromPenguin
        viewModel.maybeSimulateMessage(Constants.STUDENTID_PENGUIN)
    }

    private fun setSendButtonState(isEmpty: Boolean) {
        mButtonSend.apply {
            isEnabled = !isEmpty
            setImageDrawable(
                getDrawable(
                    if (isEmpty) R.drawable.ic_send_grey_24dp else R.drawable.ic_send_white_24dp
                )
            )
        }
    }

    private fun refreshMessageList() {
        Log.i(javaClass.getName(), "refreshMessageList")

        arrayAdapter.notifyDataSetChanged()
        mListPreviousMessages.smoothScrollToPosition(mListPreviousMessages.count)
        // Fix problem with scrolling when keyboard is present
        mListPreviousMessages.postDelayed({
            Log.i(javaClass.getName(), "mListPreviousMessages.postDelayed")
            mListPreviousMessages.setSelection(mListPreviousMessages.count)
        }, 100)
    }
}