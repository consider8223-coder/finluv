package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.DatingDatabase
import com.example.data.model.ChatMessage
import com.example.data.model.MessageType
import com.example.data.model.UserProfile
import com.example.data.model.UserSettings
import com.example.data.model.UserVerificationRecord
import com.example.data.repository.DatingRepository
import com.example.util.LocationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DiscoveryProfileWithDistance(
    val profile: UserProfile,
    val distanceKm: Double,
    val formattedDistance: String,
    val bearing: Float
)

data class MatchWithLastMessage(
    val profile: UserProfile,
    val lastMessage: ChatMessage?,
    val unreadCount: Int,
    val formattedDistance: String
)

class DatingViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DatingRepository

    init {
        val db = DatingDatabase.getDatabase(application)
        repository = DatingRepository(db.datingDao())
        viewModelScope.launch(Dispatchers.IO) {
            repository.seedInitialDataIfNeeded()
        }
    }

    // Settings & Location state
    val settingsState: StateFlow<UserSettings> = repository.userSettings
        .combine(MutableStateFlow(Unit)) { settings, _ ->
            settings ?: UserSettings()
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserSettings()
        )

    val verificationState: StateFlow<UserVerificationRecord?> = repository.verificationRecord
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // Filtered Discovery Profiles with real-time distance calculations
    val discoveryProfiles: StateFlow<List<DiscoveryProfileWithDistance>> = combine(
        repository.discoveryProfiles,
        settingsState
    ) { profiles, settings ->
        profiles
            .filter { profile ->
                val distance = LocationHelper.calculateDistanceKm(
                    settings.userLatitude, settings.userLongitude,
                    profile.latitude, profile.longitude
                )
                val matchesDistance = distance <= settings.maxDistanceKm
                val matchesAge = profile.age in settings.minAge..settings.maxAge
                val matchesVerified = if (settings.verifiedOnly) profile.isVerified else true
                matchesDistance && matchesAge && matchesVerified
            }
            .map { profile ->
                val dist = LocationHelper.calculateDistanceKm(
                    settings.userLatitude, settings.userLongitude,
                    profile.latitude, profile.longitude
                )
                DiscoveryProfileWithDistance(
                    profile = profile,
                    distanceKm = dist,
                    formattedDistance = LocationHelper.formatDistance(dist),
                    bearing = LocationHelper.calculateBearing(
                        settings.userLatitude, settings.userLongitude,
                        profile.latitude, profile.longitude
                    )
                )
            }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Matches with their latest chat message
    val matchesWithLastMessage: StateFlow<List<MatchWithLastMessage>> = combine(
        repository.matchedProfiles,
        repository.allMessages,
        settingsState
    ) { matches, allMsgs, settings ->
        matches.map { profile ->
            val profileMessages = allMsgs.filter { it.profileId == profile.id }
            val lastMsg = profileMessages.maxByOrNull { it.timestamp }
            val unreadCount = profileMessages.count { it.sender == com.example.data.model.MessageSender.PARTNER && it.status != com.example.data.model.MessageStatus.READ }
            val dist = LocationHelper.calculateDistanceKm(
                settings.userLatitude, settings.userLongitude,
                profile.latitude, profile.longitude
            )
            MatchWithLastMessage(
                profile = profile,
                lastMessage = lastMsg,
                unreadCount = unreadCount,
                formattedDistance = LocationHelper.formatDistance(dist)
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Celebration modal when a new mutual match occurs
    private val _mutualMatchCelebration = MutableStateFlow<UserProfile?>(null)
    val mutualMatchCelebration: StateFlow<UserProfile?> = _mutualMatchCelebration.asStateFlow()

    // Active Chat State
    private val _activeChatProfileId = MutableStateFlow<String?>(null)
    val activeChatProfileId: StateFlow<String?> = _activeChatProfileId.asStateFlow()

    private val _activeChatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val activeChatMessages: StateFlow<List<ChatMessage>> = _activeChatMessages.asStateFlow()

    private val _isPartnerTyping = MutableStateFlow(false)
    val isPartnerTyping: StateFlow<Boolean> = _isPartnerTyping.asStateFlow()

    // Verification Flow State
    private val _isVerifying = MutableStateFlow(false)
    val isVerifying: StateFlow<Boolean> = _isVerifying.asStateFlow()

    private val _verificationStep = MutableStateFlow(1) // 1: instructions, 2: camera/pose, 3: scanning, 4: success
    val verificationStep: StateFlow<Int> = _verificationStep.asStateFlow()

    fun onSwipeRight(profile: UserProfile) {
        viewModelScope.launch(Dispatchers.IO) {
            val isMatch = repository.likeProfile(profile.id)
            if (isMatch) {
                _mutualMatchCelebration.value = profile
            }
        }
    }

    fun onSwipeLeft(profile: UserProfile) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.passProfile(profile.id)
        }
    }

    fun onSuperLike(profile: UserProfile) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.likeProfile(profile.id)
            _mutualMatchCelebration.value = profile
        }
    }

    fun dismissMatchCelebration() {
        _mutualMatchCelebration.value = null
    }

    fun openChat(profileId: String) {
        _activeChatProfileId.value = profileId
        viewModelScope.launch(Dispatchers.IO) {
            repository.markMessagesAsRead(profileId)
            repository.observeMessages(profileId).collect { msgs ->
                _activeChatMessages.value = msgs
            }
        }
    }

    fun closeChat() {
        _activeChatProfileId.value = null
        _activeChatMessages.value = emptyList()
        _isPartnerTyping.value = false
    }

    fun sendMessage(text: String, type: MessageType = MessageType.TEXT, payload: String? = null) {
        val profileId = _activeChatProfileId.value ?: return
        if (text.isBlank() && payload.isNullOrBlank()) return

        viewModelScope.launch(Dispatchers.IO) {
            repository.sendMessage(profileId, text, type, payload)
            // Trigger contextual partner reply
            repository.triggerSimulatedPartnerReply(
                profileId = profileId,
                userMessageText = text,
                scope = viewModelScope,
                onTypingStateChanged = { typing ->
                    _isPartnerTyping.value = typing
                }
            )
        }
    }

    fun sendDateInvite(locationTitle: String, dateTime: String) {
        sendMessage(
            text = "I'd love to invite you for a date! How about $locationTitle on $dateTime?",
            type = MessageType.DATE_INVITE,
            payload = "$locationTitle • $dateTime"
        )
    }

    fun startVerification() {
        _isVerifying.value = true
        _verificationStep.value = 1
    }

    fun proceedToVerificationCamera() {
        _verificationStep.value = 2
    }

    fun completeVerificationCapture(capturedSelfieUri: String?) {
        viewModelScope.launch {
            _verificationStep.value = 3 // AI biometric scanning & liveness verification animation
            delay(2800) // realistic biometric check processing
            repository.saveVerification(
                selfieUri = capturedSelfieUri,
                score = 99,
                badgeTier = "Aura Liveness Verified"
            )
            _verificationStep.value = 4 // Success!
        }
    }

    fun dismissVerification() {
        _isVerifying.value = false
        _verificationStep.value = 1
    }

    fun updateSettings(settings: UserSettings) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateSettings(settings)
        }
    }

    fun updateLocation(lat: Double, lng: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateLocation(lat, lng)
        }
    }

    fun resetSwipes() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.resetAllSwipes()
        }
    }
}
