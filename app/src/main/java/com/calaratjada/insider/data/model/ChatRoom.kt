package com.calaratjada.insider.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_rooms")
data class ChatRoom(
    @PrimaryKey val id: String,
    val name: String,
    val description: String = "",
    val channelUri: String,
    val isVoiceEnabled: Boolean = true,
    val createdBy: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val participantCount: Int = 0
)
