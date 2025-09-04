package ai.elimu.chat.model

import ai.elimu.chat.util.getRandomEmoji
import java.util.Calendar

data class Message(
    val id: Long, val deviceId: String, val studentId: String?, val studentAvatar: String?,
    val timeSent: Calendar, val text: String
)

class MessageBuilder {
    private var id: Long = 0
    private var deviceId: String = ""
    private var studentId: String? = null
    private var studentAvatar: String? = null
    private var timeSent: Calendar = Calendar.getInstance()
    private var text: String = ""

    fun id(id: Long) = apply { this.id = id }
    fun deviceId(deviceId: String) = apply { this.deviceId = deviceId }
    fun studentId(studentId: String?) = apply { this.studentId = studentId }
    fun studentAvatar(studentAvatar: String?) = apply { this.studentAvatar = studentAvatar }
    fun timeSent(timesent: Calendar) = apply { this.timeSent = timesent }
    fun message(text: String) = apply { this.text = text }

    fun build() = Message(id, deviceId, studentId, studentAvatar, timeSent, text)
}

fun generateEmojiMessage(studentId: String): Message {
    val message = MessageBuilder()
    message.studentId(studentId)
    message.message(getRandomEmoji())
    return message.build()
}