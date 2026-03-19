package com.calaratjada.insider.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class Event(
    @PrimaryKey val id: String,
    val title: String,
    val date: String,
    val category: String,
    val description: String,
    val location: String
)
