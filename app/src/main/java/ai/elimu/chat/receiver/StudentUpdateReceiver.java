package org.literacyapp.chat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import org.literacyapp.chat.ChatApplication;
import org.literacyapp.chat.dao.MessageDao;
import org.literacyapp.chat.model.Message;
import org.literacyapp.chat.util.DeviceInfoHelper;

import java.util.List;

public class StudentUpdateReceiver extends BroadcastReceiver {

    public static final String PREF_STUDENT_ID = "pref_student_id";
    public static final String PREF_STUDENT_AVATAR = "pref_student_avatar";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(getClass().getName(), "onReceive");

        String studentId = intent.getStringExtra("studentId");
        Log.i(getClass().getName(), "studentId: " + studentId);

        String studentAvatar = intent.getStringExtra("studentAvatar");
        Log.i(getClass().getName(), "studentAvatar: " + studentAvatar);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (!TextUtils.isEmpty(studentId)) {
            String existingStudentId = sharedPreferences.getString(PREF_STUDENT_ID, null);
            Log.i(getClass().getName(), "existingStudentId: " + existingStudentId);
            if (TextUtils.isEmpty(existingStudentId)) {
                // Update previously sent messages on the current device
                ChatApplication chatApplication = (ChatApplication) context.getApplicationContext();
                MessageDao messageDao = chatApplication.getDaoSession().getMessageDao();
                List<Message> existingMessages = messageDao.queryBuilder()
                        .where(
                                MessageDao.Properties.DeviceId.eq(DeviceInfoHelper.getDeviceId(context)),
                                MessageDao.Properties.StudentId.isNull()
                        )
                        .list();
                Log.i(getClass().getName(), "existingMessages.size(): " + existingMessages.size());
                for (Message message : existingMessages) {
                    message.setStudentId(studentId);
                    message.setStudentAvatar(studentAvatar);
                    messageDao.update(message);
                }
            }

            sharedPreferences.edit().putString(PREF_STUDENT_ID, studentId).commit();
        }

        if (!TextUtils.isEmpty(studentAvatar)) {
            sharedPreferences.edit().putString(PREF_STUDENT_AVATAR, studentAvatar).commit();
        }
    }
}
