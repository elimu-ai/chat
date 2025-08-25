package ai.elimu.chat.data.local

import ai.elimu.chat.model.Message
import java.util.Calendar

fun MessageEntity.toMessage(): Message {
    return Message(
        this.id,
        this.deviceId,
        this.studentId,
        this.studentAvatar,
        Calendar.getInstance().apply { timeInMillis = this@toMessage.timeSent },
        this.text
    )
}

fun Message.toEntity(): MessageEntity {
    return MessageEntity(
        deviceId = this.deviceId,
        studentId = this.studentId,
        studentAvatar = this.studentAvatar,
        timeSent = this.timeSent.timeInMillis,
        text = this.text
    )
}