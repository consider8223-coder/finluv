package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBg
import com.example.ui.theme.RoseDark
import com.example.ui.theme.RosePrimary
import com.example.ui.theme.VerifiedCyan
import com.example.ui.theme.VioletSecondary
import com.example.viewmodel.DiscoveryProfileWithDistance
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RadarView(
    profiles: List<DiscoveryProfileWithDistance>,
    maxRadiusKm: Float,
    selectedProfile: DiscoveryProfileWithDistance?,
    onSelectProfile: (DiscoveryProfileWithDistance) -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "radar")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep"
    )
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg),
        contentAlignment = Alignment.Center
    ) {
        val density = LocalDensity.current
        val widthPx = constraints.maxWidth.toFloat()
        val heightPx = constraints.maxHeight.toFloat()
        val center = Offset(widthPx / 2f, heightPx / 2f)
        val maxRadarRadius = minOf(widthPx, heightPx) * 0.44f

        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw background radial glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        VioletSecondary.copy(alpha = 0.15f),
                        RoseDark.copy(alpha = 0.05f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = maxRadarRadius * 1.2f
                ),
                radius = maxRadarRadius * 1.2f,
                center = center
            )

            // Draw concentric range circles
            val ringCount = 4
            for (i in 1..ringCount) {
                val r = (maxRadarRadius / ringCount) * i
                drawCircle(
                    color = RosePrimary.copy(alpha = 0.2f),
                    radius = r,
                    center = center,
                    style = Stroke(width = 1.5f)
                )
            }

            // Draw crosshairs
            drawLine(
                color = RosePrimary.copy(alpha = 0.12f),
                start = Offset(center.x - maxRadarRadius, center.y),
                end = Offset(center.x + maxRadarRadius, center.y),
                strokeWidth = 1.2f
            )
            drawLine(
                color = RosePrimary.copy(alpha = 0.12f),
                start = Offset(center.x, center.y - maxRadarRadius),
                end = Offset(center.x, center.y + maxRadarRadius),
                strokeWidth = 1.2f
            )

            // Draw radar sweep beam
            val sweepRadian = Math.toRadians(sweepAngle.toDouble())
            val beamEndX = center.x + maxRadarRadius * cos(sweepRadian).toFloat()
            val beamEndY = center.y + maxRadarRadius * sin(sweepRadian).toFloat()
            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(RosePrimary.copy(alpha = 0.8f), Color.Transparent),
                    start = center,
                    end = Offset(beamEndX, beamEndY)
                ),
                start = center,
                end = Offset(beamEndX, beamEndY),
                strokeWidth = 2.5f
            )

            // Draw User's Pulse Center
            drawCircle(
                color = RosePrimary.copy(alpha = 0.3f),
                radius = 16f * pulseScale,
                center = center
            )
            drawCircle(
                color = RosePrimary,
                radius = 7f,
                center = center
            )
            drawCircle(
                color = Color.White,
                radius = 3.5f,
                center = center
            )
        }

        // Render interactive profile blips on top of the canvas
        profiles.forEach { item ->
            val ratio = (item.distanceKm / maxRadiusKm).coerceIn(0.12, 0.95)
            val rPx = (maxRadarRadius * ratio).toFloat()
            val angleRad = Math.toRadians((item.bearing - 90.0)) // adjust bearing to standard coordinate
            val blipX = center.x + rPx * cos(angleRad).toFloat()
            val blipY = center.y + rPx * sin(angleRad).toFloat()

            val isSelected = selectedProfile?.profile?.id == item.profile.id

            val blipSizeDp = if (isSelected) 46.dp else 36.dp
            val blipSizePx = with(density) { blipSizeDp.toPx() }

            Box(
                modifier = Modifier
                    .offset(
                        x = with(density) { (blipX - blipSizePx / 2f).toDp() },
                        y = with(density) { (blipY - blipSizePx / 2f).toDp() }
                    )
                    .size(blipSizeDp)
                    .clickable { onSelectProfile(item) },
                contentAlignment = Alignment.Center
            ) {
                // Outer ring
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(
                            if (isSelected) RosePrimary else VioletSecondary
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    ProfileAvatar(
                        imageUrl = item.profile.getPhotoList().firstOrNull(),
                        name = item.profile.name,
                        size = if (isSelected) 40.dp else 32.dp
                    )
                }

                if (item.profile.isVerified) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF181222)),
                        contentAlignment = Alignment.Center
                    ) {
                        VerifiedBadge(size = BadgeSize.SMALL)
                    }
                }
            }
        }
    }
}
