package com.calaratjada.insider.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "coin_transactions")
data class CoinTransaction(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val type: String,
    val amount: Int,
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)
