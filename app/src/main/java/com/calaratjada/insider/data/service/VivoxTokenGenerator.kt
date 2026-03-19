package com.calaratjada.insider.data.service

import com.calaratjada.insider.BuildConfig
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object VivoxTokenGenerator {

    private const val KEY = BuildConfig.VIVOX_KEY
    private const val ISSUER = BuildConfig.VIVOX_ISSUER
    private const val DOMAIN = BuildConfig.VIVOX_DOMAIN
    private const val TOKEN_EXPIRATION_SECONDS = 90

    fun generateLoginToken(username: String): String {
        val fromUri = "sip:.${ISSUER}.${username}.@${DOMAIN}"
        return generateToken("login", fromUri, null, null)
    }

    fun generateJoinToken(username: String, channelName: String, channelType: String = "g"): String {
        val fromUri = "sip:.${ISSUER}.${username}.@${DOMAIN}"
        val toUri = "sip:confctl-${channelType}-${ISSUER}.${channelName}@${DOMAIN}"
        return generateToken("join", fromUri, toUri, null)
    }

    fun generateJoinMuteToken(username: String, channelName: String, channelType: String = "g"): String {
        val fromUri = "sip:.${ISSUER}.${username}.@${DOMAIN}"
        val toUri = "sip:confctl-${channelType}-${ISSUER}.${channelName}@${DOMAIN}"
        return generateToken("join_muted", fromUri, toUri, null)
    }

    fun getChannelUri(channelName: String, channelType: String = "g"): String {
        return "sip:confctl-${channelType}-${ISSUER}.${channelName}@${DOMAIN}"
    }

    fun getUserUri(username: String): String {
        return "sip:.${ISSUER}.${username}.@${DOMAIN}"
    }

    private fun generateToken(
        action: String,
        fromUri: String,
        toUri: String?,
        subject: String?
    ): String {
        val exp = (System.currentTimeMillis() / 1000) + TOKEN_EXPIRATION_SECONDS
        val serial = (Math.random() * Int.MAX_VALUE).toInt()

        val payload = buildString {
            append("e${ISSUER}")
            append(".${exp}")
            append(".${action}")
            append(".${serial}")
            append(".${subject ?: ""}")
            append(".${fromUri}")
            append(".${toUri ?: ""}")
        }

        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(KEY.toByteArray(Charsets.UTF_8), "HmacSHA256"))
        val hash = mac.doFinal(payload.toByteArray(Charsets.UTF_8))
        val signature = Base64.getUrlEncoder().withoutPadding().encodeToString(hash)

        return "${payload}.${signature}"
    }
}
