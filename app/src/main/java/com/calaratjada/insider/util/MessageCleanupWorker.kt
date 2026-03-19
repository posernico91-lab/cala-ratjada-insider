package com.calaratjada.insider.util

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.calaratjada.insider.data.local.ChatDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * DSGVO Art. 17 - Right to be forgotten / Data retention policy.
 * Automatically deletes chat messages older than 90 days.
 * Scheduled as a daily periodic WorkManager task.
 */
@HiltWorker
class MessageCleanupWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val chatDao: ChatDao
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val TAG = "MessageCleanupWorker"
        const val RETENTION_DAYS = 90L
        val RETENTION_MS = RETENTION_DAYS * 24 * 60 * 60 * 1000L
    }

    override suspend fun doWork(): Result {
        return try {
            val cutoffTime = System.currentTimeMillis() - RETENTION_MS
            chatDao.deleteOldMessages(cutoffTime)
            Log.d(TAG, "Cleaned up messages older than $RETENTION_DAYS days")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Message cleanup failed", e)
            Result.retry()
        }
    }
}
