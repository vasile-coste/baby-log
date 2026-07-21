package com.vasilecoste.babylog.ui.main

import com.vasilecoste.babylog.data.db.entity.BabyProfile
import com.vasilecoste.babylog.data.db.entity.Entry
import com.vasilecoste.babylog.data.repository.DailyAggregate
import java.time.LocalDate

data class QuickStats(
    val totalFoodMl: Int,
    val vitaminTaken: Boolean,
    val poopCount: Int,
    val peeCount: Int,
)

data class MainUiState(
    val babies: List<BabyProfile> = emptyList(),
    val selectedBaby: BabyProfile? = null,
    val selectedDate: LocalDate = LocalDate.now(),
    val entries: List<Entry> = emptyList(),
    val pickerDates: List<LocalDate> = listOf(LocalDate.now()),
    val chartData: List<DailyAggregate> = emptyList(),
) {
    val quickStats: QuickStats
        get() = QuickStats(
            totalFoodMl = entries.sumOf { it.foodMl ?: 0 },
            vitaminTaken = entries.any { it.vitamin },
            poopCount = entries.count { it.poop },
            peeCount = entries.count { it.pee },
        )

    val hasBabies: Boolean get() = babies.isNotEmpty()
}
