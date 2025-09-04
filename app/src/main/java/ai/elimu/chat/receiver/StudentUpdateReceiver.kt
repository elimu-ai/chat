package ai.elimu.chat.receiver

import ai.elimu.chat.di.ServiceLocator
import ai.elimu.chat.util.Constants
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import androidx.core.content.edit

class StudentUpdateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.i(javaClass.getName(), "onReceive")
        val studentId = intent.getStringExtra(EXTRA_STUDENT_ID)
        Log.i(javaClass.getName(), "studentId: $studentId")

        val studentAvatar = intent.getStringExtra(EXTRA_STUDENT_AVATAR)
        Log.i(javaClass.getName(), "studentAvatar: $studentAvatar")

        val sharedPreferences = ServiceLocator.provideSharedPreference()

        if (!TextUtils.isEmpty(studentId)) {
            val existingStudentId = sharedPreferences.getString(Constants.PREF_STUDENT_ID, null)
            Log.i(javaClass.getName(), "existingStudentId: $existingStudentId")
            /*  if (TextUtils.isEmpty(existingStudentId)) {
                              // Update previously sent messages on the current device // TODO: Migrate to room
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
            }*/

            sharedPreferences.edit(commit = true) {
                putString(
                    Constants.PREF_STUDENT_ID,
                    studentId
                )
            }
        }

        if (!TextUtils.isEmpty(studentAvatar)) {
            sharedPreferences.edit(commit = true) {
                putString(
                    Constants.PREF_STUDENT_AVATAR,
                    studentAvatar
                )
            }
        }
    }

    companion object {
        const val EXTRA_STUDENT_ID = "studentId"

        const val EXTRA_STUDENT_AVATAR = "studentAvatar"
    }
}
