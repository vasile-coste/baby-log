package com.vasilecoste.babylog

import android.app.Application
import com.vasilecoste.babylog.data.di.AppContainer
import com.vasilecoste.babylog.notification.FeedingReminderReceiver

class BabyLogApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        FeedingReminderReceiver.createNotificationChannel(this)
    }
}
