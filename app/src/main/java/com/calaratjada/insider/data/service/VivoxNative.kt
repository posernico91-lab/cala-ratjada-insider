package com.calaratjada.insider.data.service

interface VivoxEventCallback {
    fun onVivoxEvent(type: String, json: String)
}

class VivoxNative {

    companion object {
        init {
            System.loadLibrary("vivox_bridge")
        }
    }

    external fun nativeInitialize(): Boolean
    external fun nativeSetCallback(callback: VivoxEventCallback)
    external fun nativeConnect(server: String): Boolean
    external fun nativeLogin(displayName: String, acctName: String, accessToken: String): Boolean
    external fun nativeJoinChannel(channelUri: String, accessToken: String, connectAudio: Boolean, connectText: Boolean): Boolean
    external fun nativeLeaveChannel(): Boolean
    external fun nativeSendMessage(message: String): Boolean
    external fun nativeSetMicMute(muted: Boolean)
    external fun nativePollEvents()
    external fun nativeLogout(): Boolean
    external fun nativeShutdown()
    external fun nativeGetAccountHandle(): String
    external fun nativeGetSessionHandle(): String
    external fun nativeIsInitialized(): Boolean
}
