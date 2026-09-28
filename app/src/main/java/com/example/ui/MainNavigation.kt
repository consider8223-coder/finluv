package com.example.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.LocationSearching
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.ChatDetailScreen
import com.example.ui.screens.DiscoveryScreen
import com.example.ui.screens.MatchesListScreen
import com.example.ui.screens.MyProfileScreen
import com.example.ui.screens.ProfileDetailDialog
import com.example.ui.screens.RadarScreen
import com.example.ui.screens.SafetyCenterDialog
import com.example.ui.screens.VerificationScreen
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.RoseDark
import com.example.ui.theme.RosePrimary
import com.example.viewmodel.DatingViewModel
import com.example.viewmodel.DiscoveryProfileWithDistance

enum class MainTab {
    DISCOVERY,
    RADAR,
    MATCHES,
    PROFILE
}

@Composable
fun MainApp(
    viewModel: DatingViewModel = viewModel()
) {
    var currentTab by remember { mutableStateOf(MainTab.DISCOVERY) }
    var showSafetyDialog by remember { mutableStateOf(false) }
    var selectedProfileDetail by remember { mutableStateOf<DiscoveryProfileWithDistance?>(null) }

    val discoveryProfiles by viewModel.discoveryProfiles.collectAsStateWithLifecycle()
    val matchesWithLastMsg by viewModel.matchesWithLastMessage.collectAsStateWithLifecycle()
    val settings by viewModel.settingsState.collectAsStateWithLifecycle()
    val verificationRecord by viewModel.verificationState.collectAsStateWithLifecycle()
    val mutualMatch by viewModel.mutualMatchCelebration.collectAsStateWithLifecycle()

    val activeChatId by viewModel.activeChatProfileId.collectAsStateWithLifecycle()
    val chatMessages by viewModel.activeChatMessages.collectAsStateWithLifecycle()
    val isPartnerTyping by viewModel.isPartnerTyping.collectAsStateWithLifecycle()

    val isVerifying by viewModel.isVerifying.collectAsStateWithLifecycle()
    val verificationStep by viewModel.verificationStep.collectAsStateWithLifecycle()

    val totalUnreadCount = matchesWithLastMsg.sumOf { it.unreadCount }

    // If active chat is open, show ChatDetailScreen full screen
    val activeChatProfile = matchesWithLastMsg.firstOrNull { it.profile.id == activeChatId }?.profile
        ?: discoveryProfiles.firstOrNull { it.profile.id == activeChatId }?.profile

    if (activeChatId != null && activeChatProfile != null) {
        ChatDetailScreen(
            profile = activeChatProfile,
            messages = chatMessages,
            isPartnerTyping = isPartnerTyping,
            onSendMessage = { text, type, payload ->
                viewModel.sendMessage(text, type, payload)
            },
            onBack = { viewModel.closeChat() },
            onOpenSafetyCenter = { showSafetyDialog = true }
        )
    } else {
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                NavigationBar(
                    containerColor = DarkSurface,
                    contentColor = Color.White,
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .testTag("main_navigation_bar")
                ) {
                    NavigationBarItem(
                        selected = currentTab == MainTab.DISCOVERY,
                        onClick = { currentTab = MainTab.DISCOVERY },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == MainTab.DISCOVERY) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Discovery"
                            )
                        },
                        label = { Text("Discover") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = RosePrimary,
                            indicatorColor = RosePrimary,
                            unselectedIconColor = Color.LightGray,
                            unselectedTextColor = Color.LightGray
                        ),
                        modifier = Modifier.testTag("nav_discovery_tab")
                    )

                    NavigationBarItem(
                        selected = currentTab == MainTab.RADAR,
                        onClick = { currentTab = MainTab.RADAR },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == MainTab.RADAR) Icons.Filled.LocationSearching else Icons.Outlined.LocationSearching,
                                contentDescription = "Radar"
                            )
                        },
                        label = { Text("Radar") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = RosePrimary,
                            indicatorColor = RosePrimary,
                            unselectedIconColor = Color.LightGray,
                            unselectedTextColor = Color.LightGray
                        ),
                        modifier = Modifier.testTag("nav_radar_tab")
                    )

                    NavigationBarItem(
                        selected = currentTab == MainTab.MATCHES,
                        onClick = { currentTab = MainTab.MATCHES },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (totalUnreadCount > 0) {
                                        Badge(containerColor = RosePrimary) {
                                            Text(totalUnreadCount.toString())
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (currentTab == MainTab.MATCHES) Icons.Filled.ChatBubble else Icons.Outlined.ChatBubbleOutline,
                                    contentDescription = "Chats"
                                )
                            }
                        },
                        label = { Text("Matches") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = RosePrimary,
                            indicatorColor = RosePrimary,
                            unselectedIconColor = Color.LightGray,
                            unselectedTextColor = Color.LightGray
                        ),
                        modifier = Modifier.testTag("nav_matches_tab")
                    )

                    NavigationBarItem(
                        selected = currentTab == MainTab.PROFILE,
                        onClick = { currentTab = MainTab.PROFILE },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == MainTab.PROFILE) Icons.Filled.Person else Icons.Outlined.PersonOutline,
                                contentDescription = "Profile"
                            )
                        },
                        label = { Text("Profile") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = RosePrimary,
                            indicatorColor = RosePrimary,
                            unselectedIconColor = Color.LightGray,
                            unselectedTextColor = Color.LightGray
                        ),
                        modifier = Modifier.testTag("nav_profile_tab")
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(DarkBg)
            ) {
                Crossfade(targetState = currentTab, label = "tab_crossfade") { tab ->
                    when (tab) {
                        MainTab.DISCOVERY -> {
                            DiscoveryScreen(
                                profiles = discoveryProfiles,
                                currentSettings = settings,
                                mutualMatchProfile = mutualMatch,
                                onSwipeRight = { viewModel.onSwipeRight(it) },
                                onSwipeLeft = { viewModel.onSwipeLeft(it) },
                                onSuperLike = { viewModel.onSuperLike(it) },
                                onOpenChat = { matchId ->
                                    viewModel.openChat(matchId)
                                },
                                onDismissMatch = { viewModel.dismissMatchCelebration() },
                                onUpdateSettings = { viewModel.updateSettings(it) },
                                onResetSwipes = { viewModel.resetSwipes() },
                                onOpenSafetyCenter = { showSafetyDialog = true }
                            )
                        }
                        MainTab.RADAR -> {
                            RadarScreen(
                                profiles = discoveryProfiles,
                                currentSettings = settings,
                                onLikeProfile = { viewModel.onSwipeRight(it) },
                                onOpenProfileDetails = { selectedProfileDetail = it },
                                onUpdateLocation = { lat, lng ->
                                    viewModel.updateLocation(lat, lng)
                                }
                            )
                        }
                        MainTab.MATCHES -> {
                            MatchesListScreen(
                                matches = matchesWithLastMsg,
                                onSelectMatch = { profileId ->
                                    viewModel.openChat(profileId)
                                }
                            )
                        }
                        MainTab.PROFILE -> {
                            MyProfileScreen(
                                settings = settings,
                                verificationRecord = verificationRecord,
                                onStartVerification = { viewModel.startVerification() },
                                onOpenSafetyCenter = { showSafetyDialog = true },
                                onUpdateSettings = { viewModel.updateSettings(it) },
                                onUpdateLocation = { lat, lng ->
                                    viewModel.updateLocation(lat, lng)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Full Profile Detail Dialog
    selectedProfileDetail?.let { item ->
        ProfileDetailDialog(
            profile = item.profile,
            distanceText = item.formattedDistance,
            onDismiss = { selectedProfileDetail = null },
            onLike = {
                selectedProfileDetail = null
                viewModel.onSwipeRight(item.profile)
            },
            onPass = {
                selectedProfileDetail = null
                viewModel.onSwipeLeft(item.profile)
            },
            onSuperLike = {
                selectedProfileDetail = null
                viewModel.onSuperLike(item.profile)
            }
        )
    }

    // Safety Center Dialog
    if (showSafetyDialog) {
        SafetyCenterDialog(
            onDismiss = { showSafetyDialog = false }
        )
    }

    // Secure Profile Verification Flow
    if (isVerifying) {
        VerificationScreen(
            currentStep = verificationStep,
            onStartPose = { viewModel.proceedToVerificationCamera() },
            onCompleteSelfie = { uri ->
                viewModel.completeVerificationCapture(uri)
            },
            onDismiss = { viewModel.dismissVerification() }
        )
    }
}
