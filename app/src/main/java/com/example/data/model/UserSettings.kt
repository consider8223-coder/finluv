package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey val id: Int = 1,
    val myName: String = "Alex Rivera",
    val myAge: Int = 26,
    val myOccupation: String = "UI Designer & Photographer",
    val myBio: String = "Coffee enthusiast, vintage camera collector, and weekend trail hiker. Looking for meaningful conversations and spontaneous adventures.",
    val maxDistanceKm: Int = 30,
    val minAge: Int = 21,
    val maxAge: Int = 36,
    val verifiedOnly: Boolean = false,
    val userLatitude: Double = 37.7749, // San Francisco default GPS fallback
    val userLongitude: Double = -122.4194,
    val isGpsActive: Boolean = true
)
