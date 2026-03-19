package com.calaratjada.insider.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pois")
data class Poi(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val description: String,
    val lat: Double,
    val lng: Double,
    val imageUrl: String,
    val rating: Float = 0f,
    val address: String = "",
    val website: String = "",
    val phone: String = "",
    val isFavorite: Boolean = false
)
