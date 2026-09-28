package com.example.data.repository

import com.example.data.local.DatingDao
import com.example.data.local.InitialData
import com.example.data.model.ChatMessage
import com.example.data.model.MessageSender
import com.example.data.model.MessageStatus
import com.example.data.model.MessageType
import com.example.data.model.UserProfile
import com.example.data.model.UserSettings
import com.example.data.model.UserVerificationRecord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.UUID

class DatingRepository(private val dao: DatingDao) {

    val allProfiles: Flow<List<UserProfile>> = dao.getAllProfiles()
    val discoveryProfiles: Flow<List<UserProfile>> = dao.getDiscoveryProfiles()
    val matchedProfiles: Flow<List<UserProfile>> = dao.getMatchedProfiles()
    val allMessages: Flow<List<ChatMessage>> = dao.getAllMessages()
    val verificationRecord: Flow<UserVerificationRecord?> = dao.getVerificationRecord()
    val userSettings: Flow<UserSettings?> = dao.getUserSettings()

    suspend fun seedInitialDataIfNeeded() {
        val existing = dao.getAllProfiles().firstOrNull()
        if (existing.isNullOrEmpty()) {
            dao.insertProfiles(InitialData.getInitialProfiles())
            dao.insertMessages(InitialData.getInitialMessages())
            dao.saveUserSettings(UserSettings())
            dao.saveVerificationRecord(
                UserVerificationRecord(
                    id = 1,
                    isVerified = false,
                    challengePose = "Smile & tilt head slightly right",
                    biometricScore = 0
                )
            )
        }
    }

    suspend fun likeProfile(id: String): Boolean {
        val profile = dao.getProfileById(id) ?: return false
        // Determine if this results in a mutual match (Elena and Marcus start as matches; for others, give a 75% realistic match rate)
        val isMutualMatch = profile.isMatch || (profile.compatibilityScore >= 88)
        val now = System.currentTimeMillis()
        dao.likeProfile(id, isMutualMatch, now)

        if (isMutualMatch) {
            // Send automatic celebratory starter message from the match!
            val starterMessage = ChatMessage(
                id = UUID.randomUUID().toString(),
                profileId = id,
                sender = MessageSender.PARTNER,
                text = "Hey! It's a match! 😊 I was hoping we'd connect.",
                timestamp = now,
                status = MessageStatus.DELIVERED,
                type = MessageType.TEXT
            )
            dao.insertMessage(starterMessage)
        }
        return isMutualMatch
    }

    suspend fun passProfile(id: String) {
        dao.passProfile(id)
    }

    suspend fun resetAllSwipes() {
        dao.resetAllSwipes()
    }

    fun observeMessages(profileId: String): Flow<List<ChatMessage>> =
        dao.getMessagesForProfile(profileId)

    suspend fun markMessagesAsRead(profileId: String) {
        dao.markMessagesAsRead(profileId)
    }

    suspend fun sendMessage(
        profileId: String,
        text: String,
        type: MessageType = MessageType.TEXT,
        payload: String? = null
    ): ChatMessage {
        val message = ChatMessage(
            id = UUID.randomUUID().toString(),
            profileId = profileId,
            sender = MessageSender.USER,
            text = text,
            timestamp = System.currentTimeMillis(),
            status = MessageStatus.SENT,
            type = type,
            payload = payload
        )
        dao.insertMessage(message)
        return message
    }

    /**
     * Simulates intelligent real-time conversational response from match
     */
    fun triggerSimulatedPartnerReply(
        profileId: String,
        userMessageText: String,
        scope: CoroutineScope,
        onTypingStateChanged: (Boolean) -> Unit
    ) {
        scope.launch(Dispatchers.IO) {
            // Typing delay simulation
            delay(1200)
            onTypingStateChanged(true)
            delay(1800)
            onTypingStateChanged(false)

            val profile = dao.getProfileById(profileId)
            val partnerName = profile?.name?.split(" ")?.firstOrNull() ?: "Match"

            val replyText = when {
                userMessageText.contains("coffee", ignoreCase = true) ||
                userMessageText.contains("drink", ignoreCase = true) -> {
                    "I'd love that! What neighborhood works best for you? There's a cozy spot downtown with great outdoor seating."
                }
                userMessageText.contains("photo", ignoreCase = true) ||
                userMessageText.contains("camera", ignoreCase = true) -> {
                    "Yes! Golden hour light here is incredible. We should definitely go on a photo walk."
                }
                userMessageText.contains("weekend", ignoreCase = true) ||
                userMessageText.contains("free", ignoreCase = true) -> {
                    "Saturday afternoon would be perfect! Let's make it happen ✨"
                }
                userMessageText.contains("hello", ignoreCase = true) ||
                userMessageText.contains("hey", ignoreCase = true) ||
                userMessageText.contains("hi", ignoreCase = true) -> {
                    "Hey! Hope you're having an amazing day so far. Tell me, what's been the highlight of your week?"
                }
                else -> {
                    "Haha completely agree! That's so refreshing to hear from someone on here 😊 What are you up to this evening?"
                }
            }

            val reply = ChatMessage(
                id = UUID.randomUUID().toString(),
                profileId = profileId,
                sender = MessageSender.PARTNER,
                text = replyText,
                timestamp = System.currentTimeMillis(),
                status = MessageStatus.DELIVERED,
                type = MessageType.TEXT
            )
            dao.insertMessage(reply)
        }
    }

    suspend fun saveVerification(
        selfieUri: String?,
        score: Int,
        badgeTier: String
    ) {
        val record = UserVerificationRecord(
            id = 1,
            isVerified = true,
            verifiedTimestamp = System.currentTimeMillis(),
            selfieUri = selfieUri,
            biometricScore = score,
            badgeTier = badgeTier
        )
        dao.saveVerificationRecord(record)
    }

    suspend fun updateSettings(settings: UserSettings) {
        dao.saveUserSettings(settings)
    }

    suspend fun updateLocation(lat: Double, lng: Double) {
        dao.updateUserLocation(lat, lng)
    }

    suspend fun getProfile(id: String): UserProfile? = dao.getProfileById(id)
}
