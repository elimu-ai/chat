package ai.elimu.chat.ui

import ai.elimu.chat.data.local.toEntity
import ai.elimu.chat.data.local.toMessage
import ai.elimu.chat.di.ServiceLocator
import ai.elimu.chat.model.Message
import ai.elimu.chat.model.MessageBuilder
import ai.elimu.chat.model.generateEmojiMessage
import ai.elimu.chat.util.Constants
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.random.Random

class ChatViewModel() : ViewModel() {
    private val _messages = MutableStateFlow<List<Message>>(emptyList())

    val messages: StateFlow<List<Message>> = _messages

    val sharedPreferences = ServiceLocator.provideSharedPreference()

    val chatMessageDao = ServiceLocator.provideChatMessageDao()

    fun loadRecentMessages() {
        viewModelScope.launch {
            val calendar24HoursAgo = Calendar.getInstance()
            calendar24HoursAgo.add(Calendar.HOUR_OF_DAY, -24)
            val newMessages =
                chatMessageDao.getMessages(timeStamp = calendar24HoursAgo.timeInMillis)
            val uiMessages = newMessages.map { it.toMessage() }
            _messages.value = emptyList()
            _messages.value = uiMessages
        }
    }

    fun sendMessage(message: String) {
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

        messageBuilder.message(message)

        viewModelScope.launch {
            val message = messageBuilder.build()
            chatMessageDao.insert(message.toEntity())
            _messages.value = _messages.value + message
        }
    }

    fun maybeSimulateMessage(studentId: String) {
        if (Random.nextBoolean()) {
            val delayMillis = (2000..10000).random().toLong()
            viewModelScope.launch {
                delay(delayMillis)
                val message = generateEmojiMessage(studentId)
                _messages.value = _messages.value + message
            }
        }
    }
}