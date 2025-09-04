package ai.elimu.chat.di

import ai.elimu.chat.data.local.AppDatabase
import ai.elimu.chat.data.local.ChatMessageDao
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings
import androidx.room.Room

object ServiceLocator {

    private var chatMessageDao: ChatMessageDao? = null

    private var deviceId: String? = null

    private var sharedPreferences: SharedPreferences? = null

    @SuppressLint("HardwareIds")
    fun initialize(context: Context) {
        val db = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "chat-db-new"
        ).build()
        chatMessageDao = db.messageDao()

        deviceId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

        sharedPreferences = context.getSharedPreferences("your_pref_name", Context.MODE_PRIVATE)
    }

    fun provideChatMessageDao(): ChatMessageDao {
        return chatMessageDao ?: throw IllegalStateException("ServiceLocator not initialized")
    }

    fun provideDeviceId(): String {
        return deviceId ?: throw IllegalStateException("ServiceLocator not initialized")
    }

    fun provideSharedPreference(): SharedPreferences {
        return sharedPreferences ?: throw IllegalStateException("ServiceLocator not initialized")
    }
}