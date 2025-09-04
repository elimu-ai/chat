package ai.elimu.chat.di

import ai.elimu.chat.data.local.AppDatabase
import ai.elimu.chat.data.local.ChatMessageDao
import android.content.Context
import androidx.room.Room

object ServiceLocator {

    private var chatMessageDao: ChatMessageDao? = null

    fun initialize(context: Context) {
        val db = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "chat-db-new"
        ).build()
        chatMessageDao = db.messageDao()
    }

    fun provideChatMessageDao(): ChatMessageDao {
        return chatMessageDao ?: throw IllegalStateException("ServiceLocator not initialized")
    }
}