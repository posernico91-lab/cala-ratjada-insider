package com.calaratjada.insider.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.calaratjada.insider.data.model.*

@Database(
    entities = [
        Poi::class, Event::class,
        ChatRoom::class, ChatMessage::class, ChatUser::class, Friend::class,
        UserProfile::class, CoinTransaction::class, MapEvent::class
    ],
    version = 3,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun poiDao(): PoiDao
    abstract fun eventDao(): EventDao
    abstract fun chatDao(): ChatDao
}
