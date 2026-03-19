package com.calaratjada.insider.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: String = "local",
    val googleUid: String = "",
    val email: String = "",
    val displayName: String = "",
    val nickname: String = "",
    val profilePicUri: String = "",
    val coins: Int = 10,
    val isPremium: Boolean = false,
    val premiumUntil: Long = 0,
    val createdAt: Long = System.currentTimeMillis()
)
