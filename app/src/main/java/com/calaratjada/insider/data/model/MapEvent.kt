package com.calaratjada.insider.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "map_events")
data class MapEvent(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val lat: Double,
    val lng: Double,
    val creatorUri: String,
    val creatorName: String,
    val imageUri: String = "",
    val expiresAt: Long,
    val createdAt: Long = System.currentTimeMillis()
)
