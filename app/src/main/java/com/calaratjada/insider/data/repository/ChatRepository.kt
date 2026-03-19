package com.calaratjada.insider.data.repository

import com.calaratjada.insider.BuildConfig
import com.calaratjada.insider.data.local.ChatDao
import com.calaratjada.insider.data.model.*
import com.calaratjada.insider.data.service.VivoxService
import com.calaratjada.insider.data.service.VivoxTokenGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val chatDao: ChatDao,
    private val vivoxService: VivoxService
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        // Listen for incoming messages and persist them
        scope.launch {
            vivoxService.messages.collect { msg ->
                val message = ChatMessage(
                    roomId = vivoxService.getCurrentChannelName(),
                    senderUri = msg.senderUri,
                    senderDisplayName = msg.senderDisplayName,
                    body = msg.body,
                    isFromMe = msg.senderUri == vivoxService.getUserUri()
                )
                chatDao.insertMessage(message)
            }
        }

        // Listen for participant updates
        scope.launch {
            vivoxService.participants.collect { participants ->
                participants.forEach { p ->
                    chatDao.insertUser(
                        ChatUser(
                            uri = p.uri,
                            displayName = p.displayName,
                            isOnline = true,
                            isSpeaking = p.isSpeaking,
                            isMuted = p.isMuted
                        )
                    )
                }
            }
        }

        // Seed default chat rooms
        scope.launch {
            val existingRooms = chatDao.getRoomById("general")
            if (existingRooms == null) {
                val defaultRooms = listOf(
                    ChatRoom(
                        id = "general",
                        name = "Allgemein",
                        description = "Allgemeiner Chat für alle",
                        channelUri = VivoxTokenGenerator.getChannelUri("general")
                    ),
                    ChatRoom(
                        id = "strand",
                        name = "Strand & Meer",
                        description = "Tipps zu Stränden und Wassersport",
                        channelUri = VivoxTokenGenerator.getChannelUri("strand")
                    ),
                    ChatRoom(
                        id = "gastro",
                        name = "Essen & Trinken",
                        description = "Restaurant-Empfehlungen und Geheimtipps",
                        channelUri = VivoxTokenGenerator.getChannelUri("gastro")
                    ),
                    ChatRoom(
                        id = "nightlife",
                        name = "Nightlife",
                        description = "Bars, Clubs und Abendprogramm",
                        channelUri = VivoxTokenGenerator.getChannelUri("nightlife")
                    ),
                    ChatRoom(
                        id = "hilfe",
                        name = "Hilfe & Info",
                        description = "Fragen und Hilfe rund um Cala Ratjada",
                        channelUri = VivoxTokenGenerator.getChannelUri("hilfe")
                    )
                )
                chatDao.insertRooms(defaultRooms)
            }
        }
    }

    fun getAllRooms(): Flow<List<ChatRoom>> = chatDao.getAllRooms()

    fun getMessagesForRoom(roomId: String): Flow<List<ChatMessage>> =
        chatDao.getMessagesForRoom(roomId)

    fun getOnlineUsers(): Flow<List<ChatUser>> = chatDao.getOnlineUsers()

    fun getUsersWithLocation(): Flow<List<ChatUser>> = chatDao.getUsersWithLocation()

    fun getAllFriends(): Flow<List<Friend>> = chatDao.getAllFriends()

    suspend fun addFriend(friend: Friend) = chatDao.insertFriend(friend)

    suspend fun removeFriend(friend: Friend) = chatDao.deleteFriend(friend)

    suspend fun isFriend(uri: String): Boolean = chatDao.isFriend(uri)

    suspend fun updateUserLocation(uri: String, lat: Double, lng: Double) {
        chatDao.updateUserLocation(uri, lat, lng)
    }

    suspend fun sendMessage(text: String): Boolean {
        val success = vivoxService.sendMessage(text)
        if (success) {
            val message = ChatMessage(
                roomId = vivoxService.getCurrentChannelName(),
                senderUri = vivoxService.getUserUri(),
                senderDisplayName = vivoxService.getCurrentUsername(),
                body = text,
                isFromMe = true
            )
            chatDao.insertMessage(message)
        }
        return success
    }

    suspend fun sendImageMessage(imageUri: String) {
        // Store image message locally (Vivox text channel sends URI reference)
        val msgText = "[IMG]$imageUri"
        vivoxService.sendMessage(msgText)
        val message = ChatMessage(
            roomId = vivoxService.getCurrentChannelName(),
            senderUri = vivoxService.getUserUri(),
            senderDisplayName = vivoxService.getCurrentUsername(),
            body = msgText,
            imageUri = imageUri,
            isFromMe = true
        )
        chatDao.insertMessage(message)
    }

    fun getActiveMapEvents() = chatDao.getActiveMapEvents()

    suspend fun createMapEvent(mapEvent: MapEvent) = chatDao.insertMapEvent(mapEvent)

    suspend fun createRoom(room: ChatRoom) = chatDao.insertRoom(room)

    /**
     * DSGVO Art. 17 - Delete all user data
     */
    suspend fun deleteAllUserData() {
        chatDao.deleteAllProfiles()
        chatDao.deleteAllMessages()
        chatDao.deleteAllFriends()
        chatDao.deleteAllUsers()
        chatDao.deleteAllTransactions()
        chatDao.deleteAllMapEvents()
    }
}
