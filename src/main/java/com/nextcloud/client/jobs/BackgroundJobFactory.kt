package com.nextcloud.client.jobs

import android.content.ContentResolver
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.nextcloud.client.device.DeviceInfo
import com.nextcloud.client.device.PowerManagementService
import com.nextcloud.client.preferences.AppPreferences
import com.owncloud.android.datamodel.SyncedFolderProvider
import javax.inject.Inject
import javax.inject.Provider

/**
 * This factory is responsible for creating all background jobs and for injecting
 * all jobs dependencies.
 */
class BackgroundJobFactory @Inject constructor(
    private val preferences: AppPreferences,
    private val contentResolver: ContentResolver,
    private val powerManagerService: PowerManagementService,
    private val backgroundJobManager: Provider<BackgroundJobManager>,
    private val deviceInfo: DeviceInfo
) : WorkerFactory() {

    override fun createWorker(
        context: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {

        val workerClass = try {
            Class.forName(workerClassName).kotlin
        } catch (ex: ClassNotFoundException) {
            null
        }

        return when (workerClass) {
            ContentObserverWork::class -> createContentObserverJob(context, workerParameters)
            else -> null // falls back to default factory
        }
    }

    private fun createContentObserverJob(context: Context, workerParameters: WorkerParameters): ListenableWorker? {
        val folderResolver = SyncedFolderProvider(contentResolver, preferences)
        @RequiresApi(Build.VERSION_CODES.N)
        if (deviceInfo.apiLevel >= Build.VERSION_CODES.N) {
            return ContentObserverWork(
                context,
                workerParameters,
                folderResolver,
                powerManagerService,
                backgroundJobManager.get()
            )
        } else {
            return null
        }
    }
}
