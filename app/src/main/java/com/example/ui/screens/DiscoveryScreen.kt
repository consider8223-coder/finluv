package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.UserProfile
import com.example.data.model.UserSettings
import com.example.ui.components.BadgeSize
import com.example.ui.components.DistanceChip
import com.example.ui.components.ProfileAvatar
import com.example.ui.components.VerifiedBadge
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.PassGrey
import com.example.ui.theme.RoseDark
import com.example.ui.theme.RosePrimary
import com.example.ui.theme.SuperlikeBlue
import com.example.ui.theme.VerifiedCyan
import com.example.ui.theme.VioletSecondary
import com.example.viewmodel.DiscoveryProfileWithDistance

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DiscoveryScreen(
    profiles: List<DiscoveryProfileWithDistance>,
    currentSettings: UserSettings,
    mutualMatchProfile: UserProfile?,
    onSwipeRight: (UserProfile) -> Unit,
    onSwipeLeft: (UserProfile) -> Unit,
    onSuperLike: (UserProfile) -> Unit,
    onOpenChat: (String) -> Unit,
    onDismissMatch: () -> Unit,
    onUpdateSettings: (UserSettings) -> Unit,
    onResetSwipes: () -> Unit,
    onOpenSafetyCenter: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showFilterSheet by remember { mutableStateOf(false) }
    var inspectProfile by remember { mutableStateOf<DiscoveryProfileWithDistance?>(null) }

    val currentCandidate = profiles.firstOrNull()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Aura Dating Logo Title
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(RosePrimary, VioletSecondary))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Aura",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onOpenSafetyCenter,
                        modifier = Modifier.testTag("safety_shield_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Shield,
                            contentDescription = "Safety Center",
                            tint = VerifiedCyan
                        )
                    }

                    IconButton(
                        onClick = { showFilterSheet = true },
                        modifier = Modifier.testTag("open_filters_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FilterList,
                            contentDescription = "Filter Preferences",
                            tint = Color.White
                        )
                    }
                }
            }

            // Main Deck Card Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                if (currentCandidate != null) {
                    val profile = currentCandidate.profile
                    val photos = profile.getPhotoList()
                    val pagerState = rememberPagerState(pageCount = { photos.size.coerceAtLeast(1) })

                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(28.dp))
                            .clickable { inspectProfile = currentCandidate }
                            .testTag("discovery_card_${profile.id}"),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkCard),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            // Photos Pager
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier.fillMaxSize()
                            ) { page ->
                                val photoUrl = photos.getOrNull(page)
                                if (photoUrl != null) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(photoUrl)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = profile.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                Brush.verticalGradient(
                                                    listOf(RoseDark, VioletSecondary)
                                                )
                                            )
                                    )
                                }
                            }

                            // Dark Gradient Bottom Shadow for readability
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Black.copy(alpha = 0.25f),
                                                Color.Transparent,
                                                Color.Black.copy(alpha = 0.85f)
                                            ),
                                            startY = 0f,
                                            endY = Float.POSITIVE_INFINITY
                                        )
                                    )
                            )

                            // Top Distance and Verification Chips
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                DistanceChip(distanceText = currentCandidate.formattedDistance)

                                if (profile.isVerified) {
                                    VerifiedBadge(
                                        size = BadgeSize.SMALL,
                                        showLabel = true,
                                        label = "Verified"
                                    )
                                }
                            }

                            // Photo Indicators
                            if (photos.size > 1) {
                                Row(
                                    modifier = Modifier
                                        .align(Alignment.TopCenter)
                                        .padding(top = 10.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    repeat(photos.size) { index ->
                                        Box(
                                            modifier = Modifier
                                                .width(if (pagerState.currentPage == index) 20.dp else 8.dp)
                                                .height(3.dp)
                                                .clip(RoundedCornerShape(2.dp))
                                                .background(
                                                    if (pagerState.currentPage == index) Color.White
                                                    else Color.White.copy(alpha = 0.45f)
                                                )
                                        )
                                    }
                                }
                            }

                            // Bottom Profile Summary Information
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .fillMaxWidth()
                                    .padding(horizontal = 18.dp, vertical = 20.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "${profile.name}, ${profile.age}",
                                        fontSize = 26.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    if (profile.isVerified) {
                                        VerifiedBadge(size = BadgeSize.MEDIUM)
                                    }
                                    Spacer(modifier = Modifier.weight(1f))
                                    IconButton(
                                        onClick = { inspectProfile = currentCandidate },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Info,
                                            contentDescription = "View Profile Info",
                                            tint = Color.White.copy(alpha = 0.8f)
                                        )
                                    }
                                }

                                if (profile.occupation.isNotBlank()) {
                                    Text(
                                        text = "${profile.occupation} • ${profile.city}",
                                        color = Color.LightGray,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Interests chips
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    profile.getInterestList().take(3).forEach { interest ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(Color.White.copy(alpha = 0.15f))
                                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                text = interest,
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Empty state when all profiles have been swiped
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkCard)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(RosePrimary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.LocationOn,
                                    contentDescription = null,
                                    tint = RosePrimary,
                                    modifier = Modifier.size(36.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            Text(
                                text = "You're All Caught Up!",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "No more nearby singles within ${currentSettings.maxDistanceKm} km. Expand your distance filter or check back later.",
                                fontSize = 14.sp,
                                color = Color.LightGray,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                lineHeight = 20.sp
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = { showFilterSheet = true },
                                colors = ButtonDefaults.buttonColors(containerColor = RosePrimary),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.testTag("adjust_filters_button")
                            ) {
                                Icon(imageVector = Icons.Filled.FilterList, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Adjust Discovery Filters")
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = onResetSwipes,
                                colors = ButtonDefaults.buttonColors(containerColor = DarkSurface),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.testTag("reset_profiles_button")
                            ) {
                                Icon(imageVector = Icons.Filled.Refresh, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Revisit Passed Profiles")
                            }
                        }
                    }
                }
            }

            // Bottom Action Controls (Pass, Superlike, Like)
            if (currentCandidate != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FloatingActionButton(
                        onClick = { onSwipeLeft(currentCandidate.profile) },
                        containerColor = DarkCard,
                        contentColor = PassGrey,
                        shape = CircleShape,
                        elevation = FloatingActionButtonDefaults.elevation(6.dp),
                        modifier = Modifier
                            .size(58.dp)
                            .testTag("discovery_pass_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Pass",
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    FloatingActionButton(
                        onClick = { onSuperLike(currentCandidate.profile) },
                        containerColor = DarkCard,
                        contentColor = SuperlikeBlue,
                        shape = CircleShape,
                        elevation = FloatingActionButtonDefaults.elevation(6.dp),
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("discovery_superlike_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Super Like",
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    FloatingActionButton(
                        onClick = { onSwipeRight(currentCandidate.profile) },
                        containerColor = RosePrimary,
                        contentColor = Color.White,
                        shape = CircleShape,
                        elevation = FloatingActionButtonDefaults.elevation(8.dp),
                        modifier = Modifier
                            .size(64.dp)
                            .testTag("discovery_like_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Like",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }

        // Mutual Match Celebration Modal
        if (mutualMatchProfile != null) {
            MutualMatchDialog(
                matchProfile = mutualMatchProfile,
                onSendMessage = {
                    val id = mutualMatchProfile.id
                    onDismissMatch()
                    onOpenChat(id)
                },
                onKeepSwiping = onDismissMatch
            )
        }

        // Profile Inspector Dialog
        inspectProfile?.let { selected ->
            ProfileDetailDialog(
                profile = selected.profile,
                distanceText = selected.formattedDistance,
                onDismiss = { inspectProfile = null },
                onLike = {
                    inspectProfile = null
                    onSwipeRight(selected.profile)
                },
                onPass = {
                    inspectProfile = null
                    onSwipeLeft(selected.profile)
                },
                onSuperLike = {
                    inspectProfile = null
                    onSuperLike(selected.profile)
                }
            )
        }

        // Filter Sheet
        if (showFilterSheet) {
            val sheetState = rememberModalBottomSheetState()
            var tempMaxDistance by remember { mutableStateOf(currentSettings.maxDistanceKm.toFloat()) }
            var tempVerifiedOnly by remember { mutableStateOf(currentSettings.verifiedOnly) }
            var tempAgeRange by remember {
                mutableStateOf(currentSettings.minAge.toFloat()..currentSettings.maxAge.toFloat())
            }

            ModalBottomSheet(
                onDismissRequest = { showFilterSheet = false },
                sheetState = sheetState,
                containerColor = DarkSurface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Discovery Preferences",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Maximum Distance Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Maximum Distance", color = Color.White, fontWeight = FontWeight.Medium)
                        Text(
                            text = "${tempMaxDistance.toInt()} km",
                            color = RosePrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Slider(
                        value = tempMaxDistance,
                        onValueChange = { tempMaxDistance = it },
                        valueRange = 5f..100f,
                        colors = SliderDefaults.colors(
                            thumbColor = RosePrimary,
                            activeTrackColor = RosePrimary
                        ),
                        modifier = Modifier.testTag("distance_slider")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Age Range Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Age Preference", color = Color.White, fontWeight = FontWeight.Medium)
                        Text(
                            text = "${tempAgeRange.start.toInt()} - ${tempAgeRange.endInclusive.toInt()}",
                            color = RosePrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    RangeSlider(
                        value = tempAgeRange,
                        onValueChange = { tempAgeRange = it },
                        valueRange = 18f..65f,
                        colors = SliderDefaults.colors(
                            thumbColor = RosePrimary,
                            activeTrackColor = RosePrimary
                        ),
                        modifier = Modifier.testTag("age_range_slider")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Verified Profiles Only Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            VerifiedBadge(size = BadgeSize.MEDIUM)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Verified Profiles Only",
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Only view liveness & selfie verified singles",
                                    color = Color.LightGray,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Switch(
                            checked = tempVerifiedOnly,
                            onCheckedChange = { tempVerifiedOnly = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = VerifiedCyan
                            ),
                            modifier = Modifier.testTag("verified_only_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Button(
                        onClick = {
                            onUpdateSettings(
                                currentSettings.copy(
                                    maxDistanceKm = tempMaxDistance.toInt(),
                                    minAge = tempAgeRange.start.toInt(),
                                    maxAge = tempAgeRange.endInclusive.toInt(),
                                    verifiedOnly = tempVerifiedOnly
                                )
                            )
                            showFilterSheet = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RosePrimary),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("apply_filters_button")
                    ) {
                        Text("Apply Preferences", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun MutualMatchDialog(
    matchProfile: UserProfile,
    onSendMessage: () -> Unit,
    onKeepSwiping: () -> Unit
) {
    Dialog(onDismissRequest = onKeepSwiping) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "It's a Match! 🎉",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = RosePrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "You and ${matchProfile.name} liked each other",
                    fontSize = 14.sp,
                    color = Color.LightGray
                )

                Spacer(modifier = Modifier.height(22.dp))

                // Intertwined Avatar circles
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ProfileAvatar(
                        imageUrl = null,
                        name = "Alex",
                        size = 80.dp
                    )
                    Spacer(modifier = Modifier.width((-16).dp))
                    ProfileAvatar(
                        imageUrl = matchProfile.getPhotoList().firstOrNull(),
                        name = matchProfile.name,
                        size = 80.dp
                    )
                }

                Spacer(modifier = Modifier.height(26.dp))

                Button(
                    onClick = onSendMessage,
                    colors = ButtonDefaults.buttonColors(containerColor = RosePrimary),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("match_send_message_button")
                ) {
                    Text(
                        text = "Say Hello to ${matchProfile.name.split(" ").first()}",
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onKeepSwiping,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    modifier = Modifier.testTag("match_keep_swiping_button")
                ) {
                    Text(text = "Keep Swiping", color = Color.White.copy(alpha = 0.7f))
                }
            }
        }
    }
}
