package com.calaratjada.insider.data.repository

import com.calaratjada.insider.data.SeedData
import com.calaratjada.insider.data.local.PoiDao
import com.calaratjada.insider.data.model.Poi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PoiRepository @Inject constructor(
    private val poiDao: PoiDao
) {
    fun getAllPois(): Flow<List<Poi>> = poiDao.getAllPois()

    fun getPoisByCategory(category: String): Flow<List<Poi>> =
        poiDao.getPoisByCategory(category)

    fun getFavorites(): Flow<List<Poi>> = poiDao.getFavorites()

    fun searchPois(query: String): Flow<List<Poi>> = poiDao.searchPois(query)

    suspend fun getPoiById(id: String): Poi? = poiDao.getPoiById(id)

    suspend fun toggleFavorite(id: String, isFavorite: Boolean) {
        poiDao.setFavorite(id, isFavorite)
    }

    suspend fun seedIfEmpty() {
        if (poiDao.getCount() == 0) {
            poiDao.insertAll(SeedData.pois)
        }
    }
}
