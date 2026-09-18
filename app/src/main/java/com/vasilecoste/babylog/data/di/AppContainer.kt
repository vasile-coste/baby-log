package com.vasilecoste.babylog.data.di

import android.content.Context
import androidx.room.Room
import com.vasilecoste.babylog.data.db.AppDatabase
import com.vasilecoste.babylog.data.prefs.SelectedBabyStore
import com.vasilecoste.babylog.data.repository.BabyLogRepository
import com.vasilecoste.babylog.notification.FeedingReminderScheduler

class AppContainer(context: Context) {
    private val database: AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "baby-log.db",
    ).fallbackToDestructiveMigration(dropAllTables = true).build()

    val repository: BabyLogRepository = BabyLogRepository(
        database = database,
        babyProfileDao = database.babyProfileDao(),
        entryDao = database.entryDao(),
        weightDao = database.weightDao(),
        diaperSummaryDao = database.diaperSummaryDao(),
        tummyTimeDao = database.tummyTimeDao(),
        babyThemePreferenceDao = database.babyThemePreferenceDao(),
        babyNotificationPreferenceDao = database.babyNotificationPreferenceDao(),
        sleepDao = database.sleepDao(),
    )

    val selectedBabyStore: SelectedBabyStore = SelectedBabyStore(context)

    val feedingReminderScheduler: FeedingReminderScheduler = FeedingReminderScheduler(context, repository)
}
