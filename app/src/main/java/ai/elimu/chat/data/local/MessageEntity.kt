package ai.elimu.chat.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")

data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "device_id") val deviceId: String,
    @ColumnInfo(name = "student_id") val studentId: String?,
    @ColumnInfo(name = "student_avatar") val studentAvatar: String?,
    @ColumnInfo(name = "message") val text: String,
    @ColumnInfo(name = "timestamp") val timeSent: Long
)
