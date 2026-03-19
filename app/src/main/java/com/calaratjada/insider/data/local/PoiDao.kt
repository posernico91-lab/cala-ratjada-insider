package com.calaratjada.insider.data.local

import androidx.room.*
import com.calaratjada.insider.data.model.Poi
import kotlinx.coroutines.flow.Flow

@Dao
interface PoiDao {
    @Query("SELECT * FROM pois ORDER BY name ASC")
    fun getAllPois(): Flow<List<Poi>>

    @Query("SELECT * FROM pois WHERE category = :category ORDER BY name ASC")
    fun getPoisByCategory(category: String): Flow<List<Poi>>

    @Query("SELECT * FROM pois WHERE isFavorite = 1 ORDER BY name ASC")
    fun getFavorites(): Flow<List<Poi>>

    @Query("SELECT * FROM pois WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchPois(query: String): Flow<List<Poi>>

    @Query("SELECT * FROM pois WHERE id = :id")
    suspend fun getPoiById(id: String): Poi?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pois: List<Poi>)

    @Update
    suspend fun update(poi: Poi)

    @Query("UPDATE pois SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: String, isFavorite: Boolean)

    @Query("SELECT COUNT(*) FROM pois")
    suspend fun getCount(): Int
}
