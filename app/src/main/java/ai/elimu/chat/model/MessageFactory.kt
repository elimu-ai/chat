package ai.elimu.chat.model

import ai.elimu.chat.util.getRandomEmoji
import java.util.Calendar

object MessageFactory {

    fun generateEmojiMessage(studentId: String): Message {
        val message = Message()
        message.studentId = studentId
        message.timeSent = Calendar.getInstance()
        message.text = getRandomEmoji()
        return message
    }
}