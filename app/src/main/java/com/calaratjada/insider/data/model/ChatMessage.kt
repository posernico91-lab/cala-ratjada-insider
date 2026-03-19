package com.calaratjada.insider.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val roomId: String,
    val senderUri: String,
    val senderDisplayName: String,
    val body: String,
    val imageUri: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isFromMe: Boolean = false
)
