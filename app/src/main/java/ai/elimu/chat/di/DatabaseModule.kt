package ai.elimu.chat.di

import ai.elimu.chat.data.local.AppDatabase
import ai.elimu.chat.data.local.ChatMessageDao
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase =
        Room.databaseBuilder(appContext, AppDatabase::class.java, "chat-db").build()

    @Provides
    fun provideMessageDao(db: AppDatabase): ChatMessageDao = db.messageDao()

}