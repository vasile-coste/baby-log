package com.vasilecoste.babylog.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.vasilecoste.babylog.data.db.entity.BabyNotificationPreference
import kotlinx.coroutines.flow.Flow

@Dao
interface BabyNotificationPreferenceDao {
    @Upsert
    suspend fun upsert(preference: BabyNotificationPreference)

    @Query("SELECT * FROM baby_notification_preferences WHERE babyId = :babyId LIMIT 1")
    fun getByBabyId(babyId: Long): Flow<BabyNotificationPreference?>

    @Query("SELECT * FROM baby_notification_preferences WHERE babyId = :babyId LIMIT 1")
    suspend fun getOnceByBabyId(babyId: Long): BabyNotificationPreference?
}
