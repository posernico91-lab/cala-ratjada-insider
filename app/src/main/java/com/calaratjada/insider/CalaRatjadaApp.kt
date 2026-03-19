package com.calaratjada.insider

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.calaratjada.insider.data.service.AdManager
import com.calaratjada.insider.data.service.CoinManager
import com.calaratjada.insider.data.service.ConsentManager
import com.calaratjada.insider.util.MessageCleanupWorker
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class CalaRatjadaApp : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var adManager: AdManager
    @Inject lateinit var coinManager: CoinManager
    @Inject lateinit var consentManager: ConsentManager

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        coinManager.initialize()

        // Only initialize ads if user has given consent (DSGVO Art. 6)
        if (consentManager.hasAcceptedPrivacyPolicy()) {
            MobileAds.initialize(this)
            adManager.initialize()
        }

        // Schedule DSGVO Art. 17 compliant message retention cleanup (90 days)
        scheduleMessageCleanup()
    }

    private fun scheduleMessageCleanup() {
        val cleanupRequest = PeriodicWorkRequestBuilder<MessageCleanupWorker>(
            1, TimeUnit.DAYS
        ).setConstraints(
            Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build()
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "message_cleanup",
            ExistingPeriodicWorkPolicy.KEEP,
            cleanupRequest
        )
    }
}
