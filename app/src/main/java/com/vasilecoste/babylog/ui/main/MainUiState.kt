package com.vasilecoste.babylog.ui.main

import com.vasilecoste.babylog.data.db.entity.BabyProfile
import com.vasilecoste.babylog.data.db.entity.DiaperSummary
import com.vasilecoste.babylog.data.db.entity.Entry
import com.vasilecoste.babylog.data.db.entity.TummyTimeEntry
import com.vasilecoste.babylog.data.db.entity.WeightRecord
import com.vasilecoste.babylog.data.repository.DailyAggregate
import com.vasilecoste.babylog.ui.theme.AppTheme
import java.time.LocalDate

data class QuickStats(
    val totalFoodMl: Int,
    val vitaminTaken: Boolean,
    val poopCount: Int,
    val peeCount: Int,
    val pukeCount: Int,
)

data class MainUiState(
    val babies: List<BabyProfile> = emptyList(),
    val selectedBaby: BabyProfile? = null,
    val selectedDate: LocalDate = LocalDate.now(),
    val entries: List<Entry> = emptyList(),
    val diaperSummary: DiaperSummary? = null,
    val pickerDates: List<LocalDate> = listOf(LocalDate.now()),
    val chartData: List<DailyAggregate> = emptyList(),
    val tummyTimeEntries: List<TummyTimeEntry> = emptyList(),
    val tummyTimerRunning: Boolean = false,
    val tummyTimerStartEpochMillis: Long? = null,
    val activeTheme: AppTheme = AppTheme.DEFAULT,
    val themeOverride: AppTheme? = null,
    val weightRecords: List<WeightRecord> = emptyList(),
) {
    val quickStats: QuickStats
        get() = QuickStats(
            totalFoodMl = entries.sumOf { it.foodMl ?: 0 },
            vitaminTaken = entries.any { it.vitamin },
            poopCount = entries.count { it.poop } + (diaperSummary?.poopCount ?: 0),
            peeCount = entries.count { it.pee } + (diaperSummary?.peeCount ?: 0),
            pukeCount = entries.count { it.puke },
        )

    val hasBabies: Boolean get() = babies.isNotEmpty()

    val totalTummyTimeSecondsToday: Int get() = tummyTimeEntries.sumOf { it.durationSeconds }
}
