package com.vasilecoste.babylog.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

@Entity(
    tableName = "entries",
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
data class Entry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val babyId: Long,
    val date: LocalDate,
    val time: LocalTime,
    val foodMl: Int? = null,
    val poop: Boolean = false,
    val pee: Boolean = false,
    val puke: Boolean = false,
    val vitamin: Boolean = false,
    val breastfed: Boolean = false,
)
