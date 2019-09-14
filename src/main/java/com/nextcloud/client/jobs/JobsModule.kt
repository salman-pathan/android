package com.nextcloud.client.jobs

import android.content.Context
import android.content.ContextWrapper
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class JobsModule {

    @Provides
    @Singleton
    fun backgroundJobManager(context: Context, factory: BackgroundJobFactory): BackgroundJobManager {
        val configuration = Configuration.Builder()
            .setWorkerFactory(factory)
            .build()

        val contextWrapper = object : ContextWrapper(context) {
            override fun getApplicationContext(): Context {
                return this
            }
        }

        WorkManager.initialize(contextWrapper, configuration)
        val wm = WorkManager.getInstance(context)
        return BackgroundJobManagerImpl(wm)
    }
}
