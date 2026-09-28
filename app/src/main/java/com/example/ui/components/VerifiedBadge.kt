package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldStar
import com.example.ui.theme.VerifiedCyan

enum class BadgeSize {
    SMALL,
    MEDIUM,
    LARGE
}

@Composable
fun VerifiedBadge(
    modifier: Modifier = Modifier,
    size: BadgeSize = BadgeSize.MEDIUM,
    showLabel: Boolean = false,
    label: String = "Verified"
) {
    val iconSize: Dp = when (size) {
        BadgeSize.SMALL -> 14.dp
        BadgeSize.MEDIUM -> 18.dp
        BadgeSize.LARGE -> 26.dp
    }

    if (showLabel) {
        val pillShape = RoundedCornerShape(16.dp)
        Row(
            modifier = modifier
                .clip(pillShape)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            VerifiedCyan.copy(alpha = 0.22f),
                            GoldStar.copy(alpha = 0.22f)
                        )
                    )
                )
                .padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Verified,
                contentDescription = "Verified profile",
                tint = VerifiedCyan,
                modifier = Modifier.size(iconSize)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                color = VerifiedCyan,
                fontSize = if (size == BadgeSize.LARGE) 13.sp else 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    } else {
        Box(
            modifier = modifier
                .size(iconSize + 4.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.95f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Verified,
                contentDescription = "Verified Profile",
                tint = VerifiedCyan,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}
