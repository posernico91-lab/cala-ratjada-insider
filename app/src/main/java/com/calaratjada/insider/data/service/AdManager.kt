package com.calaratjada.insider.data.service

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.appodeal.ads.Appodeal
import com.appodeal.ads.initializing.ApdInitializationCallback
import com.appodeal.ads.initializing.ApdInitializationError
import com.calaratjada.insider.BuildConfig
import com.google.android.gms.ads.*
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.unity3d.ads.IUnityAdsInitializationListener
import com.unity3d.ads.UnityAds
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

private val Context.adPrefsStore: DataStore<Preferences> by preferencesDataStore(name = "ad_prefs")

@Singleton
class AdManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private const val TAG = "AdManager"
        const val INTERSTITIAL_INTERVAL_MS = 180_000L // 3 Minuten
        const val REWARDED_COINS = 3 // Coins pro Rewarded-Video
        private val KEY_AD_FREE = booleanPreferencesKey("is_ad_free")
    }

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var interstitialAd: InterstitialAd? = null
    private var appOpenAd: AppOpenAd? = null
    private var rewardedAd: RewardedAd? = null
    private var lastInterstitialTime = 0L
    private var appodealInitialized = false

    // Ad-free status
    private val _isAdFree = MutableStateFlow(false)
    val isAdFree = _isAdFree.asStateFlow()

    fun initialize() {
        // Load ad-free status from DataStore
        scope.launch {
            val adFree = context.adPrefsStore.data.map { it[KEY_AD_FREE] ?: false }.first()
            _isAdFree.value = adFree
        }

        // Initialize Unity Ads for mediation
        val unityInitialized = UnityAds.isInitialized
        if (!unityInitialized) {
            UnityAds.initialize(
                context,
                BuildConfig.UNITY_GAME_ID,
                false,
                object : IUnityAdsInitializationListener {
                    override fun onInitializationComplete() {
                        if (BuildConfig.DEBUG) Log.d(TAG, "Unity Ads initialized")
                    }
                    override fun onInitializationFailed(
                        error: UnityAds.UnityAdsInitializationError?,
                        message: String?
                    ) {
                        if (BuildConfig.DEBUG) Log.e(TAG, "Unity Ads init failed: $message")
                    }
                }
            )
        }

        // Pre-load AdMob ads if not ad-free
        if (!_isAdFree.value) {
            loadInterstitial(context)
            loadAppOpenAd(context)
            loadRewardedAd(context)
        }
    }

    /**
     * Initialize Appodeal SDK - must be called from Activity context.
     * Call this from MainActivity.onCreate after consent is given.
     */
    fun initializeAppodeal(activity: Activity) {
        if (appodealInitialized || _isAdFree.value) return

        val adTypes = Appodeal.INTERSTITIAL or Appodeal.REWARDED_VIDEO or Appodeal.BANNER

        Appodeal.initialize(
            context = activity,
            appKey = BuildConfig.APPODEAL_APP_KEY,
            adTypes = adTypes,
            callback = object : ApdInitializationCallback {
                override fun onInitializationFinished(errors: List<ApdInitializationError>?) {
                    appodealInitialized = true
                    if (BuildConfig.DEBUG) {
                        if (errors.isNullOrEmpty()) {
                            Log.d(TAG, "Appodeal initialized successfully")
                        } else {
                            errors.forEach { Log.e(TAG, "Appodeal init error: ${it.message}") }
                        }
                    }
                }
            }
        )
    }

    /**
     * Call this after a successful ad-free purchase
     */
    fun setAdFree() {
        _isAdFree.value = true
        scope.launch {
            context.adPrefsStore.edit { it[KEY_AD_FREE] = true }
        }
        interstitialAd = null
        appOpenAd = null
        rewardedAd = null
    }

    // --- Interstitial (AdMob primary, Appodeal fallback) ---

    fun loadInterstitial(context: Context) {
        if (_isAdFree.value) return
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            BuildConfig.ADMOB_INTERSTITIAL_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    if (BuildConfig.DEBUG) Log.d(TAG, "Interstitial loaded")
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    if (BuildConfig.DEBUG) Log.e(TAG, "Interstitial failed: ${error.message}")
                }
            }
        )
    }

    fun showInterstitialIfReady(activity: Activity) {
        if (_isAdFree.value) return
        val now = System.currentTimeMillis()
        if (now - lastInterstitialTime < INTERSTITIAL_INTERVAL_MS) return

        // Try AdMob first
        interstitialAd?.let { ad ->
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    loadInterstitial(activity)
                }
                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    interstitialAd = null
                }
            }
            ad.show(activity)
            lastInterstitialTime = now
            return
        }

        // Fallback to Appodeal
        if (appodealInitialized && Appodeal.isLoaded(Appodeal.INTERSTITIAL)) {
            Appodeal.show(activity, Appodeal.INTERSTITIAL)
            lastInterstitialTime = now
            return
        }

        // Neither available, reload AdMob
        loadInterstitial(activity)
    }

    // --- App Open Ad ---

    fun loadAppOpenAd(context: Context) {
        if (_isAdFree.value) return
        val adRequest = AdRequest.Builder().build()
        AppOpenAd.load(
            context,
            BuildConfig.ADMOB_APP_OPEN_ID,
            adRequest,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    if (BuildConfig.DEBUG) Log.d(TAG, "App open ad loaded")
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    appOpenAd = null
                    if (BuildConfig.DEBUG) Log.e(TAG, "App open ad failed: ${error.message}")
                }
            }
        )
    }

    fun showAppOpenAd(activity: Activity) {
        if (_isAdFree.value) return
        appOpenAd?.let { ad ->
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    appOpenAd = null
                    loadAppOpenAd(activity)
                }
                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    appOpenAd = null
                }
            }
            ad.show(activity)
        } ?: loadAppOpenAd(activity)
    }

    // --- Rewarded Video (AdMob primary, Appodeal fallback) ---

    fun loadRewardedAd(context: Context) {
        if (_isAdFree.value) return
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            context,
            BuildConfig.ADMOB_REWARDED_ID,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    if (BuildConfig.DEBUG) Log.d(TAG, "Rewarded ad loaded")
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewardedAd = null
                    if (BuildConfig.DEBUG) Log.e(TAG, "Rewarded ad failed: ${error.message}")
                }
            }
        )
    }

    val isRewardedAdReady: Boolean
        get() {
            if (_isAdFree.value) return false
            if (rewardedAd != null) return true
            if (appodealInitialized && Appodeal.isLoaded(Appodeal.REWARDED_VIDEO)) return true
            return false
        }

    fun showRewardedAd(activity: Activity, onRewarded: (Int) -> Unit) {
        // Try AdMob first
        rewardedAd?.let { ad ->
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedAd = null
                    loadRewardedAd(activity)
                }
                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    rewardedAd = null
                }
            }
            ad.show(activity) { _ ->
                onRewarded(REWARDED_COINS)
            }
            return
        }

        // Fallback to Appodeal rewarded
        if (appodealInitialized && Appodeal.isLoaded(Appodeal.REWARDED_VIDEO)) {
            Appodeal.show(activity, Appodeal.REWARDED_VIDEO)
            onRewarded(REWARDED_COINS)
        }
    }

    // --- Native Ad ---

    fun loadNativeAd(context: Context, onAdLoaded: (NativeAd) -> Unit) {
        if (_isAdFree.value) return
        val adLoader = AdLoader.Builder(context, BuildConfig.ADMOB_NATIVE_ID)
            .forNativeAd { nativeAd ->
                onAdLoaded(nativeAd)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    if (BuildConfig.DEBUG) Log.e(TAG, "Native ad failed: ${error.message}")
                }
            })
            .withNativeAdOptions(NativeAdOptions.Builder().build())
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    // --- Banner Ad Helper ---

    fun shouldShowBannerAd(): Boolean = !_isAdFree.value
}
