package com.calaratjada.insider.data.service

import android.content.Context
import android.util.Log
import com.calaratjada.insider.BuildConfig
import com.vivox.sdk.JniHelpers
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

data class VivoxTextMessage(
    val body: String,
    val senderUri: String,
    val senderDisplayName: String,
    val language: String = "en"
)

data class VivoxParticipant(
    val uri: String,
    val displayName: String,
    val isSpeaking: Boolean = false,
    val isMuted: Boolean = false,
    val volume: Int = 50,
    val energy: Double = 0.0
)

enum class VivoxConnectionState {
    DISCONNECTED, CONNECTING, CONNECTED, LOGGED_IN, IN_CHANNEL
}

@Singleton
class VivoxService @Inject constructor() : VivoxEventCallback {

    companion object {
        private const val TAG = "VivoxService"
    }

    private val native = VivoxNative()
    private var pollJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _connectionState = MutableStateFlow(VivoxConnectionState.DISCONNECTED)
    val connectionState = _connectionState.asStateFlow()

    private val _messages = MutableSharedFlow<VivoxTextMessage>(extraBufferCapacity = 100)
    val messages = _messages.asSharedFlow()

    private val _participants = MutableStateFlow<List<VivoxParticipant>>(emptyList())
    val participants = _participants.asStateFlow()

    private val _error = MutableSharedFlow<String>(extraBufferCapacity = 10)
    val error = _error.asSharedFlow()

    private var currentUsername: String = ""
    private var currentChannelName: String = ""

    fun initialize(context: Context): Boolean {
        return try {
            JniHelpers.init(context)
            val result = native.nativeInitialize()
            if (result) {
                native.nativeSetCallback(this)
                startPolling()
            }
            result
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Vivox", e)
            false
        }
    }

    fun connect(): Boolean {
        _connectionState.value = VivoxConnectionState.CONNECTING
        return native.nativeConnect(BuildConfig.VIVOX_SERVER)
    }

    fun login(displayName: String, username: String): Boolean {
        currentUsername = username
        val token = VivoxTokenGenerator.generateLoginToken(username)
        return native.nativeLogin(displayName, username, token)
    }

    fun joinChannel(channelName: String, connectAudio: Boolean = true, connectText: Boolean = true): Boolean {
        currentChannelName = channelName
        val channelUri = VivoxTokenGenerator.getChannelUri(channelName)
        val token = VivoxTokenGenerator.generateJoinToken(currentUsername, channelName)
        return native.nativeJoinChannel(channelUri, token, connectAudio, connectText)
    }

    fun leaveChannel(): Boolean {
        currentChannelName = ""
        return native.nativeLeaveChannel()
    }

    fun sendMessage(text: String): Boolean = native.nativeSendMessage(text)

    fun setMicMute(muted: Boolean) = native.nativeSetMicMute(muted)

    fun logout(): Boolean {
        currentUsername = ""
        return native.nativeLogout()
    }

    fun shutdown() {
        pollJob?.cancel()
        native.nativeShutdown()
        _connectionState.value = VivoxConnectionState.DISCONNECTED
        _participants.value = emptyList()
    }

    fun getUserUri(): String = VivoxTokenGenerator.getUserUri(currentUsername)
    fun getCurrentUsername(): String = currentUsername
    fun getCurrentChannelName(): String = currentChannelName
    fun isInitialized(): Boolean = native.nativeIsInitialized()

    private fun startPolling() {
        pollJob?.cancel()
        pollJob = scope.launch {
            while (isActive) {
                native.nativePollEvents()
                delay(50)
            }
        }
    }

    override fun onVivoxEvent(type: String, json: String) {
        try {
            val data = JSONObject(json)
            when (type) {
                "connector_created" -> {
                    _connectionState.value = VivoxConnectionState.CONNECTED
                }
                "logged_in" -> {
                    _connectionState.value = VivoxConnectionState.LOGGED_IN
                }
                "channel_joined" -> {
                    _connectionState.value = VivoxConnectionState.IN_CHANNEL
                }
                "logged_out" -> {
                    _connectionState.value = VivoxConnectionState.DISCONNECTED
                    _participants.value = emptyList()
                }
                "text_message" -> {
                    val msg = VivoxTextMessage(
                        body = data.optString("body", ""),
                        senderUri = data.optString("sender", ""),
                        senderDisplayName = data.optString("displayName", ""),
                        language = data.optString("language", "en")
                    )
                    _messages.tryEmit(msg)
                }
                "participant_added" -> {
                    val uri = data.optString("uri", "")
                    val name = data.optString("displayName", "")
                    val current = _participants.value.toMutableList()
                    if (current.none { it.uri == uri }) {
                        current.add(VivoxParticipant(uri = uri, displayName = name))
                        _participants.value = current
                    }
                }
                "participant_removed" -> {
                    val uri = data.optString("uri", "")
                    _participants.value = _participants.value.filter { it.uri != uri }
                }
                "participant_updated" -> {
                    val uri = data.optString("uri", "")
                    _participants.value = _participants.value.map { p ->
                        if (p.uri == uri) {
                            p.copy(
                                displayName = data.optString("displayName", p.displayName),
                                isSpeaking = data.optBoolean("isSpeaking", p.isSpeaking),
                                isMuted = data.optBoolean("isMuted", p.isMuted),
                                volume = data.optInt("volume", p.volume),
                                energy = data.optDouble("energy", p.energy)
                            )
                        } else p
                    }
                }
                "error", "login_error", "channel_error" -> {
                    val errorCode = data.optInt("error", -1)
                    _error.tryEmit("Vivox Fehler: $errorCode")
                    Log.e(TAG, "Vivox error: $type - $errorCode")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error processing Vivox event: $type", e)
        }
    }
}
