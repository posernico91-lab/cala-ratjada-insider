package com.calaratjada.insider.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_users")
data class ChatUser(
    @PrimaryKey val uri: String,
    val displayName: String,
    val nickname: String = "",
    val profilePicUri: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val lastSeen: Long = System.currentTimeMillis(),
    val isOnline: Boolean = false,
    val isSpeaking: Boolean = false,
    val isMuted: Boolean = false
)
