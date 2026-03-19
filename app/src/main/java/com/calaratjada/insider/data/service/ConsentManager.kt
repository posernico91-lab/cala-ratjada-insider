package com.calaratjada.insider.data.service

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.android.ump.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * DSGVO/GDPR Consent Manager
 * Uses Google User Messaging Platform (UMP) SDK for ad tracking consent.
 * Additionally manages app-specific consents stored in EncryptedSharedPreferences.
 */
@Singleton
class ConsentManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "ConsentManager"
        private const val PREFS_NAME = "gdpr_consent"
        private const val KEY_PRIVACY_ACCEPTED = "privacy_policy_accepted"
        private const val KEY_ANALYTICS_CONSENT = "analytics_consent"
        private const val KEY_ADS_CONSENT = "ads_personalization_consent"
        private const val KEY_LOCATION_CONSENT = "location_consent"
        private const val KEY_CONSENT_TIMESTAMP = "consent_timestamp"
    }

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val securePrefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val _hasConsent = MutableStateFlow(hasAcceptedPrivacyPolicy())
    val hasConsent = _hasConsent.asStateFlow()

    private val _adsConsentGranted = MutableStateFlow(securePrefs.getBoolean(KEY_ADS_CONSENT, false))
    val adsConsentGranted = _adsConsentGranted.asStateFlow()

    fun hasAcceptedPrivacyPolicy(): Boolean = securePrefs.getBoolean(KEY_PRIVACY_ACCEPTED, false)

    fun hasAdsConsent(): Boolean = securePrefs.getBoolean(KEY_ADS_CONSENT, false)

    fun hasLocationConsent(): Boolean = securePrefs.getBoolean(KEY_LOCATION_CONSENT, false)

    /**
     * Save user consent choices (called from consent dialog)
     */
    fun saveConsent(
        privacyAccepted: Boolean,
        adsPersonalization: Boolean,
        locationSharing: Boolean
    ) {
        securePrefs.edit()
            .putBoolean(KEY_PRIVACY_ACCEPTED, privacyAccepted)
            .putBoolean(KEY_ADS_CONSENT, adsPersonalization)
            .putBoolean(KEY_LOCATION_CONSENT, locationSharing)
            .putBoolean(KEY_ANALYTICS_CONSENT, privacyAccepted)
            .putLong(KEY_CONSENT_TIMESTAMP, System.currentTimeMillis())
            .apply()
        _hasConsent.value = privacyAccepted
        _adsConsentGranted.value = adsPersonalization
    }

    /**
     * Revoke all consents (DSGVO Art. 7 Abs. 3 - right to withdraw consent)
     */
    fun revokeAllConsent() {
        securePrefs.edit()
            .putBoolean(KEY_PRIVACY_ACCEPTED, false)
            .putBoolean(KEY_ADS_CONSENT, false)
            .putBoolean(KEY_LOCATION_CONSENT, false)
            .putBoolean(KEY_ANALYTICS_CONSENT, false)
            .apply()
        _hasConsent.value = false
        _adsConsentGranted.value = false
    }

    /**
     * Request Google UMP consent form for ad tracking (EU region).
     * Should be called from Activity context.
     */
    fun requestUmpConsent(activity: Activity, onConsentResult: (Boolean) -> Unit) {
        val params = ConsentRequestParameters.Builder()
            .setTagForUnderAgeOfConsent(false)
            .build()

        val consentInformation = UserMessagingPlatform.getConsentInformation(activity)
        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                if (consentInformation.isConsentFormAvailable) {
                    UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { error ->
                        if (error != null) {
                            Log.e(TAG, "UMP consent error: ${error.message}")
                        }
                        val canShowAds = consentInformation.canRequestAds()
                        onConsentResult(canShowAds)
                    }
                } else {
                    onConsentResult(consentInformation.canRequestAds())
                }
            },
            { error ->
                Log.e(TAG, "UMP info update error: ${error.message}")
                onConsentResult(false)
            }
        )
    }
}
