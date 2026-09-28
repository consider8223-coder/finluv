package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.UserProfile
import com.example.data.model.UserSettings
import com.example.ui.components.BadgeSize
import com.example.ui.components.DistanceChip
import com.example.ui.components.ProfileAvatar
import com.example.ui.components.RadarView
import com.example.ui.components.VerifiedBadge
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkCard
import com.example.ui.theme.RoseDark
import com.example.ui.theme.RosePrimary
import com.example.ui.theme.VerifiedCyan
import com.example.util.LocationHelper
import com.example.viewmodel.DiscoveryProfileWithDistance

@Composable
fun RadarScreen(
    profiles: List<DiscoveryProfileWithDistance>,
    currentSettings: UserSettings,
    onLikeProfile: (UserProfile) -> Unit,
    onOpenProfileDetails: (DiscoveryProfileWithDistance) -> Unit,
    onUpdateLocation: (Double, Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedBlip by remember { mutableStateOf<DiscoveryProfileWithDistance?>(null) }
    var locationStatusMessage by remember { mutableStateOf<String?>(null) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineGranted || coarseGranted) {
            val deviceCoords = LocationHelper.getDeviceLocation(context)
            if (deviceCoords != null) {
                onUpdateLocation(deviceCoords.first, deviceCoords.second)
                locationStatusMessage = "GPS Updated: ${String.format("%.4f", deviceCoords.first)}, ${String.format("%.4f", deviceCoords.second)}"
            } else {
                locationStatusMessage = "Using active location coordinates"
            }
        } else {
            locationStatusMessage = "Permission denied. Using default city coordinates."
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Radar Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.LocationSearching,
                            contentDescription = null,
                            tint = RosePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Live Proximity Radar",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Text(
                        text = "${profiles.size} singles nearby within ${currentSettings.maxDistanceKm} km",
                        fontSize = 12.sp,
                        color = Color.LightGray
                    )
                }

                // GPS sync button
                IconButton(
                    onClick = {
                        val hasFine = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED

                        if (hasFine) {
                            val coords = LocationHelper.getDeviceLocation(context)
                            if (coords != null) {
                                onUpdateLocation(coords.first, coords.second)
                                locationStatusMessage = "Refreshed GPS location"
                            } else {
                                locationStatusMessage = "Refreshed live radius"
                            }
                        } else {
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(DarkCard)
                        .testTag("refresh_gps_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.GpsFixed,
                        contentDescription = "Sync GPS",
                        tint = RosePrimary
                    )
                }
            }

            if (locationStatusMessage != null) {
                Text(
                    text = locationStatusMessage!!,
                    color = VerifiedCyan,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 2.dp)
                )
            }

            // Interactive Radar Canvas
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                RadarView(
                    profiles = profiles,
                    maxRadiusKm = currentSettings.maxDistanceKm.toFloat(),
                    selectedProfile = selectedBlip,
                    onSelectProfile = { blip ->
                        selectedBlip = blip
                    }
                )
            }
        }

        // Floating Quick Preview Card when a radar blip is selected
        AnimatedVisibility(
            visible = selectedBlip != null,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            selectedBlip?.let { item ->
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    elevation = CardDefaults.cardElevation(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenProfileDetails(item) }
                        .testTag("radar_preview_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ProfileAvatar(
                                imageUrl = item.profile.getPhotoList().firstOrNull(),
                                name = item.profile.name,
                                size = 56.dp,
                                showOnlineIndicator = true
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${item.profile.name}, ${item.profile.age}",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    if (item.profile.isVerified) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        VerifiedBadge(size = BadgeSize.SMALL)
                                    }
                                }

                                Text(
                                    text = item.profile.occupation,
                                    fontSize = 12.sp,
                                    color = Color.LightGray
                                )

                                Spacer(modifier = Modifier.height(4.dp))
                                DistanceChip(distanceText = item.formattedDistance)
                            }

                            IconButton(
                                onClick = { selectedBlip = null },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Dismiss",
                                    tint = Color.LightGray
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = item.profile.bio,
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            maxLines = 2
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { onOpenProfileDetails(item) },
                                colors = ButtonDefaults.buttonColors(containerColor = DarkBg),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("radar_view_profile_button")
                            ) {
                                Text("View Profile", color = Color.White)
                            }

                            Button(
                                onClick = {
                                    val profile = item.profile
                                    selectedBlip = null
                                    onLikeProfile(profile)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = RosePrimary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("radar_like_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Favorite,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Match", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}
