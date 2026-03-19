package com.calaratjada.insider.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calaratjada.insider.data.model.ChatMessage
import com.calaratjada.insider.data.repository.ChatRepository
import com.calaratjada.insider.data.service.CoinManager
import com.calaratjada.insider.data.service.VivoxParticipant
import com.calaratjada.insider.data.service.VivoxService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatRoomViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val chatRepository: ChatRepository,
    private val vivoxService: VivoxService,
    private val coinManager: CoinManager
) : ViewModel() {

    val roomId: String = savedStateHandle.get<String>("roomId") ?: ""

    val messages: StateFlow<List<ChatMessage>> = chatRepository.getMessagesForRoom(roomId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val participants: StateFlow<List<VivoxParticipant>> = vivoxService.participants

    val coinBalance = coinManager.coinBalance

    private val _isMicMuted = MutableStateFlow(true)
    val isMicMuted: StateFlow<Boolean> = _isMicMuted.asStateFlow()

    private val _messageText = MutableStateFlow("")
    val messageText: StateFlow<String> = _messageText.asStateFlow()

    private val _insufficientCoins = MutableSharedFlow<String>(extraBufferCapacity = 5)
    val insufficientCoins = _insufficientCoins.asSharedFlow()

    // Rate Limiting: max 5 messages per 10 seconds
    private val messageTimestamps = mutableListOf<Long>()
    private val maxMessages = 5
    private val rateLimitWindowMs = 10_000L
    private val maxMessageLength = 2000

    fun onMessageTextChanged(text: String) {
        // Enforce max length on input
        _messageText.value = text.take(maxMessageLength)
    }

    /** Sanitize user input: escape HTML entities and reject injection patterns */
    private fun sanitizeMessage(text: String): String? {
        if (text.isBlank() || text.length > maxMessageLength) return null
        val sanitized = text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;")
        // Reject known injection patterns
        val injectionPatterns = listOf(
            Regex("<script", RegexOption.IGNORE_CASE),
            Regex("<iframe", RegexOption.IGNORE_CASE),
            Regex("javascript:", RegexOption.IGNORE_CASE),
            Regex("on\\w+=", RegexOption.IGNORE_CASE)
        )
        if (injectionPatterns.any { it.containsMatchIn(sanitized) }) return null
        return sanitized
    }

    /** Rate limit check: max [maxMessages] in [rateLimitWindowMs] */
    private fun isRateLimited(): Boolean {
        val now = System.currentTimeMillis()
        messageTimestamps.removeAll { now - it > rateLimitWindowMs }
        return if (messageTimestamps.size >= maxMessages) {
            true
        } else {
            messageTimestamps.add(now)
            false
        }
    }

    fun sendMessage() {
        val text = _messageText.value.trim()
        if (text.isEmpty()) return
        if (isRateLimited()) {
            viewModelScope.launch {
                _insufficientCoins.emit("Zu schnell! Warte einen Moment.")
            }
            return
        }
        val sanitized = sanitizeMessage(text)
        if (sanitized == null) {
            viewModelScope.launch {
                _insufficientCoins.emit("Ungültige Nachricht")
            }
            return
        }
        viewModelScope.launch {
            val success = coinManager.spendCoins(
                CoinManager.COST_SEND_MESSAGE,
                "spend_message",
                "Nachricht gesendet"
            )
            if (success) {
                chatRepository.sendMessage(sanitized)
                _messageText.value = ""
            } else {
                _insufficientCoins.emit("Du brauchst ${CoinManager.COST_SEND_MESSAGE} Coin(s) zum Senden")
            }
        }
    }

    /** Validate image URI: only allow content:// scheme from device gallery */
    private fun isValidImageUri(uri: Uri): Boolean {
        return uri.scheme == "content"
    }

    fun sendImage(imageUri: Uri) {
        if (!isValidImageUri(imageUri)) {
            viewModelScope.launch {
                _insufficientCoins.emit("Ungültiges Bildformat")
            }
            return
        }
        if (isRateLimited()) {
            viewModelScope.launch {
                _insufficientCoins.emit("Zu schnell! Warte einen Moment.")
            }
            return
        }
        viewModelScope.launch {
            val success = coinManager.spendCoins(
                CoinManager.COST_SEND_IMAGE,
                "spend_image",
                "Bild gesendet"
            )
            if (success) {
                chatRepository.sendImageMessage(imageUri.toString())
            } else {
                _insufficientCoins.emit("Du brauchst ${CoinManager.COST_SEND_IMAGE} Coins für ein Bild")
            }
        }
    }

    fun toggleMic() {
        _isMicMuted.value = !_isMicMuted.value
        vivoxService.setMicMute(_isMicMuted.value)
    }

    fun addFriend(participant: VivoxParticipant) {
        viewModelScope.launch {
            chatRepository.addFriend(
                com.calaratjada.insider.data.model.Friend(
                    uri = participant.uri,
                    displayName = participant.displayName,
                    isOnline = true
                )
            )
        }
    }
}
