package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.data.model.ChatMessage
import com.example.data.model.MessageSender
import com.example.data.model.MessageStatus
import com.example.data.model.MessageType
import com.example.data.model.UserProfile
import com.example.data.model.UserSettings
import com.example.data.model.UserVerificationRecord

class Converters {
    @TypeConverter
    fun fromSender(value: MessageSender): String = value.name

    @TypeConverter
    fun toSender(value: String): MessageSender = runCatching { MessageSender.valueOf(value) }.getOrDefault(MessageSender.PARTNER)

    @TypeConverter
    fun fromStatus(value: MessageStatus): String = value.name

    @TypeConverter
    fun toStatus(value: String): MessageStatus = runCatching { MessageStatus.valueOf(value) }.getOrDefault(MessageStatus.SENT)

    @TypeConverter
    fun fromType(value: MessageType): String = value.name

    @TypeConverter
    fun toType(value: String): MessageType = runCatching { MessageType.valueOf(value) }.getOrDefault(MessageType.TEXT)
}

@Database(
    entities = [
        UserProfile::class,
        ChatMessage::class,
        UserVerificationRecord::class,
        UserSettings::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class DatingDatabase : RoomDatabase() {
    abstract fun datingDao(): DatingDao

    companion object {
        @Volatile
        private var INSTANCE: DatingDatabase? = null

        fun getDatabase(context: Context): DatingDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DatingDatabase::class.java,
                    "dating_aura.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
