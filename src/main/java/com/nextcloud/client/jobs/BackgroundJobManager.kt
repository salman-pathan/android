package com.nextcloud.client.jobs

import android.os.Build
import androidx.annotation.RequiresApi

/**
 * This interface allows to control, schedule and monitor all application
 * long-running background tasks, such as periodic checks or synchronization.
 */
interface BackgroundJobManager {

    /**
     * Start content observer job that monitors changes in media folders
     * and launches synchronization when needed.
     */
    @RequiresApi(Build.VERSION_CODES.N)
    fun scheduleContentObserverJob()
}
