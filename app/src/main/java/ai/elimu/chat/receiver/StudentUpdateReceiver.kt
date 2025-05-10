package ai.elimu.chat.receiver

import ai.elimu.chat.ChatApplication
import ai.elimu.chat.dao.MessageDao
import ai.elimu.chat.util.DeviceInfoHelper.getDeviceId
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import android.text.TextUtils
import android.util.Log

class StudentUpdateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.i(javaClass.getName(), "onReceive")

        val studentId = intent.getStringExtra("studentId")
        Log.i(javaClass.getName(), "studentId: " + studentId)

        val studentAvatar = intent.getStringExtra("studentAvatar")
        Log.i(javaClass.getName(), "studentAvatar: " + studentAvatar)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        if (!TextUtils.isEmpty(studentId)) {
            val existingStudentId = sharedPreferences.getString(PREF_STUDENT_ID, null)
            Log.i(javaClass.getName(), "existingStudentId: " + existingStudentId)
            if (TextUtils.isEmpty(existingStudentId)) {
                // Update previously sent messages on the current device
                val chatApplication = context.applicationContext as ChatApplication
                val messageDao = chatApplication.daoSession!!.messageDao
                val existingMessages = messageDao.queryBuilder()
                    .where(
                        MessageDao.Properties.DeviceId.eq(getDeviceId(context)),
                        MessageDao.Properties.StudentId.isNull()
                    )
                    .list()
                Log.i(javaClass.getName(), "existingMessages.size(): " + existingMessages.size)
                for (message in existingMessages) {
                    message.studentId = studentId
                    message.studentAvatar = studentAvatar
                    messageDao.update(message)
                }
            }

            sharedPreferences.edit().putString(PREF_STUDENT_ID, studentId).commit()
        }

        if (!TextUtils.isEmpty(studentAvatar)) {
            sharedPreferences.edit().putString(PREF_STUDENT_AVATAR, studentAvatar).commit()
        }
    }

    companion object {
        const val PREF_STUDENT_ID: String = "pref_student_id"
        const val PREF_STUDENT_AVATAR: String = "pref_student_avatar"
    }
}
