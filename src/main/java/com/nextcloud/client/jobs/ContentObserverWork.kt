package com.nextcloud.client.jobs

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.evernote.android.job.JobRequest
import com.evernote.android.job.util.support.PersistableBundleCompat
import com.nextcloud.client.device.PowerManagementService
import com.owncloud.android.datamodel.SyncedFolderProvider
import com.owncloud.android.jobs.FilesSyncJob
import com.owncloud.android.jobs.MediaFoldersDetectionJob

/**
 * This work is triggered when OS detects change in media folders.
 *
 * It fires media detection job and sync job and finishes immediately.
 *
 * This job must not be started on API < 24.
 */
@RequiresApi(Build.VERSION_CODES.N)
class ContentObserverWork(
    appContext: Context,
    private val params: WorkerParameters,
    private val syncerFolderProvider: SyncedFolderProvider,
    private val powerManagementService: PowerManagementService,
    private val backgroundJobManager: BackgroundJobManager
) : Worker(appContext, params) {

    override fun doWork(): Result {
        if (params.triggeredContentUris.size > 0) {
            checkAndStartFileSyncJob()
            startMediaFolderDetectionJob()
        }
        recheduleSelf()
        return Result.success()
    }

    private fun recheduleSelf() {
        backgroundJobManager.scheduleContentObserverJob()
    }

    private fun checkAndStartFileSyncJob() {
        val syncFolders = syncerFolderProvider.countEnabledSyncedFolders() > 0
        if (!powerManagementService.isPowerSavingEnabled && syncFolders) {
            val persistableBundleCompat = PersistableBundleCompat()
            persistableBundleCompat.putBoolean(FilesSyncJob.SKIP_CUSTOM, true)

            JobRequest.Builder(FilesSyncJob.TAG)
                .startNow()
                .setExtras(persistableBundleCompat)
                .setUpdateCurrent(false)
                .build()
                .schedule()
        }
    }

    private fun startMediaFolderDetectionJob() {
        JobRequest.Builder(MediaFoldersDetectionJob.TAG)
            .startNow()
            .setUpdateCurrent(false)
            .build()
            .schedule()
    }
}
