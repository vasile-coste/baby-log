package com.vasilecoste.babylog.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "baby_profiles")
data class BabyProfile(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val birthDate: LocalDate? = null,
    val createdAtEpochMillis: Long,
)
