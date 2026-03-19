package com.calaratjada.insider.data.repository

import com.calaratjada.insider.data.SeedData
import com.calaratjada.insider.data.local.EventDao
import com.calaratjada.insider.data.model.Event
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor(
    private val eventDao: EventDao
) {
    fun getAllEvents(): Flow<List<Event>> = eventDao.getAllEvents()

    fun getEventsByCategory(category: String): Flow<List<Event>> =
        eventDao.getEventsByCategory(category)

    suspend fun seedIfEmpty() {
        if (eventDao.getCount() == 0) {
            eventDao.insertAll(SeedData.events)
        }
    }
}
