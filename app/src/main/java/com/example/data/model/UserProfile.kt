package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profiles")
data class UserProfile(
    @PrimaryKey val id: String,
    val name: String,
    val age: Int,
    val occupation: String,
    val education: String,
    val city: String,
    val bio: String,
    val latitude: Double,
    val longitude: Double,
    val photosCsv: String, // Comma-separated image descriptors / URLs / gradient tags
    val interestsCsv: String, // Comma-separated tags
    val promptQuestion: String,
    val promptAnswer: String,
    val isVerified: Boolean = false,
    val verificationBadgeText: String = "Liveness Verified",
    val isLiked: Boolean = false,
    val isPassed: Boolean = false,
    val isMatch: Boolean = false,
    val matchTimestamp: Long = 0L,
    val lastActiveText: String = "Active now",
    val compatibilityScore: Int = 92
) {
    fun getPhotoList(): List<String> = photosCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    fun getInterestList(): List<String> = interestsCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() }
}
