package com.calaratjada.insider.ui.viewmodel

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calaratjada.insider.data.model.ChatRoom
import com.calaratjada.insider.data.model.Friend
import com.calaratjada.insider.data.model.UserProfile
import com.calaratjada.insider.data.repository.ChatRepository
import com.calaratjada.insider.data.service.AuthService
import com.calaratjada.insider.data.service.CoinManager
import com.calaratjada.insider.data.service.AdManager
import com.calaratjada.insider.data.service.VivoxConnectionState
import com.calaratjada.insider.data.service.VivoxService
import com.google.android.gms.auth.api.signin.GoogleSignIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private val Context.chatDataStore by preferencesDataStore(name = "chat_prefs")

@HiltViewModel
class ChatViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val chatRepository: ChatRepository,
    private val vivoxService: VivoxService,
    val authService: AuthService,
    val coinManager: CoinManager,
    val adManager: AdManager
) : ViewModel() {

    companion object {
        private val KEY_NICKNAME = stringPreferencesKey("nickname")
        private val KEY_LAST_DAILY_BONUS = longPreferencesKey("last_daily_bonus")
    }

    val rooms: StateFlow<List<ChatRoom>> = chatRepository.getAllRooms()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val friends: StateFlow<List<Friend>> = chatRepository.getAllFriends()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val connectionState = vivoxService.connectionState

    val errors = vivoxService.error

    val isSignedIn = authService.isSignedIn

    val profile: StateFlow<UserProfile?> = authService.profile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val coinBalance = coinManager.coinBalance

    private val _nickname = MutableStateFlow<String?>(null)
    val nickname: StateFlow<String?> = _nickname.asStateFlow()

    // Tracks which rooms are unlocked (roomId -> expiresAt)
    private val _unlockedRooms = MutableStateFlow<Map<String, Long>>(emptyMap())
    val unlockedRooms: StateFlow<Map<String, Long>> = _unlockedRooms.asStateFlow()

    init {
        viewModelScope.launch {
            appContext.chatDataStore.data.map { prefs ->
                prefs[KEY_NICKNAME]
            }.collect { saved ->
                _nickname.value = saved
            }
        }
        viewModelScope.launch {
            coinManager.refreshBalance()
            checkDailyBonus()
        }
    }

    private suspend fun checkDailyBonus() {
        val prefs = appContext.chatDataStore.data.first()
        val lastBonus = prefs[KEY_LAST_DAILY_BONUS] ?: 0L
        val oneDayMs = 24 * 60 * 60 * 1000L
        if (System.currentTimeMillis() - lastBonus >= oneDayMs) {
            coinManager.creditCoins(CoinManager.DAILY_BONUS, "Täglicher Bonus")
            appContext.chatDataStore.edit { it[KEY_LAST_DAILY_BONUS] = System.currentTimeMillis() }
        }
    }

    fun getSignInIntent(): Intent = authService.getSignInIntent()

    fun handleSignInResult(data: Intent?) {
        viewModelScope.launch {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            authService.handleSignInResult(task)
            val profile = authService.getOrCreateProfile()
            if (_nickname.value == null && profile.nickname.isNotBlank()) {
                setNickname(profile.nickname)
            }
        }
    }

    fun setNickname(name: String) {
        viewModelScope.launch {
            appContext.chatDataStore.edit { prefs ->
                prefs[KEY_NICKNAME] = name
            }
            _nickname.value = name
            authService.updateNickname(name)
        }
    }

    fun connectAndLogin() {
        val name = _nickname.value ?: return
        viewModelScope.launch {
            if (!vivoxService.isInitialized()) {
                vivoxService.initialize(appContext)
            }
            if (vivoxService.connectionState.value == VivoxConnectionState.DISCONNECTED) {
                vivoxService.connect()
            }
            vivoxService.connectionState
                .filter { it == VivoxConnectionState.CONNECTED }
                .first()
            val username = name.lowercase().replace(" ", "_") + "_" + System.currentTimeMillis() % 10000
            vivoxService.login(name, username)
        }
    }

    fun isRoomUnlocked(roomId: String): Boolean {
        if (roomId == CoinManager.FREE_ROOM_ID) return true
        val expiry = _unlockedRooms.value[roomId] ?: return false
        return System.currentTimeMillis() < expiry
    }

    fun unlockRoom(roomId: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = coinManager.spendCoins(
                CoinManager.COST_UNLOCK_ROOM_24H,
                "spend_unlock",
                "Raum entsperrt (24h)"
            )
            if (success) {
                val expiry = System.currentTimeMillis() + 24 * 60 * 60 * 1000L
                _unlockedRooms.value = _unlockedRooms.value + (roomId to expiry)
            }
            onResult(success)
        }
    }

    fun joinRoom(room: ChatRoom) {
        viewModelScope.launch {
            if (vivoxService.connectionState.value == VivoxConnectionState.IN_CHANNEL) {
                vivoxService.leaveChannel()
            }
            if (vivoxService.connectionState.value.ordinal < VivoxConnectionState.LOGGED_IN.ordinal) {
                connectAndLogin()
                vivoxService.connectionState
                    .filter { it == VivoxConnectionState.LOGGED_IN }
                    .first()
            }
            vivoxService.joinChannel(room.id, room.isVoiceEnabled, true)
        }
    }

    fun leaveChannel() {
        vivoxService.leaveChannel()
    }

    fun removeFriend(friend: Friend) {
        viewModelScope.launch {
            chatRepository.removeFriend(friend)
        }
    }

    fun watchRewardedAd(activity: Activity) {
        adManager.showRewardedAd(activity) { coinsEarned ->
            viewModelScope.launch {
                coinManager.creditCoins(coinsEarned, "Belohnungsvideo angesehen")
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authService.signOut()
        }
    }

    /**
     * DSGVO Art. 17 - Complete data deletion
     */
    fun deleteAllUserData() {
        viewModelScope.launch {
            vivoxService.leaveChannel()
            chatRepository.deleteAllUserData()
            authService.signOut()
            appContext.chatDataStore.edit { it.clear() }
            _nickname.value = null
        }
    }

    override fun onCleared() {
        super.onCleared()
        vivoxService.leaveChannel()
    }
}
