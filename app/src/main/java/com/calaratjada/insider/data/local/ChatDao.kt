package com.calaratjada.insider.data.local

import androidx.room.*
import com.calaratjada.insider.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {

    // Chat Rooms
    @Query("SELECT * FROM chat_rooms ORDER BY participantCount DESC")
    fun getAllRooms(): Flow<List<ChatRoom>>

    @Query("SELECT * FROM chat_rooms WHERE id = :roomId")
    suspend fun getRoomById(roomId: String): ChatRoom?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoom(room: ChatRoom)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRooms(rooms: List<ChatRoom>)

    @Delete
    suspend fun deleteRoom(room: ChatRoom)

    // Chat Messages
    @Query("SELECT * FROM chat_messages WHERE roomId = :roomId ORDER BY timestamp ASC")
    fun getMessagesForRoom(roomId: String): Flow<List<ChatMessage>>

    @Query("SELECT * FROM chat_messages WHERE roomId = :roomId ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentMessages(roomId: String, limit: Int = 100): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage)

    @Query("DELETE FROM chat_messages WHERE roomId = :roomId")
    suspend fun deleteMessagesForRoom(roomId: String)

    // Chat Users
    @Query("SELECT * FROM chat_users WHERE isOnline = 1 ORDER BY displayName ASC")
    fun getOnlineUsers(): Flow<List<ChatUser>>

    @Query("SELECT * FROM chat_users WHERE uri = :uri")
    suspend fun getUserByUri(uri: String): ChatUser?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: ChatUser)

    @Query("UPDATE chat_users SET isOnline = 0")
    suspend fun setAllUsersOffline()

    @Query("UPDATE chat_users SET lat = :lat, lng = :lng, lastSeen = :timestamp WHERE uri = :uri")
    suspend fun updateUserLocation(uri: String, lat: Double, lng: Double, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT * FROM chat_users WHERE lat != 0.0 AND lng != 0.0 AND isOnline = 1")
    fun getUsersWithLocation(): Flow<List<ChatUser>>

    // Friends
    @Query("SELECT * FROM friends ORDER BY displayName ASC")
    fun getAllFriends(): Flow<List<Friend>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriend(friend: Friend)

    @Delete
    suspend fun deleteFriend(friend: Friend)

    @Query("SELECT EXISTS(SELECT 1 FROM friends WHERE uri = :uri)")
    suspend fun isFriend(uri: String): Boolean

    // User Profile
    @Query("SELECT * FROM user_profile WHERE id = 'local'")
    fun getProfile(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 'local'")
    suspend fun getProfileSync(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: UserProfile)

    @Query("UPDATE user_profile SET coins = coins + :amount WHERE id = 'local'")
    suspend fun addCoins(amount: Int)

    @Query("UPDATE user_profile SET coins = coins - :amount WHERE id = 'local' AND coins >= :amount")
    suspend fun spendCoins(amount: Int): Int

    @Query("SELECT coins FROM user_profile WHERE id = 'local'")
    suspend fun getCoinBalance(): Int?

    @Query("UPDATE user_profile SET profilePicUri = :uri WHERE id = 'local'")
    suspend fun updateProfilePic(uri: String)

    @Query("UPDATE user_profile SET nickname = :nickname WHERE id = 'local'")
    suspend fun updateNickname(nickname: String)

    // Coin Transactions
    @Query("SELECT * FROM coin_transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<CoinTransaction>>

    @Insert
    suspend fun insertTransaction(transaction: CoinTransaction)

    // Map Events
    @Query("SELECT * FROM map_events WHERE expiresAt > :now ORDER BY createdAt DESC")
    fun getActiveMapEvents(now: Long = System.currentTimeMillis()): Flow<List<MapEvent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMapEvent(mapEvent: MapEvent)

    @Delete
    suspend fun deleteMapEvent(mapEvent: MapEvent)

    // === DSGVO Art. 17: Data Retention & Right to Erasure ===

    /** Delete messages older than cutoff (used by MessageCleanupWorker) */
    @Query("DELETE FROM chat_messages WHERE timestamp < :cutoffTime")
    suspend fun deleteOldMessages(cutoffTime: Long)

    /** Delete a specific message (user request) */
    @Query("DELETE FROM chat_messages WHERE id = :messageId")
    suspend fun deleteMessage(messageId: String)

    /** Delete ALL user data - DSGVO Art. 17 Right to be forgotten */
    @Query("DELETE FROM user_profile")
    suspend fun deleteAllProfiles()

    @Query("DELETE FROM chat_messages")
    suspend fun deleteAllMessages()

    @Query("DELETE FROM friends")
    suspend fun deleteAllFriends()

    @Query("DELETE FROM chat_users")
    suspend fun deleteAllUsers()

    @Query("DELETE FROM coin_transactions")
    suspend fun deleteAllTransactions()

    @Query("DELETE FROM map_events")
    suspend fun deleteAllMapEvents()

    // Room Unlock Tracking (stored in DataStore, not here)
}
