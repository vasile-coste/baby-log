package com.vasilecoste.babylog.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "baby_theme_preferences",
    foreignKeys = [
        ForeignKey(
            entity = BabyProfile::class,
            parentColumns = ["id"],
            childColumns = ["babyId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class BabyThemePreference(
    @PrimaryKey val babyId: Long,
    val overrideTheme: String? = null,
)
