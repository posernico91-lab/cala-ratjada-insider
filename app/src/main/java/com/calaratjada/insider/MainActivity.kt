package com.calaratjada.insider

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.calaratjada.insider.data.service.AdManager
import com.calaratjada.insider.data.service.ConsentManager
import com.calaratjada.insider.ui.navigation.AppNavigation
import com.calaratjada.insider.ui.screens.ConsentDialog
import com.calaratjada.insider.ui.theme.CalaRatjadaInsiderTheme
import com.calaratjada.insider.ui.theme.Stone50
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var adManager: AdManager
    @Inject lateinit var consentManager: ConsentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Only show ads if consent was given
        if (consentManager.hasAcceptedPrivacyPolicy() && consentManager.hasAdsConsent()) {
            // Trigger Google UMP / TCF v2 consent form, then init ads
            consentManager.requestUmpConsent(this) { canShowAds ->
                if (canShowAds) {
                    MobileAds.initialize(this)
                    adManager.initialize()
                    adManager.initializeAppodeal(this)
                    adManager.showAppOpenAd(this)
                }
            }
        }

        setContent {
            CalaRatjadaInsiderTheme {
                val hasConsent by consentManager.hasConsent.collectAsState()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Stone50
                ) {
                    if (!hasConsent) {
                        // DSGVO consent dialog must be shown before any data processing
                        ConsentDialog(
                            onConsent = { privacyAccepted, adsConsent, locationConsent ->
                                consentManager.saveConsent(privacyAccepted, adsConsent, locationConsent)
                                // Show Google UMP/CMP (TCF v2) consent form, then initialize ads
                                if (adsConsent) {
                                    consentManager.requestUmpConsent(this@MainActivity) { canShowAds ->
                                        if (canShowAds) {
                                            MobileAds.initialize(this@MainActivity)
                                            adManager.initialize()
                                            adManager.initializeAppodeal(this@MainActivity)
                                        }
                                    }
                                }
                            },
                            onPrivacyPolicyClick = { /* handled inline, dialog stays open */ },
                            onImpressumClick = { /* handled inline, dialog stays open */ }
                        )
                    } else {
                        AppNavigation(adManager = adManager)
                    }
                }
            }
        }
    }
}
