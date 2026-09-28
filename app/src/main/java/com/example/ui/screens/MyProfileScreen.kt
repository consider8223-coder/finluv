package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserSettings
import com.example.data.model.UserVerificationRecord
import com.example.ui.components.BadgeSize
import com.example.ui.components.ProfileAvatar
import com.example.ui.components.VerifiedBadge
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.RoseDark
import com.example.ui.theme.RosePrimary
import com.example.ui.theme.VerifiedCyan
import com.example.ui.theme.VioletSecondary

@Composable
fun MyProfileScreen(
    settings: UserSettings,
    verificationRecord: UserVerificationRecord?,
    onStartVerification: () -> Unit,
    onOpenSafetyCenter: () -> Unit,
    onUpdateSettings: (UserSettings) -> Unit,
    onUpdateLocation: (Double, Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val isVerified = verificationRecord?.isVerified == true

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // User Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfileAvatar(
                imageUrl = null,
                name = settings.myName,
                size = 76.dp
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${settings.myName}, ${settings.myAge}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    if (isVerified) {
                        Spacer(modifier = Modifier.width(6.dp))
                        VerifiedBadge(size = BadgeSize.MEDIUM)
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = settings.myOccupation,
                    fontSize = 13.sp,
                    color = Color.LightGray
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = RosePrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Active GPS: SF Bay Area",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Profile Verification Banner
        if (isVerified) {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("verified_profile_card")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    VerifiedBadge(size = BadgeSize.LARGE)
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Identity & Liveness Verified",
                            fontWeight = FontWeight.Bold,
                            color = VerifiedCyan,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Confidence Score: ${verificationRecord?.biometricScore ?: 99}% • Gold Shield",
                            color = Color.LightGray,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        } else {
            // Call To Action to get verified!
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                RoseDark.copy(alpha = 0.6f),
                                VioletSecondary.copy(alpha = 0.6f)
                            )
                        )
                    )
                    .clickable { onStartVerification() }
                    .testTag("get_verified_banner")
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.VerifiedUser,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Get Verified Badge",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Take a quick 15-second selfie to unlock full trust and 3x more matches.",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp
                        )
                    }

                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = "Verify",
                        tint = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Safety Center Row
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpenSafetyCenter() }
                .testTag("open_safety_center_row")
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(VerifiedCyan.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Shield,
                        contentDescription = null,
                        tint = VerifiedCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Safety Center & Emergency Check-In",
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Real-time date itinerary, location share, and tips",
                        color = Color.LightGray,
                        fontSize = 12.sp
                    )
                }
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Location & Discovery Settings
        Text(
            text = "Discovery Radius & Location",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Max Distance", color = Color.White, fontWeight = FontWeight.Medium)
                    Text(
                        "${settings.maxDistanceKm} km",
                        color = RosePrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Slider(
                    value = settings.maxDistanceKm.toFloat(),
                    onValueChange = {
                        onUpdateSettings(settings.copy(maxDistanceKm = it.toInt()))
                    },
                    valueRange = 5f..100f,
                    colors = SliderDefaults.colors(
                        thumbColor = RosePrimary,
                        activeTrackColor = RosePrimary
                    ),
                    modifier = Modifier.testTag("profile_distance_slider")
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Verified Only Mode", color = Color.White, fontWeight = FontWeight.Medium)
                        Text(
                            "Filter discovery to verified singles only",
                            color = Color.LightGray,
                            fontSize = 11.sp
                        )
                    }

                    Switch(
                        checked = settings.verifiedOnly,
                        onCheckedChange = {
                            onUpdateSettings(settings.copy(verifiedOnly = it))
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = VerifiedCyan
                        ),
                        modifier = Modifier.testTag("profile_verified_only_switch")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Location Simulation presets (allows switching virtual GPS to test nearby calculation)
        Text(
            text = "Simulate Location / Neighborhood",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 14.sp
        )
        Text(
            text = "Test real-time Haversine distance & Radar updates from different locations",
            color = Color.Gray,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        val locations = listOf(
            Triple("SF Downtown (0.9km Elena)", 37.7749, -122.4194),
            Triple("Mission District (2.1km Sofia)", 37.7599, -122.4148),
            Triple("Oakland Uptown (4.2km Maya)", 37.8044, -122.2711),
            Triple("Berkeley Hills (8.2km Zoe)", 37.8715, -122.2730)
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            locations.forEach { (name, lat, lng) ->
                val isSelected = Math.abs(settings.userLatitude - lat) < 0.005 &&
                                 Math.abs(settings.userLongitude - lng) < 0.005
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) RosePrimary.copy(alpha = 0.2f) else DarkCard)
                        .clickable { onUpdateLocation(lat, lng) }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = name,
                            color = if (isSelected) RosePrimary else Color.White,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                        if (isSelected) {
                            Text("Active", color = RosePrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
