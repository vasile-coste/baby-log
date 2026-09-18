package com.vasilecoste.babylog.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "baby_notification_preferences")
data class BabyNotificationPreference(
    @PrimaryKey val babyId: Long,
    val enabled: Boolean = true,
    val avgDays: Int = 2,
    val offsetMinutes: Int = 10,
    val nextReminderEpochMillis: Long? = null
)
