package com.calaratjada.insider.data.local

import androidx.room.*
import com.calaratjada.insider.data.model.Event
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("SELECT * FROM events ORDER BY date ASC")
    fun getAllEvents(): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE category = :category ORDER BY date ASC")
    fun getEventsByCategory(category: String): Flow<List<Event>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<Event>)

    @Query("SELECT COUNT(*) FROM events")
    suspend fun getCount(): Int
}
