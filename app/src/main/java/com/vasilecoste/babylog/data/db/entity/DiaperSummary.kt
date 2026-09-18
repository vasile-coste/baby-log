package com.vasilecoste.babylog.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * Poop/pee counts for a day that aren't tied to a specific timed [Entry] —
 * e.g. imported historical data that only recorded a daily total.
 */
@Entity(
    tableName = "diaper_summaries",
    foreignKeys = [
        ForeignKey(
            entity = BabyProfile::class,
            parentColumns = ["id"],
            childColumns = ["babyId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("babyId"), Index("babyId", "date")],
)
data class DiaperSummary(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val babyId: Long,
    val date: LocalDate,
    val poopCount: Int,
    val peeCount: Int,
)
