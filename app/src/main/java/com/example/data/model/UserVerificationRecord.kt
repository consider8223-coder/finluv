package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "verification_record")
data class UserVerificationRecord(
    @PrimaryKey val id: Int = 1,
    val isVerified: Boolean = false,
    val verifiedTimestamp: Long = 0L,
    val selfieUri: String? = null,
    val challengePose: String = "Slight smile with right head tilt",
    val biometricScore: Int = 0,
    val badgeTier: String = "Gold Shield Verified"
)
