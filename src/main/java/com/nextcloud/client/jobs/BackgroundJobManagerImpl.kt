package com.nextcloud.client.jobs

import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

internal class BackgroundJobManagerImpl(private val workManager: WorkManager) : BackgroundJobManager {

    companion object {
        const val TAG_CONTENT_SYNC = "content_sync"
        const val MAX_CONTENT_TRIGGER_DELAY_MS = 1500L
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun scheduleContentObserverJob() {
        val constrains = Constraints.Builder()
            .setRequiresCharging(true)
            .addContentUriTrigger(MediaStore.Images.Media.INTERNAL_CONTENT_URI, true)
            .addContentUriTrigger(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true)
            .addContentUriTrigger(MediaStore.Video.Media.INTERNAL_CONTENT_URI, true)
            .addContentUriTrigger(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, true)
            .setTriggerContentMaxDelay(MAX_CONTENT_TRIGGER_DELAY_MS, TimeUnit.MILLISECONDS)
            .build()

        val request = OneTimeWorkRequest.Builder(ContentObserverWork::class.java)
            .setConstraints(constrains)
            .addTag(TAG_CONTENT_SYNC)
            .build()

        workManager.enqueue(request)
    }
}
