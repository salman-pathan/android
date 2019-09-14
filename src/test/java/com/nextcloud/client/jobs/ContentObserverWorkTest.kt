package com.nextcloud.client.jobs

import android.content.Context
import android.net.Uri
import androidx.work.WorkerParameters
import com.nextcloud.client.device.PowerManagementService
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.owncloud.android.datamodel.SyncedFolderProvider
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class ContentObserverWorkTest {

    private lateinit var worker: ContentObserverWork

    @Mock
    lateinit var params: WorkerParameters

    @Mock
    lateinit var context: Context

    @Mock
    lateinit var folderProvider: SyncedFolderProvider

    @Mock
    lateinit var powerManagementService: PowerManagementService

    @Mock
    lateinit var backgroundJobManager: BackgroundJobManager

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        worker = ContentObserverWork(
            appContext = context,
            params = params,
            syncerFolderProvider = folderProvider,
            powerManagementService = powerManagementService,
            backgroundJobManager = backgroundJobManager
        )
        val uri: Uri = Mockito.mock(Uri::class.java)
        whenever(params.triggeredContentUris).thenReturn(listOf(uri))
    }

    @Test
    fun `job reschedules self after each run unconditionally`() {
        // GIVEN
        //      nothing to sync
        whenever(params.triggeredContentUris).thenReturn(emptyList())

        // WHEN
        //      worker is called
        worker.doWork()

        // THEN
        //      worker reschedules itself unconditionally
        verify(backgroundJobManager).scheduleContentObserverJob()
    }

    @Test
    @Ignore("TODO: needs further refactoring")
    fun `sync is triggered`() {
        // GIVEN
        //      power saving is disabled
        //      some folders are configured for syncing
        whenever(powerManagementService.isPowerSavingEnabled).thenReturn(false)
        whenever(folderProvider.countEnabledSyncedFolders()).thenReturn(1)

        // WHEN
        //      worker is called
        worker.doWork()

        // THEN
        //      sync job is scheduled
        // TODO: verify(backgroundJobManager).sheduleFilesSync() or something like this
    }

    @Test
    @Ignore("TODO: needs further refactoring")
    fun `sync is not triggered under power saving mode`() {
        // GIVEN
        //      power saving is enabled
        //      some folders are configured for syncing
        whenever(powerManagementService.isPowerSavingEnabled).thenReturn(true)
        whenever(folderProvider.countEnabledSyncedFolders()).thenReturn(1)

        // WHEN
        //      worker is called
        worker.doWork()

        // THEN
        //      sync job is scheduled
        // TODO: verify(backgroundJobManager, never()).sheduleFilesSync() or something like this)
    }

    @Test
    @Ignore("TODO: needs further refactoring")
    fun `sync is not triggered if no folder are synced`() {
        // GIVEN
        //      power saving is disabled
        //      no folders configured for syncing
        whenever(powerManagementService.isPowerSavingEnabled).thenReturn(false)
        whenever(folderProvider.countEnabledSyncedFolders()).thenReturn(0)

        // WHEN
        //      worker is called
        worker.doWork()

        // THEN
        //      sync job is scheduled
        // TODO: verify(backgroundJobManager, never()).sheduleFilesSync() or something like this)
    }
}
