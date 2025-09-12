package ai.elimu.chat.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ChatMessageDao {
    @Insert
    suspend fun insert(message: MessageEntity): Long

    @Query(
        "SELECT * FROM messages WHERE device_id LIKE :deviceId AND" +
                " student_id LIKE :studentId"
    )
    suspend fun getAllMessages(deviceId: String, studentId: String): List<MessageEntity>

    @Query("SELECT * FROM messages WHERE timestamp > :timeStamp")
    suspend fun getMessages(timeStamp: Long): List<MessageEntity>

    @Query(
        "UPDATE messages SET student_id = :studentId, " +
                "student_avatar = :studentAvatar WHERE device_id = :deviceId AND student_id IS NULL"
    )
    suspend fun updateStudentInfoForDevice(
        deviceId: String,
        studentId: String?,
        studentAvatar: String?
    )
}