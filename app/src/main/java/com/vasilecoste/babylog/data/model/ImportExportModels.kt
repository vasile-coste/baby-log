package com.vasilecoste.babylog.data.model

import com.vasilecoste.babylog.data.db.entity.DiaperSummary
import com.vasilecoste.babylog.data.db.entity.Entry
import com.vasilecoste.babylog.data.db.entity.WeightRecord
import java.time.LocalDate
import java.time.LocalTime

data class ImportedEntry(
    val date: LocalDate,
    val time: LocalTime,
    val foodMl: Int?,
    val poop: Boolean,
    val pee: Boolean,
    val puke: Boolean,
    val vitamin: Boolean,
    val breastfed: Boolean,
)

data class ImportedWeight(val date: LocalDate, val weightKg: Double, val heightCm: Double?)

data class ImportedDiaperSummary(val date: LocalDate, val poopCount: Int, val peeCount: Int)

data class ImportedBabyData(
    val babyName: String,
    val entries: List<ImportedEntry>,
    val weights: List<ImportedWeight>,
    val diaperSummaries: List<ImportedDiaperSummary>,
)

data class ExportedBabyData(
    val babyName: String,
    val entries: List<Entry>,
    val weights: List<WeightRecord>,
    val diaperSummaries: List<DiaperSummary>,
)
