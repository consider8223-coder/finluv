package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ChatMessage
import com.example.data.model.UserProfile
import com.example.data.model.UserSettings
import com.example.data.model.UserVerificationRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface DatingDao {

    // Profiles
    @Query("SELECT * FROM profiles")
    fun getAllProfiles(): Flow<List<UserProfile>>

    @Query("SELECT * FROM profiles WHERE isLiked = 0 AND isPassed = 0")
    fun getDiscoveryProfiles(): Flow<List<UserProfile>>

    @Query("SELECT * FROM profiles WHERE isMatch = 1 ORDER BY matchTimestamp DESC")
    fun getMatchedProfiles(): Flow<List<UserProfile>>

    @Query("SELECT * FROM profiles WHERE id = :id LIMIT 1")
    suspend fun getProfileById(id: String): UserProfile?

    @Query("SELECT * FROM profiles WHERE id = :id LIMIT 1")
    fun observeProfileById(id: String): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfiles(profiles: List<UserProfile>)

    @Update
    suspend fun updateProfile(profile: UserProfile)

    @Query("UPDATE profiles SET isLiked = 1, isMatch = :isMatch, matchTimestamp = :timestamp WHERE id = :id")
    suspend fun likeProfile(id: String, isMatch: Boolean, timestamp: Long)

    @Query("UPDATE profiles SET isPassed = 1 WHERE id = :id")
    suspend fun passProfile(id: String)

    @Query("UPDATE profiles SET isLiked = 0, isPassed = 0, isMatch = 0")
    suspend fun resetAllSwipes()

    // Chat
    @Query("SELECT * FROM chat_messages WHERE profileId = :profileId ORDER BY timestamp ASC")
    fun getMessagesForProfile(profileId: String): Flow<List<ChatMessage>>

    @Query("SELECT * FROM chat_messages ORDER BY timestamp DESC")
    fun getAllMessages(): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<ChatMessage>)

    @Query("UPDATE chat_messages SET status = 'READ' WHERE profileId = :profileId AND sender = 'PARTNER'")
    suspend fun markMessagesAsRead(profileId: String)

    // Verification
    @Query("SELECT * FROM verification_record WHERE id = 1 LIMIT 1")
    fun getVerificationRecord(): Flow<UserVerificationRecord?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveVerificationRecord(record: UserVerificationRecord)

    // User Settings
    @Query("SELECT * FROM user_settings WHERE id = 1 LIMIT 1")
    fun getUserSettings(): Flow<UserSettings?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserSettings(settings: UserSettings)

    @Query("UPDATE user_settings SET userLatitude = :lat, userLongitude = :lng WHERE id = 1")
    suspend fun updateUserLocation(lat: Double, lng: Double)
}
