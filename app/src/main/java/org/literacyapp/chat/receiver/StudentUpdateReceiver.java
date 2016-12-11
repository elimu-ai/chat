package org.literacyapp.chat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

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
            sharedPreferences.edit().putString(PREF_STUDENT_ID, studentId).commit();
        }

        if (!TextUtils.isEmpty(studentAvatar)) {
            sharedPreferences.edit().putString(PREF_STUDENT_AVATAR, studentAvatar).commit();
        }
    }
}
