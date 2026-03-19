package com.calaratjada.insider.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "friends")
data class Friend(
    @PrimaryKey val uri: String,
    val displayName: String,
    val addedAt: Long = System.currentTimeMillis(),
    val isOnline: Boolean = false
)
