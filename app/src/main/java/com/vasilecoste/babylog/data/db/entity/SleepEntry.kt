package com.vasilecoste.babylog.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

@Entity(
    tableName = "sleep_entries",
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
data class SleepEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val babyId: Long,
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime?,
) {
    // endTime before startTime means sleep crossed midnight into the next day.
    val durationMinutes: Int
        get() = if (endTime == null) 0 else Duration.between(startTime, endTime).toMinutes().let { if (it < 0) it + 24 * 60 else it }.toInt()
}
