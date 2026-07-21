package com.vasilecoste.babylog.data.di

import android.content.Context
import androidx.room.Room
import com.vasilecoste.babylog.data.db.AppDatabase
import com.vasilecoste.babylog.data.prefs.SelectedBabyStore
import com.vasilecoste.babylog.data.repository.BabyLogRepository

class AppContainer(context: Context) {
    private val database: AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "baby-log.db",
    ).fallbackToDestructiveMigration(dropAllTables = true).build()

    val repository: BabyLogRepository = BabyLogRepository(
        babyProfileDao = database.babyProfileDao(),
        entryDao = database.entryDao(),
        weightDao = database.weightDao(),
        diaperSummaryDao = database.diaperSummaryDao(),
    )

    val selectedBabyStore: SelectedBabyStore = SelectedBabyStore(context)
}
